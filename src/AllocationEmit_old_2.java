import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AllocationEmit {
    private static final String[] INT_REGS = {"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7"};
    private static final String[] FLOAT_REGS = {"$f20", "$f22", "$f24", "$f26", "$f28", "$f30"};
    private static final String[] INT_ARGS = {"$a0", "$a1", "$a2", "$a3"};
    private static final String[] FLOAT_ARGS = {"$f12", "$f13", "$f14", "$f15"};
    private static final String INT_RETURN = "$v0";
    private static final String FLOAT_RETURN = "$f0";
    private static final String[] INTERNAL_INT = {"$v1", "$t8", "$t9"};
    private static final String[] INTERNAL_FLOAT = {"$f16", "$f17", "$f18"};
    private static final String[] UTILITY = {"$sp", "$fp", "$gp", "$ra"};

 

    public static Map<String, String> naiveAlloc() {
        Map<String, String> alloc = new LinkedHashMap<>();
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            for (String var : func.localVars) {
                alloc.put(var, "spilled");
            }
            for (String[] param : func.params) {
                alloc.put(param[0], "spilled");
            }
        }
        return alloc;
    }

    public static Map<String, String> localBBAlloc() {
        Map<String, String> alloc = new LinkedHashMap<>();
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            for (DataFlowAnalyzer.BasicBlock bb : func.basicBlocks) {
                Set<String> assignedIntRegs = new LinkedHashSet<>();
                Set<String> assignedFloatRegs = new LinkedHashSet<>();
                List<String> sortedVar = new ArrayList<>(bb.localSpillCosts.keySet());
                sortedVar.sort((a, b) -> bb.localSpillCosts.get(b) - bb.localSpillCosts.get(a));
                for (String var : sortedVar) {
                    if (alloc.containsKey(var)) {
                        continue;
                    }
                    String type = func.typeMap.getOrDefault(var, DataFlowAnalyzer.globalTypeMap.getOrDefault(var, "int"));
                    if (type.endsWith("[]")) {
                        continue;
                    }
                    boolean isFloat = type.equals("float");
                    String[] regPool = isFloat ? FLOAT_REGS : INT_REGS;
                    Set<String> assigned = isFloat ? assignedFloatRegs : assignedIntRegs;
                    boolean found = false;
                    for (String reg : regPool) {
                        if (!assigned.contains(reg)) {
                            alloc.put(var, reg);
                            assigned.add(reg);
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        alloc.put(var, "spilled");
                    }
                }
            }
        }
        return alloc;
    }

    public static Map<String, String> briggAlloc() {
        Map<String, String> alloc = new LinkedHashMap<>();
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            List<Map<String, Set<String>>> graphList = buildGraphs(func);
            Map<String, Set<String>> intGraph = graphList.get(0);
            Map<String, Set<String>> floatGraph = graphList.get(1);
            Deque<String> intStack = buildStack(intGraph, func.globalSpillCosts, INT_REGS.length);
            Deque<String> floatStack = buildStack(floatGraph, func.globalSpillCosts, FLOAT_REGS.length);

            Map<String, String> intAlloc = colorGraph(intGraph, intStack, INT_REGS);
            Map<String, String> floatAlloc = colorGraph(floatGraph, floatStack, FLOAT_REGS);
            alloc.putAll(intAlloc);
            alloc.putAll(floatAlloc);
        }
        return alloc;
    }



    public static List<Map<String, Set<String>>> buildGraphs(DataFlowAnalyzer.FunctionDa func) {
        Map<String, Set<String>> intGraph = new LinkedHashMap<>();
        Map<String, Set<String>> floatGraph = new LinkedHashMap<>();

        // Ensure all vars appear as nodes even if they don't interfere
        for (String v : func.localVars) {
            String t = func.typeMap.getOrDefault(v, "int");
            if (!t.endsWith("[]")) {
                if (t.equals("float")) {
                    floatGraph.computeIfAbsent(v, k -> new LinkedHashSet<>());
                } else {
                    intGraph.computeIfAbsent(v, k -> new LinkedHashSet<>());
                }
            }
        }
        for (String[] p : func.params) {
            String t = p[1];
            if (t.equals("float")) {
                floatGraph.computeIfAbsent(p[0], k -> new LinkedHashSet<>());
            } else {
                intGraph.computeIfAbsent(p[0], k -> new LinkedHashSet<>());
            }
        }

        for (DataFlowAnalyzer.Instruction inst : func.instructions) {
            addInterferences(intGraph, floatGraph, inst.in, func);
            addInterferences(intGraph, floatGraph, inst.out, func);
        }
        return List.of(intGraph, floatGraph);
    }

    private static void addInterferences(Map<String, Set<String>> intGraph,
                                          Map<String, Set<String>> floatGraph,
                                          Set<String> vars,
                                          DataFlowAnalyzer.FunctionDa func) {
        List<String> list = new ArrayList<>(vars);
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                String a = list.get(i);
                String b = list.get(j);
                String ta = func.typeMap.getOrDefault(a, DataFlowAnalyzer.globalTypeMap.getOrDefault(a, "int"));
                String tb = func.typeMap.getOrDefault(b, DataFlowAnalyzer.globalTypeMap.getOrDefault(b, "int"));
                if (ta.endsWith("[]") || tb.endsWith("[]")) {
                    continue;
                }
                if (ta.equals(tb)) {
                    if (ta.equals("float")) {
                        addEdge(floatGraph, a, b);
                    } else {
                        addEdge(intGraph, a, b);
                    }
                }
            }
        }
    }

    private static void addEdge(Map<String, Set<String>> graph, String a, String b) {
        graph.computeIfAbsent(a, k -> new LinkedHashSet<>()).add(b);
        graph.computeIfAbsent(b, k -> new LinkedHashSet<>()).add(a);
    }

    private static Deque<String> buildStack(Map<String, Set<String>> graph,
                                             Map<String, Integer> spillCosts,
                                             int k) {
        Map<String, Set<String>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> e : graph.entrySet()) {
            copy.put(e.getKey(), new LinkedHashSet<>(e.getValue()));
        }

        Deque<String> stack = new ArrayDeque<>();
        Map<String, Integer> degreeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> x : copy.entrySet()) {
            degreeMap.put(x.getKey(), x.getValue().size());
        }

        while (!degreeMap.isEmpty()) {

            String selected = null;
            for (Map.Entry<String, Integer> e : degreeMap.entrySet()) {
                if (e.getValue() < k) {
                    selected = e.getKey();
                    break;
                }
            }
            if (selected == null) {

                selected = Collections.min(degreeMap.keySet(),
                    Comparator.comparingInt(v -> spillCosts.getOrDefault(v, 0)));
            }
            stack.push(selected);
            for (String neighbor : copy.getOrDefault(selected, Collections.emptySet())) {
                copy.get(neighbor).remove(selected);
                degreeMap.computeIfPresent(neighbor, (kk, v) -> v - 1);
            }
            copy.remove(selected);
            degreeMap.remove(selected);
        }
        return stack;
    }

    public static Map<String, String> colorGraph(Map<String, Set<String>> graph,
                                                   Deque<String> stack,
                                                   String[] registers) {
        Map<String, String> alloc = new LinkedHashMap<>();
        while (!stack.isEmpty()) {
            String node = stack.pop();
            Set<String> usedRegs = new HashSet<>();
            for (String neighbor : graph.getOrDefault(node, Collections.emptySet())) {
                if (alloc.containsKey(neighbor) && !alloc.get(neighbor).equals("spilled")) {
                    usedRegs.add(alloc.get(neighbor));
                }
            }
            boolean assigned = false;
            for (String reg : registers) {
                if (!usedRegs.contains(reg)) {
                    alloc.put(node, reg);
                    assigned = true;
                    break;
                }
            }
            if (!assigned) {
                alloc.put(node, "spilled");
            }
        }
        return alloc;
    }



    public static List<String> instructionSelection(Map<String, String> alloc) {
        List<String> mipsLines = new ArrayList<>();


        mipsLines.add(".data");

        mipsLines.add("_newline: .asciiz \"\\n\"");
        mipsLines.add(".align 2");
        for (String s : DataFlowAnalyzer.staticVars) {
            String type = DataFlowAnalyzer.globalTypeMap.get(s);
            if (type != null && type.endsWith("[]")) {
                int size = DataFlowAnalyzer.globalArraySize.get(s);
                String baseType = type.replace("[]", "");
                if (baseType.equals("float")) {

                    StringBuilder sb = new StringBuilder();
                    sb.append(s).append(": .float");
                    for (int i = 0; i < size; i++) {
                        sb.append(i == 0 ? " " : ", ").append("0.0");
                    }
                    mipsLines.add(sb.toString());
                } else {
                    mipsLines.add(s + ": .space " + (size * 4));
                }
            } else if ("float".equals(type)) {
                mipsLines.add(s + ": .float 0.0");
            } else {
                mipsLines.add(s + ": .word 0");
            }
        }


        mipsLines.add(".text");

        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {

            Map<String, String> funcAlloc = new LinkedHashMap<>();
            for (String v : func.localVars) {
                if (alloc.containsKey(v)) {
                    funcAlloc.put(v, alloc.get(v));
                }
            }
            for (String[] p : func.params) {
                if (alloc.containsKey(p[0])) {
                    funcAlloc.put(p[0], alloc.get(p[0]));
                }
            }

            PrologueResult pr = emitPrologue(mipsLines, func, funcAlloc);

            for (DataFlowAnalyzer.Instruction inst : func.instructions) {
                String text = inst.text.trim();
  
                if (text.endsWith(":")) {
                    mipsLines.add(text);
                    continue;
                }

                String[] parts = DataFlowAnalyzer.splitParts(text);
                String op = parts[0];

                if (op.equals("assign")) {
                    emitAssign(mipsLines, parts, funcAlloc, pr, func);
                } else if (op.equals("add") || op.equals("sub") || op.equals("mult") || op.equals("div") || op.equals("and") || op.equals("or")) {
                    emitBinop(mipsLines, parts, funcAlloc, pr, func);
                } else if (DataFlowAnalyzer.isBranch(op)) {
                    emitBranch(mipsLines, parts, funcAlloc, pr, func);
                } else if (op.equals("goto")) {
                    mipsLines.add("j " + parts[1]);
                } else if (op.equals("return")) {
                    emitReturn(mipsLines, parts, funcAlloc, pr, func);
                } else if (op.equals("call")) {
                    emitCall(mipsLines, parts, funcAlloc, pr, func);
                } else if (op.equals("callr")) {
                    emitCallr(mipsLines, parts, funcAlloc, pr, func);
                } else if (op.equals("array_store")) {
                    emitArrayStore(mipsLines, parts, funcAlloc, pr, func);
                } else if (op.equals("array_load")) {
                    emitArrayLoad(mipsLines, parts, funcAlloc, pr, func);
                }
            }
        }
        return mipsLines;
    }


    private static void emitAssign(List<String> mips, String[] parts,
                                    Map<String, String> alloc, PrologueResult pr,
                                    DataFlowAnalyzer.FunctionDa func) {
        String dst = parts[1], src = parts[2];
        String dstType = getType(dst, func);
        String srcType = DataFlowAnalyzer.isLiteral(src) ? (src.contains(".") ? "float" : "int") : getType(src, func);
        boolean dstFloat = dstType.equals("float");
        boolean srcFloat = srcType.equals("float");
        boolean needConvert = dstFloat && !srcFloat;

        if (DataFlowAnalyzer.isLiteral(src)) {
            if (needConvert) {
                mips.add("li $t8, " + src);
                mips.add("mtc1 $t8, $f16");
                mips.add("cvt.s.w $f16, $f16");
                storeFReg(mips, "$f16", dst, alloc, pr);
            } else if (dstFloat) {
                String tmp = "$f16";
                mips.add("li.s " + tmp + ", " + src);
                storeFReg(mips, tmp, dst, alloc, pr);
            } else {
                String tmp = "$t8";
                mips.add("li " + tmp + ", " + src);
                storeIReg(mips, tmp, dst, alloc, pr);
            }
        } else if (needConvert) {
    
            String tmp = "$t8";
            loadIReg(mips, tmp, src, alloc, pr);
            mips.add("mtc1 " + tmp + ", $f16");
            mips.add("cvt.s.w $f16, $f16");
            storeFReg(mips, "$f16", dst, alloc, pr);
        } else if (dstFloat) {
            String tmp = "$f16";
            loadFReg(mips, tmp, src, alloc, pr);
            storeFReg(mips, tmp, dst, alloc, pr);
        } else {
            String tmp = "$t8";
            loadIReg(mips, tmp, src, alloc, pr);
            storeIReg(mips, tmp, dst, alloc, pr);
        }
    }



    private static void emitBinop(List<String> mips, String[] parts,
                                   Map<String, String> alloc, PrologueResult pr,
                                   DataFlowAnalyzer.FunctionDa func) {

        String op = parts[0];
        String src1 = parts[1], src2 = parts[2], dst = parts[3];

        String dstType = getType(dst, func);
        String src1Type = DataFlowAnalyzer.isLiteral(src1) ? (src1.contains(".") ? "float" : "int") : getType(src1, func);
        String src2Type = DataFlowAnalyzer.isLiteral(src2) ? (src2.contains(".") ? "float" : "int") : getType(src2, func);

        boolean dstFloat = dstType.equals("float");

        if (dstFloat || src1Type.equals("float") || src2Type.equals("float")) {
  
            String fs1 = "$f16", fs2 = "$f17";
            loadOperandFloat(mips, fs1, src1, src1Type, alloc, pr);
            loadOperandFloat(mips, fs2, src2, src2Type, alloc, pr);

            String fop;
            switch (op) {
                case "add":  fop = "add.s"; break;
                case "sub":  fop = "sub.s"; break;
                case "mult": fop = "mul.s"; break;
                case "div":  fop = "div.s"; break;
                default:     fop = "add.s"; break; 
            }
            mips.add(fop + " $f18, " + fs1 + ", " + fs2);
            storeFReg(mips, "$f18", dst, alloc, pr);
        } else {

            String r1 = "$t8", r2 = "$t9";
            loadOperandInt(mips, r1, src1, alloc, pr);
            loadOperandInt(mips, r2, src2, alloc, pr);

            String mipsOp;
            switch (op) {
                case "add":  mipsOp = "addu"; break;
                case "sub":  mipsOp = "subu"; break;
                case "mult": mipsOp = "mul"; break;
                case "div":  mipsOp = "div"; break;
                case "and":  mipsOp = "and"; break;
                case "or":   mipsOp = "or";  break;
                default:     mipsOp = "add"; break;
            }
            mips.add(mipsOp + " $v1, " + r1 + ", " + r2);
            storeIReg(mips, "$v1", dst, alloc, pr);
        }
    }

   

    private static void emitBranch(List<String> mips, String[] parts,
                                    Map<String, String> alloc, PrologueResult pr,
                                    DataFlowAnalyzer.FunctionDa func) {

        String op = parts[0];
        String src1 = parts[1], src2 = parts[2], label = parts[3];

        String src1Type = DataFlowAnalyzer.isLiteral(src1) ? (src1.contains(".") ? "float" : "int") : getType(src1, func);
        String src2Type = DataFlowAnalyzer.isLiteral(src2) ? (src2.contains(".") ? "float" : "int") : getType(src2, func);

        boolean isFloat = src1Type.equals("float") || src2Type.equals("float");

        if (isFloat) {
            String fs1 = "$f16", fs2 = "$f17";
            loadOperandFloat(mips, fs1, src1, src1Type, alloc, pr);
            loadOperandFloat(mips, fs2, src2, src2Type, alloc, pr);

  
            switch (op) {
                case "breq":
                    mips.add("c.eq.s " + fs1 + ", " + fs2);
                    mips.add("bc1t " + label);
                    break;
                case "brneq":
                    mips.add("c.eq.s " + fs1 + ", " + fs2);
                    mips.add("bc1f " + label);
                    break;
                case "brlt":
                    mips.add("c.lt.s " + fs1 + ", " + fs2);
                    mips.add("bc1t " + label);
                    break;
                case "brgt":
        
                    mips.add("c.lt.s " + fs2 + ", " + fs1);
                    mips.add("bc1t " + label);
                    break;
                case "brleq":
                    mips.add("c.le.s " + fs1 + ", " + fs2);
                    mips.add("bc1t " + label);
                    break;
                case "brgeq":

                    mips.add("c.le.s " + fs2 + ", " + fs1);
                    mips.add("bc1t " + label);
                    break;
            }
        } else {
            String r1 = "$t8", r2 = "$t9";
            loadOperandInt(mips, r1, src1, alloc, pr);
            loadOperandInt(mips, r2, src2, alloc, pr);

            String mipsOp;
            switch (op) {
                case "breq":  mipsOp = "beq"; break;
                case "brneq": mipsOp = "bne"; break;
                case "brlt":  mipsOp = "blt"; break;
                case "brgt":  mipsOp = "bgt"; break;
                case "brleq": mipsOp = "ble"; break;
                case "brgeq": mipsOp = "bge"; break;
                default:      mipsOp = "beq"; break;
            }
            mips.add(mipsOp + " " + r1 + ", " + r2 + ", " + label);
        }
    }



    private static void emitReturn(List<String> mips, String[] parts,
                                    Map<String, String> alloc, PrologueResult pr,
                                    DataFlowAnalyzer.FunctionDa func) {
        if (parts.length > 1 && !parts[1].isEmpty()) {
            String retVal = parts[1];
            boolean retFloat = func.retType.equals("float");
            if (DataFlowAnalyzer.isLiteral(retVal)) {
                if (retFloat) {
                    if (retVal.contains(".")) {
                        mips.add("li.s $f0, " + retVal);
                    } else {
                        mips.add("li $t8, " + retVal);
                        mips.add("mtc1 $t8, $f0");
                        mips.add("cvt.s.w $f0, $f0");
                    }
                } else {
                    mips.add("li $v0, " + retVal);
                }
            } else {
                String valType = getType(retVal, func);
                if (retFloat) {
                    if (valType.equals("float")) {
                        loadFReg(mips, "$f0", retVal, alloc, pr);
                    } else {
                        loadIReg(mips, "$t8", retVal, alloc, pr);
                        mips.add("mtc1 $t8, $f0");
                        mips.add("cvt.s.w $f0, $f0");
                    }
                } else {
                    loadIReg(mips, "$v0", retVal, alloc, pr);
                }
            }
        }
        emitEpilogue(mips, pr.frameSize, pr.usedRegs);
    }



    private static void emitCall(List<String> mips, String[] parts,
                                  Map<String, String> alloc, PrologueResult pr,
                                  DataFlowAnalyzer.FunctionDa func) {

        String funcName = parts[1];
        List<String> args = new ArrayList<>();
        for (int i = 2; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                args.add(parts[i]);
            }
        }

        if (funcName.equals("printi")) {
            if (!args.isEmpty()) {
                loadOperandInt(mips, "$a0", args.get(0), alloc, pr);
            }
            mips.add("li $v0, 1");
            mips.add("syscall");

            mips.add("li $v0, 11");
            mips.add("li $a0, 10");
            mips.add("syscall");
            return;
        }
        if (funcName.equals("printf")) {
            if (!args.isEmpty()) {
                String argType = DataFlowAnalyzer.isLiteral(args.get(0))
                    ? (args.get(0).contains(".") ? "float" : "int")
                    : getType(args.get(0), func);
                loadOperandFloat(mips, "$f12", args.get(0), argType, alloc, pr);
            }
            mips.add("li $v0, 2");
            mips.add("syscall");
            mips.add("li $v0, 11");
            mips.add("li $a0, 10");
            mips.add("syscall");
            return;
        }
        if (funcName.equals("readi")) {
            mips.add("li $v0, 5");
            mips.add("syscall");
            return;
        }
        if (funcName.equals("readf")) {
            mips.add("li $v0, 6");
            mips.add("syscall");
            return;
        }
        if (funcName.equals("not")) {
            if (!args.isEmpty()) {
                loadOperandInt(mips, "$t8", args.get(0), alloc, pr);
                mips.add("sltiu $v0, $t8, 1");
            }
            return;
        }
        if (funcName.equals("exit")) {
            if (!args.isEmpty()) {
                loadOperandInt(mips, "$a0", args.get(0), alloc, pr);
            }
            mips.add("li $v0, 17");
            mips.add("syscall");
            return;
        }


        passArgs(mips, args, alloc, pr, func);
        mips.add("jal " + funcName);
    }

    private static void emitCallr(List<String> mips, String[] parts,
                                    Map<String, String> alloc, PrologueResult pr,
                                    DataFlowAnalyzer.FunctionDa func) {

        String dst = parts[1];
        String funcName = parts[2];
        List<String> args = new ArrayList<>();
        for (int i = 3; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                args.add(parts[i]);
            }
        }

        String dstType = getType(dst, func);
        boolean dstFloat = dstType.equals("float");


        if (funcName.equals("readi")) {
            mips.add("li $v0, 5");
            mips.add("syscall");
            storeIReg(mips, "$v0", dst, alloc, pr);
            return;
        }
        if (funcName.equals("readf")) {
            mips.add("li $v0, 6");
            mips.add("syscall");
            storeFReg(mips, "$f0", dst, alloc, pr);
            return;
        }
        if (funcName.equals("not")) {
            if (!args.isEmpty()) {
                loadOperandInt(mips, "$t8", args.get(0), alloc, pr);
                mips.add("sltiu $v0, $t8, 1");
            }
            storeIReg(mips, "$v0", dst, alloc, pr);
            return;
        }


        passArgs(mips, args, alloc, pr, func);
        mips.add("jal " + funcName);


        if (dstFloat) {
            storeFReg(mips, "$f0", dst, alloc, pr);
        } else {
            storeIReg(mips, "$v0", dst, alloc, pr);
        }
    }

    private static void passArgs(List<String> mips, List<String> args,
                                  Map<String, String> alloc, PrologueResult pr,
                                  DataFlowAnalyzer.FunctionDa func) {

        int intArgIdx = 0, floatArgIdx = 0;
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            String argType = DataFlowAnalyzer.isLiteral(arg)
                ? (arg.contains(".") ? "float" : "int")
                : getType(arg, func);

            if (argType.equals("float")) {
                if (floatArgIdx < 4) {
                    loadOperandFloat(mips, FLOAT_ARGS[floatArgIdx], arg, argType, alloc, pr);
                    floatArgIdx++;
                } else {

                    loadOperandFloat(mips, "$f16", arg, argType, alloc, pr);
                    int stackIdx = i - 4; 
                    mips.add("s.s $f16, " + (stackIdx * 4) + "($sp)");
                }
            } else {
                if (intArgIdx < 4) {
                    loadOperandInt(mips, INT_ARGS[intArgIdx], arg, alloc, pr);
                    intArgIdx++;
                } else {
                    loadOperandInt(mips, "$t8", arg, alloc, pr);
                    int stackIdx = i - 4;
                    mips.add("sw $t8, " + (stackIdx * 4) + "($sp)");
                }
            }
        }
    }



    private static void emitArrayStore(List<String> mips, String[] parts,
                                        Map<String, String> alloc, PrologueResult pr,
                                        DataFlowAnalyzer.FunctionDa func) {

        String arrName = parts[1], index = parts[2], value = parts[3];
        String arrType = getType(arrName, func);
        String baseType = arrType.replace("[]", "");
        boolean isFloat = baseType.equals("float");
        boolean isGlobal = DataFlowAnalyzer.globalTypeMap.containsKey(arrName);


        if (isGlobal) {
            mips.add("la $t8, " + arrName);
        } else if (func.localArrays.contains(arrName)) {
            int off = pr.varOffset.getOrDefault(arrName, 0);
            mips.add("addiu $t8, $sp, " + off);
        } else {
            loadIReg(mips, "$t8", arrName, alloc, pr);
        }


        if (DataFlowAnalyzer.isLiteral(index)) {
            int idx = Integer.parseInt(index);
            mips.add("addiu $t8, $t8, " + (idx * 4));
        } else {
            loadIReg(mips, "$t9", index, alloc, pr);
            mips.add("sll $t9, $t9, 2");
            mips.add("add $t8, $t8, $t9");
        }


        if (isFloat) {
            String valType = DataFlowAnalyzer.isLiteral(value)
                ? (value.contains(".") ? "float" : "int") : getType(value, func);
            loadOperandFloat(mips, "$f16", value, valType, alloc, pr);
            mips.add("s.s $f16, 0($t8)");
        } else {
            loadOperandInt(mips, "$t9", value, alloc, pr);
            mips.add("sw $t9, 0($t8)");
        }
    }



    private static void emitArrayLoad(List<String> mips, String[] parts,
                                       Map<String, String> alloc, PrologueResult pr,
                                       DataFlowAnalyzer.FunctionDa func) {

        String dst = parts[1], arrName = parts[2], index = parts[3];
        String arrType = getType(arrName, func);
        String baseType = arrType.replace("[]", "");
        boolean isFloat = baseType.equals("float");
        boolean isGlobal = DataFlowAnalyzer.globalTypeMap.containsKey(arrName);


        if (isGlobal) {
            mips.add("la $t8, " + arrName);
        } else {
            int off = pr.varOffset.getOrDefault(arrName, 0);
            mips.add("addiu $t8, $sp, " + off);
        }


        if (DataFlowAnalyzer.isLiteral(index)) {
            int idx = Integer.parseInt(index);
            mips.add("addiu $t8, $t8, " + (idx * 4));
        } else {
            loadIReg(mips, "$t9", index, alloc, pr);
            mips.add("sll $t9, $t9, 2");
            mips.add("add $t8, $t8, $t9");
        }


        if (isFloat) {
            mips.add("l.s $f16, 0($t8)");
            storeFReg(mips, "$f16", dst, alloc, pr);
        } else {
            mips.add("lw $t9, 0($t8)");
            storeIReg(mips, "$t9", dst, alloc, pr);
        }
    }



    private static boolean isGlobal(String var) {
        return DataFlowAnalyzer.globalTypeMap.containsKey(var);
    }

    private static String getReg(String var, Map<String, String> alloc) {
        String r = alloc.get(var);
        if (r == null || r.equals("spilled")) {
            return null;
        }
        return r;
    }

    private static void loadIReg(List<String> mips, String targetReg, String var,
                                  Map<String, String> alloc, PrologueResult pr) {
        if (DataFlowAnalyzer.isLiteral(var)) {
            mips.add("li " + targetReg + ", " + var);
            return;
        }
        String reg = getReg(var, alloc);
        if (reg != null && !isGlobal(var)) {
            if (!reg.equals(targetReg)) {
                mips.add("move " + targetReg + ", " + reg);
            }
        } else if (isGlobal(var)) {
            mips.add("lw " + targetReg + ", " + var);
        } else {
            Integer off = pr.varOffset.get(var);
            if (off != null) {
                mips.add("lw " + targetReg + ", " + off + "($sp)");
            }
        }
    }


    private static void storeIReg(List<String> mips, String sourceReg, String var,
                                   Map<String, String> alloc, PrologueResult pr) {
        String reg = getReg(var, alloc);
        if (reg != null && !isGlobal(var)) {
            if (!reg.equals(sourceReg)) {
                mips.add("move " + reg + ", " + sourceReg);
            }
        } else if (isGlobal(var)) {
            mips.add("sw " + sourceReg + ", " + var);
        } else {
            Integer off = pr.varOffset.get(var);
            if (off != null) {
                mips.add("sw " + sourceReg + ", " + off + "($sp)");
            }
        }
    }

 
    private static void loadFReg(List<String> mips, String targetReg, String var,
                                  Map<String, String> alloc, PrologueResult pr) {
        if (DataFlowAnalyzer.isLiteral(var)) {
            mips.add("li.s " + targetReg + ", " + var);
            return;
        }
        String reg = getReg(var, alloc);
        if (reg != null && !isGlobal(var)) {
            if (!reg.equals(targetReg)) {
                mips.add("mov.s " + targetReg + ", " + reg);
            }
        } else if (isGlobal(var)) {
            mips.add("l.s " + targetReg + ", " + var);
        } else {
            Integer off = pr.varOffset.get(var);
            if (off != null) {
                mips.add("l.s " + targetReg + ", " + off + "($sp)");
            }
        }
    }


    private static void storeFReg(List<String> mips, String sourceReg, String var,
                                   Map<String, String> alloc, PrologueResult pr) {
        String reg = getReg(var, alloc);
        if (reg != null && !isGlobal(var)) {
            if (!reg.equals(sourceReg)) {
                mips.add("mov.s " + reg + ", " + sourceReg);
            }
        } else if (isGlobal(var)) {
            mips.add("s.s " + sourceReg + ", " + var);
        } else {
            Integer off = pr.varOffset.get(var);
            if (off != null) {
                mips.add("s.s " + sourceReg + ", " + off + "($sp)");
            }
        }
    }

    /** Load an operand (literal or var) into an int register */
    private static void loadOperandInt(List<String> mips, String targetReg, String operand,
                                        Map<String, String> alloc, PrologueResult pr) {
        if (DataFlowAnalyzer.isLiteral(operand)) {
            mips.add("li " + targetReg + ", " + operand);
        } else {
            loadIReg(mips, targetReg, operand, alloc, pr);
        }
    }


    private static void loadOperandFloat(List<String> mips, String targetReg, String operand,
                                          String operandType, Map<String, String> alloc,
                                          PrologueResult pr) {
        if (DataFlowAnalyzer.isLiteral(operand)) {
            if (operand.contains(".")) {
                mips.add("li.s " + targetReg + ", " + operand);
            } else {
        
                mips.add("li $t8, " + operand);
                mips.add("mtc1 $t8, " + targetReg);
                mips.add("cvt.s.w " + targetReg + ", " + targetReg);
            }
        } else if (operandType.equals("float")) {
            loadFReg(mips, targetReg, operand, alloc, pr);
        } else {

            loadIReg(mips, "$t8", operand, alloc, pr);
            mips.add("mtc1 $t8, " + targetReg);
            mips.add("cvt.s.w " + targetReg + ", " + targetReg);
        }
    }

    private static String getType(String name, DataFlowAnalyzer.FunctionDa func) {
        String t = func.typeMap.get(name);
        if (t != null) {
            return t;
        }
        t = DataFlowAnalyzer.globalTypeMap.get(name);
        if (t != null) {
            return t;
        }
        return "int";
    }



    public static class PrologueResult {
        public int frameSize;
        public Map<String, Integer> varOffset;
        public Set<String> usedRegs;
        public int outgoingArgOffset;
    }

    public static PrologueResult emitPrologue(List<String> mipsLines,
                                               DataFlowAnalyzer.FunctionDa func,
                                               Map<String, String> alloc) {
        String[] allIntRegs = {"$s0", "$s1", "$s2", "$t0", "$t1", "$t2"};
        String[] allFloatRegs = {"$f4", "$f5", "$f6", "$f20", "$f21", "$f22"};
        String[] intArgRegs = {"$a0", "$a1", "$a2", "$a3"};
        String[] floatArgRegs = {"$f12", "$f13", "$f14", "$f15"};

        Set<String> usedRegs = new LinkedHashSet<>();
        for (String var : func.localVars) {
            String reg = alloc.get(var);
            if (reg != null && !reg.equals("spilled")) {
                usedRegs.add(reg);
            }
        }
        for (String[] p : func.params) {
            String reg = alloc.get(p[0]);
            if (reg != null && !reg.equals("spilled")) {
                usedRegs.add(reg);
            }
        }


        int maxArgs = 0;
        boolean hasCalls = false;
        for (DataFlowAnalyzer.Instruction inst : func.instructions) {
            String[] parts = DataFlowAnalyzer.splitParts(inst.text.trim());
            if (parts[0].equals("call")) {
                hasCalls = true;
                maxArgs = Math.max(maxArgs, parts.length - 2);
            } else if (parts[0].equals("callr")) {
                hasCalls = true;
                maxArgs = Math.max(maxArgs, parts.length - 3);
            }
        }


        int frameSize = 4; 
 
        for (String r : allIntRegs) {
            if (usedRegs.contains(r)) {
                frameSize += 4;
            }
        }
        for (String r : allFloatRegs) {
            if (usedRegs.contains(r)) {
                frameSize += 4;
            }
        }

        for (String[] p : func.params) {
            String reg = alloc.get(p[0]);
            if (reg == null || reg.equals("spilled")) {
                frameSize += 4;
            }
        }

        for (String v : func.localVars) {
            if (!func.localArrays.contains(v)) {
                String reg = alloc.get(v);
                if (reg == null || reg.equals("spilled")) {
                    frameSize += 4;
                }
            }
        }

        for (String arr : func.localArrays) {
            frameSize += func.arraySize.get(arr) * 4;
        }

        int stackArgs = Math.max(0, maxArgs - 4);
        frameSize += stackArgs * 4;
   
        if (frameSize % 8 != 0) {
            frameSize += 4;
        }


        if (func.name.equals("main")) {
            mipsLines.add(".globl main");
        }
        mipsLines.add(func.name + ":");
        mipsLines.add("addiu $sp, $sp, -" + frameSize);
        mipsLines.add("sw $ra, " + (frameSize - 4) + "($sp)");

        Map<String, Integer> varOffset = new LinkedHashMap<>();
        int offset = frameSize - 8;


        for (String r : allIntRegs) {
            if (usedRegs.contains(r)) {
                mipsLines.add("sw " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }
        for (String r : allFloatRegs) {
            if (usedRegs.contains(r)) {
                mipsLines.add("s.s " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }

    
        int intArgIdx = 0, floatArgIdx = 0;
        for (int i = 0; i < func.params.size(); i++) {
            String pName = func.params.get(i)[0];
            String pType = func.params.get(i)[1];
            String reg = alloc.get(pName);
            boolean isFloat = pType.equals("float");

            if (isFloat) {
                if (floatArgIdx < 4) {
                    if (reg != null && !reg.equals("spilled")) {
                        mipsLines.add("mov.s " + reg + ", " + floatArgRegs[floatArgIdx]);
                    } else {
                        varOffset.put(pName, offset);
                        mipsLines.add("s.s " + floatArgRegs[floatArgIdx] + ", " + offset + "($sp)");
                        offset -= 4;
                    }
                    floatArgIdx++;
                } else {
            
                    if (reg != null && !reg.equals("spilled")) {
                        mipsLines.add("l.s " + reg + ", " + (frameSize + i * 4) + "($sp)");
                    } else {
                        varOffset.put(pName, offset);
                        mipsLines.add("l.s $f16, " + (frameSize + i * 4) + "($sp)");
                        mipsLines.add("s.s $f16, " + offset + "($sp)");
                        offset -= 4;
                    }
                }
            } else {
                if (intArgIdx < 4) {
                    if (reg != null && !reg.equals("spilled")) {
                        mipsLines.add("move " + reg + ", " + intArgRegs[intArgIdx]);
                    } else {
                        varOffset.put(pName, offset);
                        mipsLines.add("sw " + intArgRegs[intArgIdx] + ", " + offset + "($sp)");
                        offset -= 4;
                    }
                    intArgIdx++;
                } else {
                    if (reg != null && !reg.equals("spilled")) {
                        mipsLines.add("lw " + reg + ", " + (frameSize + i * 4) + "($sp)");
                    } else {
                        varOffset.put(pName, offset);
                        mipsLines.add("lw $t8, " + (frameSize + i * 4) + "($sp)");
                        mipsLines.add("sw $t8, " + offset + "($sp)");
                        offset -= 4;
                    }
                }
            }
        }


        for (String v : func.localVars) {
            if (!func.localArrays.contains(v)) {
                String reg = alloc.get(v);
                if (reg == null || reg.equals("spilled")) {
                    varOffset.put(v, offset);
                    offset -= 4;
                }
            }
        }


        for (String arr : func.localArrays) {
            int size = func.arraySize.get(arr);
            offset -= (size - 1) * 4; 
            varOffset.put(arr, offset);
            offset -= 4;
        }

        PrologueResult result = new PrologueResult();
        result.frameSize = frameSize;
        result.varOffset = varOffset;
        result.usedRegs = usedRegs;
        return result;
    }

    public static void emitEpilogue(List<String> mipsLines, int frameSize, Set<String> usedRegs) {
        String[] allIntRegs = {"$s0", "$s1", "$s2", "$t0", "$t1", "$t2"};
        String[] allFloatRegs = {"$f4", "$f5", "$f6", "$f20", "$f21", "$f22"};

        int offset = frameSize - 8;

        for (String r : allIntRegs) {
            if (usedRegs.contains(r)) {
                mipsLines.add("lw " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }
        for (String r : allFloatRegs) {
            if (usedRegs.contains(r)) {
                mipsLines.add("l.s " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }

        mipsLines.add("lw $ra, " + (frameSize - 4) + "($sp)");
        mipsLines.add("addiu $sp, $sp, " + frameSize);
        mipsLines.add("jr $ra");
    }
}