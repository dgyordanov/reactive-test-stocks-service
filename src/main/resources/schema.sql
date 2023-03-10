DROP TABLE STOCK IF EXISTS;

CREATE TABLE STOCK(
    ID serial not null primary key,
    ISIN varchar not null,
    PRICE_CURRENCY varchar not null,
    PRICE_AMOUNT decimal(50, 2) not null,
    UPDATED_ON timestamp not null,
    UNIQUE (ISIN)
);

INSERT INTO STOCK(ISIN, PRICE_CURRENCY, PRICE_AMOUNT, UPDATED_ON) VALUES ('IBM', 'EUR', 13.33, now());