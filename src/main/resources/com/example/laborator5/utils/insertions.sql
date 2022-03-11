-- Users:

DELETE FROM users;

INSERT INTO users VALUES (1, 'Stefan', 'Farcasanu', 'farca', '97b904ca8f5bc2bd5c012f5e4159cfa8', NOW());
INSERT INTO users VALUES (2, 'Victor', 'Doroftei', 'vic', 'd8d3bcfffb223aabf73973451548b12e', NOW());
INSERT INTO users VALUES (3, 'Lavinia', 'Gavrilescu', 'lavi', '41ebdbf08c4e3ac414239ba9bb210446', NOW());
INSERT INTO users VALUES (4, 'Laura', 'Deac', 'laura', '680e89809965ec41e64dc7e447f175ab', NOW());
INSERT INTO users VALUES (5, 'Mihai', 'Dretcanu', 'mita', 'bae3d929b274a4cd35c38fe92f059f1a', NOW());
INSERT INTO users VALUES (6, 'Livius', 'Habuc', 'livius', '05bac2a162f5ccb5d72c92ea2fecdf9f', NOW());
INSERT INTO users VALUES (7, 'Andrei', 'Dina', 'deiu', 'ee6c722b1c0d8a08838c3410fd9025e6', NOW());
INSERT INTO users VALUES (8, 'Mihai', 'Corocaescu', 'coro', 'bd721a246bd2c364e6bc6c1daa4503de', NOW());

-- Friendships:

DELETE FROM friendships;

INSERT INTO friendships VALUES (1, 2, TIMESTAMP '2021-06-20 12:18');
INSERT INTO friendships VALUES (1, 3, TIMESTAMP '2022-01-13 09:22');
INSERT INTO friendships VALUES (1, 4, TIMESTAMP '2021-11-20 16:15');
INSERT INTO friendships VALUES (1, 5, TIMESTAMP '2021-12-30 08:44');
INSERT INTO friendships VALUES (1, 7, TIMESTAMP '2021-01-02 07:32');
INSERT INTO friendships VALUES (2, 3, TIMESTAMP '2021-08-24 12:00');
INSERT INTO friendships VALUES (2, 4, TIMESTAMP '2021-06-30 13:57');
INSERT INTO friendships VALUES (2, 5, TIMESTAMP '2021-07-20 13:56');
INSERT INTO friendships VALUES (2, 6, TIMESTAMP '2021-03-18 00:19');
INSERT INTO friendships VALUES (2, 8, TIMESTAMP '2021-04-04 23:04');
INSERT INTO friendships VALUES (3, 4, TIMESTAMP '2021-10-17 22:17');
INSERT INTO friendships VALUES (3, 5, TIMESTAMP '2021-12-25 09:23');
INSERT INTO friendships VALUES (4, 5, TIMESTAMP '2021-10-20 08:56');
INSERT INTO friendships VALUES (4, 6, TIMESTAMP '2021-04-21 13:21');
INSERT INTO friendships VALUES (5, 6, TIMESTAMP '2021-12-12 17:56');
INSERT INTO friendships VALUES (6, 7, TIMESTAMP '2021-10-01 18:24');
INSERT INTO friendships VALUES (7, 8, TIMESTAMP '2021-02-04 13:00');

-- Events:

DELETE FROM event;

INSERT INTO event VALUES (1, 'Cool Event', 1, 'It''s Cool!', TIMESTAMP '2022-01-20 12:00');
INSERT INTO event VALUES (2, 'BRC', 2, 'Rock Music Festival', TIMESTAMP '2022-01-21 12:00');
INSERT INTO event VALUES (3, 'Genesis', 2, 'Music Festival', TIMESTAMP '2022-02-02 12:00');
INSERT INTO event VALUES (4, 'Hermes 2022', 5, 'Student Hackathon', TIMESTAMP '2022-01-17 12:00');
INSERT INTO event VALUES (5, 'Car Meet', 7, 'Event for Car Enthusiasts', TIMESTAMP '2022-07-15 12:00');
INSERT INTO event VALUES (6, 'Cookie Party', 3, 'Let''s Eat!', TIMESTAMP '2022-01-17 12:00');
INSERT INTO event VALUES (7, 'Bake-Off', 8, 'Baking Pies for Charity', TIMESTAMP '2022-01-28 12:00');
INSERT INTO event VALUES (8, 'Marathon January 2022', 1, 'Let''s run for a noble cause!', TIMESTAMP '2022-01-20 12:00');

-- Event Participants:

