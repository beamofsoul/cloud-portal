/*
Navicat MySQL Data Transfer

Source Server         : 海鳗v1.4(外网) FASTER
Source Server Version : 50634
Source Host           : 47.93.109.133:3306
Source Database       : moraydata_cloud_primary_01

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2018-05-11 15:42:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `oauth_client_details`
-- ----------------------------
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) COLLATE utf8_bin NOT NULL,
  `resource_ids` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `client_secret` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `scope` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `authorized_grant_types` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `web_server_redirect_uri` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `authorities` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) COLLATE utf8_bin DEFAULT NULL,
  `autoapprove` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('client_hm', 'open_api', '$2a$10$Qok3Z3Kg8YgxHtjXWHEVreIteUgm9HJ8kqmc3Mgrh1edLQ/njhL5m', 'select', 'password,refresh_token', null, null, null, null, null, null);

-- ----------------------------
-- Table structure for `qrtz_blob_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_calendars`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `CALENDAR_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_cron_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `CRON_EXPRESSION` varchar(120) COLLATE utf8_bin NOT NULL,
  `TIME_ZONE_ID` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_fired_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `ENTRY_ID` varchar(95) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `INSTANCE_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) COLLATE utf8_bin NOT NULL,
  `JOB_NAME` varchar(190) COLLATE utf8_bin DEFAULT NULL,
  `JOB_GROUP` varchar(190) COLLATE utf8_bin DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_job_details`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `JOB_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `JOB_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) COLLATE utf8_bin NOT NULL,
  `IS_DURABLE` varchar(1) COLLATE utf8_bin NOT NULL,
  `IS_NONCONCURRENT` varchar(1) COLLATE utf8_bin NOT NULL,
  `IS_UPDATE_DATA` varchar(1) COLLATE utf8_bin NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8_bin NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_locks`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `LOCK_NAME` varchar(40) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_paused_trigger_grps`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_scheduler_state`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `INSTANCE_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_simple_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_simprop_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `STR_PROP_1` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `STR_PROP_2` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `STR_PROP_3` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `qrtz_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `JOB_NAME` varchar(190) COLLATE utf8_bin NOT NULL,
  `JOB_GROUP` varchar(190) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) COLLATE utf8_bin NOT NULL,
  `TRIGGER_TYPE` varchar(8) COLLATE utf8_bin NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(190) COLLATE utf8_bin DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `t_invitation_code`
-- ----------------------------
DROP TABLE IF EXISTS `t_invitation_code`;
CREATE TABLE `t_invitation_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `available` bit(1) DEFAULT b'1' COMMENT '邀请码状态 - 1:可用,0:不可用',
  `code` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '邀请码',
  `expired_date` datetime DEFAULT NULL COMMENT '过期时间',
  `type` tinyint(4) DEFAULT '1' COMMENT '邀请码类型 - 1:绑定父账户邀请码',
  `user_id` bigint(20) NOT NULL COMMENT '持有用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_invitation_code
-- ----------------------------
INSERT INTO `t_invitation_code` VALUES ('11', '2018-04-16 18:02:36', '2018-04-16 18:02:36', '', '987A8A219680419EA40FDF882D1ECEC7', null, '1', '1');
INSERT INTO `t_invitation_code` VALUES ('12', '2018-04-18 16:40:43', '2018-04-18 16:40:43', '', '96BB360418414F599391A5C93F3C9553', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('13', '2018-04-18 16:41:03', '2018-04-18 16:41:03', '', 'FA6225321E5B47C6BE3764A1E864D2CD', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('14', '2018-04-18 16:46:22', '2018-04-18 16:46:22', '', 'D89229E225D7453DA6A6B8BA2FC163E8', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('15', '2018-04-18 16:48:35', '2018-04-18 16:48:35', '', '867814EE6BA34F898BC4C80B5892BF6C', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('16', '2018-04-18 16:50:01', '2018-04-18 16:50:01', '', '408362A3F0ED46D2B6CD631E15778222', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('17', '2018-04-18 16:50:52', '2018-04-18 16:50:52', '', '8E011E78903941E7AFBBAF735291D25E', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('19', '2018-04-18 19:10:59', '2018-04-18 19:10:59', '', 'F1EBCA4BEEE34F35A8FB0CC3E203F7F7', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('20', '2018-04-18 19:11:24', '2018-04-18 19:11:24', '', 'C95B6F3541FF4739B69045CCD4532AA9', null, '1', '22');
INSERT INTO `t_invitation_code` VALUES ('21', '2018-04-18 19:11:59', '2018-04-18 19:11:59', '', '54988E50D882459D8BAE2DE1FDAC5EE6', null, '1', '22');

-- ----------------------------
-- Table structure for `t_login`
-- ----------------------------
DROP TABLE IF EXISTS `t_login`;
CREATE TABLE `t_login` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `brand` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '设备品牌',
  `browser` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '所用浏览器',
  `ip_address` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT 'IP地址',
  `model` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '设备型号',
  `operating_system` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '设备操作系统',
  `screen_size` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '屏幕尺寸',
  `user_id` bigint(20) NOT NULL COMMENT '登录用户ID',
  PRIMARY KEY (`id`),
  KEY `FKqbwhd8ffw00hdkekquhhm7g7c` (`user_id`),
  CONSTRAINT `FKqbwhd8ffw00hdkekquhhm7g7c` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_login
-- ----------------------------
INSERT INTO `t_login` VALUES ('1', '2018-02-28 20:57:52', '2018-02-28 20:57:52', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('2', '2018-02-28 21:06:39', '2018-02-28 21:06:39', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('3', '2018-02-28 21:11:20', '2018-02-28 21:11:20', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('4', '2018-02-28 21:13:12', '2018-02-28 21:13:12', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('5', '2018-02-28 21:18:29', '2018-02-28 21:18:29', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('6', '2018-02-28 21:26:34', '2018-02-28 21:26:34', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('7', '2018-02-28 22:15:11', '2018-02-28 22:15:11', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('8', '2018-03-01 00:05:38', '2018-03-01 00:05:38', null, 'Firefox-57.0', '192.168.1.106', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('9', '2018-03-01 21:22:03', '2018-03-01 21:22:03', null, 'Firefox-57.0', '192.168.1.105', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('10', '2018-03-01 23:16:53', '2018-03-01 23:16:53', null, 'Firefox-57.0', '192.168.1.105', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('11', '2018-03-24 03:41:05', '2018-03-24 03:41:05', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('14', '2018-03-24 22:45:46', '2018-03-24 22:45:46', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('15', '2018-03-25 15:32:55', '2018-03-25 15:32:55', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('16', '2018-03-25 18:24:25', '2018-03-25 18:24:25', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '21');
INSERT INTO `t_login` VALUES ('17', '2018-03-25 18:25:07', '2018-03-25 18:25:07', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '21');
INSERT INTO `t_login` VALUES ('18', '2018-03-25 18:27:02', '2018-03-25 18:27:02', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('19', '2018-03-25 18:27:19', '2018-03-25 18:27:19', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '21');
INSERT INTO `t_login` VALUES ('20', '2018-03-25 18:27:23', '2018-03-25 18:27:23', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('21', '2018-03-25 18:28:38', '2018-03-25 18:28:38', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '21');
INSERT INTO `t_login` VALUES ('22', '2018-03-25 18:29:56', '2018-03-25 18:29:56', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('23', '2018-03-25 18:30:09', '2018-03-25 18:30:09', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '21');
INSERT INTO `t_login` VALUES ('24', '2018-03-25 18:30:16', '2018-03-25 18:30:16', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('25', '2018-03-25 18:37:13', '2018-03-25 18:37:13', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('26', '2018-03-25 18:37:23', '2018-03-25 18:37:23', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('27', '2018-03-25 18:37:34', '2018-03-25 18:37:34', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '21');
INSERT INTO `t_login` VALUES ('28', '2018-03-29 22:45:02', '2018-03-29 22:45:02', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('29', '2018-03-29 22:48:24', '2018-03-29 22:48:24', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('30', '2018-03-29 22:50:26', '2018-03-29 22:50:26', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('31', '2018-03-29 23:00:16', '2018-03-29 23:00:16', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('32', '2018-03-29 23:03:28', '2018-03-29 23:03:28', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('33', '2018-03-29 23:06:11', '2018-03-29 23:06:11', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('34', '2018-03-29 23:09:22', '2018-03-29 23:09:22', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('35', '2018-03-29 23:13:27', '2018-03-29 23:13:27', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('36', '2018-04-04 22:22:46', '2018-04-04 22:22:46', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('37', '2018-04-04 22:22:59', '2018-04-04 22:22:59', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('38', '2018-04-04 22:27:10', '2018-04-04 22:27:10', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('39', '2018-04-05 00:37:01', '2018-04-05 00:37:01', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('40', '2018-04-05 00:37:25', '2018-04-05 00:37:25', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('41', '2018-04-05 00:42:51', '2018-04-05 00:42:51', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('42', '2018-04-05 00:43:45', '2018-04-05 00:43:45', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('43', '2018-04-05 00:44:44', '2018-04-05 00:44:44', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('44', '2018-04-05 00:46:50', '2018-04-05 00:46:50', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('45', '2018-04-05 00:47:28', '2018-04-05 00:47:28', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('46', '2018-04-05 00:48:40', '2018-04-05 00:48:40', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('47', '2018-04-05 00:50:52', '2018-04-05 00:50:52', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('48', '2018-04-05 00:57:49', '2018-04-05 00:57:49', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('49', '2018-04-05 00:59:57', '2018-04-05 00:59:57', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('50', '2018-04-05 01:00:11', '2018-04-05 01:00:11', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('51', '2018-04-05 01:02:29', '2018-04-05 01:02:29', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('52', '2018-04-05 01:03:32', '2018-04-05 01:03:32', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('53', '2018-04-05 01:03:55', '2018-04-05 01:03:55', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('54', '2018-04-05 01:04:48', '2018-04-05 01:04:48', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('55', '2018-04-05 01:05:07', '2018-04-05 01:05:07', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('56', '2018-04-05 01:05:14', '2018-04-05 01:05:14', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('57', '2018-04-05 01:05:25', '2018-04-05 01:05:25', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('58', '2018-04-05 01:07:57', '2018-04-05 01:07:57', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('59', '2018-04-05 01:08:05', '2018-04-05 01:08:05', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('60', '2018-04-05 01:08:14', '2018-04-05 01:08:14', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('61', '2018-04-05 01:10:50', '2018-04-05 01:10:50', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('62', '2018-04-05 01:56:05', '2018-04-05 01:56:05', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('63', '2018-04-05 01:56:21', '2018-04-05 01:56:21', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('64', '2018-04-05 01:59:54', '2018-04-05 01:59:54', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('65', '2018-04-05 02:10:08', '2018-04-05 02:10:08', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('66', '2018-04-05 02:12:10', '2018-04-05 02:12:10', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('67', '2018-04-05 02:24:15', '2018-04-05 02:24:15', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('68', '2018-04-05 02:24:21', '2018-04-05 02:24:21', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('69', '2018-04-06 01:24:28', '2018-04-06 01:24:28', null, 'Firefox-57.0', '169.254.177.225', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('70', '2018-04-09 09:43:06', '2018-04-09 09:43:06', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('71', '2018-04-10 11:41:02', '2018-04-10 11:41:02', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('72', '2018-04-10 17:24:10', '2018-04-10 17:24:10', null, 'Chrome-65.0.3325.181', '192.168.31.98', null, 'Mac', null, '1');
INSERT INTO `t_login` VALUES ('73', '2018-04-13 16:49:16', '2018-04-13 16:49:16', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('74', '2018-04-19 13:31:29', '2018-04-19 13:31:29', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('75', '2018-04-19 14:06:04', '2018-04-19 14:06:04', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('76', '2018-04-19 14:40:09', '2018-04-19 14:40:09', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('77', '2018-04-19 15:40:43', '2018-04-19 15:40:43', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('78', '2018-04-19 16:26:44', '2018-04-19 16:26:44', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');
INSERT INTO `t_login` VALUES ('79', '2018-04-19 17:15:36', '2018-04-19 17:15:36', null, 'Firefox-59.0', '192.168.31.137', null, 'Windows', null, '1');

-- ----------------------------
-- Table structure for `t_order`
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `agent` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '代理商',
  `amount` decimal(12,2) DEFAULT NULL COMMENT '订单金额',
  `amount_for_agent` decimal(12,2) DEFAULT NULL COMMENT '代理商结算金额',
  `code` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '订单编号',
  `description` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `operator` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '运维人员',
  `service_begin_time` datetime DEFAULT NULL COMMENT '服务开始时间',
  `service_end_time` datetime DEFAULT NULL COMMENT '服务结束时间',
  `service_ids` varchar(256) COLLATE utf8_bin NOT NULL COMMENT '具体服务',
  `status` int(11) DEFAULT '1' COMMENT '订单状态 - 1:新建,2:生效,3:过期',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`),
  KEY `FKho2r4qgj3txpy8964fnla95ub` (`user_id`),
  CONSTRAINT `FKho2r4qgj3txpy8964fnla95ub` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_order
-- ----------------------------
INSERT INTO `t_order` VALUES ('3', '2018-04-16 05:58:47', '2018-04-18 18:19:25', null, '592547.23', '0.00', '100004', '没什么可描述的', '张三123', '2018-04-16 05:58:47', '2018-04-16 05:58:47', '1,2,3,4', '2', '22');

-- ----------------------------
-- Table structure for `t_order_item`
-- ----------------------------
DROP TABLE IF EXISTS `t_order_item`;
CREATE TABLE `t_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `description` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `service_begin_time` datetime DEFAULT NULL COMMENT '服务开始时间',
  `service_end_time` datetime DEFAULT NULL COMMENT '服务开始时间',
  `service_id` bigint(20) NOT NULL COMMENT '服务ID',
  `status` int(11) DEFAULT '1' COMMENT '订单状态 - 1:新建,2:生效,3:过期',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_order_item
-- ----------------------------
INSERT INTO `t_order_item` VALUES ('1', '2018-04-16 15:33:34', '2018-04-16 15:33:34', '没什么可描述的', '2018-04-16 05:58:47', '2018-04-16 05:58:47', '1', '1', '22');
INSERT INTO `t_order_item` VALUES ('2', '2018-04-16 15:33:34', '2018-04-16 15:33:34', '没什么可描述的', '2018-04-16 05:58:47', '2018-04-16 05:58:47', '2', '1', '22');
INSERT INTO `t_order_item` VALUES ('3', '2018-04-16 15:33:34', '2018-04-16 15:33:34', '没什么可描述的', '2018-04-16 05:58:47', '2018-04-16 05:58:47', '3', '2', '22');
INSERT INTO `t_order_item` VALUES ('4', '2018-04-16 15:33:34', '2018-04-16 15:33:34', '没什么可描述的', '2018-04-16 05:58:47', '2018-04-16 05:58:47', '4', '1', '22');

-- ----------------------------
-- Table structure for `t_permission`
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '权限名称',
  `action` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '权限行为',
  `group` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '所在分组',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父节点id',
  `resource_type` enum('menu','button') COLLATE utf8_bin NOT NULL COMMENT '资源类型',
  `sort` bigint(20) NOT NULL DEFAULT '0' COMMENT '所在排序',
  `available` bit(1) DEFAULT b'1' COMMENT '是否可用',
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `action` (`action`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_permission
-- ----------------------------
INSERT INTO `t_permission` VALUES ('1', '系统列表', 'sys:list', '系统管理', '0', 'menu', '0', '', '2018-02-23 22:56:11', '2018-02-26 00:46:20');
INSERT INTO `t_permission` VALUES ('2', '系统添加', 'sys:add', '系统管理', '1', 'button', '0', '', '2018-02-23 22:58:35', '2018-02-23 22:58:37');
INSERT INTO `t_permission` VALUES ('3', '系统修改', 'sys:update', '系统管理', '1', 'button', '0', '', '2018-02-23 22:59:15', '2018-02-23 22:59:18');
INSERT INTO `t_permission` VALUES ('4', '系统删除', 'sys:delete', '系统管理', '1', 'button', '0', '', '2018-02-23 22:59:50', '2018-02-26 00:35:49');
INSERT INTO `t_permission` VALUES ('5', '用户列表', 'user:list', '用户管理', '1', 'menu', '1', '', '2018-02-26 00:37:02', '2018-02-26 00:37:02');
INSERT INTO `t_permission` VALUES ('6', '用户添加', 'user:add', '用户管理', '5', 'button', '1', '', '2018-02-26 00:37:31', '2018-02-26 00:37:31');
INSERT INTO `t_permission` VALUES ('7', '用户修改', 'user:update', '用户管理', '5', 'button', '1', '', '2018-02-26 00:38:03', '2018-02-26 00:38:03');
INSERT INTO `t_permission` VALUES ('8', '用户删除', 'user:delete', '用户管理', '5', 'button', '1', '', '2018-02-26 00:38:16', '2018-02-26 00:38:16');
INSERT INTO `t_permission` VALUES ('9', '角色列表', 'role:list', '角色管理', '1', 'menu', '2', '', '2018-02-26 00:39:01', '2018-02-26 00:39:01');
INSERT INTO `t_permission` VALUES ('10', '角色添加', 'role:add', '角色管理', '9', 'button', '2', '', '2018-02-26 00:39:25', '2018-02-26 00:39:25');
INSERT INTO `t_permission` VALUES ('11', '角色修改', 'role:update', '角色管理', '9', 'button', '2', '', '2018-02-26 00:39:49', '2018-02-26 00:39:49');
INSERT INTO `t_permission` VALUES ('12', '角色删除', 'role:delete', '角色管理', '9', 'button', '2', '', '2018-02-26 00:40:09', '2018-02-26 00:40:09');
INSERT INTO `t_permission` VALUES ('13', '角色权限分配', 'role:rolepermission', '角色管理', '9', 'button', '2', '', '2018-02-26 00:42:12', '2018-02-26 00:42:12');
INSERT INTO `t_permission` VALUES ('14', '角色用户列表', 'role:roleuser', '角色管理', '9', 'menu', '2', '', '2018-02-26 00:42:40', '2018-02-28 20:26:51');
INSERT INTO `t_permission` VALUES ('15', '登录列表', 'login:list', '登录管理', '1', 'menu', '3', '', '2018-02-28 20:28:49', '2018-02-28 20:28:49');
INSERT INTO `t_permission` VALUES ('16', '登录增加', 'login:add', '登录管理', '15', 'button', '3', '', '2018-02-28 20:29:12', '2018-02-28 20:29:44');
INSERT INTO `t_permission` VALUES ('17', '登录修改', 'login:update', '登录管理', '15', 'button', '3', '', '2018-02-28 20:29:38', '2018-02-28 20:29:38');
INSERT INTO `t_permission` VALUES ('18', '登录删除', 'login:delete', '登录管理', '15', 'button', '3', '', '2018-02-28 20:29:58', '2018-02-28 20:29:58');

-- ----------------------------
-- Table structure for `t_qrtz_blob_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_blob_triggers`;
CREATE TABLE `t_qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `t_qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `t_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `t_qrtz_calendars`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_calendars`;
CREATE TABLE `t_qrtz_calendars` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `CALENDAR_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for `t_qrtz_cron_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_cron_triggers`;
CREATE TABLE `t_qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `CRON_EXPRESSION` varchar(120) COLLATE utf8_bin NOT NULL,
  `TIME_ZONE_ID` varchar(80) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `t_qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `t_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_cron_triggers
-- ----------------------------
INSERT INTO `t_qrtz_cron_triggers` VALUES ('quartzScheduler', 'com.moraydata.general.management.schedule.PushPublicSentimentJob', 'test', '0 0/1 * * * ?', 'Asia/Shanghai');
INSERT INTO `t_qrtz_cron_triggers` VALUES ('quartzScheduler', 'com.moraydata.general.management.schedule.TestQuartz', 'test', '0/5 * * * * ?', 'Asia/Shanghai');

-- ----------------------------
-- Table structure for `t_qrtz_fired_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_fired_triggers`;
CREATE TABLE `t_qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `ENTRY_ID` varchar(95) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `INSTANCE_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) COLLATE utf8_bin NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`),
  KEY `IDX_T_QRTZ_FT_TRIG_INST_NAME` (`SCHED_NAME`,`INSTANCE_NAME`),
  KEY `IDX_T_QRTZ_FT_INST_JOB_REQ_RCVRY` (`SCHED_NAME`,`INSTANCE_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_T_QRTZ_FT_J_G` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_T_QRTZ_FT_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_T_QRTZ_FT_T_G` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_T_QRTZ_FT_TG` (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `t_qrtz_job_details`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_job_details`;
CREATE TABLE `t_qrtz_job_details` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) COLLATE utf8_bin NOT NULL,
  `IS_DURABLE` varchar(1) COLLATE utf8_bin NOT NULL,
  `IS_NONCONCURRENT` varchar(1) COLLATE utf8_bin NOT NULL,
  `IS_UPDATE_DATA` varchar(1) COLLATE utf8_bin NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) COLLATE utf8_bin NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_T_QRTZ_J_REQ_RECOVERY` (`SCHED_NAME`,`REQUESTS_RECOVERY`),
  KEY `IDX_T_QRTZ_J_GRP` (`SCHED_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_job_details
-- ----------------------------
INSERT INTO `t_qrtz_job_details` VALUES ('quartzScheduler', 'com.moraydata.general.management.schedule.PushPublicSentimentJob', 'test', null, 'com.moraydata.general.management.schedule.PushPublicSentimentJob', '0', '0', '0', '0', 0x230D0A23546875204D61792030332031383A32363A30332043535420323031380D0A);
INSERT INTO `t_qrtz_job_details` VALUES ('quartzScheduler', 'com.moraydata.general.management.schedule.TestQuartz', 'test', null, 'com.moraydata.general.management.schedule.TestQuartz', '0', '0', '0', '0', 0x230D0A23547565204170722032342031313A34343A33362043535420323031380D0A);

-- ----------------------------
-- Table structure for `t_qrtz_locks`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_locks`;
CREATE TABLE `t_qrtz_locks` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `LOCK_NAME` varchar(40) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_locks
-- ----------------------------
INSERT INTO `t_qrtz_locks` VALUES ('quartzScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for `t_qrtz_paused_trigger_grps`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_paused_trigger_grps`;
CREATE TABLE `t_qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for `t_qrtz_scheduler_state`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_scheduler_state`;
CREATE TABLE `t_qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `INSTANCE_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for `t_qrtz_simple_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_simple_triggers`;
CREATE TABLE `t_qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `t_qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `t_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `t_qrtz_simprop_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_simprop_triggers`;
CREATE TABLE `t_qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `STR_PROP_1` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `STR_PROP_2` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `STR_PROP_3` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `t_qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `t_qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for `t_qrtz_triggers`
-- ----------------------------
DROP TABLE IF EXISTS `t_qrtz_triggers`;
CREATE TABLE `t_qrtz_triggers` (
  `SCHED_NAME` varchar(120) COLLATE utf8_bin NOT NULL,
  `TRIGGER_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `TRIGGER_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `JOB_NAME` varchar(200) COLLATE utf8_bin NOT NULL,
  `JOB_GROUP` varchar(200) COLLATE utf8_bin NOT NULL,
  `DESCRIPTION` varchar(250) COLLATE utf8_bin DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) COLLATE utf8_bin NOT NULL,
  `TRIGGER_TYPE` varchar(8) COLLATE utf8_bin NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_T_QRTZ_T_J` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  KEY `IDX_T_QRTZ_T_JG` (`SCHED_NAME`,`JOB_GROUP`),
  KEY `IDX_T_QRTZ_T_C` (`SCHED_NAME`,`CALENDAR_NAME`),
  KEY `IDX_T_QRTZ_T_G` (`SCHED_NAME`,`TRIGGER_GROUP`),
  KEY `IDX_T_QRTZ_T_STATE` (`SCHED_NAME`,`TRIGGER_STATE`),
  KEY `IDX_T_QRTZ_T_N_STATE` (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_T_QRTZ_T_N_G_STATE` (`SCHED_NAME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  KEY `IDX_T_QRTZ_T_NEXT_FIRE_TIME` (`SCHED_NAME`,`NEXT_FIRE_TIME`),
  KEY `IDX_T_QRTZ_T_NFT_ST` (`SCHED_NAME`,`TRIGGER_STATE`,`NEXT_FIRE_TIME`),
  KEY `IDX_T_QRTZ_T_NFT_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`),
  KEY `IDX_T_QRTZ_T_NFT_ST_MISFIRE` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_STATE`),
  KEY `IDX_T_QRTZ_T_NFT_ST_MISFIRE_GRP` (`SCHED_NAME`,`MISFIRE_INSTR`,`NEXT_FIRE_TIME`,`TRIGGER_GROUP`,`TRIGGER_STATE`),
  CONSTRAINT `t_qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `t_qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_qrtz_triggers
-- ----------------------------
INSERT INTO `t_qrtz_triggers` VALUES ('quartzScheduler', 'com.moraydata.general.management.schedule.PushPublicSentimentJob', 'test', 'com.moraydata.general.management.schedule.PushPublicSentimentJob', 'test', null, '1525441800000', '1525441779927', '5', 'PAUSED', 'CRON', '1525343163000', '0', null, '0', '');
INSERT INTO `t_qrtz_triggers` VALUES ('quartzScheduler', 'com.moraydata.general.management.schedule.TestQuartz', 'test', 'com.moraydata.general.management.schedule.TestQuartz', 'test', null, '1524564575000', '1524564570000', '5', 'PAUSED', 'CRON', '1524541476000', '0', null, '0', '');

-- ----------------------------
-- Table structure for `t_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `available` bit(1) DEFAULT b'1' COMMENT '是否可用',
  `name` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '角色名称',
  `priority` int(11) DEFAULT '99' COMMENT '角色优先级 - 优先级数值越低,代表优先级越大,角色normal的优先级为99',
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('1', '', 'admin', '0', '2018-01-25 23:16:22', '2018-01-25 23:16:25');
INSERT INTO `t_role` VALUES ('2', '', 'manager', '1', '2018-01-25 23:16:59', '2018-01-25 23:17:02');
INSERT INTO `t_role` VALUES ('3', '', 'normal', '99', '2018-01-25 23:17:36', '2018-02-25 23:26:41');
INSERT INTO `t_role` VALUES ('4', '', 'trial', '99', '2018-04-09 09:26:38', '2018-04-09 09:26:41');
INSERT INTO `t_role` VALUES ('5', '', 'master', '97', '2018-04-09 09:28:28', '2018-04-09 09:28:30');
INSERT INTO `t_role` VALUES ('6', '', 'slave', '98', '2018-04-09 09:28:48', '2018-04-09 09:28:50');

-- ----------------------------
-- Table structure for `t_role_permission`
-- ----------------------------
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `FKjobmrl6dorhlfite4u34hciik` (`permission_id`),
  KEY `FK90j038mnbnthgkc17mqnoilu9` (`role_id`),
  CONSTRAINT `FK90j038mnbnthgkc17mqnoilu9` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`),
  CONSTRAINT `FKjobmrl6dorhlfite4u34hciik` FOREIGN KEY (`permission_id`) REFERENCES `t_permission` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_role_permission
-- ----------------------------
INSERT INTO `t_role_permission` VALUES ('1', '1', '1', '2018-02-23 23:25:46', '2018-02-23 23:25:48');
INSERT INTO `t_role_permission` VALUES ('2', '1', '2', '2018-02-23 23:25:58', '2018-02-23 23:26:00');
INSERT INTO `t_role_permission` VALUES ('3', '1', '3', '2018-02-23 23:26:07', '2018-02-23 23:26:10');
INSERT INTO `t_role_permission` VALUES ('4', '1', '4', '2018-02-23 23:26:18', '2018-02-23 23:26:21');
INSERT INTO `t_role_permission` VALUES ('5', '1', '5', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('6', '1', '6', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('7', '1', '7', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('8', '1', '8', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('9', '1', '9', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('10', '1', '10', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('11', '1', '11', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('12', '1', '12', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('13', '1', '13', '2018-02-26 00:44:59', '2018-02-26 00:44:59');
INSERT INTO `t_role_permission` VALUES ('14', '1', '14', '2018-02-26 00:44:59', '2018-02-26 00:44:59');

-- ----------------------------
-- Table structure for `t_sequence`
-- ----------------------------
DROP TABLE IF EXISTS `t_sequence`;
CREATE TABLE `t_sequence` (
  `name` varchar(50) COLLATE utf8_bin NOT NULL,
  `min` int(11) NOT NULL,
  `max` int(11) NOT NULL,
  `current` int(11) NOT NULL,
  `increment` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_sequence
-- ----------------------------
INSERT INTO `t_sequence` VALUES ('order_code', '100001', '199999', '100005', '1');

-- ----------------------------
-- Table structure for `t_service`
-- ----------------------------
DROP TABLE IF EXISTS `t_service`;
CREATE TABLE `t_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `available` bit(1) DEFAULT b'1' COMMENT '是否可用',
  `name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '服务名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_service
-- ----------------------------
INSERT INTO `t_service` VALUES ('1', '2018-04-16 15:20:25', '2018-04-16 15:20:29', '', '旅游舆情');
INSERT INTO `t_service` VALUES ('2', '2018-04-16 15:20:39', '2018-04-16 15:20:42', '', '满意度洞察');
INSERT INTO `t_service` VALUES ('3', '2018-04-16 15:20:51', '2018-04-16 15:20:53', '', '影响力评价');
INSERT INTO `t_service` VALUES ('4', '2018-04-16 15:21:02', '2018-04-16 15:21:04', '', '传播分析');
INSERT INTO `t_service` VALUES ('5', '2018-04-16 15:21:12', '2018-04-16 15:21:15', '', '可视化大屏');
INSERT INTO `t_service` VALUES ('6', '2018-04-16 15:21:23', '2018-04-16 15:21:26', '', '报告中心');
INSERT INTO `t_service` VALUES ('7', '2018-04-16 15:21:35', '2018-04-16 15:21:37', '', '数据中心');

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `email` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '电子邮箱',
  `nickname` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '昵称',
  `password` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '密码',
  `status` int(11) DEFAULT '1' COMMENT '用户状态 - 1:正常,0:冻结,-1:删除',
  `username` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '用户名',
  `phone` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '电话号码',
  `photo` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '头像照片',
  `avatar_url` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '用户头像网络地址',
  `creator` bigint(20) DEFAULT '0' COMMENT '创建者',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '上级用户ID',
  `count_of_invitation_codes` smallint(6) DEFAULT '0' COMMENT '邀请码数量',
  `company` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '所属企业',
  `company_fax` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '企业传真',
  `company_location` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '企业所在地',
  `company_phone` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '企业联系电话',
  `company_title` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '企业中职务',
  `company_type` varchar(10) COLLATE utf8_bin DEFAULT NULL COMMENT '企业类型',
  `open_id` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '微信openId',
  `description` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '描述',
  `order_item_ids` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '可用服务订单细则编号',
  `level` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户级别',
  `notified` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否接受通知',
  `notified_hot_public_sentiment` enum('non','all','related') COLLATE utf8_bin NOT NULL DEFAULT 'non' COMMENT '热点舆情接受状态',
  `notified_negative_public_sentiment` enum('non','all','related') COLLATE utf8_bin NOT NULL DEFAULT 'non' COMMENT '负面舆情接受状态',
  `notified_warning_public_sentiment` enum('non','all','related') COLLATE utf8_bin NOT NULL DEFAULT 'non' COMMENT '预警舆情接受状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', '2018-01-25 21:23:49', '2018-02-28 15:14:18', 'beamofsoul@sina.com', '张三', '$2a$10$ooB04XZxrLnGMroyXC0Qrua3Gvp4ZMOPwb6cH1gY.UQpEalE1HCAa', '1', 'beamofsoul', '', '', null, '0', '0', '0', '什么什么有限责任公司', 'aaaaaaaaaaaaaaaaaaa', '上海浦东新区纳贤路701号百度上研大厦', '0000000000110', '总经理助理', '其他类型', '', '这里是描述信息这里是描述信息', null, '3', '', 'related', 'non', 'all');
INSERT INTO `t_user` VALUES ('12', '2018-02-01 20:14:33', '2018-03-01 23:32:28', 'tutou1@gmail.com', 'tutou1', '$2a$10$4ea07GgQgJKGr7I4o7rk5.IyxOpSZAObiXWMlqz8vXiiR4ILea2FW', '1', 'testuser1', '13200000000', '', null, '0', '0', '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('21', '2018-03-25 18:24:03', '2018-03-25 18:24:03', null, 'Justin', '$2a$10$K1839SWbAxuscaBKcGu7uubJmD1tmDNK1PlWediqzG.tE8W1D0J3W', '1', 'od4PTw_ORXTmY8EESquabIhBIya4', null, null, '', '0', '0', '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('22', '2018-04-09 15:03:10', '2018-04-09 15:03:13', null, '张三', '$2a$10$4ea07GgQgJKGr7I4o7rk5.IyxOpSZAObiXWMlqz8vXiiR4ILea2FW', '1', 'zhangsan', '13200000000', null, null, '0', '0', '0', '啊啊啊', '', '请选择省份请选择城市请选择区县', '1231233333', '哦哦哦', '景区', '', 'zhangsan', null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('38', '2018-04-12 11:57:43', '2018-04-12 11:57:43', null, null, '$2a$10$D2BOEG8/2d4fkulpUb6iPe.8n/QiaZOHQnAs.C1iYEM1dmmTC7Q.G', '1', 'dapengdapeng', null, null, null, null, null, '0', null, null, null, null, null, null, '', null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('43', '2018-04-12 17:39:27', '2018-04-12 17:39:27', null, null, '$2a$10$NTEmU3KkOOP1VNIKkgD1OOhOfB5KN5D52tj8gqBQdlqmdrmb1HNsy', '1', 'zhanglaosan', '15806660675', null, null, null, null, '0', null, null, null, null, null, null, null, '有可能是大鹏的', null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('44', '2018-04-16 08:09:42', '2018-04-16 08:09:42', null, null, '$2a$10$VSkxUrYJVFfd1QshH2uuzuNz/2yvb3ejNJra4tg0qW84DjQfXKInu', '1', 'bilibili', '13200000001', null, null, null, '1', '0', null, null, null, null, null, null, '', '没什么可描述的', null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('45', '2018-04-17 06:19:13', '2018-04-17 06:19:13', null, '张三三', '$2a$10$sefonXuXrv3fqpUvA3o9netbdxOuxDuWm8HmOx/Ru9tki3zj4q9Dm', '1', 'alibaba', '13200000002', null, null, null, '22', '0', '', '', '请选择省份请选择城市请选择区县', '', '', '', 'oQFmP07bX--NREsmAuT3054uU9WQ', 'string', '1', '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('53', '2018-04-28 13:57:27', '2018-04-28 13:57:27', null, null, '$2a$10$JeQOJgok20nSQLdZRiXwz.S/isPJlNcrd/TEHphdPB3rewh6f.5aO', '1', 'oQFmP0-K4IvdQvocmDHiJ5aPn9Uk', null, null, null, null, null, '0', null, null, null, null, null, null, 'oQFmP0-K4IvdQvocmDHiJ5aPn9Uk', null, null, '1', '', 'all', 'all', 'related');
INSERT INTO `t_user` VALUES ('55', '2018-04-28 14:06:45', '2018-04-28 14:06:45', null, null, '$2a$10$aINMrfKgf7JQh.Y2Vrmffuor45J/U0ANYqOiQYoVg0j4y5ISIOZGm', '1', 'oQFmP0_YO_oDRGDY5U9cJGOTzFn0', null, null, null, null, null, '0', null, null, null, null, null, null, 'oQFmP0_YO_oDRGDY5U9cJGOTzFn0', null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('56', '2018-04-28 16:03:25', '2018-04-28 16:03:25', null, null, '$2a$10$hVbnUK6nSWJuSYQgRusrGemZoAYlkBzHIG1ZZ0k27TYDlu.81s3xG', '1', 'jstone', '18611133559', null, null, null, null, '0', null, null, null, null, null, null, 'oQFmP06LOs9VKTzQgV635Y5aYXi8', '张总', null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('60', '2018-05-03 13:22:00', '2018-05-03 13:22:00', null, null, '$2a$10$q1NhJ3zs9UFhPUPx8bE9H.i.ez/0bLHtGW7Qy2Watcd3hI5PXD8R6', '1', 'asdasd', '', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('62', '2018-05-03 18:49:59', '2018-05-03 18:49:59', null, null, '$2a$10$GsKXzk5XEcRZyx3VebXhzupWDbKrks.zri927yOvj9EdFIZHEACIC', '1', 'oQFmP01E8wBBiJMVCh_wQbcitOz0', '17611575662', null, null, null, null, '0', null, null, null, null, null, null, 'oQFmP01E8wBBiJMVCh_wQbcitOz0', '一定是闻潮', null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('86', '2018-05-08 14:12:03', '2018-05-08 14:12:03', null, null, '$2a$10$HAneAdU2RcXIKGJkhfq4Q.ew7vDFsNuWT3RORgg4oVOPKZJxlm8qW', '1', 'oQFmP06XJkdxm2-HUl41Q6na91Tk', null, null, null, null, null, '0', null, null, null, null, null, null, 'oQFmP06XJkdxm2-HUl41Q6na91Tk', null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('87', '2018-05-08 14:43:09', '2018-05-08 14:43:09', null, null, '$2a$10$4zTSlMKX.Jq0C1CVgGGo5OU4UbPjXIrQdV0PcM.JGk1LP5a4ehoHe', '1', 'zhanglaojiu', '', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('89', '2018-05-08 18:47:03', '2018-05-08 18:47:03', null, null, '$2a$10$2RWDCEJ7MFTyoCuRn8OaX.aBWGnoWZ//ziq6wxSp9tDXT4C24C/CC', '1', 'wanglaoliu', '', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('90', '2018-05-09 17:17:03', '2018-05-09 17:17:03', null, null, '$2a$10$D1CA/Okc2s8OQ3G1mK9IE.7P2ukB67qY0UX6LSkK1dK2pMni.TIFG', '1', 'zhanglaoshi', '', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('91', '2018-05-09 17:20:35', '2018-05-09 17:20:35', null, null, '$2a$10$MKnidXfJEBm8hCffMIJ7S.8SCV1IK19bxBWNxu8GLblVUqa7uFj6u', '1', 'lilaoshi', '', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('92', '2018-05-09 17:22:37', '2018-05-09 17:22:37', null, null, '$2a$10$zQZHz1xaJMWDQtPv2BDGKeirrUARFnV.w9myCfu/vn4mqmZ.fJ2ii', '1', 'wanglaoqi', '', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('110', '2018-05-10 14:59:35', '2018-05-10 14:59:35', null, null, '$2a$10$T.a3JF0ZV1L5RajbKiFogurwbIKi0Wri4C9cbMjhp7Y0ivFoMcwvW', '1', 'hx837866040', '17611575662', null, null, null, null, '0', null, null, null, null, null, null, 'oQFmP09rAj4o3lPUblbgZMfp0T24', null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('111', '2018-05-10 15:37:48', '2018-05-10 15:37:48', null, null, '$2a$10$t51tbiwrSka5/CEfvPKZWeufp2EItpZIU9pJNrGuzdoFfe4uL6CP2', '1', 'test111', '15581642441', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');
INSERT INTO `t_user` VALUES ('112', '2018-05-11 11:30:42', '2018-05-11 11:30:42', null, null, '$2a$10$pGPviwSCaZxwAOIfAQjpyuvPXMDbkKYYwCoLAtvjfW6NNj0HhG7sG', '1', 'test222', '15117963717', null, null, null, null, '0', null, null, null, null, null, null, null, null, null, '1', '', 'non', 'non', 'non');

-- ----------------------------
-- Table structure for `t_userconnection`
-- ----------------------------
DROP TABLE IF EXISTS `t_userconnection`;
CREATE TABLE `t_userconnection` (
  `userId` varchar(255) COLLATE utf8_bin NOT NULL,
  `providerId` varchar(255) COLLATE utf8_bin NOT NULL,
  `providerUserId` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT '',
  `rank` int(11) NOT NULL,
  `displayName` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `profileUrl` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `imageUrl` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `accessToken` varchar(512) COLLATE utf8_bin NOT NULL,
  `secret` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `refreshToken` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `expireTime` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`userId`,`providerId`,`providerUserId`),
  UNIQUE KEY `UserConnectionRank` (`userId`,`providerId`,`rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_userconnection
-- ----------------------------
INSERT INTO `t_userconnection` VALUES ('21', 'weixin', 'od4PTw_ORXTmY8EESquabIhBIya4', '1', 'Justin', null, 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLsib5qXNA3ADCQADapGuQsl4HUoELnp3uOfMxxaFsMnt6PeanVTmianFjiciaaicdeGZ9fQQMvZwKl5eQ/132', '8_znLmI4ad3SEWt7ket52N4d_jg0sUBP-P2ZBtbBa0a-8fcOys0HuFOlgi2oJrQmk_EUlksnNotP6BRm-4_sWA1g', null, '8_JOK9Erfg0FY0yaP433FePjCQd2wRjKXs4fvIygIaZg_U_xxXX9oWH5-jyoD7r40EdvGLItZXWHyisP8zcX06Jg', '1521981454674');

-- ----------------------------
-- Table structure for `t_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `role_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa9c8iiy6ut0gnx491fqx4pxam` (`role_id`),
  KEY `FKq5un6x7ecoef5w1n39cop66kl` (`user_id`),
  CONSTRAINT `FKa9c8iiy6ut0gnx491fqx4pxam` FOREIGN KEY (`role_id`) REFERENCES `t_role` (`id`),
  CONSTRAINT `FKq5un6x7ecoef5w1n39cop66kl` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES ('1', '2018-02-01 16:39:25', '2018-02-01 16:39:27', '1', '1');
INSERT INTO `t_user_role` VALUES ('21', '2018-04-12 11:57:43', '2018-04-12 11:57:43', '4', '38');
INSERT INTO `t_user_role` VALUES ('26', '2018-04-12 17:39:27', '2018-04-12 17:39:27', '4', '43');
INSERT INTO `t_user_role` VALUES ('27', '2018-04-16 17:16:18', '2018-04-16 17:16:18', '6', '44');
INSERT INTO `t_user_role` VALUES ('28', '2018-04-17 14:31:44', '2018-04-17 14:31:44', '4', '45');
INSERT INTO `t_user_role` VALUES ('29', '2018-04-18 17:27:30', '2018-04-18 17:27:33', '5', '22');
INSERT INTO `t_user_role` VALUES ('30', '2018-04-18 17:28:03', '2018-04-18 17:28:06', '4', '22');
INSERT INTO `t_user_role` VALUES ('38', '2018-04-28 13:57:27', '2018-04-28 13:57:27', '4', '53');
INSERT INTO `t_user_role` VALUES ('40', '2018-04-28 14:06:45', '2018-04-28 14:06:45', '4', '55');
INSERT INTO `t_user_role` VALUES ('41', '2018-04-28 16:03:25', '2018-04-28 16:03:25', '4', '56');
INSERT INTO `t_user_role` VALUES ('45', '2018-05-03 13:22:00', '2018-05-03 13:22:00', '4', '60');
INSERT INTO `t_user_role` VALUES ('47', '2018-05-03 18:49:59', '2018-05-03 18:49:59', '4', '62');
INSERT INTO `t_user_role` VALUES ('71', '2018-05-08 14:12:03', '2018-05-08 14:12:03', '4', '86');
INSERT INTO `t_user_role` VALUES ('72', '2018-05-08 14:43:09', '2018-05-08 14:43:09', '4', '87');
INSERT INTO `t_user_role` VALUES ('74', '2018-05-08 18:47:03', '2018-05-08 18:47:03', '4', '89');
INSERT INTO `t_user_role` VALUES ('75', '2018-05-09 17:17:03', '2018-05-09 17:17:03', '4', '90');
INSERT INTO `t_user_role` VALUES ('76', '2018-05-09 17:20:35', '2018-05-09 17:20:35', '4', '91');
INSERT INTO `t_user_role` VALUES ('77', '2018-05-09 17:22:37', '2018-05-09 17:22:37', '4', '92');
INSERT INTO `t_user_role` VALUES ('88', '2018-05-10 14:59:35', '2018-05-10 14:59:35', '4', '110');
INSERT INTO `t_user_role` VALUES ('89', '2018-05-10 15:37:48', '2018-05-10 15:37:48', '4', '111');
INSERT INTO `t_user_role` VALUES ('90', '2018-05-11 11:30:42', '2018-05-11 11:30:42', '4', '112');

-- ----------------------------
-- View structure for `v_job_combine_trigger`
-- ----------------------------
DROP VIEW IF EXISTS `v_job_combine_trigger`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root_eel`@`%` SQL SECURITY DEFINER VIEW `v_job_combine_trigger` AS select count(0) AS `id`,`tqjd`.`JOB_NAME` AS `job_name`,`tqjd`.`JOB_GROUP` AS `job_group`,`tqjd`.`JOB_CLASS_NAME` AS `job_class_name`,`tqt`.`TRIGGER_NAME` AS `trigger_name`,`tqt`.`TRIGGER_GROUP` AS `trigger_group`,`tqct`.`CRON_EXPRESSION` AS `cron_expression`,`tqct`.`TIME_ZONE_ID` AS `time_zone_id` from ((`t_qrtz_job_details` `tqjd` join `t_qrtz_triggers` `tqt`) join `t_qrtz_cron_triggers` `tqct` on(((`tqjd`.`JOB_NAME` = `tqt`.`JOB_NAME`) and (`tqt`.`TRIGGER_NAME` = `tqct`.`TRIGGER_NAME`) and (`tqt`.`TRIGGER_GROUP` = `tqct`.`TRIGGER_GROUP`)))) ;

-- ----------------------------
-- View structure for `v_user_role_combine_role`
-- ----------------------------
DROP VIEW IF EXISTS `v_user_role_combine_role`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root_eel`@`%` SQL SECURITY DEFINER VIEW `v_user_role_combine_role` AS select `tu`.`id` AS `id`,`tu`.`id` AS `user_id`,`tu`.`username` AS `username`,`tu`.`nickname` AS `nickname`,ifnull(group_concat(`tur`.`role_id` order by `tur`.`role_id` ASC separator ','),0) AS `role_id`,ifnull(group_concat(`tr`.`name` order by `tr`.`id` ASC separator ','),'') AS `role_name` from ((`t_user` `tu` left join `t_user_role` `tur` on((`tur`.`user_id` = `tu`.`id`))) left join `t_role` `tr` on((`tr`.`id` = `tur`.`role_id`))) group by `tu`.`id` ;

-- ----------------------------
-- Procedure structure for `getNextSequenceValue`
-- ----------------------------
DROP PROCEDURE IF EXISTS `getNextSequenceValue`;
DELIMITER ;;
CREATE DEFINER=`root_eel`@`%` PROCEDURE `getNextSequenceValue`(IN name varchar(50), OUT code int)
BEGIN
	declare _cur int;  
	declare _maxvalue int;  -- 接收最大值  
	declare _increment int; -- 接收增长步数  
	set _increment = (select ts.increment from t_sequence ts where ts.name = name);  
	set _maxvalue = (select ts.max from t_sequence ts where ts.name = name);  
	set _cur = (select ts.current from t_sequence ts where ts.name = name);    
	update t_sequence                      -- 更新当前值  
	 set current = _cur + increment    
	 where name = name ;    
	if(_cur + _increment >= _maxvalue) then  -- 判断是都达到最大值  
				update t_sequence    
					set current = minvalue    
					where name = name ;  
	end if;  
	set code = _cur;
END
;;
DELIMITER ;
