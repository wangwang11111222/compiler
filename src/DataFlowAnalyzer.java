import java.io.*;
import java.util.*;
import java.util.regex.*;

public class DataFlowAnalyzer {
    public static class Instruction {
        public int index;
        public String text;
        public Set<String> def  = new LinkedHashSet<>();
        public Set<String> use  = new LinkedHashSet<>();
        public Set<String> in   = new HashSet<>();
        public Set<String> out  = new HashSet<>();
        public List<Instruction> successors = new ArrayList<>();

        public Instruction(int index, String text) {
            this.index = index;
            this.text  = text;
        }
    }

    public static class BasicBlock {
        public String id;
        public int startLine;
        public int endLine;
        public List<String> successors = new ArrayList<>();
        public Map<String, Integer> localSpillCosts = new TreeMap<>();

        public BasicBlock(String id, int startLine) {
            this.id        = id;
            this.startLine = startLine;
        }
    }

    public static class FunctionDa {
        public String name;
        public String retType;
        public List<String[]> params= new ArrayList<>();
        public List<Instruction> instructions= new ArrayList<>();
        public List<BasicBlock> basicBlocks= new ArrayList<>();
        public Map<String, Integer> globalSpillCosts =new TreeMap<>();
        public Map<String, String> typeMap  = new LinkedHashMap<>();
        public Map<String, Integer> arraySize = new LinkedHashMap<>();


        public Set<String> localVars = new LinkedHashSet<>();
        public Set<String> localArrays = new LinkedHashSet<>();
        public Map<String, Integer> buildLabelMap() {
            Map<String, Integer> map= new LinkedHashMap<>();
            for (Instruction inst : instructions) {
                String s = inst.text.trim();
                if (s.endsWith(":")) {
                    map.put(s.substring(0, s.length() - 1).trim(), inst.index);
                }
            }
            return map;
        }

        private void countUse(BasicBlock bb, String name, Set<String> excluded) {
        name = name.trim();
        if (!name.isEmpty() && !excluded.contains(name) && !isLiteral(name))
        bb.localSpillCosts.merge(name, 2, Integer::sum);
        }

        private void countDef(BasicBlock bb, String name, Set<String> excluded) {
            name = name.trim();
            if (!name.isEmpty() && !excluded.contains(name) && !isLiteral(name))
                bb.localSpillCosts.merge(name, 1, Integer::sum);
        }


    public void buildBb(Map<String, Integer> labelMap) {
            int n = instructions.size();
            if (n == 0) return;
            Set<Integer> leaders = new TreeSet<>();
            leaders.add(0);
            for (int i = 0; i < n; i++) {
                String text = instructions.get(i).text.trim();
                String[] parts = splitParts(text);
                String op = parts[0];

                if (isBranch(op)) {
                    String label = parts[3].trim();
                    if (labelMap.containsKey(label)) leaders.add(labelMap.get(label));
                    if (i + 1 < n) leaders.add(i + 1);
                } else if (op.equals("goto")) {
                    String label = parts[1].trim();
                    if (labelMap.containsKey(label)) leaders.add(labelMap.get(label));
                    if (i + 1 < n) leaders.add(i + 1);
                }
            }

            for (int idx : labelMap.values()) leaders.add(idx);

            List<Integer> sorted = new ArrayList<>(leaders);
            basicBlocks.clear();
            for (int j = 0; j < sorted.size(); j++) {
                BasicBlock bb = new BasicBlock("bb" + j, sorted.get(j));
                bb.endLine = (j + 1 < sorted.size()) ? sorted.get(j + 1) - 1 : n - 1;
                basicBlocks.add(bb);
            }
        }

