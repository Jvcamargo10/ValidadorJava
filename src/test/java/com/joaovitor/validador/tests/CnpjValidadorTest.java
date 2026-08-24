package com.joaovitor.validador.tests;

import static com.joaovitor.validador.testing.Assert.assertEquals;
import static com.joaovitor.validador.testing.Assert.assertFalse;
import static com.joaovitor.validador.testing.Assert.assertTrue;

import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.validators.CnpjValidador;

/**
 * Casos conferidos com a regra do contrato compartilhado (docs/REGRAS-VALIDACAO.md, seção 2):
 * módulo 11 com os pesos específicos de CNPJ, rejeição de sequência repetida mesmo quando o
 * dígito calculado "acidentalmente" bateria, aceitação com e sem máscara.
 */
public final class CnpjValidadorTest {

    private final CnpjValidador validador = new CnpjValidador();

    public void testCnpjValidoSemMascaraEAceito() {
        ResultadoValidacao r = validador.validar("11222333000181");
        assertTrue("CNPJ válido sem máscara deveria passar: " + r.mensagem(), r.valido());
        assertEquals("valorFormatado deve usar a máscara padrão", "11.222.333/0001-81", r.valorFormatado());
    }

    public void testCnpjValidoComMascaraEAceito() {
        ResultadoValidacao r = validador.validar("11.222.333/0001-81");
        assertTrue("CNPJ válido com máscara deveria passar: " + r.mensagem(), r.valido());
        assertEquals("valorFormatado deve usar a máscara padrão", "11.222.333/0001-81", r.valorFormatado());
    }

    public void testOutroCnpjValidoConhecido() {
        assertTrue("11.144.477/0001-67 é um CNPJ válido conhecido", validador.validar("11.144.477/0001-67").valido());
    }

    public void testCnpjComDigitoVerificadorErradoEhRejeitado() {
        ResultadoValidacao r = validador.validar("11.222.333/0001-82");
        assertFalse("CNPJ com DV incorreto não deveria passar", r.valido());
        assertEquals("mensagem de DV inválido", "CNPJ inválido (dígito verificador não confere).", r.mensagem());
    }

    public void testCnpjComTamanhoErradoEhRejeitado() {
        ResultadoValidacao curto = validador.validar("1122233300018");
        assertFalse("CNPJ com 13 dígitos não deveria passar", curto.valido());
        assertEquals("mensagem de tamanho inválido", "CNPJ deve conter 14 dígitos.", curto.mensagem());

        ResultadoValidacao longo = validador.validar("112223330001811");
        assertFalse("CNPJ com 15 dígitos não deveria passar", longo.valido());
    }

    public void testSequenciaRepetidaEhRejeitadaMesmoQuePassasseNoModulo11() {
        ResultadoValidacao zeros = validador.validar("00.000.000/0000-00");
        assertFalse("sequência de zeros deveria ser rejeitada", zeros.valido());
        assertEquals("mensagem de sequência repetida", "CNPJ inválido (sequência de dígitos repetidos).",
                zeros.mensagem());

        ResultadoValidacao uns = validador.validar("11.111.111/1111-11");
        assertFalse("sequência de uns deveria ser rejeitada", uns.valido());
        assertEquals("mensagem de sequência repetida", "CNPJ inválido (sequência de dígitos repetidos).",
                uns.mensagem());
    }

    public void testCnpjVazioEhRejeitado() {
        assertFalse("CNPJ vazio não deveria passar", validador.validar("").valido());
    }
}
