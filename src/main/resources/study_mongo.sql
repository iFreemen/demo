/*
 Navicat MongoDB Data Transfer

 Source Server         : 本地虚拟机MongoDB
 Source Server Type    : MongoDB
 Source Server Version : 30603
 Source Host           : 192.168.118.132:27017
 Source Schema         : study_mongo

 Target Server Type    : MongoDB
 Target Server Version : 30603
 File Encoding         : 65001

 Date: 28/11/2019 16:14:23
*/


// ----------------------------
// Collection structure for userInfoMongo
// ----------------------------
db.getCollection("userInfoMongo").drop();
db.createCollection("userInfoMongo");
