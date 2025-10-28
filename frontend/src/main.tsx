import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { BrowserRouter } from 'react-router-dom'
import AppRoutes from './routes/AppRoutes.tsx'
import { AuthContextProvider } from './context/AuthContext.tsx'
import { ToastProvider } from './context/ToastContext.tsx'


createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <BrowserRouter>
      <AuthContextProvider>
          <ToastProvider>
            <AppRoutes />
              <App />
          </ToastProvider>
      </AuthContextProvider>
    </BrowserRouter>
  </StrictMode>,
)