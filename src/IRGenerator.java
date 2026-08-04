import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class IRGenerator extends TigerBaseVisitor<String> {
    private List<String> globalInstructions = new ArrayList<>();
    private List<String> currentFuncDecls   = new ArrayList<>();
    private List<String> currentFuncInstrs  = new ArrayList<>();
    private List<String> globalInits        = new ArrayList<>();

    private Stack<String> loopEndLabels = new Stack<>();

    private int tempCounter  = 0;
    private int labelCounter = 0;
    private SymbolTable symTable;

    public IRGenerator(SymbolTable symTable) {
        this.symTable = symTable;
    }
    public List<String> getInstructions() { return globalInstructions; }



    private String newTemp(String type) {
        String t = "_t" + (++tempCounter);
        String declStr = "declare " + t + ": " + type;
        if (symTable.getCurrentScope().level == 0) {
            globalInstructions.add(declStr);
        } else {
            currentFuncDecls.add(declStr);
        }
        symTable.insert(new SymbolTable.Symbol(t, "temp", type));
        return t;
    }

    private String newLabel() {
        return "_L" + (++labelCounter);
    }


private String getType(String nameOrLiteral) {
        if (nameOrLiteral == null) return "int";
        if (nameOrLiteral.matches("-?\\d+\\.\\d*") || nameOrLiteral.matches("-?\\d*\\.\\d+")) {
            return "float";
        }
        try { 
            Integer.parseInt(nameOrLiteral); 
            return "int"; 
        } catch (NumberFormatException e) { }

        SymbolTable.Symbol sym;
        if (nameOrLiteral.matches("_\\d+_.+")) {
            sym = symTable.lookUp(nameOrLiteral.replaceFirst("_\\d+_", ""));
        } else {
            sym = symTable.lookUp(nameOrLiteral);
        }

        if (sym != null) {
            if ("array".equals(sym.basetype) && sym.elementType != null) {
                return sym.elementType;
            }
            if ("temp".equals(sym.basetype) && sym.elementType != null) {
                return sym.elementType;
            }
            return sym.basetype != null ? sym.basetype : "int";
        }
        return "int"; 
    }



    @Override
    public String visitTiger_program(TigerParser.Tiger_programContext ctx) {
        symTable.resetPointer();
        String progName = ctx.ID().getText();
        globalInstructions.add("start_program " + progName);
        globalInstructions.add("");

        symTable.reEnterNextScope(); 
        visitChildren(ctx);
        symTable.finalizeScope();

        globalInstructions.add("end_program");
        return null;
    }

    @Override
    public String visitVar_declaration(TigerParser.Var_declarationContext ctx) {
        boolean isGlobal = symTable.getCurrentScope().level == 0;
        String resolvedType = "int";
        int    arraySize    = 0;
        String elementType  = null;
        boolean isArray     = false;

        if (ctx.type().ARRAY() != null) {
            isArray     = true;
            arraySize   = Integer.parseInt(ctx.type().INTLIT().getText());
            elementType = ctx.type().base_type().getText();
            resolvedType = elementType;
        } else if (ctx.type().ID() != null) { 
            SymbolTable.Symbol typeSym = symTable.lookUp(ctx.type().ID().getText());
            if (typeSym != null) {
                if ("array".equals(typeSym.basetype)) {
                    isArray      = true;
                    arraySize    = typeSym.arraySize;
                    elementType  = typeSym.elementType;
                    resolvedType = elementType;
                } else {
                    resolvedType = typeSym.basetype;
                }
            }
        } else {
            resolvedType = ctx.type().base_type().getText();
        }
        TigerParser.Id_listContext current = ctx.id_list();
        while (current != null) {
            String originalName = current.ID().getText();
            String mangledName  = symTable.getMangledName(originalName);
            String declStr;
            if (isArray) {
                declStr = "declare " + mangledName + ": [" + arraySize + "] " + elementType;
            } else {
                declStr = "declare " + mangledName + ": " + resolvedType;
            }
            if (isGlobal) globalInstructions.add(declStr);
            else          currentFuncDecls.add(declStr);
            if (ctx.optional_init() != null && ctx.optional_init().ASSIGN() != null) {
                            String initVal = ctx.optional_init().constant().getText();
                            List<String> targetList = isGlobal ? globalInits : currentFuncInstrs;
                            if (isArray) {
                                String indexTemp = newTemp("int");
                                String startLabel = newLabel();
                                String endLabel = newLabel();
                                targetList.add("assign, " + indexTemp + ", 0");
                                targetList.add(startLabel + ":");
                                targetList.add("brgeq, " + indexTemp + ", " + arraySize + ", " + endLabel);
                                targetList.add("array_store, " + mangledName + ", " + indexTemp + ", " + initVal);
                                targetList.add("add, " + indexTemp + ", 1, " + indexTemp);
                                targetList.add("goto, " + startLabel);
                                targetList.add(endLabel + ":");
                            } else {
                                targetList.add("assign, " + mangledName + ", " + initVal);
                            }
                        }
            current = current.id_list();
        }
        return null;
    }

    @Override
    public String visitFunct(TigerParser.FunctContext ctx) {
        String funName = ctx.ID().getText();
        symTable.reEnterNextScope();
        currentFuncDecls.clear();
        currentFuncInstrs.clear();
        StringBuilder signature = new StringBuilder("start_function " + funName + "(");
        TigerParser.Param_listContext plist = ctx.param_list();
        if (plist != null && plist.param() != null) {
            String pName = symTable.getMangledName(plist.param().ID().getText());
            String pType = resolveParamType(plist.param().type());
            signature.append(pName).append(": ").append(pType);
            TigerParser.Param_list_tailContext tail = plist.param_list_tail();
            while (tail != null && tail.param() != null) {
                pName = symTable.getMangledName(tail.param().ID().getText());
                pType = resolveParamType(tail.param().type());
                signature.append(", ").append(pName).append(": ").append(pType);
                tail = tail.param_list_tail();
            }
        }

  
        String retType = "void";
        if (ctx.ret_type() != null && ctx.ret_type().type() != null) {
            TigerParser.TypeContext retCtx = ctx.ret_type().type();
            if (retCtx.base_type() != null && retCtx.ARRAY() == null) {
                retType = retCtx.base_type().getText(); 
            } else if (retCtx.ID() != null) {
          
                SymbolTable.Symbol s = symTable.lookUp(retCtx.ID().getText());
                if (s != null) {
                    retType = "array".equals(s.basetype) ? s.elementType : s.basetype;
                }
            } else if (retCtx.ARRAY() != null) {
                retType = retCtx.base_type().getText();
            }
        }

        signature.append("): ").append(retType);
        globalInstructions.add(signature.toString());

      
        if ("main".equals(funName)) {
            currentFuncInstrs.addAll(globalInits);
        }

        visit(ctx.stat_seq());

       
        boolean hasReturn = !currentFuncInstrs.isEmpty() &&
                            currentFuncInstrs.get(currentFuncInstrs.size() - 1).startsWith("return");
        if (!hasReturn) {
            currentFuncInstrs.add("return");
        }


        globalInstructions.addAll(currentFuncDecls);
        globalInstructions.addAll(currentFuncInstrs);
        globalInstructions.add("end_function");
        globalInstructions.add("");

        symTable.finalizeScope();
        return null;
    }


    private String resolveParamType(TigerParser.TypeContext typeCtx) {
        if (typeCtx.ARRAY() != null) {
            if (typeCtx.base_type() != null) return typeCtx.base_type().getText();
            if (typeCtx.ID() != null) {
                SymbolTable.Symbol s = symTable.lookUp(typeCtx.ID().getText());
                if (s != null && s.elementType != null) return s.elementType;
            }
        }
        if (typeCtx.ID() != null) {
            SymbolTable.Symbol s = symTable.lookUp(typeCtx.ID().getText());
            if (s != null) {
                return "array".equals(s.basetype) ? s.elementType : s.basetype;
            }
        }
        if (typeCtx.base_type() != null) {
            return typeCtx.base_type().getText(); 
        }
        return "int";
    }



    @Override
    public String visitStat(TigerParser.StatContext ctx) {
        if (ctx.LET() != null) {
            symTable.reEnterNextScope();
            visit(ctx.declaration_segment());
            visit(ctx.stat_seq(0));
            symTable.finalizeScope();
            return null;
        }
        if (ctx.value() != null && ctx.ASSIGN() != null) {
            String rhsTemp     = visit(ctx.expr(0));
            String varName     = ctx.value().ID().getText();
            String mangledName = symTable.getMangledName(varName);
            SymbolTable.Symbol lhsSym = symTable.lookUp(varName);

            if (ctx.value().value_tail() != null &&
                ctx.value().value_tail().LBRACK() != null) {
                String indexTemp = visit(ctx.value().value_tail().expr());
                currentFuncInstrs.add("array_store, " + mangledName + ", " + indexTemp + ", " + rhsTemp);
            } else if (lhsSym != null && "array".equals(lhsSym.basetype)) {
                String indexTemp = newTemp("int");
                String startLabel = newLabel();
                String endLabel = newLabel();
                
                SymbolTable.Symbol rhsSym = symTable.lookUp(ctx.expr(0).getText());
                boolean rhsIsArray = rhsSym != null && "array".equals(rhsSym.basetype);

                currentFuncInstrs.add("assign, " + indexTemp + ", 0");
                currentFuncInstrs.add(startLabel + ":");
                currentFuncInstrs.add("brgeq, " + indexTemp + ", " + lhsSym.arraySize + ", " + endLabel);
                
                if (rhsIsArray) {
                    String valTemp = newTemp(lhsSym.elementType);
                    currentFuncInstrs.add("array_load, " + valTemp + ", " + rhsTemp + ", " + indexTemp);
                    currentFuncInstrs.add("array_store, " + mangledName + ", " + indexTemp + ", " + valTemp);
                } else {
                    currentFuncInstrs.add("array_store, " + mangledName + ", " + indexTemp + ", " + rhsTemp);
                }
                
                currentFuncInstrs.add("add, " + indexTemp + ", 1, " + indexTemp);
                currentFuncInstrs.add("goto, " + startLabel);
                currentFuncInstrs.add(endLabel + ":");
            } else {
                currentFuncInstrs.add("assign, " + mangledName + ", " + rhsTemp);
            }
            return null;
        }
        if (ctx.IF() != null) {
            String condTemp   = visit(ctx.expr(0));
            String labelFalse = newLabel();
            currentFuncInstrs.add("breq, " + condTemp + ", 0, " + labelFalse);
            visit(ctx.stat_seq(0));
            if (ctx.ELSE() != null) {
                String labelEnd   = newLabel();
                currentFuncInstrs.add("goto, " + labelEnd);
                currentFuncInstrs.add(labelFalse + ":");
                visit(ctx.stat_seq(1));
                currentFuncInstrs.add(labelEnd + ":");
            } else {
                currentFuncInstrs.add(labelFalse + ":");
            }
            return null;
        }

    
        if (ctx.WHILE() != null) {
            String labelStart = newLabel();
            String labelEnd   = newLabel();
            loopEndLabels.push(labelEnd);
            currentFuncInstrs.add(labelStart + ":");
            String condTemp = visit(ctx.expr(0));
            currentFuncInstrs.add("breq, " + condTemp + ", 0, " + labelEnd);
            visit(ctx.stat_seq(0));
            currentFuncInstrs.add("goto, " + labelStart);
            currentFuncInstrs.add(labelEnd + ":");
            loopEndLabels.pop();
            return null;
        }

      
        if (ctx.FOR() != null) {
            String loopVar   = symTable.getMangledName(ctx.ID().getText());
            //String loopDecl = "declare " + loopVar + ": int";
            //            if (!currentFuncDecls.contains(loopDecl)) { 
            //                currentFuncDecls.add(loopDecl); 
            //            }
            String startTemp = visit(ctx.expr(0));
            String endTemp   = visit(ctx.expr(1));
            String labelStart = newLabel();
            String labelEnd   = newLabel();
            loopEndLabels.push(labelEnd);
            currentFuncInstrs.add("assign, " + loopVar + ", " + startTemp);
            currentFuncInstrs.add(labelStart + ":");
            currentFuncInstrs.add("brgt, " + loopVar + ", " + endTemp + ", " + labelEnd);
            visit(ctx.stat_seq(0));
            String incTemp = newTemp("int");
            currentFuncInstrs.add("add, " + loopVar + ", 1, " + incTemp);
            currentFuncInstrs.add("assign, " + loopVar + ", " + incTemp);
            currentFuncInstrs.add("goto, " + labelStart);
            currentFuncInstrs.add(labelEnd + ":");
            loopEndLabels.pop();
  
            return null;
        }

   
        if (ctx.BREAK() != null) {
            if (!loopEndLabels.isEmpty()) {
                currentFuncInstrs.add("goto, " + loopEndLabels.peek());
            }
            return null;
        }

   
        if (ctx.RETURN() != null) {
            if (ctx.optreturn() != null && ctx.optreturn().expr() != null) {
                String retTemp = visit(ctx.optreturn().expr());
                currentFuncInstrs.add("return, " + retTemp);
            } else {
                currentFuncInstrs.add("return");
            }
            return null;
        }

   
        if (ctx.LPAREN() != null) {
            String funName = ctx.ID().getText();

         
            StringBuilder args = new StringBuilder();
            if (ctx.expr_list() != null && ctx.expr_list().expr() != null) {
                args.append(", ").append(visit(ctx.expr_list().expr()));
                TigerParser.Expr_list_tailContext tail = ctx.expr_list().expr_list_tail();
                while (tail != null && tail.expr() != null) {
                    args.append(", ").append(visit(tail.expr()));
                    tail = tail.expr_list_tail();
                }
            }

            if (ctx.optprefix() != null && ctx.optprefix().value() != null) {
                TigerParser.ValueContext lval = ctx.optprefix().value();
                String assignee = symTable.getMangledName(lval.ID().getText());
                if (lval.value_tail() != null && lval.value_tail().LBRACK() != null) {
                    SymbolTable.Symbol funSym = symTable.lookUp(funName);
                    String retType = (funSym != null && funSym.basetype != null)
                                     ? funSym.basetype : "int";
                    String retTemp   = newTemp(retType);
                    String indexTemp = visit(lval.value_tail().expr());
                    currentFuncInstrs.add("callr, " + retTemp + ", " + funName + args);
                    currentFuncInstrs.add("array_store, " + assignee + ", " + indexTemp + ", " + retTemp);
                } else {
                    currentFuncInstrs.add("callr, " + assignee + ", " + funName + args);
                }
            } else {
                currentFuncInstrs.add("call, " + funName + args);
            }
            return null;
        }

        return null;
    }



    @Override
    public String visitValue(TigerParser.ValueContext ctx) {
        String originalName = ctx.ID().getText();
        String mangledName  = symTable.getMangledName(originalName);
        SymbolTable.Symbol sym = symTable.lookUp(originalName);

        if (ctx.value_tail() != null && ctx.value_tail().LBRACK() != null) {
   
            String indexTemp = visit(ctx.value_tail().expr());
            String elemType  = (sym != null && sym.elementType != null)
                               ? sym.elementType : "int";
            String valTemp   = newTemp(elemType);
            currentFuncInstrs.add("array_load, " + valTemp + ", " + mangledName + ", " + indexTemp);
            return valTemp;
        }

        return mangledName;
    }



    @Override
    public String visitExpr(TigerParser.ExprContext ctx) {


        if (ctx.constant() != null) {
            return ctx.constant().getText();
        }


        if (ctx.value() != null) {
            return visit(ctx.value());
        }

  
        if (ctx.LPAREN() != null) {
            return visit(ctx.expr(0));
        }


        if (ctx.expr().size() == 2) {
            String left  = visit(ctx.expr(0));
            String right = visit(ctx.expr(1));

            String leftType  = getType(left);
            String rightType = getType(right);

            boolean isRelational = ctx.EQ()!=null || ctx.NE()!=null || ctx.LT()!=null
                                || ctx.GT()!=null || ctx.LE()!=null || ctx.GE()!=null;
            boolean isLogical    = ctx.AND()!=null || ctx.OR()!=null;


            if (ctx.POW() != null) {
   
                String resultTemp  = newTemp(leftType);  
                String counterTemp = newTemp("int");
                String labelStart  = newLabel();
                String labelEnd    = newLabel();

                currentFuncInstrs.add("assign, " + resultTemp  + ", 1");
                currentFuncInstrs.add("assign, " + counterTemp + ", " + right);
                currentFuncInstrs.add(labelStart + ":");
                currentFuncInstrs.add("brleq, " + counterTemp + ", 0, " + labelEnd);
                String mulTemp = newTemp(leftType);
                currentFuncInstrs.add("mult, " + resultTemp + ", " + left + ", " + mulTemp);
                currentFuncInstrs.add("assign, " + resultTemp + ", " + mulTemp);
                String decTemp = newTemp("int");
                currentFuncInstrs.add("sub, " + counterTemp + ", 1, " + decTemp);
                currentFuncInstrs.add("assign, " + counterTemp + ", " + decTemp);
                currentFuncInstrs.add("goto, " + labelStart);
                currentFuncInstrs.add(labelEnd + ":");
                return resultTemp; 
            }

   
            String resultType;
            if (isRelational || isLogical) {
                resultType = "int";
            } else if ("float".equals(leftType) || "float".equals(rightType)) {
                resultType = "float";
            } else {
                resultType = "int";
            }

            String temp = newTemp(resultType);

            if (ctx.ADD() != null)
                currentFuncInstrs.add("add, "  + left + ", " + right + ", " + temp);
            else if (ctx.SUB() != null)
                currentFuncInstrs.add("sub, "  + left + ", " + right + ", " + temp);
            else if (ctx.MUL() != null)
                currentFuncInstrs.add("mult, " + left + ", " + right + ", " + temp);
            else if (ctx.DIV() != null)
                currentFuncInstrs.add("div, "  + left + ", " + right + ", " + temp);
            else if (ctx.AND() != null)
                currentFuncInstrs.add("and, "  + left + ", " + right + ", " + temp);
            else if (ctx.OR()  != null)
                currentFuncInstrs.add("or, "   + left + ", " + right + ", " + temp);
            else if (isRelational) {
                String trueLabel = newLabel();
                String endLabel  = newLabel();
                String op;
                if(ctx.EQ()  != null) op = "breq";
                else if (ctx.NE()  != null) op = "brneq";
                else if (ctx.LT()  != null) op = "brlt";
                else if (ctx.GT()  != null) op = "brgt";
                else if (ctx.LE()  != null) op = "brleq";
                else                        op = "brgeq";

                currentFuncInstrs.add(op + ", " + left + ", " + right + ", " + trueLabel);
                currentFuncInstrs.add("assign, " + temp + ", 0");
                currentFuncInstrs.add("goto, " + endLabel);
                currentFuncInstrs.add(trueLabel + ":");
                currentFuncInstrs.add("assign, " + temp + ", 1");
                currentFuncInstrs.add(endLabel + ":");
            }

            return temp;
        }

        return null;
    }
}