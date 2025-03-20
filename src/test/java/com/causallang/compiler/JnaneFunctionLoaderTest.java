package com.causallang.compiler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Tests unitaires pour le chargeur de fonctions du langage Jnane.
 */
public class JnaneFunctionLoaderTest {
    
    private JnaneFunctionLoader functionLoader;
    
    @BeforeEach
    public void setUp() {
        functionLoader = new JnaneFunctionLoader();
    }
    
    @Test
    public void testLoadFunctionFromFile(@TempDir Path tempDir) throws IOException {
        // Arrange
        Path functionFile = tempDir.resolve("test.jn");
        String content = "@name test.namespace:function\n" +
                         "@arg x Entier\n" +
                         "@field resultat Entier\n" +
                         "{\n" +
                         "    resultat = x;\n" +
                         "}";
        Files.writeString(functionFile, content);
        
        // Act
        functionLoader.loadFunctionFromFile(functionFile.toString());
        
        // Assert
        Map<String, JnaneFunctionLoader.FunctionInfo> functions = functionLoader.getFunctions();
        assertFalse(functions.isEmpty(), "La fonction devrait être chargée");
        assertFalse(functionLoader.hasErrors(), "Aucune erreur ne devrait être détectée");
    }
    
    @Test
    public void testDetectCyclesWithNoCycles(@TempDir Path tempDir) throws IOException {
        // Arrange
        // Fonction A qui dépend de B
        Path functionAFile = tempDir.resolve("a.jn");
        String contentA = "@name test:a\n" +
                          "@arg x Entier\n" +
                          "@field resultat Entier\n" +
                          "{\n" +
                          "    resultat = test:b(x);\n" +
                          "}";
        Files.writeString(functionAFile, contentA);
        
        // Fonction B qui ne dépend de rien
        Path functionBFile = tempDir.resolve("b.jn");
        String contentB = "@name test:b\n" +
                          "@arg x Entier\n" +
                          "@field resultat Entier\n" +
                          "{\n" +
                          "    resultat = x + 1;\n" +
                          "}";
        Files.writeString(functionBFile, contentB);
        
        // Act
        functionLoader.loadFunctionFromFile(functionAFile.toString());
        functionLoader.loadFunctionFromFile(functionBFile.toString());
        functionLoader.detectCycles();
        
        // Assert
        assertFalse(functionLoader.hasErrors(), "Aucun cycle ne devrait être détecté");
    }
    
    @Test
    public void testDetectCyclesWithCycles(@TempDir Path tempDir) throws IOException {
        // Arrange
        // Fonction A qui dépend de B
        Path functionAFile = tempDir.resolve("a.jn");
        String contentA = "@name test:a\n" +
                          "@arg x Entier\n" +
                          "@field resultat Entier\n" +
                          "{\n" +
                          "    resultat = test:b(x);\n" +
                          "}";
        Files.writeString(functionAFile, contentA);
        
        // Fonction B qui dépend de A (cycle)
        Path functionBFile = tempDir.resolve("b.jn");
        String contentB = "@name test:b\n" +
                          "@arg x Entier\n" +
                          "@field resultat Entier\n" +
                          "{\n" +
                          "    resultat = test:a(x);\n" +
                          "}";
        Files.writeString(functionBFile, contentB);
        
        // Act
        functionLoader.loadFunctionFromFile(functionAFile.toString());
        functionLoader.loadFunctionFromFile(functionBFile.toString());
        functionLoader.detectCycles();
        
        // Assert
        assertTrue(functionLoader.hasErrors(), "Un cycle devrait être détecté");
    }
    
    @Test
    public void testExtractDependencies() throws IOException {
        // Arrange
        String content = "@name test:function\n" +
                         "@arg x Entier\n" +
                         "@field resultat Entier\n" +
                         "{\n" +
                         "    // Appel à d'autres fonctions\n" +
                         "    temp1 = math.operations:add(x, 5);\n" +
                         "    temp2 = collections.list:create([1, 2, 3]);\n" +
                         "    resultat = temp1 + temp2.length;\n" +
                         "}";
        
        // Act & Assert
        // Cette méthode est privée, donc nous testons indirectement via loadFunctionFromFile
        // dans un fichier temporaire
        Path tempFile = Files.createTempFile("test_function", ".jn");
        Files.writeString(tempFile, content);
        
        functionLoader.loadFunctionFromFile(tempFile.toString());
        Map<String, JnaneFunctionLoader.FunctionInfo> functions = functionLoader.getFunctions();
        
        assertFalse(functions.isEmpty(), "La fonction devrait être chargée");
        
        // Vérifier que les dépendances ont été extraites
        JnaneFunctionLoader.FunctionInfo functionInfo = functions.values().iterator().next();
        Set<String> dependencies = functionInfo.getDependencies();
        
        assertTrue(dependencies.contains("math.operations:add"), "La dépendance math.operations:add devrait être détectée");
        assertTrue(dependencies.contains("collections.list:create"), "La dépendance collections.list:create devrait être détectée");
        
        // Nettoyage
        Files.deleteIfExists(tempFile);
    }
}
