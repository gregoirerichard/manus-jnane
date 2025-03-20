package com.causallang.compiler;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation améliorée du visiteur pour l'AST du langage Causal.
 * Cette classe construit un AST structuré en utilisant la classe ASTNode.
 */
public class ASTBuilderVisitor extends AbstractParseTreeVisitor<ASTNode> implements CausalLangVisitor {

    // Méthode par défaut pour visiter les nœuds
    @Override
    public ASTNode visitChildren(RuleNode node) {
        String ruleName = CausalLangParser.ruleNames[node.getRuleContext().getRuleIndex()];
        ASTNode result = new ASTNode(ruleName);
        
        int n = node.getChildCount();
        for (int i = 0; i < n; i++) {
            ASTNode childResult = node.getChild(i).accept(this);
            if (childResult != null) {
                result.addChild(childResult);
            }
        }
        
        return result;
    }

    // Visite du programme
    @Override
    public ASTNode visitProgram(CausalLangParser.ProgramContext ctx) {
        ASTNode programNode = new ASTNode("Program");
        
        for (CausalLangParser.DeclarationContext declCtx : ctx.declaration()) {
            ASTNode declNode = visit(declCtx);
            if (declNode != null) {
                programNode.addChild(declNode);
            }
        }
        
        return programNode;
    }

    // Visite des déclarations
    @Override
    public ASTNode visitDeclaration(CausalLangParser.DeclarationContext ctx) {
        return visitChildren(ctx);
    }

    // Visite des annotations
    @Override
    public ASTNode visitAnnotationDecl(CausalLangParser.AnnotationDeclContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode annotationNode = new ASTNode("Annotation", startToken.getLine(), startToken.getCharPositionInLine());
        
        String annotationName = ctx.annotationName().getText();
        annotationNode.setAttribute("name", annotationName);
        
        // Traitement des paramètres d'annotation s'ils existent
        if (ctx.annotationParams() != null) {
            ASTNode paramsNode = visit(ctx.annotationParams());
            if (paramsNode != null) {
                annotationNode.addChild(paramsNode);
            }
        }
        
        // Traitement de la valeur d'annotation si elle existe
        if (ctx.annotationValue() != null) {
            ASTNode valueNode = visit(ctx.annotationValue());
            if (valueNode != null) {
                annotationNode.addChild(valueNode);
            }
        }
        
        return annotationNode;
    }

    // Visite des paramètres d'annotation
    @Override
    public ASTNode visitAnnotationParams(CausalLangParser.AnnotationParamsContext ctx) {
        ASTNode paramsNode = new ASTNode("AnnotationParams");
        
        for (CausalLangParser.AnnotationParamContext paramCtx : ctx.annotationParam()) {
            ASTNode paramNode = visit(paramCtx);
            if (paramNode != null) {
                paramsNode.addChild(paramNode);
            }
        }
        
        return paramsNode;
    }

    // Visite d'un paramètre d'annotation
    @Override
    public ASTNode visitAnnotationParam(CausalLangParser.AnnotationParamContext ctx) {
        ASTNode paramNode = new ASTNode("AnnotationParam");
        paramNode.setAttribute("name", ctx.ID().getText());
        
        ASTNode valueNode = visit(ctx.annotationParamValue());
        if (valueNode != null) {
            paramNode.addChild(valueNode);
        }
        
        return paramNode;
    }

    // Visite des déclarations de fonction
    @Override
    public ASTNode visitFunctionDecl(CausalLangParser.FunctionDeclContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode functionNode = new ASTNode("Function", startToken.getLine(), startToken.getCharPositionInLine());
        
        // Traitement de l'en-tête de fonction
        ASTNode headerNode = visit(ctx.functionHeader());
        if (headerNode != null) {
            functionNode.addChild(headerNode);
        }
        
        // Traitement du corps de fonction
        ASTNode bodyNode = visit(ctx.functionBody());
        if (bodyNode != null) {
            functionNode.addChild(bodyNode);
        }
        
        return functionNode;
    }

