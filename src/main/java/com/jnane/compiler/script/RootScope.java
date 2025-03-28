package com.jnane.compiler.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe représentant la portée racine d'exécution dans le langage Jnane.
 * Cette classe étend Scope et contient une référence au script en cours d'exécution.
 */
public class RootScope extends Scope {
    private static final Logger logger = LoggerFactory.getLogger(RootScope.class);
    
    // Script associé à cette portée racine
    protected Script script;
    
    /**
     * Constructeur avec un script.
     * 
     * @param script Script associé à cette portée
     */
    public RootScope(Script script) {
        super();
        this.script = script;
        this.rootScope = this;
        logger.debug("Création d'une portée racine pour le script: {}", script.getFullFunctionName());
    }
    
    /**
     * Retourne la portée racine (this).
     * 
     * @return Cette portée racine
     */
    @Override
    public RootScope getRootScope() {
        return this;
    }
    
    /**
     * Retourne le script associé à cette portée.
     * 
     * @return Script associé
     */
    public Script getScript() {
        return script;
    }
    
    /**
     * Retourne une représentation textuelle de cette portée racine.
     * 
     * @return Représentation textuelle
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Script: ").append(script.getFullFunctionName());
        if (script.getFilePath() != null) {
            sb.append(" (").append(script.getFilePath()).append(")");
        }
        sb.append("\n");
        sb.append(super.toString());
        return sb.toString();
    }
}
