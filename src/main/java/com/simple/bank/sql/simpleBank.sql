CREATE DATABASE `simplebank`

-- simplebank.customer definition
CREATE TABLE `customer` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(254) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `mobile` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`customer_id`),
  KEY `customer_name_IDX` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='customer table';

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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='bank account table';

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
) ENGINE=InnoDB AUTO_INCREMENT=411 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='audit log table';

-- simplebank.product_purchase definition
CREATE TABLE `product_purchase` (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `account_id` bigint NOT NULL,
  `purchase_amount` decimal(19,2) NOT NULL,
  `purchase_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `transaction_trace_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`purchase_id`),
  UNIQUE KEY `uidx_purchase_transaction_trace_id` (`transaction_trace_id`),
  KEY `idx_purchase_customer_id` (`customer_id`),
  KEY `idx_purchase_product_id` (`product_id`),
  KEY `idx_purchase_account_id` (`account_id`),
  CONSTRAINT `fk_purchase_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `fk_purchase_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`),
  CONSTRAINT `fk_purchase_product` FOREIGN KEY (`product_id`) REFERENCES `wealth_product` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='wealth product purchse table';


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
  KEY `idx_transaction_account_id` (`account_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_transaction_customer_id` (`customer_id`),
  KEY `idx_transaction_trace_id` (`transaction_trace_id`) USING BTREE,
  CONSTRAINT `fk_account_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `fk_account_transaction_customer` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`customer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=289 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='account financial transaction table';

-- simplebank.transfer_history definition
CREATE TABLE `transfer_history` (
  `transfer_id` bigint NOT NULL AUTO_INCREMENT,
  `transfer_trace_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `from_account_id` bigint NOT NULL,
  `to_account_id` bigint NOT NULL,
  `transfer_amount` decimal(19,2) NOT NULL,
  `transaction_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `transfer_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uidx_transfer_trace_id` (`transfer_trace_id`),
  KEY `idx_transfer_from_account` (`from_account_id`,`transfer_time`),
  KEY `idx_transfer_to_account` (`to_account_id`,`transfer_time`),
  CONSTRAINT `fk_from_account` FOREIGN KEY (`from_account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `fk_to_account` FOREIGN KEY (`to_account_id`) REFERENCES `account` (`account_id`),
  CONSTRAINT `ck_amount_positive` CHECK ((`transfer_amount` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='transfer history table';

-- simplebank.wealth_product definition
CREATE TABLE `wealth_product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `product_code` varchar(50) NOT NULL COMMENT '产品编码',
  `type` varchar(20) NOT NULL COMMENT '产品类型（如：FIXED_INCOME, FUND）',
  `expected_return_rate` decimal(6,2) NOT NULL COMMENT '预期收益率(%)',
  `term_days` int NOT NULL COMMENT '产品期限(天)',
  `min_purchase_amount` decimal(19,2) NOT NULL COMMENT '起购金额',
  `max_purchase_amount` decimal(19,2) DEFAULT NULL COMMENT '单用户限购金额',
  `total_amount` decimal(19,2) NOT NULL COMMENT '总发行额度',
  `remaining_amount` decimal(19,2) NOT NULL COMMENT '剩余可购额度',
  `status` varchar(20) NOT NULL COMMENT '状态（ON_SALE, OFF_SALE, EXPIRED）',
  `start_date` datetime NOT NULL COMMENT '发售开始时间',
  `end_date` datetime NOT NULL COMMENT '发售结束时间',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_hot` tinyint DEFAULT '0',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `uidx_product_code` (`product_code`),
  CONSTRAINT `chk_product_remaining_amount` CHECK ((`remaining_amount` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- simplebank.sys_config definition
CREATE TABLE `sys_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) NOT NULL,
  `config_key_seq` int NOT NULL DEFAULT '1',
  `config_value` varchar(2000) NOT NULL,
  `description` varchar(500) DEFAULT '',
  `created_by` varchar(50) DEFAULT 'system',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_by` varchar(50) DEFAULT 'system',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` int DEFAULT '0',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `idx_config_key_seq` (`config_key`,`config_key_seq`),
  KEY `idx_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;