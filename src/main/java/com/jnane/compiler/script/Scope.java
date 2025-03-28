package com.jnane.compiler.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe représentant une portée d'exécution dans le langage Jnane.
 * Cette classe gère les variables et leur visibilité dans différents contextes d'exécution.
 */
public class Scope {
    private static final Logger logger = LoggerFactory.getLogger(Scope.class);
    
    // Identifiant de l'axe d'exécution
    protected String axis = "";
    
    // Portée parente
    protected Scope parentScope;
    
    // Portée racine
    protected RootScope rootScope;
    
    // Variables définies dans cette portée
    protected Map<String, Object> variables = new HashMap<>();
    
    /**
     * Constructeur par défaut.
     */
    public Scope() {
        logger.debug("Création d'une nouvelle portée");
    }
    
    /**
     * Constructeur avec une portée parente.
     * 
     * @param parentScope Portée parente
     */
    public Scope(Scope parentScope) {
        this.parentScope = parentScope;
        this.rootScope = parentScope.getRootScope();
        this.axis = parentScope.getAxis();
        logger.debug("Création d'une nouvelle portée avec parent");
    }
    
    /**
     * Retourne la portée racine.
     * 
     * @return Portée racine
     */
    public RootScope getRootScope() {
        return rootScope;
    }
    
    /**
     * Retourne la portée parente.
     * 
     * @return Portée parente
     */
    public Scope getParentScope() {
        return parentScope;
    }
    
    /**
     * Définit la portée parente.
     * 
     * @param parentScope Portée parente
     */
    public void setParentScope(Scope parentScope) {
        this.parentScope = parentScope;
    }
    
    /**
     * Retourne l'identifiant de l'axe d'exécution.
     * 
     * @return Identifiant de l'axe
     */
    public String getAxis() {
        return axis;
    }
    
    /**
     * Définit l'identifiant de l'axe d'exécution.
     * 
     * @param axis Identifiant de l'axe
     */
    public void setAxis(String axis) {
        this.axis = axis;
    }
    
    /**
     * Retourne toutes les variables définies dans cette portée.
     * 
     * @return Map des variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    /**
     * Retourne la valeur d'une variable.
     * 
     * @param variableName Nom de la variable
     * @return Valeur de la variable ou null si elle n'existe pas
     */
    public Object getVariableValue(String variableName) {
        if (variables.containsKey(variableName)) {
            return variables.get(variableName);
        } else {
            // Si la variable n'est pas trouvée dans cette portée et qu'il y a une portée parente,
            // chercher dans la portée parente
            if (parentScope != null) {
                return parentScope.getVariableValue(variableName);
            }
            return null;
        }
    }
    
    /**
     * Définit la valeur d'une variable.
     * 
     * @param variableName Nom de la variable
     * @param value Valeur de la variable
     * @return Ancienne valeur de la variable ou null si elle n'existait pas
     */
    public Object setVariableValue(String variableName, Object value) {
        Object oldValue = variables.get(variableName);
        variables.put(variableName, value);
        logger.debug("Variable définie: {} = {}", variableName, value);
        return oldValue;
    }
    
    /**
     * Vérifie si une variable existe dans cette portée.
     * 
     * @param variableName Nom de la variable
     * @return true si la variable existe, false sinon
     */
    public boolean hasVariable(String variableName) {
        boolean exists = variables.containsKey(variableName);
        if (!exists && parentScope != null) {
            exists = parentScope.hasVariable(variableName);
        }
        return exists;
    }
    
    /**
     * Retourne une représentation textuelle de cette portée.
     * 
     * @return Représentation textuelle
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scope:\n");
        
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
        
        return sb.toString();
    }
}
