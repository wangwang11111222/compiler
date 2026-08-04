# Compiler Project

Public-facing compiler project built around the Tiger language.

## What It Demonstrates

- ANTLR grammar design for lexical and syntax analysis.
- Semantic checking and symbol-table construction.
- Intermediate representation generation.
- Data-flow analysis.
- Register allocation and MIPS-like assembly emission.
- Benchmark output comparison and summary reporting.

## Structure

- `src/`: Java compiler pipeline source.
- `Tiger.g4`: Tiger language grammar.
- `examples/`: one compact example showing source, IR, data-flow JSON, and generated assembly.
- `results.csv` and `stats_summary.csv`: selected benchmark summaries.

## Public-Safe Scope

This repository excludes course PDFs, generated build folders, VM/Vagrant state, private keys, full test bundles, and large third-party simulator files.
