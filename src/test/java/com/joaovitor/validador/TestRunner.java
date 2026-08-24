package com.joaovitor.validador;

import com.joaovitor.validador.tests.CnpjValidadorTest;
import com.joaovitor.validador.tests.CpfValidadorTest;
import com.joaovitor.validador.tests.DataBrValidadorTest;
import com.joaovitor.validador.tests.EmailValidadorTest;
import com.joaovitor.validador.tests.TelefoneValidadorTest;
import com.joaovitor.validador.tests.ValidacaoServiceTest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Runner de testes próprio, sem JUnit/Maven — ver a seção "Testes" do README para o
 * porquê. Cada classe em {@code com.joaovitor.validador.tests} é instanciada e todo
 * método público sem parâmetros começando com "test" é executado; uma falha vira um
 * {@link AssertionError} lançado pelo helper {@code com.joaovitor.validador.testing.Assert},
 * capturado aqui e reportado como FAIL sem interromper o restante da suíte.
 *
 * Uso: {@code ./test.sh} (compila e roda). Sai com código != 0 se houver qualquer falha,
 * para que CI (`.github/workflows/ci.yml`) marque o build como quebrado.
 */
public final class TestRunner {

    private static final List<Class<?>> SUITES = List.of(
            CpfValidadorTest.class,
            CnpjValidadorTest.class,
            EmailValidadorTest.class,
            DataBrValidadorTest.class,
            TelefoneValidadorTest.class,
            ValidacaoServiceTest.class);

    public static void main(String[] args) throws Exception {
        long inicio = System.currentTimeMillis();
        int total = 0;
        int falhas = 0;
        List<String> detalhesFalhas = new ArrayList<>();

        for (Class<?> suite : SUITES) {
            Object instancia = suite.getDeclaredConstructor().newInstance();
            List<Method> metodos = new ArrayList<>();
            for (Method metodo : suite.getDeclaredMethods()) {
                if (metodo.getName().startsWith("test") && metodo.getParameterCount() == 0) {
                    metodos.add(metodo);
                }
            }
            metodos.sort(Comparator.comparing(Method::getName));

            for (Method metodo : metodos) {
                total++;
                String nomeCompleto = suite.getSimpleName() + "." + metodo.getName();
                try {
                    metodo.setAccessible(true);
                    metodo.invoke(instancia);
                    System.out.println("PASS  " + nomeCompleto);
                } catch (InvocationTargetException e) {
                    falhas++;
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    System.out.println("FAIL  " + nomeCompleto + " -> " + causa.getMessage());
                    detalhesFalhas.add(nomeCompleto + ": " + causa.getMessage());
                } catch (ReflectiveOperationException e) {
                    falhas++;
                    System.out.println("ERRO  " + nomeCompleto + " -> " + e);
                    detalhesFalhas.add(nomeCompleto + ": " + e);
                }
            }
        }

        long duracaoMs = System.currentTimeMillis() - inicio;
        String linha = "=".repeat(60);

        System.out.println();
        System.out.println(linha);
        System.out.printf("Total: %d | Sucesso: %d | Falhas: %d | %dms%n", total, total - falhas, falhas, duracaoMs);
        System.out.println(linha);

        if (falhas > 0) {
            System.out.println();
            System.out.println("Falhas:");
            for (String detalhe : detalhesFalhas) {
                System.out.println("  - " + detalhe);
            }
            System.exit(1);
        }
    }
}
