import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AllocationEmit {
        private static final String[] INT_REGS= {"$s0", "$s1", "$s2", "$t0", "$t1", "$t2"};
        private static final String[] FLOAT_REGS  = {"$f4", "$f5", "$f6", "$f20", "$f21", "$f22"};
        private static final String[] INT_ARGS = {"$a0", "$a1", "$a2", "$a3"};
        private static final String[] FLOAT_ARGS = {"$f12", "$f13", "$f14", "$f15"};
        private static final String INT_RETURN  = "$v0";
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
                Set<String> assignedRegs = new HashSet<>();
                List<String> sortedVar = new ArrayList<>(bb.localSpillCosts.keySet());
                sortedVar.sort((a, b) -> bb.localSpillCosts.get(b) - bb.localSpillCosts.get(a));
                for (String var : sortedVar) {
                    String type = func.typeMap.getOrDefault(var, "int");
                    String[] regPool = type.equals("float") ? FLOAT_REGS : INT_REGS;
                    for (String reg : regPool) {
                        if (!assignedRegs.contains(reg)) {
                            alloc.put(var, reg);
                            assignedRegs.add(reg);
                            break;
                        }
                    }
                    if (!alloc.containsKey(var)) {
                        alloc.put(var, "spilled");
                    }
                }
            }
        }
        return alloc;
    }

    public static Map<String, String> briggAlloc(){
        Map<String, String> alloc = new LinkedHashMap<>();
        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions){
        List<Map<String, Set<String>>> graphList = buildGraphs(func);
        Map<String, Set<String>> intGraph = graphList.get(0);
        Map<String, Set<String>> floatGraph = graphList.get(1);
        Deque<String>intStack=buildStack(intGraph,func.globalSpillCosts);
        Deque<String>floatStack=buildStack(floatGraph,func.globalSpillCosts);

        // public static Map<String, String> colorGraph(Map<String, Set<String>> graph, Deque<String> stack, String[] registers)
        Map<String,String> intAlloc=colorGraph(intGraph,intStack,INT_REGS);
        Map<String,String> floatAlloc=colorGraph(floatGraph,floatStack,FLOAT_REGS);
        alloc.putAll(intAlloc);
        alloc.putAll(floatAlloc);

        }
        return alloc;
    }


