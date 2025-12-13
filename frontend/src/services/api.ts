// src/services/api.ts
import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // ajuste se seu backend usar outra porta/host
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

// interceptor simples para log e mensagem de erro padronizada
api.interceptors.response.use(
  (resp) => resp,
  (error) => {
    // opcional: log centralizado
    console.error("API ERROR:", error?.response?.status, error?.response?.data);
    return Promise.reject(error);
  }
);

export default api;
