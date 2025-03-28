package com.jnane.compiler;

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
public class JnaneLangVisitorImpl extends AbstractParseTreeVisitor<Object> implements JnaneLangVisitor<Object> {

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

    // Visite des cas de match
    @Override
    public Object visitMatchCase(JnaneLangParser.MatchCaseContext ctx) {
        System.out.println(getIndent() + "Cas de match: " + ctx.getText());
        return visitChildren(ctx);
    }

    // Visite des instructions
    @Override
    public Object visitStatement(JnaneLangParser.StatementContext ctx) {
        System.out.println(getIndent() + "Instruction: " + ctx.getText());
        return visitChildren(ctx);
    }

    // Méthode par défaut pour les nœuds terminaux
    @Override
    public Object visitTerminal(TerminalNode node) {
        return node.getText();
    }

    // Implémentation des méthodes requises par l'interface générée

    @Override
    public Object visitAnnotationName(JnaneLangParser.AnnotationNameContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitAnnotationParams(JnaneLangParser.AnnotationParamsContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitAnnotationParam(JnaneLangParser.AnnotationParamContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitAnnotationParamValue(JnaneLangParser.AnnotationParamValueContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitAnnotationValue(JnaneLangParser.AnnotationValueContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitWhereClause(JnaneLangParser.WhereClauseContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitMultilineString(JnaneLangParser.MultilineStringContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitFunctionReference(JnaneLangParser.FunctionReferenceContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitFunctionHeader(JnaneLangParser.FunctionHeaderContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitFunctionBody(JnaneLangParser.FunctionBodyContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeInheritance(JnaneLangParser.TypeInheritanceContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeBody(JnaneLangParser.TypeBodyContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeElement(JnaneLangParser.TypeElementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeField(JnaneLangParser.TypeFieldContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitFieldAnnotations(JnaneLangParser.FieldAnnotationsContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeView(JnaneLangParser.TypeViewContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeViewBody(JnaneLangParser.TypeViewBodyContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeViewElement(JnaneLangParser.TypeViewElementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeConstraint(JnaneLangParser.TypeConstraintContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeMethod(JnaneLangParser.TypeMethodContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitMethodSignature(JnaneLangParser.MethodSignatureContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitMethodParam(JnaneLangParser.MethodParamContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitMethodBody(JnaneLangParser.MethodBodyContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitDefaultValue(JnaneLangParser.DefaultValueContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeExpr(JnaneLangParser.TypeExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeExprSuffix(JnaneLangParser.TypeExprSuffixContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitSimpleTypeExpr(JnaneLangParser.SimpleTypeExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitGenericTypeExpr(JnaneLangParser.GenericTypeExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitFunctionTypeExpr(JnaneLangParser.FunctionTypeExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTupleTypeExpr(JnaneLangParser.TupleTypeExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitRecordTypeExpr(JnaneLangParser.RecordTypeExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitRecordTypeField(JnaneLangParser.RecordTypeFieldContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeList(JnaneLangParser.TypeListContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitTypeAlias(JnaneLangParser.TypeAliasContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitEnumType(JnaneLangParser.EnumTypeContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitEnumValue(JnaneLangParser.EnumValueContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitNamespaceDecl(JnaneLangParser.NamespaceDeclContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitNamespaceId(JnaneLangParser.NamespaceIdContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitNamespaceBody(JnaneLangParser.NamespaceBodyContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitNamespaceElement(JnaneLangParser.NamespaceElementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitImportDecl(JnaneLangParser.ImportDeclContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitImportPath(JnaneLangParser.ImportPathContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitExportDecl(JnaneLangParser.ExportDeclContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitExportElement(JnaneLangParser.ExportElementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitNamespaceAlias(JnaneLangParser.NamespaceAliasContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitViewDecl(JnaneLangParser.ViewDeclContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitViewBody(JnaneLangParser.ViewBodyContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitViewElement(JnaneLangParser.ViewElementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitViewOperation(JnaneLangParser.ViewOperationContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitLensDefinition(JnaneLangParser.LensDefinitionContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitLensOperation(JnaneLangParser.LensOperationContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitLensExpr(JnaneLangParser.LensExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitAssignmentExpr(JnaneLangParser.AssignmentExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitConditionalExpr(JnaneLangParser.ConditionalExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitLogicalOrExpr(JnaneLangParser.LogicalOrExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitLogicalAndExpr(JnaneLangParser.LogicalAndExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitEqualityExpr(JnaneLangParser.EqualityExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitRelationalExpr(JnaneLangParser.RelationalExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitAdditiveExpr(JnaneLangParser.AdditiveExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitMultiplicativeExpr(JnaneLangParser.MultiplicativeExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitUnaryExpr(JnaneLangParser.UnaryExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitPostfixExpr(JnaneLangParser.PostfixExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitPostfixOp(JnaneLangParser.PostfixOpContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitPrimaryExpr(JnaneLangParser.PrimaryExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitFunctionCallExpr(JnaneLangParser.FunctionCallExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitArgumentList(JnaneLangParser.ArgumentListContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitObjectLiteral(JnaneLangParser.ObjectLiteralContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitObjectField(JnaneLangParser.ObjectFieldContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitArrayLiteral(JnaneLangParser.ArrayLiteralContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitLambdaExpr(JnaneLangParser.LambdaExprContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitParamList(JnaneLangParser.ParamListContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitExpressionStmt(JnaneLangParser.ExpressionStmtContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitBlockStmt(JnaneLangParser.BlockStmtContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Object visitPattern(JnaneLangParser.PatternContext ctx) {
        return visitChildren(ctx);
    }

    // Implémentation de la méthode manquante pour la règle docAnnotation
    @Override
    public Object visitDocAnnotation(JnaneLangParser.DocAnnotationContext ctx) {
        System.out.println(getIndent() + "Documentation: " + ctx.getText());
        return visitChildren(ctx);
    }

    // Implémentation de la méthode pour la règle annotationSequence
    @Override
    public Object visitAnnotationSequence(JnaneLangParser.AnnotationSequenceContext ctx) {
        System.out.println(getIndent() + "Séquence d'annotations: " + ctx.getText());
        return visitChildren(ctx);
    }
}
