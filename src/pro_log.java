// ---- all allocation regs treated as callee-saved ----
String[] allIntRegs = {"$s0", "$s1", "$s2", "$t0", "$t1", "$t2"};
String[] allFloatRegs = {"$f4", "$f5", "$f6", "$f20", "$f21", "$f22"};

// ---- used regs ----
Set<String> usedRegs = new HashSet<>();
for (String var : func.localVars) {
    String reg = alloc.get(var);
    if (reg != null && !reg.equals("spilled")) usedRegs.add(reg);
}
for (String[] p : func.params) {
    String reg = alloc.get(p[0]);
    if (reg != null && !reg.equals("spilled")) usedRegs.add(reg);
}

// ---- maxArgs ----
int maxArgs = 0;
for (DataFlowAnalyzer.Instruction inst : func.instructions) {
    String[] parts = DataFlowAnalyzer.splitParts(inst.text.trim());
    if (parts[0].equals("call")) maxArgs = Math.max(maxArgs, parts.length - 2);
    else if (parts[0].equals("callr")) maxArgs = Math.max(maxArgs, parts.length - 3);
}

// ---- frameSize ----
int frameSize = 4; // $ra only
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

// ---- label ----
if (func.name.equals("main")) mipsLines.add(".globl main");
mipsLines.add(func.name + ":");

// ---- prologue ----
mipsLines.add("addi $sp, $sp, -" + frameSize);
mipsLines.add("sw $ra, " + (frameSize - 4) + "($sp)");

// ---- save callee-saved regs (all $sp-relative) ----
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

// ---- handle incoming params ----
String[] intArgRegs = {"$a0", "$a1", "$a2", "$a3"};
String[] floatArgRegs = {"$f12", "$f13", "$f14", "$f15"};
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

// ---- spilled scalar locals ----
for (String v : func.localVars) {
    if (!func.localArrays.contains(v)) {
        String reg = alloc.get(v);
        if (reg == null || reg.equals("spilled")) {
            varOffset.put(v, offset);
            offset -= 4;
        }
    }
}

// ---- local arrays ----
for (String arr : func.localArrays) {
    int size = func.arraySize.get(arr);
    varOffset.put(arr, offset);
    offset -= size * 4;
}