CREATE TABLE user (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'collaborator', 'manager', 'supermanager') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE seller (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
    seller_ref VARCHAR(50) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_ref) REFERENCES user(login) ON DELETE SET NULL
);

CREATE TABLE pending_accounts (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
    lastname VARCHAR(100) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(50) NOT NULL UNIQUE,
    role ENUM('admin', 'collaborator', 'manager', 'supermanager') NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES user(login)
);

CREATE TABLE invoice (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
    date DATE NOT NULL,
    client_id VARCHAR(10) NOT NULL,
    client VARCHAR(255) NOT NULL,
    invoice_ref VARCHAR(255) NOT NULL,
    family VARCHAR(10) NULL,
    quantity INT NOT NULL,
    total_ttc DECIMAL(10, 2) NOT NULL,
    seller_ref VARCHAR(50) NOT NULL,
    total_invoice DECIMAL(10, 2) NOT NULL,
    pair INT NULL,
    status ENUM('facture', 'avoir') NOT NULL,
    date_import DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_ref) REFERENCES seller(seller_ref)
);

CREATE TABLE quotation (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT NOT NULL,
    date DATE NOT NULL,
    client_id VARCHAR(10) NOT NULL,
    client VARCHAR(255) NOT NULL,
    quotation_ref VARCHAR(255) NOT NULL,
    family VARCHAR(10) NULL,
    quantity INT NOT NULL,
    total_ttc DECIMAL(10, 2) NOT NULL,
    seller_ref VARCHAR(50) NOT NULL,
    total_quotation DECIMAL(10, 2) NOT NULL,
    pair INT NULL,
    status ENUM('validé', 'non validé') NOT NULL,
    comment TEXT NULL,
    date_import DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_ref) REFERENCES seller(seller_ref)
);