export interface ToastProps {
  message: string;
  type: 'success' | 'failure' | 'warning';
  onClose: () => void;
}
