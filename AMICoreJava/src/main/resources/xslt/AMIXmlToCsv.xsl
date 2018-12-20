<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text" encoding="UTF-8"></xsl:output>

	<xsl:template match="/AMIMessage">
		<xsl:text>#AMI RESULT&#x0a;&#x0a;</xsl:text>

		<xsl:apply-templates select="help" />
		<xsl:apply-templates select="usage" />
		<xsl:apply-templates select="error" />
		<xsl:apply-templates select="info" />
		<xsl:apply-templates select="rowset" />

		<xsl:text>&#x0a;#</xsl:text>
	</xsl:template>

	<xsl:template match="help">
		<xsl:text>#HELP: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="usage">
		<xsl:text>#USAGE: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="error">
		<xsl:text>#ERROR: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="info">
		<xsl:text>#INFO: </xsl:text>
		<xsl:copy-of select="." />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="rowset">

		<xsl:text>#ROWSET </xsl:text>
		<xsl:value-of select="@type" />

		<xsl:text>&#x0a;#FIELDS&#x0a;</xsl:text>

		<xsl:for-each select="./row[1]/field">
			<xsl:variable name="s1" select="@name" />
			<xsl:variable name="s2" select="replace($s1, '\\', '\\\\')" />
			<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
			<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
			<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />
			<xsl:text>"</xsl:text>
			<xsl:value-of select="$s5" />
			<xsl:text>"</xsl:text>
			<xsl:if test="not (position() = last())">;</xsl:if>
		</xsl:for-each>

		<xsl:text>&#x0a;#VALUES&#x0a;</xsl:text>

		<xsl:apply-templates select="row" />

	</xsl:template>

	<xsl:template match="row">

		<xsl:for-each select="field">
			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '\\', '\\\\')" />
			<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
			<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
			<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />
			<xsl:text>"</xsl:text>
			<xsl:value-of select="$s5" />
			<xsl:text>"</xsl:text>
			<xsl:if test="not (position() = last())">;</xsl:if>
		</xsl:for-each>

		<xsl:text>&#x0a;</xsl:text>

	</xsl:template>

</xsl:stylesheet>
