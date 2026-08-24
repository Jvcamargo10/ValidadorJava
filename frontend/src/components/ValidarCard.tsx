import { useState, type FormEvent } from "react";
import { ApiError, chamarApi } from "../lib/api";
import { EXEMPLOS, TIPOS, type ResultadoValidacao, type TipoDado } from "../lib/types";

interface Props {
  apiBase: string;
  onValidado: () => void;
}

export function ValidarCard({ apiBase, onValidado }: Props) {
  const [tipo, setTipo] = useState<TipoDado>("cpf");
  const [valor, setValor] = useState("");
  const [resultado, setResultado] = useState<ResultadoValidacao | null>(null);
  const [erro, setErro] = useState("");
  const [enviando, setEnviando] = useState(false);

  async function onSubmit(ev: FormEvent) {
    ev.preventDefault();
    setErro("");
    setResultado(null);
    setEnviando(true);
    try {
      const r = await chamarApi<ResultadoValidacao>(apiBase, "/api/validar", {
        method: "POST",
        body: JSON.stringify({ tipo, valor }),
      });
      setResultado(r);
      onValidado();
    } catch (e) {
      const msg = e instanceof ApiError || e instanceof Error ? e.message : String(e);
      setErro(msg);
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="card">
      <h2>Validar um dado</h2>
      {erro && <div className="msg err">{erro}</div>}
      <form className="row" onSubmit={onSubmit}>
        <div className="field">
          <label htmlFor="tipo">Tipo</label>
          <select
            id="tipo"
            value={tipo}
            onChange={(e) => setTipo(e.target.value as TipoDado)}
          >
            {TIPOS.map((t) => (
              <option key={t} value={t}>
                {rotuloTipo(t)}
              </option>
            ))}
          </select>
        </div>
        <div className="field" style={{ flex: 2 }}>
          <label htmlFor="valor">Valor</label>
          <input
            id="valor"
            name="valor"
            placeholder={EXEMPLOS[tipo]}
            value={valor}
            onChange={(e) => setValor(e.target.value)}
            required
          />
        </div>
        <button type="submit" disabled={enviando}>
          {enviando ? "Validando..." : "Validar"}
        </button>
      </form>

      {resultado && (
        <div className="resultado">
          <div className="linha">
            <span>Resultado</span>
            <span>
              <span className={`badge ${resultado.valido ? "ok" : "err"}`}>
                {resultado.valido ? "válido" : "inválido"}
              </span>
            </span>
          </div>
          <div className="linha">
            <span>Valor original</span>
            <span>{resultado.valorOriginal}</span>
          </div>
          <div className="linha">
            <span>Valor formatado</span>
            <span>{resultado.valorFormatado ?? "—"}</span>
          </div>
          <div className="linha">
            <span>Mensagem</span>
            <span>{resultado.mensagem}</span>
          </div>
        </div>
      )}
    </div>
  );
}

function rotuloTipo(t: TipoDado): string {
  switch (t) {
    case "cpf":
      return "CPF";
    case "cnpj":
      return "CNPJ";
    case "email":
      return "E-mail";
    case "data":
      return "Data (dd/mm/aaaa)";
    case "telefone":
      return "Telefone";
  }
}
