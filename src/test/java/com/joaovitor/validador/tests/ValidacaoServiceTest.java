package com.joaovitor.validador.tests;

import static com.joaovitor.validador.testing.Assert.assertEquals;
import static com.joaovitor.validador.testing.Assert.assertTrue;
import static com.joaovitor.validador.testing.Assert.fail;

import com.joaovitor.validador.enums.TipoDado;
import com.joaovitor.validador.model.HistoricoEntry;
import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.repository.HistoricoRepository;
import com.joaovitor.validador.service.ValidacaoService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Cobre a integração entre {@link ValidacaoService} e {@link HistoricoRepository} —
 * validar e registrar, listar histórico recente, contar por tipo — usando um arquivo
 * temporário real (Files.createTempFile), não um mock.
 */
public final class ValidacaoServiceTest {

    public void testValidarERegistrarGravaNoHistorico() throws IOException {
        Path arquivo = arquivoTemporario();
        ValidacaoService service = new ValidacaoService(new HistoricoRepository(arquivo));

        ResultadoValidacao r = service.validarERegistrar("cpf", "111.444.777-35");
        assertTrue("CPF válido deveria passar pelo serviço", r.valido());

        List<HistoricoEntry> historico = service.historicoRecente(10);
        assertEquals("deveria haver exatamente 1 entrada no histórico", 1, historico.size());
        assertEquals("tipo registrado deve ser cpf", "cpf", historico.get(0).tipo());

        Files.deleteIfExists(arquivo);
    }

    public void testEstatisticasContamPorTipo() throws IOException {
        Path arquivo = arquivoTemporario();
        ValidacaoService service = new ValidacaoService(new HistoricoRepository(arquivo));

        service.validarERegistrar("cpf", "111.444.777-35");
        service.validarERegistrar("cpf", "000.000.000-00"); // inválido, mas ainda é contado
        service.validarERegistrar("email", "usuario@dominio.com");

        Map<String, Integer> stats = service.estatisticas();
        assertEquals("2 validações de cpf (válida + inválida)", Integer.valueOf(2), stats.get("cpf"));
        assertEquals("1 validação de email", Integer.valueOf(1), stats.get("email"));

        Files.deleteIfExists(arquivo);
    }

    public void testHistoricoRecenteRespeitaLimiteEOrdemMaisRecentePrimeiro() throws IOException {
        Path arquivo = arquivoTemporario();
        ValidacaoService service = new ValidacaoService(new HistoricoRepository(arquivo));

        service.validarERegistrar("cpf", "111.444.777-35");
        service.validarERegistrar("email", "usuario@dominio.com");
        service.validarERegistrar("telefone", "11912345678");

        List<HistoricoEntry> ultimos2 = service.historicoRecente(2);
        assertEquals("limite deveria retornar só 2 entradas", 2, ultimos2.size());
        assertEquals("mais recente primeiro", "telefone", ultimos2.get(0).tipo());
        assertEquals("segunda mais recente", "email", ultimos2.get(1).tipo());

        Files.deleteIfExists(arquivo);
    }

    public void testTipoDesconhecidoLancaExcecao() throws IOException {
        Path arquivo = arquivoTemporario();
        ValidacaoService service = new ValidacaoService(new HistoricoRepository(arquivo));

        try {
            service.validarERegistrar("rg", "12345678");
            fail("tipo 'rg' não existe em TipoDado e deveria lançar IllegalArgumentException");
        } catch (IllegalArgumentException esperado) {
            // ok
        }

        Files.deleteIfExists(arquivo);
    }

    public void testTipoDadoFromValorEhCaseInsensitive() {
        assertEquals("fromValor deve aceitar maiúsculas", TipoDado.CPF, TipoDado.fromValor("CPF"));
        assertEquals("fromValor deve aceitar minúsculas", TipoDado.EMAIL, TipoDado.fromValor("email"));
    }

    private Path arquivoTemporario() throws IOException {
        Path arquivo = Files.createTempFile("validador-java-test-historico-", ".log");
        Files.deleteIfExists(arquivo); // HistoricoRepository cria o arquivo se não existir
        return arquivo;
    }
}
