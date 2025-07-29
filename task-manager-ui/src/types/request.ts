import type { Status } from "./task";

export interface CreateTaskRequest {
     title: string,
    description?: string,
}


export interface UpdateTaskRequest {
     title: string,
     description?: string,
     taskStatus?: Status
}