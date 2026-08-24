package com.joaovitor.validador.validators;

import com.joaovitor.validador.enums.TipoDado;
import com.joaovitor.validador.model.ResultadoValidacao;

/**
 * Mesmo algoritmo e mesmas mensagens que ValidadorPHP\App\Services\Validators\CnpjValidador
 * — ver contrato compartilhado em docs/REGRAS-VALIDACAO.md, seção 2.
 */
public final class CnpjValidador implements Validador, DigitosUtil {

    private static final int[] PESOS_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    @Override
    public ResultadoValidacao validar(String valor) {
        String digitos = apenasDigitos(valor);

        if (digitos.length() != 14) {
            return resultado(valor, false, "CNPJ deve conter 14 dígitos.", null);
        }
        if (todosDigitosIguais(digitos)) {
            return resultado(valor, false, "CNPJ inválido (sequência de dígitos repetidos).", null);
        }
        if (!digitosVerificadoresValidos(digitos)) {
            return resultado(valor, false, "CNPJ inválido (dígito verificador não confere).", null);
        }

        String formatado = "%s.%s.%s/%s-%s".formatted(
                digitos.substring(0, 2), digitos.substring(2, 5), digitos.substring(5, 8),
                digitos.substring(8, 12), digitos.substring(12, 14));

        return resultado(valor, true, "CNPJ válido.", formatado);
    }

    private boolean digitosVerificadoresValidos(String digitos) {
        int dv1 = calcularDigito(digitos.substring(0, 12), PESOS_DV1);
        int dv2 = calcularDigito(digitos.substring(0, 12) + dv1, PESOS_DV2);
        return digitos.charAt(12) == Character.forDigit(dv1, 10)
                && digitos.charAt(13) == Character.forDigit(dv2, 10);
    }

    private ResultadoValidacao resultado(String original, boolean valido, String mensagem, String formatado) {
        return new ResultadoValidacao(TipoDado.CNPJ, original, valido, formatado, mensagem);
    }
}
