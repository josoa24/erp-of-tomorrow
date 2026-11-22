from __future__ import annotations

import logging
from typing import Any, Callable

from app.agent import ui_business, ui_relations, ui_stock
from app.agent.records import ToolCallRecord

logger = logging.getLogger(__name__)

Builder = Callable[[dict[str, Any]], list[dict[str, Any]]]

BUILDERS: dict[str, Builder] = {
    "get_stock_summary": ui_stock.stock_summary,
    "search_products": ui_stock.products,
    "get_stock_status": ui_stock.stock_status,
    "get_product_autonomy": ui_stock.autonomy,
    "get_product_movements": ui_stock.movements,
    "search_suppliers": ui_stock.suppliers,
    "list_purchase_orders": ui_stock.purchase_orders,
    "get_sales_summary": ui_business.sales_summary,
    "get_top_products": ui_business.top_products,
    "search_sales": ui_business.sales,
    "search_customers": ui_business.customers,
    "get_customer_profile": ui_business.customer_profile,
    "get_hr_summary": ui_business.hr_summary,
    "search_employees": ui_business.employees,
    "list_contacts": ui_relations.contacts,
    "get_contact_summary": ui_relations.contact_summary,
    "list_leave_requests": ui_relations.leaves,
    "list_salary_advances": ui_relations.advances,
    "get_advance_summary": ui_relations.advance_summary,
    "get_sales_kpis": ui_relations.sales_kpis,
    "get_credit_summary": ui_relations.credit_summary,
    "get_module_indicators": ui_relations.module_indicators,
}


def blocks_for(record: ToolCallRecord) -> list[dict[str, Any]]:
    builder = BUILDERS.get(record.name)
    if builder is None or not record.success or not record.result:
        return []
    try:
        return builder(record.result)
    except Exception:  # noqa: BLE001
        logger.exception("Bloc d'interface impossible pour l'outil %s", record.name)
        return []
