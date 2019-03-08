<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ami="http://ami.in2p3.fr/xsl" version="2.0">

	<xsl:output method="text" encoding="UTF-8"></xsl:output>

	<xsl:template match="/AMIMessage">
		<xsl:text>#AMI RESULT&#x0a;</xsl:text>

		<xsl:apply-templates select="help" />
		<xsl:apply-templates select="usage" />
		<xsl:apply-templates select="error" />
		<xsl:apply-templates select="info" />
		<xsl:apply-templates select="rowset" />

		<xsl:text>&#x0a;#</xsl:text>
	</xsl:template>

	<xsl:template match="help">
		<xsl:text>&#x0a;#HELP: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="usage">
		<xsl:text>&#x0a;#USAGE: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="error">
		<xsl:text>&#x0a;#ERROR: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="info">
		<xsl:text>&#x0a;#INFO: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="rowset">

		<xsl:text>&#x0a;#ROWSET </xsl:text>
		<xsl:value-of select="@type" />

		<xsl:text>&#x0a;#FIELDS&#x0a;</xsl:text>

		<xsl:for-each select="./row[1]/field">
			<xsl:text>"</xsl:text>
			<xsl:value-of select="ami:replace(@name, true())" />
			<xsl:text>"</xsl:text>
			<xsl:if test="not (position() = last())">;</xsl:if>
		</xsl:for-each>

		<xsl:text>&#x0a;#VALUES&#x0a;</xsl:text>

		<xsl:apply-templates select="row" />

	</xsl:template>

	<xsl:template match="row">

		<xsl:for-each select="field">
			<xsl:text>"</xsl:text>
			<xsl:value-of select="ami:replace(text(), true())" />
			<xsl:text>"</xsl:text>
			<xsl:if test="not (position() = last())">;</xsl:if>
		</xsl:for-each>

		<xsl:text>&#x0a;</xsl:text>

	</xsl:template>

</xsl:stylesheet>
