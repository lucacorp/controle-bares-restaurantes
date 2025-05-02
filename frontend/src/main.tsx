import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import ReceitaList from './ReceitaList'; // em vez de './App'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ReceitaList />
  </StrictMode>,
);
