<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<!-- **************************************************************** -->

	<xsl:output method="text" encoding="UTF-8"></xsl:output>

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
		<xsl:variable name="s1" select="." />
		<xsl:variable name="s2" select="replace($s1, '&#x5c;', '\\\\')" />
		<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
		<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
		<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />

		<xsl:text>"</xsl:text>
		<xsl:copy-of select="$s5" />
		<xsl:text>"</xsl:text>

		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="error|info">
		<xsl:variable name="s1" select="." />
		<xsl:variable name="s2" select="replace($s1, '&#x5c;', '\\\\')" />
		<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
		<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
		<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />

		<xsl:text>{"$":"</xsl:text>
		<xsl:copy-of select="$s5" />
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
			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '&#x5c;', '\\\\')" />
			<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
			<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
			<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />

			<xsl:text>"@</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="$s5" />
			<xsl:text>",</xsl:text>
		</xsl:for-each>

		<xsl:variable name="s6" select="." />
		<xsl:variable name="s7" select="replace($s6, '&#x5c;', '\\\\')" />
		<xsl:variable name="s8" select="replace($s7, '&#xa;', '\\n')" />
		<xsl:variable name="s9" select="replace($s8, '&#x9;', '\\t')" />
		<xsl:variable name="sA" select="replace($s9, '&quot;', '\\&quot;')" />

		<xsl:text>"$":"</xsl:text>
		<xsl:value-of select="$sA" />
		<xsl:text>"</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="rowset">
		<xsl:text>{</xsl:text>

		<xsl:variable name="s1" select="sql" />
		<xsl:variable name="s2" select="replace($s1, '&#x5c;', '\\\\')" />
		<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
		<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
		<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />

		<xsl:text>"@sql":"</xsl:text>
		<xsl:value-of select="$s5" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s6" select="mql" />
		<xsl:variable name="s7" select="replace($s6, '&#x5c;', '\\\\')" />
		<xsl:variable name="s8" select="replace($s7, '&#xa;', '\\n')" />
		<xsl:variable name="s9" select="replace($s8, '&#x9;', '\\t')" />
		<xsl:variable name="sA" select="replace($s9, '&quot;', '\\&quot;')" />

		<xsl:text>"@mql":"</xsl:text>
		<xsl:value-of select="$sA" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="sB" select="ast" />
		<xsl:variable name="sC" select="replace($sB, '&#x5c;', '\\\\')" />
		<xsl:variable name="sD" select="replace($sC, '&#xa;', '\\n')" />
		<xsl:variable name="sE" select="replace($sD, '&#x9;', '\\t')" />
		<xsl:variable name="sF" select="replace($sE, '&quot;', '\\&quot;')" />

		<xsl:text>"@ast":"</xsl:text>
		<xsl:value-of select="$sF" />
		<xsl:text>",</xsl:text>

		<xsl:text>"@type":"</xsl:text>
		<xsl:value-of select="@type" />
		<xsl:text>",</xsl:text>

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
			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '&#x5c;', '\\\\')" />
			<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
			<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
			<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />

			<xsl:text>"@</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="$s5" />
			<xsl:text>",</xsl:text>
		</xsl:for-each>

		<xsl:variable name="s6" select="." />
		<xsl:variable name="s7" select="replace($s6, '&#x5c;', '\\\\')" />
		<xsl:variable name="s8" select="replace($s7, '&#xa;', '\\n')" />
		<xsl:variable name="s9" select="replace($s8, '&#x9;', '\\t')" />
		<xsl:variable name="sA" select="replace($s9, '&quot;', '\\&quot;')" />

		<xsl:text>"$":"</xsl:text>
		<xsl:value-of select="$sA" />
		<xsl:text>"</xsl:text>
		<xsl:if test="properties|link">,</xsl:if>

		<xsl:if test="properties">
			<xsl:text>"properties":[</xsl:text>
			<xsl:apply-templates select="properties" />
			<xsl:text>]</xsl:text>
			<xsl:if test="link">,</xsl:if>
		</xsl:if>

		<xsl:if test="link">
			<xsl:text>"link":[</xsl:text>
			<xsl:apply-templates select="link" />
			<xsl:text>]</xsl:text>
		</xsl:if>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="properties">
		<xsl:text>{</xsl:text>

		<xsl:for-each select="@*">
			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '&#x5c;', '\\\\')" />
			<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
			<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
			<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />

			<xsl:text>"@</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="$s5" />
			<xsl:text>"</xsl:text>
			<xsl:if test="not (position() = last())">,</xsl:if>
		</xsl:for-each>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

	<xsl:template match="link">
		<xsl:text>{</xsl:text>

		<xsl:for-each select="@*">
			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '&#x5c;', '\\\\')" />
			<xsl:variable name="s3" select="replace($s2, '&#xa;', '\\n')" />
			<xsl:variable name="s4" select="replace($s3, '&#x9;', '\\t')" />
			<xsl:variable name="s5" select="replace($s4, '&quot;', '\\&quot;')" />

			<xsl:text>"@</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="$s5" />
			<xsl:text>"</xsl:text>
			<xsl:if test="not (position() = last())">,</xsl:if>
		</xsl:for-each>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<!-- **************************************************************** -->

</xsl:stylesheet>
