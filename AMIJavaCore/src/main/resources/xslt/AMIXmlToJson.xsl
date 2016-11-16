<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text" encoding="UTF-8"></xsl:output>

	<xsl:template match="/AMIMessage">
		<xsl:text>{"AMIMessage":{</xsl:text>

		<xsl:apply-templates select="error" />
		<xsl:apply-templates select="info" />

		<xsl:text>"Result":{</xsl:text>
		<xsl:apply-templates select="Result" />
		<xsl:text>}</xsl:text>

		<xsl:text>}}</xsl:text>
	</xsl:template>

	<xsl:template match="error">
		<xsl:variable name="s1" select="." />
		<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
		<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
		<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

		<xsl:text>"error":[</xsl:text>
		<xsl:text>"</xsl:text>
		<xsl:copy-of select="$s4" />
		<xsl:text>"</xsl:text>
		<xsl:text>],</xsl:text>

		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="info">
		<xsl:variable name="s1" select="." />
		<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
		<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
		<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

		<xsl:text>"info":[</xsl:text>
		<xsl:text>"</xsl:text>
		<xsl:copy-of select="$s4" />
		<xsl:text>"</xsl:text>
		<xsl:text>],</xsl:text>

		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="Result">
		<xsl:text>"rowset":[</xsl:text>

		<xsl:apply-templates select="rowset" />

		<xsl:text>]</xsl:text>
	</xsl:template>

	<xsl:template match="rowset">
		<xsl:text>{</xsl:text>

		<xsl:text>"@type":"</xsl:text>
		<xsl:value-of select="@type" />
		<xsl:text>",</xsl:text>

		<xsl:text>"row":[</xsl:text>
		<xsl:apply-templates select="row" />
		<xsl:text>]</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="row">
		<xsl:text>{</xsl:text>

		<xsl:text>"field":[</xsl:text>
		<xsl:apply-templates select="field" />
		<xsl:text>]</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="field">
		<xsl:text>{</xsl:text>

		<xsl:for-each select="@*">
			<xsl:variable name="s1" select="name()" />
			<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
			<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
			<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

			<xsl:variable name="s5" select="." />
			<xsl:variable name="s6" select="replace($s5, '&#xa;', '\\n')" />
			<xsl:variable name="s7" select="replace($s6, '&#x9;', '\\t')" />
			<xsl:variable name="s8" select="replace($s7, '&quot;', '\\&quot;')" />

			<xsl:text>"@</xsl:text>
			<xsl:value-of select="$s4" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="$s8" />
			<xsl:text>",</xsl:text>
		</xsl:for-each>

		<xsl:variable name="s9" select="." />
		<xsl:variable name="sA" select="replace($s9, '&#xa;', '\\n')" />
		<xsl:variable name="sB" select="replace($sA, '&#x9;', '\\t')" />
		<xsl:variable name="sC" select="replace($sB, '&quot;', '\\&quot;')" />

		<xsl:text>"$":"</xsl:text>
		<xsl:value-of select="$sC" />
		<xsl:text>"</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

</xsl:stylesheet>