        public void buildCfg(Map<String, Integer> labelMap) {
            int n = instructions.size();

            Map<Integer, String> lineToBlock = new HashMap<>();
            for (BasicBlock bb : basicBlocks)
                for (int l = bb.startLine; l <= bb.endLine; l++)
                    lineToBlock.put(l, bb.id);

            for (BasicBlock bb : basicBlocks) {
                String lastText = instructions.get(bb.endLine).text.trim();

                if (lastText.endsWith(":")) {
                    addFallT(bb, bb.endLine, n, lineToBlock);
                    continue;
                }

                String[] parts = splitParts(lastText);
                String op = parts[0];

                if (op.equals("goto")) {
                    addBranchT(bb, parts[1].trim(), labelMap, lineToBlock);
                } else if (isBranch(op)) {
                    addFallT(bb, bb.endLine, n, lineToBlock);
                    addBranchT(bb, parts[3].trim(), labelMap, lineToBlock);
                } else if (op.equals("return")) {
                } else {
                    addFallT(bb, bb.endLine, n, lineToBlock);
                }
            }
        }


        public void linkInstSuccessor() {
            Map<String, BasicBlock> idToBlock = new LinkedHashMap<>();
            for (BasicBlock bb : basicBlocks) idToBlock.put(bb.id, bb);

            for (BasicBlock bb : basicBlocks) {
                for (int l = bb.startLine; l < bb.endLine; l++)
                    instructions.get(l).successors.add(instructions.get(l + 1));

                Instruction last = instructions.get(bb.endLine);
                for (String sid : bb.successors) {
                    BasicBlock sb = idToBlock.get(sid);
                    if (sb != null)
                        last.successors.add(instructions.get(sb.startLine));
                }
            }
        }


            public void compDU(Set<String> excluded) {
                for (BasicBlock bb : basicBlocks) {
                    bb.localSpillCosts.clear();
                    for (int i = bb.startLine; i <= bb.endLine; i++) {
                        Instruction inst = instructions.get(i);
                        String text = inst.text.trim();
                        if (text.endsWith(":")) continue;

                        String[] parts = splitParts(text);
                        String op = parts[0];

                     if (op.equals("assign")) {
                            addDef(inst, parts[1], excluded);
                            addUse(inst, parts[2], excluded);
                            countDef(bb, parts[1], excluded);
                            countUse(bb, parts[2], excluded);
                        } else if (op.equals("add") || op.equals("sub")|| op.equals("mult") || op.equals("div") || op.equals("and") || op.equals("or")) {
                            addUse(inst, parts[1], excluded);
                            addUse(inst, parts[2], excluded);
                            if (parts.length > 3) addDef(inst, parts[3], excluded);
                            countUse(bb, parts[1], excluded);
                            countUse(bb, parts[2], excluded);
                            if (parts.length > 3) countDef(bb, parts[3], excluded);
                        } else if (op.equals("breq") || op.equals("brneq") ||op.equals("brlt") || op.equals("brgt") || op.equals("brleq") ||op.equals("brgeq")) {
                            addUse(inst, parts[1], excluded);
                            addUse(inst, parts[2], excluded);
                            countUse(bb, parts[1], excluded);
                            countUse(bb, parts[2], excluded);
                        } else if (op.equals("goto")) {
                 
                        } else if (op.equals("return")) {
                            if (parts.length > 1) {
                                addUse(inst, parts[1], excluded);
                                countUse(bb, parts[1], excluded);
                            }
                        } else if (op.equals("call")) {
                            for (int j = 2; j < parts.length; j++) {
                                addUse(inst, parts[j], excluded);
                                countUse(bb, parts[j], excluded);
                            }
                        } else if (op.equals("callr")) {
                            addDef(inst, parts[1], excluded);
                            countDef(bb, parts[1], excluded);
                            for (int j = 3; j < parts.length; j++) {
                                addUse(inst, parts[j], excluded);
                                countUse(bb, parts[j], excluded);
                            }
                        } else if (op.equals("array_store")) {
                            addUse(inst, parts[2], excluded);
                            addUse(inst, parts[3], excluded);
                            countUse(bb, parts[2], excluded);
                            countUse(bb, parts[3], excluded);
                        } else if (op.equals("array_load")) {
                            addDef(inst, parts[1], excluded);
                            addUse(inst, parts[3], excluded);
                            countDef(bb, parts[1], excluded);
                            countUse(bb, parts[3], excluded);
                        }
                    }
                }
            }


