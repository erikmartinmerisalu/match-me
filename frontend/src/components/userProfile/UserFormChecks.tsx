import React from 'react'
import { Bounce, ToastContainer } from 'react-toastify'

function UserFormChecks() {
  return (
    <div>UserFormChecks
    
    
    <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick={false}
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
        transition={Bounce}
        />
    </div>
  )
}

export default UserFormChecks