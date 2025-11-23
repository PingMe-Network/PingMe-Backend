-- ===========================================================================================================
--
-- Bước 1: Drop DB hiện tại
-- Bước 2: Tạo lại DB đó (tên: PingMe_DB)
-- Bước 3: Chạy lại application để nó tự động tạo bảng
-- Bước 4: Chạy script sql bên dưới để nó thêm dữ liệu
--
-- ===========================================================================================================
-- Tạo Role
-- 2 Role:
-- + MEMBER
-- + ADMIN: cho phép truy cập trang quản lý trên frontend
-- ===========================================================================================================
INSERT INTO `roles` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `description`, `name`)
VALUES (1, 1, '2025-11-23 10:37:30.000000', 'anonymousUser', '2025-11-23 10:37:33.000000', 'anonymousUser',
        'Admin Role', 'ADMIN'),
       (2, 1, '2025-11-23 10:38:08.000000', 'anonymousUser', '2025-11-23 10:38:09.000000', 'anonymousUser',
        'Member Role', 'MEMBER');

-- ===========================================================================================================
-- Tạo User
-- Tất cả User tạo example ra đều có pass là Test123@
--
-- Các user có quyền ADMIN:
-- HuynhDucPhu2502@gmail.com / tranlehuygia2210@gmail.com / atvn15@gmail.com / shibalnq2112@gmail.com
--
-- Các user còn lại:
-- Test1@gmail.com / Test2@gmail.com / ... / Test10@gmail.com
-- ===========================================================================================================
INSERT INTO `users` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `address`, `auth_provider`,
                     `avatar_url`, `dob`, `email`, `gender`, `name`, `password`, `status`, `role_id`)
