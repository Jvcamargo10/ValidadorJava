package com.joaovitor.validador.validators;

import com.joaovitor.validador.enums.TipoDado;

public final class ValidadorFactory {

    private ValidadorFactory() {
    }

    public static Validador criar(TipoDado tipo) {
        return switch (tipo) {
            case CPF -> new CpfValidador();
            case CNPJ -> new CnpjValidador();
            case EMAIL -> new EmailValidador();
            case DATA -> new DataBrValidador();
            case TELEFONE -> new TelefoneValidador();
        };
    }
}
