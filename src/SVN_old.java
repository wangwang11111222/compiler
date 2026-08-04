import java.util.*;

public class SVN {
    public static int countConstProp = 0;
    public static int countLexIdent = 0;
    public static int countValIdent = 0;

    public static void optimize() {
        countConstProp = 0;
        countLexIdent = 0;
        countValIdent = 0;

        for (DataFlowAnalyzer.FunctionDa func : DataFlowAnalyzer.functions) {
            Map<String, Integer> labelMap = func.buildLabelMap();
            func.buildBb(labelMap);
            func.buildCfg(labelMap);
            List<List<DataFlowAnalyzer.BasicBlock>> ebbs = findEBBs(func);
            for (List<DataFlowAnalyzer.BasicBlock> ebb : ebbs) {
                processEBB(func, ebb);
            }
        }
    }

    private static List<List<DataFlowAnalyzer.BasicBlock>> findEBBs(DataFlowAnalyzer.FunctionDa func) {
        Map<String, Set<String>> preds = new HashMap<>();
        for (DataFlowAnalyzer.BasicBlock bb : func.basicBlocks) {
            preds.putIfAbsent(bb.id, new HashSet<>());
            for (String succ : bb.successors) {
                preds.computeIfAbsent(succ, k -> new HashSet<>()).add(bb.id);
            }
        }

        Set<String> roots = new LinkedHashSet<>();
        for (DataFlowAnalyzer.BasicBlock bb : func.basicBlocks) {
            if (preds.get(bb.id).size() != 1) {
                roots.add(bb.id);
            }
        }
        if (!func.basicBlocks.isEmpty() && !roots.contains(func.basicBlocks.get(0).id)) {
            roots.add(func.basicBlocks.get(0).id);
        }

        Map<String, DataFlowAnalyzer.BasicBlock> idToBlock = new LinkedHashMap<>();
        for (DataFlowAnalyzer.BasicBlock bb : func.basicBlocks) idToBlock.put(bb.id, bb);

        List<List<DataFlowAnalyzer.BasicBlock>> ebbs = new ArrayList<>();
        for (String rootId : roots) {
            List<DataFlowAnalyzer.BasicBlock> ebb = new ArrayList<>();
            Deque<String> worklist = new ArrayDeque<>();
            worklist.add(rootId);
            Set<String> visited = new HashSet<>();
            while (!worklist.isEmpty()) {
                String cur = worklist.poll();
                if (visited.contains(cur)) continue;
                visited.add(cur);
                DataFlowAnalyzer.BasicBlock bb = idToBlock.get(cur);
                if (bb == null) continue;
                ebb.add(bb);
                for (String succ : bb.successors) {
                    if (preds.get(succ) != null && preds.get(succ).size() == 1) {
                        worklist.add(succ);
                    }
                }
            }
            if (!ebb.isEmpty()) ebbs.add(ebb);
        }
        return ebbs;
    }

