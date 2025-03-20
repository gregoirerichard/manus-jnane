package com.causallang.compiler;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation du visiteur pour l'AST du langage Jnane.
 * Cette classe fournit une implémentation basique du visiteur qui affiche
 * les éléments de l'AST lors de la visite.
 */
public class JnaneLangVisitorImpl extends AbstractParseTreeVisitor<Object> implements JnaneLangVisitor {

    private int indentLevel = 0;
    private final Map<String, Object> symbolTable = new HashMap<>();

    // Méthode utilitaire pour l'indentation
    private String getIndent() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    // Méthode par défaut pour visiter les nœuds
    @Override
    public Object visitChildren(RuleNode node) {
        String ruleName = JnaneLangParser.ruleNames[node.getRuleContext().getRuleIndex()];
        System.out.println(getIndent() + "Visite de " + ruleName);
        
        indentLevel++;
        Object result = super.visitChildren(node);
        indentLevel--;
        
        return result;
    }

    // Visite du programme
    @Override
    public Object visitProgram(JnaneLangParser.ProgramContext ctx) {
        System.out.println("Début de l'analyse du programme Jnane");
        return visitChildren(ctx);
    }

    // Visite des déclarations
    @Override
    public Object visitDeclaration(JnaneLangParser.DeclarationContext ctx) {
        return visitChildren(ctx);
    }

    // Visite des annotations
    @Override
    public Object visitAnnotationDecl(JnaneLangParser.AnnotationDeclContext ctx) {
        String annotationName = ctx.annotationName().getText();
        System.out.println(getIndent() + "Annotation: @" + annotationName);
        
        // Traitement des paramètres d'annotation s'ils existent
        if (ctx.annotationParams() != null) {
            System.out.println(getIndent() + "  Paramètres: " + ctx.annotationParams().getText());
        }
        
        // Traitement de la valeur d'annotation si elle existe
        if (ctx.annotationValue() != null) {
            System.out.println(getIndent() + "  Valeur: " + ctx.annotationValue().getText());
        }
        
        return visitChildren(ctx);
    }

    // Visite des déclarations de fonction
    @Override
    public Object visitFunctionDecl(JnaneLangParser.FunctionDeclContext ctx) {
        System.out.println(getIndent() + "Déclaration de fonction");
        return visitChildren(ctx);
    }

    // Visite des déclarations de type
    @Override
    public Object visitTypeDecl(JnaneLangParser.TypeDeclContext ctx) {
        String typeName = ctx.ID().getText();
        System.out.println(getIndent() + "Déclaration de type: " + typeName);
        return visitChildren(ctx);
    }

    // Visite des déclarations de namespace
    @Override
    public Object visitNamespaceDecl(JnaneLangParser.NamespaceDeclContext ctx) {
        String namespaceName = ctx.namespaceId().getText();
        System.out.println(getIndent() + "Déclaration de namespace: " + namespaceName);
        return visitChildren(ctx);
    }

    // Visite des importations
    @Override
    public Object visitImportDecl(JnaneLangParser.ImportDeclContext ctx) {
        String importPath = ctx.importPath().getText();
        System.out.println(getIndent() + "Importation: " + importPath);
        return visitChildren(ctx);
    }

    // Visite des expressions
    @Override
    public Object visitExpression(JnaneLangParser.ExpressionContext ctx) {
        return visitChildren(ctx);
    }

    // Visite des littéraux
    @Override
    public Object visitLiteral(JnaneLangParser.LiteralContext ctx) {
        String literalValue = ctx.getText();
        String literalType = "inconnu";
        
        if (ctx.INTEGER() != null) {
            literalType = "entier";
        } else if (ctx.DECIMAL() != null) {
            literalType = "décimal";
        } else if (ctx.STRING() != null) {
            literalType = "chaîne";
        } else if (ctx.BOOLEAN() != null) {
            literalType = "booléen";
        } else if (ctx.NULL() != null) {
            literalType = "null";
        }
        
        System.out.println(getIndent() + "Littéral " + literalType + ": " + literalValue);
        return literalValue;
    }

    // Visite des instructions return
    @Override
    public Object visitReturnStmt(JnaneLangParser.ReturnStmtContext ctx) {
        System.out.println(getIndent() + "Instruction return");
        return visitChildren(ctx);
    }

    // Visite des instructions if
    @Override
    public Object visitIfStmt(JnaneLangParser.IfStmtContext ctx) {
        System.out.println(getIndent() + "Instruction if");
        return visitChildren(ctx);
    }

    // Visite des instructions match
    @Override
    public Object visitMatchStmt(JnaneLangParser.MatchStmtContext ctx) {
        System.out.println(getIndent() + "Instruction match");
        return visitChildren(ctx);
    }

    // Méthode par défaut pour les nœuds terminaux
    @Override
    public Object visitTerminal(TerminalNode node) {
        return node.getText();
    }
}
