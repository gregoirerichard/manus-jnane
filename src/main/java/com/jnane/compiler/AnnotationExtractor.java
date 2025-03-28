package com.jnane.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe utilitaire pour extraire les annotations des fichiers Jnane.
 * Cette classe permet d'extraire les annotations @field et @view ainsi que leurs types associés.
 */
public class AnnotationExtractor {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationExtractor.class);
    
    // Regex pour capturer les annotations @field et @view avec leur type
    private static final Pattern FIELD_VIEW_PATTERN = Pattern.compile(
        "@(field|view)\\s+(\\w+)\\s*:\\s*(\\S+)",
        Pattern.CASE_INSENSITIVE
    );
    
    // Regex pour capturer les annotations avec le nouveau format (annotations multiples)
    private static final Pattern NEW_ANNOTATION_PATTERN = Pattern.compile(
        "(@\\w+\\s+)*@(field|view)\\s+(\\w+)\\s*:\\s*(\\S+)",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Extrait les annotations @field et @view d'un fichier Jnane.
     * 
     * @param filePath Chemin du fichier à analyser
     * @return Map contenant les noms des champs et leurs types associés
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public static Map<String, FieldInfo> extractAnnotations(String filePath) throws IOException {
        logger.debug("Extraction des annotations du fichier: {}", filePath);
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return extractAnnotationsFromContent(content);
    }
    
    /**
     * Extrait les annotations @field et @view à partir du contenu d'un fichier.
     * 
     * @param content Contenu du fichier à analyser
     * @return Map contenant les noms des champs et leurs types associés
     */
    public static Map<String, FieldInfo> extractAnnotationsFromContent(String content) {
        Map<String, FieldInfo> annotations = new HashMap<>();
        
        // Recherche des annotations avec l'ancien format
        Matcher matcher = FIELD_VIEW_PATTERN.matcher(content);
        while (matcher.find()) {
            String annotationType = matcher.group(1).toLowerCase(); // field ou view
            String fieldName = matcher.group(2);
            String fieldType = matcher.group(3);
            
            annotations.put(fieldName, new FieldInfo(fieldName, fieldType, annotationType.equals("view")));
            logger.debug("Annotation trouvée: @{} {} : {}", annotationType, fieldName, fieldType);
        }
        
        // Recherche des annotations avec le nouveau format
        matcher = NEW_ANNOTATION_PATTERN.matcher(content);
        while (matcher.find()) {
            String annotationType = matcher.group(2).toLowerCase(); // field ou view
            String fieldName = matcher.group(3);
            String fieldType = matcher.group(4);
            
            // Ne pas ajouter si déjà trouvé avec l'ancien format
            if (!annotations.containsKey(fieldName)) {
                annotations.put(fieldName, new FieldInfo(fieldName, fieldType, annotationType.equals("view")));
                logger.debug("Annotation (nouveau format) trouvée: @{} {} : {}", annotationType, fieldName, fieldType);
            }
        }
        
        return annotations;
    }
    
    /**
     * Classe interne pour stocker les informations d'un champ annoté.
     */
    public static class FieldInfo {
        private final String name;
        private final String type;
        private final boolean isView;
        
        public FieldInfo(String name, String type, boolean isView) {
            this.name = name;
            this.type = type;
            this.isView = isView;
        }
        
        public String getName() {
            return name;
        }
        
        public String getType() {
            return type;
        }
        
        public boolean isView() {
            return isView;
        }
        
        @Override
        public String toString() {
            return String.format("@%s %s : %s", isView ? "view" : "field", name, type);
        }
    }
}
