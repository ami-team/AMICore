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

CREATE OR REPLACE FUNCTION UPDATE_MODIFIED_FIELD()
  RETURNS TRIGGER AS $BODY$
  BEGIN
    NEW."modified" = now();

    RETURN NEW;
  END;

  $BODY$ LANGUAGE 'plpgsql'
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_config" (
  "id" SERIAL,
  "paramName" VARCHAR(128) NOT NULL,
  "paramValue" VARCHAR(512),
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_config"
  ADD CONSTRAINT "pk1_router_config" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_config" UNIQUE ("paramName")
;;

CREATE TRIGGER "trig1_router_config"
  BEFORE UPDATE ON "router_config"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_converter" (
  "id" SERIAL,
  "xslt" VARCHAR(128) NOT NULL,
  "mime" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_converter"
  ADD CONSTRAINT "pk1_router_converter" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_converter" UNIQUE ("xslt")
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_monitoring` (
  "id" SERIAL,
  "node" VARCHAR(128) NOT NULL,
  "endpoint" VARCHAR(512) NOT NULL,
  "service" VARCHAR(128) NOT NULL,
  "frequency" INT DEFAULT 10,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);;

ALTER TABLE "router_monitoring"
  ADD CONSTRAINT "pk1_router_monitoring" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_monitoring" UNIQUE ("node")
;;

CREATE TRIGGER "trig1_router_monitoring"
  BEFORE UPDATE ON "router_monitoring"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_catalog" (
  "id" SERIAL,
  "externalCatalog" VARCHAR(128) NOT NULL,
  "internalCatalog" VARCHAR(128) NOT NULL,
  "internalSchema" VARCHAR(128),
  "jdbcUrl" VARCHAR(2048) NOT NULL,
  "user" VARCHAR(128) NOT NULL,
  "pass" VARCHAR(128) NOT NULL,
  "json" TEXT,
  "description" VARCHAR(512) DEFAULT 'N/A',
  "archived" SMALLINT NOT NULL DEFAULT 0,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "pk1_router_catalog" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_catalog" UNIQUE ("externalCatalog")
;;

CREATE TRIGGER "trig1_router_catalog"
  BEFORE UPDATE ON "router_catalog"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_entity" (
  "id" SERIAL,
  "catalog" VARCHAR(128) NOT NULL,
  "entity" VARCHAR(128) NOT NULL,
  "rank" INT,
  "json" TEXT,
  "description" VARCHAR(512) DEFAULT 'N/A',
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_entity"
  ADD CONSTRAINT "pk1_router_entity" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_entity" UNIQUE ("catalog", "entity")
;;

CREATE TRIGGER "trig1_router_entity"
  BEFORE UPDATE ON "router_entity"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_field" (
  "id" SERIAL,
  "catalog" VARCHAR(128) NOT NULL,
  "entity" VARCHAR(128) NOT NULL,
  "field" VARCHAR(128) NOT NULL,
  "rank" INT,
  "json" TEXT,
  "description" VARCHAR(512) DEFAULT 'N/A',
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_field"
  ADD CONSTRAINT "pk1_router_field" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_field" UNIQUE ("catalog", "entity", "field")
;;

CREATE TRIGGER "trig1_router_field"
  BEFORE UPDATE ON "router_field"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_foreign_key" (
  "id" SERIAL,
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
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_foreign_key"
  ADD CONSTRAINT "pk1_router_foreign_key" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_foreign_key" UNIQUE ("name"),
  ADD CONSTRAINT "uk2_router_foreign_key" UNIQUE ("fkCatalog", "fkTable", "fkColumn", "pkCatalog", "pkTable", "pkColumn")
;;

CREATE TRIGGER "trig1_router_foreign_key"
  BEFORE UPDATE ON "router_foreign_key"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_role" (
  "id" SERIAL,
  "role" VARCHAR(128) NOT NULL,
  "description" VARCHAR(512)
);;

ALTER TABLE "router_role"
  ADD CONSTRAINT "pk1_router_role" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_role" UNIQUE ("role")
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_command" (
  "id" SERIAL,
  "command" VARCHAR(128) NOT NULL,
  "class" VARCHAR(256) NOT NULL,
  "visible" SMALLINT NOT NULL DEFAULT 1,
  "roleValidatorClass" VARCHAR(256)
);;

ALTER TABLE "router_command"
  ADD CONSTRAINT "pk1_router_command" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_command" UNIQUE ("command")
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_command_role" (
  "id" SERIAL,
  "commandFK" INT NOT NULL,
  "roleFK" INT NOT NULL
);;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "pk1_router_command_role" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_command_role" UNIQUE  ("commandFK", "roleFK"),
  ADD CONSTRAINT "fk1_router_command_role" FOREIGN KEY ("commandFK") REFERENCES "router_command" ("id") ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT "fk2_router_command_role" FOREIGN KEY ("roleFK") REFERENCES "router_role" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_user" (
  "id" SERIAL,
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
  "valid" SMALLINT NOT NULL DEFAULT 1,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);;

ALTER TABLE "router_user"
  ADD CONSTRAINT "pk1_router_user" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_user" UNIQUE ("AMIUser")
;;

CREATE TRIGGER "trig1_router_user"
  BEFORE UPDATE ON "router_user"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_user_role" (
  "id" SERIAL,
  "userFK" INT NOT NULL,
  "roleFK" INT NOT NULL
);;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "pk1_router_user_role" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_user_role" UNIQUE ("userFK", "roleFK"),
  ADD CONSTRAINT "fk1_router_user_role" FOREIGN KEY ("userFK") REFERENCES "router_user" ("id") ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT "fk2_router_user_role" FOREIGN KEY ("roleFK") REFERENCES "router_role" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_dashboard` (
  "id" SERIAL,
  "name" VARCHAR(128) NOT NULL,
  "rank" INT NOT NULL DEFAULT 0,
  "json" TEXT NOT NULL,
  "shared" SMALLINT NOT NULL DEFAULT 0,
  "owner" VARCHAR(128) NOT NULL,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);;

