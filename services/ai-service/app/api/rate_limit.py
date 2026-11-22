"""Limite de débit par utilisateur (AI-13) : fenêtre glissante d'une minute, en mémoire.

Appliquée aux routes qui appellent Gemini (chat, streaming, extraction de facture).
Une seule instance du service : pas de stockage partagé (suffisant ici).
"""
from __future__ import annotations

import math
import threading
import time
from collections import defaultdict, deque
from typing import Callable

from fastapi import Request

from app.api.identity import user_from_request


class RateLimited(Exception):
    def __init__(self, retry_after_s: int) -> None:
        super().__init__("rate limited")
        self.retry_after_s = retry_after_s


class SlidingWindowLimiter:
    def __init__(self, limit: int, window_s: float = 60.0, clock: Callable[[], float] = time.monotonic) -> None:
        self.limit = limit
        self.window_s = window_s
        self._clock = clock
        self._hits: dict[str, deque[float]] = defaultdict(deque)
        self._lock = threading.Lock()

    def hit(self, key: str) -> int:
        """Enregistre une requête. Renvoie 0 si elle est acceptée, sinon le délai d'attente (secondes)."""
        now = self._clock()
        with self._lock:
            hits = self._hits[key]
            while hits and now - hits[0] >= self.window_s:
                hits.popleft()
            if len(hits) >= self.limit:
                return max(1, math.ceil(self.window_s - (now - hits[0])))
            hits.append(now)
            return 0


def enforce_rate_limit(request: Request) -> None:
    """Dépendance FastAPI : 401 sans identité, 429 au-delà de la limite de l'utilisateur."""
    user = user_from_request(request)
    wait = request.app.state.rate_limiter.hit(user.user_id)
    if wait:
        raise RateLimited(wait)
