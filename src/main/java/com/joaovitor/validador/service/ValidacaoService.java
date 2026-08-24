package com.joaovitor.validador.service;

import com.joaovitor.validador.enums.TipoDado;
import com.joaovitor.validador.model.HistoricoEntry;
import com.joaovitor.validador.model.ResultadoValidacao;
import com.joaovitor.validador.repository.HistoricoRepository;
import com.joaovitor.validador.validators.Validador;
import com.joaovitor.validador.validators.ValidadorFactory;
import java.util.List;
import java.util.Map;

public final class ValidacaoService {

    private final HistoricoRepository historico;

    public ValidacaoService(HistoricoRepository historico) {
        this.historico = historico;
    }

    /** @throws IllegalArgumentException se o tipo informado não existir */
    public ResultadoValidacao validarERegistrar(String tipoBruto, String valor) {
        TipoDado tipo = TipoDado.fromValor(tipoBruto);
        Validador validador = ValidadorFactory.criar(tipo);
        ResultadoValidacao resultado = validador.validar(valor);

        historico.registrar(resultado);

        return resultado;
    }

    public List<HistoricoEntry> historicoRecente(int limite) {
        return historico.listarUltimos(limite);
    }

    public Map<String, Integer> estatisticas() {
        return historico.estatisticasPorTipo();
    }
}
