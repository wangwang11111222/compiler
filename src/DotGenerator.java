import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class DotGenerator {

    public static String generate(ParseTree tree, TigerParser parser) {
        StringBuilder ss = new StringBuilder();
        ss.append("digraph G {\n");
        

        ss.append("  node [shape=ellipse, fontname=\"Arial\"];\n");
        
        int[] nodeCounter = {0}; 
        printNode(tree, parser, ss, nodeCounter, -1);
        
        ss.append("}\n");
        return ss.toString();
    }

    private static void printNode(ParseTree node, TigerParser parser, StringBuilder ss, int[] counter, int parentID) {
        int currentID = counter[0]++;
        String label;

        if (node instanceof TerminalNode) {
            TerminalNode terminalNode = (TerminalNode) node;
            Token token = terminalNode.getSymbol();
            int type = token.getType();

            if (type == Token.EOF) {
                label = "EOF";
            } else {

                label = parser.getVocabulary().getSymbolicName(type);
                

                if (label == null) {
                    label = token.getText().toUpperCase(); 
                }
            }
        } else if (node instanceof ParserRuleContext) {

            ParserRuleContext ctx = (ParserRuleContext) node;
            int ruleIndex = ctx.getRuleIndex();
            label = parser.getRuleNames()[ruleIndex];
        } else {
            label = "Unknown";
        }


        if (label != null) {
            label = label.replace("\"", "").replace("'", "");
        }

        ss.append("  node").append(currentID).append(" [label=\"").append(label).append("\"];\n");

        if (parentID != -1) {
            ss.append("  node").append(parentID).append(" -> node").append(currentID).append(";\n");
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            printNode(node.getChild(i), parser, ss, counter, currentID);
        }
    }
}