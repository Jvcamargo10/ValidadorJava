package com.joaovitor.validador.validators;

import com.joaovitor.validador.enums.TipoDado;
import com.joaovitor.validador.model.ResultadoValidacao;
import java.util.regex.Pattern;

public final class EmailValidador implements Validador {

    private static final Pattern REGEX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public ResultadoValidacao validar(String valor) {
        String normalizado = valor.trim().toLowerCase();

        if (!REGEX.matcher(normalizado).matches()) {
            return new ResultadoValidacao(TipoDado.EMAIL, valor, false, null, "E-mail em formato inválido.");
        }

        return new ResultadoValidacao(TipoDado.EMAIL, valor, true, normalizado, "E-mail válido.");
    }
}
