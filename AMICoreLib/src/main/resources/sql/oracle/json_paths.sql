WITH ALL_PATHS AS (
	SELECT DISTINCT
		ami_internal_table."ID",
		ami_json_table."JPATH",
		ami_json_table."JTYPE"
	FROM
		(
			SELECT JSON_DATAGUIDE(JSON_QUERY({%field%},'{%path%}')) AS val, {%primaryKey%} AS ID
			FROM {%from%}
			WHERE {%where%}
			GROUP BY {%primaryKey%}
		)  ami_internal_table,
		JSON_TABLE(
			ami_internal_table.val,
			'$[*]' COLUMNS (
				"JPATH" VARCHAR2(40) PATH '$."o:path"',
				"JTYPE" VARCHAR2(40) PATH  '$."type"'
			)
		)  ami_json_table
)

SELECT DISTINCT {%select1%}
FROM
	ALL_PATHS a, {%from%}
WHERE
    (a."JTYPE" != 'array' AND a."JTYPE" != 'object')
    AND
    a."JPATH" NOT IN
    (
		SELECT DISTINCT aa."JPATH"
		FROM ALL_PATHS aa
		WHERE aa."JTYPE" = 'array'
	
		UNION

		SELECT DISTINCT aa."JPATH"
		FROM ALL_PATHS aa, ALL_PATHS bb
		WHERE (aa."JTYPE" != 'array' AND aa."JTYPE" != 'object')
		AND bb."JTYPE" = 'array'
		AND INSTR(aa."JPATH", CONCAT(bb."JPATH",'.')) > 0
    )

	AND a."JPATH" IS NOT NULL
    AND a.ID = {%primaryKey%}
    AND {%where%}

UNION

SELECT DISTINCT {%select2%}
FROM
	ALL_PATHS a , {%from%}
WHERE
    a."JTYPE" = 'array'
	AND a."JPATH" IS NOT NULL
    AND a."ID" = {%primaryKey%}
    AND {%where%}

UNION

SELECT DISTINCT {%select3%}
FROM
	ALL_PATHS a, ALL_PATHS b, {%from%}
WHERE
	(a."JTYPE" != 'array' AND a."JTYPE" != 'object')
	AND b."JTYPE" = 'array'
	AND INSTR(a."JPATH", CONCAT(b."JPATH",'.')) > 0
	AND INSTR(a."JPATH",'.',-1) = (LENGTH(b."JPATH") + 1)
	AND a."JPATH" IS NOT NULL
	AND a."ID" = {%primaryKey%}
	AND {%where%}