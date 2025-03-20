# Compilateur pour le langage de programmation Jnane

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

## Structure du projet

- `src/main/antlr4/` : Contient la grammaire ANTLR4 du langage Jnane
- `src/main/java/` : Code source Java du compilateur
  - `JnaneLang.g4` : Grammaire ANTLR4 du langage
  - `JnaneLangVisitor.java` : Interface du visiteur pour l'AST
  - `JnaneLangVisitorImpl.java` : Implémentation basique du visiteur
  - `ASTBuilderVisitor.java` : Visiteur qui construit un AST structuré
  - `ASTNode.java` : Représentation des nœuds de l'AST
  - `JnaneFileLoader.java` : Utilitaire pour charger les fichiers Jnane selon la structure des namespaces
  - `Main.java` : Point d'entrée du compilateur
- `examples/` : Exemples de code Jnane organisés par namespaces
  - `math/operations/` : Fonctions mathématiques de base
  - `collections/list/` : Opérations sur les listes
  - `state/transformer/` : Transformations d'états

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

Pour analyser un fichier Jnane :

```bash
java -jar target/jnane-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar fichier.jn
```

Pour analyser un répertoire contenant des fichiers Jnane organisés par namespaces :

```bash
java -jar target/jnane-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar --dir repertoire
```

## Fonctionnalités actuelles

- Analyse lexicale et syntaxique complète du langage Jnane
- Construction d'un arbre syntaxique abstrait (AST)
- Support pour l'organisation des fichiers par namespaces
- Affichage formaté de l'AST

## Fonctionnalités à venir

- Vérification sémantique
- Génération de code
- Optimisations
- Interpréteur pour exécution directe

## Licence

Ce projet est sous licence MIT.
