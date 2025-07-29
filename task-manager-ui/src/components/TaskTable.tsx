import { useState } from "react";
import { useUpdateTaskStatus } from "../hooks/useTaskHooks";
import { Status, type Task } from "../types/task";
import { Box, Button, Paper, Snackbar, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import { TaskStatusChip } from "./StatusChip";
import {
    Edit as EditIcon,
    Delete as DeleteIcon,
} from "@mui/icons-material";
import { TaskFormDialog } from "./TaskFormDialog";
import { DeleteTaskDialog } from "./DeleteDialog";

export const TasksTable: React.FC<{ tasks: Task[] }> = ({ tasks }) => {
  const [editingTask, setEditingTask] = useState<Task | undefined>();
  const [deletingTask, setDeletingTask] = useState<Task | undefined>();
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [showError, setShowError] = useState(false);
  const [updatingTaskId, setUpdatingTaskId] = useState<string | null>(null);

  const updateTaskStatusMutation = useUpdateTaskStatus();

  const handleStatusChange = async (task: Task, newStatus: Status) => {
    if (task.status === newStatus) return;
    setUpdatingTaskId(task.id);
    try {
      await updateTaskStatusMutation.mutateAsync({
        id: task.id,
        taskStatus: newStatus,
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Failed to update task status';
      setErrorMessage(message);
      setShowError(true);
    }finally{
      setUpdatingTaskId(null);
    }
  };

  const getNextStatus = (currentStatus: Status): Status => {
    switch (currentStatus) {
      case Status.TODO:
        return Status.IN_PROGRESS;
      case Status.IN_PROGRESS:
        return Status.DONE;
      case Status.DONE:
        return Status.TODO;
    }
  };

  return (
    <>
      <TableContainer component={Paper} elevation={2}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell sx={{ width: '25%', maxWidth: '200px' }}>Title</TableCell>
              <TableCell sx={{ width: '35%' }}>Description</TableCell>
              <TableCell sx={{ width: '15%' }}>Status</TableCell>
              <TableCell sx={{ width: '15%', minWidth: '120px' }}>UUID</TableCell>
              <TableCell align="right" sx={{ width: '10%', minWidth: '100px' }}>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tasks.map((task) => (
              <TableRow key={task.id} hover>
                <TableCell sx={{ width: '25%', maxWidth: '200px' }}>
                  <Typography 
                    variant="body2" 
                    fontWeight="medium"
                    sx={{ 
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }}
                  >
                    {task.title}
                  </Typography>
                </TableCell>
                <TableCell sx={{ width: '35%' }}>
                  <Typography 
                    variant="body2" 
                    color="text.secondary"
                    sx={{ 
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap'
                    }}
                  >
                    {task.description || '-'}
                  </Typography>
                </TableCell>
                <TableCell sx={{ width: '15%' }}>    
                    <TaskStatusChip
                      taskStatus={task.status}
                      handleStatusChange={() => handleStatusChange(task, getNextStatus(task.status))}
                      isPending={updatingTaskId === task.id}
                      />                            
                </TableCell>
                <TableCell sx={{ width: '15%', minWidth: '120px' }}>
                  <Typography 
                    variant="caption" 
                    color="text.secondary" 
                    sx={{ 
                      fontFamily: 'monospace',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                      display: 'block'
                    }}
                  >
                    {task.id}
                  </Typography>
                </TableCell>
                <TableCell align="right" sx={{ width: '10%', minWidth: '100px' }}>
                  <Box sx={{ 
                    display: 'flex',
                    justifyContent: 'flex-end', 
                    gap: 0.5,
                    minWidth: '80px',
                  }}>
                    <Button variant="contained" size="small" startIcon={<EditIcon />} onClick={() => setEditingTask(task)}>
                      Edit
                    </Button>
                    <Button color="error" variant="contained" size="small" startIcon={<DeleteIcon />} onClick={() => setDeletingTask(task)}>
                      Delete
                    </Button>
                  </Box>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <TaskFormDialog
        open={!!editingTask}
        onClose={() => setEditingTask(undefined)}
        task={editingTask}
        mode="update"
      />

      <DeleteTaskDialog
        open={!!deletingTask}
        onClose={() => setDeletingTask(undefined)}
        task={deletingTask}
      />

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
  );
};