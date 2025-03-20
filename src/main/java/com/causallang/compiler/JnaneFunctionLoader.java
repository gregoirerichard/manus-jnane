package com.causallang.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe pour le chargement des fonctions Jnane et la détection des cycles de dépendances.
 * Cette classe implémente les fonctionnalités pour charger toutes les fonctions d'un répertoire
 * et vérifier qu'il n'y a pas de cycle dans les définitions des fonctions.
 */
public class JnaneFunctionLoader {
    
    private Map<String, FunctionInfo> functions;
    private Set<String> loadedFiles;
    private Set<String> errors;
    
    public JnaneFunctionLoader() {
        this.functions = new HashMap<>();
        this.loadedFiles = new HashSet<>();
        this.errors = new HashSet<>();
    }
    
    /**
     * Charge toutes les fonctions Jnane d'un répertoire et de ses sous-répertoires.
     * 
     * @param directory Répertoire à explorer
     * @throws IOException En cas d'erreur de lecture
     */
    public void loadFunctionsFromDirectory(String directory) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directory))) {
            List<Path> jnFiles = paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".jn"))
                .collect(Collectors.toList());
            
            for (Path path : jnFiles) {
                loadFunctionFromFile(path.toString());
            }
        }
        
        // Vérification des cycles après le chargement de toutes les fonctions
        detectCycles();
    }
    
    /**
     * Charge une fonction Jnane à partir d'un fichier.
     * 
     * @param filePath Chemin du fichier
     * @throws IOException En cas d'erreur de lecture
     */
    public void loadFunctionFromFile(String filePath) throws IOException {
        if (loadedFiles.contains(filePath)) {
            return; // Fichier déjà chargé
        }
        
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        String namespace = JnaneFileLoader.getNamespaceFromPath(filePath);
        String functionName = JnaneFileLoader.getFunctionNameFromPath(filePath);
        String fullName = namespace + ":" + functionName;
        
        // Analyse du contenu pour extraire les dépendances
        Set<String> dependencies = extractDependencies(content);
        
        // Ajout de la fonction à la liste des fonctions chargées
        functions.put(fullName, new FunctionInfo(fullName, filePath, dependencies));
        loadedFiles.add(filePath);
    }
    
    /**
     * Extrait les dépendances d'une fonction à partir de son contenu.
     * 
     * @param content Contenu du fichier de fonction
     * @return Ensemble des dépendances
     */
    private Set<String> extractDependencies(String content) {
        Set<String> dependencies = new HashSet<>();
        
        // Recherche des appels de fonction avec la notation namespace:fonction
        // Note: Ceci est une implémentation simplifiée, une analyse syntaxique complète serait nécessaire
        // pour une détection précise des dépendances
        String[] lines = content.split("\n");
        for (String line : lines) {
            // Recherche des motifs comme "ns:fonction("
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                int openParenIndex = line.indexOf('(', colonIndex);
                if (openParenIndex > colonIndex) {
                    String namespace = line.substring(0, colonIndex).trim();
                    String function = line.substring(colonIndex + 1, openParenIndex).trim();
                    
                    // Ignorer les mots-clés et les commentaires
                    if (!namespace.startsWith("//") && !namespace.equals("@name")) {
                        dependencies.add(namespace + ":" + function);
                    }
                }
            }
        }
        
        return dependencies;
    }
    
    /**
     * Détecte les cycles dans les dépendances des fonctions.
     */
    public void detectCycles() {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (String functionName : functions.keySet()) {
            if (!visited.contains(functionName)) {
                detectCyclesUtil(functionName, visited, recursionStack);
            }
        }
    }
    
    /**
     * Fonction utilitaire pour la détection des cycles (algorithme DFS).
     * 
     * @param functionName Nom de la fonction à vérifier
     * @param visited Ensemble des fonctions visitées
     * @param recursionStack Pile de récursion pour détecter les cycles
     * @return true si un cycle est détecté, false sinon
     */
    private boolean detectCyclesUtil(String functionName, Set<String> visited, Set<String> recursionStack) {
        // Marquer la fonction comme visitée et l'ajouter à la pile de récursion
        visited.add(functionName);
        recursionStack.add(functionName);
        
        // Récupérer les dépendances de la fonction
        FunctionInfo functionInfo = functions.get(functionName);
        if (functionInfo != null) {
            for (String dependency : functionInfo.getDependencies()) {
                // Si la dépendance n'est pas encore visitée
                if (!visited.contains(dependency)) {
                    if (detectCyclesUtil(dependency, visited, recursionStack)) {
                        errors.add("Cycle détecté impliquant la fonction: " + functionName);
                        return true;
                    }
                } 
                // Si la dépendance est dans la pile de récursion, un cycle est détecté
                else if (recursionStack.contains(dependency)) {
                    errors.add("Cycle détecté: " + dependency + " -> " + functionName);
                    return true;
                }
            }
        }
        
        // Retirer la fonction de la pile de récursion
        recursionStack.remove(functionName);
        return false;
    }
    
    /**
     * Récupère les erreurs de chargement et de détection de cycles.
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
     * Récupère toutes les fonctions chargées.
     * 
     * @return Map des fonctions chargées
     */
    public Map<String, FunctionInfo> getFunctions() {
        return functions;
    }
    
    /**
     * Classe interne pour stocker les informations d'une fonction.
     */
    public static class FunctionInfo {
        private String name;
        private String filePath;
        private Set<String> dependencies;
        
        public FunctionInfo(String name, String filePath, Set<String> dependencies) {
            this.name = name;
            this.filePath = filePath;
            this.dependencies = dependencies;
        }
        
        public String getName() {
            return name;
        }
        
        public String getFilePath() {
            return filePath;
        }
        
        public Set<String> getDependencies() {
            return dependencies;
        }
    }
}
