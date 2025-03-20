// Grammaire ANTLR v4 pour le langage de programmation Jnane

grammar JnaneLang;

// Point d'entrée de la grammaire
program
    : declaration* EOF
    ;

// Déclarations principales
declaration
    : annotationDecl
    | functionDecl
    | typeDecl
    | namespaceDecl
    | importDecl
    | exportDecl
    | statement
    ;

// ==================== ANNOTATIONS ====================

annotationDecl
    : AT annotationName (LBRACK annotationParams RBRACK)? annotationValue?
    ;

annotationName
    : 'name'      // Nom de fonction
    | 'arg'       // Argument de fonction
    | 'field'     // Champ de retour ou de type
    | 'view'      // Vue sur un état
    | 'type'      // Définition de type
    | 'namespace' // Définition de namespace
    | 'import'    // Importation de fonction ou type
    | 'export'    // Exportation de fonction ou type
    | 'description' // Description d'un élément
    | 'deprecated'  // Élément déprécié
    | 'example'     // Exemple d'utilisation
    | 'version'     // Version de l'élément
    | 'author'      // Auteur de l'élément
    | 'since'       // Version d'introduction
    | 'see'         // Référence à un autre élément
    | 'constraint'  // Contrainte sur un élément
    | 'memo'        // Mémoïsation
    | 'parallel'    // Exécution parallèle
    | 'private'     // Visibilité privée
    | 'protected'   // Visibilité protégée
    | 'public'      // Visibilité publique
    | 'generate_constructor' // Génération de constructeur
    | 'generate_accessors'   // Génération d'accesseurs
    | 'generate_validators'  // Génération de validateurs
    | 'generate_serializers' // Génération de sérialiseurs
    | 'inheritable'          // Annotation héritable
    | 'define_annotation'    // Définition d'annotation personnalisée
    | ID                     // Annotation personnalisée
    ;

annotationParams
    : annotationParam (COMMA annotationParam)*
    ;

annotationParam
    : ID EQUALS annotationParamValue
    ;

annotationParamValue
    : literal
    | ID
    | functionReference
    ;

annotationValue
    : ID                  // Identifiant simple (ex: @name MaFonction)
    | type                // Type (ex: @arg nom Chaine)
    | STRING              // Chaîne de caractères (ex: @description "Description")
    | multilineString     // Chaîne multiligne (ex: @example ```code```)
    | ID type             // Identifiant et type (ex: @arg nom Chaine)
    | ID type whereClause // Identifiant, type et clause where (ex: @arg age Entier where age >= 0)
    ;

whereClause
    : 'where' expression
    ;

multilineString
    : TRIPLE_QUOTE .*? TRIPLE_QUOTE
    ;

functionReference
    : namespaceId COLON ID
    ;

// ==================== FONCTIONS ====================

functionDecl
    : functionHeader functionBody
    ;

functionHeader
    : annotationDecl+
    ;

functionBody
    : blockStmt
    ;

// ==================== TYPES ====================

typeDecl
    : AT 'type' ID (COLON typeInheritance)? typeBody
    ;

typeInheritance
    : ID                      // Héritage simple
    | ID (COMMA ID)*          // Héritage multiple
    ;

typeBody
    : LBRACE typeElement* RBRACE
    ;

typeElement
    : typeField               // Champ de type
    | typeView                // Vue de type
    | typeConstraint          // Contrainte de type
    | typeMethod              // Méthode de type
    | annotationDecl          // Annotation sur le type
    ;

typeField
    : fieldAnnotations ID type (EQUALS defaultValue)?
    ;

fieldAnnotations
    : annotationDecl+
    ;

typeView
    : AT 'view' ID type typeViewBody?
    ;

typeViewBody
    : LBRACE typeViewElement* RBRACE
    ;

typeViewElement
    : typeField
    | annotationDecl
    ;

typeConstraint
    : AT 'constraint' expression
    ;

typeMethod
    : AT 'method' ID methodSignature methodBody
    ;

