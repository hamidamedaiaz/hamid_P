import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import { Toaster } from 'react-hot-toast'
import './styles/Global.css'
import './App.css'
import App from './App.jsx'

createRoot(document.getElementById('root')).render(
    <StrictMode>
        <App/>
        <Toaster
            position="top-right"
            reverseOrder={false}
            gutter={8}
            toastOptions={{
                duration: 4000,
                style: {
                    background: '#363636',
                    color: '#fff',
                    borderRadius: '10px',
                    padding: '16px',
                },
            }}
        />
    </StrictMode>,
)
