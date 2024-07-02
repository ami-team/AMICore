------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS "router_ipv6_blocks";;
DROP TABLE IF EXISTS "router_ipv4_blocks";;
DROP TABLE IF EXISTS "router_locations";;
DROP TABLE IF EXISTS "router_authority";;
DROP TABLE IF EXISTS `router_markdown`;;
DROP TABLE IF EXISTS "router_short_url";;
DROP TABLE IF EXISTS "router_search_interface";;
DROP TABLE IF EXISTS "router_dashboard_controls";;
DROP TABLE IF EXISTS "router_dashboard";;
DROP TABLE IF EXISTS "router_user_role";;
DROP TABLE IF EXISTS "router_user";;
DROP TABLE IF EXISTS "router_command_role";;
DROP TABLE IF EXISTS "router_command";;
DROP TABLE IF EXISTS "router_role";;
DROP TABLE IF EXISTS "router_foreign_key";;
DROP TABLE IF EXISTS "router_field";;
DROP TABLE IF EXISTS "router_entity";;
DROP TABLE IF EXISTS "router_catalog";;
DROP TABLE IF EXISTS `router_monitoring`;;
DROP TABLE IF EXISTS "router_converter";;
DROP TABLE IF EXISTS "router_config";;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_config" (
  "id" INTEGER PRIMARY KEY,
  "paramName" VARCHAR(128) NOT NULL,
  "paramValue" VARCHAR(512),
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("paramName")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_converter" (
  "id" INTEGER PRIMARY KEY,
  "xslt" VARCHAR(128) NOT NULL,
  "mime" VARCHAR(128) NOT NULL,
  UNIQUE ("xslt")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_monitoring` (
  "id" INTEGER PRIMARY KEY,
  "node" VARCHAR(128) NOT NULL,
  "endpoint" VARCHAR(512) NOT NULL,
  "service" VARCHAR(128) NOT NULL,
  "frequency" INT DEFAULT 10,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE ("node")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_catalog" (
  "id" INTEGER PRIMARY KEY,
  "externalCatalog" VARCHAR(128) NOT NULL,
  "internalCatalog" VARCHAR(128) NOT NULL,
  "internalSchema" VARCHAR(128),
  "jdbcUrl" VARCHAR(2048) NOT NULL,
  "user" VARCHAR(128) NOT NULL,
  "pass" VARCHAR(128) NOT NULL,
  "json" TEXT,
  "description" VARCHAR(512) DEFAULT 'N/A',
  "archived" integer NOT NULL DEFAULT 0,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("externalCatalog")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_entity" (
  "id" INTEGER PRIMARY KEY,
  "catalog" VARCHAR(128) NOT NULL,
  "entity" VARCHAR(128) NOT NULL,
  "rank" integer,
  "json" TEXT,
  "description" VARCHAR(512) DEFAULT 'N/A',
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("catalog", "entity")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_field" (
  "id" INTEGER PRIMARY KEY,
  "catalog" VARCHAR(128) NOT NULL,
  "entity" VARCHAR(128) NOT NULL,
  "field" VARCHAR(128) NOT NULL,
  "rank" integer,
  "json" TEXT,
  "description" VARCHAR(512) DEFAULT 'N/A',
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("catalog", "entity", "field")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_foreign_key" (
  "id" INTEGER PRIMARY KEY,
  "name" VARCHAR(128) NOT NULL,
  "fkCatalog" VARCHAR(128) NOT NULL,
  "fkTable" VARCHAR(128) NOT NULL,
  "fkColumn" VARCHAR(128) NOT NULL,
  "pkCatalog" VARCHAR(128) NOT NULL,
  "pkTable" VARCHAR(128) NOT NULL,
  "pkColumn" VARCHAR(128) NOT NULL,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("name"),
  UNIQUE ("fkCatalog", "fkTable", "fkColumn", "pkCatalog", "pkTable", "pkColumn")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_role" (
  "id" INTEGER PRIMARY KEY,
  "role" VARCHAR(128) NOT NULL,
  "description" VARCHAR(512),
  UNIQUE ("role")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_command" (
  "id" INTEGER PRIMARY KEY,
  "command" VARCHAR(128) NOT NULL,
  "class" VARCHAR(256) NOT NULL,
  "visible" integer NOT NULL DEFAULT 1,
  "roleValidatorClass" VARCHAR(256),
  UNIQUE ("command")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_command_role" (
  "id" INTEGER PRIMARY KEY,
  "commandFK" integer NOT NULL,
  "roleFK" integer NOT NULL,
  UNIQUE ("commandFK", "roleFK"),
  FOREIGN KEY ("commandFK") REFERENCES "router_command" ("id") ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY ("roleFK") REFERENCES "router_role" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_user" (
  "id" INTEGER PRIMARY KEY,
  "AMIUser" VARCHAR(128) NOT NULL,
  "ssoUser" VARCHAR(128),
  "AMIPass" VARCHAR(128) NOT NULL,
  "clientDN" VARCHAR(512),
  "issuerDN" VARCHAR(512),
  "firstName" VARCHAR(128) NOT NULL,
  "lastName" VARCHAR(128) NOT NULL,
  "email" VARCHAR(128) NOT NULL,
  "country" VARCHAR(128) DEFAULT 'N/A',
  "json" TEXT,
  "valid" integer NOT NULL DEFAULT 1,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE ("AMIUser")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_user_role" (
  "id" INTEGER PRIMARY KEY,
  "userFK" integer NOT NULL,
  "roleFK" integer NOT NULL,
  UNIQUE ("userFK", "roleFK"),
  FOREIGN KEY ("userFK") REFERENCES "router_user" ("id") ON DELETE CASCADE ON UPDATE NO ACTION,
  FOREIGN KEY ("roleFK") REFERENCES "router_role" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_dashboard` (
  "id" INT NOT NULL,
  "name" VARCHAR(128) NOT NULL,
  "rank" integer NOT NULL DEFAULT 0,
  "shared" integer NOT NULL DEFAULT 0,
  "owner" VARCHAR(128) NOT NULL,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_dashboard_controls" (
  "id" INTEGER PRIMARY KEY,
  "dashboardFK" INT NOT NULL,
  "control" VARCHAR(128) NOT NULL,
  "params" TEXT NOT NULL,
  "settings" TEXT NOT NULL,
  "transparent" integer NOT NULL DEFAULT 0,
  "autoRefresh" integer NOT NULL DEFAULT 1,
  "x" integer NOT NULL DEFAULT 0,
  "y" integer NOT NULL DEFAULT 0,
  "width" integer NOT NULL DEFAULT 0,
  "height" integer NOT NULL DEFAULT 0,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY ("dashboardFK") REFERENCES "router_dashboard" ("id") ON DELETE CASCADE ON UPDATE NO ACTION,
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_search_interface" (
  "id" INTEGER PRIMARY KEY,
  "group" VARCHAR(128) NOT NULL,
  "name" VARCHAR(128) NOT NULL,
  "rank" integer NOT NULL DEFAULT 0,
  "json" TEXT NOT NULL,
  "archived" integer NOT NULL DEFAULT 0,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("group", "name")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_short_url" (
  "id" INTEGER PRIMARY KEY,
  "hash" VARCHAR(16) NOT NULL,
  "name" VARCHAR(128) NOT NULL,
  "rank" integer NOT NULL DEFAULT 0,
  "json" TEXT NOT NULL,
  "shared" integer NOT NULL DEFAULT 0,
  "expire" integer NOT NULL DEFAULT 0,
  "owner" VARCHAR(128) NOT NULL,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE ("hash")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_markdown` (
  "id" INTEGER PRIMARY KEY,
  "name" VARCHAR(128) NOT NULL,
  "title" VARCHAR(128) NOT NULL,
  "body" TEXT NOT NULL,
  "archived" TINYINT(1) NOT NULL DEFAULT 0,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("name")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_authority" (
  "id" INTEGER PRIMARY KEY,
  "vo" VARCHAR(128) NOT NULL DEFAULT 'ami',
  "clientDN" VARCHAR(512) NOT NULL,
  "issuerDN" VARCHAR(512) NOT NULL,
  "notBefore" DATE NOT NULL,
  "notAfter" DATE NOT NULL,
  "serial" VARCHAR(128) NOT NULL,
  "email" VARCHAR(128) NOT NULL,
  "reason" integer,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL,
  UNIQUE ("serial")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_locations" (
  "id" INTEGER PRIMARY KEY,
  "continentCode" VARCHAR(3) NOT NULL DEFAULT 'N/A',
  "countryCode" VARCHAR(3) NOT NULL DEFAULT 'N/A',
  UNIQUE ("continentCode", "countryCode")
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_ipv4_blocks" (
  "id" INTEGER PRIMARY KEY,
  "network" VARCHAR(32) NOT NULL,
  "rangeBegin" DECIMAL(10, 0) NOT NULL,
  "rangeEnd" DECIMAL(10, 0) NOT NULL,
  "geoFK" integer NOT NULL,
  UNIQUE ("network"),
  FOREIGN KEY ("geoFK") REFERENCES "router_locations" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_ipv6_blocks" (
  "id" INTEGER PRIMARY KEY,
  "network" VARCHAR(64) NOT NULL,
  "rangeBegin" DECIMAL(38, 0) NOT NULL,
  "rangeEnd" DECIMAL(38, 0) NOT NULL,
  "geoFK" integer NOT NULL,
  UNIQUE ("network"),
  FOREIGN KEY ("geoFK") REFERENCES "router_locations" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

------------------------------------------------------------------------------------------------------------------------
