import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.List;

public class SemanticChecker extends TigerBaseVisitor<String> {
    private SymbolTable symTable;
    public boolean hasErrors = false;

    public SemanticChecker(SymbolTable symTable) {
        this.symTable = symTable;
        this.symTable.initializeScope("global");
        List<SymbolTable.Param> emptyParams = new ArrayList<>();
        
        List<SymbolTable.Param> intParam = new ArrayList<>();
        intParam.add(new SymbolTable.Param("i", "int"));
        
        List<SymbolTable.Param> floatParam = new ArrayList<>();
        floatParam.add(new SymbolTable.Param("f", "float"));
        this.symTable.insert(new SymbolTable.Symbol("printi", "function", intParam, null));
        this.symTable.insert(new SymbolTable.Symbol("printf", "function", floatParam, null));
        this.symTable.insert(new SymbolTable.Symbol("readi", "function", emptyParams, "int"));
        this.symTable.insert(new SymbolTable.Symbol("readf", "function", emptyParams, "float"));
        this.symTable.insert(new SymbolTable.Symbol("not", "function", intParam, "int"));
        this.symTable.insert(new SymbolTable.Symbol("exit", "function", intParam, null));
    }

    private void error(int line, int col, String msg) {
        System.err.println("line " + line + ":" + col + " " + msg);
        hasErrors = true;
    }

