INSERT INTO Planet (id, name) VALUES
    ('EARTH', 'Earth'),
    ('MARS', 'Mars'),
    ('VEN', 'Venus'),
    ('JUP', 'Jupiter'),
    ('SAT', 'Saturn');

INSERT INTO Client (name) VALUES
    ('Ivan Petrenko'),
    ('Olena Kovalenko'),
    ('Andriy Shevchenko'),
    ('Maria Boyko'),
    ('Taras Zhokh'),
    ('Nataliya Bondar'),
    ('Oleh Melnyk'),
    ('Iryna Kravets'),
    ('Serhiy Tkachenko'),
    ('Yulia Moroz');

INSERT INTO Ticket (created_at, client_id, from_planet_id, to_planet_id) VALUES
    ('2026-08-01 10:00:00', 1, 'EARTH', 'MARS'),
    ('2026-08-02 12:30:00', 2, 'EARTH', 'VEN'),
    ('2026-08-03 08:15:00', 3, 'MARS', 'EARTH'),
    ('2026-08-04 14:45:00', 4, 'EARTH', 'JUP'),
    ('2026-08-05 09:20:00', 5, 'VEN', 'SAT'),
    ('2026-08-06 11:00:00', 6, 'EARTH', 'SAT'),
    ('2026-08-07 16:10:00', 7, 'JUP', 'EARTH'),
    ('2026-08-08 13:35:00', 8, 'EARTH', 'VEN'),
    ('2026-08-09 07:50:00', 9, 'MARS', 'JUP'),
    ('2026-08-10 18:25:00', 10, 'SAT', 'EARTH');