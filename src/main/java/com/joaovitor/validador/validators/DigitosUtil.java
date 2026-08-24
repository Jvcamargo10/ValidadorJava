package com.joaovitor.validador.validators;

/**
 * Java não tem "traits" como o PHP — o equivalente mais próximo para compartilhar
 * comportamento entre CpfValidador e CnpjValidador são métodos default de interface,
 * usados aqui do mesmo jeito que RemovePontuacaoTrait é usada na versão em PHP.
 */
interface DigitosUtil {

    default String apenasDigitos(String valor) {
        return valor.replaceAll("\\D", "");
    }

    default boolean todosDigitosIguais(String digitos) {
        return digitos.matches("(\\d)\\1*");
    }

    default int calcularDigito(String base, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < base.length(); i++) {
            soma += Character.getNumericValue(base.charAt(i)) * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
