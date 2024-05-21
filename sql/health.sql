/*
 Navicat Premium Data Transfer

 Source Server         : 腾讯云
 Source Server Type    : MySQL
 Source Server Version : 80200 (8.2.0)
 Source Host           : 129.204.42.33:3306
 Source Schema         : health

 Target Server Type    : MySQL
 Target Server Version : 80200 (8.2.0)
 File Encoding         : 65001

 Date: 14/03/2024 20:11:23
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for collection_info
-- ----------------------------
DROP TABLE IF EXISTS `collection_info`;
CREATE TABLE `collection_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `cookbook_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_collection_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_collection_cookbook`(`cookbook_id` ASC) USING BTREE,
  CONSTRAINT `fk1_userid` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_cookbookid` FOREIGN KEY (`cookbook_id`) REFERENCES `cookbook_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cookbook_clicks
-- ----------------------------
DROP TABLE IF EXISTS `cookbook_clicks`;
CREATE TABLE `cookbook_clicks`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `cookbook_id` int NOT NULL,
  `count` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_cookbookclicks_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_cookbookclicks_cookbook`(`cookbook_id` ASC) USING BTREE,
  CONSTRAINT `fk1_cookbookclicks_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_cookbookclicks_cookbook` FOREIGN KEY (`cookbook_id`) REFERENCES `cookbook_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cookbook_info
-- ----------------------------
DROP TABLE IF EXISTS `cookbook_info`;
CREATE TABLE `cookbook_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `collect_count` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for cookbook_score
-- ----------------------------
DROP TABLE IF EXISTS `cookbook_score`;
CREATE TABLE `cookbook_score`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `cookbook_id` int NOT NULL,
  `score` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_cookbookscore_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_cookbookscore_cookbook`(`cookbook_id` ASC) USING BTREE,
  CONSTRAINT `fk1_cookbookscore_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_cookbookscore_cookbook` FOREIGN KEY (`cookbook_id`) REFERENCES `cookbook_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for diary_info
-- ----------------------------
DROP TABLE IF EXISTS `diary_info`;
CREATE TABLE `diary_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `food_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_diary_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_diary_food`(`food_id` ASC) USING BTREE,
  CONSTRAINT `fk1_diary_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_diary_food` FOREIGN KEY (`food_id`) REFERENCES `food_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for family_info
-- ----------------------------
DROP TABLE IF EXISTS `family_info`;
CREATE TABLE `family_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `family_encode` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `memo` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_family_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_family_token`(`family_encode` ASC) USING BTREE,
  CONSTRAINT `fk_family_token` FOREIGN KEY (`family_encode`) REFERENCES `fitbit_token` (`encode`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_family_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for favourite_info
-- ----------------------------
DROP TABLE IF EXISTS `favourite_info`;
CREATE TABLE `favourite_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `music_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_favourite_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_favourite_music`(`music_id` ASC) USING BTREE,
  CONSTRAINT `fk1_favourite_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_favourite_music` FOREIGN KEY (`music_id`) REFERENCES `music_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for favourite_musiclist_info
-- ----------------------------
DROP TABLE IF EXISTS `favourite_musiclist_info`;
CREATE TABLE `favourite_musiclist_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `musiclist_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk2_favourite_user`(`user_id` ASC) USING BTREE,
  INDEX `fk1_favourite_musiclist`(`musiclist_id` ASC) USING BTREE,
  CONSTRAINT `fk1_favourite_musiclist` FOREIGN KEY (`musiclist_id`) REFERENCES `musiclist_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_favourite_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for fitbit_token
-- ----------------------------
DROP TABLE IF EXISTS `fitbit_token`;
CREATE TABLE `fitbit_token`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `encode` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `fitbit_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `access_token` varchar(500) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `code` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_fitbit_user`(`user_id` ASC) USING BTREE,
  INDEX `encode`(`encode` ASC) USING BTREE,
  CONSTRAINT `fk1_fitbit_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for food_info
-- ----------------------------
DROP TABLE IF EXISTS `food_info`;
CREATE TABLE `food_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `calorie` int NOT NULL,
  `vitamin` int NOT NULL,
  `carbon` int NOT NULL,
  `fat` int NOT NULL,
  `protein` int NOT NULL,
  `fiber` int NOT NULL,
  `minerals` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for music_in_musiclist
-- ----------------------------
DROP TABLE IF EXISTS `music_in_musiclist`;
CREATE TABLE `music_in_musiclist`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `music_id` int NOT NULL,
  `musiclist_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_musicin_music`(`music_id` ASC) USING BTREE,
  INDEX `fk2_musicin_musiclist`(`musiclist_id` ASC) USING BTREE,
  CONSTRAINT `fk1_musicin_music` FOREIGN KEY (`music_id`) REFERENCES `music_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_musicin_musiclist` FOREIGN KEY (`musiclist_id`) REFERENCES `musiclist_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for music_info
-- ----------------------------
DROP TABLE IF EXISTS `music_info`;
CREATE TABLE `music_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `music` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `favourite_count` int NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for music_score
-- ----------------------------
DROP TABLE IF EXISTS `music_score`;
CREATE TABLE `music_score`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `music_id` int NOT NULL,
  `score` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_musicscore_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_musicscore_music`(`music_id` ASC) USING BTREE,
  CONSTRAINT `fk1_musicscore_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_musicscore_music` FOREIGN KEY (`music_id`) REFERENCES `music_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for musiclist_info
-- ----------------------------
DROP TABLE IF EXISTS `musiclist_info`;
CREATE TABLE `musiclist_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `introduction` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for musiclist_score
-- ----------------------------
DROP TABLE IF EXISTS `musiclist_score`;
CREATE TABLE `musiclist_score`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `musiclist_id` int NOT NULL,
  `score` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_musiclistscore_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_musiclistscore_musiclist`(`musiclist_id` ASC) USING BTREE,
  CONSTRAINT `fk1_musiclistscore_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_musiclistscore_musiclist` FOREIGN KEY (`musiclist_id`) REFERENCES `musiclist_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_comment_info
-- ----------------------------
DROP TABLE IF EXISTS `post_comment_info`;
CREATE TABLE `post_comment_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_postcomment_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_postcomment_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk1_postcomment_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_postcomment_post` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_image_info
-- ----------------------------
DROP TABLE IF EXISTS `post_image_info`;
CREATE TABLE `post_image_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `position` int NOT NULL,
  `post_id` int NOT NULL,
  `image` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_postimage_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk1_postimage_post` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_info
-- ----------------------------
DROP TABLE IF EXISTS `post_info`;
CREATE TABLE `post_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `like_count` int NOT NULL,
  `comment_count` int NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_post_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk1_post_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for post_like_info
-- ----------------------------
DROP TABLE IF EXISTS `post_like_info`;
CREATE TABLE `post_like_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_postlike_user`(`user_id` ASC) USING BTREE,
  INDEX `fk2_postlike_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk1_postlike_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk2_postlike_post` FOREIGN KEY (`post_id`) REFERENCES `post_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for question_info
-- ----------------------------
DROP TABLE IF EXISTS `question_info`;
CREATE TABLE `question_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `question` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `answer` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_question_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_question_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for sleep_info
-- ----------------------------
DROP TABLE IF EXISTS `sleep_info`;
CREATE TABLE `sleep_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `sleep_minute` int NOT NULL,
  `in_bed_minute` int NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_sleep_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk1_sleep_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for spo2_info
-- ----------------------------
DROP TABLE IF EXISTS `spo2_info`;
CREATE TABLE `spo2_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `avg` decimal(10, 1) NOT NULL,
  `min` decimal(10, 1) NOT NULL,
  `max` decimal(10, 1) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_spo2`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_spo2` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for temperature_info
-- ----------------------------
DROP TABLE IF EXISTS `temperature_info`;
CREATE TABLE `temperature_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `avg` decimal(10, 1) NOT NULL,
  `min` decimal(10, 1) NOT NULL,
  `max` decimal(10, 1) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_temperature_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk1_temperature_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `account` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `telephone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `sex` tinyint NULL DEFAULT NULL,
  `age` int NULL DEFAULT NULL,
  `height` decimal(10, 2) NULL DEFAULT NULL,
  `weight` decimal(10, 2) NULL DEFAULT NULL,
  `identity` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `disease` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `signature` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for water_record
-- ----------------------------
DROP TABLE IF EXISTS `water_record`;
CREATE TABLE `water_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `cup` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_water_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk1_water_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for wristband_info
-- ----------------------------
DROP TABLE IF EXISTS `wristband_info`;
CREATE TABLE `wristband_info`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `time` datetime NOT NULL,
  `step` int UNSIGNED NULL DEFAULT NULL,
  `distance` int UNSIGNED NULL DEFAULT NULL,
  `calories` int UNSIGNED NULL DEFAULT NULL,
  `heartrate` int UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk1_wristband_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk1_wristband_user` FOREIGN KEY (`user_id`) REFERENCES `user_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
