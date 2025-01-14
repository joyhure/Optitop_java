CREATE TABLE user (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT UNSIGNED,
    lastname VARCHAR(100) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'collaborator', 'manager', 'supermanager') NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE seller (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT UNSIGNED,
    seller_ref VARCHAR(50) NOT NULL UNIQUE,
    user_id INT UNSIGNED NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE pending_accounts (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT UNSIGNED,
    lastname VARCHAR(100) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    role ENUM('admin', 'collaborator', 'manager', 'supermanager') NOT NULL,
    created_by INT NOT NULL UNSIGNED,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (created_by) REFERENCES user(id)
);

CREATE TABLE invoice (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT UNSIGNED,
    date DATE NOT NULL,
    client_id INT NOT NULL UNSIGNED,
    client VARCHAR(255) NOT NULL,
    invoice_ref VARCHAR(255) NOT NULL,
    family VARCHAR(10) NULL,
    quantity INT NOT NULL,
    total_ttc DECIMAL(10, 2) NOT NULL,
    seller_ref VARCHAR(50) NOT NULL,
    seller_id INT NOT NULL UNSIGNED,
    total_invoice DECIMAL(10, 2) NOT NULL,
    pair INT NULL,
    date_import DATE NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);

CREATE TABLE quotation (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT UNSIGNED,
    date DATE NOT NULL,
    client_id INT NOT NULL UNSIGNED,
    client VARCHAR(255) NOT NULL,
    invoice_ref VARCHAR(255) NOT NULL,
    family VARCHAR(10) NULL,
    quantity INT NOT NULL,
    total_ttc DECIMAL(10, 2) NOT NULL,
    seller_ref VARCHAR(50) NOT NULL,
    seller_id INT NOT NULL UNSIGNED,
    total_invoice DECIMAL(10, 2) NOT NULL,
    pair INT NULL,
    date_import DATE NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);