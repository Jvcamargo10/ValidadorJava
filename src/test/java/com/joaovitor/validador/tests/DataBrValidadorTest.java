package com.joaovitor.validador.tests;

import static com.joaovitor.validador.testing.Assert.assertEquals;
import static com.joaovitor.validador.testing.Assert.assertFalse;
import static com.joaovitor.validador.testing.Assert.assertTrue;

import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.validators.DataBrValidador;

/**
 * Ver docs/REGRAS-VALIDACAO.md, seção 4: formato dd/mm/aaaa validado com
 * {@code java.time} + {@link java.time.format.ResolverStyle#STRICT}, que rejeita datas
 * que não existem no calendário (não só datas fora do formato).
 */
public final class DataBrValidadorTest {

    private final DataBrValidador validador = new DataBrValidador();

    public void testDataValidaEhAceitaEConvertidaParaIso() {
        ResultadoValidacao r = validador.validar("25/12/2026");
        assertTrue("data válida deveria passar: " + r.mensagem(), r.valido());
        assertEquals("saída deve ser ISO-8601", "2026-12-25", r.valorFormatado());
    }

    public void testAnoBissextoAceita29DeFevereiro() {
        ResultadoValidacao r = validador.validar("29/02/2024");
        assertTrue("2024 é bissexto, 29/02 deveria ser válido: " + r.mensagem(), r.valido());
        assertEquals("saída deve ser ISO-8601", "2024-02-29", r.valorFormatado());
    }

    public void testAnoNaoBissextoRejeita29DeFevereiro() {
        ResultadoValidacao r = validador.validar("29/02/2026");
        assertFalse("2026 não é bissexto, 29/02 não deveria existir", r.valido());
        assertEquals("mensagem de data inexistente", "Data inexistente no calendário.", r.mensagem());
    }

    public void testDia31DeFevereiroEhRejeitado() {
        ResultadoValidacao r = validador.validar("31/02/2026");
        assertFalse("31/02 não existe em nenhum ano", r.valido());
        assertEquals("mensagem de data inexistente", "Data inexistente no calendário.", r.mensagem());
    }

    public void testDia31DeAbrilEhRejeitado() {
        // abril tem 30 dias — STRICT não deve "rolar" para maio.
        ResultadoValidacao r = validador.validar("31/04/2026");
        assertFalse("abril não tem dia 31", r.valido());
    }

    public void testFormatoIsoEhRejeitado() {
        ResultadoValidacao r = validador.validar("2026-12-25");
        assertFalse("formato ISO não deveria ser aceito, só dd/mm/aaaa", r.valido());
        assertEquals("mensagem de formato inválido", "Data deve estar no formato dd/mm/aaaa.", r.mensagem());
    }

    public void testFormatoSemZerosAEsquerdaEhRejeitado() {
        ResultadoValidacao r = validador.validar("1/1/2026");
        assertFalse("dia/mês sem zero à esquerda não bate com o formato exigido", r.valido());
    }

    public void testDiaZeroEhRejeitado() {
        ResultadoValidacao r = validador.validar("00/01/2026");
        assertFalse("dia 00 não existe", r.valido());
    }

    public void testDiaAcimaDe31EhRejeitado() {
        ResultadoValidacao r = validador.validar("32/01/2026");
        assertFalse("dia 32 não existe", r.valido());
    }

    public void testMesAcimaDe12EhRejeitado() {
        ResultadoValidacao r = validador.validar("15/13/2026");
        assertFalse("mês 13 não existe", r.valido());
    }
}