    // Visite des déclarations de type
    @Override
    public ASTNode visitTypeDecl(CausalLangParser.TypeDeclContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode typeNode = new ASTNode("Type", startToken.getLine(), startToken.getCharPositionInLine());
        
        String typeName = ctx.ID().getText();
        typeNode.setAttribute("name", typeName);
        
        // Traitement de l'héritage si présent
        if (ctx.typeInheritance() != null) {
            ASTNode inheritanceNode = visit(ctx.typeInheritance());
            if (inheritanceNode != null) {
                typeNode.addChild(inheritanceNode);
            }
        }
        
        // Traitement du corps du type
        ASTNode bodyNode = visit(ctx.typeBody());
        if (bodyNode != null) {
            typeNode.addChild(bodyNode);
        }
        
        return typeNode;
    }

    // Visite des déclarations de namespace
    @Override
    public ASTNode visitNamespaceDecl(CausalLangParser.NamespaceDeclContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode namespaceNode = new ASTNode("Namespace", startToken.getLine(), startToken.getCharPositionInLine());
        
        ASTNode namespaceIdNode = visit(ctx.namespaceId());
        if (namespaceIdNode != null) {
            namespaceNode.setAttribute("name", namespaceIdNode.getAttribute("name"));
        }
        
        // Traitement du corps du namespace s'il existe
        if (ctx.namespaceBody() != null) {
            ASTNode bodyNode = visit(ctx.namespaceBody());
            if (bodyNode != null) {
                namespaceNode.addChild(bodyNode);
            }
        }
        
        return namespaceNode;
    }

    // Visite des identifiants de namespace
    @Override
    public ASTNode visitNamespaceId(CausalLangParser.NamespaceIdContext ctx) {
        ASTNode namespaceIdNode = new ASTNode("NamespaceId");
        
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < ctx.ID().size(); i++) {
            if (i > 0) {
                nameBuilder.append(".");
            }
            nameBuilder.append(ctx.ID(i).getText());
        }
        
