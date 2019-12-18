/*
Navicat MySQL Data Transfer

Source Server         : 本地数据库
Source Server Version : 50641
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50641
File Encoding         : 65001

Date: 2019-12-18 11:49:27
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
INSERT INTO `student` VALUES ('1', 'Freemen', '18', '100.00');
INSERT INTO `student` VALUES ('2', 'shasha', '17', '80.00');