//   Build the interference graph     
    public static List<Map<String, Set<String>>> buildGraphs(DataFlowAnalyzer.FunctionDa func) {
        Map<String, Set<String>> intGraph   = new LinkedHashMap<>();
        Map<String, Set<String>> floatGraph = new LinkedHashMap<>();

        for (DataFlowAnalyzer.Instruction inst : func.instructions) {
            for (String a : inst.in) {
                for (String b : inst.in) {
                    if (!a.equals(b)) {
                        String type = func.typeMap.get(a);
                        if (type != null && type.equals(func.typeMap.get(b))) {
                            if (type.equals("float")) { addEdge(floatGraph, a, b); }
                            else  { addEdge(intGraph,   a, b); }
                        }
                    }
                }
            }
            for (String a : inst.out) {
                for (String b : inst.out) {
                    if (!a.equals(b)) {
                        String type = func.typeMap.get(a);
                        if (type != null && type.equals(func.typeMap.get(b))) {
                            if (type.equals("float")) { addEdge(floatGraph, a, b); }
                            else { addEdge(intGraph,   a, b); }
                        }
                    }
                }
            }
        }
        return List.of(intGraph, floatGraph);
    }

    private static void addEdge(Map<String, Set<String>> graph, String a, String b) {
        graph.computeIfAbsent(a, k -> new LinkedHashSet<>()).add(b);
        graph.computeIfAbsent(b, k -> new LinkedHashSet<>()).add(a);
    }

    private static Deque<String> buildStack(Map<String, Set<String>> graph,Map<String, Integer> spillCosts) {
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
            Map.Entry<String, Integer> minEntry =
                Collections.min(degreeMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue));
            String minNode = minEntry.getKey();
            int minDeg = minEntry.getValue();

            if (minDeg < 6) {
                stack.push(minNode);
            } else {
                minNode = Collections.min(degreeMap.keySet(),
                    Comparator.comparingInt(v -> spillCosts.getOrDefault(v, 0)));
                stack.push(minNode);
            }
            for (String neighbor : copy.getOrDefault(minNode, Collections.emptySet())) {
                copy.get(neighbor).remove(minNode);
                degreeMap.computeIfPresent(neighbor, (k, v) -> v - 1);
            }
            copy.remove(minNode);
            degreeMap.remove(minNode);
        }
        return stack;
    }

        public static Map<String, String> colorGraph(Map<String, Set<String>> graph, Deque<String> stack, String[] registers) {
            Map<String, String> alloc = new LinkedHashMap<>();

            while (!stack.isEmpty()) {
                String node = stack.pop();
                Set<String> usedRegs = new HashSet<>();

                for (String neighbor : graph.getOrDefault(node, Collections.emptySet())) {
                    if (alloc.containsKey(neighbor) && !alloc.get(neighbor).equals("spilled")) {
                        usedRegs.add(alloc.get(neighbor));
                    }
                }

                if (usedRegs.size() >= registers.length) {
                    alloc.put(node, "spilled");
                    continue;
                }

                for (String reg : registers) {
                    if (!usedRegs.contains(reg)) {
                        alloc.put(node, reg);
                        break;
                    }
                }
            }

            return alloc;
        }


        public static InstructionSelection (Map<String, String> alloc){
            List<String> mipsLines = new ArrayList<>();
            mipsLines.add(".data");
            for (String s : DataFlowAnalyzer.staticVars) {
                String type = DataFlowAnalyzer.globalTypeMap.get(s);
                if (type != null && type.endsWith("[]")) {
                    int size = DataFlowAnalyzer.globalArraySize.get(s);
                    mipsLines.add(s + ": .space " + (size * 4));
                } else if ("float".equals(type)) {
                    mipsLines.add(s + ": .float 0.0");
                } else {
                    mipsLines.add(s + ": .word 0");
                }
            }
            mipsLines.add(".text");

            for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions){
                    PrologueResult pr = emitPrologue(mipsLines, func, alloc);
                    for(DataFlowAnalyzer.Instruction inst : func.instructions){
                            String text = inst.text.trim();
                            if (text.endsWith(":")) { mipsLines.add(text); continue; }

                            String[] parts = DataFlowAnalyzer.splitParts(text);
                            String op = parts[0];

                            if (op.equals("assign")) {
                                String dst = parts[1], src = parts[2];
                                String dstReg = alloc.get(dst);
                                String srcReg = alloc.get(src);
                                boolean dstSpilled = (dstReg == null || dstReg.equals("spilled"));
                                boolean srcIsLit = DataFlowAnalyzer.isLiteral(src);
                                boolean srcSpilled = !srcIsLit && (srcReg == null || srcReg.equals("spilled"));
                                boolean isGlobalDst = DataFlowAnalyzer.globalTypeMap.containsKey(dst);
                                boolean isGlobalSrc = DataFlowAnalyzer.globalTypeMap.containsKey(src);
                                String dstType = func.typeMap.getOrDefault(dst, DataFlowAnalyzer.globalTypeMap.getOrDefault(dst, "int"));
                                String srcType;
                                if (srcIsLit) {
                                    srcType = src.contains(".") ? "float" : "int";
                                } else {
                                    srcType = func.typeMap.getOrDefault(src, DataFlowAnalyzer.globalTypeMap.getOrDefault(src, "int"));
                                }
                                boolean dstFloat = dstType.equals("float");
                                boolean srcFloat = srcType.equals("float");
                                boolean needConvert = (dstFloat && !srcFloat);

                                if (srcIsLit) {
                                    if (needConvert) {
                                        // int literal â†?float var (e.g., assign, f, 5)
                                        mipsLines.add("li $t8, " + src);
                                        mipsLines.add("mtc1 $t8, $f16");
                                        mipsLines.add("cvt.s.w $f16, $f16");
                                        if (!dstSpilled && !isGlobalDst) {
                                            mipsLines.add("mov.s " + dstReg + ", $f16");
                                        } else if (isGlobalDst) {
                                            mipsLines.add("s.s $f16, " + dst);
                                        } else {
                                            mipsLines.add("s.s $f16, " + pr.varOffset.get(dst) + "($sp)");
                                        }
                                    } else if (dstFloat) {
                                        // float literal â†?float var
                                        if (!dstSpilled && !isGlobalDst) {
                                            mipsLines.add("li.s " + dstReg + ", " + src);
                                        } else if (isGlobalDst) {
                                            mipsLines.add("li.s $f16, " + src);
                                            mipsLines.add("s.s $f16, " + dst);
                                        } else {
                                            mipsLines.add("li.s $f16, " + src);
                                            mipsLines.add("s.s $f16, " + pr.varOffset.get(dst) + "($sp)");
                                        }
                                    } else {
                                        // int literal â†?int var
                                        if (!dstSpilled && !isGlobalDst) {
                                            mipsLines.add("li " + dstReg + ", " + src);
                                        } else if (isGlobalDst) {
                                            mipsLines.add("li $t8, " + src);
                                            mipsLines.add("sw $t8, " + dst);
                                        } else {
                                            mipsLines.add("li $t8, " + src);
                                            mipsLines.add("sw $t8, " + pr.varOffset.get(dst) + "($sp)");
                                        }
                                    }
                                } else if (needConvert) {
                                    // int var â†?float var (e.g., assign, f, intVar)
                                    // load int src
                                    if (!srcSpilled && !isGlobalSrc) {
                                        mipsLines.add("mtc1 " + srcReg + ", $f16");
                                    } else if (isGlobalSrc) {
                                        mipsLines.add("lw $t8, " + src);
                                        mipsLines.add("mtc1 $t8, $f16");
                                    } else {
                                        mipsLines.add("lw $t8, " + pr.varOffset.get(src) + "($sp)");
                                        mipsLines.add("mtc1 $t8, $f16");
                                    }
                                    mipsLines.add("cvt.s.w $f16, $f16");
                                    // store float dst
                                    if (!dstSpilled && !isGlobalDst) {
                                        mipsLines.add("mov.s " + dstReg + ", $f16");
                                    } else if (isGlobalDst) {
                                        mipsLines.add("s.s $f16, " + dst);
                                    } else {
                                        mipsLines.add("s.s $f16, " + pr.varOffset.get(dst) + "($sp)");
                                    }
                                } else if (!dstSpilled && !srcSpilled && !isGlobalDst && !isGlobalSrc) {
                                    // both in registers, same type
                                    if (dstFloat) mipsLines.add("mov.s " + dstReg + ", " + srcReg);
                                    else mipsLines.add("move " + dstReg + ", " + srcReg);
                                } else {
                                    // general case: load src â†?tmp, store tmp â†?dst
                                    String tmpInt = "$t8", tmpFloat = "$f16";
                                    String tmp;

                                    // load src
                                    if (!srcSpilled && !isGlobalSrc) {
                                        tmp = srcReg;
                                    } else if (isGlobalSrc) {
                                        if (srcFloat) { mipsLines.add("l.s " + tmpFloat + ", " + src); tmp = tmpFloat; }
                                        else { mipsLines.add("lw " + tmpInt + ", " + src); tmp = tmpInt; }
                                    } else {
                                        if (srcFloat) { mipsLines.add("l.s " + tmpFloat + ", " + pr.varOffset.get(src) + "($sp)"); tmp = tmpFloat; }
                                        else { mipsLines.add("lw " + tmpInt + ", " + pr.varOffset.get(src) + "($sp)"); tmp = tmpInt; }
                                    }

                                    // store dst
                                    if (!dstSpilled && !isGlobalDst) {
                                        if (dstFloat) mipsLines.add("mov.s " + dstReg + ", " + tmp);
                                        else mipsLines.add("move " + dstReg + ", " + tmp);
                                    } else if (isGlobalDst) {
                                        if (dstFloat) mipsLines.add("s.s " + tmp + ", " + dst);
                                        else mipsLines.add("sw " + tmp + ", " + dst);
                                    } else {
                                        if (dstFloat) mipsLines.add("s.s " + tmp + ", " + pr.varOffset.get(dst) + "($sp)");
                                        else mipsLines.add("sw " + tmp + ", " + pr.varOffset.get(dst) + "($sp)");
                                    }
                                }                 
                            } else if (op.equals("add") || op.equals("sub") || op.equals("mult") || op.equals("div") || op.equals("and") || op.equals("or")) {
                                // TODO
                            } else if (DataFlowAnalyzer.isBranch(op)) {
                                // TODO
                            } else if (op.equals("goto")) {
                                mipsLines.add("j " + parts[1]);
                            } else if (op.equals("return")) {
                                // TODO: load return value into $v0 or $f0
                                emitEpilogue(mipsLines, pr.frameSize, pr.usedRegs);
                            } else if (op.equals("call") || op.equals("callr")) {
                                // TODO
                            } else if (op.equals("array_store")) {
                                // TODO
                            } else if (op.equals("array_load")) {
                                // TODO
                            }
                        }
                    }
                    return mipsLines;



                    
                    


              }


        }



