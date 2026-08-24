package com.joaovitor.validador.handlers;

import com.joaovitor.validador.core.HttpUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class RootHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Map<String, Object> corpo = Map.of(
                "api", "Validador de Dados (Java)",
                "versao", "1.0",
                "descricao", "Validação e padronização de CPF, CNPJ, e-mail, data e telefone — mesmo contrato usado pela versão em PHP.",
                "endpoints", List.of(
                        "POST /api/validar      { \"tipo\": \"cpf|cnpj|email|data|telefone\", \"valor\": \"...\" }",
                        "GET  /api/historico    últimas validações registradas",
                        "GET  /api/estatisticas contagem de validações por tipo"));

        HttpUtil.responderJson(exchange, 200, corpo);
    }
}
