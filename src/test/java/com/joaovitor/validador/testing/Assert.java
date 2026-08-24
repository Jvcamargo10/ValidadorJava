package com.joaovitor.validador.testing;

import java.util.Objects;

/**
 * Helper de asserção mínimo, escrito à mão pelo mesmo motivo documentado no README
 * ("## Testes"): o ambiente onde este projeto foi construído não tem acesso ao Maven
 * Central, então JUnit (e qualquer outra dependência externa) está fora de alcance.
 * Cada método lança {@link AssertionError} com uma mensagem clara em caso de falha —
 * é isso que o {@code com.joaovitor.validador.TestRunner} captura por teste.
 */
public final class Assert {

    private Assert() {
    }

    public static void assertTrue(String mensagem, boolean condicao) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    public static void assertFalse(String mensagem, boolean condicao) {
        assertTrue(mensagem, !condicao);
    }

    public static void assertEquals(String mensagem, Object esperado, Object atual) {
        if (!Objects.equals(esperado, atual)) {
            throw new AssertionError("%s — esperado: <%s> mas foi: <%s>".formatted(mensagem, esperado, atual));
        }
    }

    public static void assertNull(String mensagem, Object atual) {
        assertTrue(mensagem + " — esperado null mas foi: <" + atual + ">", atual == null);
    }

    public static void assertNotNull(String mensagem, Object atual) {
        assertTrue(mensagem, atual != null);
    }

    public static void fail(String mensagem) {
        throw new AssertionError(mensagem);
    }
}
