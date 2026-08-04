import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SymbolTable {
    private Stack<Scope> scopeStack = new Stack<>();
    private List<Scope> Scopes = new ArrayList<>();
    private int scopeCounter = 0;
    private int scopePointer = 0;

    public static class Param {
            String id;
            String type;
            Param(String id, String type) {
                this.id = id;
                this.type = type;
            }
    }
    

    public static class Symbol {
        String name;
        String kind;      
        String basetype;      
        List<Param> paramTypes; // for functions
        int arraySize;        // for array
        String elementType;   // for array 



        @Override
        public String toString() {
            if ("function".equals(kind)) {
                String paramsStr = (paramTypes == null) ? "" : 
                    paramTypes.stream().map(p -> p.type).collect(Collectors.joining(", "));
                
                String displayReturnType = (basetype == null) ? "void" : basetype;
                
                return name + ", " + kind + ", [" + paramsStr + "], " + displayReturnType;
            }
            else if ("array".equals(basetype)) {
                return name + ", " + kind + ", " + basetype + ", " + arraySize + ", " + elementType;
            } 
            else {
                return name + ", " + kind + ", " + basetype;
            }
        }

        public Symbol(String n, String k, String b) {
            this.name = n;
            this.kind = k;
            this.basetype = b;
        }

        public Symbol(String n, String k, List<Param> p, String retType) {
            this.name = n;
            this.kind = k;
            this.paramTypes = p;
            this.basetype = retType; 
        }

        public Symbol(String n, String k, String structure, int size, String element) {
            this.name = n;
            this.kind = k;
            this.basetype = structure; 
            this.arraySize = size;
            this.elementType = element;
        }
    }

    public static class Scope {
        int id;
        int level; 
        String scopeInfo;
        Map<String, Symbol> symbols = new LinkedHashMap<>(); 

        public Scope(int id, String info, int level) { 
            this.id = id; 
            this.scopeInfo = info; 
            this.level = level;
        }
        
        public void add(Symbol s) { 
            symbols.put(s.name, s); 
        }
    }

    public Symbol lookUp(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Scope currentScope = scopeStack.get(i);
            if (currentScope.symbols.containsKey(name)) {
                return currentScope.symbols.get(name);
            }
        }
        return null;
    }

    public boolean insert(Symbol s) {
        if (scopeStack.peek().symbols.containsKey(s.name)) {
            return false;
        }
        scopeStack.peek().symbols.put(s.name, s);
        return true;
    }
 
    public void initializeScope(String scopeInfo) {
        scopeCounter++; 
        int currentLevel = scopeStack.size(); 
        Scope newScope = new Scope(scopeCounter, scopeInfo, currentLevel);
        scopeStack.push(newScope); 
        Scopes.add(newScope);    
    }

    public void finalizeScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.pop();
        }
    }

    public Scope getCurrentScope() {
    if (scopeStack.isEmpty()) return null;
    return scopeStack.peek();
    }

    public void resetPointer() {
        scopePointer = 0;
        scopeStack.clear(); 
    }

    public void reEnterNextScope() {
        if (scopePointer < Scopes.size()) {
            scopeStack.push(Scopes.get(scopePointer));
            scopePointer++;
        }
    }
    public String getMangledName(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Scope currentScope = scopeStack.get(i);
            if (currentScope.symbols.containsKey(name)) {
                return "_" + currentScope.id + "_" + name;
            }
        }
        return name; 
    }

    public void dump() {
        for (Scope scope : Scopes) {
            String indent = "";
            for (int i = 0; i < scope.level; i++) {
                indent += "    ";
            }
            System.out.println(indent + "scope " + scope.id + ": // " + scope.scopeInfo);
            for (Symbol s : scope.symbols.values()) {
                System.out.println(indent + "    " + s.toString());
            }

        }
    }
}