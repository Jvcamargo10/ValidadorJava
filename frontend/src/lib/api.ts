export const API_BASE_STORAGE_KEY = "validadorApi.base";
export const DEFAULT_API_BASE = "http://localhost:8080";

export class ApiError extends Error {}

/**
 * Chama a API do Validador. `base` é sempre o valor atual configurado pelo
 * usuário (nunca embutido em tempo de build) — o front-end é publicado
 * estaticamente no GitHub Pages e a API roda em outro lugar.
 */
export async function chamarApi<T>(
  base: string,
  path: string,
  opts: RequestInit = {},
): Promise<T> {
  const url = base.replace(/\/$/, "") + path;
  const headers = { "Content-Type": "application/json", ...(opts.headers ?? {}) };
  const resp = await fetch(url, { ...opts, headers });
  const texto = await resp.text();
  let corpo: unknown = null;
  if (texto) {
    try {
      corpo = JSON.parse(texto);
    } catch {
      corpo = texto;
    }
  }
  if (!resp.ok) {
    const erro =
      corpo && typeof corpo === "object" && "erro" in corpo
        ? String((corpo as { erro: unknown }).erro)
        : `Erro HTTP ${resp.status}`;
    throw new ApiError(erro);
  }
  return corpo as T;
}
