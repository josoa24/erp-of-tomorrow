import asyncio
import time

import pytest
from google.genai import errors as genai_errors
from google.genai import types

from app.agent.orchestrator import AgentError, GeminiAgent
from app.config import Settings
from app.erp.client import ErpClient
from app.tools.registry import ToolRegistry
from tests.fake_erp import FakeErp
from tests.fake_gemini import FakeClient, fc_response, text_response
from tests.helpers import as_user


def make_agent(script, erp=None, **kw):
    client = FakeClient(script)
    agent = GeminiAgent(client, ToolRegistry(ErpClient((erp or FakeErp()).http())), Settings(gemini_model="t", **kw))
    return agent, client


def run(agent, msg, role="ADMIN", **kw):
    with as_user(role):
        return asyncio.run(agent.run(msg, **kw))


def test_single_tool_history_and_role_tools():
    agent, client = make_agent([fc_response(("get_stock_status", {"risk": "CRITICAL"})),
                                text_response("Le riz n'a plus que 2 jours de stock.")])
    r = run(agent, "Quels produits risquent la rupture ?", role="STOCK")
    assert r.response.startswith("Le riz") and r.tool_calls[0].success
    second = client.requests[1]["contents"]
    assert [c.role for c in second] == ["user", "model", "user"]
    assert second[1].parts[0].thought_signature == b"sig"
    assert second[2].parts[0].function_response.response["data"][0]["risk"] == "CRITICAL"
    cfg = client.requests[0]["config"]
    names = {d.name for d in cfg.tools[0].function_declarations}
    assert "get_stock_status" in names and "get_sales_summary" not in names   # outils du rôle STOCK
    assert "rôle STOCK" in cfg.system_instruction and "Date du jour" in cfg.system_instruction
    assert cfg.automatic_function_calling.disable is True


def test_parallel_tools():
    agent, _ = make_agent([fc_response(("get_stock_summary", {}), ("get_sales_summary", {"period": "this_month"})),
                           text_response("ok")])
    r = run(agent, "Valeur du stock et CA du mois")
    assert [c.name for c in r.tool_calls] == ["get_stock_summary", "get_sales_summary"] and all(c.success for c in r.tool_calls)


def test_forbidden_tool_is_not_called():
    erp = FakeErp()
    agent, _ = make_agent([fc_response(("get_sales_summary", {"period": "today"})), text_response("Pas d'accès.")], erp)
    r = run(agent, "CA du jour ?", role="STOCK")
    assert r.tool_calls[0].error_code == "FORBIDDEN" and erp.requests == []


def test_no_tool_and_max_turns():
    agent, _ = make_agent([text_response("Bonjour !")])
    assert run(agent, "Salut").tool_calls == []
    script = [fc_response(("get_stock_summary", {})) for _ in range(2)] + [text_response("Synthèse.")]
    agent, client = make_agent(script, max_tool_turns=2)
    r = run(agent, "boucle")
    assert r.max_turns_reached and client.requests[-1]["config"].tool_config.function_calling_config.mode == \
        types.FunctionCallingConfigMode.NONE


def test_gemini_errors_mapped():
    err = genai_errors.ClientError(429, {"error": {"code": 429, "message": "quota", "status": "RESOURCE_EXHAUSTED"}})
    agent, _ = make_agent([err])
    with pytest.raises(AgentError) as exc:
        run(agent, "x")
    assert exc.value.code == "GEMINI_RATE_LIMIT"
