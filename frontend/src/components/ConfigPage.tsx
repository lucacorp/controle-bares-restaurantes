import { useEffect, useState, useRef } from "react";
import api from "../services/api";
import { Tab } from "@headlessui/react";

type Config = { chave: string; valor: string };

const grupos = [
  { id: "empresa", label: "Empresa" },
  { id: "sat", label: "Fiscal / SAT" },
  { id: "nfe", label: "NF-e / Certificado" },
  { id: "printer", label: "Impressora" },
  { id: "sistema", label: "Sistema" },
];

export default function ConfigPage() {
  const [configs, setConfigs] = useState<Config[]>([]);
  const [valores, setValores] = useState<Record<string, string>>({});
  const [msg, setMsg] = useState("");
  const timeouts = useRef<Record<string, NodeJS.Timeout>>({});

  const load = async () => {
    const { data } = await api.get<Config[]>("/configuracoes");
    setConfigs(data);
    // Inicializa valores locais
    const vals: Record<string, string> = {};
    data.forEach(c => vals[c.chave] = c.valor);
    setValores(vals);
  };

  const salvar = async (chave: string, valor: string) => {
    try {
      await api.post("/configuracoes", { chave, valor });
      setMsg("✅ Salvo!");
      setTimeout(() => setMsg(""), 2000);
    } catch (error) {
      setMsg("❌ Erro ao salvar");
      setTimeout(() => setMsg(""), 3000);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const getVal = (k: string) => valores[k] || "";

  const setVal = (k: string, v: string) => {
    // Atualiza imediatamente o estado local
    setValores(prev => ({ ...prev, [k]: v }));
    
    // Cancela timeout anterior se existir
    if (timeouts.current[k]) {
      clearTimeout(timeouts.current[k]);
    }
    
    // Agenda salvamento após 1 segundo de inatividade
    timeouts.current[k] = setTimeout(() => {
      salvar(k, v);
      delete timeouts.current[k];
    }, 1000);
  };

  const onBlur = (k: string) => {
    // Se houver timeout pendente, salva imediatamente
    if (timeouts.current[k]) {
      clearTimeout(timeouts.current[k]);
      salvar(k, valores[k] || "");
      delete timeouts.current[k];
    }
  };

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
            <div className="bg-blue-50 p-4 rounded-lg mb-4">
              <h3 className="font-semibold text-blue-900 mb-2">Dados da Empresa</h3>
              <p className="text-sm text-blue-700">Informações que aparecerão nos documentos fiscais</p>
            </div>

            <Campo label="Razão Social" valor={getVal("empresa.razaoSocial")} onChange={(v) => setVal("empresa.razaoSocial", v)} onBlur={() => onBlur("empresa.razaoSocial")} />
            <Campo label="Nome Fantasia" valor={getVal("empresa.nomeFantasia")} onChange={(v) => setVal("empresa.nomeFantasia", v)} onBlur={() => onBlur("empresa.nomeFantasia")} />
            <Campo label="CNPJ" valor={getVal("empresa.cnpj")} onChange={(v) => setVal("empresa.cnpj", v)} onBlur={() => onBlur("empresa.cnpj")} />
            <Campo label="Inscrição Estadual" valor={getVal("empresa.ie")} onChange={(v) => setVal("empresa.ie", v)} onBlur={() => onBlur("empresa.ie")} />
            <Campo label="UF" valor={getVal("empresa.uf")} onChange={(v) => setVal("empresa.uf", v)} onBlur={() => onBlur("empresa.uf")} placeholder="SP" />

            <div className="bg-gray-50 p-4 rounded-lg mt-4">
              <h3 className="font-semibold mb-3">Endereço</h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <Campo label="Logradouro" valor={getVal("empresa.endereco.logradouro")} onChange={(v) => setVal("empresa.endereco.logradouro", v)} onBlur={() => onBlur("empresa.endereco.logradouro")} />
                <Campo label="Número" valor={getVal("empresa.endereco.numero")} onChange={(v) => setVal("empresa.endereco.numero", v)} onBlur={() => onBlur("empresa.endereco.numero")} />
                <Campo label="Bairro" valor={getVal("empresa.endereco.bairro")} onChange={(v) => setVal("empresa.endereco.bairro", v)} onBlur={() => onBlur("empresa.endereco.bairro")} />
                <Campo label="Cidade" valor={getVal("empresa.endereco.cidade")} onChange={(v) => setVal("empresa.endereco.cidade", v)} onBlur={() => onBlur("empresa.endereco.cidade")} />
                <Campo label="UF" valor={getVal("empresa.endereco.uf")} onChange={(v) => setVal("empresa.endereco.uf", v)} onBlur={() => onBlur("empresa.endereco.uf")} />
                <Campo label="CEP" valor={getVal("empresa.endereco.cep")} onChange={(v) => setVal("empresa.endereco.cep", v)} onBlur={() => onBlur("empresa.endereco.cep")} />
              </div>
            </div>
          </Tab.Panel>


          {/* -------- SAT -------- */}
          <Tab.Panel className="space-y-3">
            <div className="bg-amber-50 p-4 rounded-lg mb-4">
              <h3 className="font-semibold text-amber-900 mb-2">SAT (Sistema Autenticador e Transmissor)</h3>
              <p className="text-sm text-amber-700">Configurações para emissão de CF-e SAT (Modelo 59)</p>
            </div>

            <CampoSelect
              label="Modo Fiscal"
              valor={getVal("modo.fiscal")}
              onChange={(v) => { setVal("modo.fiscal", v); onBlur("modo.fiscal"); }}
              opcoes={[
                { label: "SAT (Modelo 59)", value: "SAT" },
                { label: "NFC-e (Modelo 65)", value: "NFCE" },
              ]}
            />
            <Campo label="Série do SAT" valor={getVal("sat.serie")} onChange={(v) => setVal("sat.serie", v)} onBlur={() => onBlur("sat.serie")} />
            <Campo label="Ambiente (PRODUCAO/HOMOLOGACAO)" valor={getVal("sat.ambiente")} onChange={(v) => setVal("sat.ambiente", v)} onBlur={() => onBlur("sat.ambiente")} />
            <Campo label="Caminho XML" valor={getVal("sat.caminhoXml")} onChange={(v) => setVal("sat.caminhoXml", v)} onBlur={() => onBlur("sat.caminhoXml")} />
            <Campo label="CSC – ID" valor={getVal("sat.csc.id")} onChange={(v) => setVal("sat.csc.id", v)} onBlur={() => onBlur("sat.csc.id")} />
            <Campo label="CSC – Token" valor={getVal("sat.csc.token")} onChange={(v) => setVal("sat.csc.token", v)} onBlur={() => onBlur("sat.csc.token")} />
          </Tab.Panel>

          {/* -------- NF-e / Certificado -------- */}
          <Tab.Panel className="space-y-3">
            <div className="bg-green-50 p-4 rounded-lg mb-4">
              <h3 className="font-semibold text-green-900 mb-2">NF-e / Certificado Digital</h3>
              <p className="text-sm text-green-700">Configurações para emissão de NFC-e com comunicação direta SEFAZ</p>
            </div>

            <div className="bg-yellow-50 border border-yellow-200 p-4 rounded-lg mb-4">
              <h4 className="font-semibold text-yellow-900 mb-2">⚙️ Certificado Digital A1</h4>
              <p className="text-sm text-yellow-800 mb-3">
                Arquivo .pfx ou .p12 com certificado digital válido emitido por AC credenciada
              </p>
              <Campo 
                label="Caminho do Certificado" 
                valor={getVal("nfe.certificado.caminho")} 
                onChange={(v) => setVal("nfe.certificado.caminho", v)}
                onBlur={() => onBlur("nfe.certificado.caminho")}
                placeholder="C:/certificados/certificado.pfx"
              />
              <CampoSenha 
                label="Senha do Certificado" 
                valor={getVal("nfe.certificado.senha")} 
                onChange={(v) => setVal("nfe.certificado.senha", v)}
                onBlur={() => onBlur("nfe.certificado.senha")}
                placeholder="••••••••"
              />
            </div>

            <div className="bg-gray-50 p-4 rounded-lg">
              <h4 className="font-semibold mb-3">Ambiente SEFAZ</h4>
              <CampoSelect
                label="Ambiente de Emissão"
                valor={getVal("nfe.homologacao")}
                onChange={(v) => { setVal("nfe.homologacao", v); onBlur("nfe.homologacao"); }}
                opcoes={[
                  { label: "Homologação (Testes)", value: "true" },
                  { label: "Produção (Real)", value: "false" },
                ]}
              />
              <div className="mt-2 text-sm text-gray-600">
                ⚠️ Use Homologação para testes. Produção apenas com certificado válido.
              </div>
            </div>
          </Tab.Panel>

          {/* -------- Impressora -------- */}
          <Tab.Panel className="space-y-3">
            <Campo label="Impressora padrão" valor={getVal("printer.default")} onChange={(v) => setVal("printer.default", v)} onBlur={() => onBlur("printer.default")} />
            <Campo label="Largura do papel (58/80)" valor={getVal("printer.paperWidth")} onChange={(v) => setVal("printer.paperWidth", v)} onBlur={() => onBlur("printer.paperWidth")} />
          </Tab.Panel>

          {/* -------- Sistema -------- */}
          <Tab.Panel className="space-y-3">
            <Campo label="Nome do Caixa" valor={getVal("sistema.caixa")} onChange={(v) => setVal("sistema.caixa", v)} onBlur={() => onBlur("sistema.caixa")} />
            <Campo label="Logout automático (min)" valor={getVal("sistema.autoLogoutMin")} onChange={(v) => setVal("sistema.autoLogoutMin", v)} onBlur={() => onBlur("sistema.autoLogoutMin")} />
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
  onBlur,
  placeholder,
}: {
  label: string;
  valor: string;
  onChange: (v: string) => void;
  onBlur?: () => void;
  placeholder?: string;
}) {
  return (
    <div>
      <label className="block text-sm font-medium mb-1">{label}</label>
      <input
        value={valor}
        onChange={(e) => onChange(e.target.value)}
        onBlur={onBlur}
        placeholder={placeholder}
        className="border rounded p-2 w-full focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
      />
    </div>
  );
}

function CampoSenha({
  label,
  valor,
  onChange,
  onBlur,
  placeholder,
}: {
  label: string;
  valor: string;
  onChange: (v: string) => void;
  onBlur?: () => void;
  placeholder?: string;
}) {
  const [mostrar, setMostrar] = useState(false);
  
  return (
    <div>
      <label className="block text-sm font-medium mb-1">{label}</label>
      <div className="relative">
        <input
          type={mostrar ? "text" : "password"}
          value={valor}
          onChange={(e) => onChange(e.target.value)}
          onBlur={onBlur}
          placeholder={placeholder}
          className="border rounded p-2 w-full pr-10 focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        />
        <button
          type="button"
          onClick={() => setMostrar(!mostrar)}
          className="absolute right-2 top-2 text-gray-500 hover:text-gray-700"
        >
          {mostrar ? "👁️" : "👁️‍🗨️"}
        </button>
      </div>
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
