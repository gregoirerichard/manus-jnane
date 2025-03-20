package com.causallang.compiler;

import java.io.IOException;
import java.util.Set;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

/**
 * Classe principale du compilateur pour le langage Jnane.
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -jar jnane-compiler.jar <fichier.jn>");
            System.err.println("   ou: java -jar jnane-compiler.jar --dir <répertoire>");
            System.exit(1);
        }
        
        try {
            if (args[0].equals("--dir") && args.length > 1) {
                // Mode répertoire: charger toutes les fonctions et vérifier les cycles
                String directory = args[1];
                processDirectory(directory);
            } else {
                // Mode fichier unique: analyser un seul fichier
                String inputFile = args[0];
                processFile(inputFile);
            }
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Traite un répertoire contenant des fichiers Jnane.
     * 
     * @param directory Répertoire à traiter
     * @throws IOException En cas d'erreur de lecture
     */
    private static void processDirectory(String directory) throws IOException {
        System.out.println("Chargement des fonctions du répertoire: " + directory);
        
        // Charger toutes les fonctions et vérifier les cycles
        JnaneFunctionLoader functionLoader = new JnaneFunctionLoader();
        functionLoader.loadFunctionsFromDirectory(directory);
        
        // Afficher les erreurs de cycle s'il y en a
        if (functionLoader.hasErrors()) {
            System.err.println("Erreurs détectées lors du chargement des fonctions:");
            for (String error : functionLoader.getErrors()) {
                System.err.println("  - " + error);
            }
            System.exit(1);
        }
        
        System.out.println("Fonctions chargées avec succès: " + functionLoader.getFunctions().size());
        
        // Analyser chaque fonction individuellement
        for (JnaneFunctionLoader.FunctionInfo functionInfo : functionLoader.getFunctions().values()) {
            System.out.println("\nAnalyse de la fonction: " + functionInfo.getName());
            processFile(functionInfo.getFilePath());
        }
    }
    
    /**
     * Traite un fichier Jnane individuel.
     * 
     * @param inputFile Chemin du fichier à traiter
     * @throws IOException En cas d'erreur de lecture
     */
    private static void processFile(String inputFile) throws IOException {
        System.out.println("Analyse du fichier: " + inputFile);
        
        // Vérifier que le fichier a l'extension .jn
        if (!JnaneFileLoader.isValidJnaneFile(inputFile)) {
            System.err.println("Erreur: Le fichier doit avoir l'extension .jn");
            System.exit(1);
        }
        
        // Création du lexer et du parser
        CharStream input = CharStreams.fromFileName(inputFile);
        JnaneLangLexer lexer = new JnaneLangLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JnaneLangParser parser = new JnaneLangParser(tokens);
        
        // Configuration du gestionnaire d'erreurs
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                                    int line, int charPositionInLine, 
                                    String msg, RecognitionException e) {
                System.err.println("Erreur de syntaxe à la ligne " + line + ":" + charPositionInLine + " - " + msg);
            }
        });
        
        // Analyse syntaxique
        ParseTree tree = parser.program();
        
        // Construction de l'AST
        ASTBuilderVisitor astBuilder = new ASTBuilderVisitor();
        ASTNode ast = astBuilder.visit(tree);
        
        // Affichage de l'AST
        System.out.println("Arbre Syntaxique Abstrait (AST):");
        System.out.println(ast);
        
        // Vérification des types et des variables évaluables
        JnaneTypeChecker typeChecker = new JnaneTypeChecker();
        JnaneTypeCheckVisitor typeCheckVisitor = new JnaneTypeCheckVisitor(typeChecker);
        typeCheckVisitor.visit(tree);
        
        // Afficher les erreurs de type s'il y en a
        if (typeChecker.hasErrors()) {
            System.err.println("Erreurs de type détectées:");
            for (String error : typeChecker.getErrors()) {
                System.err.println("  - " + error);
            }
            System.exit(1);
        }
        
        // Utilisation du visiteur simple pour afficher les éléments visités
        System.out.println("\nParcours de l'arbre syntaxique:");
        JnaneLangVisitorImpl visitor = new JnaneLangVisitorImpl();
        visitor.visit(tree);
        
        System.out.println("\nAnalyse syntaxique terminée avec succès.");
    }
    
    /**
     * Classe interne pour la vérification des types.
     */
    private static class JnaneTypeCheckVisitor extends JnaneLangBaseVisitor<Void> {
        private JnaneTypeChecker typeChecker;
        
        public JnaneTypeCheckVisitor(JnaneTypeChecker typeChecker) {
            this.typeChecker = typeChecker;
        }
        
        // Implémentation des méthodes de visite pour la vérification des types
        // Cette implémentation est simplifiée et devrait être complétée
        
        @Override
        public Void visitAnnotationDecl(JnaneLangParser.AnnotationDeclContext ctx) {
            if (ctx.annotationName().getText().equals("field") || 
                ctx.annotationName().getText().equals("view")) {
                // Traitement des annotations de champ et de vue
                if (ctx.annotationValue() != null) {
                    String fieldName = ctx.annotationValue().getText().split(" ")[0];
                    String fieldType = ctx.annotationValue().getText().split(" ")[1];
                    
                    // Ajouter le champ à l'environnement de type
                    typeChecker.addVariable(fieldName, fieldType, Set.of());
                    
                    // Si c'est une vue, ajouter le type de vue
                    if (ctx.annotationName().getText().equals("view")) {
                        typeChecker.addViewType(fieldType);
                    }
                }
            }
            
            return super.visitAnnotationDecl(ctx);
        }
        
        @Override
        public Void visitAssignmentExpr(JnaneLangParser.AssignmentExprContext ctx) {
            String variableName = ctx.ID().getText();
            
            // Vérifier que la variable est évaluable
            // Note: Cette implémentation est simplifiée et devrait être complétée
            // avec une vérification réelle du type attendu
            if (ctx.type() != null) {
                String expectedType = ctx.type().getText();
                typeChecker.isVariableEvaluable(variableName, expectedType);
            }
            
            return super.visitAssignmentExpr(ctx);
        }
    }
}
