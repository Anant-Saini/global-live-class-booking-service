--
-- PostgreSQL database dump
--
-- Dumped from database version 18.3 (Homebrew)
-- Dumped by pg_dump version 18.3 (Homebrew)

SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

--
-- Data for Name: courses; Type: TABLE DATA; Schema: public; Owner: anant-saini
--

INSERT INTO public.courses (id, title, description) VALUES (2, 'Advanced Java Architecture', 'This course covers everything from JVM internals and Garbage Collection to Spring Boot internals, microservices orchestration with Kubernetes, and advanced design patterns like CQRS and Event Sourcing.');
INSERT INTO public.courses (id, title, description) VALUES (1, 'Python 3: Masterclass', 'Updated syllabus: Now includes FastAPI and Data Science basics with NumPy.');
INSERT INTO public.courses (id, title, description) VALUES (3, 'Full-Stack Web Development with Spring Boot & React', 'Learn to build scalable, production-ready web applications. This course covers RESTful API design with Spring Boot, JPA/Hibernate for persistence, and building dynamic user interfaces with React and Redux.');
INSERT INTO public.courses (id, title, description) VALUES (4, 'Data Science Fundamentals: From Zero to Hero', 'An intensive course on data analysis and visualization. Master Python libraries including Pandas for data manipulation, Matplotlib for plotting, and Scikit-Learn for implementing basic machine learning models like linear regression and classification.');


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: anant-saini
--

INSERT INTO public.users (id, name, role, timezone_id) VALUES (1, 'Prof. Shray', 'TEACHER', 'Asia/Kolkata');
INSERT INTO public.users (id, name, role, timezone_id) VALUES (2, 'Vishakha Verma', 'PARENT', 'Asia/Kolkata');
INSERT INTO public.users (id, name, role, timezone_id) VALUES (4, 'Lolita Singh', 'PARENT', 'Europe/London');
INSERT INTO public.users (id, name, role, timezone_id) VALUES (5, 'Takaichi Sanakyo', 'PARENT', 'Asia/Tokyo');
INSERT INTO public.users (id, name, role, timezone_id) VALUES (3, 'Thomas Pete', 'PARENT', 'America/St_Johns');
INSERT INTO public.users (id, name, role, timezone_id) VALUES (6, 'Enrique Daniels', 'TEACHER', 'Europe/Rome');
INSERT INTO public.users (id, name, role, timezone_id) VALUES (7, 'Pietro Rodreigus', 'TEACHER', 'America/Sao_Paulo');
INSERT INTO public.users (id, name, role, timezone_id) VALUES (8, 'Thomas Christian', 'TEACHER', 'Africa/Johannesburg');


--
-- Data for Name: offerings; Type: TABLE DATA; Schema: public; Owner: anant-saini
--

INSERT INTO public.offerings (id, course_id, teacher_id, name, price, currency, total_seats, booked_seats, version) VALUES (2, 4, 7, '8 Weekend Crash Course: Data Science Superstar', 29.99, 'USD', 8, 0, 0);
INSERT INTO public.offerings (id, course_id, teacher_id, name, price, currency, total_seats, booked_seats, version) VALUES (4, 3, 8, '5 Day Summer Boot Camp: Become Entry Level Full Stack Developer', 99.10, 'USD', 2, 0, 0);
INSERT INTO public.offerings (id, course_id, teacher_id, name, price, currency, total_seats, booked_seats, version) VALUES (5, 3, 8, '5 Day Summer Boot Camp: Become Entry Level Full Stack Developer', 99.10, 'USD', 5, 0, 0);
INSERT INTO public.offerings (id, course_id, teacher_id, name, price, currency, total_seats, booked_seats, version) VALUES (1, 1, 1, 'Summer Special: Python Basics', 49.99, 'USD', 5, 1, 1);
INSERT INTO public.offerings (id, course_id, teacher_id, name, price, currency, total_seats, booked_seats, version) VALUES (3, 2, 7, '2 Day Advanced Java Workshop', 0.00, 'USD', 10, 2, 2);
INSERT INTO public.offerings (id, course_id, teacher_id, name, price, currency, total_seats, booked_seats, version) VALUES (6, 4, 6, '4 Day Data Science Camp: Become a Data Star!', 20.10, 'USD', 2, 2, 2);


