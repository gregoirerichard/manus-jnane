package com.jnane.compiler.script;

import com.jnane.compiler.ContextStringBuilder;
import com.jnane.compiler.JnaneFileLoader;
import com.jnane.compiler.JnaneLangLexer;
import com.jnane.compiler.JnaneLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Classe représentant un script Jnane.
 * Cette classe encapsule le contenu source du script et son arbre syntaxique.
 */
public class Script {
    private static final Logger logger = LoggerFactory.getLogger(Script.class);

    // Contenu source original du script
    private final String sourceCode;
    
    // Arbre syntaxique du script
    private final JnaneLangParser.ProgramContext programContext;
    
    // Métadonnées du script
    private final String filePath;
    private final String namespace;
    private final String functionName;
    
    /**
     * Constructeur à partir d'un chemin de fichier.
     * 
     * @param filePath Chemin du fichier script
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    public Script(String filePath) throws IOException {
        this.filePath = filePath;
        this.sourceCode = JnaneFileLoader.loadFile(filePath);
        this.namespace = JnaneFileLoader.getNamespaceFromPath(filePath);
        this.functionName = JnaneFileLoader.getFunctionNameFromPath(filePath);
        
        // Parser le contenu source pour créer l'arbre syntaxique
        CharStream input = CharStreams.fromString(sourceCode);
        JnaneLangLexer lexer = new JnaneLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JnaneLangParser parser = new JnaneLangParser(tokens);
        this.programContext = parser.program();
        
        logger.debug("Script chargé: {}:{} depuis {}", namespace, functionName, filePath);
    }
    
    /**
     * Constructeur à partir d'un contenu source.
     * 
     * @param sourceCode Contenu source du script
     * @param namespace Namespace du script
     * @param functionName Nom de la fonction
     */
    public Script(String sourceCode, String namespace, String functionName) {
        this.sourceCode = sourceCode;
        this.namespace = namespace;
        this.functionName = functionName;
        this.filePath = null;
        
        // Parser le contenu source pour créer l'arbre syntaxique
        CharStream input = CharStreams.fromString(sourceCode);
        JnaneLangLexer lexer = new JnaneLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JnaneLangParser parser = new JnaneLangParser(tokens);
        this.programContext = parser.program();
        
        logger.debug("Script créé en mémoire: {}:{}", namespace, functionName);
    }
    
    /**
     * Retourne le contenu source original du script.
     * 
     * @return Contenu source du script
     */
    @Override
    public String toString() {
        return sourceCode;
    }
    
    /**
     * Retourne l'arbre syntaxique du script.
     * 
     * @return Arbre syntaxique
     */
    public JnaneLangParser.ProgramContext getProgramContext() {
        return programContext;
    }
    
    /**
     * Retourne une représentation textuelle de l'arbre syntaxique du script.
     * Cette méthode permet de visualiser la structure du script après parsing.
     * 
     * @return Représentation textuelle de l'arbre syntaxique
     */
    public String getParsedScript() {
        return ContextStringBuilder.programToString(programContext);
    }
    
    /**
     * Retourne le chemin du fichier script.
     * 
     * @return Chemin du fichier ou null si le script a été créé en mémoire
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Retourne le namespace du script.
     * 
     * @return Namespace
     */
    public String getNamespace() {
        return namespace;
    }
    
    /**
     * Retourne le nom de la fonction du script.
     * 
     * @return Nom de la fonction
     */
    public String getFunctionName() {
        return functionName;
    }
    
    /**
     * Retourne le nom complet de la fonction (namespace:fonction).
     * 
     * @return Nom complet de la fonction
     */
    public String getFullFunctionName() {
        return namespace + ":" + functionName;
    }
}
