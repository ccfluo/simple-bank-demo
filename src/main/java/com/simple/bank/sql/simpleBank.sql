CREATE DATABASE `simplebank`
-- simplebank.account definition

CREATE TABLE `account` (
  `account_id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(4) NOT NULL,
  `product_code` varchar(50) NOT NULL,
  `balance` decimal(15,6) NOT NULL,
  `account_status` enum('ACTIVE','CLOSE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
  `over_draft` decimal(15,2) DEFAULT NULL,
  `interest_rate` decimal(5,2) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`account_id`),
  KEY `idx_account_customer_id` (`customer_id`),
  CONSTRAINT `fk_account_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='bank account table';

-- simplebank.audit_log definition

CREATE TABLE `audit_log` (
  `audit_id` bigint NOT NULL AUTO_INCREMENT,
  `entity_type` varchar(20) NOT NULL,
  `entity_id` bigint NOT NULL,
  `operation_type` varchar(50) NOT NULL,
  `user` varchar(50) DEFAULT 'system',
  `maintenance_time` datetime NOT NULL,
  `before_data` text,
  `after_data` text,
  `ip_address` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`audit_id`),
  KEY `idx_entity_type_id` (`entity_type`,`entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='audit log table';

-- simplebank.customer definition

CREATE TABLE `customer` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(254) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- simplebank.product_purchase definition

CREATE TABLE `product_purchase` (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT COMMENT '购买记录ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `customer_id` bigint NOT NULL COMMENT '客户ID',
  `account_id` bigint NOT NULL COMMENT '支付账户ID',
  `purchase_amount` decimal(19,2) NOT NULL COMMENT '购买金额',
  `purchase_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '购买时间',
  `status` varchar(20) NOT NULL COMMENT '状态（HOLDING, REDEEMED, EXPIRED）',
  `transaction_trace_id` varchar(100) NOT NULL COMMENT '交易追踪ID',
  PRIMARY KEY (`purchase_id`),
  UNIQUE KEY `transaction_trace_id` (`transaction_trace_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `fk_purchase_account` (`account_id`),
  CONSTRAINT `fk_purchase_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `fk_purchase_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `fk_purchase_product` FOREIGN KEY (`product_id`) REFERENCES `wealth_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- simplebank.transaction_history definition

CREATE TABLE `transaction_history` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `transaction_amount` decimal(15,6) NOT NULL,
  `transaction_type` enum('CREDIT','DEBIT') DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `account_id` bigint DEFAULT NULL,
  `account_balance` decimal(15,6) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `transaction_trace_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`transaction_id`),
  KEY `idx_account_transaction_account_id` (`account_id`),
  KEY `idx_account_transaction_date` (`transaction_id`),
  KEY `fk_account_transaction_customer` (`customer_id`),
  CONSTRAINT `fk_account_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `fk_account_transaction_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- simplebank.transfer_history definition

CREATE TABLE `transfer_history` (
  `transfer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '转账记录ID（主键）',
  `transfer_trace_id` varchar(64) NOT NULL COMMENT '全局追踪ID（幂等性标识）',
  `from_account_id` bigint NOT NULL COMMENT '转出账户ID',
  `to_account_id` bigint NOT NULL COMMENT '转入账户ID',
  `transfer_amount` decimal(19,2) NOT NULL COMMENT '转账金额（>0）',
  `transaction_type` varchar(30) NOT NULL COMMENT '交易类型：TRANSFER',
  `status` varchar(20) NOT NULL COMMENT '交易状态：SUCCESS(成功)、FAIL(失败)、PROCESSING(处理中)',
  `remark` varchar(255) DEFAULT NULL COMMENT '转账备注（如附言）',
  `transfer_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '转账时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_trace_id` (`transfer_trace_id`) COMMENT '唯一索引：确保同一笔转账不重复处理',
  KEY `idx_from_account` (`from_account_id`,`transfer_time`) COMMENT '联合索引：按转出账户+时间查询',
  KEY `idx_to_account` (`to_account_id`,`transfer_time`) COMMENT '联合索引：按转入账户+时间查询',
  CONSTRAINT `fk_from_account` FOREIGN KEY (`from_account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `fk_to_account` FOREIGN KEY (`to_account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `ck_amount_positive` CHECK ((`transfer_amount` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账户转账记录表';


-- simplebank.transfer_history definition

CREATE TABLE `transfer_history` (
  `transfer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '转账记录ID（主键）',
  `transfer_trace_id` varchar(64) NOT NULL COMMENT '全局追踪ID（幂等性标识）',
  `from_account_id` bigint NOT NULL COMMENT '转出账户ID',
  `to_account_id` bigint NOT NULL COMMENT '转入账户ID',
  `transfer_amount` decimal(19,2) NOT NULL COMMENT '转账金额（>0）',
  `transaction_type` varchar(30) NOT NULL COMMENT '交易类型：TRANSFER',
  `status` varchar(20) NOT NULL COMMENT '交易状态：SUCCESS(成功)、FAIL(失败)、PROCESSING(处理中)',
  `remark` varchar(255) DEFAULT NULL COMMENT '转账备注（如附言）',
  `transfer_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '转账时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_trace_id` (`transfer_trace_id`) COMMENT '唯一索引：确保同一笔转账不重复处理',
  KEY `idx_from_account` (`from_account_id`,`transfer_time`) COMMENT '联合索引：按转出账户+时间查询',
  KEY `idx_to_account` (`to_account_id`,`transfer_time`) COMMENT '联合索引：按转入账户+时间查询',
  CONSTRAINT `fk_from_account` FOREIGN KEY (`from_account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `fk_to_account` FOREIGN KEY (`to_account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `ck_amount_positive` CHECK ((`transfer_amount` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账户转账记录表';