VALUES (1, 1, '2025-11-18 22:48:59.615037', 'anonymousUser', '2025-11-18 23:05:04.360119', 'Test1@gmail.com',
        '56/9 Lạc Long Quân, P.5, Q.11, TP.HCM', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test1@gmail.com', '2025-07-17', 'Test1@gmail.com',
        'MALE', 'Phạm Tuấn Khoa', '$2a$10$6RAjVIU8XxvwJ2ewoDPoAeCUlKJqH7gRHaashHrwkwVv0WKm5/z5e', 'OFFLINE', 2),
       (2, 1, '2025-11-18 22:49:47.075581', 'anonymousUser', '2025-11-18 23:05:42.434655', 'Test2@gmail.com',
        '10/9 Lê Duẩn, P.Thắng Nhất, TP.Vũng Tàu', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test2@gmail.com', '2025-09-10', 'Test2@gmail.com',
        'FEMALE', 'Trần Thị Thu Trang', '$2a$10$GiN9ZRoCKZDMmyjQ08LjAOGcLUDISkywdkDdMEsOc1ayTSctpAP82', 'OFFLINE', 2),
       (3, 1, '2025-11-18 22:51:09.449569', 'anonymousUser', '2025-11-18 23:06:20.796721', 'Test3@gmail.com',
        '45 Nguyễn Tất Thành, P.Vĩnh Thọ, Nha Trang', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test3@gmail.com', '2025-04-30', 'Test3@gmail.com',
        'MALE', 'Bùi Anh Quân', '$2a$10$ND9jQ7/ybwMp.8XBIiw4k.hJtg2fvxqusVTA/c7AWnqZjN/2yucgG', 'OFFLINE', 2),
       (4, 1, '2025-11-18 22:53:00.105600', 'anonymousUser', '2025-11-18 23:09:24.414137', 'Test4@gmail.com',
        '68/2 Nguyễn Văn Trỗi, P.8, Phú Nhuận, TP.HCM', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test4@gmail.com', '2001-04-17', 'Test4@gmail.com',
        'FEMALE', 'Phạm Bảo Ngọc', '$2a$10$.L99JYyUqk4DIKhrvUfv4OAaQw3DpGgdlUA9PCIPIg1u55RX.mMaK', 'OFFLINE', 2),
       (5, 1, '2025-11-18 22:54:09.992369', 'anonymousUser', '2025-11-18 23:09:54.900420', 'Test5@gmail.com',
        '112 Trần Hưng Đạo, P.Mỹ Bình, Long Xuyên, An Giang', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test5@gmail.com', '2001-01-09', 'Test5@gmail.com',
        'MALE', 'Võ Nhật Long', '$2a$10$VdLRPrwdjcO.J0Vq.MO2Iu2tNd9flFZ6tFasACEMOmTT0Rb96nrB2', 'OFFLINE', 2),
       (6, 1, '2025-11-18 22:57:19.534273', 'anonymousUser', '2025-11-18 23:16:32.044163', 'Test6@gmail.com',
        '33 Nguyễn Thị Minh Khai, P.Bến Thành, Q.1, TP.HCM', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test6@gmail.com', '2000-09-13', 'Test6@gmail.com',
        'FEMALE', 'Trần Quế My', '$2a$10$avKIRLICxePlrPnThNhALO0mltduU4OcgOoUnTmqXzBWT6Dv.Vwce', 'OFFLINE', 2),
       (7, 1, '2025-11-18 22:58:40.727272', 'anonymousUser', '2025-11-18 23:19:05.678958', 'Test7@gmail.com',
        '23/7 Hoàng Hoa Thám, P.6, Q.Bình Thạnh, TP.HCM', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test7@gmail.com', '2025-06-21', 'Test7@gmail.com',
        'MALE', 'Đặng Xuân Nam', '$2a$10$/R47zjqACeJvZUcgqvEV1uJtkGz96KGfKHM6L/.qVJcqNnOeBwUbi', 'OFFLINE', 2),
       (8, 1, '2025-11-18 22:59:51.828506', 'anonymousUser', '2025-11-18 23:21:32.273121', 'Test8@gmail.com',
        '50 Tôn Đức Thắng, P.Bến Nghé, Q.1, TP.HCM', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test8@gmail.com', '2003-10-18', 'Test8@gmail.com',
        'FEMALE', 'Phạm Hải Yến', '$2a$10$L5q7vVIc4cELw6cJ.pOYOOhMGBmnTB3kkbgDAdknKwapXlMqdV19K', 'OFFLINE', 2),
       (9, 1, '2025-11-18 23:03:47.999784', 'anonymousUser', '2025-11-18 23:23:14.336209', 'Test9@gmail.com',
        '18A Hà Huy Tập, P.Tân Lợi, TP.Buôn Ma Thuột', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test9@gmail.com', '2000-12-03', 'Test9@gmail.com',
        'MALE', 'Nguyễn Tấn Tín', '$2a$10$LXf6qogfe2XJr.BM7Of4YO3P8InO8Fgs2Up9rsn7FjZ4Mx8hZzyfG', 'OFFLINE', 2),
       (10, 1, '2025-11-18 23:04:34.924416', 'anonymousUser', '2025-11-18 23:23:48.527676', 'Test10@gmail.com',
        '44/7 Phan Đăng Lưu, P.3, Q.Bình Thạnh, TP.HCM', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test10@gmail.com', '2002-06-25', 'Test10@gmail.com',
        'FEMALE', 'Đỗ Hoài Như', '$2a$10$xTxJpev9CJ8QxrXpTc/RmO7KayY9lDCakEFKxKossIoyneB0Oxq16', 'OFFLINE', 2),
       (11, 1, '2025-11-18 23:06:50.393330', 'anonymousUser', '2025-11-18 23:36:04.009499', 'huynhducphu2502@gmail.com',
        '120 Xóm Chiếu, Phường 14, Quận 4, TPHCM', 'LOCAL',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/huynhducphu2502@gmail.com', '2003-02-25',
        'huynhducphu2502@gmail.com', 'MALE', 'Huỳnh Đức Phú',
        '$2a$10$qVGedE0iLfFMkBhsFqvNte571l38ZbQLO8luBK9xF0qbKvaclm7tW', 'OFFLINE', 1),
       (12, 1, '2025-11-23 10:43:51.329198', 'anonymousUser', '2025-11-23 10:43:51.329198', 'anonymousUser', '',
        'LOCAL', NULL, '2025-11-01', 'tranlehuygia2210@gmail.com', 'MALE', 'Lê Trần Gia Huy',
        '$2a$10$aymbUZwxdgH7YdEVd51Qd.DQV6S2x3wValMrEwpPkoRr1p1c/v9bW', NULL, 1),
       (13, 1, '2025-11-23 10:44:34.041359', 'anonymousUser', '2025-11-23 10:44:34.041359', 'anonymousUser', '',
        'LOCAL', NULL, '2025-11-01', 'atvn15@gmail.com', 'MALE', 'Nguyễn Anh Tùng',
        '$2a$10$5qVqbH5DNRDR0jqihcBa6.QxGSKT1VfIXf.NVUR1CrwCTY.71LffK', NULL, 1),
       (14, 1, '2025-11-23 10:45:03.711495', 'anonymousUser', '2025-11-23 10:45:03.711495', 'anonymousUser', '',
        'LOCAL', NULL, '2025-11-01', 'shibalnq2112@gmail.com', 'MALE', 'Lê Nguyễn Quỳnh',
        '$2a$10$rjodqTNy1AaT.PXOzdIqt.MtzO8mhbbSQ8XK/ki2MGjoq.G4ZzgX2', NULL, 1),
       (15, 1, '2025-11-23 11:00:28.015457', 'anonymousUser', '2025-11-23 11:00:28.015457', 'anonymousUser', '',
        'LOCAL', NULL, '2025-11-01', 'nhpel11@gmail.com', 'MALE', 'Phạm Ngọc Hùng',
        '$2a$10$AZ15eoSTJsSYffcY7eGsNeA5MovUgMlzooG5DKQLcE.6tFPyAA5pq', NULL, 1);

