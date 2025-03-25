# Compilateur pour le langage de programmation Jnane

[![Java CI with Maven](https://github.com/gregoirerichard/manus-jnane/actions/workflows/maven.yml/badge.svg)](https://github.com/gregoirerichard/manus-jnane/actions/workflows/maven.yml)
[![codecov](https://codecov.io/gh/gregoirerichard/manus-jnane/branch/master/graph/badge.svg)](https://codecov.io/gh/gregoirerichard/manus-jnane)

Ce projet est un compilateur pour le langage de programmation Jnane, un langage spécialisé dans la manipulation de réseaux causaux représentant des états.

## Caractéristiques du langage Jnane

- **États typés et immutables** : Représentation formelle des états d'un système avec un typage fort
- **Fonctions pures** : Organisées en namespaces avec une notation `ns:fonction(arg1, arg2, ...)`
- **Vues** : Mécanisme permettant d'observer un état selon différentes perspectives
- **Pattern lentille** : Facilite la modification d'objets imbriqués tout en préservant l'immutabilité
- **Annotations** : Système riche pour enrichir les définitions (`@name`, `@arg`, `@field`, `@view`)
- **Conservation d'historique** : Traçabilité complète des transformations d'états

## Organisation du code source

- Chaque fonction est définie dans un fichier portant son nom avec l'extension `.jn`
- Les namespaces utilisent une notation pointée (ex: `math.operations`)
- Les fonctions dans un namespace sont stockées dans une structure de répertoires correspondant à chaque segment du namespace
  - Exemple: une fonction `math.operations:add` serait définie dans le fichier `math/operations/add.jn`
- L'annotation `@name` dans l'en-tête du fichier doit inclure le namespace complet, par exemple `@name math.operations:add`
- Le bloc `return` est inutile car la structure du résultat est définie par les annotations `@field` et `@view` dans l'en-tête

## Structure du projet

- `src/main/antlr4/` : Contient la grammaire ANTLR4 du langage Jnane
- `src/main/java/` : Code source Java du compilateur
  - `JnaneLang.g4` : Grammaire ANTLR4 du langage
  - `JnaneLangVisitor.java` : Interface du visiteur pour l'AST
  - `JnaneLangVisitorImpl.java` : Implémentation basique du visiteur
  - `ASTBuilderVisitor.java` : Visiteur qui construit un AST structuré
  - `ASTNode.java` : Représentation des nœuds de l'AST
  - `JnaneFileLoader.java` : Utilitaire pour charger les fichiers Jnane selon la structure des namespaces
  - `JnaneTypeChecker.java` : Vérification des types et des variables évaluables
  - `JnaneFunctionLoader.java` : Chargement des fonctions et détection des cycles de dépendances
  - `Main.java` : Point d'entrée du compilateur
- `examples/` : Exemples de code Jnane organisés par namespaces
  - `math/operations/` : Fonctions mathématiques de base
  - `collections/list/` : Opérations sur les listes
  - `state/transformer/` : Transformations d'états
- `.github/workflows/` : Configuration de l'intégration continue avec GitHub Actions

## Prérequis

- Java 11 ou supérieur
- Maven 3.6 ou supérieur

## Compilation

Pour compiler le projet :

```bash
mvn clean package
```

Cela générera un fichier JAR exécutable dans le répertoire `target/`.

## Utilisation

Pour analyser un fichier Jnane individuel :

```bash
java -jar target/jnane-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar fichier.jn
```

Pour analyser un répertoire complet contenant des fichiers Jnane organisés par namespaces :

```bash
java -jar target/jnane-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar --dir repertoire
```

## Exemple de fichier Jnane

```
// Fonction d'addition dans le namespace math.operations

@name math.operations:add
@arg x Entier
@arg y Entier
@field resultat Entier

{
    // Implémentation simple de l'addition
    resultat = x + y;
}
```

## Intégration Continue

Ce projet utilise GitHub Actions pour l'intégration continue. À chaque push ou pull request sur la branche master, le workflow exécute automatiquement :

1. La compilation du projet avec Maven
2. L'exécution des tests unitaires
3. L'analyse des exemples de code Jnane
4. La génération de rapports de couverture de code avec Codecov

Les résultats de ces opérations sont visibles via les badges en haut de ce README et dans l'onglet Actions du dépôt GitHub.

## Vérifications effectuées par le compilateur

1. **Vérification syntaxique** : Analyse de la syntaxe selon la grammaire ANTLR4
2. **Vérification des variables évaluables** : S'assure que toutes les variables sont soit du type spécifié, soit ont une vue de ce type
3. **Détection des cycles de dépendances** : Vérifie qu'il n'y a pas de cycle dans les définitions des fonctions
4. **Vérification des namespaces** : S'assure que les fonctions sont correctement organisées selon leur namespace

## Fonctionnalités actuelles

- Analyse lexicale et syntaxique complète du langage Jnane
- Construction d'un arbre syntaxique abstrait (AST)
- Support pour l'organisation des fichiers par namespaces
- Vérification des variables évaluables
- Détection des cycles de dépendances entre fonctions
- Affichage formaté de l'AST

## Fonctionnalités à venir

- Vérification sémantique avancée
- Génération de code
- Optimisations
- Interpréteur pour exécution directe

## Todo 
- Conserver la signature de la fonction (à partir des annotations) dans un contexte, au lieu d'initier functionParamters avec les valeurs par défaut "first" et "second" 
- Ajouter les autres opérations sur les Entiers.
- Ajouter les opérations sur les Listes.
- Ajouter les opérations sur les Maps.
- Ajouter les opérations sur les autres types de nombres.
- Ajouter les opérations sur les Strings.
- Ajouter une méthode toString sur la stucture d'une opération


## Licence

Ce projet est sous licence MIT.
