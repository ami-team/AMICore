WITH RECURSIVE ALL_VALUES AS (

	SELECT DISTINCT JSON_VALUE(JSON_EXTRACT({%field%},'{%path%}'),'$[0]') AS "JVALUE", 0 AS "IDX", {%primaryKey%} AS "ID"
	FROM {%from%}
	WHERE {%where%}

	UNION

	SELECT DISTINCT JSON_VALUE(JSON_EXTRACT({%field%},'{%path%}'), CONCAT('$[', av.IDX + 1, ']')) AS "JVALUE", av.IDX + 1 AS "IDX", {%primaryKey%} AS "ID"
	FROM {%from%}, ALL_VALUES av
	WHERE av.IDX < JSON_LENGTH(JSON_EXTRACT({%field%},'{%path%}')) - 1
	AND av.JVALUE IS NOT NULL
	AND {%where%}

)
SELECT DISTINCT {%select%}
FROM {%from%}, ALL_VALUES a
WHERE
	a.JVALUE IS NOT NULL
	AND a.ID = {%primaryKey%}
	AND {%where%}