package com.causallang.compiler;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un nœud dans l'arbre syntaxique abstrait (AST) du langage Causal.
 * Cette classe sert de base pour tous les types de nœuds spécifiques.
 */
public class ASTNode {
    private String type;
    private Map<String, Object> attributes;
    private List<ASTNode> children;
    private int line;
    private int column;

    public ASTNode(String type) {
        this.type = type;
        this.attributes = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public ASTNode(String type, int line, int column) {
        this(type);
        this.line = line;
        this.column = column;
    }

    public String getType() {
        return type;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void addChild(ASTNode child) {
        children.add(child);
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb, 0);
        return sb.toString();
    }

    private void toString(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }
        
        sb.append(type);
        
        if (!attributes.isEmpty()) {
            sb.append(" {");
            boolean first = true;
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(entry.getKey()).append(": ");
                if (entry.getValue() instanceof String) {
                    sb.append("\"").append(entry.getValue()).append("\"");
                } else {
                    sb.append(entry.getValue());
                }
            }
            sb.append("}");
        }
        
        if (line > 0) {
            sb.append(" @").append(line).append(":").append(column);
        }
        
        sb.append("\n");
        
        for (ASTNode child : children) {
            child.toString(sb, indent + 1);
        }
    }
}
