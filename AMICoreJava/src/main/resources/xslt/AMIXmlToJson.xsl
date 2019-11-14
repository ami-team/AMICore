<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ami="http://ami.in2p3.fr/xsl" version="2.0">

	<!-- **************************************************************** -->

	<xsl:output method="text" encoding="UTF-8" />

	<!-- **************************************************************** -->

	<xsl:template match="/AMIMessage">
		<xsl:text>{"AMIMessage":{</xsl:text>

		<xsl:if test="help">
			<xsl:text>"help":[</xsl:text>
			<xsl:apply-templates select="help" />
			<xsl:text>]</xsl:text>
			<xsl:if test="usage|error|info|fieldDescriptions|rowset">,</xsl:if>
		</xsl:if>

		<xsl:if test="usage">
			<xsl:text>"usage":[</xsl:text>
			<xsl:apply-templates select="help" />
			<xsl:text>]</xsl:text>
			<xsl:if test="error|info|fieldDescriptions|rowset">,</xsl:if>
		</xsl:if>

		<xsl:if test="error">
			<xsl:text>"error":[</xsl:text>
			<xsl:apply-templates select="error" />
			<xsl:text>]</xsl:text>
			<xsl:if test="info|fieldDescriptions|rowset">,</xsl:if>
		</xsl:if>

		<xsl:if test="info">
			<xsl:text>"info":[</xsl:text>
			<xsl:apply-templates select="info" />
			<xsl:text>]</xsl:text>
			<xsl:if test="fieldDescriptions|rowset">,</xsl:if>
		</xsl:if>

		<xsl:if test="fieldDescriptions">
			<xsl:text>"fieldDescriptions":[</xsl:text>
			<xsl:apply-templates select="fieldDescriptions" />
			<xsl:text>]</xsl:text>
			<xsl:if test="rowset">,</xsl:if>
		</xsl:if>

		<xsl:if test="rowset">
			<xsl:text>"rowset":[</xsl:text>
			<xsl:apply-templates select="rowset" />
			<xsl:text>]</xsl:text>
		</xsl:if>

		<xsl:text>}}</xsl:text>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="help|usage">
		<xsl:text>"</xsl:text>
		<xsl:copy-of select="ami:replace(text(), true())" />
		<xsl:text>"</xsl:text>

		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="error|info">
		<xsl:text>{"$":"</xsl:text>
		<xsl:copy-of select="ami:replace(text(), true())" />
		<xsl:text>"}</xsl:text>

		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="fieldDescriptions">
		<xsl:text>{</xsl:text>

		<xsl:text>"fieldDescription":[</xsl:text>
		<xsl:apply-templates select="fieldDescription" />
		<xsl:text>]</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="fieldDescription">
		<xsl:text>{</xsl:text>

		<xsl:for-each select="@*">
			<xsl:text>"@</xsl:text>
			<xsl:value-of select="ami:replace(name(), true())" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="ami:replace(., true())" />
			<xsl:text>",</xsl:text>
		</xsl:for-each>

		<xsl:text>"$":"</xsl:text>
		<xsl:value-of select="ami:replace(text(), true())" />
		<xsl:text>"</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="rowset">
		<xsl:text>{</xsl:text>

		<xsl:text>"@sql":"</xsl:text>
		<xsl:value-of select="ami:replace(sql, true())" />
		<xsl:text>",</xsl:text>

		<xsl:text>"@mql":"</xsl:text>
		<xsl:value-of select="ami:replace(mql, true())" />
		<xsl:text>",</xsl:text>

		<xsl:text>"@ast":"</xsl:text>
		<xsl:value-of select="ami:replace(ast, true())" />
		<xsl:text>",</xsl:text>

		<xsl:text>"@type":"</xsl:text>
		<xsl:value-of select="@type" />
		<xsl:text>",</xsl:text>

		<xsl:if test="@truncated">
			<xsl:text>"@truncated":</xsl:text>
			<xsl:choose>
				<xsl:when test="@truncated = 'true' or @truncated = 'false'">
					<xsl:value-of select="@truncated" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>false</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:text>,</xsl:text>
		</xsl:if>

		<xsl:if test="@maxNumberOfRows">
			<xsl:text>"@maxNumberOfRows":</xsl:text>
				<xsl:value-of select="@maxNumberOfRows" />
			<xsl:text>,</xsl:text>
		</xsl:if>

		<xsl:if test="@totalNumberOfRows">
			<xsl:text>"@totalNumberOfRows":</xsl:text>
				<xsl:value-of select="@totalNumberOfRows" />
			<xsl:text>,</xsl:text>
		</xsl:if>

		<xsl:text>"row":[</xsl:text>
		<xsl:apply-templates select="row" />
		<xsl:text>]</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="row">
		<xsl:text>{</xsl:text>

		<xsl:text>"field":[</xsl:text>
		<xsl:apply-templates select="field" />
		<xsl:text>]</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="field">
		<xsl:text>{</xsl:text>

		<xsl:for-each select="@*">
			<xsl:text>"@</xsl:text>
			<xsl:value-of select="ami:replace(name(), true())" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="ami:replace(., true())" />
			<xsl:text>",</xsl:text>
		</xsl:for-each>

		<xsl:text>"$":"</xsl:text>
		<xsl:value-of select="ami:replace(text(), true())" />
		<xsl:text>"</xsl:text>
		<xsl:if test="properties|link">,</xsl:if>

		<xsl:if test="properties">
			<xsl:text>"properties":</xsl:text>
			<xsl:apply-templates select="properties" />
			<xsl:if test="link">,</xsl:if>
		</xsl:if>

		<xsl:if test="link">
			<xsl:text>"links":[</xsl:text>
			<xsl:apply-templates select="link" />
			<xsl:text>]</xsl:text>
		</xsl:if>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="properties|link">
		<xsl:text>{</xsl:text>

		<xsl:for-each select="@*">
			<xsl:text>"@</xsl:text>
			<xsl:value-of select="ami:replace(name(), true())" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="ami:replace(., true())" />
			<xsl:text>"</xsl:text>
			<xsl:if test="not (position() = last())">,</xsl:if>
		</xsl:for-each>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

</xsl:stylesheet>
