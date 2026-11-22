"""Chargement des variables d'environnement (même nom que le champ, en majuscules)."""
from app.config import SERVICE_DIR, Settings, get_settings


def test_env_variables_are_read(monkeypatch):
    for name, value in {"AI_RATE_LIMIT_PER_MINUTE": "7", "AI_AUDIT_DB": "/tmp/x.db", "INVOICE_MAX_MB": "2",
                        "CORS_ORIGINS": "http://a, http://b", "GEMINI_TEMPERATURE": "", "LOG_LEVEL": "debug",
                        "CHECK_MODEL_ON_STARTUP": "false"}.items():
        monkeypatch.setenv(name, value)
    get_settings.cache_clear()
    try:
        s = get_settings()
    finally:
        get_settings.cache_clear()
    assert s.ai_rate_limit_per_minute == 7 and s.invoice_max_bytes == 2 * 1024 * 1024
    assert s.cors_origins == ["http://a", "http://b"] and s.gemini_temperature is None
    assert s.log_level == "DEBUG" and s.check_model_on_startup is False


def test_audit_path_relative_to_service_dir():
    assert Settings().audit_db_path == str(SERVICE_DIR / "data" / "ai_audit.db")
    assert Settings(ai_audit_db=":memory:").audit_db_path == ":memory:"
