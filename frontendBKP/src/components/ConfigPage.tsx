import { useEffect, useState } from "react";
import api from "../services/api";
import { Tab } from "@headlessui/react";

type Config = { chave: string; valor: string };

const grupos = [
  { id: "empresa", label: "Empresa" },
  { id: "sat", label: "Fiscal / SAT" },
  { id: "printer", label: "Impressora" },
  { id: "sistema", label: "Sistema" },
];

export default function ConfigPage() {
  const [configs, setConfigs] = useState<Config[]>([]);
  const [msg, setMsg] = useState("");

  const load = async () => {
    const { data } = await api.get<Config[]>("/configuracoes");
    setConfigs(data);
  };

  const salvar = async (c: Config) => {
    await api.post("/configuracoes", c);
    setMsg("Salvo!");
    load();
  };

  useEffect(() => {
    load();
  }, []);

  const getVal = (k: string) => configs.find((c) => c.chave === k)?.valor || "";
  const setVal = (k: string, v: string) => salvar({ chave: k, valor: v });

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">Configurações</h1>
      {msg && <p className="text-green-700 mb-3">{msg}</p>}

      <Tab.Group>
        <Tab.List className="flex gap-2 border-b mb-4">
          {grupos.map((g) => (
            <Tab
              key={g.id}
              className={({ selected }) =>
                `px-3 py-1 outline-none ${
                  selected ? "border-b-2 border-blue-600 font-medium" : ""
                }`
              }
            >
              {g.label}
            </Tab>
          ))}
        </Tab.List>

        <Tab.Panels>
          {/* -------- Empresa -------- */}
          <Tab.Panel className="space-y-3">
  <Campo label="Razão Social" valor={getVal("empresa.razaoSocial")} onChange={(v) => setVal("empresa.razaoSocial", v)} />
  <Campo label="Nome Fantasia" valor={getVal("empresa.nomeFantasia")} onChange={(v) => setVal("empresa.nomeFantasia", v)} />
  <Campo label="CNPJ" valor={getVal("empresa.cnpj")} onChange={(v) => setVal("empresa.cnpj", v)} />
  <Campo label="Inscrição Estadual" valor={getVal("empresa.ie")} onChange={(v) => setVal("empresa.ie", v)} />

  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
    <Campo label="Logradouro" valor={getVal("empresa.endereco.logradouro")} onChange={(v) => setVal("empresa.endereco.logradouro", v)} />
    <Campo label="Número" valor={getVal("empresa.endereco.numero")} onChange={(v) => setVal("empresa.endereco.numero", v)} />
    <Campo label="Bairro" valor={getVal("empresa.endereco.bairro")} onChange={(v) => setVal("empresa.endereco.bairro", v)} />
    <Campo label="Cidade" valor={getVal("empresa.endereco.cidade")} onChange={(v) => setVal("empresa.endereco.cidade", v)} />
    <Campo label="UF" valor={getVal("empresa.endereco.uf")} onChange={(v) => setVal("empresa.endereco.uf", v)} />
    <Campo label="CEP" valor={getVal("empresa.endereco.cep")} onChange={(v) => setVal("empresa.endereco.cep", v)} />
  </div>
</Tab.Panel>


          {/* -------- SAT -------- */}
          <Tab.Panel className="space-y-3">
            <CampoSelect
              label="Modo Fiscal"
              valor={getVal("modo.fiscal")}
              onChange={(v) => setVal("modo.fiscal", v)}
              opcoes={[
                { label: "SAT (Modelo 59)", value: "SAT" },
                { label: "NFC-e (Modelo 65)", value: "NFCE" },
              ]}
            />
            <Campo label="Série do SAT" valor={getVal("sat.serie")} onChange={(v) => setVal("sat.serie", v)} />
            <Campo label="Ambiente (PRODUCAO/HOMOLOGACAO)" valor={getVal("sat.ambiente")} onChange={(v) => setVal("sat.ambiente", v)} />
            <Campo label="Caminho XML" valor={getVal("sat.caminhoXml")} onChange={(v) => setVal("sat.caminhoXml", v)} />
            <Campo label="CSC – ID" valor={getVal("sat.csc.id")} onChange={(v) => setVal("sat.csc.id", v)} />
            <Campo label="CSC – Token" valor={getVal("sat.csc.token")} onChange={(v) => setVal("sat.csc.token", v)} />
          </Tab.Panel>

          {/* -------- Impressora -------- */}
          <Tab.Panel className="space-y-3">
            <Campo label="Impressora padrão" valor={getVal("printer.default")} onChange={(v) => setVal("printer.default", v)} />
            <Campo label="Largura do papel (58/80)" valor={getVal("printer.paperWidth")} onChange={(v) => setVal("printer.paperWidth", v)} />
          </Tab.Panel>

          {/* -------- Sistema -------- */}
          <Tab.Panel className="space-y-3">
            <Campo label="Nome do Caixa" valor={getVal("sistema.caixa")} onChange={(v) => setVal("sistema.caixa", v)} />
            <Campo label="Logout automático (min)" valor={getVal("sistema.autoLogoutMin")} onChange={(v) => setVal("sistema.autoLogoutMin", v)} />
          </Tab.Panel>
        </Tab.Panels>
      </Tab.Group>
    </div>
  );
}

function Campo({
  label,
  valor,
  onChange,
}: {
  label: string;
  valor: string;
  onChange: (v: string) => void;
}) {
  return (
    <div>
      <label className="block text-sm mb-1">{label}</label>
      <input
        value={valor}
        onChange={(e) => onChange(e.target.value)}
        className="border rounded p-2 w-full"
      />
    </div>
  );
}

function CampoSelect({
  label,
  valor,
  onChange,
  opcoes,
}: {
  label: string;
  valor: string;
  onChange: (v: string) => void;
  opcoes: { label: string; value: string }[];
}) {
  return (
    <div>
      <label className="block text-sm mb-1">{label}</label>
      <select
        value={valor}
        onChange={(e) => onChange(e.target.value)}
        className="border rounded p-2 w-full"
      >
        {opcoes.map((o) => (
          <option key={o.value} value={o.value}>
            {o.label}
          </option>
        ))}
      </select>
    </div>
  );
}
