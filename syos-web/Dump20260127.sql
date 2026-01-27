-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: syos_billing_web
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `application_locks`
--

DROP TABLE IF EXISTS `application_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `application_locks` (
  `lock_name` varchar(100) NOT NULL,
  `holder_session_id` varchar(255) DEFAULT NULL,
  `acquired_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `expires_at` timestamp NULL DEFAULT NULL,
  `lock_data` json DEFAULT NULL,
  PRIMARY KEY (`lock_name`),
  KEY `idx_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `application_locks`
--

LOCK TABLES `application_locks` WRITE;
/*!40000 ALTER TABLE `application_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `application_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill_items`
--

DROP TABLE IF EXISTS `bill_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill_items` (
  `bill_item_id` int NOT NULL AUTO_INCREMENT,
  `bill_number` varchar(50) NOT NULL,
  `product_code` varchar(50) NOT NULL,
  `batch_id` int DEFAULT NULL,
  `quantity` int NOT NULL,
  `price_at_sale` decimal(10,2) NOT NULL,
  `discount_applied` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`bill_item_id`),
  KEY `batch_id` (`batch_id`),
  KEY `idx_billitem_bill` (`bill_number`),
  KEY `idx_billitem_product` (`product_code`),
  CONSTRAINT `bill_items_ibfk_1` FOREIGN KEY (`bill_number`) REFERENCES `bills` (`bill_number`),
  CONSTRAINT `bill_items_ibfk_2` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`),
  CONSTRAINT `bill_items_ibfk_3` FOREIGN KEY (`batch_id`) REFERENCES `stock_batches` (`batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill_items`
--

LOCK TABLES `bill_items` WRITE;
/*!40000 ALTER TABLE `bill_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `bill_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill_sequences`
--

