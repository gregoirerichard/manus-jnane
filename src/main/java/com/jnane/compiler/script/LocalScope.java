package com.jnane.compiler.script;

import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe représentant une portée locale d'exécution dans le langage Jnane.
 * Cette classe étend Scope et permet d'accéder aux variables des portées parentes.
 */
public class LocalScope extends Scope {
    private static final Logger logger = LoggerFactory.getLogger(LocalScope.class);
    
    /**
     * Constructeur avec une portée parente.
     * 
     * @param parentScope Portée parente
     */
    public LocalScope(Scope parentScope) {
        super(parentScope);
        logger.debug("Création d'une portée locale avec parent");
    }
    
    /**
     * Retourne la valeur d'une variable en cherchant d'abord dans cette portée,
     * puis dans les portées parentes si nécessaire.
     * 
     * @param variableName Nom de la variable
     * @return Valeur de la variable ou null si elle n'existe pas
     */
    @Override
    public Object getVariableValue(String variableName) {
        if (variables.containsKey(variableName)) {
            return variables.get(variableName);
        } else if (parentScope != null) {
            return parentScope.getVariableValue(variableName);
        } else {
            return null;
        }
    }
    
    /**
     * Vérifie si une variable existe dans cette portée ou dans les portées parentes.
     * 
     * @param variableName Nom de la variable
     * @return true si la variable existe, false sinon
     */
    @Override
    public boolean hasVariable(String variableName) {
        if (variables.containsKey(variableName)) {
            return true;
        } else if (parentScope != null) {
            return parentScope.hasVariable(variableName);
        } else {
            return false;
        }
    }
    
    /**
     * Retourne une représentation textuelle de cette portée locale et de ses portées parentes.
     * 
     * @return Représentation textuelle
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Local Scope:\n");
        
        // Afficher les variables de cette portée
        for (Entry<String, Object> entry : variables.entrySet()) {
            sb.append("\t").append(entry.getKey()).append(" = ");
            if (entry.getValue() instanceof String) {
                sb.append("\"").append(entry.getValue()).append("\"");
            } else {
                sb.append(entry.getValue());
            }
            sb.append("\n");
        }
        
        // Afficher les portées parentes
        if (parentScope != null) {
            sb.append("Parent Scope:\n");
            sb.append(parentScope.toString());
        }
        
        return sb.toString();
    }
}
