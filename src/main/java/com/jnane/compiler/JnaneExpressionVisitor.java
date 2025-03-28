package com.jnane.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.jnane.compiler.script.Script;

/**
 * Visiteur spécialisé pour interpréter les expressions Jnane
 * et collecter les résultats d'exécution
 */
public class JnaneExpressionVisitor extends JnaneLangBaseVisitor<Object> {
    private static final Logger logger = LoggerFactory.getLogger(JnaneExpressionVisitor.class);
    private final JnaneInterpreter interpreter;
    private Script currentScript;

    public JnaneExpressionVisitor(JnaneInterpreter interpreter) {
        this.interpreter = interpreter;
        logger.debug("JnaneExpressionVisitor initialisé avec l'interpréteur");
    }
    
    /**
     * Définit le script en cours d'exécution.
     * 
     * @param script Script en cours d'exécution
     */
    public void setCurrentScript(Script script) {
        this.currentScript = script;
        logger.debug("Script courant défini: {}", script.getFullFunctionName());
    }
    
    /**
     * Retourne le script en cours d'exécution.
     * 
     * @return Script en cours d'exécution ou null si aucun script n'est défini
     */
    public Script getCurrentScript() {
        return currentScript;
    }

    @Override
    public Object visitProgram(JnaneLangParser.ProgramContext ctx) {
        logger.debug("Visite du programme Jnane: {}", ctx.getText());

        // Traiter les annotations (comme @field, @arg)
        String currentFunction = null;
        Set<String> functionArgs = new HashSet<>();
        
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof JnaneLangParser.AnnotationDeclContext) {
                JnaneLangParser.AnnotationDeclContext annotCtx = (JnaneLangParser.AnnotationDeclContext) ctx.getChild(i);
                String annotationName = annotCtx.annotationName().getText();
                
                if (annotationName.equals("field")) {
                    String fieldName = annotCtx.annotationParams().getText();
                    logger.debug("Déclaration de champ trouvée: {}", fieldName);
                } else if (annotationName.equals("name")) {
                    // Récupérer le nom de la fonction avec son namespace
                    if (annotCtx.annotationValue() != null) {
                        currentFunction = annotCtx.annotationValue().getText();
                        logger.debug("Déclaration de fonction trouvée: {}", currentFunction);
                    }
                } else if (annotationName.equals("arg")) {
                    // Récupérer le nom de l'argument
                    if (annotCtx.annotationValue() != null) {
                        String argName = annotCtx.annotationValue().getText().split(" ")[0];
                        functionArgs.add(argName);
                        logger.debug("Déclaration d'argument trouvée: {}", argName);
                    }
                }
            }
        }
        
        // Si on a trouvé une fonction et ses arguments, les enregistrer dans l'interpréteur
        if (currentFunction != null && !functionArgs.isEmpty()) {
            interpreter.registerFunctionParameters(currentFunction, functionArgs);
            logger.info("Enregistrement des paramètres pour la fonction {}: {}", currentFunction, functionArgs);
        }

        // Visiter tous les enfants (y compris le bloc principal)
        return visitChildren(ctx);
    }

    @Override
    public Object visitStatement(JnaneLangParser.StatementContext ctx) {
        logger.debug("Visite d'une instruction: {}", ctx.getText());
        return visitChildren(ctx);
    }

    @Override
    public Object visitAssignmentExpr(JnaneLangParser.AssignmentExprContext ctx) {
        logger.debug("Visite d'une expression d'assignation: {}", ctx.getText());

        // Récupérer le nom de la variable (côté gauche de l'assignation)
        String variableName = ctx.getChild(0).getText();
        logger.debug("Nom de la variable: {}", variableName);

        // Si c'est une assignation (avec le signe =)
        if (ctx.getChildCount() > 1 && ctx.getChild(1).getText().equals("=")) {
            // Évaluer l'expression à droite de l'assignation
            Object value = visit(ctx.getChild(2));
            logger.debug("Valeur calculée pour l'assignation: {}", value);

            // Stocker la valeur dans l'interpréteur
            interpreter.setVariableValue(variableName, value);
            
            // Vérification supplémentaire pour s'assurer que la valeur est bien stockée
            Object storedValue = interpreter.getVariableValue(variableName);
            logger.debug("Valeur stockée vérifiée pour {}: {}", variableName, storedValue);

            return value;
        }

        // Si ce n'est pas une assignation, simplement visiter l'enfant
        return visit(ctx.getChild(0));
    }

    @Override
    public Object visitFunctionCallExpr(JnaneLangParser.FunctionCallExprContext ctx) {
        logger.debug("Visite d'un appel de fonction: {}", ctx.getText());

        String functionName = "";
        Map<String, Object> namedArgs = new HashMap<>();

        // Déterminer le nom de la fonction
        if (ctx.namespaceId() != null && ctx.ID() != null) {
            // Format namespace:fonction(args)
            String namespace = ctx.namespaceId().getText();
            String function = ctx.ID().getText();
            functionName = namespace + ":" + function;
            logger.debug("Appel de fonction avec namespace: {}", functionName);
        } else if (ctx.ID() != null) {
            // Format fonction(args) - cas non traité dans la grammaire actuelle
            functionName = ctx.ID().getText();
            logger.debug("Appel de fonction simple: {}", functionName);
        }

        // Collecter les arguments
        if (ctx.argumentList() != null) {
            // Analyser le texte des arguments pour extraire les arguments nommés
            // Format attendu: "argName: argValue"
            for (int i = 0; i < ctx.argumentList().expression().size(); i++) {
                JnaneLangParser.ExpressionContext exprCtx = ctx.argumentList().expression(i);
                String argText = exprCtx.getText();

                // Vérifier si c'est un argument nommé (contient ":")
                int colonPos = argText.indexOf(':');
                if (colonPos > 0) {
                    // Extraire le nom de l'argument (avant le ":")
                    String argName = argText.substring(0, colonPos).trim();

                    // Extraire et évaluer la valeur de l'argument (après le ":")
                    // Correction: utiliser directement l'expression littérale
                    String valueText = argText.substring(colonPos + 1).trim();
                    
                    // Pour les arguments numériques, les traiter directement
                    Object argValue = null;
                    try {
                        int numericValue = Integer.parseInt(valueText);
                        argValue = numericValue;
                        logger.debug("Valeur numérique extraite pour l'argument {}: {}", argName, argValue);
                    } catch (NumberFormatException e) {
                        // Si ce n'est pas un nombre, essayer de visiter l'expression
                        for (int j = 0; j < exprCtx.getChildCount(); j++) {
                            if (exprCtx.getChild(j) instanceof JnaneLangParser.ExpressionContext) {
                                argValue = visit(exprCtx.getChild(j));
                                break;
                            }
                        }
                        logger.debug("Valeur non-numérique extraite pour l'argument {}: {}", argName, argValue);
                    }

                    logger.debug("Argument nommé extrait: {} = {}", argName, argValue);
                    namedArgs.put(argName, argValue);
                } else {
                    // Argument positionnel (ancien format)
                    Object argValue = visit(exprCtx);
                    logger.debug("Argument positionnel évalué à: {}", argValue);
                    // Nous ne traitons plus les arguments positionnels
                    logger.warn("Argument positionnel détecté, mais non supporté dans le nouveau format");
                }
            }
        }

        // Appeler la fonction via l'interpréteur avec les arguments nommés
        logger.debug("Appel de la fonction {} avec {} arguments nommés", functionName, namedArgs.size());
        try {
            Object result = interpreter.interpretFunctionCallWithNamedArgs(functionName, namedArgs);
            logger.debug("Résultat de l'appel de fonction {}: {}", functionName, result);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Erreur lors de l'appel de fonction {}: {}", functionName, e.getMessage());
            throw e; // Propager l'erreur pour qu'elle soit gérée par le test
        }
    }

    @Override
    public Object visitLiteral(JnaneLangParser.LiteralContext ctx) {
        String text = ctx.getText();
        logger.debug("Traitement du littéral: {}", text);

        // Interpréter les littéraux selon leur type
        if (ctx.INTEGER() != null) {
            try {
                int value = Integer.parseInt(text);
                logger.debug("Littéral entier interprété: {}", value);
                return value;
            } catch (NumberFormatException e) {
                logger.error("Erreur lors de l'interprétation du littéral entier: {}", text, e);
            }
        } else if (ctx.DECIMAL() != null) {
            try {
                double value = Double.parseDouble(text);
                logger.debug("Littéral décimal interprété: {}", value);
                return value;
            } catch (NumberFormatException e) {
                logger.error("Erreur lors de l'interprétation du littéral décimal: {}", text, e);
            }
        } else if (ctx.STRING() != null) {
            // Enlever les guillemets
            String value = text.substring(1, text.length() - 1);
            logger.debug("Littéral chaîne interprété: {}", value);
            return value;
        } else if (ctx.BOOLEAN() != null) {
            boolean value = text.equals("Vrai");
            logger.debug("Littéral booléen interprété: {}", value);
            return value;
        } else if (ctx.NULL() != null) {
            logger.debug("Littéral null interprété");
            return null;
        }

        // Par défaut, retourner le texte brut
        return text;
    }

    @Override
    public Object visitExpressionStmt(JnaneLangParser.ExpressionStmtContext ctx) {
        logger.debug("Traitement d'une instruction d'expression: {}", ctx.getText());

        // Vérifier si c'est un appel à print
        String expr = ctx.getText();
        if (expr.startsWith("print(")) {
            Object result = visit(ctx.expression());
            logger.info("Print: {}", result);
            return result;
        }

        return visit(ctx.expression());
    }

    @Override
    public Object visitIfStmt(JnaneLangParser.IfStmtContext ctx) {
        logger.debug("Traitement d'une instruction if");

        // Évaluer la condition
        Object condition = visit(ctx.expression());
        logger.debug("Condition évaluée à: {}", condition);

        // Si la condition est vraie (non-null et non-false)
        if (condition != null && (!(condition instanceof Boolean) || (Boolean) condition)) {
            // Exécuter le bloc then
            logger.debug("Exécution du bloc then");
            return visit(ctx.blockStmt(0));
        } else if (ctx.blockStmt().size() > 1) {
            // Exécuter le bloc else s'il existe
            logger.debug("Exécution du bloc else");
            return visit(ctx.blockStmt(1));
        }

        return null;
    }

    @Override
    public Object visitBlockStmt(JnaneLangParser.BlockStmtContext ctx) {
        logger.debug("Traitement d'un bloc d'instructions");

        Object lastResult = null;

        // Exécuter chaque instruction du bloc
        for (int i = 1; i < ctx.getChildCount() - 1; i++) { // Ignorer les accolades
            lastResult = visit(ctx.getChild(i));
        }

        return lastResult;
    }

    @Override
    public Object visitPrimaryExpr(JnaneLangParser.PrimaryExprContext ctx) {
        logger.debug("Traitement d'une expression primaire: {}", ctx.getText());

        // Si c'est un identifiant, récupérer sa valeur
        if (ctx.ID() != null) {
            String variableName = ctx.ID().getText();
            Object value = interpreter.getVariableValue(variableName);
            logger.debug("Valeur de la variable {}: {}", variableName, value);
            return value;
        }

        return visitChildren(ctx);
    }

    @Override
    public Object visitConditionalExpr(JnaneLangParser.ConditionalExprContext ctx) {
        logger.debug("Traitement d'une expression conditionnelle");

        // Si c'est une expression ternaire
        if (ctx.getChildCount() > 1 && ctx.getChild(1).getText().equals("?")) {
            // Évaluer la condition
            Object condition = visit(ctx.getChild(0));
            logger.debug("Condition évaluée à: {}", condition);

            // Si la condition est vraie (non-null et non-false)
            if (condition != null && (!(condition instanceof Boolean) || (Boolean) condition)) {
                // Évaluer l'expression then
                logger.debug("Évaluation de l'expression then");
                return visit(ctx.getChild(2));
            } else {
                // Évaluer l'expression else
                logger.debug("Évaluation de l'expression else");
                return visit(ctx.getChild(4));
            }
        }

        return visitChildren(ctx);
    }

    @Override
    public Object visitAdditiveExpr(JnaneLangParser.AdditiveExprContext ctx) {
        logger.debug("Traitement d'une expression additive");

        // Si c'est une addition ou soustraction
        if (ctx.getChildCount() > 1) {
            Object left = visit(ctx.getChild(0));
            String operator = ctx.getChild(1).getText();
            Object right = visit(ctx.getChild(2));

            logger.debug("Expression additive: {} {} {}", left, operator, right);

            // Addition
            if (operator.equals("+")) {
                if (left instanceof Integer && right instanceof Integer) {
                    return interpreter.interpretAddition((Integer) left, (Integer) right);
                } else if (left instanceof String || right instanceof String) {
                    // Concaténation de chaînes
                    return String.valueOf(left) + String.valueOf(right);
                }
            }
            // Soustraction
            else if (operator.equals("-")) {
                if (left instanceof Integer && right instanceof Integer) {
                    return (Integer) left - (Integer) right;
                }
            }
        }

        return visitChildren(ctx);
    }

    @Override
    public Object visitEqualityExpr(JnaneLangParser.EqualityExprContext ctx) {
        logger.debug("Traitement d'une expression d'égalité");

        // Si c'est une comparaison d'égalité
        if (ctx.getChildCount() > 1) {
            Object left = visit(ctx.getChild(0));
            String operator = ctx.getChild(1).getText();
            Object right = visit(ctx.getChild(2));

            logger.debug("Expression d'égalité: {} {} {}", left, operator, right);

            // Égalité
            if (operator.equals("==")) {
                if (left == null && right == null) {
                    return true;
                } else if (left == null || right == null) {
                    return false;
                } else {
                    return left.equals(right);
                }
            }
            // Inégalité
            else if (operator.equals("!=")) {
                if (left == null && right == null) {
                    return false;
                } else if (left == null || right == null) {
                    return true;
                } else {
                    return !left.equals(right);
                }
            }
        }

        return visitChildren(ctx);
    }

    @Override
    public Object visitExpression(JnaneLangParser.ExpressionContext ctx) {
        logger.debug("Visite d'une expression: {}", ctx.getText());
        return visitChildren(ctx);
    }
}
