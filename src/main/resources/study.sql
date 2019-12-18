/*
Navicat MySQL Data Transfer

Source Server         : 本地数据库
Source Server Version : 50641
Source Host           : localhost:3306
Source Database       : study

Target Server Type    : MYSQL
Target Server Version : 50641
File Encoding         : 65001

Date: 2019-12-18 11:49:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL,
  `age` int(3) DEFAULT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('1', 'Freemen', '30', '100.00');
INSERT INTO `student` VALUES ('2', 'dandan', '27', '99.00');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) CHARACTER SET utf8 DEFAULT NULL,
  `password` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `real_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'Freemen', '123', 'HH');
INSERT INTO `user` VALUES ('2', 'lingling', '123', 'mm');

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(32) CHARACTER SET utf8 DEFAULT NULL,
  `password` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
  `real_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('2', 'lingling', '123', 'mm');
INSERT INTO `user_info` VALUES ('5', 'Freemen', 'mima', 'hhh');
INSERT INTO `user_info` VALUES ('6', 'Freemen', '63.12795989721711', 'hhh');
INSERT INTO `user_info` VALUES ('7', 'Freemen', '30.00538172773899', 'hhh');
INSERT INTO `user_info` VALUES ('8', 'Freemen', '12.285830320584857', 'hhh');
INSERT INTO `user_info` VALUES ('9', 'Freemen', '18.337133502106685', 'hhh');
INSERT INTO `user_info` VALUES ('10', 'Freemen', '4.837716767884537', 'hhh');
INSERT INTO `user_info` VALUES ('11', 'Freemen', '76.2638932646186', 'hhh');
