# voice-service

Transforme un message vocal en texte avec [faster-whisper](https://github.com/SYSTRAN/faster-whisper), en local et hors ligne. Port **8087**, accessible via la gateway sur `/api/voice/**` (rôles `RH` et `ADMIN`).

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

## Lancer

Python 3.11 ou plus récent :

```powershell
cd services/voice-service
py -m venv .venv
.venv\Scripts\pip install -r requirements.txt
.venv\Scripts\uvicorn app.main:app --port 8087
```

Le modèle Whisper (environ 500 Mo pour `small`) est téléchargé automatiquement à la première transcription, puis gardé en cache. Le modèle `base` est plus rapide mais moins précis.

## Configuration

| Variable | Défaut | Rôle |
|----------|--------|------|
| `VOICE_MODEL_SIZE` | `small` | `tiny`, `base`, `small`, `medium`, `large-v3` |
| `VOICE_DEVICE` | `cpu` | `cuda` si une carte NVIDIA est disponible |
| `VOICE_COMPUTE_TYPE` | `int8` | `float16` sur carte graphique |
| `VOICE_LANGUAGE` | `fr` | Langue par défaut |
| `VOICE_MAX_AUDIO_BYTES` | `10485760` | Taille maximale de l'audio |

## Tests

```powershell
.venv\Scripts\pip install -r requirements-dev.txt
.venv\Scripts\python -m pytest
```

Les tests remplacent Whisper par un faux modèle : ils ne téléchargent rien.
