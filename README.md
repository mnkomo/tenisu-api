# Tenisu Backend API

Ce projet est une application backend Spring Boot qui gère les données des joueurs de tennis. Il expose une API RESTful pour récupérer, ajouter et obtenir des statistiques sur les joueurs.

---

## Technologies Utilisées

* **Langage :** Java 21
* **Gestionnaire de dépendances :** Apache Maven 3.9.10
* **Framework :** Spring Boot
* **Base de données :** MongoDB
* **Conteneurisation :** Docker, Docker Compose

---

## Prérequis

Avant de démarrer l'application, assurez-vous d'avoir installé les éléments suivants :

* Java Development Kit (JDK) 21
* Apache Maven 3.9.10
* Docker Desktop (obligatoire pour Docker Compose)

---

## Démarrage de l'application 🚀

La méthode recommandée pour démarrer l'application est d'utiliser Docker Compose, car cela gère à la fois l'application Spring Boot et la base de données MongoDB dans des conteneurs isolés.

### Démarrage avec Docker Compose (Recommandé)

0.  **Cloner le projet depuis le repository :**

1.  **Naviguez jusqu'à la racine de votre projet :**
    Assurez-vous d'être dans le répertoire où se trouvent votre `Dockerfile` et `docker-compose.yml`.

2.  **Lancer les services :**
    Exécutez la commande suivante. `docker-compose` va automatiquement construire l'image de votre application (si nécessaire) et démarrer tous les services.
    ```bash
    docker-compose up --build
    ```
    * `--build` : force Docker Compose à reconstruire l'image de l'application à partir du `Dockerfile` à chaque exécution, garantissant que les dernières modifications du code sont prises en compte.

    Ceci va :
    * Construire l'image de votre application Spring Boot à partir du `Dockerfile`.
    * Télécharger l'image `mongo:latest` si ce n'est pas déjà fait.
    * Démarrer un conteneur MongoDB accessible par l'application Spring Boot.
    * Démarrer un conteneur pour l'application Spring Boot, exposé sur le port `9090`.

3.  **Accéder à l'application :**
    L'API de votre application sera accessible via `http://localhost:9090`.

---

## API Endpoints

Voici les points d'accès de l'API disponibles :

* **`GET /players`**
    * Description : Récupère tous les joueurs triés par leur classement, du meilleur au moins bon.
    * Réponse (Exemple) : `200 OK`
        ```json
        [
          {
            "id": 17,
            "firstname": "Rafael",
            "lastname": "Nadal",
            "shortname": "R.NAD",
            "sex": "M",
            "country": {
              "picture": "[https://tenisu.latelier.co/resources/Espagne.png](https://tenisu.latelier.co/resources/Espagne.png)",
              "code": "ESP"
            },
            "picture": "[https://tenisu.latelier.co/resources/Nadal.png](https://tenisu.latelier.co/resources/Nadal.png)",
            "data": {
              "rank": 1,
              "points": 1982,
              "weight": 85000,
              "height": 185,
              "age": 33,
              "last": [1, 0, 0, 0, 1]
            }
          },
          {
            "id": 52,
            "firstname": "Novak",
            "lastname": "Djokovic",
            "shortname": "N.DJO",
            "sex": "M",
            "country": {
              "picture": "[https://tenisu.latelier.co/resources/Serbie.png](https://tenisu.latelier.co/resources/Serbie.png)",
              "code": "SRB"
            },
            "picture": "[https://tenisu.latelier.co/resources/Djokovic.png](https://tenisu.latelier.co/resources/Djokovic.png)",
            "data": {
              "rank": 2,
              "points": 2542,
              "weight": 80000,
              "height": 188,
              "age": 31,
              "last": [1, 1, 1, 1, 1]
            }
          }
          // ... autres joueurs
        ]
        ```

