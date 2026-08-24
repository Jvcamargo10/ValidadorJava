import { useState } from "react";
import { ConexaoCard } from "./components/ConexaoCard";
import { EstatisticasCard } from "./components/EstatisticasCard";
import { HistoricoCard } from "./components/HistoricoCard";
import { ValidarCard } from "./components/ValidarCard";
import { API_BASE_STORAGE_KEY, DEFAULT_API_BASE } from "./lib/api";

function lerApiBaseInicial(): string {
  try {
    return localStorage.getItem(API_BASE_STORAGE_KEY) || DEFAULT_API_BASE;
  } catch {
    return DEFAULT_API_BASE;
  }
}

export default function App() {
  const [apiBase, setApiBase] = useState(lerApiBaseInicial);
  const [refreshToken, setRefreshToken] = useState(0);

  function atualizarDados() {
    setRefreshToken((t) => t + 1);
  }

  return (
    <>
      <header>
        <h1>Validador de Dados — Java</h1>
        <p>
          Painel em React + TypeScript que consome a API em Java puro (sem Spring) —{" "}
          <a href="https://github.com/Jvcamargo10/ValidadorJava">ver código-fonte</a> · irmã da{" "}
          <a href="https://github.com/Jvcamargo10/ValidadorPHP">versão em PHP</a>
        </p>
      </header>

      <main>
        <ConexaoCard apiBase={apiBase} onApiBaseChange={setApiBase} onConectado={atualizarDados} />
        <ValidarCard apiBase={apiBase} onValidado={atualizarDados} />
        <EstatisticasCard apiBase={apiBase} refreshToken={refreshToken} />
        <HistoricoCard apiBase={apiBase} refreshToken={refreshToken} />
      </main>

      <footer>
        Front-end React — nenhum dado sai do seu navegador além das chamadas à API que você mesmo
        está rodando.
      </footer>
    </>
  );
}
