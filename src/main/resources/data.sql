-- USERS
INSERT INTO users (id, name, username, password, role)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'admin', 'admin', '123', 'ADMIN'),
  ('22222222-2222-2222-2222-222222222222', 'Rodrigo', 'rodrigo', '123', 'CUSTOMER'),
  ('33333333-3333-3333-3333-333333333333', 'Courier1', 'courier1', '123', 'COURIER');

-- CUSTOMER (extends User)
INSERT INTO customer (id, order_count)
VALUES
  ('22222222-2222-2222-2222-222222222222', 0);

-- CUSTOMER ADDRESSES (ElementCollection)
INSERT INTO customer_addresses (customer_id, city, postal_code, street)
VALUES
  ('22222222-2222-2222-2222-222222222222', 'Lisboa', '1000-001', 'Rua A');

-- COURIER
INSERT INTO courier (id, availability, delivery_count)
VALUES
  ('33333333-3333-3333-3333-333333333333', true, 0);

-- ADMIN
INSERT INTO admin (id)
VALUES 
  ('11111111-1111-1111-1111-111111111111');

-- MENU
INSERT INTO menu (id, name)
VALUES
  ('44444444-4444-4444-4444-444444444444', 'Menu Principal');

-- RESTAURANT
INSERT INTO restaurant (id, name, nif, city, postal_code, street, kitchen_type, open, menu_id, rating, n_ratings)
VALUES
  ('55555555-5555-5555-5555-555555555555', 'Tasquinha', '123456789', 'Lisboa', '1000-002', 'Rua B', 'CHINESA', true, '44444444-4444-4444-4444-444444444444', 0, 0);

-- PRODUCTS
INSERT INTO product (id, name, description, price, available, category, popularity)
VALUES 
  ('66666666-6666-6666-6666-666666666666', 'Pizza', 'Boa pizza', 10.0, true, 'PRINCIPAL', 0),
  ('77777777-7777-7777-7777-777777777777', 'Hamburger', 'Bom burger', 8.0, true, 'PRINCIPAL', 0);

-- MENU <-> PRODUCT
INSERT INTO menu_product (menu_id, products_id)
VALUES
  ('44444444-4444-4444-4444-444444444444', '66666666-6666-6666-6666-666666666666'),
  ('44444444-4444-4444-4444-444444444444', '77777777-7777-7777-7777-777777777777');

-- MENU <-> RESTAURANT
INSERT INTO menu_restaurant (menu_id, restaurants_id)
VALUES
  ('44444444-4444-4444-4444-444444444444', '55555555-5555-5555-5555-555555555555');