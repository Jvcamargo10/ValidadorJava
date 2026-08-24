package com.joaovitor.validador.validators;

import com.joaovitor.validador.enums.TipoDado;
import com.joaovitor.validador.model.ResultadoValidacao;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

/**
 * Mesma regra que ValidadorPHP\App\Services\Validators\DataBrValidador: valida formato
 * dd/mm/aaaa E a existência real da data (31/02 é rejeitado), não só a máscara.
 */
public final class DataBrValidador implements Validador {

    private static final Pattern REGEX_FORMATO = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");
    private static final DateTimeFormatter FORMATO_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    @Override
    public ResultadoValidacao validar(String valorBruto) {
        String valor = valorBruto.trim();

        if (!REGEX_FORMATO.matcher(valor).matches()) {
            return new ResultadoValidacao(TipoDado.DATA, valor, false, null, "Data deve estar no formato dd/mm/aaaa.");
        }

        try {
            LocalDate data = LocalDate.parse(valor, FORMATO_BR);
            return new ResultadoValidacao(TipoDado.DATA, valor, true, data.toString(), "Data válida.");
        } catch (DateTimeParseException e) {
            return new ResultadoValidacao(TipoDado.DATA, valor, false, null, "Data inexistente no calendário.");
        }
    }
}
