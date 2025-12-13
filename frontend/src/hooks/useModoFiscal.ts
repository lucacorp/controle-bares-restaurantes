// src/hooks/useModoFiscal.ts
import { useEffect, useState } from "react";
import api from "../services/api";

export function useModoFiscal() {
  const [modoFiscal, setModoFiscal] = useState<"SAT" | "NFCE" | null>(null);

  useEffect(() => {
    api.get(`/config/fiscal`)
      .then((res) => setModoFiscal(res.data?.modoFiscal || "SAT"))
      .catch(() => setModoFiscal("SAT")); // fallback
  }, []);

  return modoFiscal;
}