-- ===========================================================================================================
-- Tạo mối quan hệ giữa các User (chủ yếu là tài khoản HuynhDucPhu2502@gmail.com với 9 user TestX@gmail.com)
-- ===========================================================================================================
INSERT INTO `friendships` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `status`,
                           `user_high_id`, `user_low_id`, `user_a_id`, `user_b_id`)
VALUES (1, 1, '2025-11-18 23:07:19.344422', 'Test3@gmail.com', '2025-11-18 23:24:46.665294',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 3, 3, 11),
       (2, 1, '2025-11-18 23:07:38.791005', 'Test1@gmail.com', '2025-11-18 23:24:46.171186',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 1, 1, 11),
       (3, 1, '2025-11-18 23:08:07.691509', 'Test2@gmail.com', '2025-11-18 23:24:45.639012',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 2, 2, 11),
       (4, 1, '2025-11-18 23:10:04.110379', 'Test5@gmail.com', '2025-11-18 23:24:44.638708',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 5, 5, 11),
       (5, 1, '2025-11-18 23:10:29.852871', 'Test4@gmail.com', '2025-11-18 23:24:44.107127',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 4, 4, 11),
       (6, 1, '2025-11-18 23:18:22.722881', 'Test6@gmail.com', '2025-11-18 23:24:43.524639',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 6, 6, 11),
       (7, 1, '2025-11-18 23:19:29.490937', 'Test7@gmail.com', '2025-11-18 23:24:42.997787',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 7, 7, 11),
       (8, 1, '2025-11-18 23:21:02.702739', 'Test8@gmail.com', '2025-11-18 23:24:42.401438',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 8, 8, 11),
       (9, 1, '2025-11-18 23:24:00.662050', 'Test10@gmail.com', '2025-11-18 23:24:41.848693',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 10, 10, 11),
       (10, 1, '2025-11-18 23:24:18.596440', 'Test9@gmail.com', '2025-11-18 23:24:41.084521',
        'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 9, 9, 11);

-- ===========================================================================================================
-- Tạo phòng chat giữa HuynhDucPhu2502@gmail.com với 9 user TestX@gmail.com
-- ===========================================================================================================
INSERT INTO `rooms` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `direct_key`,
                     `last_message_at`, `name`, `room_img_url`, `room_type`, `theme`, `last_message_id`)
VALUES (1, 1, '2025-11-23 10:52:26.205934', 'huynhducphu2502@gmail.com', '2025-11-23 11:19:44.279932',
        'huynhducphu2502@gmail.com', '1_11', '2025-11-23 11:13:09.397749', NULL, NULL, 'DIRECT', 'OCEAN', NULL),
       (2, 1, '2025-11-23 10:52:33.953105', 'huynhducphu2502@gmail.com', '2025-11-23 11:19:00.521773',
        'huynhducphu2502@gmail.com', '2_11', '2025-11-23 11:19:00.517775', NULL, NULL, 'DIRECT', NULL, NULL),
       (3, 1, '2025-11-23 10:52:41.242709', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:41.242709',
        'huynhducphu2502@gmail.com', '3_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (4, 1, '2025-11-23 10:52:45.011259', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:45.011259',
        'huynhducphu2502@gmail.com', '4_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (5, 1, '2025-11-23 10:52:51.836255', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:51.836255',
        'huynhducphu2502@gmail.com', '5_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (6, 1, '2025-11-23 10:52:56.270817', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:56.270817',
        'huynhducphu2502@gmail.com', '6_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (7, 1, '2025-11-23 10:53:01.048623', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:01.048623',
        'huynhducphu2502@gmail.com', '7_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (8, 1, '2025-11-23 10:53:05.071792', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:05.071792',
        'huynhducphu2502@gmail.com', '8_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (9, 1, '2025-11-23 10:53:12.400313', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:12.400313',
        'huynhducphu2502@gmail.com', '9_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (10, 1, '2025-11-23 10:53:16.699416', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:16.699416',
        'huynhducphu2502@gmail.com', '10_11', NULL, NULL, NULL, 'DIRECT', NULL, NULL),
       (11, 1, '2025-11-23 11:20:51.173246', 'huynhducphu2502@gmail.com', '2025-11-23 11:21:31.391949',
        'huynhducphu2502@gmail.com', NULL, '2025-11-23 11:21:31.390418', '3 thằng bạn',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/group-images/0e6b9276-1c82-44c2-91e9-28de91e74406.avif',
        'GROUP', 'ROSE', NULL);

