-- Insérer des données de test dans la table invoice
INSERT INTO invoice (date, client_id, client, invoice_ref, family, quantity, total_ttc, seller_ref, total_invoice, pair, status, date_import)
VALUES 
('2023-01-02', 103014513, 'ALTRI Carmin', 'F103063787', 'LENT', 1, 100.00, 'tcollab', 200.00,NULL, 'facture', '2023-01-09'),
('2023-01-02', 103014522, 'ZARZ Kelvin', 'F103063790', 'MON', 1, 235.00, 'tcollab', 335.00, 1, 'facture', '2023-01-09'),
('2023-01-02', 103014522, 'ZARZ Kelvin', 'F103063790', 'VER', 1, 50.00, 'tcollab', 335.00, 1, 'facture', '2023-01-09'),
('2023-01-02', 103014522, 'ZARZ Kelvin', 'F103063790', 'VER', 1, 50.00, 'tcollab', 335.00, 1, 'facture', '2023-01-09'),
('2023-01-02', 103014522, 'ZARZ Kelvin', 'F103063790', 'SOL', 1, 0.00, 'tcollab', 335.00, 2, 'facture', '2023-01-09'),
('2023-01-02', 103014522, 'ZARZ Kelvin', 'A103058184', 'MON', -1, -235.00, 'tcollab', 335.00, 1, 'avoir', '2023-01-09'),
('2023-01-03', 103014527, 'MASSOT Diane', 'F103063793', 'DIV', 1, 30.00, 'tmanager', 30.00, NULL, 'facture', '2023-01-09'),
('2023-01-04', 103014527, 'BINET Alexandre', 'F103063794', 'CLI', 1, 29.00, 'tsupermanager', 29.00, NULL, 'facture', '2023-01-09');

-- Insérer des données de test dans la table quotation
INSERT INTO quotation (date, client_id, client, quotation_ref, family, quantity, total_ttc, seller_ref, total_quotation, pair, status, comment, date_import)
VALUES 
('2023-01-02', 103019519, 'GILBERT David', 'D103053016', 'MON', 1, 120.00, 'tcollab', 220, 1, 'validé', 'franchise casse 30%', '2023-01-09'),
('2023-01-02', 103019519, 'GILBERT David', 'D103053016', 'VER', 1, 50.00, 'tcollab', 220.00, 1, 'validé', 'franchise casse 30%', '2023-01-09'),
('2023-01-02', 103019519, 'GILBERT David', 'D103053016', 'VER', 1, 50.00, 'tcollab', 220.00, 1, 'validé', 'franchise casse 30%', '2023-01-09'),
('2023-01-03', 103019523, 'MARTINS Chris', 'D103053020', 'SOL', 1, 300.00, 'tmanager', 300.00, NULL, 'non validé', '', '2023-01-09'),
('2023-01-03', 103019523, 'MARTINS Chris', 'D103053021', 'SOL', 1, 29.00, 'tmanager', 29.00, NULL, 'non validé', '', '2023-01-09'),
('2023-01-03', 103019523, 'MARTINS Chris', 'D103053022', 'SOL', 1, 250.00, 'tmanager', 250.00, NULL, 'validé', '', '2023-01-09');