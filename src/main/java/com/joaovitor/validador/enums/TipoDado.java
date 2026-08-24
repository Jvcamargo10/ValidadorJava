package com.joaovitor.validador.enums;

/**
 * Mesmo conjunto de tipos suportado pela versão em PHP (ver enum App\Enums\TipoDado
 * em ValidadorPHP) — o valor textual usado na API é idêntico nas duas linguagens.
 */
public enum TipoDado {
    CPF("cpf"),
    CNPJ("cnpj"),
    EMAIL("email"),
    DATA("data"),
    TELEFONE("telefone");

    private final String valor;

    TipoDado(String valor) {
        this.valor = valor;
    }

    public String valor() {
        return valor;
    }

    public static TipoDado fromValor(String valor) {
        for (TipoDado tipo : values()) {
            if (tipo.valor.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de dado desconhecido: " + valor);
    }
}
