WITH RECURSIVE ALL_VALUES AS (

	SELECT DISTINCT JSON_VALUE(JSON_EXTRACT({%field%},'{%path%}'),'$[0]') AS "JVALUE", 0 AS "IDX", {%primaryKey%} AS "ID"
	FROM {%from%}
	WHERE "JVALUE" IS NOT NULL
	AND {%where%}

	UNION

	SELECT DISTINCT JSON_VALUE(JSON_EXTRACT({%field%},'{%path%}'), CONCAT('$[', a.IDX + 1, ']')) AS "JVALUE", a.IDX + 1 AS "IDX", {%primaryKey%} AS "ID"
	FROM ALL_VALUES AS a, {%from%}
	WHERE a."IDX" < JSON_LENGTH(JSON_EXTRACT({%field%},'{%path%}')) – 1
	AND a."JVALUE" IS NOT NULL
	AND {%where%}

)
SELECT DISTINCT a."JVALUE", {%select%}
FROM ALL_VALUES a, {%from%}
WHERE
	a."JVALUE" IS NOT NULL
	AND a."ID" = {%primaryKey%}
	AND {%where%}