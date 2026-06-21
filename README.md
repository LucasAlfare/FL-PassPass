# CÓDIGO SECRETO

## Um duelo de dedução, estratégia e gerenciamento de recursos

---

# Objetivo

Descubra o código secreto do adversário antes que ele descubra o seu.

Cada jogador possui um código oculto de 4 dígitos e deve usar investigação, tentativas e bloqueios para encontrar a combinação correta.

---

# 1. Preparação

Cada jogador cria um código secreto.

O código deve possuir:

* 4 dígitos;
* números de 0 a 9;
* nenhum dígito pode se repetir.

Exemplo válido:

```
7 2 9 4
```

Exemplos inválidos:

```
7 7 2 4 ❌
```

```
1 2 3 4 5 ❌
```

O código deve permanecer escondido durante toda a partida.

---

# 2. Recursos iniciais

Cada jogador começa com:

## Energia: 25 pontos

A energia representa sua capacidade de investigar, atacar e se defender.

## Bloqueios: 2 cargas

Cada jogador possui apenas duas oportunidades de usar bloqueios durante a partida.

---

# 3. Turnos

Os jogadores alternam turnos.

Em cada turno, o jogador escolhe uma única ação:

1. Investigar
2. Tentar código
3. Bloquear

A ação escolhida deve poder ser paga com a energia disponível.

---

# AÇÃO 1 — INVESTIGAR

**Custo: 1 energia**

O jogador faz uma pergunta sobre o código adversário.

As perguntas devem seguir exatamente uma das quatro categorias:

---

## 1) Existência de dígito

Pergunta:

> O dígito X existe no código?

Exemplo:

> O número 8 existe?

Resposta:

* Sim
* Não

---

## 2) Valor da posição

Pergunta:

> A posição X possui o número Y?

Exemplo:

> O segundo dígito é 4?

Resposta:

* Sim
* Não

---

## 3) Característica da posição

Pergunta:

> A posição X possui determinada característica?

Opções:

* É par?
* É ímpar?
* É maior que 5?
* É menor que 5?

Exemplo:

> O terceiro dígito é par?

Resposta:

* Sim
* Não

---

## 4) Comparação entre posições

Pergunta:

> O valor da posição X é maior que o valor da posição Y?

Exemplo:

> O primeiro dígito é maior que o quarto?

Resposta:

* Sim
* Não

---

A investigação serve para reduzir as possibilidades e montar uma dedução do código.

---

# AÇÃO 2 — TENTAR CÓDIGO

**Custo: 5 energias**

O jogador tenta descobrir o código completo.

Exemplo:

Tentativa:

```
7 9 2 4
```

O adversário compara com o código real e informa:

## Corretos

Quantidade de dígitos na posição correta.

## Deslocados

Quantidade de dígitos corretos que existem no código, mas estão na posição errada.

---

Exemplo:

Código real:

```
7 2 9 4
```

Tentativa:

```
7 9 4 1
```

Resposta:

```
1 correto
2 deslocados
```

Significa:

* 1 número está na posição certa;
* 2 números existem no código, mas estão em posições diferentes.

---

## Vitória

Se a tentativa resultar em:

```
4 corretos
```

o jogador descobriu o código e vence imediatamente.

---

# AÇÃO 3 — BLOQUEAR

**Custo: 3 energias**

O jogador usa uma carga de bloqueio para atrapalhar o próximo turno do adversário.

Cada jogador possui apenas 2 bloqueios.

Escolha um tipo:

---

## Bloqueio de investigação

O adversário não pode usar uma categoria específica de investigação no próximo turno.

Exemplo:

> Bloquear perguntas de Existência.

O adversário continua podendo usar outras categorias.

---

## Bloqueio de tentativa

O adversário pode tentar descobrir o código, mas recebe uma pista reduzida.

Resposta normal:

```
Corretos + Deslocados
```

Com bloqueio:

```
Apenas Corretos
```

---

## Bloqueio de energia

A próxima ação do adversário custa +3 energias.

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

Após usado, o bloqueio é consumido.

---

# 4. Perda de energia

Quando um jogador chega a:

```
0 energia
```

ele perde imediatamente.

Não importa se estava próximo de descobrir o código.

A administração da energia faz parte da estratégia.

---

# 5. Regras importantes

* O código não pode ser alterado depois de criado.
* O jogador deve responder corretamente às perguntas.
* Toda ação precisa ter energia suficiente.
* Bloqueios nunca impedem completamente o adversário de jogar.
* As informações descobertas devem ser anotadas pelo jogador.
* A partida termina assim que um jogador descobre o código ou perde toda a energia.

---

# Resumo estratégico

**Investigar**
→ ganha informação, mas consome tempo e energia.

**Tentar código**
→ pode garantir a vitória, mas custa caro.

**Bloquear**
→ atrapalha o adversário, mas usa recursos limitados.

O vencedor será o jogador que conseguir administrar melhor suas informações e seus recursos.
