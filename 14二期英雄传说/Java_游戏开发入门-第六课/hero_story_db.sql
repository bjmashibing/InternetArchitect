CREATE DATABASE hero_story DEFAULT CHARACTER SET utf8;

USE hero_story;

CREATE TABLE `t_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `hero_avatar` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
);
