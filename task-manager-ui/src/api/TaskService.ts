import axios from "axios";
import type { Task, Status } from "../types/task";
import type { CreateTaskRequest, UpdateTaskRequest } from "../types/request";
export class TaskService {
    private static instance: TaskService | null = null;

    private baseUrl:string = 'http://localhost:8080/api/tasks';

    public static getInstance(): TaskService {
        if(!TaskService.instance){
            TaskService.instance = new TaskService();
        }
        return TaskService.instance;
    }

    async getAllTasks(): Promise<Task[]>{
        const response = await axios.get<Task[]>(this.baseUrl)
        return response.data;
    }

    async getTaskById(id: string): Promise<Task> {
        const response = await axios.get<Task>(`${this.baseUrl}/${id}`);
        return response.data;
    }

    async createTask(task: CreateTaskRequest): Promise<Task> {
        const response = await axios.post<Task>(this.baseUrl, task);
        return response.data;
    }

    async updateTask(id: string, task: UpdateTaskRequest): Promise<Task> {
        const response = await axios.put<Task>(`${this.baseUrl}/${id}`, task);
        return response.data;
    }

    async updateTaskStatus(id: string, status: Status): Promise<Task> {
        const response = await axios.patch<Task>(`${this.baseUrl}/${id}/status`, { status });
        return response.data;
    }

    async deleteTask(id: string): Promise<void> {
        await axios.delete(`${this.baseUrl}/${id}`);
    }
    
}