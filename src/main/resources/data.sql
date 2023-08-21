-- LOAD DATAS
INSERT INTO campaigns(title, start_date, end_date)
VALUES ('Free Shipping', '2023-05-23', null),
       ('20% OFF EVERYTHING', '2023-08-23', null),
       ('CHRISTMAS 15% OFF', '2022-12-23', '2022-12-26');

INSERT INTO status(status_name)
VALUES ('ISSUED'),
       ('REDEEMED'),
       ('SENT');

INSERT INTO vouchers(code, campaign_id, status_id)
VALUES ('ce90dfbd-ff2a-4e43-941c-ea26c04c6e7e', 1, 1),
       ('d490e225-8271-4093-a047-1598ee6b4c1b', 1, 2),
       ('44f255bd-0fba-476e-8dde-0023fd59b4e1', 2, 1),
       ('3243684c-dc57-48c1-aff3-4da430d6f5b3', 3, 1);