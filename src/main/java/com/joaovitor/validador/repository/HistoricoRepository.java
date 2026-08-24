package com.joaovitor.validador.repository;

import com.joaovitor.validador.model.HistoricoEntry;
import com.joaovitor.validador.model.ResultadoValidacao;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Persistência própria em arquivo texto (append-only), protegida por lock — o
 * equivalente funcional ao HistoricoRepository (PDO + SQLite) da versão em PHP, mas
 * sem depender de um driver JDBC externo (indisponível offline neste ambiente).
 */
public final class HistoricoRepository {

    private final Path arquivo;
    private final List<HistoricoEntry> emMemoria = Collections.synchronizedList(new ArrayList<>());
    private final ReentrantLock lock = new ReentrantLock();

    public HistoricoRepository(Path arquivo) throws IOException {
        this.arquivo = arquivo;
        Files.createDirectories(arquivo.getParent());
        if (!Files.exists(arquivo)) {
            Files.createFile(arquivo);
        }
        carregar();
    }

    private void carregar() throws IOException {
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            if (!linha.isBlank()) {
                emMemoria.add(HistoricoEntry.deLinha(linha));
            }
        }
    }

    public void registrar(ResultadoValidacao resultado) {
        HistoricoEntry entrada = new HistoricoEntry(
                resultado.tipo().valor(),
                resultado.valorOriginal(),
                resultado.valido(),
                resultado.valorFormatado(),
                resultado.mensagem(),
                Instant.now());

        emMemoria.add(entrada);

        lock.lock();
        try {
            Files.writeString(arquivo, entrada.paraLinha() + System.lineSeparator(),
                    StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOExceptionLocal(e);
        } finally {
            lock.unlock();
        }
    }

    public List<HistoricoEntry> listarUltimos(int limite) {
        synchronized (emMemoria) {
            int total = emMemoria.size();
            int inicio = Math.max(0, total - limite);
            List<HistoricoEntry> ultimos = new ArrayList<>(emMemoria.subList(inicio, total));
            Collections.reverse(ultimos);
            return ultimos;
        }
    }

    public Map<String, Integer> estatisticasPorTipo() {
        Map<String, Integer> contagem = new TreeMap<>();
        synchronized (emMemoria) {
            for (HistoricoEntry entrada : emMemoria) {
                contagem.merge(entrada.tipo(), 1, Integer::sum);
            }
        }
        return contagem;
    }

    private static final class UncheckedIOExceptionLocal extends RuntimeException {
        UncheckedIOExceptionLocal(IOException cause) {
            super(cause);
        }
    }
}
