import { useState } from "react";
import { API_BASE_STORAGE_KEY, ApiError, chamarApi } from "../lib/api";
import type { InfoApi } from "../lib/types";

interface Props {
  apiBase: string;
  onApiBaseChange: (base: string) => void;
  onConectado: () => void;
}

export function ConexaoCard({ apiBase, onApiBaseChange, onConectado }: Props) {
  const [rascunho, setRascunho] = useState(apiBase);
  const [status, setStatus] = useState<string>("");
  const [testando, setTestando] = useState(false);

  function salvar() {
    onApiBaseChange(rascunho);
    localStorage.setItem(API_BASE_STORAGE_KEY, rascunho);
    setStatus("Endereço salvo neste navegador.");
  }

  async function testar() {
    setTestando(true);
    setStatus("Testando...");
    try {
      const info = await chamarApi<InfoApi>(rascunho, "/");
      setStatus(`Conectado — ${info.api} v${info.versao}`);
      onApiBaseChange(rascunho);
      localStorage.setItem(API_BASE_STORAGE_KEY, rascunho);
      onConectado();
    } catch (e) {
      const msg = e instanceof ApiError || e instanceof Error ? e.message : String(e);
      setStatus("Não foi possível conectar: " + msg);
    } finally {
      setTestando(false);
    }
  }

  return (
    <div className="card">
      <h2>Conexão com a API</h2>
      <p className="muted">
        Esta página é só o front-end (React estático) — a API precisa estar rodando. Clone o
        repositório e rode <code>./build.sh && ./run.sh 8080</code>, então aponte o endereço
        abaixo (por padrão <code>http://localhost:8080</code>).
      </p>
      <div className="row">
        <div className="field" style={{ flex: 2 }}>
          <label htmlFor="apiBase">URL base da API</label>
          <input
            id="apiBase"
            type="text"
            value={rascunho}
            onChange={(e) => setRascunho(e.target.value)}
          />
        </div>
        <button type="button" className="secondary" onClick={salvar}>
          Salvar
        </button>
        <button type="button" className="secondary" onClick={testar} disabled={testando}>
          {testando ? "Testando..." : "Testar conexão"}
        </button>
      </div>
      <div className="muted" style={{ marginTop: 10 }}>
        {status}
      </div>
    </div>
  );
}