DELETE FROM eventparticipants;

INSERT INTO eventparticipants VALUES (1, 1);
INSERT INTO eventparticipants VALUES (1, 2);
INSERT INTO eventparticipants VALUES (1, 3);
INSERT INTO eventparticipants VALUES (1, 7);
INSERT INTO eventparticipants VALUES (1, 5);

INSERT INTO eventparticipants VALUES (2, 2);
INSERT INTO eventparticipants VALUES (2, 1);
INSERT INTO eventparticipants VALUES (2, 3);
INSERT INTO eventparticipants VALUES (2, 4);
INSERT INTO eventparticipants VALUES (2, 8);
INSERT INTO eventparticipants VALUES (2, 7);

INSERT INTO eventparticipants VALUES (3, 2);
INSERT INTO eventparticipants VALUES (3, 8);

INSERT INTO eventparticipants VALUES (4, 5);

INSERT INTO eventparticipants VALUES (5, 7);
INSERT INTO eventparticipants VALUES (5, 1);
INSERT INTO eventparticipants VALUES (5, 4);
INSERT INTO eventparticipants VALUES (5, 6);
INSERT INTO eventparticipants VALUES (5, 2);

INSERT INTO eventparticipants VALUES (6, 3);
INSERT INTO eventparticipants VALUES (6, 1);
INSERT INTO eventparticipants VALUES (6, 2);

INSERT INTO eventparticipants VALUES (7, 8);
INSERT INTO eventparticipants VALUES (7, 6);

INSERT INTO eventparticipants VALUES (8, 1);
INSERT INTO eventparticipants VALUES (8, 8);
INSERT INTO eventparticipants VALUES (8, 6);

-- Profiles:

DELETE FROM profiles;

INSERT INTO profiles VALUES(1, 'Student at UBB', 'Rm. Valcea', DATE '2001-08-20', 'Formula 1');
INSERT INTO profiles VALUES(2, '2nd Year Computer Science Student', 'Suceava', DATE '2002-04-05', 'Rally & Airplanes');
INSERT INTO profiles VALUES(3, 'Student at UTCN', 'Suceava', DATE '2001-05-21', 'Reading Books');
INSERT INTO profiles VALUES(4, 'Computer Science Student', 'Cluj-Napoca', DATE '2001-05-14', 'Watching old movies');
INSERT INTO profiles VALUES(5, 'Student', 'Botosani', DATE '2001-01-21', 'Cooking');
INSERT INTO profiles VALUES(6, 'Student at FMI, IR', 'Alba-Iulia', DATE '2001-03-01', 'Travelling');
INSERT INTO profiles VALUES(7, 'Student in Bucharest', 'Tg. Jiu', DATE '2001-05-14', 'Car racing');
INSERT INTO profiles VALUES(8, 'Hello!', 'Suceava', DATE '2001-08-02', 'Watching Netflix');

-- Posts:

DELETE FROM posts;

INSERT INTO posts VALUES (1, 1, 'This is my first post!', TIMESTAMP '2021-01-12 13:56');
INSERT INTO posts VALUES (2, 2, 'Hello BUZZ!', TIMESTAMP '2021-01-10 10:02');
INSERT INTO posts VALUES (3, 7, 'I bought a new car!', TIMESTAMP '2021-01-11 20:17');
INSERT INTO posts VALUES (4, 8, 'I really love IntelliJ...', TIMESTAMP '2021-01-08 08:22');
INSERT INTO posts VALUES (5, 2, 'This app really looks great', TIMESTAMP '2021-01-12 13:11');
INSERT INTO posts VALUES (6, 1, 'Good morning', TIMESTAMP '2021-01-12 06:09');
INSERT INTO posts VALUES (7, 2, 'I hope you are all having a wonderful day!!!!!!!', TIMESTAMP '2021-01-12 13:56');
INSERT INTO posts VALUES (8, 3, 'I love this app!', TIMESTAMP '2021-01-08 15:59');
INSERT INTO posts VALUES (9, 3, 'I went on a fishing trip', TIMESTAMP '2021-01-05 08:56');
INSERT INTO posts VALUES (10, 4, 'I can''t sleep tonight...', TIMESTAMP '2021-01-02 18:51');
INSERT INTO posts VALUES (11, 4, 'This is my first post!', TIMESTAMP '2021-01-12 13:56');
INSERT INTO posts VALUES (12, 5, 'Hello friends!', TIMESTAMP '2021-01-12 13:56');
INSERT INTO posts VALUES (13, 5, 'Oh no...the exam session is starting...', TIMESTAMP '2021-01-13 17:18');
INSERT INTO posts VALUES (14, 6, 'Happy New Year 2022!!!', TIMESTAMP '2021-01-01 00:02');
INSERT INTO posts VALUES (15, 6, 'Just 3 more weeks until the holiday', TIMESTAMP '2021-01-14 18:49');

