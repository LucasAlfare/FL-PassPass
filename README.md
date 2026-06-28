# PassPass

## Um duelo de dedução, estratégia e gerenciamento de recursos

### Visão geral

**PassPass** é um jogo competitivo de dedução onde dois jogadores entram em uma disputa para descobrir o código secreto um do outro.

Cada jogador possui uma combinação secreta de 4 dígitos e deve usar investigação, tentativas e bloqueios estratégicos para descobrir o código adversário antes de ser descoberto.

Vencer não depende apenas de descobrir a resposta: administrar energia, escolher o momento certo para atacar e impedir o avanço do adversário são partes essenciais da estratégia.

---

# 1. Objetivo do jogo

Cada jogador possui um código secreto.

O objetivo é:

> Descobrir o código secreto do adversário antes que ele descubra o seu.

A partida termina imediatamente quando:

* Um jogador descobre corretamente o código adversário.
* Um jogador fica sem energia.

---

# 2. Preparação

Antes do início da partida, cada jogador cria seu código secreto.

O código deve seguir estas regras:

* Possuir exatamente **4 dígitos**.
* Utilizar números de **0 a 9**.
* Nenhum dígito pode se repetir.

Exemplo válido:

```
7 2 9 4
```

Exemplos inválidos:

```
7 7 2 4
```

(repetição de dígito)

```
1 2 3 4 5
```

(quantidade incorreta de dígitos)

O código deve permanecer oculto durante toda a partida.

---

# 3. Recursos dos jogadores

Cada jogador começa com:

## Energia

```
25 pontos
```

A energia representa a capacidade do jogador de realizar ações.

Toda ação possui um custo.

Se a energia chegar a:

```
0
```

o jogador perde imediatamente.

A energia faz parte da estratégia:

* gastar pouco e investigar;
* gastar muito tentando finalizar;
* usar recursos para atrapalhar o adversário.

---

## Bloqueios

Cada jogador recebe:

```
2 cargas de bloqueio
```

Bloqueios são recursos limitados usados para criar obstáculos ao adversário.

Cada bloqueio utilizado consome uma carga.

---

# 4. Estrutura dos turnos

Os jogadores alternam turnos.

Em seu turno, o jogador deve escolher **uma única ação**:

1. Investigar
2. Tentar código
3. Bloquear

A ação só pode ser realizada se o jogador possuir energia suficiente.

Após a ação ser concluída, o turno passa para o adversário.

---

# 5. Ações disponíveis

---

# AÇÃO 1 — INVESTIGAR

## Custo:

```
1 energia
```

A investigação permite fazer perguntas sobre o código inimigo.

O adversário deve responder corretamente:

```
SIM
```

ou

```
NÃO
```

A investigação não revela diretamente o código.

Ela serve para eliminar possibilidades e construir uma dedução.

Existem quatro categorias de investigação.

---

## 5.1 Existência de dígito

Pergunta:

> O dígito X existe no código?

Exemplo:

```
O número 8 existe?
```

Resposta:

```
Sim
```

ou

```
Não
```

---

## 5.2 Valor da posição

Pergunta:

> A posição X possui o número Y?

Exemplo:

```
O segundo dígito é 4?
```

Resposta:

```
Sim
```

ou

```
Não
```

---

## 5.3 Característica da posição

Pergunta:

> O dígito da posição X possui determinada característica?

Possibilidades:

* É par
* É ímpar
* É maior que 5
* É menor que 5

Exemplo:

```
O terceiro dígito é par?
```

Resposta:

```
Sim
```

ou

```
Não
```

---

## 5.4 Comparação entre posições

Pergunta:

> O valor da posição X é maior que o valor da posição Y?

Exemplo:

```
O primeiro dígito é maior que o quarto?
```

Resposta:

```
Sim
```

ou

```
Não
```

---

# AÇÃO 2 — TENTAR CÓDIGO

## Custo:

```
5 energias
```

O jogador tenta descobrir o código completo do adversário.

Exemplo:

Tentativa:

```
7 9 2 4
```

O adversário compara com o código verdadeiro e informa:

## Corretos

Quantidade de dígitos:

* existentes;
* na posição correta.

## Deslocados

Quantidade de dígitos:

* existentes no código;
* porém em posição diferente.

---

## Exemplo

Código real:

```
7 2 9 4
```

Tentativa:

```
7 9 4 1
```

Resultado:

```
Corretos: 1
Deslocados: 2
```

Interpretação:

* O número 7 está correto e na posição correta.
* Os números 9 e 4 existem no código, mas estão em posições diferentes.

---

## Vitória

Se o resultado for:

```
Corretos: 4
```

o jogador descobriu o código e vence imediatamente.

---

# AÇÃO 3 — BLOQUEAR

## Custo:

```
3 energias
```

O jogador usa uma carga de bloqueio para prejudicar o próximo turno do adversário.

Bloqueios não impedem completamente o adversário de jogar.

Eles apenas alteram suas opções.

---

# Tipos de bloqueio

---

## 1. Bloqueio de investigação

O jogador escolhe uma categoria de investigação que ficará indisponível no próximo turno adversário.

Categorias:

* Existência de dígito
* Valor da posição
* Característica da posição
* Comparação entre posições

Exemplo:

```
Bloquear perguntas de existência
```

No próximo turno, o adversário não poderá perguntar:

```
O número X existe?
```

---

## 2. Bloqueio de tentativa

O adversário ainda pode tentar descobrir o código.

Porém, a resposta será reduzida.

Resposta normal:

```
Corretos + Deslocados
```

Com bloqueio:

```
Apenas Corretos
```

---

## 3. Bloqueio de energia

A próxima ação do adversário terá custo aumentado.

Exemplo:

Investigar:

Normal:

```
1 energia
```

Com bloqueio:

```
4 energias
```

---

Após ser aplicado:

* o bloqueio é consumido;
* o efeito acontece uma única vez.

---

# 6. Exemplo de partida

Jogador A:

Código:

```
7 2 9 4
```

Jogador B:

Código:

```
5 8 1 3
```

---

Turno do jogador A:

A investiga:

```
O número 9 existe?
```

Resposta:

```
Sim
```

Energia:

```
24
```

---

Turno do jogador B:

B usa bloqueio:

```
Bloquear investigação de posição
```

Energia:

```
22
```

---

Turno do jogador A:

A tenta:

```
7 2 0 4
```

Resposta:

```
Corretos: 3
Deslocados: 0
```

Agora A sabe que está muito próximo da solução.

---

# 7. Estratégia

Cada ação possui uma vantagem e um risco.

## Investigar

Vantagem:

* baixo custo;
* aumenta conhecimento.

Risco:

* demora para concluir.

---

## Tentar código

Vantagem:

* pode finalizar imediatamente.

Risco:

* custo alto.

---

## Bloquear

Vantagem:

* atrasa o adversário;
* cria oportunidade.

Risco:

* possui uso limitado;
* consome energia.

---

# 8. Regras finais

* O código secreto nunca pode ser alterado.
* Toda resposta deve ser verdadeira.
* Toda ação precisa possuir energia suficiente.
* Bloqueios não anulam completamente ações.
* Informações obtidas devem ser usadas para dedução.
* A partida termina imediatamente quando houver vencedor ou jogador sem energia.

---

# Resumo da partida

```
Criar códigos secretos

↓

Alternar turnos

↓

Investigar, tentar ou bloquear

↓

Administrar energia

↓

Descobrir o código inimigo

↓

Vencer
```