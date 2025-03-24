package com.jnane.test;

import com.jnane.compiler.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe de test unitaire pour valider le parsing et l'interprétation de la fonction math:add
 */
public class AddTest {

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
        
        // Capturer la sortie standard pour vérifier le résultat
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        try {
            // Vérifier que les fichiers ont l'extension .jn
            Assertions.assertTrue(JnaneFileLoader.isValidJnaneFile(testFilePath), 
                "Le fichier de test doit avoir l'extension .jn");
            Assertions.assertTrue(JnaneFileLoader.isValidJnaneFile(mathAddFilePath), 
                "Le fichier de fonction doit avoir l'extension .jn");
            
            // Création du lexer et du parser pour le fichier de test
            CharStream input = CharStreams.fromFileName(testFilePath);
            JnaneLangLexer lexer = new JnaneLangLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            JnaneLangParser parser = new JnaneLangParser(tokens);
            
            // Analyse syntaxique
            ParseTree tree = parser.program();
            
            // Utilisation du visiteur simple pour interpréter le script
            // Sans vérification de type qui n'est pas nécessaire pour ce test simple
            JnaneLangVisitorImpl visitor = new JnaneLangVisitorImpl();
            visitor.visit(tree);
            
            // Vérifier que la sortie contient "Test réussi"
            String output = outContent.toString();
            Assertions.assertTrue(output.contains("Test réussi"), 
                "Le test devrait afficher un message de réussite, mais a affiché: " + output);
            
        } finally {
            // Restaurer la sortie standard
            System.setOut(originalOut);
        }
    }
}
