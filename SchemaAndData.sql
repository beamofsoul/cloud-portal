/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : rabbit

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2018-04-08 19:48:56
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_invitation_code
-- ----------------------------
INSERT INTO `t_invitation_code` VALUES ('1', '2018-04-04 22:11:12', '2018-04-04 22:11:12', '', 'D7E87D056B714F20A33F93F44BF31BC6', null, '1', '1');
INSERT INTO `t_invitation_code` VALUES ('2', '2018-04-04 22:11:12', '2018-04-04 22:11:12', '', '08E71C050D704AFB939B6A1A50F5FFE3', null, '1', '1');
INSERT INTO `t_invitation_code` VALUES ('3', '2018-04-04 22:11:12', '2018-04-04 22:11:12', '', 'D26966C96C3E496590B076CE740F9DA3', null, '1', '1');
INSERT INTO `t_invitation_code` VALUES ('4', '2018-04-04 22:11:12', '2018-04-04 22:11:12', '', '48FAE1EB531046E3BEC9635D77AEC299', null, '1', '1');
INSERT INTO `t_invitation_code` VALUES ('5', '2018-04-04 22:11:12', '2018-04-04 22:11:12', '', '115A0DE1C5414AC6AC6BFB7EB74CA5F3', null, '1', '1');

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
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_role
-- ----------------------------
INSERT INTO `t_role` VALUES ('1', '', 'admin', '0', '2018-01-25 23:16:22', '2018-01-25 23:16:25');
INSERT INTO `t_role` VALUES ('2', '', 'manager', '1', '2018-01-25 23:16:59', '2018-01-25 23:17:02');
INSERT INTO `t_role` VALUES ('3', '', 'normal', '99', '2018-01-25 23:17:36', '2018-02-25 23:26:41');

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_service
-- ----------------------------

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL COMMENT '最后修改时间',
  `updated_date` datetime DEFAULT NULL COMMENT '创建时间',
  `email` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '电子邮箱',
  `nickname` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '昵称',
  `password` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '密码',
  `status` int(11) DEFAULT '1' COMMENT '用户状态 - 1:正常,0:冻结,-1:删除',
  `username` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '用户名',
  `phone` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '电话号码',
  `photo` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '头像照片',
  `avatar_url` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '用户头像网络地址',
  `creator` bigint(20) DEFAULT '0' COMMENT '创建者',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '上级用户ID',
  `count_of_invitation_codes` smallint(6) DEFAULT '0' COMMENT '邀请码数量',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('1', '2018-01-25 21:23:49', '2018-02-28 15:14:18', 'beamofsoul@sina.com', 'Justin', '$2a$10$ooB04XZxrLnGMroyXC0Qrua3Gvp4ZMOPwb6cH1gY.UQpEalE1HCAa', '1', 'beamofsoul', '18600574873', 'beamofsoul.jpeg', null, '0', '0', '0');
INSERT INTO `t_user` VALUES ('12', '2018-02-01 20:14:33', '2018-03-01 23:32:28', 'tutou1@gmail.com', 'tutou1', '$2a$10$4ea07GgQgJKGr7I4o7rk5.IyxOpSZAObiXWMlqz8vXiiR4ILea2FW', '1', 'testuser1', '13200000000', 'testuser1.jpeg', null, '0', '0', '0');
INSERT INTO `t_user` VALUES ('21', '2018-03-25 18:24:03', '2018-03-25 18:24:03', null, 'Justin', '$2a$10$K1839SWbAxuscaBKcGu7uubJmD1tmDNK1PlWediqzG.tE8W1D0J3W', '1', 'od4PTw_ORXTmY8EESquabIhBIya4', null, null, 'http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLsib5qXNA3ADCQADapGuQsl4HUoELnp3uOfMxxaFsMnt6PeanVTmianFjiciaaicdeGZ9fQQMvZwKl5eQ/132', '0', '0', '0');

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_user_role
-- ----------------------------
INSERT INTO `t_user_role` VALUES ('1', '2018-02-01 16:39:25', '2018-02-01 16:39:27', '1', '1');

-- ----------------------------
-- View structure for `v_user_role_combine_role`
-- ----------------------------
DROP VIEW IF EXISTS `v_user_role_combine_role`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `v_user_role_combine_role` AS select `tu`.`id` AS `id`,`tu`.`id` AS `user_id`,`tu`.`username` AS `username`,`tu`.`nickname` AS `nickname`,ifnull(group_concat(`tur`.`role_id` order by `tur`.`role_id` ASC separator ','),0) AS `role_id`,ifnull(group_concat(`tr`.`name` order by `tr`.`name` ASC separator ','),'') AS `role_name` from ((`t_user` `tu` left join `t_user_role` `tur` on((`tur`.`user_id` = `tu`.`id`))) left join `t_role` `tr` on((`tr`.`id` = `tur`.`role_id`))) group by `tu`.`id` ;
