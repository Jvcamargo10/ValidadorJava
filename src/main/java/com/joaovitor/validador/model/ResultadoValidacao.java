package com.joaovitor.validador.model;

import com.joaovitor.validador.enums.TipoDado;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Record (Java 16+) — equivalente à classe readonly usada em PHP para o mesmo
 * conceito (ValidadorPHP\App\Models\ResultadoValidacao).
 */
public record ResultadoValidacao(
        TipoDado tipo,
        String valorOriginal,
        boolean valido,
        String valorFormatado,
        String mensagem) {

    public Map<String, Object> paraMapa() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("tipo", tipo.valor());
        mapa.put("valorOriginal", valorOriginal);
        mapa.put("valido", valido);
        mapa.put("valorFormatado", valorFormatado);
        mapa.put("mensagem", mensagem);
        return mapa;
    }
}
