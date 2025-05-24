import React from 'react';
import ReactDOM from 'react-dom/client';
import { unstable_HistoryRouter as HistoryRouter } from 'react-router-dom';
import { history } from './history';
import App from './App';
import { AuthProvider } from './AuthContext';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <HistoryRouter history={history}>
		<AuthProvider>
              <App />
		 </AuthProvider>	  
      </HistoryRouter>
   
  </React.StrictMode>
);
