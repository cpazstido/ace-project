-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        5.7.21 - MySQL Community Server (GPL)
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 trade 的数据库结构
DROP DATABASE IF EXISTS `trade`;
CREATE DATABASE IF NOT EXISTS `trade` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `trade`;

-- 导出  表 trade.trade_coupon 结构
DROP TABLE IF EXISTS `trade_coupon`;
CREATE TABLE IF NOT EXISTS `trade_coupon` (
  `coupon_id` varchar(32) NOT NULL COMMENT '优惠卷id',
  `coupon_price` decimal(10,2) DEFAULT NULL COMMENT '优惠卷金额',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单id',
  `is_used` char(50) DEFAULT NULL COMMENT '是否已使用 0未使用 1已使用',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  trade.trade_coupon 的数据：~1 rows (大约)
DELETE FROM `trade_coupon`;
/*!40000 ALTER TABLE `trade_coupon` DISABLE KEYS */;
INSERT INTO `trade_coupon` (`coupon_id`, `coupon_price`, `user_id`, `order_id`, `is_used`, `used_time`) VALUES
	('123456789', 100.00, 1, NULL, '0', '2018-12-02 20:43:00');
/*!40000 ALTER TABLE `trade_coupon` ENABLE KEYS */;

-- 导出  表 trade.trade_goods 结构
DROP TABLE IF EXISTS `trade_goods`;
CREATE TABLE IF NOT EXISTS `trade_goods` (
  `goods_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `goods_name` varchar(255) DEFAULT NULL COMMENT '商品名称',
  `goods_number` int(11) DEFAULT NULL COMMENT '商品库存',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `goods_desc` varchar(255) DEFAULT NULL COMMENT '商品描述',
  `add_time` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10001 DEFAULT CHARSET=utf8;

-- 正在导出表  trade.trade_goods 的数据：~1 rows (大约)
DELETE FROM `trade_goods`;
/*!40000 ALTER TABLE `trade_goods` DISABLE KEYS */;
INSERT INTO `trade_goods` (`goods_id`, `goods_name`, `goods_number`, `goods_price`, `goods_desc`, `add_time`) VALUES
	(10000, 'iphone7', 10, 5000.00, '苹果手机7', '2018-12-02 18:38:10');
/*!40000 ALTER TABLE `trade_goods` ENABLE KEYS */;

-- 导出  表 trade.trade_goods_number_log 结构
DROP TABLE IF EXISTS `trade_goods_number_log`;
CREATE TABLE IF NOT EXISTS `trade_goods_number_log` (
  `goods_id` int(11) NOT NULL COMMENT '商品id',
  `order_id` varchar(32) NOT NULL COMMENT '订单id',
  `goods_number` int(11) DEFAULT NULL COMMENT '库存数量',
  `log_time` datetime DEFAULT NULL,
  PRIMARY KEY (`goods_id`,`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  trade.trade_goods_number_log 的数据：~0 rows (大约)
DELETE FROM `trade_goods_number_log`;
/*!40000 ALTER TABLE `trade_goods_number_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `trade_goods_number_log` ENABLE KEYS */;

-- 导出  表 trade.trade_mq_consumer_log 结构
DROP TABLE IF EXISTS `trade_mq_consumer_log`;
CREATE TABLE IF NOT EXISTS `trade_mq_consumer_log` (
  `group_name` varchar(255) NOT NULL COMMENT '消费组名',
  `msg_tag` varchar(255) NOT NULL COMMENT '消息TAG',
  `msg_keys` varchar(255) NOT NULL COMMENT '业务ID',
  `msg_id` varchar(255) DEFAULT NULL COMMENT '消息ID',
  `msg_body` varchar(1024) DEFAULT NULL COMMENT '消息内容',
  `consumer_status` char(1) DEFAULT NULL COMMENT '消息状态 0正在处理 1处理成功 2处理失败',
  `consumer_times` int(11) DEFAULT NULL COMMENT '消费次数',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注（错误原因）',
  PRIMARY KEY (`group_name`,`msg_tag`,`msg_keys`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='mq消息消费日志表';

-- 正在导出表  trade.trade_mq_consumer_log 的数据：~0 rows (大约)
DELETE FROM `trade_mq_consumer_log`;
/*!40000 ALTER TABLE `trade_mq_consumer_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `trade_mq_consumer_log` ENABLE KEYS */;

-- 导出  表 trade.trade_mq_producer_temp 结构
DROP TABLE IF EXISTS `trade_mq_producer_temp`;
CREATE TABLE IF NOT EXISTS `trade_mq_producer_temp` (
  `id` varchar(255) NOT NULL,
  `group_name` varchar(255) NOT NULL COMMENT '生成者组名',
  `msg_topic` varchar(255) NOT NULL COMMENT '消息主题',
  `msg_tag` varchar(255) NOT NULL COMMENT '消息tag',
  `msg_keys` varchar(255) NOT NULL COMMENT '消息keys',
  `msg_body` varchar(255) DEFAULT NULL COMMENT '消息内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息发送临时表';

-- 正在导出表  trade.trade_mq_producer_temp 的数据：~0 rows (大约)
DELETE FROM `trade_mq_producer_temp`;
/*!40000 ALTER TABLE `trade_mq_producer_temp` DISABLE KEYS */;
/*!40000 ALTER TABLE `trade_mq_producer_temp` ENABLE KEYS */;

-- 导出  表 trade.trade_order 结构
DROP TABLE IF EXISTS `trade_order`;
CREATE TABLE IF NOT EXISTS `trade_order` (
  `order_id` varchar(32) NOT NULL COMMENT '订单id',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `order_status` char(1) DEFAULT NULL COMMENT '订单状态 0未确认 1已确认 2已取消 3无效 4退货',
  `pay_status` char(1) DEFAULT NULL COMMENT '支付状态 0未支付 1支付中 2已付款',
  `shopping_status` char(1) DEFAULT NULL COMMENT '发货状态 0未发货 1已发货 2已收货',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `consignee` varchar(255) DEFAULT NULL COMMENT '收货人',
  `goods_id` int(11) DEFAULT NULL COMMENT '商品ID',
  `goods_number` int(11) DEFAULT NULL COMMENT '商品数量',
  `goods_price` decimal(10,2) DEFAULT NULL COMMENT '商品价格',
  `goods_amount` decimal(10,2) DEFAULT NULL COMMENT '商品总价',
  `shopping_fee` decimal(10,2) DEFAULT NULL COMMENT '运费',
  `order_amount` decimal(10,2) DEFAULT NULL COMMENT '订单价格',
  `coupon_id` varchar(32) DEFAULT NULL COMMENT '优惠卷id',
  `coupon_paid` decimal(10,2) DEFAULT NULL COMMENT '优惠卷金额',
  `money_paid` decimal(10,2) DEFAULT NULL COMMENT '已付金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `add_time` datetime DEFAULT NULL COMMENT '创建时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '订单确认时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='订单表';

-- 正在导出表  trade.trade_order 的数据：~5 rows (大约)
DELETE FROM `trade_order`;
/*!40000 ALTER TABLE `trade_order` DISABLE KEYS */;
INSERT INTO `trade_order` (`order_id`, `user_id`, `order_status`, `pay_status`, `shopping_status`, `address`, `consignee`, `goods_id`, `goods_number`, `goods_price`, `goods_amount`, `shopping_fee`, `order_amount`, `coupon_id`, `coupon_paid`, `money_paid`, `pay_amount`, `add_time`, `confirm_time`, `pay_time`) VALUES
	('14c1e3352b2941a18e456daab4546bdb', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:12:10', NULL, NULL),
	('1e7c3ba52afe46778dcffb281a67423b', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:12:03', NULL, NULL),
	('262c2c1d461f4d93b0b5196f99950c9b', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:18:40', NULL, NULL),
	('3f51276fe1ec4083983d77bb0b4e00af', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:37:18', NULL, NULL),
	('4fae59b5ac4d4a578c8aea69430d5fe0', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:36:07', NULL, NULL),
	('64d36fec6e55410d9f26786dc86f507c', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:11:00', NULL, NULL),
	('6f354a7cd7934d62b4028288063bc633', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:31:25', NULL, NULL),
	('72eafac149cf47279e60a229da05004c', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:16:25', NULL, NULL),
	('780f65e727cd46ad9b462144b3d30339', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:20:24', NULL, NULL),
	('7e7d519fc480451f8dfad1613c9b929a', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:23:56', NULL, NULL),
	('a634273f399e4d8f8395c179c20d3bdc', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:13:00', NULL, NULL),
	('bd284af1a0fd49f985e29ab02a57cf97', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:26:22', NULL, NULL),
	('eeadb3cee07f4fe79c7b2e4616ba6685', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:42:59', NULL, NULL),
	('f66bd14a2ebc4bc3b344c73a21986a0a', 1, '0', '0', '0', '北京', NULL, 10000, 1, 5000.00, 5000.00, 0.00, 5000.00, '123456789', 100.00, 100.00, 4800.00, '2018-12-02 20:24:57', NULL, NULL);
/*!40000 ALTER TABLE `trade_order` ENABLE KEYS */;

-- 导出  表 trade.trade_pay 结构
DROP TABLE IF EXISTS `trade_pay`;
CREATE TABLE IF NOT EXISTS `trade_pay` (
  `pay_id` varchar(32) NOT NULL COMMENT '支付编号',
  `order_id` varchar(32) DEFAULT NULL COMMENT '订单编号',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
  `is_paid` char(1) DEFAULT NULL COMMENT '是否已支付 0否 1是',
  PRIMARY KEY (`pay_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  trade.trade_pay 的数据：~0 rows (大约)
DELETE FROM `trade_pay`;
/*!40000 ALTER TABLE `trade_pay` DISABLE KEYS */;
/*!40000 ALTER TABLE `trade_pay` ENABLE KEYS */;

-- 导出  表 trade.trade_user 结构
DROP TABLE IF EXISTS `trade_user`;
CREATE TABLE IF NOT EXISTS `trade_user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `user_name` varchar(255) DEFAULT NULL COMMENT '用户姓名',
  `user_password` varchar(255) DEFAULT NULL COMMENT '密码',
  `user_mobile` varchar(255) DEFAULT NULL COMMENT '手机号',
  `user_score` int(11) DEFAULT NULL COMMENT '积分',
  `user_reg_time` datetime DEFAULT NULL COMMENT '注册时间',
  `user_money` decimal(10,2) DEFAULT '0.00' COMMENT '余额',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- 正在导出表  trade.trade_user 的数据：~0 rows (大约)
DELETE FROM `trade_user`;
/*!40000 ALTER TABLE `trade_user` DISABLE KEYS */;
INSERT INTO `trade_user` (`user_id`, `user_name`, `user_password`, `user_mobile`, `user_score`, `user_reg_time`, `user_money`) VALUES
	(1, '张三', '123456', '15828538828', NULL, NULL, 200.00);
/*!40000 ALTER TABLE `trade_user` ENABLE KEYS */;

-- 导出  表 trade.trade_user_money_log 结构
DROP TABLE IF EXISTS `trade_user_money_log`;
CREATE TABLE IF NOT EXISTS `trade_user_money_log` (
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `order_id` varchar(32) NOT NULL COMMENT '订单id',
  `money_log_type` char(1) NOT NULL COMMENT '日志类型 1订单付款 2订单退款',
  `user_money` decimal(10,0) DEFAULT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL COMMENT '日志时间',
  PRIMARY KEY (`user_id`,`order_id`,`money_log_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  trade.trade_user_money_log 的数据：~0 rows (大约)
DELETE FROM `trade_user_money_log`;
/*!40000 ALTER TABLE `trade_user_money_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `trade_user_money_log` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
