# Dice-Expression-Parse
Um parser recursivo para expressões de dados estilo RPG, com suporte a modificadores como:

- dM
- NdM
- NdM+K
- NdMkhX
- NdMklX
- d%
- NdM!
- NdM!>X
- NdM!>=X
- NdM!<X
- NdM!=X
- NdM!<=X
- NdM!!
- NdM!!X
- NdM!p
- NdM!pX
- Também suporta expressões matemáticas com precedência e parênteses

# Arquitetura Geral
O parser segue a arquitetura clássica de interpretadores:

```text
                ┌────────────────────┐
                │   String Input     │
                │  "4d6kh3 + 2"      │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │       Lexer        │
                │  Análise Léxica    │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │       Parser       │
                │ Recursive Descent  │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │         AST        │
                │ Abstract Syntax    │
                │      Tree          │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │     Evaluator      │
                │  Motor de Execução │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │     Resultado      │
                └────────────────────┘
```
# Grámatica Formal
```
<expression> ::= <term> { ("+" | "-") <term> }

<term>       ::= <factor> { ("*" | "/") <factor> }

<factor>     ::= NUMBER
               | <dice>
               | "(" <expression> ")"

<dice>       ::= [<quantity>] "d" <sides> { <modifier> }

<quantity>   ::= NUMBER

<sides>      ::= NUMBER | "%"

<modifier>   ::= <keep>
               | <explosion>

<keep>       ::= "kh" NUMBER
               | "kl" NUMBER

<explosion>  ::= "!"
               | "!!"
               | "!!" NUMBER
               | "!p"
               | "!p" NUMBER
               | "!" <condition>

<condition>  ::= <relop> NUMBER

<relop>      ::= ">"
               | ">="
               | "<"
               | "<="
               | "="
               | "!="
```

# Ordem de execução do motor

Durante a avaliação da AST:

```text
1. Rola dados base
2. Aplica explosões
3. Aplica keep (kh / kl)
4. Soma resultado do DiceNode
5. Resolve BinaryOperationNodes respeitando precedência
```

Fluxo conceitual:

```text
Dice Roll
   ↓
Explosion Phase
   ↓
Keep Phase
   ↓
Aggregation
   ↓
Math Resolution
```
# Explosões Suportadas

| Sintaxe | Comportamento |
| --- | --- |
| `!` | explode no valor máximo |
| `!!` | explosão composta |
| `!!X` | limite de explosões |
| `!>X` | explode se maior que X |
| `!>=X` | explode se maior ou igual a X |
| `!<X` | explode se menor que X |
| `!<=X` | explode se menor ou igual a X |
| `!=X` | explode se igual a X |
| `!p` | explosão penetrante |
| `!pX` | penetrante com penalidade |