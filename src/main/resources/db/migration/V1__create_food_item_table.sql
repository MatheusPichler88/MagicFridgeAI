CREATE TABLE food_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria VARCHAR(50),
    quantidade INT,
    validade DATE
);