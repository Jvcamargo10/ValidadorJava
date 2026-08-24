package com.joaovitor.validador.tests;

import static com.joaovitor.validador.testing.Assert.assertEquals;
import static com.joaovitor.validador.testing.Assert.assertFalse;
import static com.joaovitor.validador.testing.Assert.assertTrue;

import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.validators.TelefoneValidador;

/**
 * Ver docs/REGRAS-VALIDACAO.md, seção 5: DDD + 8 dígitos (fixo) ou DDD + 9 dígitos
 * (celular), com ou sem máscara.
 */
public final class TelefoneValidadorTest {

    private final TelefoneValidador validador = new TelefoneValidador();

    public void testFixoSemMascaraEhAceito() {
        ResultadoValidacao r = validador.validar("1122223333");
        assertTrue("fixo com DDD + 8 dígitos deveria passar: " + r.mensagem(), r.valido());
        assertEquals("máscara de fixo", "(11) 2222-3333", r.valorFormatado());
    }

    public void testFixoComMascaraEhAceito() {
        ResultadoValidacao r = validador.validar("(11) 2222-3333");
        assertTrue("fixo mascarado deveria passar: " + r.mensagem(), r.valido());
        assertEquals("máscara de fixo", "(11) 2222-3333", r.valorFormatado());
    }

    public void testCelularSemMascaraEhAceito() {
        ResultadoValidacao r = validador.validar("11912345678");
        assertTrue("celular com DDD + 9 dígitos deveria passar: " + r.mensagem(), r.valido());
        assertEquals("máscara de celular", "(11) 91234-5678", r.valorFormatado());
    }

    public void testCelularComMascaraEhAceito() {
        ResultadoValidacao r = validador.validar("(11) 91234-5678");
        assertTrue("celular mascarado deveria passar: " + r.mensagem(), r.valido());
        assertEquals("máscara de celular", "(11) 91234-5678", r.valorFormatado());
    }

    public void testTelefoneCurtoDemaisEhRejeitado() {
        ResultadoValidacao r = validador.validar("123456789");
        assertFalse("9 dígitos totais é curto demais (nem DDD+8 nem DDD+9)", r.valido());
        assertEquals("mensagem de tamanho inválido", "Telefone deve ter DDD + 8 ou 9 dígitos.", r.mensagem());
    }

    public void testTelefoneLongoDemaisEhRejeitado() {
        ResultadoValidacao r = validador.validar("119123456789");
        assertFalse("12 dígitos totais é longo demais", r.valido());
    }

    public void testTelefoneComLetrasSoContaDigitos() {
        ResultadoValidacao r = validador.validar("abc");
        assertFalse("sem dígitos suficientes não deveria passar", r.valido());
    }
}
