# Règles de développement du projet

> Ce document s'applique à **tous les développeurs** (Front React Native & Back Spring Boot).
> Toute Pull Request qui ne respecte pas ces règles doit être corrigée avant d'être mergée.

---

## Sommaire

1. [Règles générales](#1-règles-générales)
2. [Frontend — React Native](#2-frontend--react-native)
3. [Backend — Spring Boot](#3-backend--spring-boot)
4. [Tests unitaires](#4-tests-unitaires)
5. [Checklist avant chaque Pull Request](#5-checklist-avant-chaque-pull-request)

---

## 1. Règles générales

| # | Règle | Détail |
|---|-------|--------|
| G1 | **Réutiliser avant de créer** | Avant d'écrire un composant, un service, un hook ou une classe, vérifier qu'il n'existe pas déjà. |
| G2 | **100 lignes maximum par fichier** | Si un fichier dépasse 100 lignes, il doit être découpé (sous-composants, hooks, services, classes dédiées). |
| G3 | **Une responsabilité par fichier** | Un fichier = un rôle clair (principe **S** de SOLID). |
| G4 | **Logique métier hors de l'UI / des controllers** | La logique et les appels réseau/BDD vivent dans des fichiers dédiés. |
| G5 | **Nommage explicite** | Le nom du fichier doit dire ce qu'il fait (`UserReaderService`, `useUserList`, `PrimaryButton`). |
| G6 | **Pas de code mort** | Pas de code commenté, d'imports inutilisés ou de `console.log` / `System.out.println` laissés. |
| G7 | **Tests obligatoires** | Toute nouvelle logique (service, hook, utilitaire) est livrée avec ses tests unitaires. |
| G8 | **Jamais de `.env` ni de secret commité** | Variables d'environnement uniquement ; un `.env.example` sans valeurs réelles. |
| G9 | **Code écrit avec une IA** | Branche dédiée, Pull Request relue par un humain, aucun `Co-Authored-By` dans les commits. Voir [WORKFLOW_GIT.md](WORKFLOW_GIT.md). |

---

## 2. Frontend — React Native

### 2.1 Réutilisation des composants

Avant de créer un composant :

1. Chercher dans `src/components/` (et `src/components/common/`).
2. Si un composant proche existe → **l'étendre via des props**, ne pas le dupliquer.
3. Si le composant est nouveau et réutilisable → le placer dans `src/components/common/`.
4. Si le composant est spécifique à un écran → le placer dans `src/screens/<Ecran>/components/`.

❌ À ne pas faire : créer `BlueButton`, `RedButton`, `BigButton` séparément.
✅ À faire : un seul `Button` avec des props `variant`, `size`.

```tsx
<Button variant="primary" size="lg" onPress={handleSubmit} title="Valider" />
```

### 2.2 Interdiction des services dans les composants

Un composant **affiche** et **réagit aux interactions**. Il ne doit **jamais** :

- appeler directement `fetch` / `axios` ;
- contenir de la logique métier (calculs, transformations, règles) ;
- gérer directement le stockage (`AsyncStorage`, etc.).

Découpage obligatoire :

| Couche | Dossier | Rôle |
|--------|---------|------|
| API | `src/services/api/` | Appels HTTP purs (un fichier par ressource). |
| Services | `src/services/` | Logique métier, transformation des données. |
| Hooks | `src/hooks/` | Liaison entre services et composants (état, chargement, erreurs). |
| Composants | `src/components/` | Affichage uniquement. |
| Écrans | `src/screens/` | Assemblage des composants + hooks. |

❌ Mauvais :

```tsx
export const UserList = () => {
  const [users, setUsers] = useState([]);
  useEffect(() => {
    axios.get('/api/users').then(res => setUsers(res.data));
  }, []);
  return <FlatList data={users} renderItem={...} />;
};
```

✅ Bon :

```ts
// src/services/api/userApi.ts
export const fetchUsers = () => httpClient.get<User[]>('/users');
```

```ts
// src/hooks/useUserList.ts
export const useUserList = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  useEffect(() => {
    fetchUsers().then(res => setUsers(res.data)).finally(() => setLoading(false));
  }, []);
  return { users, loading };
};
```

```tsx
// src/components/UserList.tsx
export const UserList = () => {
  const { users, loading } = useUserList();
  if (loading) return <Loader />;
  return <FlatList data={users} renderItem={({ item }) => <UserCard user={item} />} />;
};
```

### 2.3 Structure des dossiers

```
src/
├── components/
│   └── common/          # Composants réutilisables (Button, Input, Loader...)
├── screens/
│   └── Home/
│       ├── HomeScreen.tsx
│       └── components/  # Composants propres à l'écran
├── hooks/               # Hooks personnalisés
├── services/
│   └── api/             # Appels HTTP
├── types/               # Types / interfaces TypeScript
├── utils/               # Fonctions utilitaires pures
├── constants/           # Couleurs, tailles, routes...
└── navigation/          # Configuration de la navigation
```

### 2.4 Limite de 100 lignes

Si un composant dépasse 100 lignes :

- extraire les **sous-composants** visuels ;
- extraire la logique dans un **hook** ;
- extraire les **styles** dans un fichier `*.styles.ts` ;
- extraire les fonctions utilitaires dans `utils/`.

### 2.5 Affichage des erreurs : page dédiée

Les erreurs **ne s'affichent jamais** dans une popup, une alerte, un toast ou un message noyé dans l'écran de détail.
Toute erreur redirige vers une **page d'erreur dédiée** (`src/screens/Error/ErrorScreen.tsx`).

- Une seule page d'erreur réutilisable, qui reçoit le type d'erreur : `network`, `notFound`, `validation`, `forbidden`, `server`, `business`.
- Elle affiche : un titre clair, le message de l'API, et des actions (`Réessayer`, `Retour`).
- Les services et hooks **remontent** l'erreur ; seule la navigation décide d'ouvrir `ErrorScreen`.
- Interdit : `Alert.alert(...)`, `Modal` d'erreur, texte rouge dans un écran de détail.

```ts
// src/hooks/useApiError.ts
export const useApiError = () => {
  const navigation = useNavigation();
  return (error: unknown) => navigation.navigate('Error', toErrorParams(error));
};
```

### 2.6 Informations validées : non modifiables

Une fois qu'une information est **validée** (opération confirmée, saisie approuvée, bon de commande validé), elle devient **en lecture seule**.

- Les écrans affichent les données validées sans aucun champ éditable ni bouton `Modifier` / `Supprimer`.
- Une donnée validée n'est jamais corrigée en place : on crée une **nouvelle opération** (ex. mouvement inverse, avoir).
- Le statut (`DRAFT` → `VALIDATED`) est piloté par le backend ; le front se contente de masquer l'édition selon ce statut.
- Un composant de formulaire reçoit une prop `readOnly` dérivée du statut, pas une règle codée en dur dans l'écran.

---

## 3. Backend — Spring Boot

Les **mêmes règles** s'appliquent : réutilisation, séparation des responsabilités, 100 lignes maximum.

### 3.1 Séparation par opération (Reader / Creator / Updater / Deleter)

Pour chaque entité, on **ne crée pas un gros `UserService`** qui fait tout.
On sépare chaque type d'opération dans sa propre classe :

| Classe | Responsabilité |
|--------|----------------|
| `UserReaderService` | Lecture (findById, findAll, recherche...) |
| `UserCreatorService` | Création |
| `UserUpdaterService` | Modification |
| `UserDeleterService` | Suppression |

Chaque service dépend d'une **interface** (principe **D** de SOLID) :

```java
public interface UserReader {
    UserResponse findById(Long id);
    List<UserResponse> findAll();
}
```

```java
@Service
@RequiredArgsConstructor
public class UserReaderService implements UserReader {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }
}
```

### 3.2 Controllers fins

Un controller **ne contient aucune logique métier**. Il reçoit, valide, délègue et répond.

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserReader userReader;
    private final UserCreator userCreator;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userReader.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreator.create(request));
    }
}
```

Si le controller dépasse 100 lignes → le découper (ex. `UserQueryController` / `UserCommandController`).

### 3.3 Structure des packages

```
com.<projet>/
├── user/
│   ├── controller/      # UserController
│   ├── service/
│   │   ├── reader/      # UserReader + UserReaderService
│   │   ├── creator/     # UserCreator + UserCreatorService
│   │   ├── updater/     # UserUpdater + UserUpdaterService
│   │   └── deleter/     # UserDeleter + UserDeleterService
│   ├── repository/      # UserRepository
│   ├── entity/          # User
│   ├── dto/             # UserCreateRequest, UserResponse...
│   └── mapper/          # UserMapper
└── common/
    ├── exception/       # Exceptions + GlobalExceptionHandler
    ├── config/          # Configuration (sécurité, CORS...)
    └── util/            # Utilitaires partagés
```

### 3.4 Règles complémentaires

- **Jamais d'entité JPA exposée** dans un controller : toujours passer par des **DTO**.
- **Mapping** entité ↔ DTO dans un `Mapper` dédié (MapStruct recommandé).
- **Injection par constructeur** uniquement (`@RequiredArgsConstructor`), pas de `@Autowired` sur les champs.
- **Validation** des entrées avec `@Valid` et les annotations `jakarta.validation`.
- **Gestion des erreurs** centralisée dans un `@RestControllerAdvice`.
- **Transactions** (`@Transactional`) sur les services d'écriture (Creator / Updater / Deleter).

### 3.5 Rappel des principes SOLID

| Principe | Application dans le projet |
|----------|---------------------------|
| **S** — Single Responsibility | Une classe = une opération (Reader, Creator, Updater, Deleter). |
| **O** — Open/Closed | On étend via de nouvelles implémentations, on ne modifie pas le code stable. |
| **L** — Liskov Substitution | Toute implémentation d'une interface doit pouvoir la remplacer sans casser le code. |
| **I** — Interface Segregation | Des interfaces petites (`UserReader`, `UserCreator`) plutôt qu'une grosse interface. |
| **D** — Dependency Inversion | Les controllers dépendent des **interfaces**, pas des implémentations. |

### 3.6 Données validées : immuables

Le backend **garantit** l'immuabilité, le front ne fait que la refléter.

- Toute entité validable porte un statut (`DRAFT`, `VALIDATED`).
- Un `Updater` ou un `Deleter` appelé sur une entité `VALIDATED` lève une `ImmutableResourceException` (HTTP **409**).
- Les entités purement historiques (ex. `StockMovement`) n'ont **ni Updater ni Deleter** : elles sont créées une fois et jamais modifiées.
- Correction = nouvelle écriture (mouvement inverse, avoir), jamais modification de l'existant.
- Les erreurs sont renvoyées avec un `type` explicite (`NOT_FOUND`, `VALIDATION`, `IMMUTABLE`, `BUSINESS`) pour que le front ouvre la page d'erreur adaptée.

---

## 4. Tests unitaires

### 4.1 Règles communes

- **Toute nouvelle logique est testée** : services, hooks, utilitaires, mappers.
- **Un fichier de test par fichier testé**, avec le même nom suffixé (`UserReaderServiceTest`, `useUserList.test.ts`).
- **Structure AAA** dans chaque test : *Arrange* (préparer), *Act* (exécuter), *Assert* (vérifier).
- **Un test = un comportement**. Nom explicite : `shouldThrowNotFound_whenUserDoesNotExist`.
- **Tester les cas nominaux ET les cas d'erreur.**
- **Les dépendances externes sont mockées** (API, base de données, stockage).
- Les tests respectent aussi la **limite de 100 lignes** : découper par comportement si besoin.
- **Couverture minimale visée : 80 %** sur les services et les hooks.
- **Une PR avec des tests en échec n'est jamais mergée.**

### 4.2 Frontend — Jest + React Native Testing Library

| Élément | Quoi tester |
|---------|-------------|
| `services/` | Transformation des données, règles métier. |
| `hooks/` | États (chargement, succès, erreur) avec l'API mockée. |
| `utils/` | Fonctions pures : entrées → sorties. |
| `components/` | Rendu selon les props et réaction aux interactions (pas de détails d'implémentation). |

Emplacement : à côté du fichier testé dans un dossier `__tests__/`.

```ts
// src/hooks/__tests__/useUserList.test.ts
jest.mock('../../services/api/userApi');

it('returns users after loading', async () => {
  (fetchUsers as jest.Mock).mockResolvedValue({ data: [{ id: 1, name: 'Alice' }] });

  const { result } = renderHook(() => useUserList());

  await waitFor(() => expect(result.current.loading).toBe(false));
  expect(result.current.users).toHaveLength(1);
});
```

Commande :

```bash
npm test
```

### 4.3 Backend — JUnit 5 + Mockito

| Élément | Quoi tester | Outil |
|---------|-------------|-------|
| Services (Reader, Creator...) | Logique métier, exceptions | JUnit 5 + Mockito |
| Mappers | Conversion entité ↔ DTO | JUnit 5 |
| Controllers | Statuts HTTP, validation, format de réponse | `@WebMvcTest` + MockMvc |
| Repositories (requêtes custom) | Requêtes spécifiques | `@DataJpaTest` |

Emplacement : `src/test/java/` avec **le même package** que la classe testée.

```java
@ExtendWith(MockitoExtension.class)
class UserReaderServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserReaderService userReaderService;

    @Test
    void shouldThrowNotFound_whenUserDoesNotExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userReaderService.findById(1L));
    }
}
```

Commande :

```bash
mvn test
```

---

## 5. Checklist avant chaque Pull Request

### Général
- [ ] J'ai vérifié qu'aucun composant / service équivalent n'existait déjà.
- [ ] Aucun fichier ne dépasse **100 lignes**.
- [ ] Chaque fichier a **une seule responsabilité**.
- [ ] Pas de code mort, pas de logs de debug.
- [ ] Aucun `.env`, mot de passe ni clé dans les fichiers modifiés.
- [ ] Commits sans `Co-Authored-By` ni mention d'outil IA ; code IA passé par une branche et une PR relue.

### Frontend
- [ ] Aucun appel API ni logique métier dans un composant.
- [ ] Les appels API sont dans `services/api/`, la logique dans `services/` ou `hooks/`.
- [ ] Les composants réutilisables sont dans `components/common/`.
- [ ] Les erreurs ouvrent la **page d'erreur dédiée** (aucune popup, alerte ni message dans le détail).
- [ ] Aucune donnée validée n'est modifiable ni supprimable dans l'interface.

### Backend
- [ ] Les opérations sont séparées en **Reader / Creator / Updater / Deleter**.
- [ ] Les controllers dépendent d'**interfaces** et ne contiennent pas de logique métier.
- [ ] Aucune entité JPA n'est exposée : uniquement des DTO.
- [ ] Les entités validées sont immuables (409 sur modification/suppression) et les erreurs ont un `type` explicite.

### Tests
- [ ] Les nouvelles logiques ont leurs **tests unitaires** (cas nominal + cas d'erreur).
- [ ] `npm test` et `mvn test` passent sans erreur.
- [ ] La couverture des services / hooks modifiés reste ≥ 80 %.
