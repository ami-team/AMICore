<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text" encoding="UTF-8"></xsl:output>

	<xsl:template match="/AMIMessage">
		<xsl:text>#AMI Result&#x0a;&#x0a;</xsl:text>

		<xsl:apply-templates select="error" />
		<xsl:apply-templates select="info" />
		<xsl:apply-templates select="rowset" />

		<xsl:text>&#x0a;#</xsl:text>
	</xsl:template>

	<xsl:template match="error">
		<xsl:text>Error: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="info">
		<xsl:text>Info: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="sql">
		<xsl:text>  Sql: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="mql">
		<xsl:text>  Mql: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="ast">
		<xsl:text>  Ast: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="rowset">
		<xsl:text>Rowset: </xsl:text>
		<xsl:value-of select="@type" />
		<xsl:text>&#x0a;</xsl:text>
		<xsl:apply-templates select="sql" />
		<xsl:apply-templates select="mql" />
		<xsl:apply-templates select="ast" />
		<xsl:apply-templates select="row" />
	</xsl:template>

	<xsl:template match="row">

		<xsl:text>  -> row&#x0a;</xsl:text>

		<xsl:for-each select="field">

			<xsl:variable name="s1" select="@name" />
			<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
			<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
			<xsl:text>    -> </xsl:text>
			<xsl:value-of select="$s3" />
			<xsl:text> = </xsl:text>

			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
			<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
			<xsl:text>"</xsl:text>
			<xsl:value-of select="$s3" />
			<xsl:text>"</xsl:text>

			<xsl:text>&#x0a;</xsl:text>

		</xsl:for-each>

	</xsl:template>

</xsl:stylesheet>