        public void calLiveness() {
            boolean changed = true;
            while (changed) {
                changed = false;
                for (int i = instructions.size() - 1; i >= 0; i--) {
                    Instruction inst = instructions.get(i);
                    Set<String> oldIn  = new HashSet<>(inst.in);
                    Set<String> oldOut = new HashSet<>(inst.out);
                    inst.out.clear();
                    for (Instruction succ : inst.successors)
                        inst.out.addAll(succ.in);
                    inst.in.clear();
                    inst.in.addAll(inst.use);
                    Set<String> diff = new HashSet<>(inst.out);
                    diff.removeAll(inst.def);
                    inst.in.addAll(diff);
                    if (!inst.in.equals(oldIn) || !inst.out.equals(oldOut))
                        changed = true;
                }
            }
        }



        public void calGlobalCost() {
            Map<String, Integer> loopDepths = comLoopDepth();
            globalSpillCosts.clear();
            for (BasicBlock bb : basicBlocks) {
                int depth  = loopDepths.getOrDefault(bb.id, 0);
                int weight = (int) Math.pow(10, depth);
                for (Map.Entry<String, Integer> e : bb.localSpillCosts.entrySet())
                    globalSpillCosts.merge(e.getKey(), e.getValue() * weight, Integer::sum);
            }
        }

  
        private Map<String, Integer> comLoopDepth() {
            List<String> ids = new ArrayList<>();
            Map<String, Set<String>> preds = new HashMap<>();
            for (BasicBlock bb : basicBlocks) {
                ids.add(bb.id);
                preds.putIfAbsent(bb.id, new HashSet<>());
                for (String s : bb.successors)
                    preds.computeIfAbsent(s, nothing -> new HashSet<>()).add(bb.id);
            }


            Map<String, Set<String>> dom = new HashMap<>();
            for (String id : ids) dom.put(id, new HashSet<>(ids));
            dom.put(ids.get(0), new HashSet<>(Set.of(ids.get(0))));
            boolean ch = true;
            while (ch) {
                ch = false;
                for (int i = 1; i < ids.size(); i++) {
                    String b = ids.get(i);
                    Set<String> ps = preds.getOrDefault(b, Collections.emptySet());
                    Set<String> nd = null;
                    for (String p : ps) {
                        if (nd == null) nd = new HashSet<>(dom.get(p));
                        else nd.retainAll(dom.get(p));
                    }
                    if (nd == null) nd = new HashSet<>();
                    nd.add(b);
                    if (!nd.equals(dom.get(b))) { dom.put(b, nd); ch = true; }
                }
            }


            List<String[]> backEdges = new ArrayList<>();
            for (BasicBlock bb : basicBlocks)
                for (String s : bb.successors)
                    if (dom.getOrDefault(bb.id, Collections.emptySet()).contains(s))
                        backEdges.add(new String[]{bb.id, s});

            Map<String, Integer> depth = new LinkedHashMap<>();
            for (String id : ids) depth.put(id, 0);
            for (String[] edge : backEdges) {
                String tail = edge[0], header = edge[1];
                Set<String> body = new HashSet<>();
                body.add(header);
                if (!tail.equals(header)) {
                    body.add(tail);
                    Deque<String> stk = new ArrayDeque<>();
                    stk.push(tail);
                    while (!stk.isEmpty()) {
                        String node = stk.pop();
                        for (String p : preds.getOrDefault(node, Collections.emptySet()))
                            if (!body.contains(p)) { body.add(p); stk.push(p); }
                    }
                }
                for (String b : body) depth.merge(b, 1, Integer::sum);
            }
            return depth;
        }


        public void analyze(Set<String> excluded) {
            Map<String, Integer> labelMap = buildLabelMap();
            buildBb(labelMap);
            buildCfg(labelMap);
            linkInstSuccessor();
            compDU(excluded);
            calLiveness();
            calGlobalCost();
        }


        private void addFallT(BasicBlock bb, int lastLine, int n,
                                    Map<Integer, String> lineToBlock) {
            int fall = lastLine + 1;
            if (fall < n && lineToBlock.containsKey(fall))
                if (!bb.successors.contains(lineToBlock.get(fall)))
                    bb.successors.add(lineToBlock.get(fall));
        }

