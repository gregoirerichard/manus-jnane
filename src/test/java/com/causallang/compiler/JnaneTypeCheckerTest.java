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
import java.util.Set;

/**
 * Tests unitaires pour le vérificateur de types du langage Jnane.
 */
public class JnaneTypeCheckerTest {
    
    private JnaneTypeChecker typeChecker;
    
    @BeforeEach
    public void setUp() {
        typeChecker = new JnaneTypeChecker();
    }
    
    @Test
    public void testVariableEvaluableWithCorrectType() {
        // Arrange
        Set<String> views = new HashSet<>();
        typeChecker.addVariable("resultat", "Entier", views);
        
        // Act
        boolean isEvaluable = typeChecker.isVariableEvaluable("resultat", "Entier");
        
        // Assert
        assertTrue(isEvaluable, "La variable devrait être évaluable avec son type correct");
        assertFalse(typeChecker.hasErrors(), "Aucune erreur ne devrait être détectée");
    }
    
    @Test
    public void testVariableEvaluableWithView() {
        // Arrange
        Set<String> views = new HashSet<>();
        views.add("EntierPositif");
        typeChecker.addVariable("resultat", "Entier", views);
        typeChecker.addViewType("EntierPositif");
        
        // Act
        boolean isEvaluable = typeChecker.isVariableEvaluable("resultat", "EntierPositif");
        
        // Assert
        assertTrue(isEvaluable, "La variable devrait être évaluable avec un type de vue");
        assertFalse(typeChecker.hasErrors(), "Aucune erreur ne devrait être détectée");
    }
    
    @Test
    public void testVariableNotEvaluableWithIncorrectType() {
        // Arrange
        Set<String> views = new HashSet<>();
        typeChecker.addVariable("resultat", "Entier", views);
        
        // Act
        boolean isEvaluable = typeChecker.isVariableEvaluable("resultat", "Chaine");
        
        // Assert
        assertFalse(isEvaluable, "La variable ne devrait pas être évaluable avec un type incorrect");
        assertTrue(typeChecker.hasErrors(), "Une erreur devrait être détectée");
    }
    
    @Test
    public void testUndefinedVariable() {
        // Act
        boolean isEvaluable = typeChecker.isVariableEvaluable("variableInexistante", "Entier");
        
        // Assert
        assertFalse(isEvaluable, "Une variable non définie ne devrait pas être évaluable");
        assertTrue(typeChecker.hasErrors(), "Une erreur devrait être détectée");
    }
}
