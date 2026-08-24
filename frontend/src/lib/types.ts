export type TipoDado = "cpf" | "cnpj" | "email" | "data" | "telefone";

export const TIPOS: TipoDado[] = ["cpf", "cnpj", "email", "data", "telefone"];

export const EXEMPLOS: Record<TipoDado, string> = {
  cpf: "111.444.777-35",
  cnpj: "11.222.333/0001-81",
  email: "nome@exemplo.com",
  data: "21/08/2026",
  telefone: "11912345678",
};

export interface ResultadoValidacao {
  tipo: TipoDado;
  valorOriginal: string;
  valido: boolean;
  valorFormatado: string | null;
  mensagem: string;
}

export interface HistoricoItem {
  tipo: TipoDado;
  valorOriginal: string;
  valido: boolean;
  valorFormatado: string | null;
  mensagem: string;
  criadoEm: string;
}

export interface HistoricoResposta {
  itens: HistoricoItem[];
}

export interface EstatisticasResposta {
  porTipo: Partial<Record<TipoDado, number>>;
}

export interface InfoApi {
  api: string;
  versao: string;
  descricao: string;
  endpoints: string[];
}
