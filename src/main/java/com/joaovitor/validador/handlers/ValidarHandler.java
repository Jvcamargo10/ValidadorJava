package com.joaovitor.validador.handlers;

import com.joaovitor.validador.core.HttpUtil;
import com.joaovitor.validador.core.JsonReader;
import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.service.ValidacaoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.Map;

public final class ValidarHandler implements HttpHandler {

    private final ValidacaoService service;

    public ValidarHandler(ValidacaoService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            HttpUtil.responderJson(exchange, 405, Map.of("erro", "Método não suportado, use POST."));
            return;
        }

        Map<String, Object> corpo = JsonReader.lerObjeto(HttpUtil.lerCorpo(exchange));
        String tipo = String.valueOf(corpo.getOrDefault("tipo", ""));
        String valor = String.valueOf(corpo.getOrDefault("valor", ""));

        if (tipo.isBlank() || valor.isBlank()) {
            HttpUtil.responderJson(exchange, 400, Map.of("erro", "Campos \"tipo\" e \"valor\" são obrigatórios."));
            return;
        }

        try {
            ResultadoValidacao resultado = service.validarERegistrar(tipo, valor);
            HttpUtil.responderJson(exchange, 200, resultado.paraMapa());
        } catch (IllegalArgumentException e) {
            HttpUtil.responderJson(exchange, 400,
                    Map.of("erro", "Tipo '" + tipo + "' não suportado. Use: cpf, cnpj, email, data, telefone."));
        }
    }
}
