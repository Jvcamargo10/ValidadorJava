package com.joaovitor.validador.handlers;

import com.joaovitor.validador.core.HttpUtil;
import com.joaovitor.validador.service.ValidacaoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;

public final class EstatisticasHandler implements HttpHandler {

    private final ValidacaoService service;

    public EstatisticasHandler(ValidacaoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpUtil.responderJson(exchange, 200, Map.of("porTipo", service.estatisticas()));
    }
}
