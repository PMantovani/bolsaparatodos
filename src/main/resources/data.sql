INSERT INTO User (id, name, email, password_hash) VALUES
(1, 'Name', 'email@email.com', '1234');

INSERT INTO Account (id, user_id, name, type) VALUES
(1, 1, 'Account', 'CHECKING_ACCOUNT');

INSERT INTO Transaction (id, value, from_account_id, to_account_id, title, transaction_date, is_invisible, is_accountable, creation_date, last_modified_date) VALUES
(1, 123.45, 1, null, 'Transaction Title', '2020-01-01 10:00:00-03', false, true, '2020-01-01 10:00:00-03', '2020-01-01 10:00:00-03'),
(2, 200.45, 1, null, 'Transaction Title', '2020-01-01 11:00:00-03', false, true, '2020-01-01 10:00:00-03', '2020-01-01 10:00:00-03'),
(3, 678.91, null, 1, 'Transaction Title 2', '2020-01-02 10:00:00-03', false, true, '2020-01-01 10:00:00-03', '2020-01-01 10:00:00-03'),
(4, 50.00, null, 1, 'Transaction Title 2', '2020-01-02 10:00:00-03', false, true, '2020-01-01 10:00:00-03', '2020-01-01 10:00:00-03');