import { useState } from "react";
import { useDeleteTask } from "../hooks/useTaskHooks";
import { Alert, Box, Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, Snackbar, TextField, Typography } from "@mui/material";
import type { Task } from "../types/task";

export const DeleteTaskDialog: React.FC<{
  open: boolean;
  onClose: () => void;
  task?: Task;
}> = ({ open, onClose, task }) => {
  const [confirmId, setConfirmId] = useState('');
  const [error, setError] = useState('');

  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string>('');

  const deleteTaskMutation = useDeleteTask();

  const handleClose = () => {
    setConfirmId('');
    setError('');
    onClose();
  };

  const handleDelete = async () => {
    if (!task) return;
    
    if (confirmId !== task.id) {
      setError('UUID does not match. Please enter the correct task UUID.');
      return;
    }

    try {
      await deleteTaskMutation.mutateAsync(task.id);
      handleClose();
    } catch (error) {
      console.error('Error deleting task:', error);
      const message = error instanceof Error ? error.message : 'Failed to update task status';
      setErrorMessage(message);
      setShowError(true);
    }
  };

  return (
    <>
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>Delete Task</DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 1 }}>
          <Alert severity="warning" sx={{ mb: 2 }}>
            This action cannot be undone. Please enter the task UUID to confirm deletion.
          </Alert>
          
          {task && (
            <Box sx={{ mb: 2, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
              <Typography variant="subtitle2" gutterBottom>
                Task to delete:
              </Typography>
              <Typography variant="body2" color="text.secondary">
                <strong>ID:</strong> {task.id}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                <strong>Title:</strong> {task.title}
              </Typography>
            </Box>
          )}
          
          <TextField
            label="Enter Task UUID"
            value={confirmId}
            onChange={(e) => {
              setConfirmId(e.target.value);
              setError('');
            }}
            error={!!error}
            helperText={error}
            fullWidth
            disabled={deleteTaskMutation.isPending}
            placeholder={task?.id}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} disabled={deleteTaskMutation.isPending}>
          Cancel
        </Button>
        <Button
          onClick={handleDelete}
          variant="contained"
          color="error"
          disabled={deleteTaskMutation.isPending || !confirmId}
          startIcon={deleteTaskMutation.isPending && <CircularProgress size={16} />}
        >
          Delete
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