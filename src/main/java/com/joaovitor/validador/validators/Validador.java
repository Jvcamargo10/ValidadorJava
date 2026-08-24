package com.joaovitor.validador.validators;

import com.joaovitor.validador.model.ResultadoValidacao;

public interface Validador {
    ResultadoValidacao validar(String valor);
}