DROP TABLE IF EXISTS `bill_sequences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill_sequences` (
  `id` int NOT NULL AUTO_INCREMENT,
  `business_day_id` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `business_day_id` (`business_day_id`),
  CONSTRAINT `bill_sequences_ibfk_1` FOREIGN KEY (`business_day_id`) REFERENCES `business_day` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill_sequences`
--

LOCK TABLES `bill_sequences` WRITE;
/*!40000 ALTER TABLE `bill_sequences` DISABLE KEYS */;
/*!40000 ALTER TABLE `bill_sequences` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bills`
--

DROP TABLE IF EXISTS `bills`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bills` (
  `bill_number` varchar(50) NOT NULL,
  `transaction_date` datetime NOT NULL,
  `cashier_id` varchar(50) DEFAULT NULL,
  `customer_id` varchar(50) DEFAULT NULL,
  `channel` varchar(20) NOT NULL DEFAULT 'IN_STORE',
  `subtotal` decimal(10,2) DEFAULT NULL,
  `discount_amount` decimal(10,2) DEFAULT '0.00',
  `total_amount` decimal(10,2) DEFAULT NULL,
  `business_day_id` bigint DEFAULT NULL,
  PRIMARY KEY (`bill_number`),
  KEY `business_day_id` (`business_day_id`),
  KEY `idx_bill_date` (`transaction_date`),
  KEY `idx_bill_cashier` (`cashier_id`),
  KEY `idx_bill_channel` (`channel`),
  KEY `idx_bill_cashier_date` (`cashier_id`,`transaction_date`),
  CONSTRAINT `bills_ibfk_1` FOREIGN KEY (`cashier_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `bills_ibfk_2` FOREIGN KEY (`business_day_id`) REFERENCES `business_day` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bills`
--

LOCK TABLES `bills` WRITE;
/*!40000 ALTER TABLE `bills` DISABLE KEYS */;
/*!40000 ALTER TABLE `bills` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `business_day`
--

DROP TABLE IF EXISTS `business_day`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `business_day` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_business_active` (`active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `business_day`
--

LOCK TABLES `business_day` WRITE;
/*!40000 ALTER TABLE `business_day` DISABLE KEYS */;
/*!40000 ALTER TABLE `business_day` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `full_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `registration_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_locations`
--

DROP TABLE IF EXISTS `inventory_locations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_locations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_code` varchar(50) NOT NULL,
  `batch_id` int NOT NULL,
  `location` enum('MAIN','SHELF','WEBSITE') NOT NULL,
  `quantity` int NOT NULL DEFAULT '0',
  `moved_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `version` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_batch_location` (`batch_id`,`location`),
  KEY `idx_product_location` (`product_code`,`location`),
  KEY `idx_location_version` (`id`,`version`),
  CONSTRAINT `inventory_locations_ibfk_1` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`),
  CONSTRAINT `inventory_locations_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `stock_batches` (`batch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_locations`
--

LOCK TABLES `inventory_locations` WRITE;
/*!40000 ALTER TABLE `inventory_locations` DISABLE KEYS */;
INSERT INTO `inventory_locations` VALUES (1,'RICE001',1,'MAIN',127,'2026-01-24 05:59:40',1),(2,'RICE001',1,'SHELF',45,'2026-01-24 05:59:40',1),(3,'RICE001',1,'WEBSITE',28,'2026-01-24 05:59:40',0),(4,'DHAL001',2,'MAIN',89,'2026-01-24 05:59:40',0),(5,'DHAL001',2,'SHELF',15,'2026-01-24 05:59:40',0),(6,'DHAL001',2,'WEBSITE',46,'2026-01-24 05:59:40',0),(7,'OIL001',3,'MAIN',45,'2026-01-24 05:59:40',0),(8,'OIL001',3,'SHELF',8,'2026-01-24 05:59:40',0),(9,'OIL001',3,'WEBSITE',47,'2026-01-24 05:59:40',0),(10,'MILK001',4,'MAIN',20,'2026-01-24 05:59:40',0),(11,'MILK001',4,'SHELF',30,'2026-01-24 05:59:40',0),(12,'BREAD001',5,'MAIN',40,'2026-01-24 05:59:40',0),(13,'BREAD001',5,'SHELF',40,'2026-01-24 05:59:40',0),(14,'TEA001',6,'MAIN',80,'2026-01-24 05:59:40',0),(15,'TEA001',6,'SHELF',40,'2026-01-24 05:59:40',0),(16,'SUGAR001',7,'MAIN',200,'2026-01-24 05:59:40',0),(17,'SUGAR001',7,'SHELF',50,'2026-01-24 05:59:40',0),(18,'SALT001',8,'MAIN',250,'2026-01-24 05:59:40',0),(19,'SALT001',8,'SHELF',50,'2026-01-24 05:59:40',0),(20,'RICE001',9,'MAIN',50,'2026-01-25 15:00:02',0);
/*!40000 ALTER TABLE `inventory_locations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory_transactions`
--

DROP TABLE IF EXISTS `inventory_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory_transactions` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT,
  `transaction_type` enum('SALE','RESTOCK','TRANSFER','ADJUSTMENT','RETURN') NOT NULL,
  `product_code` varchar(50) NOT NULL,
  `batch_id` int DEFAULT NULL,
  `quantity_change` int NOT NULL,
  `from_location` enum('MAIN','SHELF','WEBSITE') DEFAULT NULL,
  `to_location` enum('MAIN','SHELF','WEBSITE') DEFAULT NULL,
  `user_id` varchar(50) DEFAULT NULL,
  `bill_number` varchar(50) DEFAULT NULL,
  `timestamp` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `previous_quantity` int DEFAULT NULL,
  `new_quantity` int DEFAULT NULL,
  `notes` text,
  PRIMARY KEY (`transaction_id`),
  KEY `batch_id` (`batch_id`),
  KEY `bill_number` (`bill_number`),
  KEY `idx_product` (`product_code`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_type` (`transaction_type`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `inventory_transactions_ibfk_1` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`),
  CONSTRAINT `inventory_transactions_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `stock_batches` (`batch_id`),
  CONSTRAINT `inventory_transactions_ibfk_3` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `inventory_transactions_ibfk_4` FOREIGN KEY (`bill_number`) REFERENCES `bills` (`bill_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory_transactions`
--

LOCK TABLES `inventory_transactions` WRITE;
/*!40000 ALTER TABLE `inventory_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `inventory_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_methods`
--

DROP TABLE IF EXISTS `payment_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_methods` (
  `payment_method_id` int NOT NULL,
  `method_name` varchar(50) NOT NULL,
  PRIMARY KEY (`payment_method_id`),
  UNIQUE KEY `method_name` (`method_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_methods`
--

LOCK TABLES `payment_methods` WRITE;
/*!40000 ALTER TABLE `payment_methods` DISABLE KEYS */;
INSERT INTO `payment_methods` VALUES (1,'CASH'),(2,'ONLINE_GATEWAY');
/*!40000 ALTER TABLE `payment_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `bill_number` varchar(50) NOT NULL,
  `payment_method_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `amount_tendered` decimal(10,2) DEFAULT NULL,
  `change_given` decimal(10,2) DEFAULT NULL,
  `transaction_ref` varchar(255) DEFAULT NULL,
  `payment_date` datetime NOT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `payment_method_id` (`payment_method_id`),
  KEY `idx_payment_bill` (`bill_number`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`bill_number`) REFERENCES `bills` (`bill_number`),
  CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`payment_method_id`) REFERENCES `payment_methods` (`payment_method_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `performance_metrics`
--

DROP TABLE IF EXISTS `performance_metrics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `performance_metrics` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `metric_name` varchar(100) NOT NULL,
  `metric_value` decimal(10,2) DEFAULT NULL,
  `recorded_at` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `concurrent_users` int DEFAULT NULL,
  `active_sessions` int DEFAULT NULL,
  `metadata` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_metric_name` (`metric_name`),
  KEY `idx_timestamp` (`recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `performance_metrics`
--

LOCK TABLES `performance_metrics` WRITE;
/*!40000 ALTER TABLE `performance_metrics` DISABLE KEYS */;
/*!40000 ALTER TABLE `performance_metrics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_categories`
--

DROP TABLE IF EXISTS `product_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL,
  `description` text,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `category_name` (`category_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_categories`
--

LOCK TABLES `product_categories` WRITE;
/*!40000 ALTER TABLE `product_categories` DISABLE KEYS */;
INSERT INTO `product_categories` VALUES (1,'Beverages','Drinks and beverages','2026-01-24 21:48:13'),(2,'Dairy Products','Milk, cheese, yogurt','2026-01-24 21:48:13'),(3,'Rice & Grains','Rice, wheat, cereals','2026-01-24 21:48:13'),(4,'Cooking Oil','Vegetable oils and cooking fats','2026-01-24 21:48:13'),(5,'Bakery','Bread and baked goods','2026-01-24 21:48:13'),(6,'Dry Goods','Lentils, beans, pulses','2026-01-24 21:48:13'),(7,'Spices & Condiments','Salt, sugar, spices','2026-01-24 21:48:13');
/*!40000 ALTER TABLE `product_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_code` varchar(50) NOT NULL,
  `name` varchar(255) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `image_url` longtext,
  `category_id` int DEFAULT NULL,
  `is_deleted` tinyint(1) DEFAULT '0',
  `deleted_at` datetime DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_code`),
  KEY `idx_product_name` (`name`),
  KEY `idx_category` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `product_categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES ('apple001','apple ausitralia',120.00,'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAdHx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5OjcBCgoKDQwNGg8PGjclHyU3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3N//AABEIAGQAZAMBIgACEQEDEQH/xAAbAAACAwEBAQAAAAAAAAAAAAAEBQADBgIHAf/EADcQAAIBAwMCAwYDBgcAAAAAAAECAwAEEQUSITFBE1FhBiJxkaHRIzKBB0JSYrHBFBUzcoLC8P/EABkBAAMBAQEAAAAAAAAAAAAAAAMEBQIBAP/EACIRAAMAAgICAwADAAAAAAAAAAABAgMREiEEMRMiQRQycf/aAAwDAQACEQMRAD8A1A61iNf1hjrTRKSREMD41qJNTgAO1v1rHX+k3Ut5LcQ7X8Q5AqTkqWtF/wAHx7V8qXQ0s715U9we9Wt0xJJ9MPILgdKwGmXT2MrrNHiToFYdK1mkahHAg/EJZuoqdmShf6VfIludIQTW2pSa2Q8L7EbIJHFbEwltKIfg+Z60XDf2syneFJpXqupxSRPDC2GXtQ+TTSE8UW70ZDWoHtr1fBO4MOaOsbclEa6UjP8ADSm/vWa4Xc2Ho2zvy7LEG47kU83XFJFD4m3vY8vf8NDEnhOc4zg0CIHv5Rk7U9a6mmWUjPvbeATRSSGCNW2YBoKensHjx5E/szm99mnt7YSSLkN0rPXun3NvbSXUSFokOG8xW/tb2KWzJnfcQMKrdqzepz+EZY4z+HIORTHNLR6PkyJzXtHnd3qAMx4PSpWuGmWko3mNcmpTiqdEPJltW1oZWNsh952DDqQe1FsILceM2Wj6hQelAezmow2ErSXA3heGiZfzg+tfLi+jmnk8KLZGxJCE5AFI3PWy+uVU5S6Or0W+pe/EgRscEHvWfa4kt5WRiQy8VrNOhiZhtRAT2HQUbqWlaa433FrFJIBwzDn50u8sp6aNO1DUtGIj1S4LDZJIqICzbcc+QOaHm1Vi5dm5+NaGbSbR45YY7fwkbG4KxBOOlKLr2SbcJLe4yOpST7ij4/j/AHoy5qKdr9Fl0sngLePxu4Fc2dwyvuB5q3W5biCFbW4gMajhSeh+BoC2B2gg0y0uOwXjZarar2aS2viF97miV1FmYKxOD09KzaOw70bEXkUEDpStQh+UjQw6huOw9emarnk8RTk80rRmUZ+dEDe7qVU4xkk1lLs0+M9sf6VaWUlmrXFzskzyM1KzLiQuTtPXyqUdN6IeXEqtspa6ku5nlfHvHdwMCmNoVxvbkdMUnsxuCjJwO1M43yyxHhd1DydstY5SnSHmlTeFIGfAQ9KZTS75OcFeuCazkb5dsDC9FHWr1mJyQcktjrS/D7bOXhVPkbGPS7B7JZnuGNweoxgfClEkRDMcYUnjPlReiws75ZSF7g5NF6osO1UTO5cclj17UzSVTvRMm3jyuG9iW606O8g2SLG2ePDfkN9qw2t6K2lyeLbBmtmP5W6xny+FbqaVl3Ychg3IzQUiC6tZYZcHcCcEYwaHF8RvjTWzARyZYYp5YY2gE7R3NJbiFbe+2DkjtRtvOSSdwpi5TN4bq57Why5CECJ8E5/dqyNsrtbcz4yPWlwk8TaFcHjv1omGeMZwSxVfOh8ehnikNoLW6miV44gVPQnPNSjNN1dILRY8Dj+YjFSu6kTq8qp6lGFsn9z0FNLZTNIpZg3GMZ6UkjkCoApwSMdab6dNEm7xMhuMYNduQk39TWR2UEelGV2XIHUnJJ7fSkQc71xjAOAB3qiW/eRRDwEXrg9TXNu/435lPHGCOOf60JrRvDLlN0zaaXfJFCASpOMc85qm8uvHl3ANheOO31pF/iGWRUZsHGTj/wB60XC+4btxx0xjqO1DdvWgKwyqdhMj/gjcCCfXrQ0bHlyRnB5qq6uVCqoPIPeqWk2j3gBtXnPeuL0GlaRmNZAXUyfNAcj5VTC+U4OKp1q4DahhT+4M+lVRS4XGcc0/M/VGfkSbQxhbgbuV3djRMEhVgei85zil9m6tvYAe7zg1bNqCOrBEwM1xydWXbGSS71yJigHAFSkyuzch8em6pXOATkgcSAjk8UXDNtxtOSe9KEkB4PSr45RnvRKkmY8o2SXkKrAHHNMbBsJuyM+eaQxOCwHamSzpHDhW69j5/Gl7kd+VNaGaN78j45b97zouCYKuA4IyMAdqTQzMBgt+Udua+vOVO1TnPQCguNnnaYylnSSTcwzt/TFVXVzjeN2c96GWUgDpg/m9TSX2h1EQQ7EPvNwP70THidUpQPLnUS6f4Jrq6M19cSdmbA+AqRzE/OgI2Lc5z51YGIPlVNwl0RY8pvtjCO62IQM5qwS7uO3agMeR5xXaSFT3BrLkbx+QMFmIGNwH6mpQqsrDLgE+rY/tX2s8Q/zF99ZNZseSU8wOlDxHeMowI8801TWbe54uk8NuhIGRRcOgR6gPFtdrfzRNg/Svb1/YirNciiGRlOCKIikyScZXGMk9KNn9ltTjH4a+KvlKnPzFcLpN+vE1hcIfNPeH3+lYaT9DEeXv2QXIVdo6kUVaRSYLkgebfaqhaNbJmS1usZ6+A/2q2JL+8VtkXgxA9WGD8qE50Nzm36Kr2+SBML1HasfeyvcTGRzknp6VsdW0+3FiWg5kHVieWrHSbQ+CQPiaP43F9oS8yrfT9AylkbKg+tEpIGXB5J86q3AuVT3iew5o210TVrtgLXS76XP8Fs5HzxTbSJs25egcMV4zV8c5AwRuHrWgsP2c+1d6RnThbIf37mVVHyBJ+lazSf2PNlX1fVfjHaJ/2b7UOnK/RiMrR54s0O0ZQA1K9ytv2f8AsraRCL/Lo5SOS8zFmP1r5QucjH8lngQ6CidPlliuQYZpIm80bBqVKKxefRpbD2s1a3ukgaVJ0Jx+KmT9MV6Hpd208UbPFECwzwtSpS1pI1S6HlvEryBTnBpnHDEi8Rr+oqVKyD2fTZ2r4320LZ84xXDabYIcrZWwPn4K/apUrx5lkcMUf+nFGv8AtUCrCxFSpXjwLdX0sKExqg/415/7Ye2us6eWjtXiXPG7ac/1qVK0vYTFKb7MTPrur3Ehkl1K5LHyfA+lfKlSjqUEP//Z',7,0,NULL,'2026-01-24 15:48:53','2026-01-24 23:01:01'),('BREAD001','White Bread',120.00,NULL,NULL,0,'2026-01-24 14:31:35','2026-01-24 00:29:40','2026-01-25 07:38:32'),('DHAL001','Red Lentils 500g',180.00,NULL,NULL,0,'2026-01-24 14:31:40','2026-01-24 00:29:40','2026-01-25 07:38:32'),('MILK001','Fresh Milk 1L',220.00,'',1,0,NULL,'2026-01-24 00:29:40','2026-01-24 23:01:17'),('OIL001','Coconut Oil 1L',450.00,'',3,0,NULL,'2026-01-24 00:29:40','2026-01-24 23:01:41'),('OIL002','OIl ',222.00,'data:image/jpeg;base64,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',5,0,NULL,'2026-01-24 08:29:51','2026-01-24 23:03:04'),('RICE001','Basmati Rice 1kg',350.00,NULL,NULL,0,NULL,'2026-01-24 00:29:40','2026-01-24 00:29:40'),('SALT001','Table Salt 500g',60.00,NULL,NULL,0,NULL,'2026-01-24 00:29:40','2026-01-24 00:29:40'),('SUGAR001','White Sugar 1kg',150.00,NULL,NULL,0,NULL,'2026-01-24 00:29:40','2026-01-24 00:29:40'),('SUNFLOWER001','maliban sunflower',100.00,'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMTEhUTEhMWFRUWFRoVFhgWGBcVFRYYFxUXGBYXFxcaHSggGBolGxUXITEhJSkrLi4uFx8zODMtNygtLisBCgoKDg0OGhAQGy0lHiUtLS8tLS0vLS0uLS0vKy0vLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLTUtLS0tNS0tK//AABEIAOEA4QMBIgACEQEDEQH/xAAbAAEAAgMBAQAAAAAAAAAAAAAAAwUBAgQGB//EADoQAAIBAgMFBQcDAgYDAAAAAAABAgMRBCExBRJBUWEGcYGR8BMiMqGxwdEHQuFSchQzYoKS8RUjsv/EABsBAQACAwEBAAAAAAAAAAAAAAABAgMEBQYH/8QAMhEBAAICAQIEAwcCBwAAAAAAAAECAxEEEiEFMUFRExSBMmFxobHB8CIjFTRCUpHR4f/aAAwDAQACEQMRAD8A+4gAAAAAAAAAAAAAAAAAAAAABUbe7R0MJG9WV5P4YRzm+tuC6sra0VjdpRa0Vjcrcw2fPcd+oEpbqowssnOT1tk3GHLit5rVaFFtPtLia7kpS3Yy93djkks7cbu/XW2eiRo5PEcVfLu1r8vHXy7vrUsXBRU3OKi1dSbSja1730tbiQYbalKpFzhNOEXZz0je13ZvXJrM+QVZ1Ksk5uUmluxV3aMYvKNtFostMuppKn100v5u1/hzb9XNWfFZ32qxfO+0Psr2jSvBb63qnwLjJa3S5WzvyJViI7zjvLeSu1xS4N8j4vRm4u8ZSjKzV43WVrSz1zWfjYnoYycFJU5zp726pWdr2vZN8rNvxv1LR4pPrVMcz3h9kjUT0d9flkzY+O4LbleimoTze7H3vecYr9kW3eKbvlHPK/A9LsjtlVnPdlSc5SaSVO9lFJt7sX8Ur3zvy5GfH4ljt2tExLLTk1t2l70EOHqykk5QcctG03flldfMmOhE7jbZAASAAAAAAAAAAAAhq4qEdZJeJy1dr01xuYb8jFT7VoFgCmq7dS0jlzuc8+0EuS9eJq28U41fX8hX9pe3dLC1pUG4qcUm1U3lvbyvHd3U8tVe2qeli+7O7V/xOHhW3d1yveN03FptWduln4nyn9Upe1dHEpJOL9lOy1XxU34Pf/5Iuf07266dGpDX31PO/wC6Nu7SKyRr18RrEzkm26fo2ppWcPVEd31Ajr1owi5TkoxWrbsl3tnmsd2xp0YOpU+FZJL4pN6Rir5tnzXtD2nr4343uU4Nvci3a+dt7RyaVly152Nj/EcVq7p3c/Lmin4vUbf/AFCnKU4YWyjZxhNq7k8veUXwtey6pvkeQlSb9+bc5yV7yu3xzu9eNu84qTz5dFpo8ua1J3Uu3/bvaXX8Zpo5mXLfJP8AVLn3ta87ssKdO3vO2emXFZK/dllx+qlT927yvpazt1aT01suvQgs2t1K+menl64dTs9ikleWizWSu1nz0z48ma/w9q9DDrWlu6tpNtcLpN3XLP8AnRm0IvLeeq3r3s+Nlfhmr369DSWOjxSflrz721oI1XJpQpu7+G2beXLxfDiW6IhPS3rxXNXtnZWTye6uF7c+N+iIJ6JJPho7Jvjklq+8u9ndlMXNx9x04vVyaja3Fq+8n0sep2Z2IhB3qVZTd80vdTXJvNvzWhs4+Jkv5R+zLXj2t6PG7J2RUxEmoQySV7rdWayu7dF11PovZzYMcNDO0qjXvSS7slfhl4+SVtRoxglGMVFLRJJJdyRIdPj8KuKeqe8tzHhinf1AAbrMAAAAAAAAAw3YpNp7T/bD+Wa/I5NMFd2/4Fhidowjknd8kV1XGzlxy5LIq1J3zO6hHnc4s8vJnnUzqPZCGrT9XODEYd5628i9jBcjeWET4IrbiReB4qvWlHJvL7fghjibprrl8y47Q7Mm4vdj4555c1z5ni61eVN2d7cb6pW4/k5WbiTW2iZSdpffwtXjaO919x76y8Gio7LVnGjUnvNJJ5acFd631X1OrauKXsqqv8VOfTPdefzuUuyaijh7O6cpJvhZbr69E/8At23ONj/sWrPuz0vrBd0Y3GSqyUptNK+4uEU75vrdZm1JWyvy6NWvryefcclKbk1urLm9Mr2v5/QtYKNNb1SW7d715uz77ap/LPU2orFY1Dk9MzO5bYbC712uPG+S4PPjxXijpw+zFrK7V8rJ2Ttbn65FdW7RR0owlU6ttLL5+tSN4rFVXm92P9MFu/PV+ZS24+78V4xPUYfZ8m92Fo/7reHd/JZYLsVGpK9SvFc1FNt+LasVGxuz8pxz34PhJe/HxV7+JdYbZ2Ko2vJTjfVO9vPNGOuSY/qmNwzRhr6vU4HsZg4RUdxz5uUm280+FlwL3DYaFOKjCMYpaKKSXyPPbO2lOOua6noMPiFNXX/R3OJnw37ViIllrWseUJgAbywAAAAAAAAAAABxbWxipwb4vJFMmSMdJtbygcG2do/ti8vr/BQzq26t5kdTENu/FmVSzz8TxPM5ls15n+QM0M3x8y2pTSOCOtor8kkl/NzFhzTTy7juljFotTX/ABr5nB7Hm+41m7P0rIzzzsnqaXdLGrn4EOLw9Ct/m0ac+rim/PVFMqtnl9c+42WJd9fuZa+KdtWNGO7I4KrGUVCUHJWe7OeXDRtryK+n+nOE3d2VSslwUZR5WzvB30Ra08Ra13nm/wAElLFZGSnPxzKe+teirfYDD5buIxEOF4+yT/5Om2vCxrT/AE52fe8vaTlrepNy+WS+Rb08Zz0b+5KsRczV5uOY7KdMKt9kKMP8vLkrJEj2BFLTx/JZ+2ubRqGvemC3f906V1HDyp/Dk+WqZZYfEqatJWfTiROrwtoRztrx5lKZoxfZnt7JYr07PLMnwOK3Xl5dCOTvmQ7tnl67zF8zMX6qj1tGopJNG5S7Ixednoy6PXcPkxnxxb19QABtAAAAAAAAAeS2/id6o1wjkvDX53PU16m7Fy5JvyR4LHVL97d2cTxrN0Y4r9RpTTb8dTthnc4qbtY76PLXmeSp3lMuuhT6HUsHfREVAssOzrcfDW3aUK6thrHBiaduJ6adJMqMXSs7otyuHFY2KiUMlbxMW9eu8lmmnwNJRdjhWjvKyNsxFG0oZZixj7iSDaXj6zN3LI0TyXrxN4ZqxmrM+W0NoyfDgbKdvqR03kSJdOoi9hLGV+pmxHT6krfEmLTMdwpL8CSyMyldW9Zmm96/JetvRCOnUal1PU4GtvQTPK1uZa7DxGe6+Oh2fCOT8PN0T5SL0AHrgAAAAAAABXbeqWovq0vv9jws6l5t8Fp4ZHrO1Va0Yrvl5W/J42PE8j45k6s3T7fz91odNLN6llhmvPl8irpOx14aRw6zqwuKM8jvoVLIp6NU6qdY63HzRCqyeJRW46uPaHBiJ3fn+PsZOTyd00mGZyvdnPPWzJFPO3rQ5ZSz/Jxcs7SzKRI56ffuOaTz7zdNPMw6EsHyJtL+vI5pO1mSU5Ex2S3izoi7r164HLTeptGfrgB0bzt3EtORy1JfU3hU4iLalDpmQyavfhxM1Z6eTIJFpt3Qkccreun2M4OvZp8mjSUrd5HSfvNGfFk6bxpD3EHdJ8zJzbNnenF9PodJ9CxX66Rb3iJAAGQAAAAAHk+207W/t+sv4PMwZ6PtxrFf6V/9M8vCVjxXi3fk2WjyTU9fH7nVTq2v3nDTmTq3M5VoFhTnwOilW4cSup1DeM+N/AitpqLGtiFmcNSp89DSVXQhlItfJN/MTbz+eZrNkU5mm+Y+k2lCmr6kVSy05GPDUtpDpjIk0tY5ocyW5SYWhI9TaFQhlL1wEHYjQn3iWnNWOXe1Me0yI0h1SqX1NYTIXLIUpeuo0qkczaDzXrXIhubUpe9bxLU84HsNhSvSXRtFiV2wV/6u+TZYn0Tg/wCXp+EAADaAAAAAB5Pt1T92EujXzizyE3bM+gdrqG9h2/6Xf5NfdHz1ZnkfGadPI37wtCSlLNdDphLQ4qX1J0zj2hDoUuBv7S3Xh8zmiSOVjHMG0kpEU6g3uJG3e5MVG29mYUiOSMyLaEqZsyPe0NnIjQlhM2jK+pDv+BtIrMJbmYyImzP1uRoS7xne8vX4I4Mw5EaVT3yNYyzzNN8wNCZSzN4SzuQJklHOaXURHdD3eyY2pR8fqzsIcHC1OK/0r6Ex9G49enFWvtEfokABmAAAAABDjKG/CUeaa/HzPlVWG7Jx5NruaZ9bPnna3BbleTtlJby7+P0OH43g6qVyR6JhS07/AGN4vIjUjNssjzEkp4y4GE22aQa4fMfYrpCWcjRTzI/ac80YWWY6RJJmU8iLfM740JFLgbLmQ3N4iYG9zLkaC/pEaSki8ze5C5317jLmRMITBGqkzVPX6kaQ3Ujac9LEW/cTeeQ0hKpHbsOlv1Yrm/8Av5XK6o8n3HpuxuFu3N8F83/F/M2uFg+NnrT7/wAvUh6xGQD3ywAAAAAAAAUHbHBb9HfWtN3/ANryl9n4F+azgmmnmmrNc09TFnxRlxzSfUfH28zeCOztBgHRquL0vk+a4P1yK9S9dDw+XHNLTWfOEpk0ZVmRt6ekxF8zFpDHQ2bI7m8pXJmEMqRqw3yZqxECVMy2Q7xlyI0bSuQpsjUxGQ0bTNmzZG3czveZXSNpuHMPTvIpT5jeyX3I0jbZSzMOefeaxlqaqV2TpCZe81HmfSdh4X2dGK4tbz73/FjxHZbZ/tayb+FZvu/k+jI9F4Hx/tZp/CP3XgAB6JIAAAAAAAAAAKDtdsn21LeivfgrrquK9dT5u/ddmfZz5/2x2H7OXtIL3JP/AIviu7ijg+LcTf8Aer9f+x5qM7IzGeVuZDGXB5Mzv8bHn+lDe7WT+hI5cSKTura/YRqXQ0hneMOWXLI0nNaLzNb6kxCNpISRunlrmQU5Z9Da4mEJFI3i+RDT01NoyImBPF9/h8jK55ohUjMattPqV6RI5evwJyIpS4mntvMnpRtLKfrkbUlfrfJc3fQ5Y3mz3HY3YeleayX+Wnxf9b+xscfjWzZIx1+v3QmI2vuzmzPYUkn8cs5faPh+S1APZ4sVcVIpXyhkAAZAAAAAAAAAAAAixNCM4uMldNWaJQRMRMakfLu1GwpYeV1d03pLl0fUpIzbyPs+IoRnFxmlKLyaZ877S9k50W6lFb9PW1ryj3parqvE83zfDZxzN8cbr+n/AIh59S62f1MKVuRz06qZjf5nK6FZlLNNu/4DnY0Uuhje68CdIbRM73rgaRlkzRyXy4k6Q6IyEqpzOpyMKdx0G3U6nA2pz4nJ7VeuJpUrjo2h1utkyJSbyOaipVJKFNOcnkoxTbfcj6H2b7D2tPFZvVU1ml/e+Pcsu82cHDyZZ1SPqtEbcXZHs661qk1akndc6j6f6evl0+iwikrJWSySXAQikrJWSyVtDJ6TicSnHrqPOfOV4gABtpAAAAAAAAAAAAAAAACt23tP2MG0t6XBfdnbiK26r+RQYmLm25Z3MOa+o1Hmh8i7QV67qSqpK7d2krJ+RVQ7RbuVSMo9dV5o+uYzYcJ6ooMd2IjLRI49uPE/ajaunjKO3aUtJp+KOp7Tg+K+RPjv03v+0qq/6c1F8Lku5v8AJj+UxT7x9DTq/wAfFZXRpLaEeaK2fYLEL98/NmI9hsR/XP5k/K4v935I6XdPaUdbkNbbUF+63kjSn2DqP4nN+LO7C9gUnfczJ+XxffP0OlUVNvxfw3l/ar/PQlw+IrVP27q66+SPWYXsjb9q8i2w3Zy3An4cf6amlZ2ZnOjJODcXxayb6X4rofUNkbdU0lUyfPh4nlsPsa3AsMPgGjYwTfH5LPapgp9nVpRyea9aFumdSl4tCzIALgAAAAAAAAAAAAAAGJICvxT3n0IFSLP2KHsUYZx7naFb7Ez7AsfYoz7Ij4RpW/4foYeEXIs/ZoezRPwoNKt4GPI1/wDHx5Ft7NDcRHwYNKn/AMfHkZWAXIttxDcQ+DBpVrBLkbLCLkWW4huIn4MGlesN0N1hzt3DKiPhQaccaJ0UnYk3TO6XimksgAuAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAP/9k=',4,0,NULL,'2026-01-24 22:59:30','2026-01-24 22:59:30'),('TEA001','Ceylon Tea 100g',280.00,NULL,NULL,0,NULL,'2026-01-24 00:29:40','2026-01-24 00:29:40'),('WATER001','Premium Mineral Water 1.5L',120.00,NULL,NULL,1,'2026-01-24 06:41:26','2026-01-24 01:08:44','2026-01-24 01:11:26');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `request_log`
--

DROP TABLE IF EXISTS `request_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `request_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` varchar(255) NOT NULL,
  `request_type` varchar(100) NOT NULL,
  `user_id` varchar(50) DEFAULT NULL,
  `session_id` varchar(255) DEFAULT NULL,
  `request_timestamp` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `processing_start` timestamp(3) NULL DEFAULT NULL,
  `processing_end` timestamp(3) NULL DEFAULT NULL,
  `status` enum('QUEUED','PROCESSING','COMPLETED','FAILED') DEFAULT 'QUEUED',
  `response_time_ms` int DEFAULT NULL,
  `error_message` text,
  `request_data` json DEFAULT NULL,
  `response_data` json DEFAULT NULL,
  `server_thread` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `request_id` (`request_id`),
  KEY `session_id` (`session_id`),
  KEY `idx_status` (`status`),
  KEY `idx_timestamp` (`request_timestamp`),
  KEY `idx_user` (`user_id`),
  KEY `idx_type` (`request_type`),
  CONSTRAINT `request_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `request_log_ibfk_2` FOREIGN KEY (`session_id`) REFERENCES `user_sessions` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=386 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `request_log`
--

LOCK TABLES `request_log` WRITE;
/*!40000 ALTER TABLE `request_log` DISABLE KEYS */;
INSERT INTO `request_log` VALUES (48,'3d2ee430-31aa-4dd0-8e7e-0f3b57866121','GOOGLE_LOGIN',NULL,NULL,'2026-01-18 18:56:17.458',NULL,'2026-01-18 18:56:17.614','COMPLETED',156,NULL,NULL,NULL,'http-nio-8081-exec-1'),(49,'8bd02bbc-8868-4b12-a4c5-581d93afc6f0','CHECK_AUTH',NULL,NULL,'2026-01-18 18:56:17.625',NULL,'2026-01-18 18:56:17.625','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-2'),(50,'c90fb72e-a950-4106-b340-586dec563ca9','CHECK_AUTH',NULL,NULL,'2026-01-18 18:56:17.634',NULL,'2026-01-18 18:56:17.635','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-3'),(51,'1ae6b37d-a12d-4730-9109-a62e3e71dfca','LOGOUT',NULL,NULL,'2026-01-18 18:57:56.386',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(52,'422671fb-0b6f-493d-ba54-1b7b1f3ca963','LOGIN',NULL,NULL,'2026-01-18 19:12:51.705',NULL,'2026-01-18 19:12:51.877','COMPLETED',655,NULL,NULL,NULL,'http-nio-8081-exec-7'),(53,'0f9c55f6-b59e-4f8c-b525-9550b322d698','CHECK_AUTH',NULL,NULL,'2026-01-18 19:12:51.934',NULL,'2026-01-18 19:12:51.940','COMPLETED',6,NULL,NULL,NULL,'http-nio-8081-exec-8'),(54,'a5bf94c9-4870-4635-b444-c137d1916e12','CHECK_AUTH',NULL,NULL,'2026-01-18 19:12:51.952',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(55,'ac2c2f93-8509-495c-a158-16faf6cd786f','LOGOUT','admin',NULL,'2026-01-18 19:13:04.625',NULL,'2026-01-18 19:13:04.626','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-10'),(56,'49c96765-6c1a-4324-86c6-90ecae88ba99','LOGIN',NULL,NULL,'2026-01-18 19:13:15.272',NULL,'2026-01-18 19:13:15.379','FAILED',107,NULL,NULL,NULL,'http-nio-8081-exec-2'),(57,'3f0448c9-cb3d-4356-8ffa-04a3791ccef6','LOGIN',NULL,NULL,'2026-01-18 19:13:21.728',NULL,'2026-01-18 19:13:21.822','COMPLETED',95,NULL,NULL,NULL,'http-nio-8081-exec-3'),(58,'4d8cdd21-c94e-445f-863f-c194951d626a','CHECK_AUTH',NULL,NULL,'2026-01-18 19:13:21.844',NULL,'2026-01-18 19:13:21.845','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-4'),(59,'8fe436a2-a9fc-4ec2-a4dc-77aa8acdc3d8','CHECK_AUTH',NULL,NULL,'2026-01-18 19:13:21.859',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(60,'99c40ca4-bf43-4246-85c3-2ab9a0942da0','LOGOUT','admin',NULL,'2026-01-18 19:14:10.895',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-7'),(61,'5d69c17b-6925-434f-9446-8b714dd81aea','LOGIN',NULL,NULL,'2026-01-19 07:53:04.729',NULL,'2026-01-19 07:53:04.996','FAILED',891,NULL,NULL,NULL,'http-nio-8081-exec-9'),(62,'734014ef-4d1a-40b1-b832-613e8c7e9f11','LOGIN',NULL,NULL,'2026-01-19 07:53:14.990',NULL,'2026-01-19 07:53:15.093','COMPLETED',104,NULL,NULL,NULL,'http-nio-8081-exec-10'),(63,'948f0ae3-641b-4d5f-ae5c-70347a266609','CHECK_AUTH',NULL,NULL,'2026-01-19 07:53:15.129',NULL,'2026-01-19 07:53:15.130','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-1'),(64,'c71a1f48-fc3e-465a-953c-0103bfcccf20','CHECK_AUTH',NULL,NULL,'2026-01-19 07:53:15.140',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(65,'a3dc33da-1138-45dd-b751-273be994e1aa','LOGIN',NULL,NULL,'2026-01-20 13:11:32.373',NULL,'2026-01-20 13:11:32.505','FAILED',663,NULL,NULL,NULL,'http-nio-8081-exec-9'),(66,'32ba3ba2-5cf5-48be-b301-32aff7677dd4','GOOGLE_LOGIN',NULL,NULL,'2026-01-20 13:11:38.364',NULL,'2026-01-20 13:11:39.809','COMPLETED',1445,NULL,NULL,NULL,'http-nio-8081-exec-2'),(67,'058754d1-515f-4821-836a-2b87148a991a','CHECK_AUTH',NULL,NULL,'2026-01-20 13:11:39.834',NULL,'2026-01-20 13:11:39.835','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-1'),(68,'e0fc3a33-342a-4000-9df1-e49f601c3b9d','CHECK_AUTH',NULL,NULL,'2026-01-20 13:11:39.846',NULL,'2026-01-20 13:11:39.846','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-3'),(69,'fdc81da5-0226-46bf-a2b1-af9ecf175ce8','GOOGLE_LOGIN',NULL,NULL,'2026-01-20 16:48:54.005',NULL,'2026-01-20 16:48:55.087','COMPLETED',1084,NULL,NULL,NULL,'http-nio-8081-exec-9'),(70,'74ddaa9f-a826-4f7f-aebb-c34a6d84caba','CHECK_AUTH',NULL,NULL,'2026-01-20 16:48:55.103',NULL,'2026-01-20 16:48:55.104','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-10'),(71,'1b7fb2cd-2b9c-4936-8b26-99017fb79842','CHECK_AUTH',NULL,NULL,'2026-01-20 16:48:55.114',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(72,'3d2b5e98-2918-4cbc-a09b-8a1ecba1e374','LOGOUT','psthirimanna',NULL,'2026-01-20 16:48:57.216',NULL,'2026-01-20 16:48:57.221','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-1'),(73,'c95f4b4d-3d14-4274-9f52-16f074e2022d','GOOGLE_LOGIN',NULL,NULL,'2026-01-20 19:13:17.421',NULL,'2026-01-20 19:13:18.115','COMPLETED',3146,NULL,NULL,NULL,'http-nio-8081-exec-8'),(74,'e6a00949-8007-4082-8261-9e4804b64413','CHECK_AUTH',NULL,NULL,'2026-01-20 19:13:18.184',NULL,'2026-01-20 19:13:18.187','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-10'),(75,'97672185-8f81-4d1f-b133-f76b08637daf','CHECK_AUTH',NULL,NULL,'2026-01-20 19:13:18.208',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(76,'cc639a75-17d4-41a5-aec0-e11b3cf06425','LOGOUT','psthirimanna',NULL,'2026-01-20 19:13:22.150',NULL,'2026-01-20 19:13:22.152','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-1'),(77,'fe044714-270f-47d2-b20c-205c7f0e27f7','LOGIN',NULL,NULL,'2026-01-20 19:13:31.588',NULL,'2026-01-20 19:13:31.996','FAILED',407,NULL,NULL,NULL,'http-nio-8081-exec-3'),(78,'2fdf9843-3543-4f4b-a7c2-bde112b93be8','GOOGLE_LOGIN',NULL,NULL,'2026-01-20 19:25:11.117',NULL,'2026-01-20 19:25:11.507','COMPLETED',1035,NULL,NULL,NULL,'http-nio-8081-exec-5'),(79,'35309ed3-19f9-42ae-9b12-a453a941e0ea','CHECK_AUTH',NULL,NULL,'2026-01-20 19:25:11.528',NULL,'2026-01-20 19:25:11.529','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-6'),(80,'a9e47a48-2e1c-4664-9acc-841fa3bcb54a','CHECK_AUTH',NULL,NULL,'2026-01-20 19:25:11.537',NULL,'2026-01-20 19:25:11.537','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-7'),(81,'e0c5e8af-5f05-4729-8237-9637f8edf1e2','LOGOUT','psthirimanna',NULL,'2026-01-20 19:25:19.953',NULL,'2026-01-20 19:25:19.954','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-8'),(82,'ccc2dad8-0f26-4133-9ecc-17d9f5869eb3','LOGIN',NULL,NULL,'2026-01-20 19:25:26.773',NULL,'2026-01-20 19:25:26.858','COMPLETED',86,NULL,NULL,NULL,'http-nio-8081-exec-9'),(83,'049babbd-1b94-4ea6-865e-7b4af6645022','CHECK_AUTH',NULL,NULL,'2026-01-20 19:25:26.894',NULL,'2026-01-20 19:25:26.894','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-10'),(84,'a0cd9017-da00-4c2c-b12e-90fce205b4bd','CHECK_AUTH',NULL,NULL,'2026-01-20 19:25:26.912',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-1'),(85,'8d6e13f6-8232-4828-976f-b8f15e76122e','LOGOUT','admin',NULL,'2026-01-20 19:25:31.493',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(86,'578d1ab2-53e2-4e69-ba5d-0d97fbbedf5d','LOGIN',NULL,NULL,'2026-01-20 19:25:36.192',NULL,'2026-01-20 19:25:36.260','FAILED',67,NULL,NULL,NULL,'http-nio-8081-exec-3'),(87,'087c655f-e6d5-4e39-86b0-ac30d3bf7914','LOGIN',NULL,NULL,'2026-01-20 19:25:40.618',NULL,'2026-01-20 19:25:40.677','FAILED',59,NULL,NULL,NULL,'http-nio-8081-exec-4'),(88,'9d59204c-9a03-446e-b233-743af8d14351','LOGIN',NULL,NULL,'2026-01-20 19:25:44.105',NULL,'2026-01-20 19:25:44.168','FAILED',62,NULL,NULL,NULL,'http-nio-8081-exec-5'),(89,'f352a285-4d8d-4cc0-b6ec-59fa5e0dbb95','LOGIN',NULL,NULL,'2026-01-20 19:25:44.695',NULL,'2026-01-20 19:25:44.752','FAILED',64,NULL,NULL,NULL,'http-nio-8081-exec-6'),(90,'2118ed08-b2ae-46a3-9151-1989fa862dd7','LOGIN',NULL,NULL,'2026-01-20 19:25:44.857',NULL,'2026-01-20 19:25:44.915','FAILED',58,NULL,NULL,NULL,'http-nio-8081-exec-7'),(91,'f64a5f70-87f7-4e54-8734-50e492c41dc7','LOGIN',NULL,NULL,'2026-01-20 19:27:12.001',NULL,'2026-01-20 19:27:12.068','FAILED',65,NULL,NULL,NULL,'http-nio-8081-exec-9'),(92,'52d84bbc-c77a-4e9e-b9e8-7e5c4845ae54','LOGIN',NULL,NULL,'2026-01-20 19:27:46.024',NULL,'2026-01-20 19:27:46.027','FAILED',4,NULL,NULL,NULL,'http-nio-8081-exec-1'),(93,'af2466e0-ecef-4c41-82c6-1165da8c2e78','LOGIN',NULL,NULL,'2026-01-20 19:27:46.618',NULL,'2026-01-20 19:27:46.620','FAILED',2,NULL,NULL,NULL,'http-nio-8081-exec-2'),(94,'d538b1fe-f2f4-4ab3-a4fb-d0fd972b4ad8','LOGIN',NULL,NULL,'2026-01-20 19:28:02.031',NULL,'2026-01-20 19:28:02.098','FAILED',67,NULL,NULL,NULL,'http-nio-8081-exec-4'),(95,'55436f1b-9498-4378-a6d7-244a7907a643','LOGIN',NULL,NULL,'2026-01-20 19:28:24.239',NULL,'2026-01-20 19:28:24.242','FAILED',2,NULL,NULL,NULL,'http-nio-8081-exec-8'),(96,'8158d26f-a9ec-4ac7-ae2c-42733c31c62c','LOGIN',NULL,NULL,'2026-01-20 19:28:32.415',NULL,'2026-01-20 19:28:32.417','FAILED',1,NULL,NULL,NULL,'http-nio-8081-exec-10'),(97,'2fbf5e77-75b9-40ed-82ff-66f6116cb297','LOGIN',NULL,NULL,'2026-01-20 19:29:52.567',NULL,'2026-01-20 19:29:52.645','COMPLETED',78,NULL,NULL,NULL,'http-nio-8081-exec-4'),(98,'c97ba654-386e-4b2a-ae35-e9ca0623bf0e','CHECK_AUTH',NULL,NULL,'2026-01-20 19:29:52.684',NULL,'2026-01-20 19:29:52.685','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-6'),(99,'161b0779-9b2d-4a82-bd22-6d9b025685ab','CHECK_AUTH',NULL,NULL,'2026-01-20 19:29:52.707',NULL,'2026-01-20 19:29:52.708','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-5'),(100,'2caec9e6-b8fe-4597-acdc-c14ef399367a','LOGOUT','poojana',NULL,'2026-01-20 19:29:55.158',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-7'),(101,'cf090c0d-fd4c-4d7f-beb8-1d9deeb7eff0','LOGIN',NULL,NULL,'2026-01-20 19:30:20.045',NULL,'2026-01-20 19:30:20.118','COMPLETED',73,NULL,NULL,NULL,'http-nio-8081-exec-2'),(102,'ce8c5ed4-a088-4e66-be8f-2cfecbb730b6','CHECK_AUTH',NULL,NULL,'2026-01-20 19:30:20.145',NULL,'2026-01-20 19:30:20.146','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-1'),(103,'8b05836d-a57b-4cb6-a493-29b3efcf9655','CHECK_AUTH',NULL,NULL,'2026-01-20 19:30:20.159',NULL,'2026-01-20 19:30:20.160','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-3'),(104,'812d7f12-6303-4a8b-9925-fe9a6e571d51','LOGIN',NULL,NULL,'2026-01-20 19:32:09.123',NULL,'2026-01-20 19:32:09.199','COMPLETED',76,NULL,NULL,NULL,'http-nio-8081-exec-2'),(105,'72603816-3e7a-4900-833a-669b01537a4a','CHECK_AUTH',NULL,NULL,'2026-01-20 19:32:09.214',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-1'),(106,'6c7b8289-120e-4d30-a03d-d4f74d95173c','CHECK_AUTH',NULL,NULL,'2026-01-20 19:32:09.230',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-3'),(107,'2e6aef5f-4bb3-4445-9ce6-277acd70b975','LOGIN',NULL,NULL,'2026-01-20 19:42:10.223',NULL,'2026-01-20 19:42:10.342','COMPLETED',119,NULL,NULL,NULL,'http-nio-8081-exec-7'),(108,'3ea006a7-8b1e-4232-a0c1-6ff2b54d089d','LOGIN',NULL,NULL,'2026-01-20 20:14:12.940',NULL,'2026-01-20 20:14:13.445','COMPLETED',2105,NULL,NULL,NULL,'http-nio-8081-exec-8'),(109,'84597615-cd40-4f26-9b6a-6c944103fe53','GOOGLE_LOGIN',NULL,NULL,'2026-01-20 20:17:36.017',NULL,'2026-01-20 20:17:36.811','COMPLETED',795,NULL,NULL,NULL,'http-nio-8081-exec-2'),(110,'d87a6bc4-2fec-4f98-8143-17137252b143','CHECK_AUTH',NULL,NULL,'2026-01-20 20:17:36.836',NULL,'2026-01-20 20:17:36.837','COMPLETED',7,NULL,NULL,NULL,'http-nio-8081-exec-3'),(111,'174a78a4-d747-440c-a336-6b6a2f20876e','CHECK_AUTH',NULL,NULL,'2026-01-20 20:17:36.845',NULL,'2026-01-20 20:17:36.845','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-5'),(112,'5dc24be5-8262-4ede-8fca-86f9e50dcf3f','LOGIN',NULL,NULL,'2026-01-21 06:03:45.853',NULL,'2026-01-21 06:03:46.440','FAILED',3984,NULL,NULL,NULL,'http-nio-8081-exec-8'),(113,'3e4099c5-e014-42a9-ae33-9d154c3cabad','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 00:09:01.359',NULL,'2026-01-24 00:09:02.158','COMPLETED',2207,NULL,NULL,NULL,'http-nio-8081-exec-8'),(114,'99e340be-8561-441b-ad47-d0ab7a2c8777','CHECK_AUTH',NULL,NULL,'2026-01-24 00:09:02.187',NULL,'2026-01-24 00:09:02.188','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-6'),(115,'f9dfd873-455c-4564-8eb8-ec96d343b61e','CHECK_AUTH',NULL,NULL,'2026-01-24 00:09:02.202',NULL,'2026-01-24 00:09:02.202','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-1'),(116,'eb08cc7c-5094-495d-80a2-af4a88f0514b','LOGOUT','psthirimanna',NULL,'2026-01-24 00:09:10.471',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(117,'8c1d0a38-53af-43f3-b8a1-e24333958755','LOGIN',NULL,NULL,'2026-01-24 00:09:17.680',NULL,'2026-01-24 00:09:17.741','FAILED',59,NULL,NULL,NULL,'http-nio-8081-exec-4'),(118,'a3cb69e6-fde3-41a1-8fd1-9f68276961e8','LOGIN',NULL,NULL,'2026-01-24 00:09:39.007',NULL,'2026-01-24 00:09:39.011','FAILED',3,NULL,NULL,NULL,'http-nio-8081-exec-9'),(119,'4cc4e394-c2b5-435f-88ed-2dc2e851a595','LOGIN',NULL,NULL,'2026-01-24 00:13:45.345',NULL,'2026-01-24 00:13:45.348','FAILED',4,NULL,NULL,NULL,'http-nio-8081-exec-2'),(120,'f503f586-de67-4e1c-b519-746e75c081f9','LOGIN',NULL,NULL,'2026-01-24 00:14:46.305',NULL,'2026-01-24 00:14:46.405','COMPLETED',99,NULL,NULL,NULL,'http-nio-8081-exec-5'),(121,'1916392d-ce2a-443b-8211-dbc51ce8d84f','CHECK_AUTH',NULL,NULL,'2026-01-24 00:14:46.443',NULL,'2026-01-24 00:14:46.444','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-7'),(122,'4104c5a9-2c8d-4dba-9231-69781ef7f34b','CHECK_AUTH',NULL,NULL,'2026-01-24 00:14:46.467',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(123,'b260a65f-de94-42b9-8df9-48335d86d82a','LOGOUT','admin',NULL,'2026-01-24 00:14:50.291',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-8'),(124,'cddc50e8-97c1-46c1-9f9d-0744df87797a','LOGIN',NULL,NULL,'2026-01-24 00:15:12.674',NULL,'2026-01-24 00:15:12.677','FAILED',3,NULL,NULL,NULL,'http-nio-8081-exec-3'),(125,'106e8846-3b27-426a-9641-a1c73f95a8ed','LOGIN',NULL,NULL,'2026-01-24 00:15:13.898',NULL,'2026-01-24 00:15:13.901','FAILED',3,NULL,NULL,NULL,'http-nio-8081-exec-10'),(126,'fc167ee1-7737-4028-a601-8990573c8213','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 00:15:43.950',NULL,'2026-01-24 00:15:44.536','COMPLETED',588,NULL,NULL,NULL,'http-nio-8081-exec-8'),(127,'88098665-8b61-45d1-9e06-919e81a4f424','CHECK_AUTH',NULL,NULL,'2026-01-24 00:15:44.587',NULL,'2026-01-24 00:15:44.587','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-6'),(128,'a3f9f8d3-27e7-4ade-9aaa-15172b3139aa','CHECK_AUTH',NULL,NULL,'2026-01-24 00:15:44.600',NULL,'2026-01-24 00:15:44.600','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-1'),(129,'ae040ff3-9e3a-4e8d-8ca2-8d04caf9911c','LOGIN',NULL,NULL,'2026-01-24 00:18:34.111',NULL,'2026-01-24 00:18:34.114','FAILED',3,NULL,NULL,NULL,'http-nio-8081-exec-6'),(130,'f0729ffa-9344-42dd-8166-42a360028279','LOGIN',NULL,NULL,'2026-01-24 00:18:55.556',NULL,'2026-01-24 00:18:55.655','COMPLETED',100,NULL,NULL,NULL,'http-nio-8081-exec-10'),(131,'ed066acd-4ea0-4418-8600-f9183a23c937','VIEW_PRODUCT_CATALOG','admin',NULL,'2026-01-24 00:19:34.847',NULL,'2026-01-24 00:19:34.895','FAILED',NULL,'Failed to retrieve product catalog: Failed to fetch products: Table \'syos_billing_web.shelf_inventory\' doesn\'t exist',NULL,NULL,'http-nio-8081-exec-9'),(132,'6e4afa65-1051-437d-8f6a-709f5e6e1cd7','VIEW_PRODUCT_CATALOG','admin',NULL,'2026-01-24 00:27:12.143',NULL,'2026-01-24 00:27:12.145','FAILED',NULL,'Failed to retrieve product catalog: Failed to fetch products: Table \'syos_billing_web.shelf_inventory\' doesn\'t exist',NULL,NULL,'http-nio-8081-exec-6'),(133,'d1a821e6-e622-4978-8064-65a2d56fb141','VIEW_PRODUCT_CATALOG','admin',NULL,'2026-01-24 00:27:14.202',NULL,'2026-01-24 00:27:14.203','FAILED',NULL,'Failed to retrieve product catalog: Failed to fetch products: Table \'syos_billing_web.shelf_inventory\' doesn\'t exist',NULL,NULL,'http-nio-8081-exec-2'),(134,'dab583a8-7de2-4247-be3c-3a8b4b3e6798','LOGIN',NULL,NULL,'2026-01-24 00:28:24.996',NULL,'2026-01-24 00:28:25.300','COMPLETED',1167,NULL,NULL,NULL,'http-nio-8081-exec-8'),(135,'b935a14a-6fe0-4e53-8c79-bd52ef323c87','VIEW_PRODUCT_CATALOG','admin',NULL,'2026-01-24 00:28:35.411',NULL,'2026-01-24 00:28:35.431','COMPLETED',19,NULL,NULL,NULL,'http-nio-8081-exec-9'),(136,'6a19bbb5-12a2-4c23-85ec-b12f831ac5a9','VIEW_PRODUCT_CATALOG','admin',NULL,'2026-01-24 00:29:48.472',NULL,'2026-01-24 00:29:48.475','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-2'),(137,'8410ff03-9e3c-4685-9e2f-abe4e74a30e2','LOGIN',NULL,NULL,'2026-01-24 00:42:57.796',NULL,'2026-01-24 00:42:58.084','COMPLETED',1196,NULL,NULL,NULL,'http-nio-8081-exec-6'),(138,'06b36fd6-08e8-4db3-a18a-a9127907cf6d','LOGIN',NULL,NULL,'2026-01-24 00:55:07.642',NULL,'2026-01-24 00:55:08.017','COMPLETED',1741,NULL,NULL,NULL,'http-nio-8081-exec-7'),(139,'a914af37-353e-4211-be13-5cb8ca77104a','VIEW_PRODUCT_CATALOG','admin',NULL,'2026-01-24 00:55:34.966',NULL,'2026-01-24 00:55:35.004','COMPLETED',39,NULL,NULL,NULL,'http-nio-8081-exec-10'),(140,'8bc4935a-9ea3-463f-b7f8-91d4c41b61e9','GET_PRODUCT_DETAILS','admin',NULL,'2026-01-24 00:57:48.229',NULL,'2026-01-24 00:57:48.233','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-3'),(141,'8217b9de-3c97-47f0-86af-be02d4a407e0','CREATE_PRODUCT','admin',NULL,'2026-01-24 01:08:44.844',NULL,'2026-01-24 01:08:44.855','COMPLETED',12,NULL,NULL,NULL,'http-nio-8081-exec-6'),(142,'fb3f2aae-97ee-4289-805b-9ec1bd83bd3a','UPDATE_PRODUCT','admin',NULL,'2026-01-24 01:09:52.513',NULL,'2026-01-24 01:09:52.525','COMPLETED',12,NULL,NULL,NULL,'http-nio-8081-exec-9'),(143,'c00f71f5-f8b2-45f8-9f9f-41965a76906b','DELETE_PRODUCT','admin',NULL,'2026-01-24 01:11:12.453',NULL,'2026-01-24 01:11:12.453','FAILED',NULL,'Product code is required',NULL,NULL,'http-nio-8081-exec-2'),(144,'a8f699b1-51ad-44b6-9162-f172043a40c0','DELETE_PRODUCT','admin',NULL,'2026-01-24 01:11:26.745',NULL,'2026-01-24 01:11:26.752','COMPLETED',8,NULL,NULL,NULL,'http-nio-8081-exec-3'),(145,'14bac941-285b-4b20-8207-f8d6f8c11131','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 01:22:15.493',NULL,'2026-01-24 01:22:16.904','COMPLETED',1412,NULL,NULL,NULL,'http-nio-8081-exec-6'),(146,'fd9d07d3-38b6-494e-b565-460e7188bc95','CHECK_AUTH',NULL,NULL,'2026-01-24 01:22:16.925',NULL,'2026-01-24 01:22:16.928','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-7'),(147,'2051de79-bd8f-406c-95b7-abfe24873c6a','CHECK_AUTH',NULL,NULL,'2026-01-24 01:22:16.938',NULL,'2026-01-24 01:22:16.938','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-8'),(148,'bd1e135b-8882-4edf-9c4f-7566d436ab98','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 01:24:13.581',NULL,'2026-01-24 01:24:13.873','COMPLETED',293,NULL,NULL,NULL,'http-nio-8081-exec-1'),(149,'9b2c08f3-ab64-447c-b18f-41b873354ed2','CHECK_AUTH',NULL,NULL,'2026-01-24 01:24:13.907',NULL,'2026-01-24 01:24:13.908','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-2'),(150,'84d4485b-0658-4502-beeb-ca86c6764140','CHECK_AUTH',NULL,NULL,'2026-01-24 01:24:13.925',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-3'),(151,'b46aae71-65e8-4f9a-ab84-b7830c06a9aa','CHECK_AUTH',NULL,NULL,'2026-01-24 01:26:01.312',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(152,'cea91338-fa53-4d65-8537-d41d094cd788','CHECK_AUTH',NULL,NULL,'2026-01-24 01:26:01.324',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-6'),(153,'d04fdf46-9539-4690-bf3d-4081d444925c','CHECK_AUTH',NULL,NULL,'2026-01-24 01:26:01.836',NULL,'2026-01-24 01:26:01.836','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-7'),(154,'69d0493b-7a34-4bf9-b41e-f68fa5170039','CHECK_AUTH',NULL,NULL,'2026-01-24 01:26:01.856',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-8'),(155,'fe9d8933-a665-4d48-ab3a-672063a4b5cd','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 01:29:21.185',NULL,'2026-01-24 01:29:21.189','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-10'),(156,'772b8e62-056c-45bf-86f6-aaf346776628','CHECK_AUTH',NULL,NULL,'2026-01-24 01:32:15.502',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(157,'0bbfadc7-04d4-43a8-89de-787fa0bf1dd1','CHECK_AUTH',NULL,NULL,'2026-01-24 01:32:15.516',NULL,'2026-01-24 01:32:15.516','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-3'),(158,'4e0a7e02-aaa9-4381-98bc-61e0c3648b85','CHECK_AUTH',NULL,NULL,'2026-01-24 01:33:31.078',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(159,'582cbfa9-767b-421f-bb4b-968c7ed611e9','CHECK_AUTH',NULL,NULL,'2026-01-24 01:33:31.081',NULL,'2026-01-24 01:33:31.081','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-6'),(160,'3f860173-ca64-4e4e-bc71-fa21ca01e9bd','CHECK_AUTH',NULL,NULL,'2026-01-24 01:33:31.840',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-7'),(161,'2c6651d9-c131-465b-8cc4-29c9132597ac','CHECK_AUTH',NULL,NULL,'2026-01-24 01:33:31.842',NULL,'2026-01-24 01:33:31.843','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-8'),(162,'d6453df8-339e-44f5-bfec-14562896e5fb','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 01:33:37.357',NULL,'2026-01-24 01:33:37.361','COMPLETED',6,NULL,NULL,NULL,'http-nio-8081-exec-9'),(163,'2c314864-8172-4043-8720-47df9972bad1','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 01:43:43.482',NULL,'2026-01-24 01:43:43.495','COMPLETED',30,NULL,NULL,NULL,'http-nio-8081-exec-2'),(164,'c6d8ea8e-389d-4467-944b-108c442c439b','CHECK_AUTH',NULL,NULL,'2026-01-24 01:44:30.086',NULL,'2026-01-24 01:44:30.089','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-4'),(165,'6c3b73bf-cf59-4107-b0b0-0721d09c1010','CHECK_AUTH',NULL,NULL,'2026-01-24 01:44:30.102',NULL,'2026-01-24 01:44:30.104','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-5'),(166,'78cbe522-ffc8-43bc-99c5-ffb8cc2c63af','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 01:44:33.154',NULL,'2026-01-24 01:44:33.161','COMPLETED',9,NULL,NULL,NULL,'http-nio-8081-exec-6'),(167,'b938241b-51f1-4c42-9a0a-c9e39dfe7fd6','LOGIN',NULL,NULL,'2026-01-24 07:43:54.389',NULL,'2026-01-24 07:43:54.567','FAILED',183,NULL,NULL,NULL,'http-nio-8081-exec-1'),(168,'15d7714d-f95a-44ae-b60e-23771ef0e664','LOGIN',NULL,NULL,'2026-01-24 07:59:09.195',NULL,'2026-01-24 07:59:09.358','FAILED',163,NULL,NULL,NULL,'http-nio-8081-exec-6'),(169,'05bb58a1-1bb6-4462-a176-615f59ab0f70','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 07:59:42.366',NULL,'2026-01-24 07:59:42.934','COMPLETED',566,NULL,NULL,NULL,'http-nio-8081-exec-9'),(170,'918f91df-8898-47e3-9d9a-7e663fcd0c61','CHECK_AUTH',NULL,NULL,'2026-01-24 07:59:42.971',NULL,'2026-01-24 07:59:42.975','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-10'),(171,'9f7b7e03-6468-4014-b031-8fc636746697','CHECK_AUTH',NULL,NULL,'2026-01-24 07:59:42.989',NULL,'2026-01-24 07:59:42.991','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-1'),(172,'51195e1a-1125-49b2-96e9-5d00e722122f','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 07:59:54.106',NULL,'2026-01-24 07:59:54.133','COMPLETED',28,NULL,NULL,NULL,'http-nio-8081-exec-3'),(173,'aa2972f1-9e09-42d9-bb12-85abfe0e2480','CHECK_AUTH',NULL,NULL,'2026-01-24 08:01:39.899',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(174,'db9f8142-8500-47ce-8587-8cabb34d7cba','CHECK_AUTH',NULL,NULL,'2026-01-24 08:01:39.910',NULL,'2026-01-24 08:01:39.913','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-6'),(175,'e133ce64-5bb3-43ca-8bbc-796d6683b02e','LOGOUT','psthirimanna',NULL,'2026-01-24 08:01:42.549',NULL,'2026-01-24 08:01:42.589','COMPLETED',39,NULL,NULL,NULL,'http-nio-8081-exec-7'),(176,'f605a73e-11c4-4255-9026-daf0601dc3bb','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 08:08:26.904',NULL,'2026-01-24 08:08:27.233','COMPLETED',330,NULL,NULL,NULL,'http-nio-8081-exec-10'),(177,'17826293-10f2-4214-b022-a6a460974eac','CHECK_AUTH',NULL,NULL,'2026-01-24 08:08:27.256',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-1'),(178,'3585f505-0222-4428-83b5-626559eedf86','CHECK_AUTH',NULL,NULL,'2026-01-24 08:08:27.268',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-3'),(179,'80460183-db10-42b1-b89a-120cbd81c8ad','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 08:09:24.302',NULL,'2026-01-24 08:09:24.309','COMPLETED',6,NULL,NULL,NULL,'http-nio-8081-exec-5'),(180,'54616ffb-bb9a-4a9a-89a4-77a97332e2c6','CREATE_PRODUCT','psthirimanna',NULL,'2026-01-24 08:29:45.605',NULL,'2026-01-24 08:29:45.617','FAILED',NULL,'Product with code \'OIL001\' already exists',NULL,NULL,'http-nio-8081-exec-8'),(181,'286f7395-08d1-4400-95a1-30bf141e6518','CREATE_PRODUCT','psthirimanna',NULL,'2026-01-24 08:29:51.143',NULL,'2026-01-24 08:29:51.150','COMPLETED',7,NULL,NULL,NULL,'http-nio-8081-exec-9'),(182,'000940c0-4601-404d-bb78-3599948e8c28','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 08:29:51.157',NULL,'2026-01-24 08:29:51.168','COMPLETED',11,NULL,NULL,NULL,'http-nio-8081-exec-10'),(183,'c09be7ce-a0c7-4921-b0e3-3e3786b19de3','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 08:54:22.195',NULL,'2026-01-24 08:54:22.679','COMPLETED',486,NULL,NULL,NULL,'http-nio-8081-exec-2'),(184,'2bc30a6e-6d05-4363-9431-59173f61d7d9','CHECK_AUTH',NULL,NULL,'2026-01-24 08:54:22.703',NULL,'2026-01-24 08:54:22.704','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-4'),(185,'efb676e0-9ce5-4f57-8a0d-c5362b2d7d05','CHECK_AUTH',NULL,NULL,'2026-01-24 08:54:22.714',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(186,'05de067c-ecb6-4fe7-8d85-85a47f081e7d','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 08:54:28.304',NULL,'2026-01-24 08:54:28.309','COMPLETED',7,NULL,NULL,NULL,'http-nio-8081-exec-6'),(187,'76fcfd47-d7a8-4a37-9714-bd3d6f999068','CHECK_AUTH',NULL,NULL,'2026-01-24 08:54:59.900',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-8'),(188,'1c69a2b6-ad33-46ee-b9a0-88cf69c9596c','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 08:55:06.758',NULL,'2026-01-24 08:55:07.111','COMPLETED',354,NULL,NULL,NULL,'http-nio-8081-exec-9'),(189,'82a66283-51ba-40c8-852c-5331c655cd82','CHECK_AUTH',NULL,NULL,'2026-01-24 08:55:07.136',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-10'),(190,'b7d5714c-8ef0-4b9b-83b0-f4fc5fe63ce8','CHECK_AUTH',NULL,NULL,'2026-01-24 08:55:07.145',NULL,'2026-01-24 08:55:07.146','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-1'),(191,'1e8de081-d32c-40ea-9b24-ef36c61d858a','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 08:55:08.867',NULL,'2026-01-24 08:55:08.871','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-3'),(192,'d85604f9-137d-481d-8c49-3417437f4217','CHECK_AUTH',NULL,NULL,'2026-01-24 08:55:19.997',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(193,'b5aaf587-85ff-4e28-bf64-4fb87e046043','CHECK_AUTH',NULL,NULL,'2026-01-24 08:55:20.014',NULL,'2026-01-24 08:55:20.014','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-4'),(194,'770afb70-7206-44fa-ac00-f153253e6c9f','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 08:55:23.696',NULL,'2026-01-24 08:55:23.707','COMPLETED',12,NULL,NULL,NULL,'http-nio-8081-exec-5'),(195,'852de776-f137-45af-8b31-e025c5deecbb','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 08:58:05.737',NULL,'2026-01-24 08:58:05.744','COMPLETED',6,NULL,NULL,NULL,'http-nio-8081-exec-7'),(196,'872ed6f9-a8b2-4835-bcec-743af91b4c00','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 08:58:15.747',NULL,'2026-01-24 08:58:15.749','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-8'),(197,'a4faa89d-524e-4696-b2fa-b6265b4c1d9f','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:00:55.737',NULL,'2026-01-24 09:00:55.745','COMPLETED',9,NULL,NULL,NULL,'http-nio-8081-exec-10'),(198,'1423ae21-b252-42df-b8d4-2fa9bba1fe89','DELETE_PRODUCT','psthirimanna',NULL,'2026-01-24 09:01:35.612',NULL,'2026-01-24 09:01:35.624','COMPLETED',12,NULL,NULL,NULL,'http-nio-8081-exec-2'),(199,'feef6bf1-07ca-4243-b4e9-7dda1b329262','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:01:35.629',NULL,'2026-01-24 09:01:35.637','COMPLETED',7,NULL,NULL,NULL,'http-nio-8081-exec-4'),(200,'ea824f34-a786-4798-ba3f-ceee4138610a','DELETE_PRODUCT','psthirimanna',NULL,'2026-01-24 09:01:40.591',NULL,'2026-01-24 09:01:40.597','COMPLETED',7,NULL,NULL,NULL,'http-nio-8081-exec-6'),(201,'966d72b1-d854-49cf-8770-21ec5b4fcfd2','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:01:40.604',NULL,'2026-01-24 09:01:40.607','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-7'),(202,'81ef8614-042e-43a6-bec8-12f2e8aa21c4','CHECK_AUTH',NULL,NULL,'2026-01-24 09:01:44.266',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-8'),(203,'d3e52314-473e-4265-856e-519181636826','CHECK_AUTH',NULL,NULL,'2026-01-24 09:01:44.304',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(204,'22cbda17-ab20-4766-a7de-3c3afdfcf6c2','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:01:46.413',NULL,'2026-01-24 09:01:46.415','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-10'),(205,'4c69d1ae-4a16-4ec2-bbff-8d1d989d26ce','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:12:22.747',NULL,'2026-01-24 09:12:22.749','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-2'),(206,'1d2366c7-993d-4233-8215-a78a1d470839','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:12:49.739',NULL,'2026-01-24 09:12:49.741','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-5'),(207,'dd430018-1fbd-4070-86ed-6c2d5cb270b0','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:28:27.779',NULL,'2026-01-24 09:28:27.781','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-7'),(208,'e1d2a1e5-3ad4-478e-8869-a9037e1e26d2','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 09:29:45.759',NULL,'2026-01-24 09:29:45.764','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-9'),(209,'da1d1fb9-5dd0-4f1e-bf8c-c37f200bbbf9','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 14:27:33.138',NULL,'2026-01-24 14:27:33.559','COMPLETED',425,NULL,NULL,NULL,'http-nio-8081-exec-4'),(210,'547c606f-b211-4654-8abc-6c03b281303e','CHECK_AUTH',NULL,NULL,'2026-01-24 14:27:33.587',NULL,'2026-01-24 14:27:33.587','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-5'),(211,'3c122d88-4a7d-46a1-94eb-87272c7d5e29','CHECK_AUTH',NULL,NULL,'2026-01-24 14:27:33.601',NULL,'2026-01-24 14:27:33.602','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-6'),(212,'6f5a25cb-becc-401b-9195-74c8aa09c473','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 14:29:08.112',NULL,'2026-01-24 14:29:08.145','COMPLETED',32,NULL,NULL,NULL,'http-nio-8081-exec-9'),(213,'687fd4ae-dfc3-4ee6-bc31-7d7ac32cd332','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 14:37:53.374',NULL,'2026-01-24 14:37:53.377','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-1'),(214,'76712137-4169-421f-afd1-feffa8a2d0a4','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 15:38:27.555',NULL,'2026-01-24 15:38:28.365','COMPLETED',2282,NULL,NULL,NULL,'http-nio-8081-exec-6'),(215,'cc13cec3-b996-4136-87a2-b3ea9413c239','CHECK_AUTH',NULL,NULL,'2026-01-24 15:38:28.392',NULL,'2026-01-24 15:38:28.393','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-7'),(216,'fbcb3de5-53f1-493a-b234-184ed0ec23c3','CHECK_AUTH',NULL,NULL,'2026-01-24 15:38:28.408',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-8'),(217,'e21fba8d-805b-4d54-ad59-79d55753af40','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 15:38:30.339',NULL,'2026-01-24 15:38:30.358','COMPLETED',21,NULL,NULL,NULL,'http-nio-8081-exec-9'),(218,'0bdad4ca-16ca-42bc-b3d3-fcc1d4b3fd30','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 15:41:14.287',NULL,'2026-01-24 15:41:14.288','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-3'),(219,'507ec5f8-34f7-4589-9d41-e94dcb69eeed','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 15:43:17.329',NULL,'2026-01-24 15:43:17.332','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-5'),(220,'748e6423-4e55-4a20-b650-c08e63c92312','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 15:43:51.317',NULL,'2026-01-24 15:43:51.319','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-7'),(221,'56e6b58b-6480-4100-9b3a-08468f1dd834','CREATE_PRODUCT','psthirimanna',NULL,'2026-01-24 15:46:27.991',NULL,'2026-01-24 15:46:28.026','FAILED',NULL,'Failed to create product: Failed to save product: Data truncation: Data too long for column \'image_url\' at row 1',NULL,NULL,'http-nio-8081-exec-10'),(222,'05a5ce2c-137e-43ea-a30e-8b4710552c97','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 15:46:55.286',NULL,'2026-01-24 15:46:55.288','COMPLETED',8,NULL,NULL,NULL,'http-nio-8081-exec-2'),(223,'8c450ec0-4fc8-4ba7-8da4-cba4768c194a','CREATE_PRODUCT','psthirimanna',NULL,'2026-01-24 15:48:53.490',NULL,'2026-01-24 15:48:53.499','COMPLETED',9,NULL,NULL,NULL,'http-nio-8081-exec-4'),(224,'515ec4af-d6c9-425a-9d72-2bb1bb35ed5c','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 15:48:53.503',NULL,'2026-01-24 15:48:53.528','COMPLETED',26,NULL,NULL,NULL,'http-nio-8081-exec-5'),(225,'bddf3304-a0f0-405f-886d-9d641ecd7ab6','UPDATE_PRODUCT','psthirimanna',NULL,'2026-01-24 16:03:08.443',NULL,'2026-01-24 16:03:08.466','COMPLETED',24,NULL,NULL,NULL,'http-nio-8081-exec-7'),(226,'901240c8-8d61-41fa-8d64-973b8243c6ce','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 16:03:08.473',NULL,'2026-01-24 16:03:08.477','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-8'),(227,'1947fead-789a-4826-b82e-616ee26c5130','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 16:30:02.129',NULL,'2026-01-24 16:30:02.486','COMPLETED',359,NULL,NULL,NULL,'http-nio-8081-exec-10'),(228,'3be9c421-fe35-4119-aa91-10910d9ca281','CHECK_AUTH',NULL,NULL,'2026-01-24 16:30:02.506',NULL,'2026-01-24 16:30:02.507','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-1'),(229,'f365ac32-a06f-4283-888b-3d51344d8c91','CHECK_AUTH',NULL,NULL,'2026-01-24 16:30:02.516',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(230,'2b823246-82cb-4168-99a2-51fb8525694c','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 16:30:14.553',NULL,'2026-01-24 16:30:14.559','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-3'),(231,'98bb54c1-069c-4fec-beff-f28023679c54','CHECK_AUTH',NULL,NULL,'2026-01-24 16:33:37.763',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(232,'1185c849-4fb4-4d3f-9b2b-cb912c005ed9','CHECK_AUTH',NULL,NULL,'2026-01-24 16:33:37.775',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-6'),(233,'c597d1d6-c3ef-49b5-9d49-9a0b8206a7c3','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 16:33:40.984',NULL,'2026-01-24 16:33:40.987','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-7'),(234,'1baa7263-fb2a-4ce7-83d1-73513f4fec66','LOGIN',NULL,NULL,'2026-01-24 22:27:19.732',NULL,'2026-01-24 22:27:20.102','COMPLETED',391,NULL,NULL,NULL,'http-nio-8081-exec-10'),(235,'60100c87-97fc-435d-8d1e-f3881e771198','LOGIN',NULL,NULL,'2026-01-24 22:36:29.959',NULL,'2026-01-24 22:36:30.247','COMPLETED',1179,NULL,NULL,NULL,'http-nio-8081-exec-8'),(236,'715e6ac0-39e7-43a7-ac6d-459aac6a4819','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 22:49:42.131',NULL,'2026-01-24 22:49:43.487','COMPLETED',1357,NULL,NULL,NULL,'http-nio-8081-exec-2'),(237,'bb3e54c9-98cc-4d68-bfaf-96720d3c7e74','CHECK_AUTH',NULL,NULL,'2026-01-24 22:49:43.511',NULL,'2026-01-24 22:49:43.512','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-3'),(238,'8ca33b1d-e229-4ac9-bbf5-f28255c1ec9c','CHECK_AUTH',NULL,NULL,'2026-01-24 22:49:43.522',NULL,'2026-01-24 22:49:43.522','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-4'),(239,'ca372164-08e0-4b94-b0ed-5171bb453fa1','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 22:56:06.398',NULL,'2026-01-24 22:56:06.481','COMPLETED',84,NULL,NULL,NULL,'http-nio-8081-exec-7'),(240,'3e8396f4-c9ec-436b-80e9-6cbebae0f0f1','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 22:58:01.742',NULL,'2026-01-24 22:58:02.106','COMPLETED',365,NULL,NULL,NULL,'http-nio-8081-exec-9'),(241,'1cfb1875-0a15-47ce-94df-c525404f7bb6','CHECK_AUTH',NULL,NULL,'2026-01-24 22:58:02.134',NULL,'2026-01-24 22:58:02.135','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-10'),(242,'2633ef36-7ee0-4568-8d77-62105fd52a7b','CHECK_AUTH',NULL,NULL,'2026-01-24 22:58:02.146',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-1'),(243,'35394221-1f64-4332-96ac-ab669127548c','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 22:58:03.328',NULL,'2026-01-24 22:58:03.343','COMPLETED',15,NULL,NULL,NULL,'http-nio-8081-exec-3'),(244,'68f78286-2b14-49bb-ab25-04692a4db6ce','CREATE_PRODUCT','psthirimanna',NULL,'2026-01-24 22:59:30.294',NULL,'2026-01-24 22:59:30.313','COMPLETED',20,NULL,NULL,NULL,'http-nio-8081-exec-7'),(245,'fd7d6893-9589-4c2b-ae87-a1bc1ff3e1d4','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 22:59:30.318',NULL,'2026-01-24 22:59:30.332','COMPLETED',15,NULL,NULL,NULL,'http-nio-8081-exec-8'),(246,'eca4165f-07c8-4b52-934d-524e382dbc4c','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:00:21.299',NULL,'2026-01-24 23:00:21.302','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-1'),(247,'5d0e0523-7d14-46d8-a620-ed282098c666','CHECK_AUTH',NULL,NULL,'2026-01-24 23:00:21.302',NULL,'2026-01-24 23:00:21.304','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-3'),(248,'827b800a-2d52-4391-a434-978120e11ba6','CHECK_AUTH',NULL,NULL,'2026-01-24 23:00:21.307',NULL,'2026-01-24 23:00:21.316','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-4'),(249,'18c895ce-9ba0-4a1d-89cd-8a133122f87c','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:00:37.277',NULL,'2026-01-24 23:00:37.280','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-5'),(250,'91878256-76e8-4371-a21a-357a859d3870','UPDATE_PRODUCT','psthirimanna',NULL,'2026-01-24 23:01:01.112',NULL,'2026-01-24 23:01:01.136','COMPLETED',25,NULL,NULL,NULL,'http-nio-8081-exec-9'),(251,'a0518b64-61d3-4b07-92e0-fd2690f2b212','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:01:01.141',NULL,'2026-01-24 23:01:01.143','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-2'),(252,'ef7fa8bc-1599-4f45-b59a-a92b1ae986b2','UPDATE_PRODUCT','psthirimanna',NULL,'2026-01-24 23:01:17.700',NULL,'2026-01-24 23:01:17.708','COMPLETED',9,NULL,NULL,NULL,'http-nio-8081-exec-3'),(253,'a9b063a7-ff1e-413b-a7af-f7b8816c845b','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:01:17.712',NULL,'2026-01-24 23:01:17.715','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-1'),(254,'abf69f7d-fb71-4c63-b2eb-637c688712f7','UPDATE_PRODUCT','psthirimanna',NULL,'2026-01-24 23:01:41.120',NULL,'2026-01-24 23:01:41.128','COMPLETED',8,NULL,NULL,NULL,'http-nio-8081-exec-6'),(255,'0e3cd874-d33f-4475-8c02-c5bd5522a12d','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:01:41.133',NULL,'2026-01-24 23:01:41.135','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-5'),(256,'4e7b90f3-2fd7-4930-b0a9-421d8f470011','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:01:53.286',NULL,'2026-01-24 23:01:53.289','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-7'),(257,'ebd970bb-1aba-459c-ad47-533191cc57c0','UPDATE_PRODUCT','psthirimanna',NULL,'2026-01-24 23:02:53.975',NULL,'2026-01-24 23:02:53.984','COMPLETED',9,NULL,NULL,NULL,'http-nio-8081-exec-2'),(258,'b9c3f67e-2abd-4fc0-94e9-2f249a0c6e53','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:02:53.988',NULL,'2026-01-24 23:02:53.991','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-3'),(259,'36965160-e613-4630-ad19-9a21a23acd22','UPDATE_PRODUCT','psthirimanna',NULL,'2026-01-24 23:03:04.695',NULL,'2026-01-24 23:03:04.706','COMPLETED',12,NULL,NULL,NULL,'http-nio-8081-exec-1'),(260,'255efeeb-3386-4e2d-8ff6-4a8287842595','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:03:04.710',NULL,'2026-01-24 23:03:04.713','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-4'),(261,'4a1d132a-8eb3-41a8-8616-92f11564c75f','LOGOUT','psthirimanna',NULL,'2026-01-24 23:03:41.492',NULL,'2026-01-24 23:03:41.494','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-5'),(262,'dbbd254c-76e6-41be-b85e-8ed5120bd0ba','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 23:19:00.262',NULL,'2026-01-24 23:19:01.231','COMPLETED',2852,NULL,NULL,NULL,'http-nio-8081-exec-5'),(263,'73317803-27e1-45d4-b694-9fab198c0839','CHECK_AUTH',NULL,NULL,'2026-01-24 23:19:01.261',NULL,'2026-01-24 23:19:01.262','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-6'),(264,'d996f49f-15cb-4566-b07f-e2f4d75a00e6','CHECK_AUTH',NULL,NULL,'2026-01-24 23:19:01.273',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-8'),(265,'14577265-2ad8-42b2-8be7-fa2ae4bf292b','CHECK_AUTH',NULL,NULL,'2026-01-24 23:22:02.310',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-10'),(266,'62ff8940-8884-4c41-a772-ad461439aadd','CHECK_AUTH',NULL,NULL,'2026-01-24 23:22:02.313',NULL,'2026-01-24 23:22:02.315','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-1'),(267,'0b5f2939-3d38-4bbf-8848-a9dbfd5bbc7b','CHECK_AUTH',NULL,NULL,'2026-01-24 23:22:02.320',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-4'),(268,'4fea7729-c3df-4384-9915-19c89cc502a5','CHECK_AUTH',NULL,NULL,'2026-01-24 23:22:02.324',NULL,'2026-01-24 23:22:02.327','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-5'),(269,'a330ebd6-a562-4375-9ee1-26d3599feb73','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:22:02.337',NULL,'2026-01-24 23:22:02.362','COMPLETED',39,NULL,NULL,NULL,'http-nio-8081-exec-2'),(270,'af4f0048-c303-45b8-a2c4-f806c7b7834a','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:23:32.480',NULL,'2026-01-24 23:23:32.489','COMPLETED',8,NULL,NULL,NULL,'http-nio-8081-exec-10'),(271,'d4454212-518a-452d-8430-de3274222e6d','CHECK_AUTH',NULL,NULL,'2026-01-24 23:24:04.279',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-2'),(272,'68c9f4f4-71e2-46cf-aa87-54de2f05f543','CHECK_AUTH',NULL,NULL,'2026-01-24 23:24:04.285',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-6'),(273,'e591f702-a31d-479a-9783-c73585ee1f57','CHECK_AUTH',NULL,NULL,'2026-01-24 23:24:04.288',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(274,'a7baa690-25da-4b96-82d0-a515080ebd99','CHECK_AUTH',NULL,NULL,'2026-01-24 23:24:04.290',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-8'),(275,'eba6db24-d08d-4cd8-be2e-3893e6966ad7','LOGOUT','psthirimanna',NULL,'2026-01-24 23:24:22.450',NULL,'2026-01-24 23:24:22.450','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-1'),(276,'3754b6fd-beef-4c12-a6a9-789a3fe7e70c','GOOGLE_LOGIN',NULL,NULL,'2026-01-24 23:28:28.069',NULL,'2026-01-24 23:28:28.570','COMPLETED',502,NULL,NULL,NULL,'http-nio-8081-exec-8'),(277,'9dd930f5-9a6a-40ca-99a0-4bc7e3906c08','CHECK_AUTH',NULL,NULL,'2026-01-24 23:28:28.587',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-1'),(278,'0c1cde72-b31f-4e60-993e-b0a738ef7be3','CHECK_AUTH',NULL,NULL,'2026-01-24 23:28:28.596',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-10'),(279,'109500b4-684f-4c20-ae9e-9f7d794f0110','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:28:32.518',NULL,'2026-01-24 23:28:32.521','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-4'),(280,'cee64a64-caf5-4c7e-bd22-cf5c9f5edac8','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:28:36.590',NULL,'2026-01-24 23:28:36.593','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-2'),(281,'049275b6-ac9c-463d-b3be-68b3de43fd92','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-24 23:31:34.905',NULL,'2026-01-24 23:31:34.908','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-4'),(282,'86d63493-8ec7-4f71-b967-7942a3180900','GOOGLE_LOGIN',NULL,NULL,'2026-01-25 05:05:52.678',NULL,'2026-01-25 05:05:53.476','COMPLETED',1893,NULL,NULL,NULL,'http-nio-8081-exec-1'),(283,'2200cef7-a546-482f-af48-8e1aabe4621c','CHECK_AUTH',NULL,NULL,'2026-01-25 05:05:53.502',NULL,'2026-01-25 05:05:53.505','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-2'),(284,'3de60596-3735-46bc-ae41-df887d8c3d9d','CHECK_AUTH',NULL,NULL,'2026-01-25 05:05:53.516',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-7'),(285,'d64d25e6-e752-4247-8220-2b8d14dc3694','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-25 05:06:03.647',NULL,'2026-01-25 05:06:03.701','COMPLETED',55,NULL,NULL,NULL,'http-nio-8081-exec-5'),(286,'2e2ae487-a7f0-42a0-bf5c-e480b1448c0d','LOGIN',NULL,NULL,'2026-01-25 05:46:28.118',NULL,'2026-01-25 05:46:28.233','COMPLETED',116,NULL,NULL,NULL,'http-nio-8081-exec-7'),(287,'0ab65dbb-88df-4df3-95ba-9a7a06af3fde','LOGIN',NULL,NULL,'2026-01-25 05:48:59.071',NULL,'2026-01-25 05:48:59.218','COMPLETED',668,NULL,NULL,NULL,'http-nio-8081-exec-8'),(288,'a70673e0-d2ab-46b9-af4b-4126ea872a65','VIEW_INVENTORY','admin',NULL,'2026-01-25 05:49:01.707',NULL,'2026-01-25 05:49:01.735','FAILED',24,'Failed to retrieve inventory: Failed making field \'java.time.LocalDate#year\' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type.',NULL,NULL,'http-nio-8081-exec-9'),(289,'2e37057f-a5ea-47ec-b95c-02dca28ba908','LOGIN',NULL,NULL,'2026-01-25 06:52:32.212',NULL,'2026-01-25 06:52:32.372','COMPLETED',666,NULL,NULL,NULL,'http-nio-8081-exec-1'),(290,'4113d465-42b0-49cf-b9e7-f46ee96f372c','VIEW_INVENTORY','admin',NULL,'2026-01-25 06:52:34.909',NULL,'2026-01-25 06:52:34.920','COMPLETED',12,NULL,NULL,NULL,'http-nio-8081-exec-2'),(291,'bc58885d-b5af-46e5-9c11-cb16db04dd96','VIEW_INVENTORY','admin',NULL,'2026-01-25 07:00:08.147',NULL,'2026-01-25 07:00:08.151','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-6'),(292,'a601879c-91c2-4455-acbb-f05c1ee7250b','VIEW_INVENTORY','admin',NULL,'2026-01-25 07:28:41.942',NULL,'2026-01-25 07:28:41.949','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-9'),(293,'6e5bf60b-14c3-4215-928d-ce043796a5b7','VIEW_INVENTORY','admin',NULL,'2026-01-25 07:37:52.388',NULL,'2026-01-25 07:37:52.390','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-1'),(294,'5a3b23dc-3f77-4cd2-9486-e2ba42510d00','VIEW_INVENTORY','admin',NULL,'2026-01-25 07:38:35.895',NULL,'2026-01-25 07:38:35.897','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-5'),(295,'4de04f62-0d4e-4e37-a50b-6135429934b9','VIEW_INVENTORY','admin',NULL,'2026-01-25 07:40:37.114',NULL,'2026-01-25 07:40:37.116','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-8'),(296,'ec95c3f9-3543-4088-80d4-f64a82b9f9bb','VIEW_INVENTORY','admin',NULL,'2026-01-25 07:42:19.368',NULL,'2026-01-25 07:42:19.370','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-3'),(297,'b21860e0-d112-4e9c-a1a1-d6288d436909','VIEW_INVENTORY','admin',NULL,'2026-01-25 07:42:30.891',NULL,'2026-01-25 07:42:30.893','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-1'),(298,'405c169a-7fcf-4b9b-ad85-530cd8968c44','GOOGLE_LOGIN',NULL,NULL,'2026-01-25 09:12:12.651',NULL,'2026-01-25 09:12:13.361','COMPLETED',1347,NULL,NULL,NULL,'http-nio-8081-exec-7'),(299,'e025bce0-d001-498c-bd1d-465ccfcc2356','CHECK_AUTH',NULL,NULL,'2026-01-25 09:12:13.379',NULL,'2026-01-25 09:12:13.380','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-8'),(300,'dd80187d-5a16-4319-bfd2-c7e45820de7b','CHECK_AUTH',NULL,NULL,'2026-01-25 09:12:13.388',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(301,'7ab1bf73-2c40-438f-91bd-bdd1ad5072ad','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-25 09:12:19.660',NULL,'2026-01-25 09:12:19.674','COMPLETED',15,NULL,NULL,NULL,'http-nio-8081-exec-1'),(302,'66e4901c-9370-4ab4-8cc4-679a52b0d52c','GOOGLE_LOGIN',NULL,NULL,'2026-01-25 09:24:18.081',NULL,'2026-01-25 09:24:18.809','COMPLETED',1938,NULL,NULL,NULL,'http-nio-8081-exec-3'),(303,'8dc5a475-abd7-472d-8073-0d8432a09a16','CHECK_AUTH',NULL,NULL,'2026-01-25 09:24:18.831',NULL,'2026-01-25 09:24:18.832','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-4'),(304,'570f0558-d893-4d6e-b0ee-350f9522a9e5','CHECK_AUTH',NULL,NULL,'2026-01-25 09:24:18.842',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-5'),(305,'aeb4cb14-0db7-4e11-a4b8-bdd4c7be12d8','LOGIN',NULL,NULL,'2026-01-25 09:28:01.385',NULL,'2026-01-25 09:28:01.529','COMPLETED',146,NULL,NULL,NULL,'http-nio-8081-exec-10'),(306,'4402fb67-1e79-427b-afec-83593c11bb4e','RECEIVE_STOCK','admin',NULL,'2026-01-25 09:28:05.289',NULL,'2026-01-25 09:28:05.298','FAILED',NULL,'Failed to receive stock: Failed making field \'java.time.LocalDate#year\' accessible; either increase its visibility or write a custom TypeAdapter for its declaring type.',NULL,NULL,'http-nio-8081-exec-1'),(307,'69e50d6e-60df-4597-a649-d9334871d4da','LOGIN',NULL,NULL,'2026-01-25 09:29:58.815',NULL,'2026-01-25 09:29:59.167','COMPLETED',1497,NULL,NULL,NULL,'http-nio-8081-exec-5'),(308,'f8313b2a-5bf1-44f2-8e4b-3be10d920fe1','RECEIVE_STOCK','admin',NULL,'2026-01-25 09:30:02.515',NULL,'2026-01-25 09:30:02.548','COMPLETED',35,NULL,NULL,NULL,'http-nio-8081-exec-6'),(309,'af15037d-2e86-4401-9c8e-1530e0f1e827','LOGIN',NULL,NULL,'2026-01-25 11:13:25.172',NULL,'2026-01-25 11:13:25.560','COMPLETED',1840,NULL,NULL,NULL,'http-nio-8081-exec-7'),(310,'2ae61555-a84c-4ae7-8ef2-00a8eca0fb8e','TRANSFER_STOCK','admin',NULL,'2026-01-25 11:13:31.562',NULL,'2026-01-25 11:13:31.676','COMPLETED',116,NULL,NULL,NULL,'http-nio-8081-exec-8'),(311,'20b4b99b-9b27-46b8-aede-338c19e1488d','GOOGLE_LOGIN',NULL,NULL,'2026-01-25 11:57:40.893',NULL,'2026-01-25 11:57:42.124','COMPLETED',1235,NULL,NULL,NULL,'http-nio-8081-exec-1'),(312,'5c4f1dfb-bff6-4569-8cc9-ff87f8e2242c','CHECK_AUTH',NULL,NULL,'2026-01-25 11:57:42.150',NULL,'2026-01-25 11:57:42.152','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-2'),(313,'8a5f68fd-e271-48db-9f7c-bdcb4430f740','CHECK_AUTH',NULL,NULL,'2026-01-25 11:57:42.161',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-3'),(314,'f94399d6-6d77-4311-b5a4-832d24f7c275','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:57:44.943',NULL,'2026-01-25 11:57:44.963','COMPLETED',21,NULL,NULL,NULL,'http-nio-8081-exec-5'),(315,'f371fa32-eb5a-4056-86f8-8cc43c6c66f2','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:57:53.753',NULL,'2026-01-25 11:57:53.756','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-6'),(316,'7d9bf1d5-bb5b-4bde-bceb-be9b67f8f4ef','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:57:54.739',NULL,'2026-01-25 11:57:54.741','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-4'),(317,'8a5097b6-8fbf-42e3-8521-3cf8223680a4','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:57:56.014',NULL,'2026-01-25 11:57:56.016','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-7'),(318,'e51ecdcb-fba9-40e8-99ae-16b0f39ed975','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:57:57.174',NULL,'2026-01-25 11:57:57.176','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-8'),(319,'0ea40bc1-a79d-4890-8e6b-de2c1a8b3b4f','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:26.813',NULL,'2026-01-25 11:58:26.815','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-1'),(320,'10856df2-f076-4504-bc9c-19eade420632','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:27.622',NULL,'2026-01-25 11:58:27.625','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-2'),(321,'cf986681-3cef-493c-a216-3b0c1470fd05','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:28.286',NULL,'2026-01-25 11:58:28.288','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-3'),(322,'fc4be930-73c0-4930-864c-cf5d26a53536','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:28.941',NULL,'2026-01-25 11:58:28.945','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-5'),(323,'6f097fdd-25d2-4a3b-9264-238adaa90b3d','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:29.645',NULL,'2026-01-25 11:58:29.648','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-6'),(324,'49edfc19-9a3e-416c-81fc-6e134666ff2f','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:30.659',NULL,'2026-01-25 11:58:30.661','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-4'),(325,'ae32e9c8-19d9-4c91-a9de-4ecb570941a2','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:31.129',NULL,'2026-01-25 11:58:31.131','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-7'),(326,'c335dda4-9e71-4feb-964b-dba7ff6b8c31','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:31.567',NULL,'2026-01-25 11:58:31.570','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-8'),(327,'9814afbb-7647-4492-a6df-c0781eee28b0','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:32.670',NULL,'2026-01-25 11:58:32.673','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-9'),(328,'d4b26e15-8d32-4d1b-8fe1-69801cc022b5','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 11:58:33.066',NULL,'2026-01-25 11:58:33.068','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-10'),(329,'c5639286-f1f7-4758-a4ec-8c5bfc088dab','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-25 11:59:14.382',NULL,'2026-01-25 11:59:14.409','COMPLETED',26,NULL,NULL,NULL,'http-nio-8081-exec-2'),(330,'a2d17033-4e42-405f-98f6-e182deb8d0ea','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-25 12:01:17.696',NULL,'2026-01-25 12:01:17.698','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-5'),(331,'efbcfc19-7d2f-4c8f-8e9f-1a680d8d38cd','CHECK_AUTH',NULL,NULL,'2026-01-25 12:01:39.925',NULL,'2026-01-25 12:01:39.925','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-7'),(332,'e429c49a-cb3d-40cd-962f-dd7855f266f1','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:01:39.925',NULL,'2026-01-25 12:01:39.930','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-4'),(333,'3736d1c6-30bf-425a-8ecd-4702236be2eb','CHECK_AUTH',NULL,NULL,'2026-01-25 12:01:39.928',NULL,'2026-01-25 12:01:39.931','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-8'),(334,'45c0b928-ca9d-489d-bde7-895f7cf2a654','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:03:13.937',NULL,'2026-01-25 12:03:13.940','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-2'),(335,'9abf1b9f-21db-4492-87bc-9423916fd6ba','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:03:46.916',NULL,'2026-01-25 12:03:46.918','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-5'),(336,'364b9c52-da98-4429-915a-7d2285dadf17','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:06:17.930',NULL,'2026-01-25 12:06:17.933','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-7'),(337,'f582e703-453b-4798-969d-11e68d2fa8c0','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:08:35.901',NULL,'2026-01-25 12:08:35.903','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-4'),(338,'437e54a3-6857-4ff9-a9d3-8b84129dcb3f','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:10:57.541',NULL,'2026-01-25 12:10:57.543','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-1'),(339,'77a9a1bd-bb83-450b-9a88-726e49bc330e','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:10:59.096',NULL,'2026-01-25 12:10:59.099','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-10'),(340,'de49905d-c16f-4fbb-b6df-876cd15b0609','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:10:59.923',NULL,'2026-01-25 12:10:59.926','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-2'),(341,'5e378909-19dd-40a5-b018-07f9f4331530','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:16.703',NULL,'2026-01-25 12:13:16.706','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-5'),(342,'74b39121-6813-4ca7-8e09-aaca8e705764','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:17.227',NULL,'2026-01-25 12:13:17.229','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-6'),(343,'82c64a7e-b2af-47b6-baea-08302ddfa798','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:17.795',NULL,'2026-01-25 12:13:17.797','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-7'),(344,'5cd94cb6-1e4c-4555-8f9c-957565518b74','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:18.562',NULL,'2026-01-25 12:13:18.565','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-8'),(345,'d2c621bf-ccb7-4d00-bca8-0eb71b816972','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:19.964',NULL,'2026-01-25 12:13:19.967','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-4'),(346,'334b9873-e492-45a3-990c-c6d7de38bb7d','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:20.662',NULL,'2026-01-25 12:13:20.664','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-9'),(347,'52b75c39-2fe1-4d34-86ab-705ca2ad55b3','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:21.317',NULL,'2026-01-25 12:13:21.318','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-1'),(348,'53015a69-bb70-46e7-b651-fd4658ab09e9','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:23.616',NULL,'2026-01-25 12:13:23.617','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-10'),(349,'ccb1d524-e5e7-48b3-9c1f-a32ab473a58f','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:24.707',NULL,'2026-01-25 12:13:24.709','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-2'),(350,'e73cd341-4007-4c79-a781-3c096a674f2d','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:30.920',NULL,'2026-01-25 12:13:30.922','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-3'),(351,'a1068a20-178b-4c74-8d59-80b8568452bc','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:31.777',NULL,'2026-01-25 12:13:31.779','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-5'),(352,'a414b7df-42cd-4b3c-b962-4044ddd14ae9','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:32.356',NULL,'2026-01-25 12:13:32.358','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-6'),(353,'57916923-12a2-487a-ac7f-eb2740224127','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:32.911',NULL,'2026-01-25 12:13:32.913','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-7'),(354,'80e78646-d079-4a77-a726-14115f3582ba','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 12:13:33.426',NULL,'2026-01-25 12:13:33.428','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-8'),(355,'9b9ead31-964e-4066-909d-04d9a2d1af87','LOGIN',NULL,NULL,'2026-01-25 19:13:31.370',NULL,'2026-01-25 19:13:31.404','FAILED',43,NULL,NULL,NULL,'http-nio-8081-exec-6'),(356,'479f3f89-0d00-41e8-b5a5-131bf4de1307','GOOGLE_LOGIN',NULL,NULL,'2026-01-25 19:13:48.841',NULL,'2026-01-25 19:13:49.379','COMPLETED',537,NULL,NULL,NULL,'http-nio-8081-exec-8'),(357,'fe0412fe-3585-450f-a787-9d3c258e1b7d','CHECK_AUTH',NULL,NULL,'2026-01-25 19:13:49.406',NULL,'2026-01-25 19:13:49.406','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-4'),(358,'1f22352f-9bce-45d2-9521-d33e00553ece','CHECK_AUTH',NULL,NULL,'2026-01-25 19:13:49.417',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(359,'0e1c04be-5b67-4183-b604-14178a6cc1dc','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 19:13:56.012',NULL,'2026-01-25 19:13:56.042','COMPLETED',30,NULL,NULL,NULL,'http-nio-8081-exec-1'),(360,'b0a85fe4-353e-4c1d-b23c-8015230bedc4','GOOGLE_LOGIN',NULL,NULL,'2026-01-25 20:11:25.487',NULL,'2026-01-25 20:11:26.061','COMPLETED',1198,NULL,NULL,NULL,'http-nio-8081-exec-7'),(361,'38a9d0f3-e244-4aeb-b3ff-0d10e2b3b61b','CHECK_AUTH',NULL,NULL,'2026-01-25 20:11:26.080',NULL,'2026-01-25 20:11:26.081','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-8'),(362,'25050071-6e01-470f-be92-9d40c0820194','CHECK_AUTH',NULL,NULL,'2026-01-25 20:11:26.091',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-9'),(363,'c770990d-653b-4d24-8325-a8a210b06e5b','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 20:14:29.565',NULL,'2026-01-25 20:14:29.588','COMPLETED',24,NULL,NULL,NULL,'http-nio-8081-exec-1'),(364,'f3bba7ed-ec21-42b8-a83d-770c34890a8a','LOGIN',NULL,NULL,'2026-01-25 20:19:20.330',NULL,'2026-01-25 20:19:20.486','COMPLETED',156,NULL,NULL,NULL,'http-nio-8081-exec-4'),(365,'8741f73d-1603-4bd4-8f91-0ee7350899ae','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-25 20:34:02.917',NULL,'2026-01-25 20:34:02.925','COMPLETED',5,NULL,NULL,NULL,'http-nio-8081-exec-9'),(366,'62a8091c-2fff-485c-9b4a-6b9fef47cc19','CHECK_AUTH',NULL,NULL,'2026-01-25 20:34:02.917',NULL,'2026-01-25 20:34:02.917','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-1'),(367,'bf54919d-85ea-4279-80a4-20102e993963','CHECK_AUTH',NULL,NULL,'2026-01-25 20:34:02.924',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-10'),(368,'c8444057-8ff2-4a29-83ca-f7019b57b3b6','GOOGLE_LOGIN',NULL,NULL,'2026-01-25 21:09:13.862',NULL,'2026-01-25 21:09:14.876','COMPLETED',1016,NULL,NULL,NULL,'http-nio-8081-exec-1'),(369,'8ea5de26-e615-4a1e-9c6a-2ffa2a876d58','CHECK_AUTH',NULL,NULL,'2026-01-25 21:09:14.898',NULL,NULL,'PROCESSING',NULL,NULL,NULL,NULL,'http-nio-8081-exec-10'),(370,'18dccf71-fa6f-4662-b809-86396e0fb863','CHECK_AUTH',NULL,NULL,'2026-01-25 21:09:14.907',NULL,'2026-01-25 21:09:14.908','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-9'),(371,'0f663077-c115-40d3-a5f8-a47b77f5b5da','GOOGLE_LOGIN',NULL,NULL,'2026-01-27 14:50:25.977',NULL,'2026-01-27 14:50:27.286','COMPLETED',3352,NULL,NULL,NULL,'http-nio-8081-exec-9'),(372,'9e2945b8-be2b-4474-9351-385ff72ba107','CHECK_AUTH',NULL,NULL,'2026-01-27 14:50:27.323',NULL,'2026-01-27 14:50:27.326','COMPLETED',3,NULL,NULL,NULL,'http-nio-8081-exec-10'),(373,'d60b2e08-365c-456a-b9b8-19194eeb4197','CHECK_AUTH',NULL,NULL,'2026-01-27 14:50:27.356',NULL,'2026-01-27 14:50:27.357','COMPLETED',2,NULL,NULL,NULL,'http-nio-8081-exec-1'),(374,'cdf4b911-ac6b-4fec-b61f-36cb642ccbae','GOOGLE_LOGIN',NULL,NULL,'2026-01-27 15:29:55.376',NULL,'2026-01-27 15:29:55.877','COMPLETED',506,NULL,NULL,NULL,'http-nio-8081-exec-7'),(375,'d9ef2199-687a-4e73-80c0-965a5bdaebd8','CHECK_AUTH',NULL,NULL,'2026-01-27 15:29:55.931',NULL,'2026-01-27 15:29:55.932','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-5'),(376,'fc6e3741-dabd-40a4-9bb8-3ecabffe02b2','CHECK_AUTH',NULL,NULL,'2026-01-27 15:29:55.959',NULL,'2026-01-27 15:29:55.959','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-8'),(377,'8b857183-1809-4129-ba75-49eb91f65f81','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-27 15:48:16.936',NULL,'2026-01-27 15:48:16.978','COMPLETED',42,NULL,NULL,NULL,'http-nio-8081-exec-10'),(378,'760a744b-937c-4496-9b50-b8468e4fcfda','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-27 15:48:52.757',NULL,'2026-01-27 15:48:52.761','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-3'),(379,'5dc73ba0-3f80-43e7-a314-ad4a4f032c66','GOOGLE_LOGIN',NULL,NULL,'2026-01-27 16:27:21.287',NULL,'2026-01-27 16:27:21.670','COMPLETED',383,NULL,NULL,NULL,'http-nio-8081-exec-8'),(380,'d893c151-89c7-4976-b345-40d8db31a293','CHECK_AUTH',NULL,NULL,'2026-01-27 16:27:21.697',NULL,'2026-01-27 16:27:21.698','COMPLETED',0,NULL,NULL,NULL,'http-nio-8081-exec-9'),(381,'0408de3c-9d9f-4699-97c9-9e9426f58c40','CHECK_AUTH',NULL,NULL,'2026-01-27 16:27:21.709',NULL,'2026-01-27 16:27:21.710','COMPLETED',1,NULL,NULL,NULL,'http-nio-8081-exec-10'),(382,'3b18e4c1-27cc-4b95-9b42-7ae9e719d9c6','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-27 16:27:24.829',NULL,'2026-01-27 16:27:24.834','COMPLETED',6,NULL,NULL,NULL,'http-nio-8081-exec-1'),(383,'dafd806b-cee5-43ce-a552-9aac7ba3b212','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-27 16:28:02.763',NULL,'2026-01-27 16:28:02.766','COMPLETED',4,NULL,NULL,NULL,'http-nio-8081-exec-6'),(384,'987ada3c-3249-4421-a98f-777db5b522c3','VIEW_INVENTORY','psthirimanna',NULL,'2026-01-27 16:30:13.335',NULL,'2026-01-27 16:30:13.339','COMPLETED',7,NULL,NULL,NULL,'http-nio-8081-exec-5'),(385,'d70a786a-f295-4c89-9dd6-351620a33fba','VIEW_PRODUCT_CATALOG','psthirimanna',NULL,'2026-01-27 16:30:28.736',NULL,'2026-01-27 16:30:28.782','COMPLETED',45,NULL,NULL,NULL,'http-nio-8081-exec-8');
/*!40000 ALTER TABLE `request_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL,
  `role_name` varchar(50) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'CASHIER'),(3,'CUSTOMER'),(1,'MANAGER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_batches`
--

DROP TABLE IF EXISTS `stock_batches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_batches` (
  `batch_id` int NOT NULL AUTO_INCREMENT,
  `product_code` varchar(50) NOT NULL,
  `purchase_date` date NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `available_quantity` int NOT NULL DEFAULT '0',
  `discount_percentage` decimal(5,2) DEFAULT '0.00',
  `version` int NOT NULL DEFAULT '0',
  `last_updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`batch_id`),
  KEY `idx_batch_product` (`product_code`),
  KEY `idx_batch_expiry` (`product_code`,`expiry_date`),
  KEY `idx_batch_version` (`batch_id`,`version`),
  CONSTRAINT `stock_batches_ibfk_1` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_batches`
--

LOCK TABLES `stock_batches` WRITE;
/*!40000 ALTER TABLE `stock_batches` DISABLE KEYS */;
INSERT INTO `stock_batches` VALUES (1,'RICE001','2026-01-01','2026-12-31',200,0.00,0,'2026-01-24 00:29:40'),(2,'DHAL001','2026-01-01','2026-06-30',150,0.00,0,'2026-01-24 00:29:40'),(3,'OIL001','2026-01-01','2026-08-31',100,0.00,0,'2026-01-24 00:29:40'),(4,'MILK001','2026-01-20','2026-01-27',50,0.00,0,'2026-01-24 00:29:40'),(5,'BREAD001','2026-01-21','2026-01-24',80,0.00,0,'2026-01-24 00:29:40'),(6,'TEA001','2026-01-01','2027-01-01',120,0.00,0,'2026-01-24 00:29:40'),(7,'SUGAR001','2026-01-01','2027-12-31',250,0.00,0,'2026-01-24 00:29:40'),(8,'SALT001','2026-01-01','2028-01-01',300,0.00,0,'2026-01-24 00:29:40'),(9,'RICE001','2026-01-25','2027-01-25',50,0.00,0,'2026-01-25 09:30:02');
/*!40000 ALTER TABLE `stock_batches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_movements`
--

DROP TABLE IF EXISTS `stock_movements`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stock_movements` (
  `movement_id` bigint NOT NULL AUTO_INCREMENT,
  `product_code` varchar(50) NOT NULL,
  `batch_id` int NOT NULL,
  `quantity` int NOT NULL,
  `from_location` enum('MAIN','SHELF','WEBSITE','SUPPLIER') NOT NULL,
  `to_location` enum('MAIN','SHELF','WEBSITE','CUSTOMER') NOT NULL,
  `movement_type` enum('RESTOCK','TRANSFER','SALE','ADJUSTMENT','RETURN') NOT NULL,
  `movement_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `user_id` varchar(50) DEFAULT NULL,
  `bill_number` varchar(50) DEFAULT NULL,
  `notes` text,
  `previous_quantity` int DEFAULT NULL,
  `new_quantity` int DEFAULT NULL,
  PRIMARY KEY (`movement_id`),
  KEY `batch_id` (`batch_id`),
  KEY `idx_movement_date` (`movement_date`),
  KEY `idx_product` (`product_code`),
  KEY `idx_type` (`movement_type`),
  KEY `idx_user` (`user_id`),
  CONSTRAINT `stock_movements_ibfk_1` FOREIGN KEY (`product_code`) REFERENCES `products` (`product_code`),
  CONSTRAINT `stock_movements_ibfk_2` FOREIGN KEY (`batch_id`) REFERENCES `stock_batches` (`batch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_movements`
--

LOCK TABLES `stock_movements` WRITE;
/*!40000 ALTER TABLE `stock_movements` DISABLE KEYS */;
INSERT INTO `stock_movements` VALUES (1,'RICE001',1,20,'MAIN','SHELF','TRANSFER','2026-01-25 16:43:31','admin',NULL,NULL,147,127);
/*!40000 ALTER TABLE `stock_movements` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_sessions`
--

DROP TABLE IF EXISTS `user_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_sessions` (
  `session_id` varchar(255) NOT NULL,
  `user_id` varchar(50) NOT NULL,
  `login_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `last_activity` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ip_address` varchar(45) DEFAULT NULL,
  `device_info` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `logout_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`session_id`),
  KEY `idx_session_user` (`user_id`),
  KEY `idx_session_active` (`is_active`),
  KEY `idx_session_last_activity` (`last_activity`),
  CONSTRAINT `user_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_sessions`
--

LOCK TABLES `user_sessions` WRITE;
/*!40000 ALTER TABLE `user_sessions` DISABLE KEYS */;
INSERT INTO `user_sessions` VALUES ('0684e8da-578b-4762-b21a-4b2001935eee','admin','2026-01-25 20:19:20','2026-01-25 20:19:20','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('0795c2b5-a57d-422a-82b2-2bf2908e69f9','psthirimanna','2026-01-24 08:54:22','2026-01-24 08:54:59','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('0d57aed4-ac18-4d23-9fc5-e3e5a19574b3','psthirimanna','2026-01-25 09:24:18','2026-01-25 09:24:18','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('12a80958-b340-48c1-984f-fbda666f4600','psthirimanna','2026-01-27 15:29:55','2026-01-27 15:29:55','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('14693374-9057-4545-9a54-404cf81b2d58','psthirimanna','2026-01-27 14:50:27','2026-01-27 14:50:27','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('158daa6c-06d0-4c9a-8f22-2ab09be6b02d','poojana','2026-01-20 19:42:10','2026-01-20 19:42:10','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('163d3d41-a107-4cd2-ab26-6ad1f064922f','psthirimanna','2026-01-20 19:13:18','2026-01-20 19:13:22','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-20 19:13:22'),('196ab76c-15bf-4da6-b4ac-ec15121f4c01','psthirimanna','2026-01-20 16:48:55','2026-01-20 16:48:57','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-20 16:48:57'),('1b33f734-0327-402a-a650-ec1e81297384','psthirimanna','2026-01-24 14:27:33','2026-01-24 14:27:33','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('1d63501e-c36d-409a-8030-37b102d7a6fb','psthirimanna','2026-01-25 20:11:26','2026-01-25 20:34:02','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('1d895261-7d8f-434e-89b4-fff795ab55af','admin','2026-01-19 07:53:15','2026-01-19 07:53:15','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('2437fd4f-6e5f-42b5-ad15-5defb98d8a14','admin','2026-01-18 19:12:51','2026-01-18 19:13:04','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36',0,'2026-01-18 19:13:04'),('264c4ae6-d47c-49fe-974e-373f0d766864','psthirimanna','2026-01-18 18:56:17','2026-01-18 18:57:56','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36',0,'2026-01-18 18:57:56'),('2b142f58-0c1a-40dd-8707-02dcf8582548','psthirimanna','2026-01-20 13:11:39','2026-01-20 13:11:39','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('33cbd80a-0dbe-47e3-b2e4-42d35f73a8ef','admin','2026-01-25 09:29:59','2026-01-25 09:29:59','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('41bbd917-7118-4a7b-9161-dfdb6c720dab','psthirimanna','2026-01-25 05:05:53','2026-01-25 05:05:53','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('44ec6fa8-ac82-442a-9b54-3dcdcf3d7a99','psthirimanna','2026-01-25 21:09:14','2026-01-25 21:09:14','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('4c01fecb-b422-4fe5-9fe1-8a8db1d5c620','admin','2026-01-25 05:46:28','2026-01-25 05:46:28','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('4e4672f7-0165-4520-9513-eb8ac9cf7cc0','psthirimanna','2026-01-24 15:38:28','2026-01-24 15:38:28','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('4ef20a73-d583-4b77-ac3a-8c39cb08c06b','psthirimanna','2026-01-24 01:22:16','2026-01-24 01:22:16','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('5a81b0de-d507-454d-aaf9-5926a95f4bd4','admin','2026-01-24 00:42:58','2026-01-24 00:42:58','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('5bba789f-9391-4042-9c3b-f1b79ff5cc25','psthirimanna','2026-01-24 22:49:43','2026-01-24 22:49:43','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('5ca602e7-4137-4294-aa45-97f654e1b3b8','psthirimanna','2026-01-24 08:08:27','2026-01-24 08:08:27','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('6245e9be-12e3-4cec-a951-401f4878103f','admin','2026-01-24 00:55:07','2026-01-24 00:55:07','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('640652d4-a296-470f-b9ac-2fd5b845980e','psthirimanna','2026-01-24 16:30:02','2026-01-24 16:33:37','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('6b7faf9f-d372-4a36-9dcb-319baa4b73bd','admin','2026-01-20 19:25:26','2026-01-20 19:25:31','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-20 19:25:31'),('74bf49cd-09aa-4df6-8b0d-194fb6876ea3','psthirimanna','2026-01-24 23:19:01','2026-01-24 23:24:22','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-24 23:24:22'),('75d9932a-057d-48d3-9bba-e88375592298','psthirimanna','2026-01-25 11:57:42','2026-01-25 12:01:39','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('768df554-5bbe-415c-9670-e84a17674a0b','psthirimanna','2026-01-20 19:25:11','2026-01-20 19:25:19','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-20 19:25:19'),('773c1f66-eb7d-4d0d-811b-aefe58784285','poojana','2026-01-20 20:14:13','2026-01-20 20:14:13','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('7f28a316-9763-4e00-baa0-548193c69b23','admin','2026-01-24 00:28:25','2026-01-24 00:28:25','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('83f2c4ef-ca4d-4bd3-84dd-593936378766','psthirimanna','2026-01-24 23:28:28','2026-01-24 23:28:28','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('8da45cb2-4acf-45e7-bb65-cddf5e0da5f2','psthirimanna','2026-01-24 22:58:02','2026-01-24 23:03:41','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-24 23:03:41'),('94b35d2d-fc26-44e9-913b-e433ad5d1d03','admin','2026-01-24 22:36:30','2026-01-24 22:36:30','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('95a1132f-637d-4b06-8c94-d31eaab5fcc5','admin','2026-01-24 00:18:55','2026-01-24 00:18:55','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('96092351-d371-4339-8a54-57fb35935340','admin','2026-01-25 11:13:25','2026-01-25 11:13:25','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('979b7583-a516-4ed3-8a4b-8df674a61b8e','poojana','2026-01-20 19:32:09','2026-01-20 19:32:09','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('9a396598-b6da-45bf-9947-7a73f7d87c36','poojana','2026-01-20 19:29:52','2026-01-20 19:29:55','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-20 19:29:55'),('a2804c0a-9816-45f3-af80-609341c4ae3f','admin','2026-01-24 00:14:46','2026-01-24 00:14:50','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-24 00:14:50'),('abf61752-707b-4777-9ac8-b483d9036ad3','psthirimanna','2026-01-20 20:17:36','2026-01-20 20:17:36','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('b82633b5-4c60-477b-8fb9-db62b6c356ca','psthirimanna','2026-01-24 08:55:07','2026-01-24 09:01:44','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('ba1ce2da-1350-4b85-9e7d-07c61ab92381','psthirimanna','2026-01-24 01:24:13','2026-01-24 01:44:30','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('bb6f4edc-47e2-4d52-b749-2a3af8b045a7','psthirimanna','2026-01-24 00:09:02','2026-01-24 00:09:10','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-24 00:09:10'),('d5e05b30-e30b-4a38-81bd-9d23087c06b3','psthirimanna','2026-01-25 09:12:13','2026-01-25 09:12:13','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('d61d41c1-19e7-42a1-8b93-e2228df2f390','admin','2026-01-25 06:52:32','2026-01-25 06:52:32','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('d65ec7cf-4f1a-4081-889d-7364da326f43','psthirimanna','2026-01-27 16:27:21','2026-01-27 16:27:21','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('d7ab1256-fc8e-4988-a085-bb78426cdcac','admin','2026-01-18 19:13:21','2026-01-18 19:14:10','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/143.0.0.0 Safari/537.36',0,'2026-01-18 19:14:10'),('d8daf96d-afd7-4e4b-834d-75a5bfce88be','admin','2026-01-24 22:27:20','2026-01-24 22:27:20','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('e0897659-7415-44e3-a092-8c3361863b5f','psthirimanna','2026-01-25 19:13:49','2026-01-25 19:13:49','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('e433b981-da95-40eb-8629-d18993179f40','psthirimanna','2026-01-24 00:15:44','2026-01-24 00:15:44','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL),('e8ad605a-1273-457a-8208-e261b0a11f2c','psthirimanna','2026-01-24 07:59:42','2026-01-24 08:01:42','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',0,'2026-01-24 08:01:42'),('ea2ef399-270e-49df-9846-624684455f4e','admin','2026-01-25 09:28:01','2026-01-25 09:28:01','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('fa58f7dc-89ab-4285-9c22-86cc592b6326','admin','2026-01-25 05:48:59','2026-01-25 05:48:59','0:0:0:0:0:0:0:1','PostmanRuntime/7.51.0',1,NULL),('feba4a7f-94f3-4bca-809d-9f03bdb810f6','poojana','2026-01-20 19:30:20','2026-01-20 19:30:20','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/144.0.0.0 Safari/537.36',1,NULL);
/*!40000 ALTER TABLE `user_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` varchar(50) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `role_id` int NOT NULL,
  `email` varchar(255) NOT NULL,
  `contact_number` varchar(50) DEFAULT NULL,
  `google_id` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `google_id` (`google_id`),
  KEY `idx_role` (`role_id`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('admin','admin','$2a$10$Z4S0tM3b3zJCGAnA41zU0eN/RDgaGJcIdd/nbqePRTFJ.1RB/bI3G',3,'psthirimanna123@gmail.com','0772333473',NULL,1,'2026-01-17 22:29:49'),('cashier001','John Doe','$2a$10$e7KJW4xnf9Pvi2mL5fJfQu0EUSZ81DZxTMrcOd7XOQ0SJfNT9RZ/2',2,'john.doe@example.com','0771234567',NULL,0,'2026-01-25 20:19:27'),('cashier01','Cashier One','$2a$10$7s9rQm5XkqZ9GvXkY0eR2uP9l4rM0m8Qh8Zc5XK9mZqzK0v0QK3eG',2,'cashier@syos.com','0710000002',NULL,1,'2026-01-17 22:36:00'),('manager01','Main Manager','12345678aA',1,'manager@syos.com','0710000001',NULL,1,'2026-01-17 22:35:41'),('poojana','Poojana Thirimanna','12345678aA',1,'psthirimanna1234@gmail.com','0772333473',NULL,1,'2026-01-18 13:55:25'),('psthirimanna','Poojana Thirimanna',NULL,1,'psthirimanna@gmail.com',NULL,'107830311739488573837',1,'2026-01-17 20:59:38');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'syos_billing_web'
--
/*!50003 DROP FUNCTION IF EXISTS `get_next_bill_number` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` FUNCTION `get_next_bill_number`(day_id BIGINT) RETURNS varchar(50) CHARSET utf8mb4
    DETERMINISTIC
BEGIN
    DECLARE seq_num INT;
    
    -- Insert and get auto-increment value atomically
    INSERT INTO bill_sequences (business_day_id) VALUES (day_id);
    SET seq_num = LAST_INSERT_ID();
    
    -- Format: BILL-20260117-0001
    RETURN CONCAT('BILL-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(seq_num, 4, '0'));
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `deduct_stock_for_sale` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `deduct_stock_for_sale`(
    IN p_product_code VARCHAR(50),
    IN p_quantity INT,
    IN p_bill_number VARCHAR(50),
    IN p_user_id VARCHAR(50),
    OUT o_batch_id INT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_batch_id INT;
    DECLARE v_available_qty INT;
    DECLARE v_deduct_qty INT;
    DECLARE v_version INT;
    DECLARE remaining_qty INT DEFAULT p_quantity;
    
    DECLARE batch_cursor CURSOR FOR
        SELECT il.batch_id, il.quantity, il.version
        FROM inventory_locations il
        JOIN stock_batches sb ON il.batch_id = sb.batch_id
        WHERE il.product_code = p_product_code
          AND il.location = 'SHELF'
          AND il.quantity > 0
        ORDER BY sb.expiry_date ASC, sb.purchase_date ASC
        FOR UPDATE;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    START TRANSACTION;
    
    OPEN batch_cursor;
    
    sale_loop: LOOP
        FETCH batch_cursor INTO v_batch_id, v_available_qty, v_version;
        
        IF done OR remaining_qty <= 0 THEN
            LEAVE sale_loop;
        END IF;
        
        SET v_deduct_qty = LEAST(v_available_qty, remaining_qty);
        
        -- Update with optimistic locking
        UPDATE inventory_locations
        SET quantity = quantity - v_deduct_qty,
            version = version + 1
        WHERE batch_id = v_batch_id 
          AND location = 'SHELF'
          AND version = v_version;
        
        -- Check if update succeeded
        IF ROW_COUNT() = 0 THEN
            ROLLBACK;
            SIGNAL SQLSTATE '45000' 
            SET MESSAGE_TEXT = 'Concurrent modification detected';
        END IF;
        
        -- Log the sale
        INSERT INTO stock_movements (
            product_code, batch_id, quantity, from_location, to_location,
            movement_type, user_id, bill_number, previous_quantity, new_quantity
        ) VALUES (
            p_product_code, v_batch_id, v_deduct_qty, 'SHELF', 'CUSTOMER',
            'SALE', p_user_id, p_bill_number, v_available_qty, v_available_qty - v_deduct_qty
        );
        
        SET remaining_qty = remaining_qty - v_deduct_qty;
        SET o_batch_id = v_batch_id;
    END LOOP;
    
    CLOSE batch_cursor;
    
    IF remaining_qty > 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Insufficient stock for sale';
    ELSE
        COMMIT;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_fefo_inventory` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_fefo_inventory`(
    IN p_product_code VARCHAR(50),
    IN p_location ENUM('MAIN', 'SHELF', 'WEBSITE')
)
BEGIN
    SELECT 
        il.id,
        il.product_code,
        il.batch_id,
        il.quantity,
        il.location,
        sb.expiry_date,
        sb.discount_percentage,
        p.name as product_name,
        p.unit_price
    FROM inventory_locations il
    JOIN stock_batches sb ON il.batch_id = sb.batch_id
    JOIN products p ON il.product_code = p.product_code
    WHERE il.product_code = p_product_code
      AND il.location = p_location
      AND il.quantity > 0
    ORDER BY sb.expiry_date ASC, sb.purchase_date ASC;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `transfer_stock_fefo` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `transfer_stock_fefo`(
    IN p_product_code VARCHAR(50),
    IN p_from_location ENUM('MAIN', 'SHELF', 'WEBSITE'),
    IN p_to_location ENUM('MAIN', 'SHELF', 'WEBSITE'),
    IN p_quantity INT,
    IN p_user_id VARCHAR(50)
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_batch_id INT;
    DECLARE v_available_qty INT;
    DECLARE v_transfer_qty INT;
    DECLARE remaining_qty INT DEFAULT p_quantity;
    
    DECLARE batch_cursor CURSOR FOR
        SELECT il.batch_id, il.quantity
        FROM inventory_locations il
        JOIN stock_batches sb ON il.batch_id = sb.batch_id
        WHERE il.product_code = p_product_code
          AND il.location = p_from_location
          AND il.quantity > 0
        ORDER BY sb.expiry_date ASC, sb.purchase_date ASC
        FOR UPDATE;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    START TRANSACTION;
    
    OPEN batch_cursor;
    
    transfer_loop: LOOP
        FETCH batch_cursor INTO v_batch_id, v_available_qty;
        
        IF done OR remaining_qty <= 0 THEN
            LEAVE transfer_loop;
        END IF;
        
        -- Calculate how much to transfer from this batch
        SET v_transfer_qty = LEAST(v_available_qty, remaining_qty);
        
        -- Deduct from source location
        UPDATE inventory_locations
        SET quantity = quantity - v_transfer_qty,
            version = version + 1
        WHERE batch_id = v_batch_id 
          AND location = p_from_location;
        
        -- Add to destination location
        INSERT INTO inventory_locations (product_code, batch_id, location, quantity)
        VALUES (p_product_code, v_batch_id, p_to_location, v_transfer_qty)
        ON DUPLICATE KEY UPDATE 
            quantity = quantity + v_transfer_qty,
            version = version + 1;
        
        -- Log the movement
        INSERT INTO stock_movements (
            product_code, batch_id, quantity, from_location, to_location,
            movement_type, user_id, previous_quantity, new_quantity
        ) VALUES (
            p_product_code, v_batch_id, v_transfer_qty, p_from_location, p_to_location,
            'TRANSFER', p_user_id, v_available_qty, v_available_qty - v_transfer_qty
        );
        
        SET remaining_qty = remaining_qty - v_transfer_qty;
    END LOOP;
    
    CLOSE batch_cursor;
    
    IF remaining_qty > 0 THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Insufficient stock for transfer';
    ELSE
        COMMIT;
    END IF;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-27 23:32:54
