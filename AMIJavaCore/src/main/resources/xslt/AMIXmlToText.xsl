<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text" encoding="UTF-8"></xsl:output>

	<xsl:template match="/">
		<xsl:apply-templates select="/AMIMessage/Result" />
	</xsl:template>

	<xsl:template match="/AMIMessage/Result">

		<xsl:text>#AMI&#x0a;&#x0a;</xsl:text>

		<xsl:apply-templates select="error" />
		<xsl:apply-templates select="info" />

		<xsl:text>Result:&#x0a;</xsl:text>
		<xsl:apply-templates select="rowset" />

		<xsl:text>#</xsl:text>

	</xsl:template>

	<xsl:template match="error">
		<xsl:text>error: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="info">
		<xsl:text>info: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="rowset">

		<xsl:text>  -> rowset </xsl:text>
		<xsl:value-of select="@type" />
		<xsl:text>&#x0a;</xsl:text>

		<xsl:apply-templates select="row" />
		<xsl:text>&#x0a;</xsl:text>

	</xsl:template>

	<xsl:template match="row">

		<xsl:text>    -> row&#x0a;</xsl:text>

		<xsl:for-each select="field">

			<xsl:text>      -> </xsl:text>
			<xsl:variable name="s1" select="@name"/>
			<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
			<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
			<xsl:value-of select="$s3" />
			<xsl:text> = </xsl:text>
			<xsl:variable name="s1" select="."/>
			<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
			<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
			<xsl:text>"</xsl:text>
			<xsl:value-of select="$s3" />
			<xsl:text>"</xsl:text>

			<xsl:text>&#x0a;</xsl:text>

		</xsl:for-each>

	</xsl:template>

</xsl:stylesheet>