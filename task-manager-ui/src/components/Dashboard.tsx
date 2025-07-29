import { Alert, Box, Button, CircularProgress, Container, Paper, Typography } from "@mui/material";
import { useTasks } from "../hooks/useTaskHooks";
import { useState } from "react";
import { TasksTable } from "./TaskTable";
import {
        Add as AddIcon,
} from '@mui/icons-material';
import { TaskFormDialog } from "./TaskFormDialog";

export const TaskManagementDashboard: React.FC = () => {
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  
  const { data: tasks, isLoading, isError, error } = useTasks();

  if (isLoading) {
    return (
      <Container maxWidth="md" sx={{ py: 4, textAlign: 'center'}}>
        <CircularProgress size={48} variant="indeterminate"/>
        <Typography variant="h6" sx={{ mt: 2 }}>
          Loading tasks...
        </Typography>
      </Container>
    );
  }

  if (isError) {
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Alert severity="error">
          <Typography variant="h6">Error loading tasks</Typography>
          <Typography variant="body2">
            {error instanceof Error ? error.message : 'An unknown error occurred'}
          </Typography>
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      {/* Header Section */}
      <Box 
        sx={{ 
          mb: 4, 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          flexWrap: { xs: 'wrap', sm: 'nowrap' },
          gap: 2
        }}
      >
        <Typography 
          variant="h4" 
          component="h1" 
          sx={{ 
            fontWeight: 600,
            color: 'text.primary',
            fontSize: { xs: '1.75rem', sm: '2.125rem' }
          }}
        >
          Task Management Dashboard
        </Typography>
        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={() => setCreateDialogOpen(true)}
          sx={{
            minWidth: 'fit-content',
            maxWidth: 'fit-content',
            whiteSpace: 'nowrap',
            px: 3,
            py: 1,
            fontWeight: 500,
            textTransform: 'none',
            borderRadius: 2,
            boxShadow: 1,
          }}
        >
          Create Task
        </Button>
      </Box>

      {tasks && tasks.length > 0 ? (
        <Box sx={{ mt: 3 }}>
          <TasksTable tasks={tasks} />
        </Box>
      ) : (
        <Paper 
          elevation={1}
          sx={{ 
            p: 6, 
            textAlign: 'center',
            borderRadius: 3,
            border: '1px solid',
            borderColor: 'divider',
            backgroundColor: 'background.paper'
          }}
        >
          <Box sx={{ mb: 3 }}>
            <Typography 
              variant="h5" 
              sx={{ 
                color: 'text.primary',
                fontWeight: 500,
                mb: 1
              }}
            >
              No tasks found
            </Typography>
            <Typography 
              variant="body1" 
              sx={{ 
                color: 'text.secondary',
                maxWidth: 400,
                mx: 'auto',
                lineHeight: 1.6
              }}
            >
              Get started by creating your first task to organize and track your work
            </Typography>
          </Box>
          <Button
            variant="contained"
            size="large"
            startIcon={<AddIcon />}
            onClick={() => setCreateDialogOpen(true)}
            sx={{
              minWidth: 'fit-content',
              maxWidth: 'fit-content',
              whiteSpace: 'nowrap',
              px: 4,
              py: 1.5,
              fontWeight: 500,
              textTransform: 'none',
              borderRadius: 2,
              fontSize: '1rem',
              boxShadow: 1,
              '&:hover': {
                boxShadow: 2,
              }
            }}
          >
            Create Your First Task
          </Button>
        </Paper>
      )}

      <TaskFormDialog
        open={createDialogOpen}
        onClose={() => setCreateDialogOpen(false)}
        mode="create"
      />
    </Container>
  );
};

export default TaskManagementDashboard;