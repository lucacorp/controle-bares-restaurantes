import { Navigate } from "react-router-dom";
import { useAuth } from "../AuthContext";

export default function RotaProtegida({ children }: { children: JSX.Element }) {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
}
