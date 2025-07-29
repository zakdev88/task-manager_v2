export interface Task {
    id: string;
    title: string;
    description? : string;
    status: Status;
}

export enum Status {
    TODO = 'TODO',
    IN_PROGRESS = 'IN_PROGRESS',
    DONE = 'DONE'
}