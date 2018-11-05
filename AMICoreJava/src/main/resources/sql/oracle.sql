/*-------------------------------------------------------------------------*/

BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE "router_ipv6_blocks"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_ipv4_blocks"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_locations"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_search_interface"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_authority"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_short_url"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_user_role"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_user"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_command_role"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_command"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_role"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_converter"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_catalog"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP TABLE "router_config"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -942 THEN
    RAISE;
  END IF;
END;
;;

/*-------------------------------------------------------------------------*/

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_ipv6_blocks"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_ipv4_blocks"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_locations"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_search_interface"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_authority"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_short_url"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_user_role"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_user"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_command_role"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_command"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_role"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_converter"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_catalog"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

BEGIN
  EXECUTE IMMEDIATE 'DROP SEQUENCE "seq_router_config"';
EXCEPTION
  WHEN OTHERS THEN IF SQLCODE != -2289 THEN
    RAISE;
  END IF;
END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_config" (
  "id" NUMBER(*, 0),
  "paramName" VARCHAR2(128),
  "paramValue" VARCHAR2(512),
  "created" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR2(128) NOT NULL,
  "modified" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR2(128) NOT NULL
);;

ALTER TABLE "router_config"
  ADD CONSTRAINT "pk1_router_config" PRIMARY KEY ("id")
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "uk1_router_config" UNIQUE ("paramName")
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "ck1_router_config" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "ck2_router_config" CHECK("paramName" IS NOT NULL)
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "ck3_router_config" CHECK("paramValue" IS NOT NULL)
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "ck4_router_config" CHECK("created" IS NOT NULL)
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "ck5_router_config" CHECK("createdBy" IS NOT NULL)
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "ck6_router_config" CHECK("modified" IS NOT NULL)
;;

