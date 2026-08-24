package com.joaovitor.validador.tests;

import static com.joaovitor.validador.testing.Assert.assertEquals;
import static com.joaovitor.validador.testing.Assert.assertFalse;
import static com.joaovitor.validador.testing.Assert.assertTrue;

import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.validators.EmailValidador;

/** Ver docs/REGRAS-VALIDACAO.md, seção 3: regex + normalização (minúsculas, sem espaços). */
public final class EmailValidadorTest {

    private final EmailValidador validador = new EmailValidador();

    public void testEmailValidoEhAceito() {
        ResultadoValidacao r = validador.validar("usuario@dominio.com");
        assertTrue("e-mail simples válido deveria passar: " + r.mensagem(), r.valido());
        assertEquals("normalização não deveria alterar um e-mail já em minúsculas",
                "usuario@dominio.com", r.valorFormatado());
    }

    public void testEmailComSubdominioEhAceito() {
        assertTrue("e-mail com domínio .com.br deveria passar",
                validador.validar("contato@empresa.com.br").valido());
    }

    public void testNormalizacaoParaMinusculasESemEspacos() {
        ResultadoValidacao r = validador.validar("  Usuario@Dominio.COM  ");
        assertTrue("deveria ser válido após trim", r.valido());
        assertEquals("deve normalizar para minúsculas e sem espaços nas pontas",
                "usuario@dominio.com", r.valorFormatado());
    }

    public void testEmailSemArrobaEhRejeitado() {
        assertFalse("e-mail sem @ não deveria passar", validador.validar("usuario.dominio.com").valido());
    }

    public void testEmailSemDominioEhRejeitado() {
        assertFalse("e-mail sem TLD (sem ponto após o domínio) não deveria passar",
                validador.validar("usuario@dominio").valido());
    }

    public void testEmailComTldDeUmaLetraEhRejeitado() {
        assertFalse("TLD precisa de ao menos 2 letras", validador.validar("usuario@dominio.c").valido());
    }

    public void testEmailComParteLocalVaziaEhRejeitado() {
        assertFalse("e-mail sem parte local não deveria passar", validador.validar("@dominio.com").valido());
    }

    public void testEmailComArrobaDuplicadoEhRejeitado() {
        assertFalse("e-mail com @ duplicado não deveria passar",
                validador.validar("usuario@@dominio.com").valido());
    }

    public void testMensagemDeErroParaEmailInvalido() {
        ResultadoValidacao r = validador.validar("nao-e-email");
        assertFalse("deveria ser inválido", r.valido());
        assertEquals("mensagem de formato inválido", "E-mail em formato inválido.", r.mensagem());
    }
}
