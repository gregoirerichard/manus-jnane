package com.jnane.test;

import com.jnane.compiler.*;
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
 * en utilisant les classes Script et ScriptExecutor
 */
public class AddTest {
    private static final Logger logger = LoggerFactory.getLogger(AddTest.class);
    private ScriptExecutor scriptExecutor;

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
        scriptExecutor = new ScriptExecutor();
    }

    @AfterEach
    public void tearDown() {
        MDC.remove("testname");
        logger.info("Fin du test AddTest");
    }

    /**
     * Test l'interprétation du script testAdd.jn qui utilise la fonction math:add
     * en utilisant les classes Script et ScriptExecutor
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

            // Charger le script à partir du fichier
            Script script = new Script(testFilePath);
            logger.debug("Script chargé: {}", script.getFullFunctionName());
            
            // Afficher le contenu source du script
            logger.debug("Contenu source du script:\n{}", script.toString());
            
            // Afficher la structure parsée du script
            logger.debug("Structure parsée du script:\n{}", script.getParsedScript());

            // Exécuter le script avec ScriptExecutor
            Object resultat = scriptExecutor.executeScript(script);
            logger.debug("Exécution du script terminée");
            
            // Afficher le résultat
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