    @Override
    public String visitTiger_program(TigerParser.Tiger_programContext ctx) {
        String progName = ctx.ID().getText();
        visitChildren(ctx);
        SymbolTable.Symbol mainSym = symTable.lookUp("main");
        if (mainSym == null || !"function".equals(mainSym.kind)) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Program must contain a function named 'main'");
        } else if (mainSym.basetype != null && !mainSym.basetype.equals("void")) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Function 'main' must have a void return type");
        } else if (mainSym.paramTypes != null && !mainSym.paramTypes.isEmpty()) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Function 'main' must not accept parameters");
        }
        symTable.finalizeScope();
        return null;
    }


    @Override 
    public String visitType_declaration(TigerParser.Type_declarationContext ctx) {
        String newTypeName = ctx.ID().getText();
        TigerParser.TypeContext typeCtx = ctx.type();
        SymbolTable.Symbol newTypeSym;

        // Array Type 
        if (typeCtx.ARRAY() != null) {
            int size = Integer.parseInt(typeCtx.INTLIT().getText());
            String elemType = typeCtx.base_type().getText(); 
            
  
            newTypeSym = new SymbolTable.Symbol(newTypeName, "type", "array", size, elemType);
        } 
        // ID 
        else if (typeCtx.ID() != null) {
            String targetName = typeCtx.ID().getText();
            SymbolTable.Symbol targetSym = symTable.lookUp(targetName);

            if (targetSym == null) {
                error(typeCtx.ID().getSymbol().getLine(), 
                    typeCtx.ID().getSymbol().getCharPositionInLine(), 
                    "Undefined type: " + targetName);
                return null;
            }
            if ("array".equals(targetSym.basetype)) {
                newTypeSym = new SymbolTable.Symbol(
                    newTypeName, 
                    "type", 
                    "array", 
                    targetSym.arraySize, 
                    targetSym.elementType
                );
            } else {
                newTypeSym = new SymbolTable.Symbol(newTypeName, "type", targetSym.basetype);
            }
        }
        // Base Type 
        else {
            String targetType = typeCtx.base_type().getText(); // "int" or "float"
            newTypeSym = new SymbolTable.Symbol(newTypeName, "type", targetType);
        }

        if (!symTable.insert(newTypeSym)) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), 
                "type " + newTypeName + " already declared");
        }
        return null;
    }
    @Override
    public String visitType(TigerParser.TypeContext ctx) {
        if (ctx.ID() != null) {
        String typeName = ctx.ID().getText();
        if (symTable.lookUp(typeName) == null) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Undefined type: " + typeName);
            return null; 
        }
        return typeName;
        }
        if (ctx.base_type() != null && ctx.ARRAY() == null) {
            return ctx.base_type().getText();
        }
        if (ctx.ARRAY() != null) {
            return "array"; 
        }
        return null;
    }

    @Override
    public String visitFunct(TigerParser.FunctContext ctx) {
        String funName = ctx.ID().getText();
        String retBaseType = null; 
        if (ctx.ret_type() != null && ctx.ret_type().type() != null) {
            TigerParser.TypeContext retCtx = ctx.ret_type().type();
            if (retCtx.ID() != null) {
                SymbolTable.Symbol existing = symTable.lookUp(retCtx.ID().getText());
                if (existing != null) {
                    if ("array".equals(existing.basetype)) {
                        error(retCtx.start.getLine(), retCtx.start.getCharPositionInLine(), "Semantic error: Function cannot return an array");
                    }
                    retBaseType = existing.basetype;
                } else {
                    error(retCtx.start.getLine(), retCtx.start.getCharPositionInLine(), "Undefined return type: " + retCtx.ID().getText());
                }
            } else if (retCtx.ARRAY() != null) {
                error(retCtx.start.getLine(), retCtx.start.getCharPositionInLine(), "Semantic error: Function cannot return an array");
                retBaseType = "array";
            } else {
                retBaseType = retCtx.base_type().getText();
            }
        }
        List<TigerParser.ParamContext> paramCtxs = new ArrayList<>();
        TigerParser.Param_listContext plist = ctx.param_list();
        if (plist != null && plist.param() != null) {
            paramCtxs.add(plist.param());
            TigerParser.Param_list_tailContext tail = plist.param_list_tail();
            while (tail != null && tail.param() != null) {
                paramCtxs.add(tail.param());
                tail = tail.param_list_tail();
            }
        }
        List<SymbolTable.Param> p = new ArrayList<>();
        for (TigerParser.ParamContext pCtx : paramCtxs) {
            String paramName = pCtx.ID().getText();
            TigerParser.TypeContext typeCtx = pCtx.type();
            String paramBaseType = null;

            if (typeCtx.ID() != null) {
                SymbolTable.Symbol existing = symTable.lookUp(typeCtx.ID().getText());
                if (existing != null) {
                    if ("array".equals(existing.basetype)) {
                        error(typeCtx.start.getLine(), typeCtx.start.getCharPositionInLine(), "Semantic error: Function parameter cannot be an array");
                    }
                    paramBaseType = existing.basetype;
                } else {
                    error(typeCtx.start.getLine(), typeCtx.start.getCharPositionInLine(), "Undefined parameter type: " + typeCtx.ID().getText());
                }
            } else if (typeCtx.ARRAY() != null) {
                error(typeCtx.start.getLine(), typeCtx.start.getCharPositionInLine(), "Semantic error: Function parameter cannot be an array");
                paramBaseType = "array";
            } else if (typeCtx.base_type() != null) {
                paramBaseType = typeCtx.base_type().getText();
            }
            p.add(new SymbolTable.Param(paramName, paramBaseType));
        }
        SymbolTable.Symbol newFunSym = new SymbolTable.Symbol(funName, "function", p, retBaseType);
        if (!symTable.insert(newFunSym)) {
            error(ctx.ID().getSymbol().getLine(), ctx.ID().getSymbol().getCharPositionInLine(), "Duplicate function declaration: " + funName);
        }
        symTable.initializeScope("Function " + funName);
        for (int i = 0; i < p.size(); i++) {
            SymbolTable.Param param = p.get(i);
            TigerParser.TypeContext originalTypeCtx = paramCtxs.get(i).type();
            SymbolTable.Symbol paramSym = null;

            if (param.type != null) {
                if ("array".equals(param.type)) {
                    if (originalTypeCtx.ID() != null) {
                        SymbolTable.Symbol typeDef = symTable.lookUp(originalTypeCtx.ID().getText());
                        if (typeDef != null) {
                            paramSym = new SymbolTable.Symbol(param.id, "var", "array", typeDef.arraySize, typeDef.elementType);
                        }
                    } else {
                        int size = Integer.parseInt(originalTypeCtx.INTLIT().getText());
                        String elemType = originalTypeCtx.base_type().getText();
                        paramSym = new SymbolTable.Symbol(param.id, "var", "array", size, elemType);
                    }
                } else {
                    paramSym = new SymbolTable.Symbol(param.id, "var", param.type);
                }

                if (paramSym != null) {
                    if (!symTable.insert(paramSym)) {
                        error(paramCtxs.get(i).start.getLine(), paramCtxs.get(i).start.getCharPositionInLine(), "Duplicate parameter name: " + param.id);
                    }
                }
            }
        }

        visit(ctx.stat_seq());
        symTable.finalizeScope();
        return null;
    }

    @Override
    public String visitVar_declaration(TigerParser.Var_declarationContext ctx) {
        TigerParser.Storage_classContext storage_classCtx = ctx.storage_class();
        int currentLevel = symTable.getCurrentScope().level;


        if (storage_classCtx.getText().equals("static") && currentLevel > 0) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), 
                "Local variable cannot be static");
        }
        if (storage_classCtx.getText().equals("var") && currentLevel == 0) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), 
                "Global variable must be static");
        }


        TigerParser.TypeContext typeCtx = ctx.type();
        String varType = null;      // "int", "float", or "array"
        int arraySize = 0;          //  if varType is "array"
        String elementType = null;  // if varType is "array"

        if (typeCtx.ARRAY() != null) {
        error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Arrays can be created only by first creating an array type");
        } 
        else if (typeCtx.ID() != null) {
     
            String typeName = typeCtx.ID().getText();
            SymbolTable.Symbol existingType = symTable.lookUp(typeName);
            
            if (existingType == null) {
                error(typeCtx.start.getLine(), typeCtx.start.getCharPositionInLine(), 
                    "Undefined type: " + typeName);
                return null; 
            }
            
            varType = existingType.basetype; // "int", "float", or "array"
            if ("array".equals(varType)) {
                arraySize = existingType.arraySize;
                elementType = existingType.elementType;
            }
        } 
        else {

            varType = typeCtx.base_type().getText();
        }

        TigerParser.Optional_initContext optInit = ctx.optional_init();
        if (optInit.ASSIGN() != null) {
            TigerParser.ConstantContext constantCtx = optInit.constant();
            String initType;
            if (constantCtx.INTLIT() != null) {
                initType = "int";
            } else {
                initType = "float";
            }

            if (!"array".equals(varType)) {
                if (varType.equals("int") && initType.equals("float")) {
                    error(optInit.start.getLine(), optInit.start.getCharPositionInLine(), 
                        "Narrowing conversion: cannot initialize int with float");
                } 
                else if (!varType.equals(initType) && !(varType.equals("float") && initType.equals("int"))) {
                    error(optInit.start.getLine(), optInit.start.getCharPositionInLine(), 
                        "Type mismatch: cannot initialize " + varType + " with " + initType);
                }
            } 
            else {
                if (elementType.equals("int") && initType.equals("float")) {
                    error(optInit.start.getLine(), optInit.start.getCharPositionInLine(), 
                        "Narrowing conversion: cannot initialize int array with float scalar");
                } 
                else if (!elementType.equals(initType) && !(elementType.equals("float") && initType.equals("int"))) {
                    error(optInit.start.getLine(), optInit.start.getCharPositionInLine(), 
                        "Type mismatch: cannot initialize " + elementType + " array with " + initType);
                }
            }
        }


        TigerParser.Id_listContext current = ctx.id_list();
        while (current != null) {
            TerminalNode idNode = current.ID();
            String name = idNode.getText();
            SymbolTable.Symbol newVarSym;
            String variableKind;
            if (currentLevel == 0) {
                variableKind = "static";
            } else {
                variableKind = "var";
            }

            if ("array".equals(varType)) {
                 newVarSym = new SymbolTable.Symbol(name, variableKind, "array", arraySize, elementType);
            } else {
                 newVarSym = new SymbolTable.Symbol(name, variableKind, varType);
            }

            if (!symTable.insert(newVarSym)) {
                error(current.start.getLine(), 
                    current.start.getCharPositionInLine(), 
                    "Variable '" + name + "' cannot be redefined");
            } 
            current = current.id_list(); 
        }

        return null;
    }

    @Override
    public String visitStat(TigerParser.StatContext ctx) {
        if (ctx.LET() != null) {
            symTable.initializeScope("Let Block"); 
            visit(ctx.declaration_segment());      
            visit(ctx.stat_seq(0));                
            symTable.finalizeScope();              
            return null;
        }
        if (ctx.FOR() != null) {

            
            String startType = visit(ctx.expr(0)); 
            String endType   = visit(ctx.expr(1));
            if (!"int".equals(startType) || !"int".equals(endType)) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "For loop bounds must be integers");
            }

            String id = ctx.ID().getText();
            SymbolTable.Symbol loopVar = symTable.lookUp(id);
            if (loopVar == null) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Undefined loop variable: " + id);
            } else if (!"int".equals(loopVar.basetype) || "array".equals(loopVar.basetype)) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Loop variable " + id + " must be a scalar integer.");
            }
            visit(ctx.stat_seq(0)); 
            return null;
        }
        if (ctx.WHILE() != null) {
            String condType = visit(ctx.expr(0));
            if (condType != null && !condType.equals("int")) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Condition must be integer");
            }
            visit(ctx.stat_seq(0));
            return null;
        }
        if (ctx.IF() != null) {
            String condType = visit(ctx.expr(0));
            if (condType != null && !condType.equals("int")) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Condition must be integer");
            }
            visit(ctx.stat_seq(0));
            if (ctx.ELSE() != null) visit(ctx.stat_seq(1));
            return null;
        }
        if (ctx.BREAK() != null) {
            return null;
        }
        if (ctx.RETURN() != null) {
            if (ctx.optreturn().expr() != null) {
                visit(ctx.optreturn().expr());
            }
            return null;
        }
        if (ctx.LPAREN() != null) {
            String funName = ctx.ID().getText();
            SymbolTable.Symbol funSymbol = symTable.lookUp(funName);
            
            if (funSymbol == null) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Undefined function: " + funName);
            } else {
                // Check if returning to a variable
                if (ctx.optprefix() != null && ctx.optprefix().value() != null) {
                    String lhsType = visit(ctx.optprefix().value());
                    if (lhsType != null) {
                        String retType = funSymbol.basetype;
                        
                        if (retType == null) {
                            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Cannot assign a void function to a variable");
                        } else if (lhsType.equals("int") && retType.equals("float")) {
                            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Narrowing conversion");
                        } else if (!lhsType.equals(retType) && !(lhsType.equals("float") && retType.equals("int"))) {
                            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Type mismatch in assignment");
                        }
                    }
                }
                if (ctx.expr_list() != null) {
                    if (funSymbol.paramTypes != null) {
                        List<String> argTypes = new ArrayList<>();
                        if (ctx.expr_list().expr() != null) {
                            argTypes.add(visit(ctx.expr_list().expr()));
                            TigerParser.Expr_list_tailContext tail = ctx.expr_list().expr_list_tail();
                            while (tail != null && tail.expr() != null) {
                                argTypes.add(visit(tail.expr()));
                                tail = tail.expr_list_tail();
                            }
                        }
                        
                        if (argTypes.size() != funSymbol.paramTypes.size()) {
                            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), 
                                "Argument count mismatch: expected " + funSymbol.paramTypes.size() + " but got " + argTypes.size());
                        } else {
                                for (int i = 0; i < argTypes.size(); i++) {
                                    String paramType = funSymbol.paramTypes.get(i).type;
                                    String argType = argTypes.get(i);
                                    if ("array".equals(argType)) {
                                        error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Semantic error: Cannot send an array as an argument");
                                    } else if (paramType != null && argType != null) {
                                        if (paramType.equals("int") && argType.equals("float")) {
                                            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), 
                                                "Narrowing conversion in argument " + (i + 1));
                                        } else if (!paramType.equals(argType) && !("float".equals(paramType) && "int".equals(argType))) {
                                            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), 
                                                "Type mismatch in argument " + (i + 1));
                                        }
                                    }
                                }
                        }
                    }
                } else if (funSymbol.paramTypes != null && !funSymbol.paramTypes.isEmpty()) {
                    error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), 
                        "Argument count mismatch: expected " + funSymbol.paramTypes.size() + " but got 0");
                }
            }
            return null;
        }
        if (ctx.value() != null && ctx.ASSIGN() != null) {
             String lhsType = visit(ctx.value()); 
             String rhsType = visit(ctx.expr(0));
             
             if (lhsType != null && rhsType != null) {
                 if (lhsType.equals("int") && rhsType.equals("float")) {
                     error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Narrowing conversion");
                 } else if (!lhsType.equals(rhsType) && !(lhsType.equals("float") && rhsType.equals("int"))) {
                     error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Type mismatch in assignment");
                 }
             }
             return null;
        }
        return null;
    }
    @Override 
    public String visitConstant(TigerParser.ConstantContext ctx){
        if(ctx.INTLIT()!=null){
            return "int";
        }else{
            return "float";
        }
    }
    @Override
    public String visitValue(TigerParser.ValueContext ctx){
        String name = ctx.ID().getText();
        SymbolTable.Symbol sym = symTable.lookUp(name);

        if (sym == null) {
            error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Undefined variable: " + name);
            return null;
        }
        if (!"var".equals(sym.kind) && !"static".equals(sym.kind)) {
        error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), name + " is not a variable");
        return null;
        }
        if (ctx.value_tail() != null && ctx.value_tail().LBRACK() != null) {
            if (!"array".equals(sym.basetype)) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Cannot index non-array variable: " + name);
                return null;
            }
            String indexType = visit(ctx.value_tail().expr());
            if (indexType != null && !indexType.equals("int")) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Array index must evaluate to an integer");
                return null;
            }
            return sym.elementType; 
        } 
        return sym.basetype;
    }

    private boolean isRelational(TigerParser.ExprContext exprCtx) {
        if (exprCtx == null) return false;
        return exprCtx.EQ() != null || exprCtx.NE() != null || exprCtx.LT() != null || 
               exprCtx.GT() != null || exprCtx.LE() != null || exprCtx.GE() != null;
    }
        @Override
        public String visitExpr(TigerParser.ExprContext ctx) {
        if (ctx.LPAREN() != null) {
            return visit(ctx.expr(0));
        }
        if (ctx.constant() != null) {
            return visit(ctx.constant());
        }
        if (ctx.value() != null) {
            return visit(ctx.value());
        }

        if (ctx.expr().size() == 2) {
            String leftType = visit(ctx.expr(0));
            String rightType = visit(ctx.expr(1));

            if (leftType == null || rightType == null) {
                return null; 
            }

            if (leftType.equals("array") || rightType.equals("array")) {
                error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Operators must operate on scalar values, not arrays");
                return null;
            }
            if (ctx.AND() != null || ctx.OR() != null) {
                if (!leftType.equals("int") || !rightType.equals("int")) {
                    error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Logical operators require integer operands");
                }
                return "int";
            }
            if (ctx.EQ() != null || ctx.NE() != null || ctx.LT() != null || ctx.GT() != null || ctx.LE() != null || ctx.GE() != null) {
                if (isRelational(ctx.expr(0)) || isRelational(ctx.expr(1))) {
                    error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Relational operators cannot be chained");
                }
                
                if (!leftType.equals(rightType)) {
                    error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Relational operators require both operands to be of the same type");
                }
                return "int"; 
            }
            if (ctx.POW() != null) {
                if (!rightType.equals("int")) {
                    error(ctx.start.getLine(), ctx.start.getCharPositionInLine(), "Exponentiation right operand must be an integer");
                }
                return leftType; 
            }
            if (ctx.ADD() != null || ctx.SUB() != null || ctx.MUL() != null || ctx.DIV() != null) {
                if (leftType.equals("float") || rightType.equals("float")) {
                    return "float";
                }
                return "int";
            }
        }
        
        return "not checked";
    }

}