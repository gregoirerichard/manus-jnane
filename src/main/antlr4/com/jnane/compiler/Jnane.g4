// Grammaire ANTLR v4 complète pour le langage de programmation de réseaux causaux

grammar Jnane;

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
    | lensOperation
    | functionCall
    ;

// ==================== EXPRESSIONS ====================

expression
    : literal                                                 # LiteralExpr
    | ID                                                      # IdentifierExpr
    | THIS                                                    # ThisExpr
    | SUPER                                                   # SuperExpr
    | LPAREN expression RPAREN                                # ParenExpr
    | expression DOT ID                                       # MemberAccessExpr
    | expression LBRACK expression RBRACK                     # IndexAccessExpr
    | functionCall                                            # FunctionCallExpr
    | viewOperation                                           # ViewOperationExpr
    | lensOperation                                           # LensOperationExpr
    | objectLiteral                                           # ObjectLiteralExpr
    | arrayLiteral                                            # ArrayLiteralExpr
    | lambdaExpr                                              # LambdaExprExpr
    | expression op=(PLUS | MINUS | STAR | SLASH | PERCENT) expression # BinaryExpr
    | expression op=(EQUALS_EQUALS | NOT_EQUALS | LESS_THAN | LESS_THAN_EQUALS | GREATER_THAN | GREATER_THAN_EQUALS) expression # ComparisonExpr
    | expression op=(AND | OR) expression                     # LogicalExpr
    | op=(NOT | MINUS) expression                             # UnaryExpr
    | expression QUESTION_MARK expression COLON expression    # TernaryExpr
    | expression AS type                                      # CastExpr
    | expression IS type                                      # TypeCheckExpr
    | expression PIPE_GREATER expression                      # PipeExpr
    | MATCH expression LBRACE matchCase+ RBRACE               # MatchExpr
    | IF expression THEN expression ELSE expression           # InlineIfExpr
    | LET ID EQUALS expression IN expression                  # LetExpr
    | DO LBRACE statement+ RBRACE                             # DoExpr
    | TRY expression CATCH LPAREN ID RPAREN expression        # TryExpr
    | THROW expression                                        # ThrowExpr
    | AWAIT expression                                        # AwaitExpr
    | ASYNC expression                                        # AsyncExpr
    ;

// Appel de fonction
functionCall
    : namespaceId COLON ID LPAREN (expression (COMMA expression)*)? RPAREN
    | ID LPAREN (expression (COMMA expression)*)? RPAREN
    | expression DOT ID LPAREN (expression (COMMA expression)*)? RPAREN
    ;

// Littéraux
literal
    : INT                                                     # IntLiteral
    | FLOAT                                                   # FloatLiteral
    | STRING                                                  # StringLiteral
    | multilineString                                         # MultilineStringLiteral
    | BOOLEAN                                                 # BooleanLiteral
    | NULL                                                    # NullLiteral
    | UNDEFINED                                               # UndefinedLiteral
    ;

// Littéral objet
objectLiteral
    : LBRACE (objectProperty (COMMA objectProperty)*)? RBRACE
    ;

objectProperty
    : ID COLON expression
    | STRING COLON expression
    | LBRACK expression RBRACK COLON expression
    | SPREAD expression
    ;

// Littéral tableau
arrayLiteral
    : LBRACK (expression (COMMA expression)*)? RBRACK
    ;

// Expression lambda
lambdaExpr
    : LPAREN (ID (COMMA ID)*)? RPAREN ARROW_RIGHT expression
    | LPAREN (ID COLON type (COMMA ID COLON type)*)? RPAREN ARROW_RIGHT (type)? LBRACE statement* RBRACE
    | ID ARROW_RIGHT expression
    ;

// Cas de pattern matching
matchCase
    : pattern ARROW_RIGHT expression
    ;

pattern
    : literal                                                 # LiteralPattern
    | ID                                                      # IdentifierPattern
    | UNDERSCORE                                              # WildcardPattern
    | ID AT pattern                                           # BindingPattern
    | namespaceId COLON ID LPAREN pattern (COMMA pattern)* RPAREN # ConstructorPattern
    | LBRACE (ID COLON pattern (COMMA ID COLON pattern)*)? RBRACE # ObjectPattern
    | LBRACK (pattern (COMMA pattern)*)? RBRACK               # ArrayPattern
    | pattern PIPE pattern                                    # OrPattern
    | pattern AND pattern                                     # AndPattern
    | pattern IF expression                                   # GuardPattern
    ;

// ==================== STATEMENTS ====================

