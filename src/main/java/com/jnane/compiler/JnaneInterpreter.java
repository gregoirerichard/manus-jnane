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
        logger.debug("Interprétation de l'appel de fonction: {}", functionName);
        
        if (functionName.equals("math:add") && args.length == 2) {
            if (args[0] instanceof Integer && args[1] instanceof Integer) {
                return interpretAddition((Integer) args[0], (Integer) args[1]);
            }
        }
        
        logger.warn("Fonction non supportée ou arguments invalides: {}", functionName);
        return null;
    }
    
    /**
     * Réinitialise l'état de l'interpréteur
     */
    public void reset() {
        variables.clear();
        logger.debug("État de l'interpréteur réinitialisé");
    }
}
