# Modifications de la syntaxe des annotations

## Résumé des changements

La syntaxe des annotations a été modifiée pour prendre en charge un nouveau format plus expressif et flexible. Le nouveau format permet de déclarer des annotations multiples, de spécifier clairement le nom et le type, et d'ajouter une documentation optionnelle.

## Nouveau format d'annotations

Le nouveau format d'annotations suit cette structure :
```
(<annotation>)* <name> : <type> (@doc("<doc>"))?
```

Où :
- `(<annotation>)*` représente zéro ou plusieurs annotations (comme `@name`, `@field`, `@view`, `@arg`)
- `<name>` est l'identifiant
- `<type>` est le type associé
- `(@doc("<doc>"))?` est une documentation optionnelle

### Cas spécial pour l'annotation @name

L'annotation `@name` suit un format légèrement différent pour prendre en compte les namespaces :
```
@name <namespace>:<function> (@doc("<doc>"))?
```

Par exemple :
```
@name com.soprahr.foryou.appl.telework.workflow.teleworkmodality:process @doc("Create a workflow.approval:process and apply csp")
```

### Annotations multiples

Le nouveau format permet de combiner plusieurs annotations pour un même élément :
```
@arg @view confidentiality : com.soprahr.foryou.appl.telework.workflow.teleworkmodality:confidentiality
```

### Documentation optionnelle

La documentation peut être ajoutée à n'importe quelle annotation avec la syntaxe `@doc("...")` :
```
@field label : workflow:labelAndTranslation @doc("Mask the label from com.soprahr.foryou.appl.telework.workflow.teleworkmodality:process")
```

## Modifications apportées à la grammaire

Les modifications suivantes ont été apportées à la grammaire ANTLR :

1. Ajout d'une règle `annotationSequence` pour gérer les annotations multiples :
```antlr
annotationSequence
    : (AT annotationName (LBRACK annotationParams RBRACK)?)+
    ;
```

2. Ajout d'une règle `docAnnotation` pour gérer la documentation :
```antlr
docAnnotation
    : AT 'doc' LPAREN STRING RPAREN
    ;
```

3. Modification de la règle `annotationDecl` pour prendre en charge le nouveau format :
```antlr
annotationDecl
    : AT annotationName (LBRACK annotationParams RBRACK)? annotationValue?  // Format actuel
    | annotationSequence ID COLON typeExpr (docAnnotation)?                 // Nouveau format pour @field et @view
    | AT 'name' namespaceId COLON ID (docAnnotation)?                       // Nouveau format pour @name
    ;
```

## Exemples

### Exemple 1 : Annotation @name avec documentation
```
@name com.soprahr.foryou.appl.telework.workflow.teleworkmodality:process @doc("Create a workflow.approval:process and apply csp")
```

### Exemple 2 : Annotations multiples
```
@arg @view confidentiality : com.soprahr.foryou.appl.telework.workflow.teleworkmodality:confidentiality
```

### Exemple 3 : Annotation @field simple
```
@field CORE : workflow:core
```

### Exemple 4 : Annotation @field avec documentation
```
@field label : workflow:labelAndTranslation @doc("Mask the label from com.soprahr.foryou.appl.telework.workflow.teleworkmodality:process")
```

### Exemple 5 : Annotation @view
```
@view process : workflow.approval:process
```

## Impact sur le code existant

Ces modifications sont rétrocompatibles avec le format d'annotations existant. Le parser peut toujours traiter les annotations dans l'ancien format tout en prenant en charge le nouveau format plus expressif.
