package com.joaovitor.validador.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser JSON recursivo-descendente minimalista (sem dependências externas) — o
 * suficiente para os corpos de requisição desta API (objetos simples de string/
 * boolean/número). Não é um parser JSON completo de propósito geral.
 */
public final class JsonReader {

    private final String texto;
    private int pos;

    private JsonReader(String texto) {
        this.texto = texto;
        this.pos = 0;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> lerObjeto(String json) {
        JsonReader leitor = new JsonReader(json == null ? "{}" : json);
        leitor.pularEspacos();
        if (leitor.pos >= leitor.texto.length()) {
            return new LinkedHashMap<>();
        }
        Object valor = leitor.lerValor();
        return valor instanceof Map ? (Map<String, Object>) valor : new LinkedHashMap<>();
    }

    private Object lerValor() {
        pularEspacos();
        char c = texto.charAt(pos);
        return switch (c) {
            case '{' -> lerObjetoInterno();
            case '[' -> lerArray();
            case '"' -> lerString();
            case 't', 'f' -> lerBooleano();
            case 'n' -> {
                pos += 4; // "null"
                yield null;
            }
            default -> lerNumero();
        };
    }

    private Map<String, Object> lerObjetoInterno() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        pos++; // {
        pularEspacos();
        if (texto.charAt(pos) == '}') {
            pos++;
            return mapa;
        }
        while (true) {
            pularEspacos();
            String chave = lerString();
            pularEspacos();
            pos++; // :
            Object valor = lerValor();
            mapa.put(chave, valor);
            pularEspacos();
            char c = texto.charAt(pos);
            pos++;
            if (c == '}') break;
        }
        return mapa;
    }

    private List<Object> lerArray() {
        List<Object> lista = new ArrayList<>();
        pos++; // [
        pularEspacos();
        if (texto.charAt(pos) == ']') {
            pos++;
            return lista;
        }
        while (true) {
            lista.add(lerValor());
            pularEspacos();
            char c = texto.charAt(pos);
            pos++;
            if (c == ']') break;
        }
        return lista;
    }

    private String lerString() {
        pos++; // "
        StringBuilder sb = new StringBuilder();
        while (texto.charAt(pos) != '"') {
            char c = texto.charAt(pos);
            if (c == '\\') {
                pos++;
                char escapado = texto.charAt(pos);
                switch (escapado) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'u' -> {
                        String hex = texto.substring(pos + 1, pos + 5);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                    }
                    default -> sb.append(escapado);
                }
            } else {
                sb.append(c);
            }
            pos++;
        }
        pos++; // "
        return sb.toString();
    }

    private Boolean lerBooleano() {
        if (texto.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        }
        pos += 5; // false
        return Boolean.FALSE;
    }

    private Object lerNumero() {
        int inicio = pos;
        while (pos < texto.length() && "-+.eE0123456789".indexOf(texto.charAt(pos)) >= 0) {
            pos++;
        }
        String num = texto.substring(inicio, pos);
        return num.contains(".") || num.contains("e") || num.contains("E")
                ? Double.parseDouble(num)
                : Long.parseLong(num);
    }

    private void pularEspacos() {
        while (pos < texto.length() && Character.isWhitespace(texto.charAt(pos))) {
            pos++;
        }
    }
}
