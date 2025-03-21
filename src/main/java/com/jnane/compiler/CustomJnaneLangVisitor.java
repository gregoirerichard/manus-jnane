package com.jnane.compiler;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * Interface personnalisée du visiteur pour l'AST du langage Jnane.
 * Cette interface peut être utilisée pour étendre les fonctionnalités du visiteur généré par ANTLR.
 */
public interface CustomJnaneLangVisitor<T> extends ParseTreeVisitor<T> {
    
    // Méthodes supplémentaires spécifiques au langage Jnane peuvent être ajoutées ici
    
}
