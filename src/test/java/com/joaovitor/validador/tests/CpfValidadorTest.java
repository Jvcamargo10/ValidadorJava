package com.joaovitor.validador.tests;

import static com.joaovitor.validador.testing.Assert.assertEquals;
import static com.joaovitor.validador.testing.Assert.assertFalse;
import static com.joaovitor.validador.testing.Assert.assertTrue;

import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.validators.CpfValidador;

/**
 * Casos conferidos com a regra do contrato compartilhado (docs/REGRAS-VALIDACAO.md, seção 1):
 * módulo 11, rejeição de sequência repetida mesmo quando o cálculo do dígito "acidentalmente"
 * bateria, aceitação com e sem máscara.
 */
public final class CpfValidadorTest {

    private final CpfValidador validador = new CpfValidador();

    public void testCpfValidoSemMascaraEAceito() {
        ResultadoValidacao r = validador.validar("11144477735");
        assertTrue("CPF válido sem máscara deveria passar: " + r.mensagem(), r.valido());
        assertEquals("valorFormatado deve usar a máscara padrão", "111.444.777-35", r.valorFormatado());
    }

    public void testCpfValidoComMascaraEAceito() {
        ResultadoValidacao r = validador.validar("111.444.777-35");
        assertTrue("CPF válido com máscara deveria passar: " + r.mensagem(), r.valido());
        assertEquals("valorFormatado deve usar a máscara padrão", "111.444.777-35", r.valorFormatado());
    }

    public void testOutroCpfValidoConhecido() {
        assertTrue("123.456.789-09 é um CPF válido conhecido", validador.validar("123.456.789-09").valido());
        assertTrue("529.982.247-25 é um CPF válido conhecido", validador.validar("52998224725").valido());
    }

    public void testCpfComDigitoVerificadorErradoEhRejeitado() {
        // 111.444.777-35 é válido; trocar o último dígito quebra só o DV, não o tamanho.
        ResultadoValidacao r = validador.validar("111.444.777-36");
        assertFalse("CPF com DV incorreto não deveria passar", r.valido());
        assertEquals("mensagem de DV inválido", "CPF inválido (dígito verificador não confere).", r.mensagem());
    }

    public void testCpfComTamanhoErradoEhRejeitado() {
        ResultadoValidacao curto = validador.validar("1234567890");
        assertFalse("CPF com 10 dígitos não deveria passar", curto.valido());
        assertEquals("mensagem de tamanho inválido", "CPF deve conter 11 dígitos.", curto.mensagem());

        ResultadoValidacao longo = validador.validar("123456789012");
        assertFalse("CPF com 12 dígitos não deveria passar", longo.valido());
    }

    public void testSequenciaRepetidaEhRejeitadaMesmoQuePassasseNoModulo11() {
        // Regra explícita do contrato: 000.000.000-00 e 111.111.111-11 têm DV que "bate"
        // matematicamente no módulo 11, mas devem ser rejeitados mesmo assim.
        ResultadoValidacao zeros = validador.validar("000.000.000-00");
        assertFalse("sequência de zeros deveria ser rejeitada", zeros.valido());
        assertEquals("mensagem de sequência repetida", "CPF inválido (sequência de dígitos repetidos).",
                zeros.mensagem());

        ResultadoValidacao uns = validador.validar("111.111.111-11");
        assertFalse("sequência de uns deveria ser rejeitada", uns.valido());
        assertEquals("mensagem de sequência repetida", "CPF inválido (sequência de dígitos repetidos).",
                uns.mensagem());
    }

    public void testCpfVazioEhRejeitado() {
        assertFalse("CPF vazio não deveria passar", validador.validar("").valido());
    }
}