* **`GET /players/{id}`**
    * Description : Récupère un joueur par son identifiant unique.
    * Paramètres :
        * `id` (Path Variable) : L'ID du joueur (ex: `17`).
    * Réponse (Exemple pour `id=17`) : `200 OK`
        ```json
        {
          "id": 17,
          "firstname": "Rafael",
          "lastname": "Nadal",
          "shortname": "R.NAD",
          "sex": "M",
          "country": {
            "picture": "[https://tenisu.latelier.co/resources/Espagne.png](https://tenisu.latelier.co/resources/Espagne.png)",
            "code": "ESP"
          },
          "picture": "[https://tenisu.latelier.co/resources/Nadal.png](https://tenisu.latelier.co/resources/Nadal.png)",
          "data": {
            "rank": 1,
            "points": 1982,
            "weight": 85000,
            "height": 185,
            "age": 33,
            "last": [1, 0, 0, 0, 1]
          }
        }
        ```
        * Réponse d'erreur (Exemple pour joueur non trouvé) : `404 Not Found`
            ```json
            {
              "message": "Player with ID 999 not found",
              "statusCode": 404
            }
            ```

* **`GET /players/statistics`**
    * Description : Récupère des statistiques globales sur les joueurs, y compris le pays avec le ratio de victoires le plus élevé, l'IMC moyen et la taille médiane.
    * Réponse (Exemple) : `200 OK`
        ```json
        {
          "country": {
            "picture": "[https://tenisu.latelier.co/resources/Serbie.png](https://tenisu.latelier.co/resources/Serbie.png)",
            "code": "SRB"
          },
          "averageIMC": 22.37,
          "medianHeight": 170.0
        }
        ```

* **`POST /players`**
    * Description : Ajoute un nouveau joueur à la base de données.
    * Corps de la requête (Exemple `CreatePlayerDto`) :
        ```json
        {
          "firstname": "New",
          "lastname": "Player",
          "shortname": "N.PLR",
          "sex": "M",
          "country": {
            "picture": "[https://tenisu.latelier.co/resources/France.png](https://tenisu.latelier.co/resources/France.png)",
            "code": "FRA"
          },
          "picture": "[https://tenisu.latelier.co/resources/default.png](https://tenisu.latelier.co/resources/default.png)",
          "data": {
            "rank": 100,
            "points": 500,
            "weight": 70000,
            "height": 180,
            "age": 25,
            "last": [1, 0, 1, 0, 1]
          }
        }
        ```
    * Réponse (Exemple) : `201 Created`
        ```json
        {
          "id": "65b7d1e8c0b9f2a3e4d5c6b7", # ID généré par MongoDB
          "firstname": "New",
          "lastname": "Player",
          "shortname": "N.PLR",
          "sex": "M",
          "country": {
            "picture": "[https://tenisu.latelier.co/resources/France.png](https://tenisu.latelier.co/resources/France.png)",
            "code": "FRA"
          },
          "picture": "[https://tenisu.latelier.co/resources/default.png](https://tenisu.latelier.co/resources/default.png)",
          "data": {
            "rank": 100,
            "points": 500,
            "weight": 70000,
            "height": 180,
            "age": 25,
            "last": [1, 0, 1, 0, 1]
          }
        }
        ```
        * Réponse d'erreur (Exemple si le joueur existe déjà) : `409 Conflict`
            ```json
            {
              "message": "Player with the same firstname and lastname already exists",
              "statusCode": 409
            }
            ```

---

## Gestion des Erreurs

L'application utilise un gestionnaire d'erreurs global (`GlobalExceptionHandler`) pour fournir des réponses cohérentes en cas de problèmes :

* **`404 Not Found` (PlayerNotFoundException)** : Si un joueur n'est pas trouvé.
* **`204 No Content` (NoContentException)** : Si une ressource est demandée mais qu'aucun contenu n'est disponible (ex: aucune joueur trouvé).
* **`409 Conflict` (ExistingPlayerException)** : Si vous tentez de créer une ressource qui existe déjà.
* **`400 Bad Request` (IllegalArgumentException)** : Pour des arguments invalides dans la requête.
* **`500 Internal Server Error` (Exception générique)** : Pour toute autre erreur inattendue du serveur.

Chaque réponse d'erreur inclura un message et un code de statut HTTP, par exemple :
```json
{
  "message": "An unexpected error occurred: Something went wrong on the server",
  "statusCode": 500
}