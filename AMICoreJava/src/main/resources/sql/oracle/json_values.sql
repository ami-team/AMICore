WITH
ALL_VALUES AS
(
	SELECT DISTINCT "ami_json_values_table"."JVALUE", "ami_internal_data_table"."ID"
	FROM
	(
		SELECT {%primaryKey%} AS "ID", JSON_QUERY({%field%},'{%pathStart%}') AS val
		FROM  {%from%}
		WHERE {%where%}

	) "ami_internal_data_table",
	JSON_TABLE
	(
		"ami_internal_data_table".val, '$[*]' COLUMNS
		(
			"JVALUE" VARCHAR2 FORMAT JSON PATH '{%pathEnd%}'
		)
	) "ami_json_values_table"
	WHERE
	 	"ami_json_values_table"."JVALUE" IS NOT NULL

	UNION

	SELECT DISTINCT "ami_json_values_table"."JVALUE", "ami_internal_data_table"."ID"
	FROM
	(
		SELECT {%primaryKey%} AS "ID", JSON_QUERY({%field%},'{%pathStart%}') AS val
		FROM  {%from%}
		WHERE {%where%}

	) "ami_internal_data_table",
	JSON_TABLE
	(
		"ami_internal_data_table".val , '$[*]' COLUMNS
		(
			"JVALUE" VARCHAR2 PATH '{%pathEnd%}'
		)
	) "ami_json_values_table"
	WHERE
		"ami_json_values_table"."JVALUE" IS NOT NULL

	UNION

	SELECT DISTINCT "ami_json_values_table"."JVALUE", "ami_internal_data_table"."ID"
	FROM
	(
		SELECT {%primaryKey%} AS "ID", JSON_ARRAY(JSON_VALUE({%field%},'{%path%}')) AS val
		FROM {%from%}
		WHERE {%where%}

	) "ami_internal_data_table",
	JSON_TABLE
	(
		"ami_internal_data_table".val , '$[*]' COLUMNS
		(
			"JVALUE" VARCHAR2 PATH '$[*]'
		)
	) "ami_json_values_table"
	WHERE "ami_json_values_table"."JVALUE" IS NOT NULL
)
SELECT DISTINCT a."JVALUE" AS VALUE
FROM "ALL_VALUES" a, {%from%}
WHERE
	a."JVALUE" IS NOT NULL
	AND a."ID" = {%primaryKey%}
	AND {%where%}