public static class PrologueResult {
    public int frameSize;
    public Map<String, Integer> varOffset;
    public Set<String> usedRegs;
    public int outgoingArgOffset; // where outgoing args start
}

public static PrologueResult emitPrologue(
        List<String> mipsLines,
        DataFlowAnalyzer.FunctionDa func,
        Map<String, String> alloc) {

    String[] allIntRegs = {"$s0", "$s1", "$s2", "$t0", "$t1", "$t2"};
    String[] allFloatRegs = {"$f4", "$f5", "$f6", "$f20", "$f21", "$f22"};
    String[] intArgRegs = {"$a0", "$a1", "$a2", "$a3"};
    String[] floatArgRegs = {"$f12", "$f13", "$f14", "$f15"};

    Set<String> usedRegs = new HashSet<>();
    for (String var : func.localVars) {
        String reg = alloc.get(var);
        if (reg != null && !reg.equals("spilled")) usedRegs.add(reg);
    }
    for (String[] p : func.params) {
        String reg = alloc.get(p[0]);
        if (reg != null && !reg.equals("spilled")) usedRegs.add(reg);
    }

    int maxArgs = 0;
    for (DataFlowAnalyzer.Instruction inst : func.instructions) {
        String[] parts = DataFlowAnalyzer.splitParts(inst.text.trim());
        if (parts[0].equals("call")) maxArgs = Math.max(maxArgs, parts.length - 2);
        else if (parts[0].equals("callr")) maxArgs = Math.max(maxArgs, parts.length - 3);
    }

    int frameSize = 4;
    for (String r : allIntRegs) { if (usedRegs.contains(r)) frameSize += 4; }
    for (String r : allFloatRegs) { if (usedRegs.contains(r)) frameSize += 4; }
    for (String[] p : func.params) {
        String reg = alloc.get(p[0]);
        if (reg == null || reg.equals("spilled")) frameSize += 4;
    }
    for (String v : func.localVars) {
        if (!func.localArrays.contains(v)) {
            String reg = alloc.get(v);
            if (reg == null || reg.equals("spilled")) frameSize += 4;
        }
    }
    for (String arr : func.localArrays) { frameSize += func.arraySize.get(arr) * 4; }
    int stackArgs = Math.max(0, maxArgs - 4);
    frameSize += stackArgs * 4;
    if (frameSize % 8 != 0) frameSize += 4;

    if (func.name.equals("main")) mipsLines.add(".globl main");
    mipsLines.add(func.name + ":");
    mipsLines.add("addi $sp, $sp, -" + frameSize);
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

    for (int i = 0; i < func.params.size(); i++) {
        String pName = func.params.get(i)[0];
        String pType = func.params.get(i)[1];
        String reg = alloc.get(pName);
        if (i < 4) {
            if (reg != null && !reg.equals("spilled")) {
                if (pType.equals("float")) mipsLines.add("mov.s " + reg + ", " + floatArgRegs[i]);
                else mipsLines.add("move " + reg + ", " + intArgRegs[i]);
            } else {
                varOffset.put(pName, offset);
                if (pType.equals("float")) mipsLines.add("s.s " + floatArgRegs[i] + ", " + offset + "($sp)");
                else mipsLines.add("sw " + intArgRegs[i] + ", " + offset + "($sp)");
                offset -= 4;
            }
        } else {
            if (reg != null && !reg.equals("spilled")) {
                if (pType.equals("float")) mipsLines.add("l.s " + reg + ", " + (frameSize + (i - 4) * 4) + "($sp)");
                else mipsLines.add("lw " + reg + ", " + (frameSize + (i - 4) * 4) + "($sp)");
            } else {
                varOffset.put(pName, offset);
                String tmpReg = pType.equals("float") ? "$f16" : "$t8";
                if (pType.equals("float")) {
                    mipsLines.add("l.s " + tmpReg + ", " + (frameSize + (i - 4) * 4) + "($sp)");
                    mipsLines.add("s.s " + tmpReg + ", " + offset + "($sp)");
                } else {
                    mipsLines.add("lw " + tmpReg + ", " + (frameSize + (i - 4) * 4) + "($sp)");
                    mipsLines.add("sw " + tmpReg + ", " + offset + "($sp)");
                }
                offset -= 4;
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
        varOffset.put(arr, offset);
        offset -= size * 4;
    }

    PrologueResult result = new PrologueResult();
    result.frameSize = frameSize;
    result.varOffset = varOffset;
    result.usedRegs = usedRegs;
    return result;
}

}




