package com.joaovitor.validador.handlers;

import com.joaovitor.validador.core.HttpUtil;
import com.joaovitor.validador.model.HistoricoEntry;
import com.joaovitor.validador.service.ValidacaoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class HistoricoHandler implements HttpHandler {

    private final ValidacaoService service;

    public HistoricoHandler(ValidacaoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, String> params = HttpUtil.queryParams(exchange);
        int limite = 50;
        try {
            limite = Integer.parseInt(params.getOrDefault("limite", "50"));
        } catch (NumberFormatException ignored) {
            // mantém o padrão
        }

        List<Map<String, Object>> itens = service.historicoRecente(limite).stream()
                .map(HistoricoEntry::paraMapa)
                .toList();

        HttpUtil.responderJson(exchange, 200, Map.of("itens", itens));
    }
}