INSERT INTO `room_participants` (`active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `last_read_at`,
                                 `last_read_message_id`, `role`, `room_id`, `user_id`)
VALUES (1, '2025-11-23 10:52:26.272324', 'huynhducphu2502@gmail.com', '2025-11-23 11:12:56.463561', 'Test1@gmail.com',
        '2025-11-23 11:12:56.461609', 23, 'MEMBER', 1, 1),
       (1, '2025-11-23 10:52:26.257516', 'huynhducphu2502@gmail.com', '2025-11-23 11:13:09.401746',
        'huynhducphu2502@gmail.com', '2025-11-23 11:13:09.397749', 24, 'MEMBER', 1, 11),
       (1, '2025-11-23 10:52:33.962722', 'huynhducphu2502@gmail.com', '2025-11-23 11:16:51.016870', 'Test2@gmail.com',
        '2025-11-23 11:16:51.013871', 28, 'MEMBER', 2, 2),
       (1, '2025-11-23 10:52:33.956090', 'huynhducphu2502@gmail.com', '2025-11-23 11:19:00.521773',
        'huynhducphu2502@gmail.com', '2025-11-23 11:19:00.517775', 31, 'MEMBER', 2, 11),
       (1, '2025-11-23 10:52:41.249423', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:41.249423',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 3, 3),
       (1, '2025-11-23 10:52:41.245680', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:41.245680',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 3, 11),
       (1, '2025-11-23 10:52:45.017269', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:45.017269',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 4, 4),
       (1, '2025-11-23 10:52:45.014266', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:45.014266',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 4, 11),
       (1, '2025-11-23 10:52:51.843246', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:51.843246',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 5, 5),
       (1, '2025-11-23 10:52:51.839246', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:51.839246',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 5, 11),
       (1, '2025-11-23 10:52:56.279047', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:56.279047',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 6, 6),
       (1, '2025-11-23 10:52:56.274958', 'huynhducphu2502@gmail.com', '2025-11-23 10:52:56.274958',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 6, 11),
       (1, '2025-11-23 10:53:01.054546', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:01.054546',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 7, 7),
       (1, '2025-11-23 10:53:01.052660', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:01.052660',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 7, 11),
       (1, '2025-11-23 10:53:05.081600', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:05.081600',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 8, 8),
       (1, '2025-11-23 10:53:05.077600', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:05.077600',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 8, 11),
       (1, '2025-11-23 10:53:12.405306', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:12.405306',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 9, 9),
       (1, '2025-11-23 10:53:12.403308', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:12.403308',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 9, 11),
       (1, '2025-11-23 10:53:16.705068', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:16.705068',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 10, 10),
       (1, '2025-11-23 10:53:16.702064', 'huynhducphu2502@gmail.com', '2025-11-23 10:53:16.702064',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 10, 11),
       (1, '2025-11-23 11:20:51.197864', 'huynhducphu2502@gmail.com', '2025-11-23 11:22:17.086133',
        'huynhducphu2502@gmail.com', NULL, NULL, 'ADMIN', 11, 1),
       (1, '2025-11-23 11:20:51.203919', 'huynhducphu2502@gmail.com', '2025-11-23 11:20:51.203919',
        'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 11, 9),
       (1, '2025-11-23 11:20:51.185896', 'huynhducphu2502@gmail.com', '2025-11-23 11:21:31.391949',
        'huynhducphu2502@gmail.com', '2025-11-23 11:21:31.390418', 34, 'OWNER', 11, 11);

INSERT INTO `messages` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `client_msg_id`,
                        `content`, `type`, `room_id`, `sender_id`)
VALUES (1, 1, '2025-11-23 11:09:19.509451', 'Test1@gmail.com', '2025-11-23 11:09:19.509451', 'Test1@gmail.com',
        _binary 0xe51b61ad9672458198bffb8d11fcf3a7, 'Ê nay làm gì đó?', 'TEXT', 1, 1),
       (2, 1, '2025-11-23 11:09:27.053270', 'Test1@gmail.com', '2025-11-23 11:09:27.053270', 'Test1@gmail.com',
        _binary 0xa1b30bfb9eb94e7785ba82d2b0d89dcc, 'Tính hỏi xem rảnh không.', 'TEXT', 1, 1),
       (3, 1, '2025-11-23 11:09:33.225105', 'huynhducphu2502@gmail.com', '2025-11-23 11:09:33.225105',
        'huynhducphu2502@gmail.com', _binary 0x3bef23d0fd684b8aa286dfd3070a2fa7, 'Rảnh nè.', 'TEXT', 1, 11),
       (4, 1, '2025-11-23 11:09:43.882997', 'Test1@gmail.com', '2025-11-23 11:09:43.882997', 'Test1@gmail.com',
        _binary 0xa9fbdaac12d04afea407a342990af72d, 'Vậy đi ăn không?', 'TEXT', 1, 1),
       (5, 1, '2025-11-23 11:09:50.055509', 'Test1@gmail.com', '2025-11-23 11:09:50.055509', 'Test1@gmail.com',
        _binary 0x8b44ee2e78f642b1b39a099b09be0eab, 'Tui đói quá.', 'TEXT', 1, 1),
       (6, 1, '2025-11-23 11:09:58.033089', 'huynhducphu2502@gmail.com', '2025-11-23 11:09:58.033089',
        'huynhducphu2502@gmail.com', _binary 0x2a61af1fd65c41f8a005a932b7d3796f, 'Đi luôn', 'TEXT', 1, 11),
       (7, 1, '2025-11-23 11:10:00.977484', 'huynhducphu2502@gmail.com', '2025-11-23 11:10:00.977484',
        'huynhducphu2502@gmail.com', _binary 0x486df899ff564312aa3ccfbe5cbc20c0, 'Muốn ăn gì?', 'TEXT', 1, 11),
       (8, 1, '2025-11-23 11:10:33.915304', 'Test1@gmail.com', '2025-11-23 11:10:33.915304', 'Test1@gmail.com',
        _binary 0xbf4455fb21384c4da5d9ad33c2df3e86, 'Tui thèm đồ Nhật.', 'TEXT', 1, 1),
       (9, 1, '2025-11-23 11:10:41.600705', 'huynhducphu2502@gmail.com', '2025-11-23 11:10:41.600705',
        'huynhducphu2502@gmail.com', _binary 0x1a02dcb9ab9c4e48860ab066f5c7ae0f, 'Sushi hay ramen?', 'TEXT', 1, 11),
       (10, 1, '2025-11-23 11:10:45.620330', 'huynhducphu2502@gmail.com', '2025-11-23 11:10:45.620330',
        'huynhducphu2502@gmail.com', _binary 0xeee01e5f5c194692a7d1c63b69119dd8, 'Hay cả hai?', 'TEXT', 1, 11),
       (11, 1, '2025-11-23 11:10:54.521617', 'Test1@gmail.com', '2025-11-23 11:10:54.521617', 'Test1@gmail.com',
        _binary 0xb5a6ed7c919f4e1682a15ecdf0498f6b, 'Sushi trước', 'TEXT', 1, 1),
       (12, 1, '2025-11-23 11:10:59.053008', 'Test1@gmail.com', '2025-11-23 11:10:59.053008', 'Test1@gmail.com',
        _binary 0xd35d426301d24ee6ae4b3ea4df2d9755, 'Rồi tính tiếp.', 'TEXT', 1, 1),
       (13, 1, '2025-11-23 11:11:06.754011', 'huynhducphu2502@gmail.com', '2025-11-23 11:11:06.754011',
        'huynhducphu2502@gmail.com', _binary 0x468999e94f49470a91b89bf1ed08817f, 'Ok', 'TEXT', 1, 11),
       (14, 1, '2025-11-23 11:11:09.969584', 'huynhducphu2502@gmail.com', '2025-11-23 11:11:09.969584',
        'huynhducphu2502@gmail.com', _binary 0x6aa4fdd0d56d476f831bc06113a8ec95, 'Đặt bàn trước không?', 'TEXT', 1, 11),
       (15, 1, '2025-11-23 11:11:31.438837', 'Test1@gmail.com', '2025-11-23 11:11:31.438837', 'Test1@gmail.com',
        _binary 0x29ff96d40070469e839dc5920a602e6a, 'Chắc cần', 'TEXT', 1, 1),
       (16, 1, '2025-11-23 11:11:35.985998', 'Test1@gmail.com', '2025-11-23 11:11:35.985998', 'Test1@gmail.com',
        _binary 0xb5497f7b2aaa449785d732d4705e5c86, 'Tại quán đó hơi đông người', 'TEXT', 1, 1),
       (17, 1, '2025-11-23 11:11:45.431724', 'huynhducphu2502@gmail.com', '2025-11-23 11:11:45.431724',
        'huynhducphu2502@gmail.com', _binary 0x7f9a88c731d646a4b08e4257f0c1fe05, 'Để tui đặt cho', 'TEXT', 1, 11),
       (18, 1, '2025-11-23 11:12:00.200273', 'huynhducphu2502@gmail.com', '2025-11-23 11:12:00.200273',
        'huynhducphu2502@gmail.com', _binary 0x3ace6d1883e545d2bbbe43c3a791c31a, 'Nice', 'TEXT', 1, 11),
       (19, 1, '2025-11-23 11:12:08.120651', 'huynhducphu2502@gmail.com', '2025-11-23 11:12:08.120651',
        'huynhducphu2502@gmail.com', _binary 0xeda3d2e1aa234111b07b99c3229d43b2, 'Đặt xong rồi nè nha', 'TEXT', 1, 11),
       (20, 1, '2025-11-23 11:12:24.358342', 'huynhducphu2502@gmail.com', '2025-11-23 11:12:24.358342',
        'huynhducphu2502@gmail.com', _binary 0xe177f05d4d51465c87ba2bebe5e8f5d9, 'Nhanh dữ', 'TEXT', 1, 11),
       (21, 1, '2025-11-23 11:12:34.964411', 'huynhducphu2502@gmail.com', '2025-11-23 11:12:34.964411',
        'huynhducphu2502@gmail.com', _binary 0x85f60e88c43c4a1a8c07f4488589b2ad, 'Chắc tại có bàn trống hả', 'TEXT', 1,
        11),
       (22, 1, '2025-11-23 11:12:50.549762', 'Test1@gmail.com', '2025-11-23 11:12:50.549762', 'Test1@gmail.com',
        _binary 0x1db10fba4192419e917403aa7dee0ea9, 'Ok ngon mà tính mấy giờ đi', 'TEXT', 1, 1),
       (23, 1, '2025-11-23 11:12:56.461609', 'Test1@gmail.com', '2025-11-23 11:12:56.461609', 'Test1@gmail.com',
        _binary 0xb475a50ceac04713b9e734388084b04f, 'Thôi đi giờ đi', 'TEXT', 1, 1),
       (24, 1, '2025-11-23 11:13:09.397749', 'huynhducphu2502@gmail.com', '2025-11-23 11:13:09.397749',
        'huynhducphu2502@gmail.com', _binary 0x017fc4c5564d495f811a2b4160d87b59, 'Ok ok xuất ngay giờ luôn cũng đc',
        'TEXT', 1, 11),
       (25, 1, '2025-11-23 11:15:46.721616', 'Test2@gmail.com', '2025-11-23 11:15:46.721616', 'Test2@gmail.com',
        _binary 0xd28c409ed5894bd594a102b1f4a4bd61, 'Hi ông', 'TEXT', 2, 2),
       (26, 1, '2025-11-23 11:15:56.239432', 'huynhducphu2502@gmail.com', '2025-11-23 11:15:56.239432',
        'huynhducphu2502@gmail.com', _binary 0xcf204de5587a4cf58bb4f918b275fd6a, 'Có chuyện gì vậy', 'TEXT', 2, 11),
       (27, 1, '2025-11-23 11:16:50.714291', 'Test2@gmail.com', '2025-11-23 11:16:50.714291', 'Test2@gmail.com',
        _binary 0xd5b9c013e4924a688a33cf2f1ccb7b1a,
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/chats/c94f6465-c4ec-4227-916d-2e11d6c82a62', 'IMAGE', 2, 2),
       (28, 1, '2025-11-23 11:16:51.013871', 'Test2@gmail.com', '2025-11-23 11:16:51.013871', 'Test2@gmail.com',
        _binary 0xb26cd5237832463c97a7b988fcb12876, 'Bạn biết ông này không, tôi thấy bạn nói chuyện với ổng', 'TEXT',
        2, 2),
       (29, 1, '2025-11-23 11:17:09.835756', 'huynhducphu2502@gmail.com', '2025-11-23 11:17:09.835756',
        'huynhducphu2502@gmail.com', _binary 0x6a40a4e0c6964d1ebad5308683830858,
        'À, tôi biết, ổ tên Nguyễn Công Danh KOL đình đám đó bạn', 'TEXT', 2, 11),
       (30, 1, '2025-11-23 11:19:00.186957', 'huynhducphu2502@gmail.com', '2025-11-23 11:19:00.186957',
        'huynhducphu2502@gmail.com', _binary 0xbef9d0b70d4f40e8a2db391dd6315ab7,
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/chats/1283efdf-e669-4c70-a891-e8a6e9298eb7', 'VIDEO', 2, 11),
       (31, 1, '2025-11-23 11:19:00.517775', 'huynhducphu2502@gmail.com', '2025-11-23 11:19:00.517775',
        'huynhducphu2502@gmail.com', _binary 0x24ea21b57aa24bd79ed63f7de8fe8a43, 'Đây bạn nha', 'TEXT', 2, 11),
       (32, 1, '2025-11-23 11:20:55.747447', 'huynhducphu2502@gmail.com', '2025-11-23 11:20:55.747447',
        'huynhducphu2502@gmail.com', _binary 0x377a68a017694c02b4a39a8d6b3d9bcb,
        'Huỳnh Đức Phú đã đổi chủ đề nhóm thành "ROSE"', 'SYSTEM', 11, 11),
       (33, 1, '2025-11-23 11:21:26.830187', 'huynhducphu2502@gmail.com', '2025-11-23 11:21:26.830187',
        'huynhducphu2502@gmail.com', _binary 0x0225fae32e4b4e1591902e39e70afc13, 'Huỳnh Đức Phú đã cập nhật ảnh nhóm',
        'SYSTEM', 11, 11),
       (34, 1, '2025-11-23 11:21:31.390418', 'huynhducphu2502@gmail.com', '2025-11-23 11:21:31.390418',
        'huynhducphu2502@gmail.com', _binary 0x53ee7539247545cb9a26dc24368453be, 'Chào ae', 'TEXT', 11, 11),
       (35, 1, '2025-11-23 11:22:17.089133', 'huynhducphu2502@gmail.com', '2025-11-23 11:22:17.089133',
        'huynhducphu2502@gmail.com', _binary 0x217ead337fff4e06861cb66067e77468,
        'Huỳnh Đức Phú đã đổi quyền của Phạm Tuấn Khoa thành ADMIN', 'SYSTEM', 11, 11);

UPDATE rooms
SET last_message_id = 24
WHERE id = 1;

UPDATE rooms
SET last_message_id = 31
WHERE id = 2;

UPDATE rooms
SET last_message_id = 34
WHERE id = 11;
-- ===========================================================================================================

INSERT INTO `blogs` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `category`, `content`,
                     `description`, `img_preview_url`, `is_approved`, `title`, `user_id`)
VALUES (1, 1, '2025-11-23 18:50:27.291443', 'huynhducphu2502@gmail.com', '2025-11-23 18:50:46.982676',
        'huynhducphu2502@gmail.com', 'TECHNOLOGY',
        '<p>Kotlin Multiplatform (KMP) đang ngày càng khẳng định vị thế như một trong những hướng tiếp cận hiệu quả nhất cho phát triển ứng dụng đa nền tảng, đặc biệt khi doanh nghiệp muốn <em>tối ưu chi phí nhưng vẫn giữ trọn trải nghiệm native</em>. Không giống các framework “write once run everywhere”, KMP tập trung vào <strong>chia sẻ business logic</strong>—những phần như networking, xử lý dữ liệu, validation, caching—trong khi UI vẫn hoàn toàn native để đảm bảo hiệu năng và cảm giác sử dụng mượt mà.</p><p><br></p><p>Một trong những ưu điểm lớn nhất của KMP là khả năng nâng cấp từng phần thay vì bắt buộc rewrite toàn bộ ứng dụng. Điều này phù hợp với mô hình doanh nghiệp vừa/nhỏ hay các startup đang muốn <strong>chuyển đổi công nghệ mà không gián đoạn hoạt động hiện tại</strong>. Với cơ chế expect/actual, các phần native API vẫn có thể được gọi trực tiếp từ shared module, ví dụ:</p><p><br></p><p>// commonMain</p><p>expect fun getPlatformName(): String</p><p><br></p><p>// androidMain</p><p>actual fun getPlatformName(): String = "Android"</p><p><br></p><p>// iosMain</p><p>actual fun getPlatformName(): String = "iOS"</p><p><br></p><p>Ngoài khả năng chia sẻ code, hệ sinh thái của KMP ngày càng mạnh mẽ nhờ <strong>Kotlinx Serialization</strong>, <strong>Ktor</strong>, <strong>SQLDelight</strong>, và <strong>Compose Multiplatform</strong>, giúp nhà phát triển xây dựng sản phẩm nhanh và dễ maintain hơn.</p><p><br></p><p>Khi so sánh với Flutter hay React Native, nhiều team đánh giá KMP mang lại lợi thế lớn hơn trong những trường hợp yêu cầu cao về performance hoặc sâu vào tích hợp hệ thống. Vì KMP vẫn dùng <strong>UI native</strong>, nhà phát triển có thể sử dụng toàn bộ tài nguyên, thư viện, widget và pattern quen thuộc trên từng nền tảng. Điều này cũng mở ra khả năng <strong>tối ưu hiệu suất từng nền tảng mà không tạo ra sự lệch pha trong hành vi ứng dụng</strong>.</p><p>Các công ty lớn như Netflix, Philips, VMWare đã áp dụng KMP cho nhiều phần trong ứng dụng sản xuất. Theo JetBrains, bản cập nhật Compose Multiplatform và Gradle 8 sẽ giúp KMP trở thành nền tảng đa năng, vừa linh hoạt vừa mạnh mẽ và phù hợp cho sản phẩm lớn trong nhiều năm tới.</p><p><br></p><p><strong>Tóm lại</strong>, nếu doanh nghiệp muốn tiết kiệm chi phí, rút ngắn thời gian phát triển và giữ nguyên trải nghiệm native, thì Kotlin Multiplatform là một trong những lựa chọn đáng cân nhắc nhất hiện nay. 🚀</p><p><br></p>',
        'Giảm chi phí, tăng tốc phát triển và giữ nguyên native experience.',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/blog-images/31d55288-27a0-48a0-9af4-b6598b600644.png', b'1',
        'Kotlin Multiplatform: Tương lai của phát triển ứng dụng đa nền tảng', 11),
       (2, 1, '2025-11-23 18:56:47.757820', 'huynhducphu2502@gmail.com', '2025-11-23 18:56:53.912752',
        'huynhducphu2502@gmail.com', 'EDUCATION',
        '<p>Trong bối cảnh lượng kiến thức ngày càng khổng lồ và tốc độ thay đổi nhanh chóng, việc học thông minh quan trọng hơn rất nhiều so với học nhiều. Nhiều nghiên cứu về khoa học nhận thức cho thấy rằng não bộ lưu giữ thông tin tốt nhất khi chúng ta chủ động tương tác với kiến thức thay vì tiếp nhận thụ động. Dưới đây là bốn kỹ thuật học tập đã chứng minh hiệu quả cao và được áp dụng rộng rãi trong các lĩnh vực từ giáo dục đến nghiên cứu chuyên sâu.</p><p><br></p><h2><strong>1. Active Recall – Tự buộc não truy xuất thông tin</strong></h2><p>Active Recall là kỹ thuật học tập hiệu quả nhất theo các nghiên cứu hiện đại. Thay vì đọc lại tài liệu liên tục, người học che phần đáp án và cố gắng <em>tự nhớ lại</em> nội dung. Quá trình chủ động truy xuất này kích hoạt vùng trí nhớ sâu, giúp thông tin lưu lại lâu hơn. Ví dụ: khi học từ vựng, thay vì nhìn từ và nghĩa liên tục, hãy thử che nghĩa lại và hỏi: “Từ này có nghĩa là gì?”. Nếu bạn phải vận động não để trả lời, bạn đang tạo ra một “điểm neo trí nhớ” vững chắc hơn nhiều.</p><p><br></p><h2><strong>2. Spaced Repetition – Ôn tập theo chu kỳ giãn cách</strong></h2><p>Spaced Repetition giải quyết vấn đề “quên nhanh” bằng cách ôn tập theo khoảng thời gian tăng dần: ngày 1, ngày 3, ngày 7, ngày 14… Các nghiên cứu của Ebbinghaus chỉ ra rằng bộ não quên thông tin theo đường cong rất nhanh nếu không được củng cố đúng lúc. Việc ôn lại ở thời điểm não bắt đầu quên giúp tái xây dựng lại ký ức và kéo dài thời gian ghi nhớ. Công cụ như <strong>Anki</strong>, <strong>Quizlet</strong>, <strong>RemNote</strong> mô phỏng chính xác mô hình này, giúp người học ghi nhớ từ vựng, định nghĩa hoặc công thức lâu dài mà không quá tải.</p><p><br></p><h2><strong>3. Pomodoro – Quản lý sự tập trung</strong></h2><p>Pomodoro là phương pháp chia thời gian học thành các phiên ngắn, phổ biến nhất là 25 phút học – 5 phút nghỉ. Não bộ hoạt động hiệu quả nhất trong những khoảng tập trung ngắn nhưng cường độ cao, và Pomodoro giúp duy trì nhịp độ này mà không bị kiệt sức. Cứ 4 phiên Pomodoro thì nghỉ dài hơn 15–20 phút. Phương pháp này đặc biệt hữu ích cho những ai dễ bị sao nhãng, trì hoãn hoặc gặp khó khăn trong việc duy trì sự tập trung liên tục.</p><p><br></p><h2><strong>4. Feynman Technique – Kiểm tra mức độ “hiểu thật”</strong></h2><p>Richard Feynman, nhà vật lý từng đoạt Nobel, tin rằng bạn chỉ thực sự hiểu một khái niệm khi có thể giải thích nó bằng ngôn ngữ đơn giản. Kỹ thuật này yêu cầu người học viết lại kiến thức như đang dạy cho một đứa trẻ 12 tuổi. Nếu gặp chỗ nào không thể giải thích đơn giản, đó chính là “lỗ hổng kiến thức”. Bạn quay lại tài liệu, bổ sung chỗ thiếu và thử giải thích lại lần nữa. Đây là công cụ tuyệt vời để học các môn khó như toán, vật lý, lập trình hoặc tài chính.</p><p><br></p><h2><strong>Kết luận</strong></h2><p>Việc học hiệu quả không nằm ở thời gian bạn bỏ ra, mà nằm ở chiến lược. Khi kết hợp <strong>Active Recall</strong>, <strong>Spaced Repetition</strong>, <strong>Pomodoro</strong> và <strong>Feynman Technique</strong>, bạn sẽ tạo thành một quy trình học tối ưu: học chủ động – củng cố đúng thời điểm – duy trì nhịp độ tập trung – kiểm tra sự hiểu thật sự. Nếu duy trì đều đặn, hiệu suất học tập có thể tăng gấp đôi chỉ trong vài tuần.</p><p><br></p>',
        'Đơn giản nhưng cực hiệu quả nếu áp dụng đúng cách.',
        'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/blog-images/1afd53fe-f324-46f4-ae88-89d0f2aa391e.jpg', b'1',
        '4 kỹ thuật học tập hiện đại giúp bạn ghi nhớ nhanh và sâu hơn', 11);
