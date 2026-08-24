# Validador de Dados — Java

Serviço HTTP em **Java 21 puro** (sem Spring, sem Maven/Gradle) que valida e padroniza CPF, CNPJ, e-mail, data e telefone com **exatamente as mesmas regras** da versão em PHP ([ValidadorPHP](https://github.com/Jvcamargo10/ValidadorPHP)). Ver o contrato compartilhado entre as duas em [`docs/REGRAS-VALIDACAO.md`](docs/REGRAS-VALIDACAO.md) (troque o link acima pelo do seu repositório depois de subir os dois).

## Por que este projeto existe

Na Prefeitura de Osasco, padronizei validação de datas via expressões regulares entre PHP, Java, JavaScript/TypeScript e C# — o objetivo era garantir que sistemas diferentes concordassem sobre o que é um dado válido. Este par de projetos (PHP + Java) formaliza exatamente essa ideia num exemplo verificável: rodei a mesma bateria de testes (`curl`) contra as duas APIs e os resultados batem campo a campo, inclusive nas mensagens de erro.

## Por que sem Spring Boot/Maven

O ambiente onde este projeto foi construído não tinha acesso ao Maven Central, então qualquer dependência externa (Spring, Jackson, driver JDBC de SQLite) ficaria fora do alcance. Em vez de simplificar o escopo, resolvi construir com o que o próprio JDK já oferece:

- **HTTP**: `com.sun.net.httpserver.HttpServer`, embutido no JDK desde o Java 6.
- **JSON**: um parser/serializador pequeno, escrito à mão (`core/JsonReader.java`, `core/JsonWriter.java`) — o suficiente para o formato desta API.
- **Persistência**: arquivo texto próprio, append-only, com um formato de linha delimitado (equivalente funcional ao SQLite usado na versão PHP).
- **Concorrência**: `Executors.newVirtualThreadPerTaskExecutor()` (virtual threads do Java 21) para o servidor HTTP.

Como conceito de arquitetura, é o mesmo raciocínio usado na API em C#/.NET do mesmo portfólio ([ContratosApi](https://github.com/Jvcamargo10/ContratosApi)): quando uma peça de infraestrutura não está disponível, a solução foi implementar a peça mínima necessária diretamente, documentando a decisão — não abrir mão da funcionalidade.

## Como rodar

Requer JDK ≥ 21.

```bash
./build.sh      # compila com javac (sem dependências)
./run.sh 8080   # roda o servidor na porta 8080
```

## Testes

Mesmo motivo do "Por que sem Spring Boot/Maven" acima: sem acesso ao Maven Central, JUnit
também está fora de alcance. Em vez de deixar o projeto sem testes automatizados, resolvi
implementar a peça mínima necessária — um harness de asserção próprio, em Java puro, sem
nenhuma dependência externa:

- `src/test/java/com/joaovitor/validador/testing/Assert.java` — `assertTrue`/`assertFalse`/`assertEquals`/`fail`,
  cada um lançando `AssertionError` com mensagem clara em caso de falha.
- `src/test/java/com/joaovitor/validador/tests/` — uma classe de teste por validador
  (`CpfValidadorTest`, `CnpjValidadorTest`, `EmailValidadorTest`, `DataBrValidadorTest`,
  `TelefoneValidadorTest`) mais `ValidacaoServiceTest`, que cobre o serviço + o histórico
  em arquivo (`HistoricoRepository`) com um arquivo temporário real.
- `src/test/java/com/joaovitor/validador/TestRunner.java` — descobre por reflexão todo
  método `testXxx()` das classes acima, executa cada um isoladamente (uma falha não
  interrompe as demais), imprime `PASS`/`FAIL` por teste e um resumo no final.

```bash
./test.sh   # compila src/main + src/test e roda o TestRunner
```

Sai com código de saída != 0 se qualquer teste falhar — é isso que faz `.github/workflows/ci.yml`
marcar o build como quebrado. `./build.sh` continua compilando só `src/main`, então o artefato
de produção (e a imagem Docker) nunca inclui código de teste.

Os testes cobrem as regras de `docs/REGRAS-VALIDACAO.md`: dígito verificador de CPF/CNPJ via
módulo 11 (documento válido passa, dígito verificador incorreto falha, e sequências repetidas
como `000.000.000-00`/`111.111.111-11` são rejeitadas mesmo quando o cálculo do módulo 11
"bateria" sozinho), entrada com e sem máscara, normalização de e-mail, validação semântica real
de data via `java.time` com `ResolverStyle.STRICT` (`31/02/2026` e `29/02` em ano não bissexto
são rejeitados, não só o formato `dd/mm/aaaa`), e telefone fixo (8 dígitos) vs. celular (9 dígitos).

## Docker

A API também roda em container, via build multi-stage (`eclipse-temurin:21-jdk` compila com `javac`/`build.sh`, `eclipse-temurin:21-jre` executa):

```bash
docker compose up --build
```

Isso sobe a API em `http://localhost:8080`, persistindo `data/historico.log` no host via bind mount. Sem Docker Compose, o mesmo `Dockerfile` também pode ser usado diretamente com `docker build`/`docker run`.

## Integração contínua

Todo push/PR roda `.github/workflows/ci.yml`, com dois jobs independentes: um compila a API com JDK 21 e roda a suíte de testes (`./build.sh` seguido de `./test.sh` — ver "## Testes" acima), outro instala dependências e builda o front-end React (`npm ci && npm run build` em `frontend/`).

## Front-end (React + TypeScript, GitHub Pages)

O painel (formulário de validação, histórico, contagem por tipo) é uma aplicação **React + TypeScript** de verdade, construída com [Vite](https://vitejs.dev/), em `frontend/`. O `vite.config.ts` já está configurado para publicar o build de produção diretamente em `docs/` (`base: '/ValidadorJava/'`, `build.outDir: '../docs'`), que é a pasta servida pelo GitHub Pages — ou seja, `docs/` é gerado, não é escrito à mão.

```bash
cd frontend
npm install
npm run dev      # ambiente de desenvolvimento (http://localhost:5173)
npm run build    # gera o build de produção em ../docs
```

Para publicar:

1. Rode `npm run build` dentro de `frontend/` e faça commit do conteúdo atualizado de `docs/`.
2. Suba este repositório no GitHub.
3. Em **Settings → Pages**, escolha a branch `main` e a pasta `/docs` como source.
4. O GitHub gera um link tipo `https://SEU_USUARIO.github.io/ValidadorJava/`.

O GitHub Pages só serve os artefatos estáticos gerados pelo Vite — a API continua precisando rodar em algum lugar (na sua máquina, via `./run.sh`, via Docker — ver seção abaixo —, ou publicada num serviço como Render/Railway). A URL base da API é um campo em runtime, salvo em `localStorage` pelo navegador (nunca embutido em tempo de build, já que o front-end estático e a API rodam em lugares diferentes) — ao abrir a página publicada, informe no campo "URL base da API" o endereço onde a API estiver rodando (por padrão `http://localhost:8080`). O CORS já está liberado em `HttpUtil.comCors(...)` (ver `Main.java`) para permitir isso.

## Endpoints

Idênticos aos da versão em PHP:

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/validar` | Valida um dado. Body: `{"tipo": "cpf\|cnpj\|email\|data\|telefone", "valor": "..."}` |
| GET | `/api/historico?limite=50` | Últimas validações registradas |
| GET | `/api/estatisticas` | Contagem de validações por tipo |

### Exemplo comparando as duas linguagens

```bash
# PHP (porta 8000) e Java (porta 8080) rodando ao mesmo tempo:
curl -s -X POST http://localhost:8000/api/validar -d '{"tipo":"cpf","valor":"111.444.777-35"}'
curl -s -X POST http://localhost:8080/api/validar -d '{"tipo":"cpf","valor":"111.444.777-35"}'
# as duas respostas são idênticas campo a campo
```

## Estrutura

```
src/main/java/com/joaovitor/validador/
  Main.java              ponto de entrada (HttpServer)
  core/                  JsonReader, JsonWriter, HttpUtil — utilitários sem dependências externas
  enums/TipoDado.java
  model/                 ResultadoValidacao, HistoricoEntry (records)
  validators/            Um validador por tipo + DigitosUtil (interface com métodos default,
                         o equivalente em Java de uma trait do PHP)
  repository/            HistoricoRepository (arquivo texto + cache em memória)
  service/ValidacaoService.java
  handlers/              Handlers HTTP (um por rota)

src/test/java/com/joaovitor/validador/
  testing/Assert.java    helper de asserção próprio (sem JUnit)
  tests/                 uma classe de teste por validador + ValidacaoServiceTest
  TestRunner.java        descobre e roda os testes por reflexão, imprime PASS/FAIL

frontend/                painel React + TypeScript (Vite)
  src/components/        ConexaoCard, ValidarCard, EstatisticasCard, HistoricoCard
  src/lib/                cliente HTTP (fetch) e tipos compartilhados com a API
  vite.config.ts          base '/ValidadorJava/' + build.outDir '../docs' (publica direto em docs/)

docs/                    gerado por `npm run build` em frontend/ — servido pelo GitHub Pages
```

## Regras de validação implementadas

Mesmas da versão em PHP — ver [`docs/REGRAS-VALIDACAO.md`](docs/REGRAS-VALIDACAO.md):

- **CPF/CNPJ**: dígito verificador via módulo 11, rejeitando sequências repetidas.
- **E-mail**: regex + normalização.
- **Data**: `dd/mm/aaaa` com validação semântica real via `java.time` (`ResolverStyle.STRICT`, rejeita `31/02`).
- **Telefone**: fixo (8 dígitos) ou celular (9 dígitos), com ou sem máscara.
