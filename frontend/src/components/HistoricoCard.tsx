import { useEffect, useState } from "react";
import { ApiError, chamarApi } from "../lib/api";
import type { HistoricoItem, HistoricoResposta } from "../lib/types";

interface Props {
  apiBase: string;
  refreshToken: number;
}

export function HistoricoCard({ apiBase, refreshToken }: Props) {
  const [itens, setItens] = useState<HistoricoItem[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);

  async function carregar() {
    setCarregando(true);
    setErro("");
    try {
      const r = await chamarApi<HistoricoResposta>(apiBase, "/api/historico?limite=20");
      setItens(r.itens);
    } catch (e) {
      const msg = e instanceof ApiError || e instanceof Error ? e.message : String(e);
      setErro(msg);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    carregar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiBase, refreshToken]);

  return (
    <div className="card">
      <h2>
        Histórico recente
        <button
          type="button"
          className="secondary"
          style={{ float: "right" }}
          onClick={carregar}
          disabled={carregando}
        >
          {carregando ? "Atualizando..." : "Atualizar"}
        </button>
      </h2>
      <table>
        <thead>
          <tr>
            <th>Tipo</th>
            <th>Valor original</th>
            <th>Resultado</th>
            <th>Mensagem</th>
            <th>Quando</th>
          </tr>
        </thead>
        <tbody>
          {erro && (
            <tr>
              <td colSpan={5} className="muted">
                {erro}
              </td>
            </tr>
          )}
          {!erro && itens.length === 0 && (
            <tr>
              <td colSpan={5} className="muted">
                Nenhuma validação registrada ainda.
              </td>
            </tr>
          )}
          {!erro &&
            itens.map((item, i) => (
              <tr key={`${item.criadoEm}-${i}`}>
                <td>{item.tipo}</td>
                <td>{item.valorOriginal}</td>
                <td>
                  <span className={`badge ${item.valido ? "ok" : "err"}`}>
                    {item.valido ? "válido" : "inválido"}
                  </span>
                </td>
                <td>{item.mensagem}</td>
                <td className="muted">{new Date(item.criadoEm).toLocaleString("pt-BR")}</td>
              </tr>
            ))}
        </tbody>
      </table>
    </div>
  );
}
