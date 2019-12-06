
DROP VIEW IF EXISTS "FILE_VIEW";;
DROP TABLE IF EXISTS "DATASET_FILE_BRIDGE";;
DROP TABLE IF EXISTS "DATASET_PARAM";;
DROP TABLE IF EXISTS "FILE";;
DROP TABLE IF EXISTS "DATASET_GRAPH";;
DROP TABLE IF EXISTS "DATASET";;
DROP TABLE IF EXISTS "FILE_TYPE";;
DROP TABLE IF EXISTS "DATASET_TYPE";;
DROP TABLE IF EXISTS "PROJECT";;

CREATE TABLE "PROJECT" (
  "id" integer auto_increment PRIMARY KEY
,  "name" VARCHAR(128) NOT NULL
,  "description" VARCHAR(512)
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,   UNIQUE ("name")
);;

CREATE TABLE "DATASET_TYPE" (
  "id" integer auto_increment PRIMARY KEY
,  "projectFK" integer NOT NULL
,  "name" VARCHAR(128) NOT NULL
,  "description" VARCHAR(512)
,  "photo" CLOB
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,  UNIQUE ("name", "projectFK")
,  FOREIGN KEY ("projectFK") REFERENCES "PROJECT" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

CREATE TABLE "FILE_TYPE" (
  "id" integer auto_increment PRIMARY KEY
,  "projectFK" integer NOT NULL
,  "name" VARCHAR(128) NOT NULL
,  "description" VARCHAR(512)
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,  FOREIGN KEY ("projectFK") REFERENCES "PROJECT" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

CREATE TABLE "DATASET" (
  "id" integer auto_increment PRIMARY KEY
,  "projectFK" integer NOT NULL
,  "typeFK" integer NOT NULL
,  "name" VARCHAR(128) NOT NULL
,  "valid" integer DEFAULT 1 NOT NULL
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,  UNIQUE ("name", "projectFK")
,  FOREIGN KEY ("projectFK") REFERENCES "PROJECT" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
,  FOREIGN KEY ("typeFK") REFERENCES "DATASET_TYPE" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

CREATE TABLE "FILE" (
  "id" integer auto_increment PRIMARY KEY
,  "typeFK" integer NOT NULL
,  "name" VARCHAR(128) NOT NULL
,  "size" integer
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,  UNIQUE ("name")
,  FOREIGN KEY ("typeFK") REFERENCES "FILE_TYPE" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

CREATE TABLE "DATASET_FILE_BRIDGE" (
  "id" integer auto_increment PRIMARY KEY
,  "datasetFK" integer NOT NULL
,  "fileFK" integer NOT NULL
,  "comment" VARCHAR(128)
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,  UNIQUE ("datasetFK", "fileFK")
,  FOREIGN KEY ("datasetFK") REFERENCES "DATASET" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
,  FOREIGN KEY ("fileFK") REFERENCES "FILE" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

CREATE TABLE "DATASET_GRAPH" (
  "id" integer auto_increment PRIMARY KEY
,  "sourceFK" integer NOT NULL
,  "destinationFK" integer NOT NULL
,  "comment" VARCHAR(128)
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,  UNIQUE ("sourceFK", "destinationFK")
,  FOREIGN KEY ("sourceFK") REFERENCES "DATASET" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
,  FOREIGN KEY ("destinationFK") REFERENCES "DATASET" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

CREATE TABLE "DATASET_PARAM" (
  "id" integer auto_increment PRIMARY KEY
,  "datasetFK" integer NOT NULL
,  "name" VARCHAR(128) NOT NULL
,  "type" VARCHAR(128) NOT NULL
,  "intValue" integer
,  "floatValue" DECIMAL(6,4)
,  "stringValue" VARCHAR(512)
,  "timestampValue" TIMESTAMP
,  "booleanValue" integer DEFAULT NULL
,  "created" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "createdBy" VARCHAR(128) NOT NULL
,  "modified" TIMESTAMP NOT NULL DEFAULT current_timestamp
,  "modifiedBy" VARCHAR(128) NOT NULL
,  UNIQUE ("name", "datasetFK")
,  FOREIGN KEY ("datasetFK") REFERENCES "DATASET" ("id") ON DELETE CASCADE ON UPDATE NO ACTION
);;

CREATE VIEW "FILE_VIEW" AS
  SELECT "DATASET_FILE_BRIDGE"."id" AS "id", "PROJECT"."name" AS "projectName", "DATASET"."name" AS "datasetName",  "FILE"."name" AS "fileName"
  FROM "PROJECT", "DATASET", "FILE", "DATASET_FILE_BRIDGE"
  WHERE "DATASET"."projectFK" = "PROJECT"."id"
  AND "DATASET_FILE_BRIDGE"."datasetFK" = "DATASET"."id"
  AND "DATASET_FILE_BRIDGE"."fileFK" = "FILE"."id"
;;