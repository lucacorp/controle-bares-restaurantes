import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import ReceitaList from './components/ReceitaList';

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ReceitaList />
  </StrictMode>,
);