ALTER TABLE "router_dashboard"
  ADD CONSTRAINT "pk1_router_dashboard" PRIMARY KEY ("id")
;;

CREATE TRIGGER "trig1_router_dashboard"
  BEFORE UPDATE ON "router_dashboard"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_search_interface" (
  "id" SERIAL,
  "group" VARCHAR(128) NOT NULL,
  "name" VARCHAR(128) NOT NULL,
  "rank" INT NOT NULL DEFAULT 0,
  "json" TEXT NOT NULL,
  "archived" SMALLINT NOT NULL DEFAULT 0,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "pk1_router_search_interface" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_search_interface" UNIQUE ("group", "name")
;;

CREATE TRIGGER "trig1_router_search_interface"
  BEFORE UPDATE ON "router_search_interface"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_short_url" (
  "id" SERIAL,
  "hash" VARCHAR(16) NOT NULL,
  "name" VARCHAR(128) NOT NULL,
  "rank" INT NOT NULL DEFAULT 0,
  "json" TEXT NOT NULL,
  "shared" SMALLINT NOT NULL DEFAULT 0,
  "expire" SMALLINT NOT NULL DEFAULT 0,
  "owner" VARCHAR(128) NOT NULL,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "pk1_router_short_url" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_short_url" UNIQUE ("hash")
;;

CREATE TRIGGER "trig1_router_short_url"
  BEFORE UPDATE ON "router_short_url"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE `router_markdown` (
  "id" SERIAL,
  "name" VARCHAR(128) NOT NULL,
  "title" VARCHAR(128) NOT NULL,
  "body" TEXT NOT NULL,
  "archived" TINYINT(1) NOT NULL DEFAULT 0,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_markdown"
  ADD CONSTRAINT "pk1_router_markdown" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_markdown" UNIQUE ("name")
;;

CREATE TRIGGER "trig1_router_markdown"
  BEFORE UPDATE ON "router_markdown"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_authority" (
  "id" SERIAL,
  "vo" VARCHAR(128) NOT NULL DEFAULT 'ami',
  "clientDN" VARCHAR(512) NOT NULL,
  "issuerDN" VARCHAR(512) NOT NULL,
  "notBefore" DATE NOT NULL,
  "notAfter" DATE NOT NULL,
  "serial" VARCHAR(128) NOT NULL,
  "email" VARCHAR(128) NOT NULL,
  "reason" SMALLINT,
  "created" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR(128) NOT NULL,
  "modified" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR(128) NOT NULL
);;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "pk1_router_authority" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_authority" UNIQUE ("serial")
;;

CREATE TRIGGER "trig1_router_authority"
  BEFORE UPDATE ON "router_authority"
  FOR EACH ROW
    EXECUTE PROCEDURE UPDATE_MODIFIED_FIELD()
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_locations" (
  "id" SERIAL,
  "continentCode" VARCHAR(3) NOT NULL DEFAULT 'N/A',
  "countryCode" VARCHAR(3) NOT NULL DEFAULT 'N/A'
);;

ALTER TABLE "router_locations"
  ADD CONSTRAINT "pk1_router_locations" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_locations" UNIQUE ("continentCode", "countryCode")
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_ipv4_blocks" (
  "id" SERIAL,
  "network" VARCHAR(32) NOT NULL,
  "rangeBegin" DECIMAL(10, 0) NOT NULL,
  "rangeEnd" DECIMAL(10, 0) NOT NULL,
  "geoFK" INT NOT NULL
);;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "pk1_router_ipv4_blocks" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_ipv4_blocks" UNIQUE ("network"),
  ADD CONSTRAINT "fk1_router_ipv4_blocks" FOREIGN KEY ("geoFK") REFERENCES "router_locations" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;;

------------------------------------------------------------------------------------------------------------------------

CREATE TABLE "router_ipv6_blocks" (
  "id" SERIAL,
  "network" VARCHAR(64) NOT NULL,
  "rangeBegin" DECIMAL(38, 0) NOT NULL,
  "rangeEnd" DECIMAL(38, 0) NOT NULL,
  "geoFK" INT NOT NULL
);;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "pk1_router_ipv6_blocks" PRIMARY KEY ("id"),
  ADD CONSTRAINT "uk1_router_ipv6_blocks" UNIQUE ("network"),
  ADD CONSTRAINT "fk1_router_ipv6_blocks" FOREIGN KEY ("geoFK") REFERENCES "router_locations" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION
;;

------------------------------------------------------------------------------------------------------------------------
