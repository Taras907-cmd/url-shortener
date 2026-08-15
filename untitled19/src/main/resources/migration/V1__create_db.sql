CREATE TABLE Planet (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(500) NOT NULL
);

CREATE TABLE Client (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE Ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    client_id INT NOT NULL,
    from_planet_id VARCHAR(10) NOT NULL,
    to_planet_id VARCHAR(10) NOT NULL,
    CONSTRAINT fk_ticket_client
        FOREIGN KEY (client_id) REFERENCES Client (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_ticket_from_planet
        FOREIGN KEY (from_planet_id) REFERENCES Planet (id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_to_planet
        FOREIGN KEY (to_planet_id) REFERENCES Planet (id)
        ON DELETE RESTRICT
);