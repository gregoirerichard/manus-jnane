package com.causallang.compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire pour gérer le chargement des fichiers Jnane
 * en respectant la structure des namespaces.
 */
public class JnaneFileLoader {
    
    /**
     * Charge un fichier Jnane en fonction de son chemin.
     * 
     * @param filePath Chemin du fichier à charger
     * @return Le contenu du fichier
     * @throws IOException En cas d'erreur de lecture
     */
    public static String loadFile(String filePath) throws IOException {
        return org.apache.commons.io.FileUtils.readFileToString(new File(filePath), "UTF-8");
    }
    
    /**
     * Détermine le namespace d'un fichier Jnane en fonction de son chemin.
     * 
     * @param filePath Chemin du fichier
     * @return Le namespace correspondant
     */
    public static String getNamespaceFromPath(String filePath) {
        Path path = Paths.get(filePath);
        Path fileName = path.getFileName();
        Path parent = path.getParent();
        
        if (parent == null) {
            return "";
        }
        
        List<String> namespaceParts = new ArrayList<>();
        Path current = parent;
        
        while (current != null && !current.toString().isEmpty()) {
            namespaceParts.add(0, current.getFileName().toString());
            current = current.getParent();
        }
        
        return String.join(".", namespaceParts);
    }
    
    /**
     * Détermine le nom de la fonction d'un fichier Jnane en fonction de son nom de fichier.
     * 
     * @param filePath Chemin du fichier
     * @return Le nom de la fonction
     */
    public static String getFunctionNameFromPath(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        
        // Enlever l'extension .jn
        if (fileName.endsWith(".jn")) {
            return fileName.substring(0, fileName.length() - 3);
        }
        
        return fileName;
    }
    
    /**
     * Construit le chemin complet d'une fonction à partir de son namespace et de son nom.
     * 
     * @param baseDir Répertoire de base
     * @param namespace Namespace de la fonction
     * @param functionName Nom de la fonction
     * @return Le chemin complet du fichier
     */
    public static String buildFunctionPath(String baseDir, String namespace, String functionName) {
        String namespacePath = namespace.replace('.', File.separatorChar);
        return Paths.get(baseDir, namespacePath, functionName + ".jn").toString();
    }
    
    /**
     * Vérifie si un fichier est un fichier Jnane valide.
     * 
     * @param filePath Chemin du fichier
     * @return true si le fichier est un fichier Jnane valide, false sinon
     */
    public static boolean isValidJnaneFile(String filePath) {
        return filePath.endsWith(".jn");
    }
}
