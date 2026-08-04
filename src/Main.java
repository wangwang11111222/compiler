import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.*;

public class Main {

    static class ErrorHandler extends BaseErrorListener {
        public boolean hasError = false;
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            hasError = true;
            System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }

    public static void main(String[] args) {
        String inputFile = "";
        String irFile = "";  
        boolean flagScanner = false;
        boolean flagParser = false;
        boolean flagSymbolTable = false;
        boolean flagIR = false;
        boolean flagData = false;
        boolean flagNaive = false;
        boolean flagLocal = false;
        boolean flagBriggs = false;
        boolean flagBriggsGraph = false;
        boolean flagSVN = false;
        String dataflowFile = "";

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-f") && i + 1 < args.length) {
                inputFile = args[++i];
            } else if (arg.equals("-r") && i + 1 < args.length) { 
                irFile = args[++i];
            } else if (arg.equals("-s")) {
                flagScanner = true;
            } else if (arg.equals("-p")) {
                flagParser = true;
            } else if (arg.equals("-t")) {
                flagSymbolTable = true;
            } else if (arg.equals("-i")) {
                flagIR = true;
            } else if (arg.equals("-d")) {
                flagData = true;
            } else if (arg.equals("-n")) {
                flagNaive = true;
            } else if (arg.equals("-l")) {
                flagLocal = true;
            } else if (arg.equals("-g")) {
                flagBriggs = true;
            } else if (arg.equals("-b")) {
                flagBriggsGraph = true;
            } else if (arg.equals("-D") && i + 1 < args.length) {
                dataflowFile = args[++i];
            } else if (arg.equals("-x")) {
                flagSVN = true;
            }else {
                System.err.println("Error: Unknown argument " + arg);
                System.exit(1);
            }
        }

        if (inputFile.isEmpty()) System.exit(1);
        CharStream input = null;
        try {
            input = CharStreams.fromFileName(inputFile);
        } catch (IOException e) {
            System.err.println("Error: Cannot read file " + inputFile);
            System.exit(1);
        }
        TigerLexer lexer = new TigerLexer(input);
        ErrorHandler lexerErrorListener = new ErrorHandler();
        lexer.removeErrorListeners();
        lexer.addErrorListener(lexerErrorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        if (lexerErrorListener.hasError) System.exit(2);

        if (flagScanner) {
            String outPath = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".tokens";
            try (PrintWriter out = new PrintWriter(outPath)) {
                Vocabulary vocab = lexer.getVocabulary();
                for (Token token : tokens.getTokens()) {
                    if (token.getType() == Token.EOF) break;
                    if (token.getChannel() != Token.DEFAULT_CHANNEL) continue;
                    String typeName = vocab.getSymbolicName(token.getType());
                    String text = token.getText();
                    if (typeName == null) typeName = String.valueOf(token.getType());
                    out.println("<" + typeName + ", \"" + text + "\">");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TigerParser parser = new TigerParser(tokens);
        ErrorHandler parserErrorListener = new ErrorHandler();
        parser.removeErrorListeners();
        parser.addErrorListener(parserErrorListener);

        ParseTree tree = parser.tiger_program();

        if (parserErrorListener.hasError || parser.getNumberOfSyntaxErrors() > 0) System.exit(3);

        if (flagParser) {
            String outPath = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".tree.gv";
            try (PrintWriter out = new PrintWriter(outPath)) {
                out.print(DotGenerator.generate(tree, parser));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SymbolTable symbolTable = new SymbolTable();
        SemanticChecker checker = new SemanticChecker(symbolTable);
        checker.visit(tree);
        if (flagSymbolTable) {
            String outPath = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".st";
            PrintStream originalOut = System.out;
            try (PrintStream fileOut = new PrintStream(new FileOutputStream(outPath))) {
                System.setOut(fileOut);
                symbolTable.dump();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.setOut(originalOut);
            }
        }
        if (checker.hasErrors) {
            System.exit(4);
        }

        if (flagIR) {
            IRGenerator irGenerator = new IRGenerator(symbolTable);
            irGenerator.visit(tree);
            String outPath = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".ir";
            try (PrintWriter out = new PrintWriter(outPath)) {
                for (String instr : irGenerator.getInstructions()) {
                    out.println(instr);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

      
        if (!irFile.isEmpty()) {
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(irFile))) {
                String line;
                while ((line = br.readLine()) != null) lines.add(line);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            DataFlowAnalyzer.parseIR(lines);
            Set<String> excludedStatic = new LinkedHashSet<>(DataFlowAnalyzer.staticVars);
            for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
                Set<String> excluded = new HashSet<>(excludedStatic);
                excluded.addAll(DataFlowAnalyzer.globalArrays);
                excluded.addAll(func.localArrays);
                func.analyze(excluded);
            }
            if(flagData){
            String outPath = inputFile.substring(0, inputFile.lastIndexOf('.')) + ".dataflow.json";
            try (PrintWriter out = new PrintWriter(outPath)) {
                out.print(DataFlowAnalyzer.toJSON());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }

        if (flagSVN && DataFlowAnalyzer.functions != null) {
            SVN.optimize();
            Set<String> excludedStatic2 = new LinkedHashSet<>(DataFlowAnalyzer.staticVars);
            for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
                for (DataFlowAnalyzer.Instruction inst : func.instructions) {
                    inst.def.clear(); inst.use.clear();
                    inst.in.clear(); inst.out.clear();
                    inst.successors.clear();
                }
                func.basicBlocks.clear();
                func.globalSpillCosts.clear();
                Set<String> excluded = new HashSet<>(excludedStatic2);
                excluded.addAll(DataFlowAnalyzer.globalArrays);
                excluded.addAll(func.localArrays);
                func.analyze(excluded);
            }
            System.out.println(SVN.report());
        }


        if (flagNaive || flagLocal || flagBriggs) {
   
            if (DataFlowAnalyzer.functions == null && !irFile.isEmpty()) {
                List<String> irLines = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(irFile))) {
                    String line;
                    while ((line = br.readLine()) != null) irLines.add(line);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                DataFlowAnalyzer.parseIR(irLines);
                Set<String> excludedStatic = new LinkedHashSet<>(DataFlowAnalyzer.staticVars);
                for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
                    Set<String> excluded = new HashSet<>(excludedStatic);
                    excluded.addAll(DataFlowAnalyzer.globalArrays);
                    excluded.addAll(func.localArrays);
                    func.analyze(excluded);
                }
            }

            String basePath = inputFile.substring(0, inputFile.lastIndexOf('.'));

            if (flagNaive) {
                Map<String, String> alloc = AllocationEmit.naiveAlloc();
                List<String> mips = AllocationEmit.instructionSelection(alloc);
                String outPath = basePath + ".naive.s";
                try (PrintWriter out = new PrintWriter(outPath)) {
                    for (String line : mips) out.println(line);
                } catch (IOException e) { e.printStackTrace(); }
            }

            if (flagLocal) {
                Map<String, String> alloc = AllocationEmit.localBBAlloc();
                List<String> mips = AllocationEmit.instructionSelection(alloc);
                String outPath = basePath + ".ib.s";
                try (PrintWriter out = new PrintWriter(outPath)) {
                    for (String line : mips) out.println(line);
                } catch (IOException e) { e.printStackTrace(); }
            }

            if (flagBriggs) {
                Map<String, String> alloc = AllocationEmit.briggAlloc();
                List<String> mips = AllocationEmit.instructionSelection(alloc);
                String outPath = basePath + ".briggs.s";
                try (PrintWriter out = new PrintWriter(outPath)) {
                    for (String line : mips) out.println(line);
                } catch (IOException e) { e.printStackTrace(); }
        if (flagBriggsGraph) {
                for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
                    List<Map<String, Set<String>>> graphs = AllocationEmit.buildGraphs(func);
                    AllocationEmit.exportGraphViz(graphs.get(0), graphs.get(1), basePath, alloc, func.globalSpillCosts);
                }
            }
            }
        }

        System.exit(0);
    }
}