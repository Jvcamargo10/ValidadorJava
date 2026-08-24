package com.joaovitor.validador.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpUtil {

    private HttpUtil() {
    }

    public static String lerCorpo(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    public static void responderJson(HttpExchange exchange, int status, Object corpo) throws IOException {
        byte[] bytes = JsonWriter.escrever(corpo).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Envolve um handler adicionando cabeçalhos CORS liberados para qualquer origem —
     * só para permitir que o front-end estático (docs/index.html, publicado no GitHub
     * Pages ou aberto localmente) chame esta API rodando em localhost. Também responde
     * ao preflight (OPTIONS) direto, sem repassar ao handler real.
     */
    public static HttpHandler comCors(HttpHandler handlerReal) {
        return exchange -> {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            handlerReal.handle(exchange);
        };
    }

    public static Map<String, String> queryParams(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        Map<String, String> params = new java.util.LinkedHashMap<>();
        if (query == null || query.isBlank()) return params;
        for (String par : query.split("&")) {
            String[] kv = par.split("=", 2);
            params.put(kv[0], kv.length > 1 ? kv[1] : "");
        }
        return params;
    }
}
