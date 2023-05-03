INSERT INTO users(id_user, created_at, full_name, phone_number, password, salt, role)
VALUES (1, NOW(), 'admin', '111111', 'b0679217306687c03c4b09cb3a4b6a2df45349e9f96208ad54e80e38ca6d5337',
        'helloworld', 2);

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