        namespaceIdNode.setAttribute("name", nameBuilder.toString());
        return namespaceIdNode;
    }

    // Visite des importations
    @Override
    public ASTNode visitImportDecl(CausalLangParser.ImportDeclContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode importNode = new ASTNode("Import", startToken.getLine(), startToken.getCharPositionInLine());
        
        ASTNode pathNode = visit(ctx.importPath());
        if (pathNode != null) {
            importNode.setAttribute("path", pathNode.getAttribute("path"));
        }
        
        // Traitement de l'alias s'il existe
        if (ctx.AS() != null) {
            importNode.setAttribute("alias", ctx.ID().getText());
        }
        
        return importNode;
    }

    // Visite des chemins d'importation
    @Override
    public ASTNode visitImportPath(CausalLangParser.ImportPathContext ctx) {
        ASTNode pathNode = new ASTNode("ImportPath");
        
        ASTNode namespaceIdNode = visit(ctx.namespaceId());
        String namespaceName = (String) namespaceIdNode.getAttribute("name");
        
        String target;
        if (ctx.STAR() != null) {
            target = "*";
        } else {
            target = ctx.ID().getText();
        }
        
        pathNode.setAttribute("path", namespaceName + ":" + target);
        return pathNode;
    }

    // Visite des expressions
    @Override
    public ASTNode visitExpression(CausalLangParser.ExpressionContext ctx) {
        return visitChildren(ctx);
    }

    // Visite des littéraux
    @Override
    public ASTNode visitLiteral(CausalLangParser.LiteralContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode literalNode = new ASTNode("Literal", startToken.getLine(), startToken.getCharPositionInLine());
        
        String literalValue = ctx.getText();
        String literalType = "unknown";
        
        if (ctx.INTEGER() != null) {
            literalType = "integer";
            literalNode.setAttribute("value", Integer.parseInt(literalValue));
        } else if (ctx.DECIMAL() != null) {
            literalType = "decimal";
            literalNode.setAttribute("value", Double.parseDouble(literalValue));
        } else if (ctx.STRING() != null) {
            literalType = "string";
            // Enlever les guillemets
            literalValue = literalValue.substring(1, literalValue.length() - 1);
            literalNode.setAttribute("value", literalValue);
        } else if (ctx.BOOLEAN() != null) {
            literalType = "boolean";
            literalNode.setAttribute("value", Boolean.parseBoolean(literalValue));
        } else if (ctx.NULL() != null) {
            literalType = "null";
            literalNode.setAttribute("value", null);
        }
        
        literalNode.setAttribute("type", literalType);
        return literalNode;
    }

    // Visite des instructions return
    @Override
    public ASTNode visitReturnStmt(CausalLangParser.ReturnStmtContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode returnNode = new ASTNode("Return", startToken.getLine(), startToken.getCharPositionInLine());
        
        ASTNode exprNode = visit(ctx.expression());
        if (exprNode != null) {
            returnNode.addChild(exprNode);
        }
        
        return returnNode;
    }

    // Visite des instructions if
    @Override
    public ASTNode visitIfStmt(CausalLangParser.IfStmtContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode ifNode = new ASTNode("If", startToken.getLine(), startToken.getCharPositionInLine());
        
        // Condition
        ASTNode conditionNode = visit(ctx.expression());
        if (conditionNode != null) {
            ifNode.addChild(conditionNode);
        }
        
        // Bloc then
        ASTNode thenNode = visit(ctx.blockStmt(0));
        if (thenNode != null) {
            thenNode.setAttribute("role", "then");
            ifNode.addChild(thenNode);
        }
        
        // Bloc else s'il existe
        if (ctx.blockStmt().size() > 1) {
            ASTNode elseNode = visit(ctx.blockStmt(1));
            if (elseNode != null) {
                elseNode.setAttribute("role", "else");
                ifNode.addChild(elseNode);
            }
        } else if (ctx.ifStmt() != null) {
            // Else if
            ASTNode elseIfNode = visit(ctx.ifStmt());
            if (elseIfNode != null) {
                elseIfNode.setAttribute("role", "else");
                ifNode.addChild(elseIfNode);
            }
        }
        
        return ifNode;
    }

    // Visite des instructions match
    @Override
    public ASTNode visitMatchStmt(CausalLangParser.MatchStmtContext ctx) {
        Token startToken = ctx.getStart();
        ASTNode matchNode = new ASTNode("Match", startToken.getLine(), startToken.getCharPositionInLine());
        
        // Expression à matcher
        ASTNode exprNode = visit(ctx.expression());
        if (exprNode != null) {
            matchNode.addChild(exprNode);
        }
        
        // Cas de match
        for (CausalLangParser.MatchCaseContext caseCtx : ctx.matchCase()) {
            ASTNode caseNode = visit(caseCtx);
            if (caseNode != null) {
                matchNode.addChild(caseNode);
            }
        }
        
        return matchNode;
    }

    // Visite des cas de match
    @Override
    public ASTNode visitMatchCase(CausalLangParser.MatchCaseContext ctx) {
        ASTNode caseNode = new ASTNode("MatchCase");
        
        // Pattern
        ASTNode patternNode = visit(ctx.pattern());
        if (patternNode != null) {
            caseNode.addChild(patternNode);
        }
        
        // Statement
        ASTNode stmtNode = visit(ctx.statement());
        if (stmtNode != null) {
            caseNode.addChild(stmtNode);
        }
        
        return caseNode;
    }

    // Méthode par défaut pour les nœuds terminaux
    @Override
    public ASTNode visitTerminal(TerminalNode node) {
        Token token = node.getSymbol();
        ASTNode terminalNode = new ASTNode("Terminal", token.getLine(), token.getCharPositionInLine());
        terminalNode.setAttribute("text", token.getText());
        return terminalNode;
    }
}
