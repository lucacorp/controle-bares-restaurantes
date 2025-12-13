// src/hooks/useModoFiscal.ts
import { useEffect, useState } from "react";
import axios from "axios";

export function useModoFiscal() {
  const [modoFiscal, setModoFiscal] = useState<"SAT" | "NFCE" | null>(null);

  useEffect(() => {
    axios.get("/api/config/fiscal")
      .then((res) => setModoFiscal(res.data.modoFiscal))
      .catch(() => setModoFiscal("SAT")); // fallback
  }, []);

  return modoFiscal;
}