statement
    : variableDecl SEMICOLON?                                 # VariableDeclStmt
    | expression SEMICOLON?                                   # ExpressionStmt
    | assignmentStmt SEMICOLON?                               # AssignmentStatement
    | returnStmt SEMICOLON?                                   # ReturnStatement
    | ifStmt                                                  # IfStatement
    | matchStmt                                               # MatchStatement
    | forStmt                                                 # ForStatement
    | whileStmt                                               # WhileStatement
    | doWhileStmt SEMICOLON?                                  # DoWhileStatement
    | blockStmt                                               # BlockStatement
    | breakStmt SEMICOLON?                                    # BreakStatement
    | continueStmt SEMICOLON?                                 # ContinueStatement
    | throwStmt SEMICOLON?                                    # ThrowStatement
    | tryStmt                                                 # TryStatement
    ;

// Déclaration de variable
variableDecl
    : LET ID (COLON type)? (EQUALS expression)?
    | CONST ID (COLON type)? EQUALS expression
    ;

// Affectation
assignmentStmt
    : ID EQUALS expression
    | ID op=(PLUS_EQUALS | MINUS_EQUALS | STAR_EQUALS | SLASH_EQUALS | PERCENT_EQUALS) expression
    | expression DOT ID EQUALS expression
    | expression LBRACK expression RBRACK EQUALS expression
    ;

// Return
returnStmt
    : RETURN expression?
    ;

// If
ifStmt
    : IF LPAREN expression RPAREN statement (ELSE statement)?
    | IF expression statement (ELSE statement)?
    ;

// Match
matchStmt
    : MATCH expression LBRACE matchCase+ RBRACE
    ;

// For
forStmt
    : FOR LPAREN variableDecl SEMICOLON expression SEMICOLON expression RPAREN statement
    | FOR LPAREN LET ID IN expression RPAREN statement
    | FOR LPAREN LET ID OF expression RPAREN statement
    ;

// While
whileStmt
    : WHILE LPAREN expression RPAREN statement
    ;

// Do-While
doWhileStmt
    : DO statement WHILE LPAREN expression RPAREN
    ;

// Block
blockStmt
    : LBRACE statement* RBRACE
    ;

// Break
breakStmt
    : BREAK
    ;

// Continue
continueStmt
    : CONTINUE
    ;

// Throw
throwStmt
    : THROW expression
    ;

// Try-Catch-Finally
tryStmt
    : TRY blockStmt catchClause? finallyClause?
    ;

catchClause
    : CATCH LPAREN ID (COLON type)? RPAREN blockStmt
    ;

finallyClause
    : FINALLY blockStmt
    ;

// ==================== LEXER RULES ====================

// Mots-clés
LET : 'let';
CONST : 'const';
IF : 'if';
ELSE : 'else';
THEN : 'then';
MATCH : 'match';
FOR : 'for';
WHILE : 'while';
DO : 'do';
BREAK : 'break';
CONTINUE : 'continue';
RETURN : 'return';
FUNCTION : 'function';
IN : 'in';
OF : 'of';
AS : 'as';
IS : 'is';
TRY : 'try';
CATCH : 'catch';
FINALLY : 'finally';
THROW : 'throw';
ASYNC : 'async';
AWAIT : 'await';
THIS : 'this';
SUPER : 'super';
NULL : 'null';
UNDEFINED : 'undefined';

// Opérateurs
PLUS : '+';
MINUS : '-';
STAR : '*';
SLASH : '/';
PERCENT : '%';
EQUALS : '=';
PLUS_EQUALS : '+=';
MINUS_EQUALS : '-=';
STAR_EQUALS : '*=';
SLASH_EQUALS : '/=';
PERCENT_EQUALS : '%=';
EQUALS_EQUALS : '==';
NOT_EQUALS : '!=';
LESS_THAN : '<';
LESS_THAN_EQUALS : '<=';
GREATER_THAN : '>';
GREATER_THAN_EQUALS : '>=';
AND : '&&';
OR : '||';
NOT : '!';
QUESTION_MARK : '?';
ARROW_RIGHT : '=>';
PIPE : '|';
PIPE_GREATER : '|>';
SPREAD : '...';

// Délimiteurs
LPAREN : '(';
RPAREN : ')';
LBRACE : '{';
RBRACE : '}';
LBRACK : '[';
RBRACK : ']';
SEMICOLON : ';';
COLON : ':';
COMMA : ',';
DOT : '.';
AT : '@';
UNDERSCORE : '_';

// Littéraux
BOOLEAN : 'true' | 'false';
INT : [0-9]+ | '0x' [0-9a-fA-F]+ | '0b' [01]+ | '0o' [0-7]+;
FLOAT : [0-9]+ '.' [0-9]* | '.' [0-9]+;
STRING : '"' (~["\\\r\n] | '\\' .)* '"' | '\'' (~['\\\r\n] | '\\' .)* '\'';
TRIPLE_QUOTE : '```';

// Identifiants
ID : [a-zA-Z_][a-zA-Z0-9_]*;

// Commentaires et espaces
COMMENT : '//' ~[\r\n]* -> skip;
MULTILINE_COMMENT : '/*' .*? '*/' -> skip;
WS : [ \t\r\n]+ -> skip;
