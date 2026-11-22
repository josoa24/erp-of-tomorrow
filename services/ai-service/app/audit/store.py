"""Journal des actions IA (AI-12, TRUST-10), persisté dans SQLite (bibliothèque standard).

Ne contient JAMAIS de token, de résultat brut d'outil ni d'argument d'outil (donc aucun numéro de
compte bancaire) : seulement l'identité, la question tronquée, les noms d'outils, les actions
(outil + identifiant) et l'étiquette de confiance. Une ligne écrite n'est jamais modifiée.
"""
from __future__ import annotations

import json
import sqlite3
import threading
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

_SCHEMA = """CREATE TABLE IF NOT EXISTS ai_audit (
    id INTEGER PRIMARY KEY AUTOINCREMENT, created_at TEXT NOT NULL, user_id TEXT NOT NULL,
    email TEXT, role TEXT NOT NULL, conversation_id TEXT, question TEXT NOT NULL, tools TEXT NOT NULL,
    actions TEXT NOT NULL, trust_label TEXT)"""
_COLUMNS = ("created_at", "user_id", "email", "role", "conversation_id", "question", "tools",
            "actions", "trust_label")
_JSON_COLUMNS = {"tools", "actions"}


def _now() -> str:
    return datetime.now(timezone.utc).isoformat(timespec="seconds").replace("+00:00", "Z")


class AuditStore:
    def __init__(self, path: str) -> None:
        if path != ":memory:":
            Path(path).parent.mkdir(parents=True, exist_ok=True)
        self._lock = threading.Lock()
        self._db = sqlite3.connect(path, check_same_thread=False)
        self._db.row_factory = sqlite3.Row
        with self._lock, self._db:
            self._db.execute(_SCHEMA)

    def record(self, entry: dict[str, Any]) -> None:
        row = {**entry, "created_at": _now()}
        values = [json.dumps(row.get(c), ensure_ascii=False) if c in _JSON_COLUMNS else row.get(c)
                  for c in _COLUMNS]
        with self._lock, self._db:
            self._db.execute(f"INSERT INTO ai_audit ({', '.join(_COLUMNS)}) "
                             f"VALUES ({', '.join('?' for _ in _COLUMNS)})", values)

    def list(self, limit: int = 50, user_id: str | None = None) -> list[dict[str, Any]]:
        sql, params = "SELECT * FROM ai_audit", []
        if user_id:
            sql, params = sql + " WHERE user_id = ?", [user_id]
        with self._lock:
            rows = self._db.execute(sql + " ORDER BY id DESC LIMIT ?", [*params, limit]).fetchall()
        return [{k: json.loads(r[k]) if k in _JSON_COLUMNS else r[k] for k in r.keys()} for r in rows]

    def close(self) -> None:
        with self._lock:
            self._db.close()
