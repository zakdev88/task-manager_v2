import { useEffect, useState } from "react"
import { Status, type Task } from "../types/task"
import { useCreateTask, useUpdateTask } from "../hooks/useTaskHooks";
import { 
  Box, 
  Button, 
  CircularProgress, 
  Dialog, 
  DialogActions, 
  DialogContent, 
  DialogTitle, 
  FormControl, 
  InputLabel, 
  MenuItem, 
  Select, 
  TextField,
  Typography,
  Snackbar
} from "@mui/material";

export const TaskFormDialog: React.FC<{
    open: boolean,
    onClose: () => void,
    task?: Task,
    mode: 'create' | 'update'
}> = ({ open, onClose, task, mode }) => {
    const [title, setTitle] = useState(task?.title || '');
    const [description, setDescription] = useState(task?.description || '');
    const [taskStatus, setTaskStatus] = useState(task?.status || Status.TODO);
    const [errors, setErrors] = useState<{ title?: string }>({});
    const [showError, setShowError] = useState(false);
    const [errorMessage, setErrorMessage] = useState<string>('');

    const createTaskMutation = useCreateTask();
    const updateTaskMutation = useUpdateTask();

    const handleClose = (): void => {
        setTitle('');
        setDescription('');
        setTaskStatus(Status.TODO);
        setErrors({});
        onClose();
    }

    useEffect(() => {
        if (task && mode === 'update') {
            setTitle(task.title || '');
            setDescription(task.description || '');
            setTaskStatus(task.status || Status.TODO);
        } else if (mode === 'create') {
            setTitle('');
            setDescription('');
            setTaskStatus(Status.TODO);
        }
        setErrors({});
    }, [task, mode])
    
    const validateForm = () => {
        const newErrors: { title?: string } = {};

        if (!title.trim()) {
            newErrors.title = 'Title is required';
        }

        setErrors(newErrors);

        return Object.keys(newErrors).length === 0;
    }

    const handleSubmit = async () => {
        if (!validateForm()) return;

        try {
            if (mode == 'create') {
                await createTaskMutation.mutateAsync({
                    title: title.trim(),
                    description: description.trim() || undefined
                })
            } else if (task && mode == 'update') {
                await updateTaskMutation.mutateAsync({
                    id: task.id,
                    task: {
                        ...task,
                        title: title.trim(),
                        description: description.trim() || undefined,
                        taskStatus: taskStatus
                    }
                })
            }
            handleClose();
        } catch (error) {
            console.log('Error saving task', error);
            const message = error instanceof Error ? error.message : 'Failed to update task status';
            setErrorMessage(message);
            setShowError(true);
        }
    }

    const isLoading = createTaskMutation.isPending || updateTaskMutation.isPending;

    return (
        <>
        <Dialog 
          open={open} 
          onClose={handleClose} 
          maxWidth='sm' 
          fullWidth
        >
            <DialogTitle 
              sx={{ 
                display: 'flex', 
                justifyContent: 'space-between', 
                alignItems: 'center',
                pb: 2
              }}
            >
                <Typography variant="h6" sx={{ fontWeight: 600 }}>
                    {mode === 'create' ? 'Create New Task' : 'Edit Task'}
                </Typography>
            </DialogTitle>

            <DialogContent sx={{ pb: 3 }}>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 3, pt: 1 }}>
                    <TextField
                        label='Title'
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        error={!!errors.title}
                        helperText={errors.title}
                        fullWidth
                        required
                        disabled={isLoading}
                        variant="outlined"
                        sx={{
                          '& .MuiOutlinedInput-root': {
                            borderRadius: 2
                          }
                        }}
                    />
                    
                    <TextField
                        label='Description'
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        multiline
                        rows={3}
                        fullWidth
                        disabled={isLoading}
                        variant="outlined"
                        placeholder="Enter task description (optional)"
                        sx={{
                          '& .MuiOutlinedInput-root': {
                            borderRadius: 2
                          }
                        }}
                    />
                    
                    {mode === "update" && (
                        <FormControl 
                          fullWidth 
                          disabled={isLoading}
                          variant="outlined"
                        >
                            <InputLabel>Status</InputLabel>
                            <Select
                                value={taskStatus}
                                onChange={(e) => setTaskStatus(e.target.value as Status)}
                                label='Status'
                                sx={{
                                  borderRadius: 2
                                }}
                                MenuProps={{
                                    PaperProps : {
                                        sx: {
                                            maxWidth: 200
                                        }
                                    }
                                }}
                            >
                                <MenuItem value={Status.TODO}>To Do</MenuItem>
                                <MenuItem value={Status.IN_PROGRESS}>In Progress</MenuItem>
                                <MenuItem value={Status.DONE}>Done</MenuItem>
                            </Select>
                        </FormControl>
                    )}
                </Box>
            </DialogContent>
            
            <DialogActions sx={{ px: 3, pb: 3, gap: 1.5 }}>
                <Button
                    onClick={handleClose}
                    variant="outlined"
                    disabled={isLoading}
                    sx={{
                      textTransform: 'none',
                      fontWeight: 500,
                      borderRadius: 2,
                      px: 3
                    }}
                >
                    Cancel
                </Button>
                <Button
                    onClick={handleSubmit}
                    variant='contained'
                    disabled={isLoading}
                    startIcon={isLoading ? <CircularProgress size={16} color="inherit" /> : null}
                    sx={{
                      textTransform: 'none',
                      fontWeight: 500,
                      borderRadius: 2,
                      px: 3,
                      minWidth: 'fit-content'
                    }}
                >
                    {isLoading 
                      ? (mode === 'create' ? 'Creating...' : 'Updating...') 
                      : (mode === 'create' ? 'Create Task' : 'Update Task')
                    }
                </Button>
            </DialogActions>
        </Dialog>
          <Snackbar
                 open={showError}
                 autoHideDuration={4000}
                 onClose={() => setShowError(false)}
                 anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                 message={
                   <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                     <Typography variant="body2" sx={{ fontWeight: 600, color: 'white' }}>
                       Update Failed
                     </Typography>
                     <Typography variant="caption" sx={{ color: 'rgba(255, 255, 255, 0.8)' }}>
                       {errorMessage}
                     </Typography>
                   </Box>
                 }
                 ContentProps={{
                   sx: {
                     backgroundColor: 'error.main',
                     color: 'white',
                     borderRadius: 1,
                     minWidth: 'auto',
                     maxWidth: '350px'
                   }
                 }}
               />
        </>
    )
}