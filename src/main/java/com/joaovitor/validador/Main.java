package com.joaovitor.validador;

import com.joaovitor.validador.core.HttpUtil;
import com.joaovitor.validador.handlers.EstatisticasHandler;
import com.joaovitor.validador.handlers.HistoricoHandler;
import com.joaovitor.validador.handlers.RootHandler;
import com.joaovitor.validador.handlers.ValidarHandler;
import com.joaovitor.validador.repository.HistoricoRepository;
import com.joaovitor.validador.service.ValidacaoService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP com com.sun.net.httpserver (parte do JDK, sem dependências externas —
 * Maven Central não estava acessível no ambiente onde este projeto foi construído).
 */
public final class Main {

    public static void main(String[] args) throws IOException {
        int porta = args.length > 0 ? Integer.parseInt(args[0]) : 8080;

        HistoricoRepository repositorio = new HistoricoRepository(Path.of("data", "historico.log"));
        ValidacaoService service = new ValidacaoService(repositorio);

        HttpServer servidor = HttpServer.create(new InetSocketAddress(porta), 0);
        servidor.createContext("/", HttpUtil.comCors(new RootHandler()));
        servidor.createContext("/api/validar", HttpUtil.comCors(new ValidarHandler(service)));
        servidor.createContext("/api/historico", HttpUtil.comCors(new HistoricoHandler(service)));
        servidor.createContext("/api/estatisticas", HttpUtil.comCors(new EstatisticasHandler(service)));
        servidor.setExecutor(Executors.newVirtualThreadPerTaskExecutor());

        servidor.start();
        System.out.println("Validador de Dados (Java) rodando em http://localhost:" + porta);
    }
}