methodSignature
    : LPAREN methodParam? (COMMA methodParam)* RPAREN (ARROW_RIGHT type)?
    ;

methodParam
    : ID COLON type
    ;

methodBody
    : LBRACE statement* RBRACE
    ;

defaultValue
    : literal
    | objectLiteral
    | arrayLiteral
    | functionCall
    ;

// Types
type
    : simpleType
    | genericType
    | unionType
    | functionType
    | whereType
    | tupleType
    | recordType
    | optionalType
    ;

simpleType
    : ID                      // Type simple (ex: Entier, Chaine)
    | namespaceId COLON ID    // Type qualifié par namespace (ex: collections:Liste)
    ;

genericType
    : ID LESS_THAN typeList GREATER_THAN
    ;

unionType
    : type PIPE type (PIPE type)*
    ;

functionType
    : 'Fonction' LESS_THAN typeList COMMA type GREATER_THAN
    ;

whereType
    : type 'where' expression
    ;

tupleType
    : 'Tuple' LESS_THAN typeList GREATER_THAN
    ;

recordType
    : LBRACE recordTypeField (COMMA recordTypeField)* RBRACE
    ;

recordTypeField
    : ID COLON type
    ;

optionalType
    : 'Option' LESS_THAN type GREATER_THAN
    | type QUESTION_MARK
    ;

typeList
    : type (COMMA type)*
    ;

// Type alias
typeAlias
    : AT 'alias' ID EQUALS type
    ;

// Enum type
enumType
    : AT 'enum' ID LBRACE enumValue (COMMA enumValue)* RBRACE
    ;

enumValue
    : ID (LPAREN type RPAREN)?
    ;

// ==================== NAMESPACES ====================

namespaceDecl
    : AT 'namespace' namespaceId namespaceBody?
    ;

namespaceId
    : ID (DOT ID)*
    ;

namespaceBody
    : LBRACE namespaceElement* RBRACE
    ;

namespaceElement
    : annotationDecl
    | typeDecl
    | functionDecl
    | namespaceDecl
    | importDecl
    | exportDecl
    ;

// Imports et exports
importDecl
    : AT 'import' importPath (AS ID)?
    ;

importPath
    : namespaceId COLON (ID | STAR)
    ;

exportDecl
    : AT 'export' exportElement
    | AT 'export' importPath (AS ID)?
    ;

exportElement
    : ID
    | namespaceId COLON ID
    ;

// Alias de namespace
namespaceAlias
    : AT 'alias' ID namespaceId
    ;

// ==================== VUES ET LENTILLES ====================

viewDecl
    : AT 'view' ID type viewBody?
    ;

viewBody
    : LBRACE viewElement* RBRACE
    ;

viewElement
    : typeField
    | annotationDecl
    ;

// Opérations sur les vues
viewOperation
    : 'ns:vue' LPAREN expression COMMA STRING RPAREN
    | 'ns:vue_temporelle' LPAREN expression COMMA expression RPAREN
    | 'ns:vue_transformee' LPAREN expression COMMA STRING COMMA objectLiteral RPAREN
    | 'ns:composer_vues' LPAREN expression (COMMA expression)+ RPAREN
    | 'ns:convertir_vue' LPAREN expression COMMA STRING RPAREN
    | 'ns:fusionner_vues' LPAREN arrayLiteral RPAREN
    | 'ns:projeter_vue' LPAREN expression COMMA arrayLiteral RPAREN
    | 'ns:filtrer_vue' LPAREN expression COMMA lambdaExpr RPAREN
    | 'ns:historique' LPAREN expression RPAREN
    | 'ns:etat_historique' LPAREN expression COMMA expression RPAREN
    | 'ns:vue_historique' LPAREN expression COMMA expression COMMA STRING RPAREN
    | 'ns:reconstruire_etat' LPAREN expression COMMA expression RPAREN
    | 'ns:reconstruire_vue' LPAREN expression COMMA expression COMMA STRING RPAREN
    | 'ns:journal_transformations' LPAREN expression RPAREN
    | 'ns:filtrer_journal' LPAREN expression COMMA expression RPAREN
    ;

