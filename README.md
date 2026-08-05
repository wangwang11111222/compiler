# Tiger Language Compiler

Complete compiler implementation for the Tiger language, built with ANTLR4 and Java. The compiler covers lexical analysis, parsing, semantic checking, IR generation, data-flow analysis, optimization, register allocation, and MIPS-style assembly emission.

## Compiler Pipeline

```text
Source .tiger
  -> Lexer / Parser
  -> Semantic Checker
  -> Symbol Table
  -> IR Generator
  -> Data-Flow Analysis
  -> SVN Optimization
  -> Register Allocation
  -> MIPS-style Assembly
```

## Project Structure

```text
compiler/
|-- Tiger.g4
|-- src/
|   |-- Main.java
|   |-- SemanticChecker.java
|   |-- SymbolTable.java
|   |-- IRGenerator.java
|   |-- DataFlowAnalyzer.java
|   |-- AllocationEmit.java
|   |-- SVN.java
|   `-- DotGenerator.java
|-- examples/
|-- results.csv
|-- stats_summary.csv
`-- Makefile
```

## Key Implementations

- ANTLR grammar and parse tree integration.
- Scope-aware symbol table and type checking.
- Three-address-code IR generation.
- Basic blocks, CFG construction, liveness, and dominator analysis.
- Naive, local, and Briggs-style graph-coloring register allocation.
- Simple value numbering for constant propagation and folding.
- MIPS-style assembly output.

## Build

```bash
make
```

## Example

```bash
./cs8803_bin/tigerc -f examples/benchmark1.tiger -t -i -d -g -x
```

## Engineering Focus

This project demonstrates end-to-end compiler construction and large-system debugging across frontend parsing, middle-end analysis, and backend code generation.
