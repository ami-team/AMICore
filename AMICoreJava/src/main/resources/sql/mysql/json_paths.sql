WITH RECURSIVE ALL_PATHS AS (

	SELECT DISTINCT JSON_VALUE(REGEXP_REPLACE(JSON_SEARCH(JSON_QUERY({%field%},'{%path%}'),'all','%'),'\\[[0-9]?\\]','\\[*\\]'),'$[0]') AS "JPATH", 0 AS "IDX" , {%primaryKey%} AS "ID"
	FROM {%from%}
	WHERE {%where%}

	UNION

	SELECT DISTINCT JSON_VALUE(REGEXP_REPLACE(JSON_SEARCH(JSON_QUERY({%field%},'{%path%}'),'all','%'),'\\[[0-9]?\\]','\\[*\\]'), CONCAT('$[', ap.IDX + 1, ']')) AS "JPATH", ap.IDX + 1 AS "IDX", {%primaryKey%} AS "ID"
	FROM ALL_PATHS ap, {%from%}
	WHERE ap.JPATH IS NOT NULL
	AND  ap.IDX < JSON_LENGTH(JSON_SEARCH(JSON_QUERY({%field%},'{%path%}'),'all','%')) - 1
	AND {%where%}

)
SELECT DISTINCT {%select%}, a.JPATH AS "PATH"
FROM {%from%}, ALL_PATHS a
WHERE
	a.JPATH IS NOT NULL
    AND a.ID = {%primaryKey%}
    AND {%where%}