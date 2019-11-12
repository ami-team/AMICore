<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ami="http://ami.in2p3.fr/xsl" version="2.0">

	<xsl:output method="text" encoding="UTF-8" />

	<xsl:template match="/AMIMessage">
		<xsl:text>#AMI Result&#x0a;&#x0a;</xsl:text>

		<xsl:apply-templates select="help" />
		<xsl:apply-templates select="usage" />
		<xsl:apply-templates select="error" />
		<xsl:apply-templates select="info" />
		<xsl:apply-templates select="rowset" />

		<xsl:text>&#x0a;#</xsl:text>
	</xsl:template>

	<xsl:template match="help">
		<xsl:text>Help: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="usage">
		<xsl:text>Usage: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="error">
		<xsl:text>Error: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="info">
		<xsl:text>Info: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="sql">
		<xsl:text>  Sql: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="mql">
		<xsl:text>  Mql: </xsl:text>
		<xsl:copy-of select="text()" />
		<xsl:text>&#x0a;</xsl:text>
	</xsl:template>

	<xsl:template match="ast">
		<xsl:text>  Ast: </xsl:text>
		<xsl:copy-of select="text()" />
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

			<xsl:text>    -> </xsl:text>
			<xsl:value-of select="ami:replace(@name, false())" />
			<xsl:text> = </xsl:text>

			<xsl:text>"</xsl:text>
			<xsl:value-of select="ami:replace(.[1], false())" />
			<xsl:text>"</xsl:text>

			<xsl:text>&#x0a;</xsl:text>

		</xsl:for-each>

	</xsl:template>

</xsl:stylesheet>
