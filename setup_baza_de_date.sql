DROP TABLE IF EXISTS lista_favorite CASCADE;
DROP TABLE IF EXISTS istoric_pret CASCADE;
DROP TABLE IF EXISTS tranzactii CASCADE;
DROP TABLE IF EXISTS solduri CASCADE;
DROP TABLE IF EXISTS operatiuni_financiare CASCADE;
DROP TABLE IF EXISTS verificare_kyc CASCADE;
DROP TABLE IF EXISTS wallet CASCADE;
DROP TABLE IF EXISTS utilizatori CASCADE;
DROP TABLE IF EXISTS criptomonede CASCADE;

CREATE TABLE utilizatori (
                             id_utilizator SERIAL PRIMARY KEY,
                             nume_complet VARCHAR(100) NOT NULL,
                             username VARCHAR(50) UNIQUE NOT NULL,
                             email VARCHAR(100) UNIQUE NOT NULL,
                             parola VARCHAR(255) NOT NULL,
                             numar_de_telefon VARCHAR(20),
                             data_inregistrarii TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             tip_utilizator VARCHAR(20) DEFAULT 'Standard',
                             status_cont VARCHAR(20) DEFAULT 'activ'
);

CREATE TABLE criptomonede (
                              id_moneda SERIAL PRIMARY KEY,
                              simbol VARCHAR(10) UNIQUE NOT NULL,
                              denumire_completa VARCHAR(50) NOT NULL,
                              pret_curent DECIMAL(20, 8) NOT NULL,
                              pret_maxim DECIMAL(20, 8),
                              data_lansarii DATE,
                              descriere TEXT,
                              status VARCHAR(20) DEFAULT 'activ',
                              comision_trading DECIMAL(5, 2) DEFAULT 0.1
);

CREATE TABLE wallet (
                        id_wallet SERIAL PRIMARY KEY,
                        id_utilizator INT REFERENCES utilizatori(id_utilizator) ON DELETE CASCADE,
                        adresa_wallet VARCHAR(100) UNIQUE NOT NULL,
                        tip_wallet VARCHAR(20),
                        data_crearii TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR(20) DEFAULT 'activ'
);

CREATE TABLE verificare_kyc (
                                id_verificare SERIAL PRIMARY KEY,
                                id_utilizator INT REFERENCES utilizatori(id_utilizator) ON DELETE CASCADE,
                                nume_complet VARCHAR(100),
                                cnp VARCHAR(20),
                                tip_document VARCHAR(50),
                                data_trimiterii TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                status_verificare VARCHAR(20) DEFAULT 'in_asteptare',
                                tara_emitenta VARCHAR(50)
);

CREATE TABLE operatiuni_financiare (
                                       id_operatiune SERIAL PRIMARY KEY,
                                       id_utilizator INT REFERENCES utilizatori(id_utilizator) ON DELETE CASCADE,
                                       tip_operatiune VARCHAR(20) NOT NULL,
                                       suma DECIMAL(15, 2) NOT NULL,
                                       moneda_fiat VARCHAR(5) DEFAULT 'RON',
                                       data_operatiune TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       metoda_plata VARCHAR(50) DEFAULT 'Card Bancar',
                                       status VARCHAR(20) DEFAULT 'complet'
);