    private static void processEBB(DataFlowAnalyzer.FunctionDa func, List<DataFlowAnalyzer.BasicBlock> ebb) {
        Map<String, String> valToVar = new HashMap<>();
        Map<String, String> varToVal = new HashMap<>();
        Map<String, String> constMap = new HashMap<>();

        for (DataFlowAnalyzer.BasicBlock bb : ebb) {
            for (int i = bb.startLine; i <= bb.endLine; i++) {
                DataFlowAnalyzer.Instruction inst = func.instructions.get(i);
                String text = inst.text.trim();
                if (text.endsWith(":")) continue;
                String[] parts = DataFlowAnalyzer.splitParts(text);
                String op = parts[0];

                if (op.equals("assign")) {
                    String dst = parts[1], src = parts[2];
                    String resolvedSrc = resolve(src, constMap);

                    if (DataFlowAnalyzer.isLiteral(resolvedSrc) && !resolvedSrc.equals(src)) {
                        inst.text = "assign, " + dst + ", " + resolvedSrc;
                        countConstProp++;
                    }

                    if (DataFlowAnalyzer.isLiteral(resolvedSrc)) {
                        constMap.put(dst, resolvedSrc);
                    } else {
                        constMap.remove(dst);
                    }

                    String valKey = "assign:" + resolvedSrc;
                    if (valToVar.containsKey(valKey)) {
                        String existing = valToVar.get(valKey);
                        if (!existing.equals(dst) && !DataFlowAnalyzer.isLiteral(resolvedSrc)) {
                            inst.text = "assign, " + dst + ", " + existing;
                            countValIdent++;
                        }
                    } else {
                        if (!DataFlowAnalyzer.isLiteral(resolvedSrc)) {
                            valToVar.put(valKey, dst);
                        }
                    }
                    varToVal.put(dst, valKey);

                } else if (op.equals("add") || op.equals("sub") || op.equals("mult") || op.equals("div") || op.equals("and") || op.equals("or")) {
                    if (parts.length < 4) continue;
                    String src1 = parts[1], src2 = parts[2], dst = parts[3];
                    String r1 = resolve(src1, constMap);
                    String r2 = resolve(src2, constMap);

                    boolean prop = false;
                    if (!r1.equals(src1)) { prop = true; }
                    if (!r2.equals(src2)) { prop = true; }

                    if (DataFlowAnalyzer.isLiteral(r1) && DataFlowAnalyzer.isLiteral(r2)
                            && !r1.contains(".") && !r2.contains(".")) {
                        String folded = fold(op, r1, r2);
                        if (folded != null) {
                            inst.text = "assign, " + dst + ", " + folded;
                            constMap.put(dst, folded);
                            countConstProp++;
                            varToVal.put(dst, "const:" + folded);
                            valToVar.put("const:" + folded, dst);
                            continue;
                        }
                    }

                    if (DataFlowAnalyzer.isLiteral(r1) && DataFlowAnalyzer.isLiteral(r2)
                            && (r1.contains(".") || r2.contains("."))) {
                        String folded = foldFloat(op, r1, r2);
                        if (folded != null) {
                            inst.text = "assign, " + dst + ", " + folded;
                            constMap.put(dst, folded);
                            countConstProp++;
                            varToVal.put(dst, "const:" + folded);
                            valToVar.put("const:" + folded, dst);
                            continue;
                        }
                    }

                    if (prop) {
                        inst.text = op + ", " + r1 + ", " + r2 + ", " + dst;
                        countConstProp++;
                    }

                    constMap.remove(dst);

                    String valKey = op + ":" + r1 + ":" + r2;
                    String commKey = null;
                    if (isCommutative(op)) {
                        commKey = op + ":" + r2 + ":" + r1;
                    }

                    if (valToVar.containsKey(valKey)) {
                        String existing = valToVar.get(valKey);
                        inst.text = "assign, " + dst + ", " + existing;
                        if (r1.equals(src1) && r2.equals(src2)) countLexIdent++;
                        else countValIdent++;
                    } else if (commKey != null && valToVar.containsKey(commKey)) {
                        String existing = valToVar.get(commKey);
                        inst.text = "assign, " + dst + ", " + existing;
                        countLexIdent++;
                    } else {
                        valToVar.put(valKey, dst);
                        if (commKey != null) valToVar.put(commKey, dst);
                    }
                    varToVal.put(dst, valKey);

                } else if (op.equals("callr") || op.equals("array_load")) {
                    String dst = parts[1];
                    constMap.remove(dst);
                    varToVal.remove(dst);
                } else if (op.equals("call") || op.equals("array_store")) {
                    // don't clear everything, but be conservative
                }
            }
        }
    }

    private static String resolve(String operand, Map<String, String> constMap) {
        if (DataFlowAnalyzer.isLiteral(operand)) return operand;
        if (constMap.containsKey(operand)) return constMap.get(operand);
        return operand;
    }

    private static String fold(String op, String s1, String s2) {
        try {
            int a = Integer.parseInt(s1);
            int b = Integer.parseInt(s2);
            switch (op) {
                case "add": return String.valueOf(a + b);
                case "sub": return String.valueOf(a - b);
                case "mult": return String.valueOf(a * b);
                case "div": if (b != 0) return String.valueOf(a / b); else return null;
                case "and": return String.valueOf((a != 0 && b != 0) ? 1 : 0);
                case "or": return String.valueOf((a != 0 || b != 0) ? 1 : 0);
                default: return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String foldFloat(String op, String s1, String s2) {
        try {
            float a = Float.parseFloat(s1);
            float b = Float.parseFloat(s2);
            float result;
            switch (op) {
                case "add": result = a + b; break;
                case "sub": result = a - b; break;
                case "mult": result = a * b; break;
                case "div": if (b != 0) result = a / b; else return null; break;
                default: return null;
            }
            String r = String.valueOf(result);
            if (!r.contains(".")) r = r + ".0";
            return r;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean isCommutative(String op) {
        return op.equals("add") || op.equals("mult") || op.equals("and") || op.equals("or");
    }

    public static String report() {
        return "SVN Optimizations:\n"
             + "  Constant propagation/folding: " + countConstProp + "\n"
             + "  Lexically identical removal:  " + countLexIdent + "\n"
             + "  Value identical removal:      " + countValIdent + "\n"
             + "  Total:                        " + (countConstProp + countLexIdent + countValIdent) + "\n";
    }
}
