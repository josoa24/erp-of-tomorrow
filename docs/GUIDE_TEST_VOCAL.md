# Guide de test de la saisie vocale (NovaUI + voice-service)

Ce guide s'adresse au poste qui teste la voix de bout en bout. La partie interface a été vérifiée sans audio réel : les états d'erreur et de permission ont été simulés, la transcription Whisper n'a pas été exécutée.

## 1. Démarrer

Prérequis : Docker, un micro, Chrome, Edge ou Firefox. Le navigateur n'autorise le micro qu'en `localhost` ou en HTTPS.

```bash
docker compose -f docker-compose.dev.yml --profile ai --profile voice up -d
```

Le premier démarrage du service vocal télécharge le modèle Whisper (1 à 2 minutes, `VOICE_MODEL_SIZE=small`). Le port du service n'est pas publié : tout passe par la gateway (`http://localhost:8080`) et le front (`http://localhost:5173`). Pour suivre le chargement :

```bash
docker compose -f docker-compose.dev.yml logs -f voice-service
```

Préchauffer le modèle avant le test (jeton obtenu par `POST /api/auth/login`) :

```bash
curl -X POST http://localhost:8080/api/voice/transcribe -H "Authorization: Bearer <jeton>" -F "audio=@note.wav"
```

## 2. Où se trouve le micro

- Accueil : barre sombre Eclipse, à gauche de `Ctrl K`.
- Autres écrans : barre claire Eclipse en bas de page.
- Raccourci : `Alt M` démarre ou termine l'enregistrement, `Échap` annule.

## 3. Parcours nominal

1. Cliquer sur le micro, autoriser l'accès : le bouton devient rouge, un indicateur de niveau s'anime, le message « Je vous écoute… » apparaît.
2. Dire « Quel est le stock de riz aujourd'hui ? » puis se taire environ 1,4 s : l'enregistrement s'arrête seul, « Transcription en cours… » s'affiche.
3. Avec l'envoi automatique activé (par défaut), la demande part et la réponse d'Eclipse s'affiche. Avec l'envoi automatique désactivé (interrupteur « Envoi automatique », mémorisé dans le navigateur), le texte reste dans la barre pour relecture.

Langues : français par défaut (`language=fr`). L'anglais passe sans changement de configuration côté Whisper ; le malgache demande de changer `VOICE_LANGUAGE` du service.

## 4. États à vérifier

| État | Comment le provoquer | Message attendu |
|------|----------------------|-----------------|
| Demande de permission | Premier clic sur le micro | « Autorisez l'accès au micro dans votre navigateur… » |
| Permission refusée | Bloquer le micro dans les réglages du site | « L'accès au micro est refusé. Autorisez-le… », bouton Réessayer |
| Aucun micro | Débrancher ou désactiver le micro | « Aucun micro détecté… » |
| Micro occupé | Ouvrir le micro dans une autre application | « Le micro est utilisé par une autre application… » |
| Silence | Cliquer puis ne rien dire pendant 7 s | « Je n'ai rien entendu… » |
| Texte vide | Bruit sans parole | « Je n'ai rien entendu… » |
| Trop long | Parler 45 s sans pause : arrêt automatique | La phrase est transcrite ; au-delà de 10 Mo : « L'enregistrement est trop long… » |
| Trop gros (413) | Service configuré avec `VOICE_MAX_AUDIO_BYTES` très bas | « L'enregistrement dépasse la taille autorisée (10 Mo)… » |
| Audio illisible (422) | Format non décodable | « Je n'ai pas pu lire cet enregistrement… » |
| Service indisponible (502/503/504) | `docker compose stop voice-service` | « Le service vocal est indisponible pour le moment… » |
| Non compatible | Navigateur sans `MediaRecorder` | « Ce navigateur ne permet pas l'enregistrement vocal… » |
| Hors HTTPS | Ouvrir le front par `http://<adresse IP>:5173` | « Le navigateur n'autorise le micro qu'en HTTPS ou sur localhost. » |
| Annulation | `Échap` ou « Annuler » pendant l'écoute ou la transcription | Retour à l'état de repos, aucune requête conservée |
| 401 / 403 | Jeton expiré | Page d'erreur dédiée (comportement commun de l'application) |

## 5. Points à relever pendant le test

- Durée entre la fin de la phrase et le texte (première requête plus lente à cause du chargement du modèle ; le délai maximal côté front est de 90 s).
- Qualité de la transcription des montants, des noms de produits et de clients.
- Comportement de l'arrêt automatique avec un micro bruyant : seuils dans `novaui/src/features/eclipse/voice/constants/voiceConfig.ts` (`SPEECH_LEVEL`, `TRAILING_SILENCE_MS`, `NO_SPEECH_TIMEOUT_MS`).
- Format audio envoyé : `audio/webm;codecs=opus` (Chrome, Edge, Firefox), `audio/mp4` (Safari).

## 6. Suite possible : transcription en temps réel

Le service expose déjà `WS /api/voice/stream` (messages `partial` et `final`). Un navigateur ne pouvant pas envoyer l'en-tête `Authorization` sur un WebSocket, son usage dans NovaUI demande d'abord que la gateway accepte le jeton par un autre canal (sous-protocole WebSocket ou jeton à usage unique). Ce choix touche à la sécurité de la gateway et n'a pas été fait dans cette version.
