# Contrato de validação — regras compartilhadas entre PHP e Java

Este documento é o "contrato" único que as duas implementações (`ValidadorPHP` e `ValidadorJava`)
seguem à risca, para garantir que **o mesmo dado produza exatamente o mesmo resultado**
independente da linguagem que o validou — o mesmo problema que resolvi no trabalho ao
padronizar validação de datas via regex entre PHP, Java, JavaScript/TypeScript e C#.

## 1. CPF

- Formato de entrada aceito: com ou sem máscara (`123.456.789-09` ou `12345678909`).
- Regra de formato: exatamente 11 dígitos após remover pontuação.
- Regra de validade: dígitos verificadores calculados via módulo 11 (algoritmo oficial da Receita Federal).
- Rejeitar sequências de dígito repetido (`00000000000`, `11111111111`, ...) mesmo que passem no módulo 11.
- Saída padronizada: `###.###.###-##`.

## 2. CNPJ

- Formato de entrada aceito: com ou sem máscara (`12.345.678/0001-95` ou `12345678000195`).
- Regra de formato: exatamente 14 dígitos após remover pontuação.
- Regra de validade: dígitos verificadores via módulo 11 com pesos `[5,4,3,2,9,8,7,6,5,4,3,2]` (1º DV) e `[6,5,4,3,2,9,8,7,6,5,4,3,2]` (2º DV).
- Rejeitar sequências de dígito repetido.
- Saída padronizada: `##.###.###/####-##`.

## 3. E-mail

- Regex: `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`
- Normalização de saída: minúsculas, sem espaços nas pontas.

## 4. Data (formato brasileiro)

- Entrada: `dd/mm/aaaa`.
- Regex de formato: `^\d{2}/\d{2}/\d{4}$`
- Validação semântica: dia/mês/ano devem formar uma data real (considerar anos bissextos).
- Saída padronizada (para persistência/API): ISO-8601 `aaaa-mm-dd`.

## 5. Telefone (Brasil)

- Entrada aceita: `(11) 91234-5678`, `11912345678`, `(11) 1234-5678`.
- Regex de formato: `^\(?\d{2}\)?\s?9?\d{4}-?\d{4}$` após remover espaços/traços na normalização de entrada.
- Saída padronizada: `(##) #####-####` (celular, 9 dígitos) ou `(##) ####-####` (fixo, 8 dígitos).

## Por que isso importa

Numa organização com mais de uma stack, a mesma regra de negócio ("o que é um CPF válido")
não pode divergir entre o sistema em PHP e o sistema em Java — senão um aceita um dado que o
outro rejeita, e isso vira bug de integração difícil de rastrear. Este contrato documentado é
o que evita essa divergência: qualquer nova linguagem que precise validar os mesmos dados
implementa a partir desta mesma especificação.
