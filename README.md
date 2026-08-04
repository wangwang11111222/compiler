# Tiger Language Compiler

A complete compiler implementation for the **Tiger language**, built with ANTLR4 and Java. The compiler covers the full pipeline from lexical analysis to MIPS assembly generation, with advanced optimizations including data-flow analysis, graph-coloring register allocation, and value-number optimization.

## Compiler Pipeline

```
Source Code (.tiger)
    │
    ▼
┌──────────────┐     ┌──────────────────┐     ┌──────────────────┐
│  Lexer/Parser │────▶│  Semantic Checker │────▶│  Symbol Table     │
│  (ANTLR4)     │     │  (Type System)    │     │  (Scope Mgmt)     │
└──────────────┘     └──────────────────┘     └────────┬─────────┘
                                                       │
┌──────────────┐     ┌──────────────────┐              ▼
│  MIPS Code   │◀────│  Register Alloc.  │◀────┌──────────────────┐
│  Generation  │     │  (Naive/Local/    │     │  IR Generator     │
│              │     │   Briggs)         │     │  (3-Address Code)  │
└──────────────┘     └──────────────────┘     └────────┬─────────┘
        │                                                │
        │                    ┌──────────────────┐        ▼
        │                    │  SVN Optimizer   │◀──┌──────────────────┐
        │                    │  (Const. Prop.)  │   │  Data Flow Analysis│
        │                    └──────────────────┘   │  (Liveness/CFG)    │
        │                                            └──────────────────┘
        ▼
┌──────────────┐
│  MIPS .s File │
└──────────────┘
```

## Project Structure

```
compiler/
├── Tiger.g4                    # ANTLR4 grammar definition for Tiger language
├── src/
│   ├── Main.java               # CLI entry point with 12 pipeline flags
│   ├── SemanticChecker.java    # Type checking, main function validation
│   ├── SymbolTable.java        # Scoped symbol table with name mangling
│   ├── IRGenerator.java        # Three-address code IR generation
│   ├── DataFlowAnalyzer.java   # Basic blocks, CFG, liveness, dominators
│   ├── AllocationEmit.java     # Naive / Local / Briggs graph-coloring allocation
│   ├── SVN.java                # Simple Value Number optimization
│   └── DotGenerator.java       # Graphviz DOT graph output
├── examples/
│   ├── benchmark1.tiger        # Sample Tiger source
│   ├── benchmark1.ir           # Generated IR
│   ├── benchmark1.dataflow.json# Data-flow analysis output
│   └── benchmark1.briggs.s     # Briggs-allocated MIPS assembly
├── results.csv                 # Benchmark instruction counts
├── stats_summary.csv           # Aggregated statistics
├── Makefile                    # Build system
└── README.md
```

## Building

### Prerequisites

- **JDK 11+**
- **ANTLR 4.13.2** (`antlr-4.13.2-complete.jar`)

### Build

```bash
# Set ANTLR jar path in Makefile if needed
make
```

This produces `cs8803_bin/tigerc.jar` and a wrapper script `cs8803_bin/tigerc`.

## Usage

```bash
./cs8803_bin/tigerc -f <source.tiger> [options]
```

### CLI Options

| Flag | Description |
|------|-------------|
| `-f <file>` | Input Tiger source file |
| `-s` | Run scanner (lexical analysis) |
| `-p` | Run parser (syntax analysis) |
| `-t` | Build symbol table |
| `-i` | Generate IR (three-address code) |
| `-d` | Run data-flow analysis |
| `-D <file>` | Output data-flow JSON |
| `-n` | Naive register allocation |
| `-l` | Local register allocation |
| `-g` | Briggs graph-coloring allocation |
| `-b` | Output Briggs interference graph |
| `-x` | Enable SVN optimization |
| `-r <file>` | Load pre-generated IR |

### Example

```bash
# Full pipeline: parse → semantic check → IR → data-flow → Briggs → MIPS
./cs8803_bin/tigerc -f examples/benchmark1.tiger -t -i -d -g -x
```

## Optimization Results

Benchmark comparison across register allocation strategies (instruction counts):

| Benchmark | Naive | Local | Briggs | Briggs+SVN |
|-----------|-------|-------|--------|------------|
| benchmark1 | 305 | 317 | 313 | 311 |
| benchmark2 | 4,991 | 5,003 | 5,003 | 4,919 |
| benchmark3 | 1,701,557 | 2,464,867 | 2,337,649 | 2,337,649 |
| benchmark4 | 19,587 | 19,599 | 19,599 | 19,597 |

**Key findings**: Briggs graph-coloring consistently outperforms local allocation on large benchmarks. SVN optimization provides additional constant folding and propagation benefits.

## Key Implementations

- **Name Mangling**: Scope-aware identifier naming (`_scopeId_name`) for nested scope disambiguation
- **Liveness Analysis**: Backward data-flow on basic blocks with live-in/live-out sets
- **Briggs Algorithm**: Graph-coloring with spill-cost weighting by loop depth
- **SVN Optimization**: Constant propagation/folding, lexical identity elimination, value-identity deduplication

## License

MIT
