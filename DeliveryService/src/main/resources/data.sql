-- COURIERS (mirrored from TascaEats seed data)
INSERT INTO couriers (id, name, username, available)
VALUES ('33333333-3333-3333-3333-333333333333', 'Courier1', 'courier1', true)
ON CONFLICT (id) DO NOTHING;
