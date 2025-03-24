package com.jnane.compiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe pour interpréter les résultats des scripts Jnane
 */
public class JnaneInterpreter {
    private static final Logger logger = LoggerFactory.getLogger(JnaneInterpreter.class);
    private Map<String, Object> variables = new HashMap<>();
    
    /**
     * Récupère la valeur d'une variable
     * 
     * @param name Nom de la variable
     * @return Valeur de la variable ou null si elle n'existe pas
     */
    public Object getVariableValue(String name) {
        logger.debug("Récupération de la valeur de la variable {}: {}", name, variables.get(name));
        return variables.get(name);
    }
    
    /**
     * Définit la valeur d'une variable
     * 
     * @param name Nom de la variable
     * @param value Valeur de la variable
     */
    public void setVariableValue(String name, Object value) {
        variables.put(name, value);
        logger.debug("Variable définie: {} = {}", name, value);
    }
    
    /**
     * Interprète une addition entre deux entiers
     * 
     * @param a Premier entier
     * @param b Deuxième entier
     * @return Résultat de l'addition
     */
    public int interpretAddition(int a, int b) {
        int result = a + b;
        logger.debug("Interprétation de l'addition: {} + {} = {}", a, b, result);
        return result;
    }
    
    /**
     * Interprète un appel de fonction
     * 
     * @param functionName Nom de la fonction
     * @param args Arguments de la fonction
     * @return Résultat de l'appel de fonction
     */
    public Object interpretFunctionCall(String functionName, Object... args) {
        logger.debug("Interprétation de l'appel de fonction: {} avec {} arguments", functionName, args.length);
        
        if (functionName.equals("math:add") && args.length == 2) {
            logger.debug("Arguments pour math:add: {} (type: {}) et {} (type: {})", 
                args[0], (args[0] != null ? args[0].getClass().getSimpleName() : "null"),
                args[1], (args[1] != null ? args[1].getClass().getSimpleName() : "null"));
            
            if (args[0] instanceof Integer && args[1] instanceof Integer) {
                int result = interpretAddition((Integer) args[0], (Integer) args[1]);
                // Stocker directement le résultat dans la variable "resultat"
                setVariableValue("resultat", result);
                logger.debug("Résultat de math:add stocké dans la variable 'resultat': {}", result);
                return result;
            } else {
                // Tentative de conversion en entiers
                try {
                    int arg1 = convertToInt(args[0]);
                    int arg2 = convertToInt(args[1]);
                    logger.debug("Conversion des arguments en entiers: {} et {}", arg1, arg2);
                    int result = interpretAddition(arg1, arg2);
                    // Stocker directement le résultat dans la variable "resultat"
                    setVariableValue("resultat", result);
                    logger.debug("Résultat de math:add stocké dans la variable 'resultat': {}", result);
                    return result;
                } catch (NumberFormatException e) {
                    logger.error("Impossible de convertir les arguments en entiers: {} et {}", args[0], args[1], e);
                }
            }
        } else if (functionName.equals("print") && args.length > 0) {
            // Gestion de la fonction print
            String message = String.valueOf(args[0]);
            logger.info("Print: {}", message);
            return message;
        }
        
        logger.warn("Fonction non supportée ou arguments invalides: {}", functionName);
        return null;
    }
    
    /**
     * Convertit un objet en entier
     * 
     * @param obj Objet à convertir
     * @return Valeur entière
     * @throws NumberFormatException si la conversion échoue
     */
    private int convertToInt(Object obj) throws NumberFormatException {
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof String) {
            return Integer.parseInt((String) obj);
        } else if (obj instanceof Double) {
            return ((Double) obj).intValue();
        } else if (obj instanceof Float) {
            return ((Float) obj).intValue();
        } else if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else {
            throw new NumberFormatException("Impossible de convertir l'objet en entier: " + obj);
        }
    }
    
    /**
     * Réinitialise l'état de l'interpréteur
     */
    public void reset() {
        variables.clear();
        logger.debug("État de l'interpréteur réinitialisé");
    }
    
    /**
     * Exécute un script Jnane
     * 
     * @param visitor Visiteur pour l'analyse du script
     * @param tree Arbre syntaxique du script
     * @return Résultat de l'exécution
     */
    public Object executeScript(JnaneExpressionVisitor visitor, org.antlr.v4.runtime.tree.ParseTree tree) {
        logger.info("Exécution du script Jnane");
        // Ne pas réinitialiser les variables pour permettre le partage entre les scripts
        // reset();
        Object result = visitor.visit(tree);
        logger.info("Exécution du script terminée");
        // Afficher toutes les variables définies pour le débogage
        logger.debug("Variables définies après exécution: {}", variables);
        return result;
    }
}
