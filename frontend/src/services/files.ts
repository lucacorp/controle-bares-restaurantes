// src/services/files.ts
export const openPdf = (id:number)=>
  window.open(`${import.meta.env.VITE_API}/comanda-resumo/${id}/pdf`,'_blank');
export const openXml = (id:number)=>
  window.open(`${import.meta.env.VITE_API}/comanda-resumo/${id}/xml`,'_blank');