ALTER TABLE "router_config"
  ADD CONSTRAINT "ck7_router_config" CHECK("modifiedBy" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_config"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_config"
  BEFORE INSERT ON "router_config"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_config".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

CREATE TRIGGER "trig2_router_config"
  BEFORE UPDATE ON "router_config"
  FOR EACH ROW
  BEGIN
    :NEW."modified" := SYSDATE;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_catalog" (
  "id" NUMBER(*, 0),
  "externalCatalog" VARCHAR2(128),
  "internalCatalog" VARCHAR2(128),
  "internalSchema" VARCHAR2(128),
  "jdbcUrl" VARCHAR2(512),
  "user" VARCHAR2(128),
  "pass" VARCHAR2(128),
  "custom" CLOB,
  "archived" NUMBER(1, 0) DEFAULT '0',
  "created" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR2(128) NOT NULL,
  "modified" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR2(128) NOT NULL
);;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "pk1_router_catalog" PRIMARY KEY ("id")
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "uk1_router_catalog" UNIQUE ("externalCatalog")
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck1_router_catalog" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck2_router_catalog" CHECK("externalCatalog" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck3_router_catalog" CHECK("internalCatalog" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck4_router_catalog" CHECK("jdbcUrl" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck5_router_catalog" CHECK("user" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck6_router_catalog" CHECK("pass" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck7_router_catalog" CHECK("archived" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck8_router_catalog" CHECK("created" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck9_router_catalog" CHECK("createdBy" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck10_router_catalog" CHECK("modified" IS NOT NULL)
;;

ALTER TABLE "router_catalog"
  ADD CONSTRAINT "ck11_router_catalog" CHECK("modifiedBy" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_catalog"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_catalog"
  BEFORE INSERT ON "router_catalog"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_catalog".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

CREATE TRIGGER "trig2_router_catalog"
  BEFORE UPDATE ON "router_catalog"
  FOR EACH ROW
  BEGIN
    :NEW."modified" := SYSDATE;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_converter" (
  "id" NUMBER(*, 0),
  "xslt" VARCHAR2(128),
  "mime" VARCHAR2(128)
);;

ALTER TABLE "router_converter"
  ADD CONSTRAINT "pk1_router_converter" PRIMARY KEY ("id")
;;

ALTER TABLE "router_converter"
  ADD CONSTRAINT "uk1_router_converter" UNIQUE ("xslt")
;;

ALTER TABLE "router_converter"
  ADD CONSTRAINT "ck1_router_converter" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_converter"
  ADD CONSTRAINT "ck2_router_converter" CHECK("xslt" IS NOT NULL)
;;

ALTER TABLE "router_converter"
  ADD CONSTRAINT "ck3_router_converter" CHECK("mime" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_converter"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_converter"
  BEFORE INSERT ON "router_converter"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_converter".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_role" (
  "id" NUMBER(*, 0),
  "role" VARCHAR2(128),
  "description" VARCHAR2(512)
);;

ALTER TABLE "router_role"
  ADD CONSTRAINT "pk1_router_role" PRIMARY KEY ("id")
;;

ALTER TABLE "router_role"
  ADD CONSTRAINT "uk1_router_role" UNIQUE ("role")
;;

ALTER TABLE "router_role"
  ADD CONSTRAINT "ck1_router_role" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_role"
  ADD CONSTRAINT "ck2_router_role" CHECK("role" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_role"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_role"
  BEFORE INSERT ON "router_role"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_role".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_command" (
  "id" NUMBER(*, 0),
  "command" VARCHAR2(128),
  "class" VARCHAR2(256),
  "visible" NUMBER(1, 0) DEFAULT '1',
  "secured" NUMBER(1, 0) DEFAULT '0',
  "roleValidatorClass" VARCHAR2(256)
);;

ALTER TABLE "router_command"
  ADD CONSTRAINT "pk1_router_command" PRIMARY KEY ("id")
;;

ALTER TABLE "router_command"
  ADD CONSTRAINT "uk1_router_command" UNIQUE ("command")
;;

ALTER TABLE "router_command"
  ADD CONSTRAINT "ck1_router_command" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_command"
  ADD CONSTRAINT "ck2_router_command" CHECK("command" IS NOT NULL)
;;

ALTER TABLE "router_command"
  ADD CONSTRAINT "ck3_router_command" CHECK("class" IS NOT NULL)
;;

ALTER TABLE "router_command"
  ADD CONSTRAINT "ck4_router_command" CHECK("visible" IS NOT NULL)
;;

ALTER TABLE "router_command"
  ADD CONSTRAINT "ck5_router_command" CHECK("secured" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_command"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_command"
  BEFORE INSERT ON "router_command"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_command".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_command_role" (
  "id" NUMBER(*, 0),
  "commandFK" NUMBER(*, 0),
  "roleFK" NUMBER(*, 0)
);;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "pk1_router_command_role" PRIMARY KEY ("id")
;;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "uk1_router_command_role" UNIQUE ("commandFK", "roleFK")
;;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "ck1_router_command_role" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "ck2_router_command_role" CHECK("commandFK" IS NOT NULL)
;;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "ck3_router_command_role" CHECK("roleFK" IS NOT NULL)
;;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "fk1_router_command_role" FOREIGN KEY ("commandFK") REFERENCES "router_command" ("id") ON DELETE CASCADE
;;

ALTER TABLE "router_command_role"
  ADD CONSTRAINT "fk2_router_command_role" FOREIGN KEY ("roleFK") REFERENCES "router_role" ("id") ON DELETE CASCADE
;;

CREATE SEQUENCE "seq_router_command_role"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_command_role"
  BEFORE INSERT ON "router_command_role"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_command_role".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_user" (
  "id" NUMBER(*, 0),
  "AMIUser" VARCHAR2(128),
  "AMIPass" VARCHAR2(128),
  "clientDN" VARCHAR2(512),
  "issuerDN" VARCHAR2(512),
  "firstName" VARCHAR2(128),
  "lastName" VARCHAR2(128),
  "email" VARCHAR2(128),
  "country" VARCHAR2(128) DEFAULT 'N;;A',
  "valid" NUMBER(1, 0) DEFAULT '1',
  "created" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "modified" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);;

ALTER TABLE "router_user"
  ADD CONSTRAINT "pk1_router_user" PRIMARY KEY ("id")
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "uk1_router_user" UNIQUE ("AMIUser")
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck1_router_user" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck2_router_user" CHECK("AMIUser" IS NOT NULL)
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck3_router_user" CHECK("AMIPass" IS NOT NULL)
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck4_router_user" CHECK("firstName" IS NOT NULL)
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck5_router_user" CHECK("lastName" IS NOT NULL)
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck6_router_user" CHECK("valid" IS NOT NULL)
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck7_router_user" CHECK("created" IS NOT NULL)
;;

ALTER TABLE "router_user"
  ADD CONSTRAINT "ck8_router_user" CHECK("modified" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_user"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_user"
  BEFORE INSERT ON "router_user"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_user".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

CREATE TRIGGER "trig2_router_user"
  BEFORE UPDATE ON "router_user"
  FOR EACH ROW
  BEGIN
    :NEW."modified" := SYSDATE;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_user_role" (
  "id" NUMBER(*, 0),
  "userFK" NUMBER(*, 0),
  "roleFK" NUMBER(*, 0)
);;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "pk1_router_user_role" PRIMARY KEY ("id")
;;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "uk1_router_user_role" UNIQUE ("userFK", "roleFK")
;;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "ck1_router_user_role" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "ck2_router_user_role" CHECK("userFK" IS NOT NULL)
;;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "ck3_router_user_role" CHECK("roleFK" IS NOT NULL)
;;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "fk1_router_user_role" FOREIGN KEY ("userFK") REFERENCES "router_user" ("id") ON DELETE CASCADE
;;

ALTER TABLE "router_user_role"
  ADD CONSTRAINT "fk2_router_user_role" FOREIGN KEY ("roleFK") REFERENCES "router_role" ("id") ON DELETE CASCADE
;;

CREATE SEQUENCE "seq_router_user_role"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_user_role"
  BEFORE INSERT ON "router_user_role"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_user_role".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_short_url" (
  "id" NUMBER(*, 0),
  "hash" VARCHAR2(16),
  "name" VARCHAR2(64),
  "rank" NUMBER(*, 0) DEFAULT '0',
  "json" CLOB,
  "shared" NUMBER(1, 0) DEFAULT '0',
  "expire" NUMBER(1, 0) DEFAULT '0',
  "created" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR2(128) NOT NULL,
  "modified" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR2(128) NOT NULL
 );;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "pk1_router_short_url" PRIMARY KEY ("id")
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "uk1_router_short_url" UNIQUE ("hash")
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck1_router_short_url" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck2_router_short_url" CHECK("hash" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck3_router_short_url" CHECK("name" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck4_router_short_url" CHECK("rank" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck5_router_short_url" CHECK("json" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck6_router_short_url" CHECK("shared" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck7_router_short_url" CHECK("expire" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck8_router_short_url" CHECK("created" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck9_router_short_url" CHECK("createdBy" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck10_router_short_url" CHECK("modified" IS NOT NULL)
;;

ALTER TABLE "router_short_url"
  ADD CONSTRAINT "ck11_router_short_url" CHECK("modifiedBy" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_short_url"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_short_url"
  BEFORE INSERT ON "router_short_url"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_short_url".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

CREATE TRIGGER "trig2_router_short_url"
  BEFORE UPDATE ON "router_short_url"
  FOR EACH ROW
  BEGIN
    :NEW."modified" := SYSDATE;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_authority" (
  "id" NUMBER(*, 0),
  "vo" VARCHAR2(128) DEFAULT 'ami',
  "clientDN" VARCHAR2(512),
  "issuerDN" VARCHAR2(512),
  "not  BEFORE" DATE,
  "notAfter" DATE,
  "serial" VARCHAR2(128),
  "email" VARCHAR2(128),
  "reason" NUMBER(5, 0),
  "created" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR2(128) NOT NULL,
  "modified" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR2(128) NOT NULL
);;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "pk1_router_authority" PRIMARY KEY ("id")
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "uk1_router_authority" UNIQUE ("serial")
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck1_router_authority" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck2_router_authority" CHECK("vo" IS NOT NULL)
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck3_router_authority" CHECK("clientDN" IS NOT NULL)
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck4_router_authority" CHECK("issuerDN" IS NOT NULL)
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck5_router_authority" CHECK("not  BEFORE" IS NOT NULL)
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck6_router_authority" CHECK("notAfter" IS NOT NULL)
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck7_router_authority" CHECK("serial" IS NOT NULL)
;;

ALTER TABLE "router_authority"
  ADD CONSTRAINT "ck8_router_authority" CHECK("email" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_authority"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_authority"
  BEFORE INSERT ON "router_authority"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_authority".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

CREATE TRIGGER "trig2_router_authority"
  BEFORE UPDATE ON "router_authority"
  FOR EACH ROW
  BEGIN
    :NEW."modified" := SYSDATE;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_search_interface" (
  "id" NUMBER(*, 0),
  "interface" VARCHAR2(128),
  "json" CLOB,
  "archived" NUMBER(1, 0) DEFAULT '0',
  "created" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "createdBy" VARCHAR2(128) NOT NULL,
  "modified" TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  "modifiedBy" VARCHAR2(128) NOT NULL
);;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "pk1_router_search_interface" PRIMARY KEY ("id")
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "uk1_router_search_interface" UNIQUE ("interface")
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck1_router_search_interface" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck2_router_search_interface" CHECK("interface" IS NOT NULL)
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck3_router_search_interface" CHECK("json" IS NOT NULL)
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck4_router_search_interface" CHECK("archived" IS NOT NULL)
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck5_router_search_interface" CHECK("created" IS NOT NULL)
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck6_router_search_interface" CHECK("createdBy" IS NOT NULL)
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck7_router_search_interface" CHECK("modified" IS NOT NULL)
;;

ALTER TABLE "router_search_interface"
  ADD CONSTRAINT "ck8_router_search_interface" CHECK("modifiedBy" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_search_interface"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_search_interface"
  BEFORE INSERT ON "router_search_interface"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_search_interface".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

CREATE TRIGGER "trig2_router_search_interface"
  BEFORE UPDATE ON "router_search_interface"
  FOR EACH ROW
  BEGIN
    :NEW."modified" := SYSDATE;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_locations" (
  "id" NUMBER(*, 0),
  "continentCode" VARCHAR2(3) DEFAULT 'N/A',
  "countryCode" VARCHAR2(3) DEFAULT 'N/A'
);;

ALTER TABLE "router_locations"
  ADD CONSTRAINT "pk1_router_locations" PRIMARY KEY ("id")
;;

ALTER TABLE "router_locations"
  ADD CONSTRAINT "uk1_router_locations" UNIQUE ("continentCode", "countryCode")
;;

ALTER TABLE "router_locations"
  ADD CONSTRAINT "ck1_router_locations" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_locations"
  ADD CONSTRAINT "ck2_router_locations" CHECK("continentCode" IS NOT NULL)
;;

ALTER TABLE "router_locations"
  ADD CONSTRAINT "ck3_router_locations" CHECK("countryCode" IS NOT NULL)
;;

CREATE SEQUENCE "seq_router_locations"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_locations"
  BEFORE INSERT ON "router_locations"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_locations".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_ipv4_blocks" (
  "id" NUMBER(*, 0),
  "network" VARCHAR2(32),
  "rangeBegin" NUMBER(10, 0),
  "rangeEnd" NUMBER(10, 0),
  "geoFK" NUMBER(*, 0)
);;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "pk1_router_ipv4_blocks" PRIMARY KEY ("id")
;;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "uk1_router_ipv4_blocks" UNIQUE ("network")
;;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "ck1_router_ipv4_blocks" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "ck2_router_ipv4_blocks" CHECK("network" IS NOT NULL)
;;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "ck3_router_ipv4_blocks" CHECK("rangeBegin" IS NOT NULL)
;;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "ck4_router_ipv4_blocks" CHECK("rangeEnd" IS NOT NULL)
;;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "ck5_router_ipv4_blocks" CHECK("geoFK" IS NOT NULL)
;;

ALTER TABLE "router_ipv4_blocks"
  ADD CONSTRAINT "fk1_router_ipv4_blocks" FOREIGN KEY ("geoFK") REFERENCES "router_locations" ("id")
;;

CREATE SEQUENCE "seq_router_ipv4_blocks"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_ipv4_blocks"
  BEFORE INSERT ON "router_ipv4_blocks"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_ipv4_blocks".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/

CREATE TABLE "router_ipv6_blocks" (
  "id" NUMBER(*, 0),
  "network" VARCHAR2(64),
  "rangeBegin" NUMBER(38, 0),
  "rangeEnd" NUMBER(38, 0),
  "geoFK" NUMBER(*, 0)
);;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "pk1_router_ipv6_blocks" PRIMARY KEY ("id")
;;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "uk1_router_ipv6_blocks" UNIQUE ("network")
;;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "ck1_router_ipv6_blocks" CHECK("id" IS NOT NULL)
;;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "ck2_router_ipv6_blocks" CHECK("network" IS NOT NULL)
;;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "ck3_router_ipv6_blocks" CHECK("rangeBegin" IS NOT NULL)
;;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "ck4_router_ipv6_blocks" CHECK("rangeEnd" IS NOT NULL)
;;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "ck5_router_ipv6_blocks" CHECK("geoFK" IS NOT NULL)
;;

ALTER TABLE "router_ipv6_blocks"
  ADD CONSTRAINT "fk1_router_ipv6_blocks" FOREIGN KEY ("geoFK") REFERENCES "router_locations" ("id")
;;

CREATE SEQUENCE "seq_router_ipv6_blocks"
  START WITH 1 INCREMENT BY 1 CACHE 10
;;

CREATE TRIGGER "trig1_router_ipv6_blocks"
  BEFORE INSERT ON "router_ipv6_blocks"
  FOR EACH ROW
  BEGIN
    SELECT "seq_router_ipv6_blocks".NEXTVAL INTO :NEW."id" FROM dual;
  END;
;;

/*-------------------------------------------------------------------------*/
