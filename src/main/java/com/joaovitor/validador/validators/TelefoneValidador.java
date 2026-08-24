package com.joaovitor.validador.validators;

import com.joaovitor.validador.enums.TipoDado;
import com.joaovitor.validador.model.ResultadoValidacao;

public final class TelefoneValidador implements Validador {

    @Override
    public ResultadoValidacao validar(String valor) {
        String digitos = valor.replaceAll("\\D", "");

        if (digitos.length() != 10 && digitos.length() != 11) {
            return new ResultadoValidacao(TipoDado.TELEFONE, valor, false, null,
                    "Telefone deve ter DDD + 8 ou 9 dígitos.");
        }

        String ddd = digitos.substring(0, 2);
        String numero = digitos.substring(2);

        String formatado = numero.length() == 9
                ? "(%s) %s-%s".formatted(ddd, numero.substring(0, 5), numero.substring(5))
                : "(%s) %s-%s".formatted(ddd, numero.substring(0, 4), numero.substring(4));

        return new ResultadoValidacao(TipoDado.TELEFONE, valor, true, formatado, "Telefone válido.");
    }
}
