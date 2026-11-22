SET ROLE stock_svc;

ALTER TABLE IF EXISTS stock.products ADD COLUMN IF NOT EXISTS category varchar(80);

ALTER TABLE IF EXISTS stock.products ADD COLUMN IF NOT EXISTS purchase_price numeric(14,2);

ALTER TABLE IF EXISTS stock.products DROP CONSTRAINT IF EXISTS ck_products_purchase_price_non_negative;

ALTER TABLE IF EXISTS stock.products ADD CONSTRAINT ck_products_purchase_price_non_negative
    CHECK (purchase_price >= 0);

DO $$
BEGIN
    IF to_regclass('stock.products') IS NULL
        OR to_regclass('stock.purchase_order_lines') IS NULL
        OR to_regclass('stock.purchase_orders') IS NULL THEN
        RETURN;
    END IF;
    UPDATE stock.products p
    SET purchase_price = last_line.unit_price
    FROM (
        SELECT DISTINCT ON (l.product_id) l.product_id, l.unit_price
        FROM stock.purchase_order_lines l
        JOIN stock.purchase_orders o ON o.id = l.purchase_order_id
        WHERE o.status IN ('VALIDATED', 'RECEIVED')
        ORDER BY l.product_id, coalesce(o.received_at, o.validated_at, o.created_at) DESC, l.id DESC
    ) last_line
    WHERE p.id = last_line.product_id
      AND p.purchase_price IS NULL;
END $$;

RESET ROLE;
