CREATE TABLE `student_verification` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` int(11) NOT NULL,
  `school` varchar(255) DEFAULT NULL,
  `student_id` varchar(100) DEFAULT NULL,
  `card_image` LONGTEXT DEFAULT NULL,
  `status` int(11) DEFAULT '0' COMMENT '0: Pending, 1: Approved, 2: Rejected',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
