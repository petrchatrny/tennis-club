INSERT INTO users(id_user, created_at, full_name, phone_number, password, salt, role)
VALUES (1, NOW(), 'admin', '111111111', 'd81aca2ecd821d22fe1721ba1ed608274a9c1d20ae4907cea0e9ffeabb066552',
        '45f8sad6sd5s4sa6d5ff', 1);

INSERT INTO surfaces(id_surface, created_at)
VALUES (1, NOW()),
       (2, NOW());

INSERT INTO surface_prices(valid_from, valid_to, price_per_minute_in_czk, id_surface)
VALUES ('2023-04-24T10:00:00', null, 20, 1),
       ('2023-04-24T10:00:00', null, 25, 2);

INSERT INTO courts(id_court, id_surface, created_at)
VALUES (1, 1, NOW()),
       (2, 1, NOW()),
       (3, 2, NOW()),
       (4, 2, NOW());