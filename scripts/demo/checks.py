"""Outil de vérification : chaque contrôle affiche OK ou ÉCHEC, le bilan compte les échecs."""
from __future__ import annotations

from typing import Callable


class Checks:
    def __init__(self) -> None:
        self.failures: list[str] = []
        self.count = 0

    def section(self, title: str) -> None:
        print(f"\n== {title}")

    def expect(self, label: str, condition: Callable[[], bool], detail: Callable[[], object] = lambda: "") -> None:
        self.count += 1
        try:
            ok = bool(condition())
            info = "" if ok else f" -> {detail()}"
        except Exception as err:  # un contrôle qui plante est un échec, pas un arrêt
            ok, info = False, f" -> {type(err).__name__}: {err}"
        print(f"  {'OK    ' if ok else 'ÉCHEC '} {label}{info}")
        if not ok:
            self.failures.append(label)

    def status(self, label: str, api, method: str, path: str, expected: int, body=None) -> object:
        status, payload = api.request(method, path, body)
        self.expect(f"{label} ({method} {path} -> {expected})", lambda: status == expected,
                    lambda: f"{status} {str(payload)[:160]}")
        return payload

    def summary(self) -> int:
        print(f"\n{self.count - len(self.failures)}/{self.count} contrôles réussis")
        for label in self.failures:
            print(f"  ÉCHEC : {label}")
        return 1 if self.failures else 0