-- Requests:

DELETE FROM requests;

INSERT INTO requests VALUES (1, 1, 8, TIMESTAMP '2021-01-10 07:08', 'pending');
INSERT INTO requests VALUES (2, 6, 1, TIMESTAMP '2021-01-12 14:56', 'pending');
INSERT INTO requests VALUES (3, 2, 7, TIMESTAMP '2021-01-11 12:35', 'pending');
INSERT INTO requests VALUES (4, 3, 2, TIMESTAMP '2021-01-05 10:09', 'pending');
INSERT INTO requests VALUES (5, 4, 2, TIMESTAMP '2021-01-09 13:39', 'pending');
INSERT INTO requests VALUES (6, 7, 5, TIMESTAMP '2021-01-12 06:57', 'pending');
INSERT INTO requests VALUES (7, 8, 3, TIMESTAMP '2021-01-13 22:33', 'pending');
INSERT INTO requests VALUES (8, 3, 7, TIMESTAMP '2021-01-14 23:12', 'pending');
INSERT INTO requests VALUES (9, 8, 5, TIMESTAMP '2021-01-02 13:22', 'pending');
INSERT INTO requests VALUES (10, 2, 6, TIMESTAMP '2021-01-07 09:55', 'pending');

-- Message:

DELETE FROM messagedata;
DELETE FROM messagereceivers;

insert into public.messagedata (id, from, message, date, originalmessage)
values  (1, 2, 'Servus dragilor!', '2022-01-14 20:35:08.063940', null),
        (2, 3, 'Salutari!!!', '2022-01-14 20:35:29.978432', 1),
        (3, 3, 'Ce faceti?', '2022-01-14 20:35:33.262401', 1),
        (4, 3, 'Ce mai face apartamentul 18?', '2022-01-14 20:36:02.272259', 1),
        (5, 4, 'Helloooo!!!', '2022-01-14 20:36:24.368237', 4),
        (6, 4, 'Bem o ciocolata calda diseara?', '2022-01-14 20:36:49.549009', 4),
        (7, 3, 'Sigur ca da, cum sa nu?', '2022-01-14 20:37:36.066684', 1),
        (8, 4, 'Stefan ce face? Intarzie iar', '2022-01-14 20:38:02.739393', 4),
        (9, 1, 'Sunt aici, am ajuns si eu!!!', '2022-01-14 20:41:37.554745', 8),
        (10, 4, 'Tu cu ce te imbraci diseara la seara de ciocolata calda?', '2022-01-14 20:42:33.425046', null),
        (11, 1, 'Cu un pulover, pentru ca probabil o sa fie frig', '2022-01-14 20:43:04.524746', 10),
        (12, 1, 'Sau un hanorac, inca nu m-am decis', '2022-01-14 20:43:17.313441', 10),
        (13, 4, 'Am inteles, okkk', '2022-01-14 20:43:21.520828', 10),
        (14, 2, 'Hello, imi poti imprumuta si mie fierul de calcat, te rog?', '2022-01-14 20:55:14.761884', null),
        (15, 3, 'Normal ca da', '2022-01-14 20:55:37.094266', 14),
        (16, 3, 'Vii tu la mine sa-l iei sau ti-l aduc eu?', '2022-01-14 20:55:47.012939', 14),
        (17, 2, 'Hai ca vin eu, ma pornesc in 5 minute', '2022-01-14 20:56:02.103660', 16);
		
insert into public.messagereceivers (message, to)
values  (1, 3),
        (1, 4),
        (1, 1),
        (2, 4),
        (2, 1),
        (2, 2),
        (3, 4),
        (3, 1),
        (3, 2),
        (4, 4),
        (4, 1),
        (4, 2),
        (5, 1),
        (5, 2),
        (5, 3),
        (6, 1),
        (6, 2),
        (6, 3),
        (7, 4),
        (7, 1),
        (7, 2),
        (8, 1),
        (8, 2),
        (8, 3),
        (9, 2),
        (9, 3),
        (9, 4),
        (10, 1),
        (11, 4),
        (12, 4),
        (13, 1),
        (14, 3),
        (15, 2),
        (16, 2),
        (17, 3);