        private void addBranchT(BasicBlock bb, String label,
                Map<String, Integer> labelMap, Map<Integer, String> lineToBlock) {
            if (labelMap.containsKey(label) && lineToBlock.containsKey(labelMap.get(label))) {
                String target = lineToBlock.get(labelMap.get(label));
                if (!bb.successors.contains(target))
                    bb.successors.add(target);
            }
        }
    }



    static List<String> staticVars;
    static Set<String>   globalArrays;
    static List<FunctionDa> functions;
    static Map<String, String>  globalTypeMap  = new LinkedHashMap<>();
    static Map<String, Integer> globalArraySize = new LinkedHashMap<>();

    static void parseIR(List<String> lines) {
        staticVars = new ArrayList<>();
        globalArrays = new LinkedHashSet<>();
        functions = new ArrayList<>();
        globalTypeMap   = new LinkedHashMap<>(); 
        globalArraySize = new LinkedHashMap<>();    

        boolean inFunction = false;
        FunctionDa cur = null;
        int instrIndex = 0;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            if (line.startsWith("start_program") || line.startsWith("end_program")) continue;

            if (line.startsWith("declare") && !inFunction) {
                String[] da = parseDeclare(line);
                staticVars.add(da[0]);
                globalTypeMap.put(da[0], da[1].equals("array") ? da[2] + "[]" : da[2]);
                if (da[1].equals("array")) {
                    globalArrays.add(da[0]);
                    globalArraySize.put(da[0], Integer.parseInt(da[3]));
                }
                continue;
            }

            if (line.startsWith("declare") && inFunction && cur != null) {
                String[] da = parseDeclare(line);
                String name = da[0];
                if (da[1].equals("array")) {
                    cur.localArrays.add(name);
                    cur.typeMap.put(name, da[2] + "[]");
                    cur.arraySize.put(name, Integer.parseInt(da[3]));
                } else {
                    cur.localVars.add(name);
                    cur.typeMap.put(name, da[2]);
                }
                continue;
            }

            if (line.startsWith("start_function")) {
                inFunction = true;
                cur = parseFunctionHeader(line);
                instrIndex = 0;
                continue;
            }

            if (line.startsWith("end_function")) {
                inFunction = false;
                functions.add(cur);
                cur = null;
                continue;
            }

            if (inFunction && cur != null) {
                cur.instructions.add(new Instruction(instrIndex++, line));
            }
        }
    }




    static String[] parseDeclare(String line) {
        String rest = line.substring("declare".length()).trim();
        int colon = rest.indexOf(':');
        String name = rest.substring(0, colon).trim();
        String type = rest.substring(colon + 1).trim();
        if (type.startsWith("[")) {
            int close    = type.indexOf(']');
            String size  = type.substring(1, close).trim();
            String base  = type.substring(close + 1).trim(); // "int" or "float"
            return new String[]{name, "array", base, size};
        } else {
            return new String[]{name, "scalar", type, "1"};
        }
    }

    static FunctionDa parseFunctionHeader(String line) {
        FunctionDa f = new FunctionDa();
        Matcher m = Pattern.compile(
            "start_function\\s+(\\w+)\\s*\\((.*)\\)\\s*:\\s*(\\w+)"
        ).matcher(line);
        if (m.find()) {
            f.name    = m.group(1);
            f.retType = m.group(3);
            String ps = m.group(2).trim();
            if (!ps.isEmpty()) {
                for (String p : ps.split(",")) {
                    String[] kv = p.trim().split(":");
                    String pName = kv[0].trim();
                    String pType = kv.length > 1 ? kv[1].trim() : "int";
                    f.params.add(new String[]{pName, pType});
                    f.typeMap.put(pName, pType);
                }
            }
        } else {
            String[] parts = line.split("\\s+");
            f.name = parts.length > 1 ? parts[1] : "unknown";
            f.retType = "void";
        }
        return f;
    }



    static String[] splitParts(String instr) {
        String[] parts = instr.split(",");
        for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();
        return parts;
    }

    static boolean isLiteral(String s) {
        if (s == null || s.isEmpty()) return true;
        try { Integer.parseInt(s);  return true; } catch (NumberFormatException e) {}
        try { Double.parseDouble(s); return true; } catch (NumberFormatException e) {}
        return false;
    }

    static boolean isBranch(String op) {
        return Set.of("breq","brneq","brlt","brleq","brgeq","brgt").contains(op);
    }

    static void addDef(Instruction inst, String name, Set<String> excluded) {
        name = name.trim();
        if (!name.isEmpty() && !excluded.contains(name) && !isLiteral(name)) inst.def.add(name);
    }
    static void addUse(Instruction inst, String name, Set<String> excluded) {
        name = name.trim();
        if (!name.isEmpty() && !excluded.contains(name) && !isLiteral(name)) inst.use.add(name);
    }



    static String toJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        sb.append("  \"static_vars\": [");
        for (int i = 0; i < staticVars.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(staticVars.get(i)).append("\"");
        }
        sb.append("],\n");

        sb.append("  \"functions\": [\n");
        for (int f = 0; f < functions.size(); f++) {
            if (f > 0) sb.append(",\n");
            FunctionDa func = functions.get(f);
            int n = func.instructions.size();

            sb.append("    {\n");
            sb.append("      \"name\": \"").append(func.name).append("\",\n");

            sb.append("      \"instructions\": {\n");
            for (int i = 0; i < n; i++) {
                sb.append("        \"").append(i).append("\": \"")
                  .append(escapeJson(func.instructions.get(i).text)).append("\"");
                if (i < n - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      },\n");

            sb.append("      \"basic_blocks\": [\n");
            for (int j = 0; j < func.basicBlocks.size(); j++) {
                BasicBlock bb = func.basicBlocks.get(j);
                sb.append("        { \"id\": \"").append(bb.id)
                  .append("\", \"start_line\": ").append(bb.startLine)
                  .append(", \"end_line\": ").append(bb.endLine)
                  .append(", \"successors\": [");
                for (int k = 0; k < bb.successors.size(); k++) {
                    if (k > 0) sb.append(", ");
                    sb.append("\"").append(bb.successors.get(k)).append("\"");
                }
                sb.append("] }");
                if (j < func.basicBlocks.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      ],\n");

            sb.append("      \"liveness\": {\n");
            for (int i = 0; i < n; i++) {
                Instruction inst = func.instructions.get(i);
                List<String> inList  = new ArrayList<>(inst.in);
                List<String> outList = new ArrayList<>(inst.out);
                Collections.sort(inList);
                Collections.sort(outList);
                sb.append("        \"").append(i).append("\": { \"in\": ")
                  .append(jsonArray(inList)).append(", \"out\": ")
                  .append(jsonArray(outList)).append(" }");
                if (i < n - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      },\n");
            sb.append("      \"local_spill_costs\": {\n");
            List<BasicBlock> nonEmpty = new ArrayList<>();
            for (BasicBlock bb : func.basicBlocks)
                if (!bb.localSpillCosts.isEmpty()) nonEmpty.add(bb);
            for (int j = 0; j < nonEmpty.size(); j++) {
                BasicBlock bb = nonEmpty.get(j);
                sb.append("        \"").append(bb.id).append("\": {\n");
                List<String> vars = new ArrayList<>(bb.localSpillCosts.keySet());
                for (int k = 0; k < vars.size(); k++) {
                    sb.append("          \"").append(vars.get(k)).append("\": ")
                      .append(bb.localSpillCosts.get(vars.get(k)));
                    if (k < vars.size() - 1) sb.append(",");
                    sb.append("\n");
                }
                sb.append("        }");
                if (j < nonEmpty.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      },\n");

            sb.append("      \"global_spill_costs\": {\n");
            List<String> gKeys = new ArrayList<>(func.globalSpillCosts.keySet());
            for (int k = 0; k < gKeys.size(); k++) {
                sb.append("        \"").append(gKeys.get(k)).append("\": ")
                  .append(func.globalSpillCosts.get(gKeys.get(k)));
                if (k < gKeys.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      }\n");
            sb.append("    }");
        }
        sb.append("\n  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    static String jsonArray(List<String> items) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("\"").append(items.get(i)).append("\"");
        }
        return sb.append("]").toString();
    }

}
