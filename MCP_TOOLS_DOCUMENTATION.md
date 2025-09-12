# Documentation des Outils MCP - Bug Tracking Janitor

## Vue d'ensemble

L'application Bug Tracking Janitor expose 5 outils MCP pour la gestion des projets via le protocole MCP (Model Context Protocol). Ces outils permettent d'effectuer des opérations CRUD complètes sur les projets.

## Configuration

- **Serveur MCP** : `backlog-mcp-server`
- **Port** : 3000
- **Protocole** : SSE (Server-Sent Events)
- **Base de données** : MongoDB

## Outils Disponibles

### 1. create-project

**Description** : Crée un nouveau projet

**Paramètres** :

- `projectJson` (String) : JSON contenant les détails du projet

**Format JSON attendu** :

```json
{
  "projectName": "Nom du projet",
  "projectCode": "CODE_PROJET",
  "description": "Description du projet"
}
```

**Exemple d'utilisation** :

```json
{
  "projectName": "Application Mobile E-commerce",
  "projectCode": "MOBILE_ECOM",
  "description": "Application mobile pour la vente en ligne"
}
```

**Réponse** : JSON du projet créé avec l'ID généré

### 2. find-project-by-id

**Description** : Recherche un projet par son ID

**Paramètres** :

- `id` (String) : ID du projet à rechercher

**Réponse** : JSON du projet trouvé ou message d'erreur si non trouvé

### 3. update-project

**Description** : Met à jour un projet existant

**Paramètres** :

- `projectDetails` (String) : JSON contenant les détails à mettre à jour

**Format JSON attendu** :

```json
{
  "id": "ID_DU_PROJET",
  "projectName": "Nouveau nom (optionnel)",
  "projectCode": "NOUVEAU_CODE (optionnel)",
  "description": "Nouvelle description (optionnel)"
}
```

**Note** : Seuls les champs fournis seront mis à jour. L'ID est obligatoire.

### 4. delete-project

**Description** : Supprime un projet par son ID

**Paramètres** :

- `id` (String) : ID du projet à supprimer

**Réponse** : Message de confirmation ou d'erreur

### 5. find-projects

**Description** : Recherche des projets avec pagination, recherche et filtres

**Paramètres** :

- `page` (int) : Numéro de page (défaut: 0)
- `size` (int) : Taille de page (défaut: 10, max: 100)
- `search` (String, optionnel) : Terme de recherche dans le nom, code ou description
- `filter` (String, optionnel) : Filtre au format "clé:valeur"

**Réponse** : JSON avec les résultats paginés :

```json
{
  "content": [...], // Liste des projets
  "totalElements": 50,
  "totalPages": 5,
  "currentPage": 0,
  "size": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

## Gestion des Erreurs

Tous les outils retournent des messages d'erreur au format JSON en cas de problème :

```json
{
  "error": "Description de l'erreur"
}
```

## Validation

- **Nom du projet** : Obligatoire et non vide
- **Code du projet** : Obligatoire et non vide
- **ID** : Obligatoire pour les opérations de mise à jour et suppression

## Exemples d'Utilisation

### Créer un projet

```json
{
  "projectName": "Système de Gestion RH",
  "projectCode": "HR_SYSTEM",
  "description": "Système complet de gestion des ressources humaines"
}
```

### Rechercher des projets

- Page 0, taille 5 : `page=0, size=5`
- Recherche "mobile" : `search=mobile`
- Filtre par statut : `filter=status:active`

## Technologies Utilisées

- **Spring Boot 3.4.10**
- **Spring AI MCP Server**
- **MongoDB** avec Spring Data
- **Gson** pour la sérialisation JSON
- **Java 21**

## Sécurité

L'application utilise Spring Security avec une configuration de base. En production, il est recommandé de configurer une authentification appropriée.

## Démarrage

```bash
gradle bootRun
```

L'application sera accessible sur `http://localhost:3000` et les outils MCP seront automatiquement exposés.
