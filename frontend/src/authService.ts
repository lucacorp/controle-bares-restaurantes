// src/authService.ts
import { history } from './history';

export function logout() {
  localStorage.removeItem('token');
  history.push('/login'); // navega sem recarregar a página
}
