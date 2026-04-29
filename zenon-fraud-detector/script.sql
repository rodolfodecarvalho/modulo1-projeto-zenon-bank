create table transactions
(
    id                    bigint auto_increment primary key,
    step                  int                                                          not null,
    type                  ENUM ('CASH_IN', 'CASH_OUT', 'DEBIT', 'PAYMENT', 'TRANSFER') not null,
    amount                decimal(20, 2)                                               not null,
    name_origin           varchar(50)                                                  not null,
    old_balance_origin    decimal(20, 2)                                               not null,
    new_balance_origin    decimal(20, 2)                                               not null,
    name_recipient        varchar(50)                                                  not null,
    old_balance_recipient decimal(20, 2)                                               not null,
    new_balance_recipient decimal(20, 2)                                               not null,
    is_fraud              tinyint(1) default 0,
    is_flagged_fraud      tinyint(1) default 0
);


INSERT INTO transactions ( step, `type`, amount, name_origin, old_balance_origin, new_balance_origin, name_recipient
                         , old_balance_recipient, new_balance_recipient, is_fraud, is_flagged_fraud)
VALUES (1, 'PAYMENT', 5000.00, 'C1000001', 5000.00, 0.00, 'M1000001', 0.00, 0.00, 0, 0)