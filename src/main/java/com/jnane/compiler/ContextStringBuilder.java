package com.jnane.compiler;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Classe utilitaire pour générer une représentation textuelle des contextes du parser.
 * Cette classe permet de convertir l'arbre syntaxique en une chaîne de caractères formatée.
 */
public class ContextStringBuilder {
    private static final String INDENT = "  ";
    
    /**
     * Convertit un contexte en chaîne de caractères formatée.
     * 
     * @param context Contexte à convertir
     * @return Représentation textuelle du contexte
     */
    public static String toString(ParserRuleContext context) {
        StringBuilder sb = new StringBuilder();
        buildString(context, sb, 0);
        return sb.toString();
    }
    
    /**
     * Méthode récursive pour construire la représentation textuelle d'un contexte.
     * Version améliorée qui affiche les valeurs des éléments terminaux et
     * conserve sur la même ligne les assignations et appels de fonction.
     * 
     * @param context Contexte à convertir
     * @param sb StringBuilder pour construire la chaîne
     * @param level Niveau d'indentation
     */
    private static void buildString(ParseTree context, StringBuilder sb, int level) {
        // Si c'est un nœud terminal, afficher simplement sa valeur
        if (context instanceof TerminalNode) {
            sb.append(context.getText()).append(" ");
            return;
        }
        
        // Récupérer le nom du contexte
        String contextName = "";
        if (context instanceof ParserRuleContext) {
            contextName = context.getClass().getSimpleName();
            // Enlever le suffixe "Context" s'il existe
            if (contextName.endsWith("Context")) {
                contextName = contextName.substring(0, contextName.length() - 7);
            }
        }
        
        // Vérifier si c'est une assignation ou un appel de fonction
        boolean isAssignment = contextName.contains("Assignment") || contextName.equals("AssignmentExpr");
        boolean isFunctionCall = contextName.contains("FunctionCall") || contextName.equals("FunctionCallExpr");
        
        // Indentation selon le niveau (sauf pour les éléments qui seront sur la même ligne)
        if (!isAssignment && !isFunctionCall) {
            for (int i = 0; i < level; i++) {
                sb.append(INDENT);
            }
            sb.append(contextName).append("\n");
        }
        
        // Traitement spécial pour les assignations et appels de fonction
        if (isAssignment || isFunctionCall) {
            for (int i = 0; i < level; i++) {
                sb.append(INDENT);
            }
            sb.append(contextName).append(": ");
            
            // Traiter les enfants sur la même ligne
            for (int i = 0; i < context.getChildCount(); i++) {
                ParseTree child = context.getChild(i);
                buildString(child, sb, 0); // Pas d'indentation supplémentaire
            }
            sb.append("\n");
        } else {
            // Traitement normal pour les autres contextes
            for (int i = 0; i < context.getChildCount(); i++) {
                ParseTree child = context.getChild(i);
                buildString(child, sb, level + 1);
            }
        }
    }
    
    /**
     * Convertit spécifiquement un contexte de programme en chaîne de caractères formatée.
     * 
     * @param programContext Contexte de programme à convertir
     * @return Représentation textuelle du programme
     */
    public static String programToString(JnaneLangParser.ProgramContext programContext) {
        return toString(programContext);
    }
    
    /**
     * Convertit spécifiquement un contexte d'expression en chaîne de caractères formatée.
     * 
     * @param expressionContext Contexte d'expression à convertir
     * @return Représentation textuelle de l'expression
     */
    public static String expressionToString(JnaneLangParser.ExpressionContext expressionContext) {
        return toString(expressionContext);
    }
    
    /**
     * Convertit spécifiquement un contexte de déclaration en chaîne de caractères formatée.
     * 
     * @param statementContext Contexte de déclaration à convertir
     * @return Représentation textuelle de la déclaration
     */
    public static String statementToString(JnaneLangParser.StatementContext statementContext) {
        return toString(statementContext);
    }
}