CREATE TABLE solduri (
                         id_sold SERIAL PRIMARY KEY,
                         id_utilizator INT REFERENCES utilizatori(id_utilizator) ON DELETE CASCADE,
                         id_criptomoneda INT REFERENCES criptomonede(id_moneda) ON DELETE CASCADE,
                         id_wallet INT REFERENCES wallet(id_wallet) ON DELETE CASCADE,
                         cantitate_disponibilA DECIMAL(20, 8) DEFAULT 0,
                         cantitate_blocata DECIMAL(20, 8) DEFAULT 0,
                         valoarea_totala DECIMAL(20, 2),
                         data_actualizare TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tranzactii (
                            id_tranzactie SERIAL PRIMARY KEY,
                            id_utilizator INT REFERENCES utilizatori(id_utilizator) ON DELETE CASCADE,
                            id_criptomoneda INT REFERENCES criptomonede(id_moneda) ON DELETE CASCADE,
                            tip_tranzactie VARCHAR(10) NOT NULL,
                            cantitate DECIMAL(20, 8) NOT NULL,
                            pret DECIMAL(20, 8) NOT NULL,
                            comision DECIMAL(10, 2),
                            data_si_ora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            status VARCHAR(20) DEFAULT 'finalizat'
);

CREATE TABLE istoric_pret (
                              id_istoric SERIAL PRIMARY KEY,
                              id_moneda INT REFERENCES criptomonede(id_moneda) ON DELETE CASCADE,
                              pret_deschidere DECIMAL(20, 8),
                              pret_inchidere DECIMAL(20, 8),
                              volum_tranzactionare DECIMAL(20, 2),
                              data_si_ora_exacta TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE lista_favorite (
                                id_favorit SERIAL PRIMARY KEY,
                                id_utilizator INT REFERENCES utilizatori(id_utilizator) ON DELETE CASCADE,
                                id_moneda INT REFERENCES criptomonede(id_moneda) ON DELETE CASCADE,
                                data_adaugarii TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO utilizatori (nume_complet, username, email, parola, numar_de_telefon, tip_utilizator, status_cont, data_inregistrarii) VALUES
                                                                                                                                       ('Andrei Popescu', 'apopescu', 'andrei.popescu@corporate.ro', 'pass123', '0722100100', 'VIP', 'activ', '2023-01-10'),
                                                                                                                                       ('Elena Ionescu', 'elena.i', 'elena.ionescu@business.ro', 'pass123', '0722100101', 'VIP', 'activ', '2023-01-15'),
                                                                                                                                       ('Alexandru Dumitru', 'alex.d', 'alex.dumitru@tech.ro', 'pass123', '0722100102', 'Premium', 'activ', '2023-02-20'),
                                                                                                                                       ('Cristian Radu', 'cradu', 'cristian.radu@finance.ro', 'pass123', '0722100103', 'Premium', 'activ', '2023-03-05'),
                                                                                                                                       ('Maria Stoica', 'mariast', 'maria.stoica@consulting.ro', 'pass123', '0722100104', 'VIP', 'activ', '2023-03-10'),
                                                                                                                                       ('Gabriel Gheorghe', 'ggheorghe', 'gabriel.g@yahoo.com', 'pass123', '0722100105', 'Standard', 'activ', '2023-04-12'),
                                                                                                                                       ('Ioana Munteanu', 'ioanam', 'ioana.munteanu@gmail.com', 'pass123', '0722100106', 'Standard', 'activ', '2023-05-01'),
                                                                                                                                       ('Mihai Lazăr', 'mlazar', 'mihai.lazar@outlook.com', 'pass123', '0722100107', 'Standard', 'activ', '2023-05-15'),
                                                                                                                                       ('Adrian Diaconu', 'adi_dia', 'adrian.diaconu@gmail.com', 'pass123', '0722100108', 'Standard', 'activ', '2023-06-01'),
                                                                                                                                       ('Roxana Barbu', 'roxanab', 'roxana.barbu@yahoo.com', 'pass123', '0722100109', 'Standard', 'activ', '2023-06-20'),
                                                                                                                                       ('Florin Neagu', 'fneagu', 'florin.neagu@gmail.com', 'pass123', '0722100110', 'Standard', 'activ', '2023-07-04'),
                                                                                                                                       ('Simona Preda', 'spreda', 'simona.preda@corporate.ro', 'pass123', '0722100111', 'Premium', 'activ', '2023-07-10'),
                                                                                                                                       ('Vlad Stanciu', 'vstanciu', 'vlad.stanciu@tech.ro', 'pass123', '0722100112', 'Standard', 'suspendat', '2023-08-01'),
                                                                                                                                       ('Daniela Nistor', 'daniela.n', 'daniela.nistor@gmail.com', 'pass123', '0722100113', 'Standard', 'activ', '2023-08-15'),
                                                                                                                                       ('George Marin', 'gmarin', 'george.marin@yahoo.com', 'pass123', '0722100114', 'Standard', 'activ', '2023-09-01'),
                                                                                                                                       ('Camelia Dobre', 'camelia.d', 'camelia.dobre@outlook.com', 'pass123', '0722100115', 'Standard', 'activ', '2023-09-10'),
                                                                                                                                       ('Victor Oprea', 'voprea', 'victor.oprea@gmail.com', 'pass123', '0722100116', 'Premium', 'activ', '2023-09-25'),
                                                                                                                                       ('Alina Cojocaru', 'acojocaru', 'alina.cojocaru@business.ro', 'pass123', '0722100117', 'VIP', 'activ', '2023-10-01'),
                                                                                                                                       ('Bogdan Voinea', 'bvoinea', 'bogdan.voinea@gmail.com', 'pass123', '0722100118', 'Standard', 'blocat', '2023-10-15'),
                                                                                                                                       ('Diana Manole', 'dmanole', 'diana.manole@yahoo.com', 'pass123', '0722100119', 'Standard', 'activ', '2023-11-01');

INSERT INTO utilizatori (nume_complet, username, email, parola, tip_utilizator, status_cont, data_inregistrarii)
VALUES ('Horia HODL', 'horia_hodl', 'horia@crypto.ro', 'pass123', 'Standard', 'activ', '2023-01-01');

INSERT INTO utilizatori (nume_complet, username, email, parola, tip_utilizator, status_cont, data_inregistrarii)
VALUES ('Barbu Delavrancea', 'barbu_vip', 'barbu@vip.ro', 'pass123', 'VIP', 'activ', NOW());

INSERT INTO utilizatori (nume_complet, username, email, parola, tip_utilizator)
VALUES ('Tiriac Ion', 'tiriac_crypto', 'ion@tiriac.ro', 'pass123', 'VIP');

INSERT INTO utilizatori (nume_complet, username, email, parola, tip_utilizator, data_inregistrarii)
VALUES ('Nou Venit', 'newbie_2026', 'new@email.com', 'pass123', 'Standard', NOW());

INSERT INTO criptomonede (simbol, denumire_completa, pret_curent, data_lansarii, status, comision_trading) VALUES
                                                                                                               ('BTC', 'Bitcoin', 64500.00, '2009-01-03', 'activ', 0.1),
                                                                                                               ('ETH', 'Ethereum', 3450.00, '2015-07-30', 'activ', 0.1),
                                                                                                               ('SOL', 'Solana', 145.20, '2020-03-16', 'activ', 0.2),
                                                                                                               ('BNB', 'Binance Coin', 590.50, '2017-07-08', 'activ', 0.1),
                                                                                                               ('ADA', 'Cardano', 0.45, '2017-09-29', 'activ', 0.15),
                                                                                                               ('XRP', 'Ripple', 0.62, '2012-06-01', 'activ', 0.1),
                                                                                                               ('DOT', 'Polkadot', 7.20, '2020-05-26', 'activ', 0.2),
                                                                                                               ('DOGE', 'Dogecoin', 0.16, '2013-12-06', 'activ', 0.3),
                                                                                                               ('AVAX', 'Avalanche', 38.90, '2020-09-21', 'activ', 0.2),
                                                                                                               ('LINK', 'Chainlink', 18.50, '2017-09-19', 'activ', 0.2),
                                                                                                               ('USDT', 'Tether', 1.00, '2014-10-06', 'activ', 0.1),
                                                                                                               ('USDC', 'USD Coin', 1.00, '2018-09-26', 'activ', 0.1),
                                                                                                               ('XMR', 'Monero', 120.50, '2014-04-18', 'activ', 0.2),
                                                                                                               ('LTC', 'Litecoin', 75.20, '2011-10-07', 'activ', 0.1),
                                                                                                               ('SHIB', 'Shiba Inu', 0.000025, '2020-08-01', 'activ', 0.3),
                                                                                                               ('PEPE', 'Pepe Coin', 0.000007, '2023-04-17', 'activ', 0.3),
                                                                                                               ('TRX', 'Tron', 0.11, '2017-09-13', 'activ', 0.1),
                                                                                                               ('ATOM', 'Cosmos', 11.20, '2019-03-14', 'activ', 0.2),
                                                                                                               ('FIL', 'Filecoin', 8.50, '2017-07-15', 'activ', 0.2),
                                                                                                               ('APT', 'Aptos', 15.20, '2022-10-12', 'activ', 0.2);

INSERT INTO criptomonede (simbol, denumire_completa, pret_curent, data_lansarii, status)
VALUES ('GHOST', 'Ghost Coin', 0.99, '2024-01-01', 'activ');

INSERT INTO wallet (id_utilizator, adresa_wallet, tip_wallet, status)
SELECT id_utilizator, '0x' || md5(random()::text),
       CASE WHEN random() > 0.5 THEN 'hot' ELSE 'cold' END,
       'activ'
FROM utilizatori
WHERE username NOT IN ('tiriac_crypto', 'horia_hodl');

INSERT INTO wallet (id_utilizator, adresa_wallet, tip_wallet, status)
VALUES ((SELECT id_utilizator FROM utilizatori WHERE username='tiriac_crypto'), '0xTiriacWallet', 'cold', 'activ');

INSERT INTO wallet (id_utilizator, adresa_wallet, tip_wallet, status)
VALUES ((SELECT id_utilizator FROM utilizatori WHERE username='horia_hodl'), '0xHoriaWallet', 'hot', 'activ');

INSERT INTO verificare_kyc (id_utilizator, cnp, tip_document, nume_complet, status_verificare, tara_emitenta)
SELECT id_utilizator,
       (1000000000000 + floor(random() * 8999999999999))::text,
    'Carte Identitate',
       nume_complet,
       'aprobat',
       'Romania'
FROM utilizatori WHERE tip_utilizator IN ('VIP', 'Premium');

INSERT INTO verificare_kyc (id_utilizator, cnp, tip_document, nume_complet, status_verificare)
VALUES ((SELECT id_utilizator FROM utilizatori WHERE username='horia_hodl'), '1890101123456', 'CI', 'Horia HODL', 'aprobat');

INSERT INTO operatiuni_financiare (id_utilizator, tip_operatiune, suma, moneda_fiat, status)
SELECT id_utilizator, 'DEPUNERE', floor(random() * 5000 + 100), 'RON', 'complet'
FROM utilizatori WHERE id_utilizator <= 20;

INSERT INTO operatiuni_financiare (id_utilizator, tip_operatiune, suma, moneda_fiat, status)
VALUES ((SELECT id_utilizator FROM utilizatori WHERE username='tiriac_crypto'), 'DEPUNERE', 5000000, 'EUR', 'complet');

INSERT INTO operatiuni_financiare (id_utilizator, tip_operatiune, suma, moneda_fiat, status)
VALUES ((SELECT id_utilizator FROM utilizatori WHERE username='horia_hodl'), 'DEPUNERE', 5000, 'RON', 'complet');

INSERT INTO operatiuni_financiare (id_utilizator, tip_operatiune, suma, moneda_fiat, status)
SELECT id_utilizator, 'RETRAGERE', 500, 'RON', 'complet'
FROM utilizatori WHERE id_utilizator <= 10;

INSERT INTO solduri (id_utilizator, id_criptomoneda, id_wallet, cantitate_disponibila, valoarea_totala)
VALUES (
           (SELECT id_utilizator FROM utilizatori WHERE username='tiriac_crypto'),
           (SELECT id_moneda FROM criptomonede WHERE simbol='BTC'),
           (SELECT id_wallet FROM wallet WHERE adresa_wallet='0xTiriacWallet'),
           1000, 65000000
       );

INSERT INTO solduri (id_utilizator, id_criptomoneda, id_wallet, cantitate_disponibila, valoarea_totala)
VALUES (
           (SELECT id_utilizator FROM utilizatori WHERE username='horia_hodl'),
           (SELECT id_moneda FROM criptomonede WHERE simbol='ETH'),
           (SELECT id_wallet FROM wallet WHERE adresa_wallet='0xHoriaWallet'),
           5000, 15000000
       );

INSERT INTO solduri (id_utilizator, id_criptomoneda, id_wallet, cantitate_disponibila)
SELECT u.id_utilizator, c.id_moneda, w.id_wallet, (random() * 10)
FROM utilizatori u
         JOIN wallet w ON u.id_utilizator = w.id_utilizator
         CROSS JOIN criptomonede c
WHERE u.id_utilizator <= 20 AND c.simbol IN ('ADA', 'XRP', 'DOGE', 'SOL')
  AND random() > 0.8;

INSERT INTO tranzactii (id_utilizator, id_criptomoneda, tip_tranzactie, cantitate, pret, status)
SELECT
    floor(random() * 20 + 1),
    floor(random() * 10 + 1),
    CASE WHEN random() > 0.5 THEN 'BUY' ELSE 'SELL' END,
    (random() * 100),
    (random() * 1000 + 10),
    'finalizat'
FROM generate_series(1, 50);

INSERT INTO tranzactii (id_utilizator, id_criptomoneda, tip_tranzactie, cantitate, pret, status)
VALUES (
           (SELECT id_utilizator FROM utilizatori WHERE username='tiriac_crypto'),
           (SELECT id_moneda FROM criptomonede WHERE simbol='BTC'),
           'BUY', 1, 99999, 'finalizat'
       );

UPDATE criptomonede SET pret_maxim = pret_curent * (1.2 + (random() * 0.3));
UPDATE criptomonede SET pret_maxim = 73750.00 WHERE simbol = 'BTC';
UPDATE criptomonede SET pret_maxim = 4891.00 WHERE simbol = 'ETH';
UPDATE criptomonede SET pret_maxim = 260.00 WHERE simbol = 'SOL';

UPDATE criptomonede SET pret_curent = 99999 WHERE simbol = 'BTC';
UPDATE criptomonede SET pret_curent = 0.1 WHERE simbol = 'DOGE';

DELETE FROM tranzactii WHERE id_criptomoneda = (SELECT id_moneda FROM criptomonede WHERE simbol = 'GHOST');

DELETE FROM operatiuni_financiare WHERE id_utilizator = (SELECT id_utilizator FROM utilizatori WHERE username='horia_hodl') AND tip_operatiune = 'RETRAGERE';

DELETE FROM tranzactii WHERE id_utilizator = (SELECT id_utilizator FROM utilizatori WHERE username='barbu_vip');
