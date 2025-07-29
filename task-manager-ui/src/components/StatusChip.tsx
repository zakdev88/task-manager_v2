import { Chip, Skeleton } from "@mui/material";
import { Status } from "../types/task";
import {
  RadioButtonUnchecked as TodoIcon,
  PlayArrow as InProgressIcon,
  CheckCircle as DoneIcon
} from '@mui/icons-material';

type TaskStatusChipProps = {
  taskStatus: Status;
  handleStatusChange: () => void;
  isPending: boolean
};

export const TaskStatusChip: React.FC<TaskStatusChipProps> = ({ taskStatus, handleStatusChange, isPending }) => {
  
  const getTaskStatusConfig = (status: Status) => {
    switch (status) {
      case Status.TODO:
        return {
          color: 'default' as const,
          icon: <TodoIcon fontSize="small" />,
          label: 'To Do',
          tooltip: 'Mark as In Progress',
          sx: {
            backgroundColor: 'grey.100',
            color: 'text.secondary',
            borderColor: 'grey.300',
            '& .MuiChip-icon': {
              color: 'text.secondary'
            }
          }
        };
      case Status.IN_PROGRESS:
        return {
          color: 'primary' as const,
          icon: <InProgressIcon fontSize="small" />,
          label: 'In Progress',
          tooltip: 'Mark as Done',
          sx: {
            backgroundColor: 'primary.50',
            color: 'primary.700',
            borderColor: 'primary.200',
            '& .MuiChip-icon': {
              color: 'primary.600'
            }
          }
        };
      case Status.DONE:
        return {
          color: 'success' as const,
          icon: <DoneIcon fontSize="small" />,
          label: 'Done',
          tooltip: 'Mark as To Do',
          sx: {
            backgroundColor: 'success.50',
            color: 'success.700',
            borderColor: 'success.200',
            '& .MuiChip-icon': {
              color: 'success.600'
            }
          }
        };
    }
  };

  const config = getTaskStatusConfig(taskStatus);

  if(isPending){
    return (
      <Skeleton 
        variant="rounded" 
        width={90} 
        height={24}
        sx={{
          borderRadius: '12px'
        }}
      />
    );
  }

  return (
        <Chip 
        label={config.label}
        color={config.color}
        icon={config.icon}
        clickable
        onClick={handleStatusChange}
        size="small"
        variant="outlined"
        sx={{
            minWidth: '90px',
            maxWidth: 'fit-content',
            fontWeight: 500,
            fontSize: '0.75rem',
            height: '24px',
            ...config.sx
        }}
        />
  );
};