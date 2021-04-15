/*
 Navicat Premium Data Transfer

 Source Server         : RussianShop
 Source Server Type    : PostgreSQL
 Source Server Version : 120003
 Source Host           : localhost:5432
 Source Catalog        : RussianShop
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 120003
 File Encoding         : 65001

 Date: 14/12/2020 00:50:26
*/


-- ----------------------------
-- Sequence structure for carpetdetails_id_seq
-- ----------------------------
DROP SEQUENCE IF EXISTS "public"."carpetdetails_id_seq";
CREATE SEQUENCE "public"."carpetdetails_id_seq" 
INCREMENT 1
MINVALUE  1
MAXVALUE 2147483647
START 1
CACHE 1;

-- ----------------------------
-- Table structure for carpetdetails
-- ----------------------------
DROP TABLE IF EXISTS "public"."carpetdetails";
CREATE TABLE "public"."carpetdetails" (
  "id" int4 NOT NULL DEFAULT nextval('carpetdetails_id_seq'::regclass),
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "price" float4 NOT NULL,
  "brand" text COLLATE "pg_catalog"."default" NOT NULL,
  "size" text[] COLLATE "pg_catalog"."default" NOT NULL,
  "inventory" int4 NOT NULL,
  "deliverytime" date NOT NULL,
  "imagesrc" text COLLATE "pg_catalog"."default" NOT NULL,
  "imagessrc" text[] COLLATE "pg_catalog"."default" NOT NULL,
  "discountprice" float4,
  "attributes" text COLLATE "pg_catalog"."default",
  "lastvisited" timestamp(6),
  "counter" int4
)
;

-- ----------------------------
-- Records of carpetdetails
-- ----------------------------
INSERT INTO "public"."carpetdetails" VALUES (3, 'Esfehan Carpet', 478000, 'Tabriz', '{80*60,100*50,120*80}', 50, '3921-03-20', '1', '{1.1,1.2,1.3,1.4}', 325000, '{"Shane":1000,"Shape":"Squere","Test":"test","Color":"blue"}', '2020-12-06 01:16:24.241', 5);
INSERT INTO "public"."carpetdetails" VALUES (2, 'Tabriz Carpet', 958000, 'Tabriz', '{80*60,100*50,120*80}', 50, '3921-03-20', '1', '{1.1,1.2,1.3,1.4}', 325000, '{"Shane":1000,"Shape":"Squere","Test":"test","Color":"blue"}', '2020-12-06 01:16:18.266', 8);
INSERT INTO "public"."carpetdetails" VALUES (1, 'Iranian Carpet', 685000, 'Tabriz', '{80*60,100*50,120*80}', 50, '3921-03-20', '1', '{1.1,1.2,1.3,1.4}', 325000, '{"Shane":1000,"Shape":"Squere","Test":"test","Color":"blue"}', '2020-12-06 01:16:05.405', 2);
INSERT INTO "public"."carpetdetails" VALUES (5, 'Shirazian Carpet', 350000, 'Tabriz', '{80*60,100*50,120*80}', 50, '3921-03-20', '1', '{1.1,1.2,1.3,1.4}', 325000, '{"Shane":1000,"Shape":"Squere","Test":"test","Color":"blue"}', '2020-12-13 18:49:54.94', 24);
INSERT INTO "public"."carpetdetails" VALUES (4, 'Iranian Carpet', 630000, 'Tabriz', '{80*60,100*50,120*80}', 50, '3921-03-20', '1', '{1.1,1.2,1.3,1.4}', 325000, '{"Shane":1000,"Shape":"Squere","Test":"test","Color":"blue"}', '2020-12-13 20:43:32.57', 4);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS "public"."orders";
CREATE TABLE "public"."orders" (
  "id" int4 NOT NULL,
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "email" text COLLATE "pg_catalog"."default" NOT NULL,
  "contactnumber" int4 NOT NULL,
  "address" text COLLATE "pg_catalog"."default" NOT NULL
)
;

-- ----------------------------
-- Records of orders
-- ----------------------------

-- ----------------------------
-- Table structure for orderstour
-- ----------------------------
DROP TABLE IF EXISTS "public"."orderstour";
CREATE TABLE "public"."orderstour" (
  "id" int4 NOT NULL,
  "name" text COLLATE "pg_catalog"."default" NOT NULL,
  "email" text COLLATE "pg_catalog"."default" NOT NULL,
  "contactnumber" int4 NOT NULL,
  "tourdate" date NOT NULL,
  "tourtime" time(6) NOT NULL
)
;

-- ----------------------------
-- Records of orderstour
-- ----------------------------

-- ----------------------------
-- Alter sequences owned by
-- ----------------------------
ALTER SEQUENCE "public"."carpetdetails_id_seq"
OWNED BY "public"."carpetdetails"."id";
SELECT setval('"public"."carpetdetails_id_seq"', 6, true);

-- ----------------------------
-- Primary Key structure for table carpetdetails
-- ----------------------------
ALTER TABLE "public"."carpetdetails" ADD CONSTRAINT "carpetdetails_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table orders
-- ----------------------------
ALTER TABLE "public"."orders" ADD CONSTRAINT "orders_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table orderstour
-- ----------------------------
ALTER TABLE "public"."orderstour" ADD CONSTRAINT "orderstour_pkey" PRIMARY KEY ("id");
