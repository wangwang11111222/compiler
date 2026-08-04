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
            if (preds.get(bb.id).size() != 1) roots.add(bb.id);
        }
        if (!func.basicBlocks.isEmpty() && !roots.contains(func.basicBlocks.get(0).id))
            roots.add(func.basicBlocks.get(0).id);
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
                    if (preds.get(succ) != null && preds.get(succ).size() == 1)
                        worklist.add(succ);
                }
            }
            if (!ebb.isEmpty()) ebbs.add(ebb);
        }
        return ebbs;
    }

    private static void processEBB(DataFlowAnalyzer.FunctionDa func, List<DataFlowAnalyzer.BasicBlock> ebb) {
        Map<String, String> constMap = new HashMap<>();
        Map<String, String> varToCanon = new HashMap<>();
        Map<String, String> exprToVar = new HashMap<>();

        for (DataFlowAnalyzer.BasicBlock bb : ebb) {
            for (int i = bb.startLine; i <= bb.endLine; i++) {
                DataFlowAnalyzer.Instruction inst = func.instructions.get(i);
                String text = inst.text.trim();
                if (text.endsWith(":")) continue;
                String[] parts = DataFlowAnalyzer.splitParts(text);
                if (parts.length == 0) continue;
                String op = parts[0];

                if (op.equals("assign")) {
                    if (parts.length < 3) continue;
                    String dst = parts[1], src = parts[2];
                    String resolvedSrc = resolve(src, constMap);
                    if (DataFlowAnalyzer.isLiteral(resolvedSrc) && !resolvedSrc.equals(src)) {
                        inst.text = "assign, " + dst + ", " + resolvedSrc;
                        countConstProp++;
                        constMap.put(dst, resolvedSrc);
                        varToCanon.put(dst, resolvedSrc);
                        continue;
                    }
                    if (DataFlowAnalyzer.isLiteral(resolvedSrc)) {
                        constMap.put(dst, resolvedSrc);
                        varToCanon.put(dst, resolvedSrc);
                    } else {
                        constMap.remove(dst);
                        String srcCanon = getCanon(resolvedSrc, varToCanon);
                        varToCanon.put(dst, srcCanon);
                        String exprKey = "assign:" + srcCanon;
                        if (exprToVar.containsKey(exprKey)) {
                            String existing = exprToVar.get(exprKey);
                            if (!existing.equals(dst)) {
                                inst.text = "assign, " + dst + ", " + existing;
                                countValIdent++;
                            }
                        } else {
                            exprToVar.put(exprKey, dst);
                        }
                    }

                } else if (op.equals("add") || op.equals("sub") || op.equals("mult") || op.equals("div")
                        || op.equals("and") || op.equals("or")) {
                    if (parts.length < 4) continue;
                    String src1 = parts[1], src2 = parts[2], dst = parts[3];
                    String r1 = resolve(src1, constMap);
                    String r2 = resolve(src2, constMap);
                    boolean propHappened = !r1.equals(src1) || !r2.equals(src2);
                    if (DataFlowAnalyzer.isLiteral(r1) && DataFlowAnalyzer.isLiteral(r2)) {
                        String folded = r1.contains(".") || r2.contains(".")
                                ? foldFloat(op, r1, r2) : fold(op, r1, r2);
                        if (folded != null) {
                            inst.text = "assign, " + dst + ", " + folded;
                            constMap.put(dst, folded);
                            varToCanon.put(dst, folded);
                            countConstProp++;
                            continue;
                        }
                    }
                    if (propHappened) {
                        inst.text = op + ", " + r1 + ", " + r2 + ", " + dst;
                        countConstProp++;
                    }
                    constMap.remove(dst);
                    String c1 = getCanon(r1, varToCanon);
                    String c2 = getCanon(r2, varToCanon);
                    String valKey = makeKey(op, c1, c2);
                    String commKey = isCommutative(op) ? makeKey(op, c2, c1) : null;
                    String lexKey = makeKey(op, r1, r2);
                    String lexComm = isCommutative(op) ? makeKey(op, r2, r1) : null;
                    if (exprToVar.containsKey(valKey)) {
                        String existing = exprToVar.get(valKey);
                        inst.text = "assign, " + dst + ", " + existing;
                        varToCanon.put(dst, getCanon(existing, varToCanon));
                        if (lexKey.equals(valKey) || (lexComm != null && lexComm.equals(valKey))) {
                            countLexIdent++;
                        } else {
                            countValIdent++;
                        }
                    } else if (commKey != null && exprToVar.containsKey(commKey)) {
                        String existing = exprToVar.get(commKey);
                        inst.text = "assign, " + dst + ", " + existing;
                        varToCanon.put(dst, getCanon(existing, varToCanon));
                        if (lexKey.equals(commKey) || (lexComm != null && lexComm.equals(commKey))) {
                            countLexIdent++;
                        } else {
                            countValIdent++;
                        }
                    } else {
                        exprToVar.put(valKey, dst);
                        if (commKey != null) exprToVar.put(commKey, dst);
                        varToCanon.put(dst, dst);
                    }

                } else if (op.equals("callr") || op.equals("array_load")) {
                    if (parts.length > 1) {
                        String dst = parts[1];
                        constMap.remove(dst);
                        varToCanon.remove(dst);
                        exprToVar.entrySet().removeIf(e -> e.getValue().equals(dst));
                    }
                }
            }
        }
    }

    private static String getCanon(String var, Map<String, String> varToCanon) {
        if (DataFlowAnalyzer.isLiteral(var)) return var;
        String canon = varToCanon.get(var);
        if (canon == null) return var;
        if (canon.equals(var)) return var;
        String canon2 = varToCanon.get(canon);
        if (canon2 != null && !canon2.equals(canon)) return canon2;
        return canon;
    }

    private static String resolve(String operand, Map<String, String> constMap) {
        if (DataFlowAnalyzer.isLiteral(operand)) return operand;
        if (constMap.containsKey(operand)) return constMap.get(operand);
        return operand;
    }

    private static String makeKey(String op, String a, String b) {
        return op + ":" + a + ":" + b;
    }

    private static String fold(String op, String s1, String s2) {
        try {
            int a = Integer.parseInt(s1), b = Integer.parseInt(s2);
            switch (op) {
                case "add":  return String.valueOf(a + b);
                case "sub":  return String.valueOf(a - b);
                case "mult": return String.valueOf(a * b);
                case "div":  return b != 0 ? String.valueOf(a / b) : null;
                case "and":  return String.valueOf((a != 0 && b != 0) ? 1 : 0);
                case "or":   return String.valueOf((a != 0 || b != 0) ? 1 : 0);
                default:     return null;
            }
        } catch (NumberFormatException e) { return null; }
    }

    private static String foldFloat(String op, String s1, String s2) {
        try {
            float a = Float.parseFloat(s1), b = Float.parseFloat(s2);
            float r;
            switch (op) {
                case "add":  r = a + b; break;
                case "sub":  r = a - b; break;
                case "mult": r = a * b; break;
                case "div":  if (b != 0) { r = a / b; break; } return null;
                default:     return null;
            }
            String res = String.valueOf(r);
            return res.contains(".") ? res : res + ".0";
        } catch (NumberFormatException e) { return null; }
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