// Définition de lentille
lensDefinition
    : 'ns:lentille_champ' LPAREN ID COMMA STRING RPAREN
    | 'ns:lentille_chemin' LPAREN ID COMMA STRING RPAREN
    | 'ns:lentille_collection' LPAREN ID COMMA expression RPAREN
    | 'ns:lentille_predicat' LPAREN ID COMMA lambdaExpr RPAREN
    | 'ns:lentille_optionnelle' LPAREN ID COMMA STRING RPAREN
    | 'ns:creer_lentille' LPAREN lambdaExpr COMMA lambdaExpr RPAREN
    ;

// Opérations sur les lentilles
lensOperation
    : 'ns:voir' LPAREN lensExpr COMMA expression RPAREN
    | 'ns:modifier' LPAREN lensExpr COMMA expression COMMA expression RPAREN
    | 'ns:transformer' LPAREN lensExpr COMMA expression COMMA expression RPAREN
    | 'ns:composer_lentilles' LPAREN arrayLiteral RPAREN
    | 'ns:composer_lentilles_paralleles' LPAREN objectLiteral RPAREN
    | 'ns:lentille_conditionnelle' LPAREN lambdaExpr COMMA lensExpr COMMA lensExpr RPAREN
    | 'ns:modifier_via_lentille' LPAREN expression COMMA lensExpr COMMA expression RPAREN
    | 'ns:annuler_modification' LPAREN expression RPAREN
    | 'ns:valider_si' LPAREN expression COMMA lambdaExpr RPAREN
    | 'ns:transformer_etat' LPAREN expression COMMA lensExpr COMMA expression RPAREN
    ;

lensExpr
    : ID
    | lensDefinition
    ;

// Syntaxe fluide et opérateurs pour lentilles
fluidLensOperation
    : expression PIPE_FORWARD 'ns:modifier' LPAREN lensExpr COMMA expression RPAREN
    | expression PIPE_FORWARD 'ns:transformer' LPAREN lensExpr COMMA lambdaExpr RPAREN
    ;

lensOperatorAccess
    : expression AT_GREATER lensExpr
    ;

lensOperatorModify
    : expression AT_GREATER lensExpr COLON_EQUALS expression
    ;

lensOperatorTransform
    : expression AT_GREATER lensExpr COLON_TILDE expression
    ;

// ==================== EXPRESSIONS ET STATEMENTS ====================

// Expressions
expression
    : assignmentExpr
    | conditionalExpr
    | logicalOrExpr
    | pipeExpr
    | viewOperation
    | lensOperation
    | fluidLensOperation
    | lensOperatorAccess
    | lensOperatorModify
    | lensOperatorTransform
    ;

// Assignation
assignmentExpr
    : ID (COLON type)? EQUALS expression
    ;

// Expression conditionnelle (ternaire)
conditionalExpr
    : logicalOrExpr (QUESTION logicalOrExpr COLON logicalOrExpr)?
    ;

// Opérateurs logiques
logicalOrExpr
    : logicalAndExpr (OR logicalAndExpr)*
    ;

logicalAndExpr
    : equalityExpr (AND equalityExpr)*
    ;

equalityExpr
    : relationalExpr ((EQUALS_EQUALS | NOT_EQUALS) relationalExpr)*
    ;

relationalExpr
    : additiveExpr ((LESS_THAN | GREATER_THAN | LESS_EQUALS | GREATER_EQUALS) additiveExpr)*
    ;

// Opérateurs arithmétiques
additiveExpr
    : multiplicativeExpr ((PLUS | MINUS) multiplicativeExpr)*
    ;

multiplicativeExpr
    : unaryExpr ((MULTIPLY | DIVIDE | MODULO) unaryExpr)*
    ;

