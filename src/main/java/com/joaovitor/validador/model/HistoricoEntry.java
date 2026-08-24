package com.joaovitor.validador.model;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public record HistoricoEntry(
        String tipo,
        String valorOriginal,
        boolean valido,
        String valorFormatado,
        String mensagem,
        Instant criadoEm) {

    // Caractere de controle "Unit Separator" (código 31) — praticamente nunca aparece
    // em entrada de usuário normal, então serve como delimitador seguro para o formato
    // de persistência interno em arquivo, sem precisar de um parser JSON completo.
    private static final char DELIM_CHAR = (char) 31;
    private static final String DELIM = String.valueOf(DELIM_CHAR);

    public Map<String, Object> paraMapa() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("tipo", tipo);
        mapa.put("valorOriginal", valorOriginal);
        mapa.put("valido", valido);
        mapa.put("valorFormatado", valorFormatado);
        mapa.put("mensagem", mensagem);
        mapa.put("criadoEm", criadoEm.toString());
        return mapa;
    }

    /** Serializa como uma linha de texto simples para persistência em arquivo (ver HistoricoRepository). */
    public String paraLinha() {
        return String.join(DELIM,
                tipo,
                escapar(valorOriginal),
                Boolean.toString(valido),
                escapar(valorFormatado == null ? "" : valorFormatado),
                escapar(mensagem),
                criadoEm.toString());
    }

    public static HistoricoEntry deLinha(String linha) {
        String[] partes = linha.split(DELIM, -1);
        return new HistoricoEntry(
                partes[0],
                desescapar(partes[1]),
                Boolean.parseBoolean(partes[2]),
                partes[3].isEmpty() ? null : desescapar(partes[3]),
                desescapar(partes[4]),
                Instant.parse(partes[5]));
    }

    private static String escapar(String valor) {
        return valor.replace("\n", "\\n");
    }

    private static String desescapar(String valor) {
        return valor.replace("\\n", "\n");
    }
}
