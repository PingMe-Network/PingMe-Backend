-- Tạo User
INSERT INTO `users` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `address`, `auth_provider`, `avatar_url`, `dob`, `email`, `gender`, `name`, `password`, `status`, `role_id`) VALUES
	(1, 1, '2025-11-18 22:48:59.615037', 'anonymousUser', '2025-11-18 23:05:04.360119', 'Test1@gmail.com', '56/9 Lạc Long Quân, P.5, Q.11, TP.HCM', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test1@gmail.com', '2025-07-17', 'Test1@gmail.com', 'MALE', 'Phạm Tuấn Khoa', '$2a$10$6RAjVIU8XxvwJ2ewoDPoAeCUlKJqH7gRHaashHrwkwVv0WKm5/z5e', 'OFFLINE', NULL),
	(2, 1, '2025-11-18 22:49:47.075581', 'anonymousUser', '2025-11-18 23:05:42.434655', 'Test2@gmail.com', '10/9 Lê Duẩn, P.Thắng Nhất, TP.Vũng Tàu', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test2@gmail.com', '2025-09-10', 'Test2@gmail.com', 'FEMALE', 'Trần Thị Thu Trang', '$2a$10$GiN9ZRoCKZDMmyjQ08LjAOGcLUDISkywdkDdMEsOc1ayTSctpAP82', 'OFFLINE', NULL),
	(3, 1, '2025-11-18 22:51:09.449569', 'anonymousUser', '2025-11-18 23:06:20.796721', 'Test3@gmail.com', '45 Nguyễn Tất Thành, P.Vĩnh Thọ, Nha Trang', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test3@gmail.com', '2025-04-30', 'Test3@gmail.com', 'MALE', 'Bùi Anh Quân', '$2a$10$ND9jQ7/ybwMp.8XBIiw4k.hJtg2fvxqusVTA/c7AWnqZjN/2yucgG', 'OFFLINE', NULL),
	(4, 1, '2025-11-18 22:53:00.105600', 'anonymousUser', '2025-11-18 23:09:24.414137', 'Test4@gmail.com', '68/2 Nguyễn Văn Trỗi, P.8, Phú Nhuận, TP.HCM', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test4@gmail.com', '2001-04-17', 'Test4@gmail.com', 'FEMALE', 'Phạm Bảo Ngọc', '$2a$10$.L99JYyUqk4DIKhrvUfv4OAaQw3DpGgdlUA9PCIPIg1u55RX.mMaK', 'OFFLINE', NULL),
	(5, 1, '2025-11-18 22:54:09.992369', 'anonymousUser', '2025-11-18 23:09:54.900420', 'Test5@gmail.com', '112 Trần Hưng Đạo, P.Mỹ Bình, Long Xuyên, An Giang', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test5@gmail.com', '2001-01-09', 'Test5@gmail.com', 'MALE', 'Võ Nhật Long', '$2a$10$VdLRPrwdjcO.J0Vq.MO2Iu2tNd9flFZ6tFasACEMOmTT0Rb96nrB2', 'OFFLINE', NULL),
	(6, 1, '2025-11-18 22:57:19.534273', 'anonymousUser', '2025-11-18 23:16:32.044163', 'Test6@gmail.com', '33 Nguyễn Thị Minh Khai, P.Bến Thành, Q.1, TP.HCM', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test6@gmail.com', '2000-09-13', 'Test6@gmail.com', 'FEMALE', 'Trần Quế My', '$2a$10$avKIRLICxePlrPnThNhALO0mltduU4OcgOoUnTmqXzBWT6Dv.Vwce', 'OFFLINE', NULL),
	(7, 1, '2025-11-18 22:58:40.727272', 'anonymousUser', '2025-11-18 23:19:05.678958', 'Test7@gmail.com', '23/7 Hoàng Hoa Thám, P.6, Q.Bình Thạnh, TP.HCM', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test7@gmail.com', '2025-06-21', 'Test7@gmail.com', 'MALE', 'Đặng Xuân Nam', '$2a$10$/R47zjqACeJvZUcgqvEV1uJtkGz96KGfKHM6L/.qVJcqNnOeBwUbi', 'OFFLINE', NULL),
	(8, 1, '2025-11-18 22:59:51.828506', 'anonymousUser', '2025-11-18 23:21:32.273121', 'Test8@gmail.com', '50 Tôn Đức Thắng, P.Bến Nghé, Q.1, TP.HCM', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test8@gmail.com', '2003-10-18', 'Test8@gmail.com', 'FEMALE', 'Phạm Hải Yến', '$2a$10$L5q7vVIc4cELw6cJ.pOYOOhMGBmnTB3kkbgDAdknKwapXlMqdV19K', 'OFFLINE', NULL),
	(9, 1, '2025-11-18 23:03:47.999784', 'anonymousUser', '2025-11-18 23:23:14.336209', 'Test9@gmail.com', '18A Hà Huy Tập, P.Tân Lợi, TP.Buôn Ma Thuột', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test9@gmail.com', '2000-12-03', 'Test9@gmail.com', 'MALE', 'Nguyễn Tấn Tín', '$2a$10$LXf6qogfe2XJr.BM7Of4YO3P8InO8Fgs2Up9rsn7FjZ4Mx8hZzyfG', 'OFFLINE', NULL),
	(10, 1, '2025-11-18 23:04:34.924416', 'anonymousUser', '2025-11-18 23:23:48.527676', 'Test10@gmail.com', '44/7 Phan Đăng Lưu, P.3, Q.Bình Thạnh, TP.HCM', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/Test10@gmail.com', '2002-06-25', 'Test10@gmail.com', 'FEMALE', 'Đỗ Hoài Như', '$2a$10$xTxJpev9CJ8QxrXpTc/RmO7KayY9lDCakEFKxKossIoyneB0Oxq16', 'OFFLINE', NULL),
	(11, 1, '2025-11-18 23:06:50.393330', 'anonymousUser', '2025-11-18 23:36:04.009499', 'huynhducphu2502@gmail.com', '120 Xóm Chiếu, Phường 14, Quận 4, TPHCM', 'LOCAL', 'https://pingme-s3.s3.ap-southeast-1.amazonaws.com/avatar/huynhducphu2502@gmail.com', '2003-02-25', 'huynhducphu2502@gmail.com', 'MALE', 'Huỳnh Đức Phú', '$2a$10$qVGedE0iLfFMkBhsFqvNte571l38ZbQLO8luBK9xF0qbKvaclm7tW', 'OFFLINE', NULL);

-- Thiết lập mối quan hệ bạn bè với account HuynhDucPhu2502@gmail.com
INSERT INTO `friendships` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `status`, `user_high_id`, `user_low_id`, `user_a_id`, `user_b_id`) VALUES
	(1, 1, '2025-11-18 23:07:19.344422', 'Test3@gmail.com', '2025-11-18 23:24:46.665294', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 3, 3, 11),
	(2, 1, '2025-11-18 23:07:38.791005', 'Test1@gmail.com', '2025-11-18 23:24:46.171186', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 1, 1, 11),
	(3, 1, '2025-11-18 23:08:07.691509', 'Test2@gmail.com', '2025-11-18 23:24:45.639012', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 2, 2, 11),
	(4, 1, '2025-11-18 23:10:04.110379', 'Test5@gmail.com', '2025-11-18 23:24:44.638708', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 5, 5, 11),
	(5, 1, '2025-11-18 23:10:29.852871', 'Test4@gmail.com', '2025-11-18 23:24:44.107127', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 4, 4, 11),
	(6, 1, '2025-11-18 23:18:22.722881', 'Test6@gmail.com', '2025-11-18 23:24:43.524639', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 6, 6, 11),
	(7, 1, '2025-11-18 23:19:29.490937', 'Test7@gmail.com', '2025-11-18 23:24:42.997787', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 7, 7, 11),
	(8, 1, '2025-11-18 23:21:02.702739', 'Test8@gmail.com', '2025-11-18 23:24:42.401438', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 8, 8, 11),
	(9, 1, '2025-11-18 23:24:00.662050', 'Test10@gmail.com', '2025-11-18 23:24:41.848693', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 10, 10, 11),
	(10, 1, '2025-11-18 23:24:18.596440', 'Test9@gmail.com', '2025-11-18 23:24:41.084521', 'huynhducphu2502@gmail.com', 'ACCEPTED', 11, 9, 9, 11);
	
-- Tạo phòng Chat 1-1 acc HuynhDucPhu2502@gmail với tất cả mối quan hệ
INSERT INTO `rooms` (`id`, `active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `direct_key`, `last_message_at`, `name`, `room_type`, `last_message_id`) VALUES
	(1, 1, '2025-11-18 23:25:00.507732', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:00.507732', 'huynhducphu2502@gmail.com', '1_11', NULL, NULL, 'DIRECT', NULL),
	(2, 1, '2025-11-18 23:25:08.087240', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:08.087240', 'huynhducphu2502@gmail.com', '2_11', NULL, NULL, 'DIRECT', NULL),
	(3, 1, '2025-11-18 23:25:20.223219', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:20.223219', 'huynhducphu2502@gmail.com', '3_11', NULL, NULL, 'DIRECT', NULL),
	(4, 1, '2025-11-18 23:25:24.722840', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:24.722840', 'huynhducphu2502@gmail.com', '4_11', NULL, NULL, 'DIRECT', NULL),
	(5, 1, '2025-11-18 23:25:33.839558', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:33.839558', 'huynhducphu2502@gmail.com', '5_11', NULL, NULL, 'DIRECT', NULL),
	(6, 1, '2025-11-18 23:25:38.330108', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:38.330108', 'huynhducphu2502@gmail.com', '6_11', NULL, NULL, 'DIRECT', NULL),
	(7, 1, '2025-11-18 23:25:45.049678', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:45.049678', 'huynhducphu2502@gmail.com', '7_11', NULL, NULL, 'DIRECT', NULL),
	(8, 1, '2025-11-18 23:25:52.230728', 'huynhducphu2502@gmail.com', '2025-11-19 09:30:35.393892', 'Test8@gmail.com', '8_11', '2025-11-19 09:30:35.389710', NULL, 'DIRECT', 26),
	(9, 1, '2025-11-18 23:25:56.847710', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:56.847710', 'huynhducphu2502@gmail.com', '9_11', NULL, NULL, 'DIRECT', NULL),
	(10, 1, '2025-11-18 23:26:01.236993', 'huynhducphu2502@gmail.com', '2025-11-18 23:26:01.236993', 'huynhducphu2502@gmail.com', '10_11', NULL, NULL, 'DIRECT', NULL);
	
-- Data chat của HuynhDucPhu2502@gmail.com với Test8@gmail.com
INSERT INTO `room_participants` (`active`, `created_at`, `created_by`, `updated_at`, `updated_by`, `last_read_at`, `last_read_message_id`, `role`, `room_id`, `user_id`) VALUES
	(1, '2025-11-18 23:25:00.536125', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:00.536125', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 1, 1),
	(1, '2025-11-18 23:25:00.525385', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:00.525385', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 1, 11),
	(1, '2025-11-18 23:25:08.095625', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:08.095625', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 2, 2),
	(1, '2025-11-18 23:25:08.089213', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:08.089213', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 2, 11),
	(1, '2025-11-18 23:25:20.226183', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:20.226183', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 3, 3),
	(1, '2025-11-18 23:25:20.225213', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:20.225213', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 3, 11),
	(1, '2025-11-18 23:25:24.728824', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:24.728824', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 4, 4),
	(1, '2025-11-18 23:25:24.725822', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:24.725822', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 4, 11),
	(1, '2025-11-18 23:25:33.845522', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:33.845522', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 5, 5),
	(1, '2025-11-18 23:25:33.842524', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:33.842524', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 5, 11),
	(1, '2025-11-18 23:25:38.335969', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:38.335969', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 6, 6),
	(1, '2025-11-18 23:25:38.334004', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:38.334004', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 6, 11),
	(1, '2025-11-18 23:25:45.054667', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:45.054667', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 7, 7),
	(1, '2025-11-18 23:25:45.051656', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:45.051656', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 7, 11),
	(1, '2025-11-18 23:25:52.234897', 'huynhducphu2502@gmail.com', '2025-11-19 09:30:35.395871', 'Test8@gmail.com', '2025-11-19 09:30:35.389710', 26, 'MEMBER', 8, 8),
	(1, '2025-11-18 23:25:52.232902', 'huynhducphu2502@gmail.com', '2025-11-19 09:30:29.088463', 'huynhducphu2502@gmail.com', '2025-11-19 09:30:29.086083', 25, 'MEMBER', 8, 11),
	(1, '2025-11-18 23:25:56.856885', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:56.856885', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 9, 9),
	(1, '2025-11-18 23:25:56.849692', 'huynhducphu2502@gmail.com', '2025-11-18 23:25:56.849692', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 9, 11),
	(1, '2025-11-18 23:26:01.241667', 'huynhducphu2502@gmail.com', '2025-11-18 23:26:01.241667', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 10, 10),
	(1, '2025-11-18 23:26:01.239668', 'huynhducphu2502@gmail.com', '2025-11-18 23:26:01.239668', 'huynhducphu2502@gmail.com', NULL, NULL, 'MEMBER', 10, 11);