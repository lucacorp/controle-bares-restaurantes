import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './components/Login'
import Dashboard from './components/Dashboard'
import TableList from './components/TableList'
import ProductForm from './components/ProductForm'

export default function App() {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(() => {
    return localStorage.getItem('loggedIn') === 'true'
  })

  useEffect(() => {
    localStorage.setItem('loggedIn', isLoggedIn.toString())
  }, [isLoggedIn])

  const handleLogin = () => setIsLoggedIn(true)
  
  const handleLogout = () => {
    localStorage.removeItem('loggedIn')
    setIsLoggedIn(false)
  }

  const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    return isLoggedIn ? children : <Navigate to="/login" replace />
  }

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to={isLoggedIn ? "/dashboard" : "/login"} replace />} />

        <Route
          path="/login"
          element={
            isLoggedIn ? (
              <Navigate to="/dashboard" replace />
            ) : (
              <div className="min-h-screen bg-gray-100 flex items-center justify-center">
                <Login onLogin={handleLogin} />
              </div>
            )
          }
        />

        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <Dashboard onLogout={handleLogout} />
            </ProtectedRoute>
          }
        />

        <Route
          path="/mesas"
          element={
            <ProtectedRoute>
              <TableList />
            </ProtectedRoute>
          }
        />

        <Route
          path="/produtos"
          element={
            <ProtectedRoute>
              <ProductForm />
            </ProtectedRoute>
          }
        />
      </Routes>
    </Router>
  )
}
