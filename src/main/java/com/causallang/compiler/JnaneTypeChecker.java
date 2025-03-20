package com.causallang.compiler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Classe pour la vérification des types et des variables évaluables dans le langage Jnane.
 * Cette classe implémente les vérifications nécessaires pour s'assurer que toutes les variables
 * sont évaluables (soit du type spécifié, soit ayant une vue de ce type).
 */
public class JnaneTypeChecker {
    
    private Map<String, TypeInfo> typeEnvironment;
    private Set<String> viewTypes;
    private Set<String> errors;
    
    public JnaneTypeChecker() {
        this.typeEnvironment = new HashMap<>();
        this.viewTypes = new HashSet<>();
        this.errors = new HashSet<>();
    }
    
    /**
     * Vérifie qu'une variable est évaluable (du type spécifié ou ayant une vue de ce type).
     * 
     * @param variableName Nom de la variable
     * @param expectedType Type attendu
     * @return true si la variable est évaluable, false sinon
     */
    public boolean isVariableEvaluable(String variableName, String expectedType) {
        if (!typeEnvironment.containsKey(variableName)) {
            errors.add("Erreur: Variable '" + variableName + "' non définie.");
            return false;
        }
        
        TypeInfo typeInfo = typeEnvironment.get(variableName);
        String actualType = typeInfo.getType();
        
        // Vérification directe du type
        if (actualType.equals(expectedType)) {
            return true;
        }
        
        // Vérification des vues
        for (String viewType : typeInfo.getViews()) {
            if (viewType.equals(expectedType)) {
                return true;
            }
        }
        
        errors.add("Erreur: Variable '" + variableName + "' de type '" + actualType + 
                   "' n'est pas évaluable comme '" + expectedType + "'.");
        return false;
    }
    
    /**
     * Ajoute une variable à l'environnement de type.
     * 
     * @param variableName Nom de la variable
     * @param type Type de la variable
     * @param views Vues disponibles pour ce type
     */
    public void addVariable(String variableName, String type, Set<String> views) {
        typeEnvironment.put(variableName, new TypeInfo(type, views));
    }
    
    /**
     * Ajoute un type de vue à l'ensemble des types de vue connus.
     * 
     * @param viewType Type de vue
     */
    public void addViewType(String viewType) {
        viewTypes.add(viewType);
    }
    
    /**
     * Vérifie si un type est un type de vue connu.
     * 
     * @param type Type à vérifier
     * @return true si le type est un type de vue, false sinon
     */
    public boolean isViewType(String type) {
        return viewTypes.contains(type);
    }
    
    /**
     * Récupère les erreurs de vérification de type.
     * 
     * @return Ensemble des erreurs
     */
    public Set<String> getErrors() {
        return errors;
    }
    
    /**
     * Vérifie si des erreurs ont été détectées.
     * 
     * @return true si des erreurs ont été détectées, false sinon
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Classe interne pour stocker les informations de type d'une variable.
     */
    private static class TypeInfo {
        private String type;
        private Set<String> views;
        
        public TypeInfo(String type, Set<String> views) {
            this.type = type;
            this.views = views;
        }
        
        public String getType() {
            return type;
        }
        
        public Set<String> getViews() {
            return views;
        }
    }
}
