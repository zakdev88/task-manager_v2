export const QUERY_KEYS = {
    Tasks : ['tasks'] as const,
    Task: (id: string) => ['tasks', id] as const
};