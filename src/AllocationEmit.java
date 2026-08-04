import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AllocationEmit {

    private static final String[] INT_REGS = {"$s0", "$s1", "$s2", "$t0", "$t1", "$t2"};
    private static final String[] FLOAT_REGS = {"$f4", "$f5", "$f6", "$f20", "$f21", "$f22"};
    private static final String[] INT_ARGS = {"$a0", "$a1", "$a2", "$a3"};
    private static final String[] FLOAT_ARGS = {"$f12", "$f13", "$f14", "$f15"};

    private static Map<String, Map<String, String>> correctedTypes = new HashMap<>();
    private static boolean preprocessed = false;

    public static void preprocess() {
        if (preprocessed) return;
        preprocessed = true;
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            Map<String, String> cMap = new HashMap<>();
            correctedTypes.put(func.name, cMap);
            boolean changed = true;
            while (changed) {
                changed = false;
                for (DataFlowAnalyzer.Instruction inst : func.instructions) {
                    String text = inst.text.trim();
                    if (text.endsWith(":")) continue;
                    String[] parts = DataFlowAnalyzer.splitParts(text);
                    String op = parts[0];
                    if (op.equals("assign")) {
                        String dst = parts[1], src = parts[2];
                        String srcType = DataFlowAnalyzer.isLiteral(src) ? (src.contains(".") ? "float" : "int") : getType(src, func);
                        if (srcType.equals("float") && !getType(dst, func).equals("float")) {
                            cMap.put(dst, "float"); changed = true;
                        }
                    } else if (op.equals("add") || op.equals("sub") || op.equals("mult") || op.equals("div")) {
                        String src1 = parts[1], src2 = parts[2], dst = parts[3];
                        String s1t = DataFlowAnalyzer.isLiteral(src1) ? (src1.contains(".") ? "float" : "int") : getType(src1, func);
                        String s2t = DataFlowAnalyzer.isLiteral(src2) ? (src2.contains(".") ? "float" : "int") : getType(src2, func);
                        if ((s1t.equals("float") || s2t.equals("float")) && !getType(dst, func).equals("float")) {
                            cMap.put(dst, "float"); changed = true;
                        }
                    } else if (op.equals("callr")) {
                        String dst = parts[1], fn = parts[2];
                        DataFlowAnalyzer.FunctionDa callee = findFunc(fn);
                        if (callee != null && callee.retType.equals("float") && !getType(dst, func).equals("float")) {
                            cMap.put(dst, "float"); changed = true;
                        } else if (fn.equals("readf") && !getType(dst, func).equals("float")) {
                            cMap.put(dst, "float"); changed = true;
                        }
                    } else if (op.equals("array_load")) {
                        String dst = parts[1], arr = parts[2];
                        if (getType(arr, func).contains("float") && !getType(dst, func).equals("float")) {
                            cMap.put(dst, "float"); changed = true;
                        }
                    }
                }
            }
        }
    }

    private static DataFlowAnalyzer.FunctionDa findFunc(String name) {
        for (DataFlowAnalyzer.FunctionDa f : DataFlowAnalyzer.functions) {
            if (f.name.equals(name)) return f;
        }
        return null;
    }

    public static Map<String, String> naiveAlloc() {
        preprocess();
        Map<String, String> alloc = new LinkedHashMap<>();
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            for (String var : func.localVars) alloc.put(var, "spilled");
            for (String[] param : func.params) alloc.put(param[0], "spilled");
        }
        return alloc;
    }

    public static Map<String, String> localBBAlloc() {
        preprocess();
        Map<String, String> alloc = new LinkedHashMap<>();
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            Set<String> assignedIntRegs = new LinkedHashSet<>();
            Set<String> assignedFloatRegs = new LinkedHashSet<>();
            for (DataFlowAnalyzer.BasicBlock bb : func.basicBlocks) {
                List<String> sortedVar = new ArrayList<>(bb.localSpillCosts.keySet());
                sortedVar.sort((a, b) -> bb.localSpillCosts.get(b) - bb.localSpillCosts.get(a));
                for (String var : sortedVar) {
                    if (alloc.containsKey(var)) continue;
                    String type = getType(var, func);
                    if (type.contains("[")) continue;
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
                    if (!found) alloc.put(var, "spilled");
                }
            }
        }
        return alloc;
    }

    public static Map<String, String> briggAlloc() {
        preprocess();
        Map<String, String> alloc = new LinkedHashMap<>();
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            List<Map<String, Set<String>>> graphList = buildGraphs(func);
            Map<String, Set<String>> intGraph = graphList.get(0);
            Map<String, Set<String>> floatGraph = graphList.get(1);
            Deque<String> intStack = buildStack(intGraph, func.globalSpillCosts, INT_REGS.length);
            Deque<String> floatStack = buildStack(floatGraph, func.globalSpillCosts, FLOAT_REGS.length);
            alloc.putAll(colorGraph(intGraph, intStack, INT_REGS));
            alloc.putAll(colorGraph(floatGraph, floatStack, FLOAT_REGS));
        }
        return alloc;
    }

            public static void exportGraphViz(Map<String, Set<String>> intGraph, Map<String, Set<String>> floatGraph, String fileName, Map<String, String> alloc, Map<String, Integer> spillCosts) {
                try (PrintWriter out = new PrintWriter(new FileWriter(fileName + ".color.gv"))) {
                    out.println("graph G {");
                    for (String node : intGraph.keySet()) {
                        String reg = alloc.getOrDefault(node, "spilled");
                        int cost = spillCosts.getOrDefault(node, 0);
                        out.println("  \"" + node + "\" [label=\"" + node + "\\ncost=" + cost + "\\n" + reg + "\"];");
                        for (String neighbor : intGraph.get(node)) {
                            if (node.compareTo(neighbor) < 0) out.println("  \"" + node + "\" -- \"" + neighbor + "\";");
                        }
                    }
                    for (String node : floatGraph.keySet()) {
                        String reg = alloc.getOrDefault(node, "spilled");
                        int cost = spillCosts.getOrDefault(node, 0);
                        out.println("  \"" + node + "\" [label=\"" + node + "\\ncost=" + cost + "\\n" + reg + "\"];");
                        for (String neighbor : floatGraph.get(node)) {
                            if (node.compareTo(neighbor) < 0) out.println("  \"" + node + "\" -- \"" + neighbor + "\";");
                        }
                    }
                    out.println("}");
                } catch (IOException e) {}
            }

    public static List<Map<String, Set<String>>> buildGraphs(DataFlowAnalyzer.FunctionDa func) {
        Map<String, Set<String>> intGraph = new LinkedHashMap<>();
        Map<String, Set<String>> floatGraph = new LinkedHashMap<>();
        for (String v : func.localVars) {
            String t = getType(v, func);
            if (!t.contains("[")) {
                if (t.equals("float")) floatGraph.computeIfAbsent(v, k -> new LinkedHashSet<>());
                else intGraph.computeIfAbsent(v, k -> new LinkedHashSet<>());
            }
        }
        for (String[] p : func.params) {
            if (p[1].equals("float")) floatGraph.computeIfAbsent(p[0], k -> new LinkedHashSet<>());
            else intGraph.computeIfAbsent(p[0], k -> new LinkedHashSet<>());
        }
        for (DataFlowAnalyzer.Instruction inst : func.instructions) {
            addInterferences(intGraph, floatGraph, inst.in, func);
            addInterferences(intGraph, floatGraph, inst.out, func);
            String[] parts = DataFlowAnalyzer.splitParts(inst.text.trim());
            String defVar = null;
            String op = parts[0];
            if (op.equals("assign")) defVar = parts[1];
            else if (op.equals("add") || op.equals("sub") || op.equals("mult") || op.equals("div") || op.equals("and") || op.equals("or")) defVar = parts[3];
            else if (op.equals("callr")) defVar = parts[1];
            else if (op.equals("array_load")) defVar = parts[1];
            if (defVar != null && !DataFlowAnalyzer.isLiteral(defVar)) {
                Set<String> outAndDef = new HashSet<>(inst.out);
                outAndDef.add(defVar);
                addInterferences(intGraph, floatGraph, outAndDef, func);
            }
        }
        return List.of(intGraph, floatGraph);
    }

    private static void addInterferences(Map<String, Set<String>> intGraph, Map<String, Set<String>> floatGraph, Set<String> vars, DataFlowAnalyzer.FunctionDa func) {
        List<String> list = new ArrayList<>(vars);
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                String a = list.get(i), b = list.get(j);
                String ta = getType(a, func), tb = getType(b, func);
                if (ta.contains("[") || tb.contains("[")) continue;
                if (ta.equals(tb)) {
                    if (ta.equals("float")) {
                        floatGraph.computeIfAbsent(a, k -> new LinkedHashSet<>()).add(b);
                        floatGraph.computeIfAbsent(b, k -> new LinkedHashSet<>()).add(a);
                    } else {
                        intGraph.computeIfAbsent(a, k -> new LinkedHashSet<>()).add(b);
                        intGraph.computeIfAbsent(b, k -> new LinkedHashSet<>()).add(a);
                    }
                }
            }
        }
    }

    private static Deque<String> buildStack(Map<String, Set<String>> graph, Map<String, Integer> spillCosts, int k) {
        Map<String, Set<String>> copy = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> e : graph.entrySet()) copy.put(e.getKey(), new LinkedHashSet<>(e.getValue()));
        Deque<String> stack = new ArrayDeque<>();
        Map<String, Integer> degreeMap = new LinkedHashMap<>();
        for (Map.Entry<String, Set<String>> x : copy.entrySet()) degreeMap.put(x.getKey(), x.getValue().size());
        while (!degreeMap.isEmpty()) {
            String selected = null;
            for (Map.Entry<String, Integer> e : degreeMap.entrySet()) {
                if (e.getValue() < k) { selected = e.getKey(); break; }
            }
            if (selected == null) selected = Collections.min(degreeMap.keySet(), Comparator.comparingInt(v -> spillCosts.getOrDefault(v, 0)));
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

    public static Map<String, String> colorGraph(Map<String, Set<String>> graph, Deque<String> stack, String[] registers) {
        Map<String, String> alloc = new LinkedHashMap<>();
        while (!stack.isEmpty()) {
            String node = stack.pop();
            Set<String> usedRegs = new HashSet<>();
            for (String neighbor : graph.getOrDefault(node, Collections.emptySet())) {
                if (alloc.containsKey(neighbor) && !alloc.get(neighbor).equals("spilled")) usedRegs.add(alloc.get(neighbor));
            }
            boolean assigned = false;
            for (String reg : registers) {
                if (!usedRegs.contains(reg)) { alloc.put(node, reg); assigned = true; break; }
            }
            if (!assigned) alloc.put(node, "spilled");
        }
        return alloc;
    }

    public static List<String> instructionSelection(Map<String, String> alloc) {
        preprocess();
        List<String> mips = new ArrayList<>();
        mips.add(".data");
        mips.add("_newline: .asciiz \"\\n\"");
        mips.add(".align 2");
        for (String s : DataFlowAnalyzer.staticVars) {
            String type = DataFlowAnalyzer.globalTypeMap.get(s);
            if (type != null && type.contains("[")) {
                int size = DataFlowAnalyzer.globalArraySize.get(s);
                if (type.contains("float")) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(s).append(": .float");
                    for (int i = 0; i < size; i++) sb.append(i == 0 ? " " : ", ").append("0.0");
                    mips.add(sb.toString());
                } else {
                    mips.add(s + ": .space " + (size * 4));
                }
            } else if ("float".equals(type)) {
                mips.add(s + ": .float 0.0");
            } else {
                mips.add(s + ": .word 0");
            }
        }
        mips.add(".text");
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            Map<String, String> funcAlloc = new LinkedHashMap<>();
            for (String v : func.localVars) if (alloc.containsKey(v)) funcAlloc.put(v, alloc.get(v));
            for (String[] p : func.params) if (alloc.containsKey(p[0])) funcAlloc.put(p[0], alloc.get(p[0]));
            PrologueResult pr = emitPrologue(mips, func, funcAlloc);
            for (DataFlowAnalyzer.Instruction inst : func.instructions) {
                String text = inst.text.trim();
                if (text.endsWith(":")) { mips.add(text); continue; }
                String[] parts = DataFlowAnalyzer.splitParts(text);
                String op = parts[0];
                if (op.equals("assign")) emitAssign(mips, parts, funcAlloc, pr, func);
                else if (op.equals("add") || op.equals("sub") || op.equals("mult") || op.equals("div") || op.equals("and") || op.equals("or")) emitBinop(mips, parts, funcAlloc, pr, func);
                else if (DataFlowAnalyzer.isBranch(op)) emitBranch(mips, parts, funcAlloc, pr, func);
                else if (op.equals("goto")) mips.add("j " + parts[1]);
                else if (op.equals("return")) emitReturn(mips, parts, funcAlloc, pr, func);
                else if (op.equals("call")) emitCall(mips, parts, funcAlloc, pr, func);
                else if (op.equals("callr")) emitCallr(mips, parts, funcAlloc, pr, func);
                else if (op.equals("array_store")) emitArrayStore(mips, parts, funcAlloc, pr, func);
                else if (op.equals("array_load")) emitArrayLoad(mips, parts, funcAlloc, pr, func);
            }
        }
        return mips;
    }

    private static void emitAssign(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        String dst = parts[1], src = parts[2];
        String dstType = getType(dst, func);
        String srcType = DataFlowAnalyzer.isLiteral(src) ? (src.contains(".") ? "float" : "int") : getType(src, func);
        boolean dstFloat = dstType.equals("float");
        boolean srcFloat = srcType.equals("float");
        if (DataFlowAnalyzer.isLiteral(src)) {
            if (dstFloat && !src.contains(".")) {
                mips.add("li $t8, " + src);
                mips.add("mtc1 $t8, $f16");
                mips.add("cvt.s.w $f16, $f16");
                storeFReg(mips, "$f16", dst, alloc, pr, func);
            } else if (dstFloat) {
                mips.add("li.s $f16, " + src);
                storeFReg(mips, "$f16", dst, alloc, pr, func);
            } else {
                mips.add("li $t8, " + src);
                storeIReg(mips, "$t8", dst, alloc, pr, func);
            }
        } else if (dstFloat && !srcFloat) {
            loadIReg(mips, "$t8", src, alloc, pr, func);
            mips.add("mtc1 $t8, $f16");
            mips.add("cvt.s.w $f16, $f16");
            storeFReg(mips, "$f16", dst, alloc, pr, func);
        } else if (dstFloat) {
            loadFReg(mips, "$f16", src, alloc, pr, func);
            storeFReg(mips, "$f16", dst, alloc, pr, func);
        } else {
            loadIReg(mips, "$t8", src, alloc, pr, func);
            storeIReg(mips, "$t8", dst, alloc, pr, func);
        }
    }

    private static void emitBinop(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        String op = parts[0], src1 = parts[1], src2 = parts[2], dst = parts[3];
        String dstType = getType(dst, func);
        String s1t = DataFlowAnalyzer.isLiteral(src1) ? (src1.contains(".") ? "float" : "int") : getType(src1, func);
        String s2t = DataFlowAnalyzer.isLiteral(src2) ? (src2.contains(".") ? "float" : "int") : getType(src2, func);
        if ((op.equals("and") || op.equals("or"))) {
            loadOperandInt(mips, "$t8", src1, alloc, pr, func);
            loadOperandInt(mips, "$t9", src2, alloc, pr, func);
            mips.add(op + " $v1, $t8, $t9");
            storeIReg(mips, "$v1", dst, alloc, pr, func);
        } else if (dstType.equals("float") || s1t.equals("float") || s2t.equals("float")) {
            loadOperandFloat(mips, "$f16", src1, s1t, alloc, pr, func);
            loadOperandFloat(mips, "$f17", src2, s2t, alloc, pr, func);
            String fop;
            switch (op) {
                case "add": fop = "add.s"; break;
                case "sub": fop = "sub.s"; break;
                case "mult": fop = "mul.s"; break;
                case "div": fop = "div.s"; break;
                default: fop = "add.s"; break;
            }
            mips.add(fop + " $f18, $f16, $f17");
            storeFReg(mips, "$f18", dst, alloc, pr, func);
        } else {
            loadOperandInt(mips, "$t8", src1, alloc, pr, func);
            loadOperandInt(mips, "$t9", src2, alloc, pr, func);
            String mop;
            switch (op) {
                case "add": mop = "addu"; break;
                case "sub": mop = "subu"; break;
                case "mult": mop = "mul"; break;
                case "div": mop = "div"; break;
                default: mop = "addu"; break;
            }
            mips.add(mop + " $v1, $t8, $t9");
            storeIReg(mips, "$v1", dst, alloc, pr, func);
        }
    }

    private static void emitBranch(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        String op = parts[0], src1 = parts[1], src2 = parts[2], label = parts[3];
        String s1t = DataFlowAnalyzer.isLiteral(src1) ? (src1.contains(".") ? "float" : "int") : getType(src1, func);
        String s2t = DataFlowAnalyzer.isLiteral(src2) ? (src2.contains(".") ? "float" : "int") : getType(src2, func);
        if (s1t.equals("float") || s2t.equals("float")) {
            loadOperandFloat(mips, "$f16", src1, s1t, alloc, pr, func);
            loadOperandFloat(mips, "$f17", src2, s2t, alloc, pr, func);
            switch (op) {
                case "breq": mips.add("c.eq.s $f16, $f17"); mips.add("bc1t " + label); break;
                case "brneq": mips.add("c.eq.s $f16, $f17"); mips.add("bc1f " + label); break;
                case "brlt": mips.add("c.lt.s $f16, $f17"); mips.add("bc1t " + label); break;
                case "brgt": mips.add("c.lt.s $f17, $f16"); mips.add("bc1t " + label); break;
                case "brleq": mips.add("c.le.s $f16, $f17"); mips.add("bc1t " + label); break;
                case "brgeq": mips.add("c.le.s $f17, $f16"); mips.add("bc1t " + label); break;
            }
        } else {
            loadOperandInt(mips, "$t8", src1, alloc, pr, func);
            loadOperandInt(mips, "$t9", src2, alloc, pr, func);
            String mop;
            switch (op) {
                case "breq": mop = "beq"; break;
                case "brneq": mop = "bne"; break;
                case "brlt": mop = "blt"; break;
                case "brgt": mop = "bgt"; break;
                case "brleq": mop = "ble"; break;
                case "brgeq": mop = "bge"; break;
                default: mop = "beq"; break;
            }
            mips.add(mop + " $t8, $t9, " + label);
        }
    }

    private static void emitReturn(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
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
                        loadFReg(mips, "$f0", retVal, alloc, pr, func);
                    } else {
                        loadIReg(mips, "$t8", retVal, alloc, pr, func);
                        mips.add("mtc1 $t8, $f0");
                        mips.add("cvt.s.w $f0, $f0");
                    }
                } else {
                    loadIReg(mips, "$v0", retVal, alloc, pr, func);
                }
            }
        }
        emitEpilogue(mips, pr.frameSize, pr.usedRegs);
    }

    private static void emitCall(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        String funcName = parts[1];
        List<String> args = new ArrayList<>();
        for (int i = 2; i < parts.length; i++) if (!parts[i].isEmpty()) args.add(parts[i]);
        if (isBuiltin(funcName)) {
            handleBuiltins(mips, funcName, args, alloc, pr, func, null);
            return;
        }
        passArgs(mips, funcName, args, alloc, pr, func);
        mips.add("jal " + funcName);
    }

    private static void emitCallr(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        String dst = parts[1], funcName = parts[2];
        List<String> args = new ArrayList<>();
        for (int i = 3; i < parts.length; i++) if (!parts[i].isEmpty()) args.add(parts[i]);
        if (isBuiltin(funcName)) {
            handleBuiltins(mips, funcName, args, alloc, pr, func, dst);
            return;
        }
        passArgs(mips, funcName, args, alloc, pr, func);
        mips.add("jal " + funcName);
        if (getType(dst, func).equals("float")) storeFReg(mips, "$f0", dst, alloc, pr, func);
        else storeIReg(mips, "$v0", dst, alloc, pr, func);
    }

    private static boolean isBuiltin(String name) {
        return Set.of("printi", "printf", "readi", "readf", "not", "exit").contains(name);
    }

    private static void handleBuiltins(List<String> mips, String funcName, List<String> args, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func, String dst) {
        if (funcName.equals("printi")) {
            if (!args.isEmpty()) loadOperandInt(mips, "$a0", args.get(0), alloc, pr, func);
            mips.add("li $v0, 1");
            mips.add("syscall");
            mips.add("li $v0, 11");
            mips.add("li $a0, 10");
            mips.add("syscall");
        } else if (funcName.equals("printf")) {
            if (!args.isEmpty()) {
                String at = DataFlowAnalyzer.isLiteral(args.get(0)) ? (args.get(0).contains(".") ? "float" : "int") : getType(args.get(0), func);
                loadOperandFloat(mips, "$f12", args.get(0), at, alloc, pr, func);
            }
            mips.add("li $v0, 2");
            mips.add("syscall");
            mips.add("li $v0, 11");
            mips.add("li $a0, 10");
            mips.add("syscall");
        } else if (funcName.equals("readi")) {
            mips.add("li $v0, 5");
            mips.add("syscall");
            if (dst != null) storeIReg(mips, "$v0", dst, alloc, pr, func);
        } else if (funcName.equals("readf")) {
            mips.add("li $v0, 6");
            mips.add("syscall");
            if (dst != null) storeFReg(mips, "$f0", dst, alloc, pr, func);
        } else if (funcName.equals("not")) {
            if (!args.isEmpty()) {
                loadOperandInt(mips, "$t8", args.get(0), alloc, pr, func);
                mips.add("sltiu $v0, $t8, 1");
            }
            if (dst != null) storeIReg(mips, "$v0", dst, alloc, pr, func);
        } else if (funcName.equals("exit")) {
            if (!args.isEmpty()) loadOperandInt(mips, "$a0", args.get(0), alloc, pr, func);
            mips.add("li $v0, 17");
            mips.add("syscall");
        }
    }

    private static void passArgs(List<String> mips, String funcName, List<String> args, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        DataFlowAnalyzer.FunctionDa callee = findFunc(funcName);
        int intArgIdx = 0, floatArgIdx = 0, stackArgIdx = 0;
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            String argType = DataFlowAnalyzer.isLiteral(arg) ? (arg.contains(".") ? "float" : "int") : getType(arg, func);
            String expectedType = (callee != null && i < callee.params.size()) ? callee.params.get(i)[1] : argType;
            if (expectedType.equals("float")) {
                if (floatArgIdx < 4) {
                    loadOperandFloat(mips, FLOAT_ARGS[floatArgIdx], arg, argType, alloc, pr, func);
                    floatArgIdx++;
                } else {
                    loadOperandFloat(mips, "$f16", arg, argType, alloc, pr, func);
                    mips.add("s.s $f16, " + (stackArgIdx * 4) + "($sp)");
                    stackArgIdx++;
                }
            } else {
                if (intArgIdx < 4) {
                    loadOperandInt(mips, INT_ARGS[intArgIdx], arg, alloc, pr, func);
                    intArgIdx++;
                } else {
                    loadOperandInt(mips, "$t8", arg, alloc, pr, func);
                    mips.add("sw $t8, " + (stackArgIdx * 4) + "($sp)");
                    stackArgIdx++;
                }
            }
        }
    }

    private static void emitArrayStore(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        String arrName = parts[1], index = parts[2], value = parts[3];
        boolean isFloat = getType(arrName, func).contains("float");
        String valType = DataFlowAnalyzer.isLiteral(value) ? (value.contains(".") ? "float" : "int") : getType(value, func);
        boolean needConvert = isFloat && !valType.equals("float");

        if (isFloat) {
            loadOperandFloat(mips, "$f16", value, valType, alloc, pr, func);
        } else {
            loadOperandInt(mips, "$v1", value, alloc, pr, func);
        }

        computeArrayAddr(mips, arrName, index, alloc, pr, func);

        if (isFloat) {
            mips.add("s.s $f16, 0($t8)");
        } else {
            mips.add("sw $v1, 0($t8)");
        }
    }

    private static void emitArrayLoad(List<String> mips, String[] parts, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        String dst = parts[1], arrName = parts[2], index = parts[3];
        boolean isFloat = getType(arrName, func).contains("float");
        computeArrayAddr(mips, arrName, index, alloc, pr, func);
        if (isFloat) {
            mips.add("l.s $f16, 0($t8)");
            storeFReg(mips, "$f16", dst, alloc, pr, func);
        } else {
            mips.add("lw $v1, 0($t8)");
            storeIReg(mips, "$v1", dst, alloc, pr, func);
        }
    }

    private static void computeArrayAddr(List<String> mips, String arrName, String index, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        boolean isLocalArray = func.localArrays != null && func.localArrays.contains(arrName);
        boolean isGlobalArray = DataFlowAnalyzer.globalTypeMap.containsKey(arrName) && DataFlowAnalyzer.globalTypeMap.get(arrName).contains("[");

        if (isGlobalArray) {
            mips.add("la $t8, " + arrName);
        } else if (isLocalArray) {
            int off = pr.varOffset.getOrDefault(arrName, 0);
            mips.add("addiu $t8, $sp, " + off);
        } else {
            mips.add("la $t8, " + arrName);
        }

        if (DataFlowAnalyzer.isLiteral(index)) {
            int idxVal = Integer.parseInt(index);
            if (idxVal != 0) {
                mips.add("addiu $t8, $t8, " + (idxVal * 4));
            }
        } else {
            loadIReg(mips, "$t9", index, alloc, pr, func);
            mips.add("sll $t9, $t9, 2");
            mips.add("addu $t8, $t8, $t9");
        }
    }

    private static String getType(String name, DataFlowAnalyzer.FunctionDa func) {
        if (func != null && correctedTypes.containsKey(func.name) && correctedTypes.get(func.name).containsKey(name)) {
            return correctedTypes.get(func.name).get(name);
        }
        String t = func != null && func.typeMap != null ? func.typeMap.get(name) : null;
        if (t != null) return t;
        if (func != null && func.params != null) {
            for (String[] p : func.params) if (p[0].equals(name)) return p[1];
        }
        t = DataFlowAnalyzer.globalTypeMap.get(name);
        if (t != null) return t;
        return "int";
    }

    private static void loadIReg(List<String> mips, String targetReg, String var, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        if (DataFlowAnalyzer.isLiteral(var)) {
            mips.add("li " + targetReg + ", " + var);
            return;
        }
        boolean isLocalArray = func != null && func.localArrays != null && func.localArrays.contains(var);
        boolean isGlobalArray = DataFlowAnalyzer.globalTypeMap.containsKey(var) && DataFlowAnalyzer.globalTypeMap.get(var).contains("[");
        if (isLocalArray) {
            int off = pr.varOffset.getOrDefault(var, 0);
            mips.add("addiu " + targetReg + ", $sp, " + off);
            return;
        } else if (isGlobalArray) {
            mips.add("la " + targetReg + ", " + var);
            return;
        }
        if (alloc.containsKey(var)) {
            String reg = alloc.get(var);
            if (!reg.equals("spilled")) {
                if (!reg.equals(targetReg)) mips.add("move " + targetReg + ", " + reg);
            } else {
                Integer off = pr.varOffset.get(var);
                if (off != null) {
                    mips.add("lw " + targetReg + ", " + off + "($sp)");
                } else {
                    mips.add("lw " + targetReg + ", " + var);
                }
            }
        } else {
            mips.add("lw " + targetReg + ", " + var);
        }
    }

    private static void storeIReg(List<String> mips, String sourceReg, String var, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        if (alloc.containsKey(var)) {
            String reg = alloc.get(var);
            if (!reg.equals("spilled")) {
                if (!reg.equals(sourceReg)) mips.add("move " + reg + ", " + sourceReg);
            } else {
                Integer off = pr.varOffset.get(var);
                if (off != null) {
                    mips.add("sw " + sourceReg + ", " + off + "($sp)");
                } else {
                    mips.add("sw " + sourceReg + ", " + var);
                }
            }
        } else {
            mips.add("sw " + sourceReg + ", " + var);
        }
    }

    private static void loadFReg(List<String> mips, String targetReg, String var, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        if (DataFlowAnalyzer.isLiteral(var)) {
            if (var.contains(".")) {
                mips.add("li.s " + targetReg + ", " + var);
            } else {
                mips.add("li $t8, " + var);
                mips.add("mtc1 $t8, " + targetReg);
                mips.add("cvt.s.w " + targetReg + ", " + targetReg);
            }
            return;
        }
        if (alloc.containsKey(var)) {
            String reg = alloc.get(var);
            if (!reg.equals("spilled")) {
                if (!reg.equals(targetReg)) mips.add("mov.s " + targetReg + ", " + reg);
            } else {
                Integer off = pr.varOffset.get(var);
                if (off != null) {
                    mips.add("l.s " + targetReg + ", " + off + "($sp)");
                } else {
                    mips.add("l.s " + targetReg + ", " + var);
                }
            }
        } else {
            mips.add("l.s " + targetReg + ", " + var);
        }
    }

    private static void storeFReg(List<String> mips, String sourceReg, String var, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        if (alloc.containsKey(var)) {
            String reg = alloc.get(var);
            if (!reg.equals("spilled")) {
                if (!reg.equals(sourceReg)) mips.add("mov.s " + reg + ", " + sourceReg);
            } else {
                Integer off = pr.varOffset.get(var);
                if (off != null) {
                    mips.add("s.s " + sourceReg + ", " + off + "($sp)");
                } else {
                    mips.add("s.s " + sourceReg + ", " + var);
                }
            }
        } else {
            mips.add("s.s " + sourceReg + ", " + var);
        }
    }

    private static void loadOperandInt(List<String> mips, String targetReg, String operand, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        if (DataFlowAnalyzer.isLiteral(operand)) mips.add("li " + targetReg + ", " + operand);
        else loadIReg(mips, targetReg, operand, alloc, pr, func);
    }

    private static void loadOperandFloat(List<String> mips, String targetReg, String operand, String operandType, Map<String, String> alloc, PrologueResult pr, DataFlowAnalyzer.FunctionDa func) {
        if (DataFlowAnalyzer.isLiteral(operand)) {
            if (operand.contains(".")) {
                mips.add("li.s " + targetReg + ", " + operand);
            } else {
                mips.add("li $v1, " + operand);
                mips.add("mtc1 $v1, " + targetReg);
                mips.add("cvt.s.w " + targetReg + ", " + targetReg);
            }
        } else if (operandType.equals("float")) {
            loadFReg(mips, targetReg, operand, alloc, pr, func);
        } else {
            loadIReg(mips, "$v1", operand, alloc, pr, func);
            mips.add("mtc1 $v1, " + targetReg);
            mips.add("cvt.s.w " + targetReg + ", " + targetReg);
        }
    }

    public static class PrologueResult {
        public int frameSize;
        public Map<String, Integer> varOffset;
        public Set<String> usedRegs;
    }

    public static PrologueResult emitPrologue(List<String> mips, DataFlowAnalyzer.FunctionDa func, Map<String, String> alloc) {
        Set<String> usedRegs = new LinkedHashSet<>();
        for (String var : func.localVars) if (alloc.containsKey(var) && !alloc.get(var).equals("spilled")) usedRegs.add(alloc.get(var));
        for (String[] p : func.params) if (alloc.containsKey(p[0]) && !alloc.get(p[0]).equals("spilled")) usedRegs.add(alloc.get(p[0]));

        int maxArgs = 0;
        for (DataFlowAnalyzer.Instruction inst : func.instructions) {
            String[] parts = DataFlowAnalyzer.splitParts(inst.text.trim());
            if (parts[0].equals("call")) maxArgs = Math.max(maxArgs, parts.length - 2);
            else if (parts[0].equals("callr")) maxArgs = Math.max(maxArgs, parts.length - 3);
        }

        int frameSize = 4;
        for (String r : INT_REGS) if (usedRegs.contains(r)) frameSize += 4;
        for (String r : FLOAT_REGS) if (usedRegs.contains(r)) frameSize += 4;
        for (String[] p : func.params) if (!alloc.containsKey(p[0]) || alloc.get(p[0]).equals("spilled")) frameSize += 4;
        for (String v : func.localVars) {
            if (func.localArrays != null && func.localArrays.contains(v)) continue;
            if (!alloc.containsKey(v) || alloc.get(v).equals("spilled")) frameSize += 4;
        }
        if (func.localArrays != null) {
            for (String arr : func.localArrays) frameSize += func.arraySize.get(arr) * 4;
        }
        int stackArgs = Math.max(maxArgs, 4);
        frameSize += stackArgs * 4;
        if (frameSize % 8 != 0) frameSize += 4;

        if (func.name.equals("main")) mips.add(".globl main");
        mips.add(func.name + ":");
        mips.add("addiu $sp, $sp, -" + frameSize);
        mips.add("sw $ra, " + (frameSize - 4) + "($sp)");

        Map<String, Integer> varOffset = new LinkedHashMap<>();
        int offset = frameSize - 8;

        for (String r : INT_REGS) {
            if (usedRegs.contains(r)) {
                mips.add("sw " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }
        for (String r : FLOAT_REGS) {
            if (usedRegs.contains(r)) {
                mips.add("s.s " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }

        int intArgIdx = 0, floatArgIdx = 0, stackArgIdx = 0;
        for (int i = 0; i < func.params.size(); i++) {
            String pName = func.params.get(i)[0];
            String pType = func.params.get(i)[1];
            String reg = alloc.get(pName);
            if (pType.equals("float")) {
                if (floatArgIdx < 4) {
                    if (reg != null && !reg.equals("spilled")) {
                        mips.add("mov.s " + reg + ", " + FLOAT_ARGS[floatArgIdx]);
                    } else {
                        varOffset.put(pName, offset);
                        mips.add("s.s " + FLOAT_ARGS[floatArgIdx] + ", " + offset + "($sp)");
                        offset -= 4;
                    }
                    floatArgIdx++;
                } else {
                    int callerOff = frameSize + (stackArgIdx * 4);
                    if (reg != null && !reg.equals("spilled")) {
                        mips.add("l.s " + reg + ", " + callerOff + "($sp)");
                    } else {
                        varOffset.put(pName, offset);
                        mips.add("l.s $f16, " + callerOff + "($sp)");
                        mips.add("s.s $f16, " + offset + "($sp)");
                        offset -= 4;
                    }
                    stackArgIdx++;
                }
            } else {
                if (intArgIdx < 4) {
                    if (reg != null && !reg.equals("spilled")) {
                        mips.add("move " + reg + ", " + INT_ARGS[intArgIdx]);
                    } else {
                        varOffset.put(pName, offset);
                        mips.add("sw " + INT_ARGS[intArgIdx] + ", " + offset + "($sp)");
                        offset -= 4;
                    }
                    intArgIdx++;
                } else {
                    int callerOff = frameSize + (stackArgIdx * 4);
                    if (reg != null && !reg.equals("spilled")) {
                        mips.add("lw " + reg + ", " + callerOff + "($sp)");
                    } else {
                        varOffset.put(pName, offset);
                        mips.add("lw $t8, " + callerOff + "($sp)");
                        mips.add("sw $t8, " + offset + "($sp)");
                        offset -= 4;
                    }
                    stackArgIdx++;
                }
            }
        }

        for (String v : func.localVars) {
            if (func.localArrays != null && func.localArrays.contains(v)) continue;
            String reg = alloc.get(v);
            if (reg == null || reg.equals("spilled")) {
                varOffset.put(v, offset);
                offset -= 4;
            }
        }

        if (func.localArrays != null) {
            for (String arr : func.localArrays) {
                int sz = func.arraySize.get(arr);
                varOffset.put(arr, offset - (sz - 1) * 4);
                offset -= sz * 4;
            }
        }

        PrologueResult result = new PrologueResult();
        result.frameSize = frameSize;
        result.varOffset = varOffset;
        result.usedRegs = usedRegs;
        return result;
    }

    public static void emitEpilogue(List<String> mips, int frameSize, Set<String> usedRegs) {
        int offset = frameSize - 8;
        for (String r : INT_REGS) {
            if (usedRegs.contains(r)) {
                mips.add("lw " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }
        for (String r : FLOAT_REGS) {
            if (usedRegs.contains(r)) {
                mips.add("l.s " + r + ", " + offset + "($sp)");
                offset -= 4;
            }
        }
        mips.add("lw $ra, " + (frameSize - 4) + "($sp)");
        mips.add("addiu $sp, $sp, " + frameSize);
        mips.add("jr $ra");
    }
}