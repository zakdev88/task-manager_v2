import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { QUERY_KEYS } from "../constants/queryKeys"
import { TaskService } from "../api/TaskService"
import type { CreateTaskRequest, UpdateTaskRequest } from "../types/request"
import { Status, type Task } from "../types/task"

const sortTasks = (tasks: Task[]) => {
  const statusOrder = {
    [Status.TODO]: 1,
    [Status.IN_PROGRESS]: 2,
    [Status.DONE]: 3
  };
  return tasks.sort((a, b) => {
    const statusDiff = statusOrder[a.status] - statusOrder[b.status];
    if (statusDiff !== 0) return statusDiff;
        return a.title.localeCompare(b.title);
  });
};

export const useTasks = () => {
    return useQuery({
        queryKey: QUERY_KEYS.Tasks,
        queryFn : () => TaskService.getInstance().getAllTasks(),
        select: (data) => sortTasks(data),
        staleTime: 5*60*1000
    })
}

export const useSearchTaskById = (id: string, enabled: boolean = true) => {
    return useQuery({
        queryKey: [QUERY_KEYS.Tasks, 'search', id],
        queryFn: () => TaskService.getInstance().getTaskById(id),
        enabled: enabled && !!id.trim(),
        staleTime: 5 * 60 * 1000
    })
}


export const useCreateTask = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (task: CreateTaskRequest) => TaskService.getInstance().createTask(task),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: QUERY_KEYS.Tasks})
        }
    })
}

export const useUpdateTask = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({id, task}: {id: string, task: UpdateTaskRequest}) => TaskService.getInstance().updateTask(id, task),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: QUERY_KEYS.Tasks})
        }
    })
}

export const useUpdateTaskStatus = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn : ({id, taskStatus} : {id: string, taskStatus: Status }) => TaskService.getInstance().updateTaskStatus(id, taskStatus),
        onSuccess: () => queryClient.invalidateQueries({queryKey: QUERY_KEYS.Tasks})
    })
}

export const useDeleteTask = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: (id: string) => TaskService.getInstance().deleteTask(id),
        onSuccess: () => queryClient.invalidateQueries({queryKey: QUERY_KEYS.Tasks})
    })
}