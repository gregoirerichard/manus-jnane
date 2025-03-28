package com.jnane.compiler.script;

import com.jnane.compiler.JnaneExpressionVisitor;
import com.jnane.compiler.JnaneInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Classe responsable de l'exécution des scripts Jnane.
 * Cette classe encapsule le processus d'exécution d'un script en utilisant
 * la classe Script pour charger et parser le script, puis en l'exécutant
 * avec un interpréteur.
 */
public class ScriptExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ScriptExecutor.class);
    
    // Interpréteur utilisé pour exécuter les scripts
    private final JnaneInterpreter interpreter;
    
    /**
     * Constructeur par défaut.
     * Crée un nouvel interpréteur pour exécuter les scripts.
     */
    public ScriptExecutor() {
        this.interpreter = new JnaneInterpreter("src/main/resources");
        logger.debug("ScriptExecutor initialisé avec un nouvel interpréteur");
    }
    
    /**
     * Constructeur avec un interpréteur existant.
     * 
     * @param interpreter Interpréteur à utiliser pour exécuter les scripts
     */
    public ScriptExecutor(JnaneInterpreter interpreter) {
        this.interpreter = interpreter;
        logger.debug("ScriptExecutor initialisé avec un interpréteur existant");
    }
    
    /**
     * Exécute un script à partir d'un fichier.
     * 
     * @param scriptPath Chemin du fichier script à exécuter
     * @return Résultat de l'exécution (valeur de la variable "resultat" si elle existe)
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public Object executeScript(String scriptPath) throws IOException {
        logger.debug("Exécution du script: {}", scriptPath);
        
        // Charger le script
        Script script = new Script(scriptPath);
        logger.debug("Script chargé: {}", script.getFullFunctionName());
        
        return executeScript(script);
    }
    
    /**
     * Exécute un script déjà chargé.
     * 
     * @param script Script à exécuter
     * @return Objet Scope contenant le contexte d'exécution et les variables
     */
    public Scope executeScript(Script script) {
        logger.debug("Exécution du script: {}", script.getFullFunctionName());
        
        try {
            // Créer un visiteur pour interpréter le contenu
            JnaneExpressionVisitor visitor = new JnaneExpressionVisitor(interpreter);
            
            // Définir le script courant dans le visiteur
            visitor.setCurrentScript(script);
            
            // Vérifier que le contexte du programme n'est pas null
            if (script.getProgramContext() == null) {
                logger.error("Le contexte du programme est null");
                return null;
            }
            
            // Exécuter le script et récupérer le Scope
            Scope scope = interpreter.executeScript(visitor, script.getProgramContext());
            logger.debug("Exécution du script terminée");
            
            return scope;
        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution du script: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Exécute un script avec des arguments.
     * 
     * @param script Script à exécuter
     * @param args Arguments à passer au script
     * @return Résultat de l'exécution (valeur de la variable "resultat" si elle existe)
     */
    public Object executeScript(Script script, Map<String, Object> args) {
        logger.debug("Exécution du script: {} avec {} arguments", script.getFullFunctionName(), args.size());
        
        // Définir les variables d'entrée
        for (Map.Entry<String, Object> entry : args.entrySet()) {
            interpreter.setVariableValue(entry.getKey(), entry.getValue());
        }
        
        return executeScript(script);
    }
    
    /**
     * Exécute un script à partir d'un fichier avec des arguments.
     * 
     * @param scriptPath Chemin du fichier script à exécuter
     * @param args Arguments à passer au script
     * @return Résultat de l'exécution (valeur de la variable "resultat" si elle existe)
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public Object executeScript(String scriptPath, Map<String, Object> args) throws IOException {
        logger.debug("Exécution du script: {} avec {} arguments", scriptPath, args.size());
        
        // Charger le script
        Script script = new Script(scriptPath);
        logger.debug("Script chargé: {}", script.getFullFunctionName());
        
        return executeScript(script, args);
    }
    
    /**
     * Réinitialise l'interpréteur.
     * Cette méthode efface toutes les variables et l'état de l'interpréteur.
     */
    public void reset() {
        interpreter.reset();
        logger.debug("Interpréteur réinitialisé");
    }
    
    /**
     * Retourne l'interpréteur utilisé par cet exécuteur.
     * 
     * @return Interpréteur
     */
    public JnaneInterpreter getInterpreter() {
        return interpreter;
    }
}