--
-- Data for Name: bookings; Type: TABLE DATA; Schema: public; Owner: anant-saini
--

INSERT INTO public.bookings (id, user_id, offering_id, booked_at, booking_price, currency) VALUES (1, 2, 3, '2026-05-31 22:51:48.005425+05:30', 0.00, 'USD');
INSERT INTO public.bookings (id, user_id, offering_id, booked_at, booking_price, currency) VALUES (2, 4, 1, '2026-05-31 22:55:50.589759+05:30', 49.99, 'USD');
INSERT INTO public.bookings (id, user_id, offering_id, booked_at, booking_price, currency) VALUES (3, 5, 3, '2026-05-31 23:04:02.624997+05:30', 0.00, 'USD');
INSERT INTO public.bookings (id, user_id, offering_id, booked_at, booking_price, currency) VALUES (4, 4, 6, '2026-05-31 23:15:39.425635+05:30', 20.10, 'USD');
INSERT INTO public.bookings (id, user_id, offering_id, booked_at, booking_price, currency) VALUES (5, 5, 6, '2026-05-31 23:26:09.296054+05:30', 20.10, 'USD');


--
-- Data for Name: sessions; Type: TABLE DATA; Schema: public; Owner: anant-saini
--

INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (1, 3, 7, '2026-06-15 22:30:00+05:30', '2026-06-16 00:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (2, 3, 7, '2026-06-16 22:30:00+05:30', '2026-06-17 00:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (3, 3, 7, '2026-07-10 18:30:00+05:30', '2026-07-10 21:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (4, 3, 7, '2026-07-11 18:30:00+05:30', '2026-07-11 21:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (5, 2, 7, '2026-11-08 06:30:00+05:30', '2026-11-08 09:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (6, 2, 7, '2026-11-15 06:30:00+05:30', '2026-11-15 09:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (7, 2, 7, '2026-11-22 06:30:00+05:30', '2026-11-22 09:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (8, 1, 1, '2026-07-15 09:00:00+05:30', '2026-07-15 11:00:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (9, 1, 1, '2026-07-16 09:00:00+05:30', '2026-07-16 11:00:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (10, 3, 7, '2026-07-15 09:30:00+05:30', '2026-07-15 10:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (11, 3, 7, '2026-07-15 10:30:00+05:30', '2026-07-15 12:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (12, 6, 6, '2026-07-20 19:30:00+05:30', '2026-07-20 21:00:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (13, 6, 6, '2026-07-21 19:30:00+05:30', '2026-07-21 20:30:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (14, 6, 6, '2026-07-22 19:30:00+05:30', '2026-07-22 21:00:00+05:30');
INSERT INTO public.sessions (id, offering_id, teacher_id, start_time, end_time) VALUES (15, 6, 6, '2026-07-23 19:30:00+05:30', '2026-07-23 20:30:00+05:30');


--
-- Name: bookings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anant-saini
--

SELECT pg_catalog.setval('public.bookings_id_seq', 6, true);


--
-- Name: courses_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anant-saini
--

SELECT pg_catalog.setval('public.courses_id_seq', 4, true);


--
-- Name: offerings_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anant-saini
--

SELECT pg_catalog.setval('public.offerings_id_seq', 6, true);


--
-- Name: sessions_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anant-saini
--

SELECT pg_catalog.setval('public.sessions_id_seq', 15, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: anant-saini
--

SELECT pg_catalog.setval('public.users_id_seq', 8, true);


--
-- PostgreSQL database dump complete
--

