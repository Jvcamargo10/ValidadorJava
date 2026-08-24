import { useEffect, useState } from "react";
import { ApiError, chamarApi } from "../lib/api";
import { TIPOS, type EstatisticasResposta, type TipoDado } from "../lib/types";

interface Props {
  apiBase: string;
  refreshToken: number;
}

export function EstatisticasCard({ apiBase, refreshToken }: Props) {
  const [contagens, setContagens] = useState<Partial<Record<TipoDado, number>>>({});
  const [carregado, setCarregado] = useState(false);
  const [erro, setErro] = useState("");

  useEffect(() => {
    let cancelado = false;
    async function carregar() {
      setErro("");
      try {
        const r = await chamarApi<EstatisticasResposta>(apiBase, "/api/estatisticas");
        if (!cancelado) {
          setContagens(r.porTipo || {});
          setCarregado(true);
        }
      } catch (e) {
        if (!cancelado) {
          const msg = e instanceof ApiError || e instanceof Error ? e.message : String(e);
          setErro(msg);
        }
      }
    }
    carregar();
    return () => {
      cancelado = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiBase, refreshToken]);

  const tiposComDados = TIPOS.filter((t) => contagens[t] !== undefined);
  const max = Math.max(1, ...Object.values(contagens).map((v) => v ?? 0));

  return (
    <div className="card">
      <h2>Contagem por tipo</h2>
      {erro && <div className="muted">{erro}</div>}
      {!erro && !carregado && <div className="muted">Carregando…</div>}
      {!erro && carregado && tiposComDados.length === 0 && (
        <p className="muted">Nenhuma validação registrada ainda.</p>
      )}
      {!erro &&
        carregado &&
        tiposComDados.map((t) => {
          const valor = contagens[t] || 0;
          const largura = Math.round((valor / max) * 100);
          return (
            <div className="bar-row" key={t}>
              <span>{t}</span>
              <div className="bar-track">
                <div className="bar-fill" style={{ width: `${largura}%` }} />
              </div>
              <span>{valor}</span>
            </div>
          );
        })}
    </div>
  );
}
