# voice-service

Transforme un message vocal en texte avec [faster-whisper](https://github.com/SYSTRAN/faster-whisper), en local et hors ligne. Port **8087**, accessible via la gateway sur `/api/voice/**` (tous les rôles métier : `STOCK`, `VENTE`, `RH`, `CRM`, `ADMIN`, comme l'assistant IA).

C'est la première brique de l'assistant vocal (tâche AI-16) : le texte obtenu sera ensuite envoyé à l'assistant IA.

## Endpoint

`POST /api/voice/transcribe` en `multipart/form-data` :

| Champ | Obligatoire | Description |
|-------|:-----------:|-------------|
| `audio` | oui | Fichier audio : wav, mp3, webm, ogg, m4a… (10 Mo maximum) |
| `language` | non | Code de langue (`fr` par défaut, `mg` pour le malgache, `en`…) |

Réponse `200` :

```json
{ "text": "Quel est notre chiffre d'affaires ?", "language": "fr", "durationSeconds": 2.1 }
```

Erreurs, au même format que les services Spring (`status`, `type`, `message`, `timestamp`) :

| Statut | Cas |
|--------|-----|
| 400 | Fichier vide |
| 413 | Fichier trop gros |
| 415 | Ce n'est pas un fichier audio |
| 422 | Audio illisible |

Exemple :

```powershell
curl.exe -X POST http://localhost:8080/api/voice/transcribe -H "Authorization: Bearer <token>" -F "audio=@note.wav"
```

## Transcription en temps réel

`WS /stream?language=fr` : le client envoie le son du micro en continu, en messages binaires (PCM 16 bits, mono, 16 kHz). Le service renvoie du JSON :

```json
{ "type": "partial", "text": "Bonjour quel est" }
{ "type": "final", "text": "Bonjour, quel est notre chiffre d'affaires ?" }
```

- `partial` : texte provisoire, envoyé environ toutes les 0,8 seconde pendant que la personne parle.
- `final` : texte définitif, envoyé après 0,7 seconde de silence (ou 15 secondes de parole continue). C'est lui qu'on enverra à l'assistant IA.
- Le message texte `stop` termine la phrase en cours tout de suite.

**Page de démo** : lancer le service, puis ouvrir http://localhost:8087/demo (Chrome, Edge ou Firefox), cliquer sur « Parler » et autoriser le micro. Le navigateur n'accepte le micro qu'en `localhost` ou en HTTPS. La page permet de choisir le micro (utile quand un micro virtuel est pris par défaut), affiche le niveau du micro en direct et un journal de debug, aussi envoyé dans les logs du service.

Logs : chaque connexion et chaque phrase sont tracées (`voice.stream`) avec la durée de l'audio, le niveau, le temps de calcul et le texte.

Via la gateway, un navigateur ne peut pas envoyer l'en-tête `Authorization` sur un WebSocket : la page de démo se connecte donc directement au service. Le front web (NovaUI), lui, peut passer par `ws://<gateway>/api/voice/stream` avec son token dans les en-têtes.

## Lancer

Python 3.11 ou plus récent :

```powershell
cd services/voice-service
py -m venv .venv
.venv\Scripts\pip install -r requirements.txt
.venv\Scripts\uvicorn app.main:app --port 8087
```

Le modèle Whisper (environ 500 Mo pour `small`) est téléchargé automatiquement à la première transcription, puis gardé en cache. Pour le temps réel sur un processeur seul, `base` (145 Mo) répond plus vite, au prix d'un peu de précision.

Si le téléchargement automatique bloque, télécharger les fichiers du modèle (`config.json`, `model.bin`, `tokenizer.json`, `vocabulary.txt`) depuis la page Hugging Face `Systran/faster-whisper-base` dans un dossier, puis donner ce dossier à `VOICE_MODEL_SIZE` :

```powershell
$env:VOICE_MODEL_SIZE = "$HOME\.cache\whisper-models\faster-whisper-base"
```

## Configuration

| Variable | Défaut | Rôle |
|----------|--------|------|
| `VOICE_MODEL_SIZE` | `small` | `tiny`, `base`, `small`, `medium`, `large-v3`, ou le chemin d'un dossier de modèle |
| `VOICE_PARTIAL_MODEL_SIZE` | vide | Modèle plus petit et plus rapide pour le texte provisoire du temps réel (ex. `tiny`) ; vide = même modèle que le texte définitif |
| `VOICE_DEVICE` | `cpu` | `cuda` si une carte NVIDIA est disponible |
| `VOICE_COMPUTE_TYPE` | `int8` | `float16` sur carte graphique |
| `VOICE_LANGUAGE` | `fr` | Langue par défaut |
| `VOICE_INITIAL_PROMPT` | phrases et vocabulaire de l'ERP | Contexte donné à Whisper pour la langue par défaut (oriente vers « chiffre d'affaires », « stock »…) ; vide pour le désactiver |
| `VOICE_DEBUG_DIR` | vide | Si renseigné (ex. `debug`), chaque phrase du temps réel y est enregistrée en `.wav` avec son texte dans `transcriptions.jsonl`. Dossier ignoré par git, à n'activer qu'en local |
| `VOICE_MAX_AUDIO_BYTES` | `10485760` | Taille maximale de l'audio |

## Tests

```powershell
.venv\Scripts\pip install -r requirements-dev.txt
.venv\Scripts\python -m pytest
```

Les tests remplacent Whisper par un faux modèle : ils ne téléchargent rien.
