package com.jnane.test;

import com.jnane.compiler.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.nio.file.Paths;

/**
 * Classe de test unitaire pour valider le parsing et l'interprétation de la fonction math:add
 */
public class AddTest {
    private static final Logger logger = LoggerFactory.getLogger(AddTest.class);
    private final JnaneInterpreter interpreter = new JnaneInterpreter();

    @BeforeEach
    public void setUp() {
        // Configurer le nom du test pour le fichier de log
        MDC.put("testname", "AddTest");

        // Créer le répertoire de logs s'il n'existe pas
        File logDir = new File("target/logs");
        if (!logDir.exists()) {
            boolean mkdirs = logDir.mkdirs();
        }

        logger.info("Démarrage du test AddTest");
        interpreter.reset();
    }

    @AfterEach
    public void tearDown() {
        MDC.remove("testname");
        logger.info("Fin du test AddTest");
    }

    /**
     * Test l'interprétation du script testAdd.jn qui utilise la fonction math:add
     */
    @Test
    public void testAddFunction() throws Exception {
        // Récupérer le chemin du fichier de test
        String testResourcesPath = Paths.get("src", "test", "resources").toAbsolutePath().toString();
        String testFilePath = Paths.get(testResourcesPath, "com", "jnane", "test", "testAdd.jn").toString();

        // Récupérer le chemin du fichier de la fonction math:add
        String mainResourcesPath = Paths.get("src", "main", "resources").toAbsolutePath().toString();
        String mathAddFilePath = Paths.get(mainResourcesPath, "math", "add.jn").toString();

        logger.info("Fichier de test: {}", testFilePath);
        logger.info("Fichier de fonction: {}", mathAddFilePath);

        try {
            // Vérifier que les fichiers ont l'extension .jn
            Assertions.assertTrue(JnaneFileLoader.isValidJnaneFile(testFilePath),
                    "Le fichier de test doit avoir l'extension .jn");
            Assertions.assertTrue(JnaneFileLoader.isValidJnaneFile(mathAddFilePath),
                    "Le fichier de fonction doit avoir l'extension .jn");

            logger.debug("Validation des extensions de fichiers réussie");

            // Création du lexer et du parser pour le fichier de test
            CharStream input = CharStreams.fromFileName(testFilePath);
            JnaneLangLexer lexer = new JnaneLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JnaneLangParser parser = new JnaneLangParser(tokens);

            logger.debug("Lexer et parser créés avec succès");

            // Analyse syntaxique
            ParseTree tree = parser.program();
            logger.debug("Analyse syntaxique réussie");

            // Utilisation du visiteur d'expressions pour interpréter le script
            JnaneExpressionVisitor visitor = new JnaneExpressionVisitor(interpreter);
            interpreter.executeScript(visitor, tree);
            logger.debug("Exécution du script terminée");

            // Récupérer la valeur du résultat
            Object resultat = interpreter.getVariableValue("resultat");
            logger.info("Résultat obtenu: {}", resultat);

            // Vérifier que le résultat est correct
            Assertions.assertNotNull(resultat, "Le résultat ne devrait pas être null");
            Assertions.assertEquals(8, resultat, "Le résultat devrait être 8");

        } catch (Exception e) {
            logger.error("Erreur lors de l'exécution du test", e);
            throw e;
        }
    }
}