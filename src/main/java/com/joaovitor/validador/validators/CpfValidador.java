package com.joaovitor.validador.validators;

import com.joaovitor.validador.enums.TipoDado;
import com.joaovitor.validador.model.ResultadoValidacao;

/**
 * Mesmo algoritmo (módulo 11) e mesmas mensagens que ValidadorPHP\App\Services\Validators\CpfValidador
 * — ver contrato compartilhado em docs/REGRAS-VALIDACAO.md, seção 1.
 */
public final class CpfValidador implements Validador, DigitosUtil {

    private static final int[] PESOS_DV1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_DV2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public ResultadoValidacao validar(String valor) {
        String digitos = apenasDigitos(valor);

        if (digitos.length() != 11) {
            return resultado(valor, false, "CPF deve conter 11 dígitos.", null);
        }
        if (todosDigitosIguais(digitos)) {
            return resultado(valor, false, "CPF inválido (sequência de dígitos repetidos).", null);
        }
        if (!digitosVerificadoresValidos(digitos)) {
            return resultado(valor, false, "CPF inválido (dígito verificador não confere).", null);
        }

        String formatado = "%s.%s.%s-%s".formatted(
                digitos.substring(0, 3), digitos.substring(3, 6), digitos.substring(6, 9), digitos.substring(9, 11));

        return resultado(valor, true, "CPF válido.", formatado);
    }

    private boolean digitosVerificadoresValidos(String digitos) {
        int dv1 = calcularDigito(digitos.substring(0, 9), PESOS_DV1);
        int dv2 = calcularDigito(digitos.substring(0, 9) + dv1, PESOS_DV2);
        return digitos.charAt(9) == Character.forDigit(dv1, 10)
                && digitos.charAt(10) == Character.forDigit(dv2, 10);
    }

    private ResultadoValidacao resultado(String original, boolean valido, String mensagem, String formatado) {
        return new ResultadoValidacao(TipoDado.CPF, original, valido, formatado, mensagem);
    }
}
