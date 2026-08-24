package com.joaovitor.validador.core;

import java.util.List;
import java.util.Map;

/**
 * Serializador JSON "de saída apenas" (write-only), escrito à mão porque não há acesso
 * a repositórios de pacotes (Maven Central) neste ambiente para trazer uma lib como
 * Jackson/Gson. Suficiente para o formato de respostas desta API (mapas e listas de
 * mapas com String, boolean, Number ou null).
 */
public final class JsonWriter {

    private JsonWriter() {
    }

    public static String escrever(Object valor) {
        StringBuilder sb = new StringBuilder();
        escreverValor(valor, sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static void escreverValor(Object valor, StringBuilder sb) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof String s) {
            escreverString(s, sb);
        } else if (valor instanceof Boolean || valor instanceof Number) {
            sb.append(valor);
        } else if (valor instanceof Map<?, ?> mapa) {
            escreverMapa((Map<String, Object>) mapa, sb);
        } else if (valor instanceof List<?> lista) {
            escreverLista(lista, sb);
        } else {
            escreverString(valor.toString(), sb);
        }
    }

    private static void escreverMapa(Map<String, Object> mapa, StringBuilder sb) {
        sb.append('{');
        boolean primeiro = true;
        for (Map.Entry<String, Object> entrada : mapa.entrySet()) {
            if (!primeiro) sb.append(',');
            primeiro = false;
            escreverString(entrada.getKey(), sb);
            sb.append(':');
            escreverValor(entrada.getValue(), sb);
        }
        sb.append('}');
    }

    private static void escreverLista(List<?> lista, StringBuilder sb) {
        sb.append('[');
        boolean primeiro = true;
        for (Object item : lista) {
            if (!primeiro) sb.append(',');
            primeiro = false;
            escreverValor(item, sb);
        }
        sb.append(']');
    }

    private static void escreverString(String valor, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }
}
