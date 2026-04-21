# Sistema de Estoque com Swing

Aplicação desktop em Java Swing para controle de estoque com **módulo operacional** e **painel administrativo**.

## Funcionalidades

### Aba "Estoque"
- Cadastro de produtos com nome, quantidade e preço unitário.
- Tabela com cálculo automático do total por item.
- Remoção de item selecionado.
- Limpeza de todo o estoque.
- Visualização do valor total em estoque.

### Aba "Painel administrativo"
- Indicadores em tempo real:
  - total de produtos cadastrados;
  - total de unidades em estoque;
  - valor financeiro total do estoque;
  - quantidade de produtos com estoque baixo.
- Definição de limite para considerar estoque baixo.
- Ajuste de quantidade no item selecionado (entrada/saída manual).
- Reajuste percentual de preço em lote para todos os itens.

## Como executar

```bash
javac -d out src/EstoqueApp.java
java -cp out EstoqueApp
```