// Expressions unaires
unaryExpr
    : (NOT | MINUS) unaryExpr
    | primaryExpr
    ;

// Expression de pipeline
pipeExpr
    : primaryExpr (PIPE_FORWARD primaryExpr)*
    ;

// Expressions primaires
primaryExpr
    : literal
    | ID
    | functionCall
    | objectAccess
    | objectLiteral
    | arrayLiteral
    | LPAREN expression RPAREN
    ;

// Appel de fonction
functionCall
    : namespaceId COLON ID LPAREN argumentList? RPAREN
    | ID LPAREN argumentList? RPAREN
    ;

argumentList
    : expression (COMMA expression)*
    ;

// Accès aux champs d'un objet
objectAccess
    : primaryExpr DOT ID
    | primaryExpr LBRACK expression RBRACK
    ;

// Littéral d'objet
objectLiteral
    : LBRACE objectField (COMMA objectField)* RBRACE
    | LBRACE RBRACE
    ;

objectField
    : ID COLON expression
    | ELLIPSIS expression
    ;

// Littéral de tableau
arrayLiteral
    : LBRACK expression (COMMA expression)* RBRACK
    | LBRACK RBRACK
    ;

// Expression lambda
lambdaExpr
    : ID ARROW_RIGHT expression
    | LPAREN paramList? RPAREN ARROW_RIGHT expression
    | LPAREN paramList? RPAREN ARROW_RIGHT blockStmt
    ;

paramList
    : ID (COMMA ID)*
    ;

// Instructions
statement
    : expressionStmt
    | returnStmt
    | ifStmt
    | matchStmt
    | blockStmt
    ;

expressionStmt
    : expression SEMICOLON?
    ;

returnStmt
    : 'return' expression SEMICOLON?
    ;

ifStmt
    : 'if' expression blockStmt ('else' (ifStmt | blockStmt))?
    ;

matchStmt
    : 'match' expression LBRACE matchCase+ RBRACE
    ;

matchCase
    : pattern ARROW_RIGHT statement
    ;

pattern
    : ID ID? (ARROW_RIGHT ID)?
    | literal
    | UNDERSCORE
    ;

blockStmt
    : LBRACE statement* RBRACE
    ;

// Littéraux
literal
    : INTEGER
    | DECIMAL
    | STRING
    | BOOLEAN
    | NULL
    ;

// ==================== TOKENS LEXICAUX ====================

// Symboles
AT : '@';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
LPAREN : '(';
RPAREN : ')';
SEMICOLON : ';';
COLON : ':';
COMMA : ',';
DOT : '.';
EQUALS : '=';
EQUALS_EQUALS : '==';
NOT_EQUALS : '!=';
LESS_THAN : '<';
GREATER_THAN : '>';
LESS_EQUALS : '<=';
GREATER_EQUALS : '>=';
PLUS : '+';
MINUS : '-';
MULTIPLY : '*';
DIVIDE : '/';
MODULO : '%';
AND : '&&';
OR : '||';
NOT : '!';
QUESTION : '?';
QUESTION_MARK : '?';
PIPE : '|';
PIPE_FORWARD : '|>';
ARROW_RIGHT : '=>';
AT_GREATER : '@>';
COLON_EQUALS : ':=';
COLON_TILDE : ':~';
UNDERSCORE : '_';
ELLIPSIS : '...';
AS : 'as';
STAR : '*';
TRIPLE_QUOTE : '```';

// Littéraux
INTEGER : [0-9]+;
DECIMAL : [0-9]+ '.' [0-9]+;
STRING : '"' (~["\r\n] | '\\"')* '"';
BOOLEAN : 'Vrai' | 'Faux';
NULL : 'Vide';

// Identifiants
ID : [a-zA-Z_][a-zA-Z0-9_]*;

// Commentaires et espaces
COMMENT : '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT : '/*' .*? '*/' -> skip;
WS : [ \t\r\n]+ -> skip;
