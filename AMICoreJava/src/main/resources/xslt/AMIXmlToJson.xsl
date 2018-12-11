<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

	<xsl:output method="text" encoding="UTF-8"></xsl:output>

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

	<xsl:template match="help|usage">
		<xsl:variable name="s1" select="." />
		<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
		<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
		<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

		<xsl:text>"</xsl:text>
		<xsl:copy-of select="$s4" />
		<xsl:text>"</xsl:text>

		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="error|info">
		<xsl:variable name="s1" select="." />
		<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
		<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
		<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

		<xsl:text>{"$":"</xsl:text>
		<xsl:copy-of select="$s4" />
		<xsl:text>"}</xsl:text>

		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="fieldDescriptions">
		<xsl:text>{</xsl:text>

		<xsl:text>"fieldDescription":[</xsl:text>
		<xsl:apply-templates select="fieldDescription" />
		<xsl:text>]</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="fieldDescription">
		<xsl:text>{</xsl:text>

		<xsl:for-each select="@*">
			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
			<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
			<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

			<xsl:text>"@</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="$s4" />
			<xsl:text>",</xsl:text>
		</xsl:for-each>

		<xsl:variable name="s5" select="." />
		<xsl:variable name="s6" select="replace($s5, '&#xa;', '\\n')" />
		<xsl:variable name="s7" select="replace($s6, '&#x9;', '\\t')" />
		<xsl:variable name="s8" select="replace($s7, '&quot;', '\\&quot;')" />

		<xsl:text>"$":"</xsl:text>
		<xsl:value-of select="$s8" />
		<xsl:text>"</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="rowset">
		<xsl:text>{</xsl:text>

		<xsl:variable name="s1" select="sql" />
		<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
		<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
		<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

		<xsl:text>"@sql":"</xsl:text>
		<xsl:value-of select="$s4" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s5" select="mql" />
		<xsl:variable name="s6" select="replace($s5, '&#xa;', '\\n')" />
		<xsl:variable name="s7" select="replace($s6, '&#x9;', '\\t')" />
		<xsl:variable name="s8" select="replace($s7, '&quot;', '\\&quot;')" />

		<xsl:text>"@mql":"</xsl:text>
		<xsl:value-of select="$s8" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s9" select="ast" />
		<xsl:variable name="sA" select="replace($s9, '&#xa;', '\\n')" />
		<xsl:variable name="sB" select="replace($sA, '&#x9;', '\\t')" />
		<xsl:variable name="sC" select="replace($sB, '&quot;', '\\&quot;')" />

		<xsl:text>"@ast":"</xsl:text>
		<xsl:value-of select="$sC" />
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
			<xsl:variable name="s1" select="." />
			<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
			<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
			<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

			<xsl:text>"@</xsl:text>
			<xsl:value-of select="name()" />
			<xsl:text>":"</xsl:text>
			<xsl:value-of select="$s4" />
			<xsl:text>",</xsl:text>
		</xsl:for-each>

		<xsl:variable name="s5" select="." />
		<xsl:variable name="s6" select="replace($s5, '&#xa;', '\\n')" />
		<xsl:variable name="s7" select="replace($s6, '&#x9;', '\\t')" />
		<xsl:variable name="s8" select="replace($s7, '&quot;', '\\&quot;')" />

		<xsl:text>"$":"</xsl:text>
		<xsl:value-of select="$s8" />
		<xsl:text>"</xsl:text>

		<xsl:if test="link">
			<xsl:text>",link":[</xsl:text>
			<xsl:apply-templates select="link" />
			<xsl:text>]</xsl:text>
		</xsl:if>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

	<xsl:template match="link">
		<xsl:text>{</xsl:text>

		<xsl:variable name="s1" select="id" />
		<xsl:variable name="s2" select="replace($s1, '&#xa;', '\\n')" />
		<xsl:variable name="s3" select="replace($s2, '&#x9;', '\\t')" />
		<xsl:variable name="s4" select="replace($s3, '&quot;', '\\&quot;')" />

		<xsl:text>"@id":"</xsl:text>
		<xsl:value-of select="$s4" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s5" select="class" />
		<xsl:variable name="s6" select="replace($s5, '&#xa;', '\\n')" />
		<xsl:variable name="s7" select="replace($s6, '&#x9;', '\\t')" />
		<xsl:variable name="s8" select="replace($s7, '&quot;', '\\&quot;')" />

		<xsl:text>"@class":"</xsl:text>
		<xsl:value-of select="$s8" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s9" select="css" />
		<xsl:variable name="s10" select="replace($s9, '&#xa;', '\\n')" />
		<xsl:variable name="s11" select="replace($s10, '&#x9;', '\\t')" />
		<xsl:variable name="s12" select="replace($s11, '&quot;', '\\&quot;')" />

		<xsl:text>"@css":"</xsl:text>
		<xsl:value-of select="$s12" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s13" select="href" />
		<xsl:variable name="s14" select="replace($s13, '&#xa;', '\\n')" />
		<xsl:variable name="s15" select="replace($s14, '&#x9;', '\\t')" />
		<xsl:variable name="s16" select="replace($s15, '&quot;', '\\&quot;')" />

		<xsl:text>"@href":"</xsl:text>
		<xsl:value-of select="$s16" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s17" select="target" />
		<xsl:variable name="s18" select="replace($s17, '&#xa;', '\\n')" />
		<xsl:variable name="s19" select="replace($s18, '&#x9;', '\\t')" />
		<xsl:variable name="s20" select="replace($s19, '&quot;', '\\&quot;')" />

		<xsl:text>"@target":"</xsl:text>
		<xsl:value-of select="$s20" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s21" select="label" />
		<xsl:variable name="s22" select="replace($s21, '&#xa;', '\\n')" />
		<xsl:variable name="s23" select="replace($s22, '&#x9;', '\\t')" />
		<xsl:variable name="s24" select="replace($s23, '&quot;', '\\&quot;')" />

		<xsl:text>"@label":"</xsl:text>
		<xsl:value-of select="$s24" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s25" select="data-ctrl" />
		<xsl:variable name="s26" select="replace($s25, '&#xa;', '\\n')" />
		<xsl:variable name="s27" select="replace($s26, '&#x9;', '\\t')" />
		<xsl:variable name="s28" select="replace($s27, '&quot;', '\\&quot;')" />

		<xsl:text>"@data-ctrl":"</xsl:text>
		<xsl:value-of select="$s28" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s29" select="data-location" />
		<xsl:variable name="s30" select="replace($s29, '&#xa;', '\\n')" />
		<xsl:variable name="s31" select="replace($s30, '&#x9;', '\\t')" />
		<xsl:variable name="s32" select="replace($s31, '&quot;', '\\&quot;')" />

		<xsl:text>"@data-location":"</xsl:text>
		<xsl:value-of select="$s32" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s33" select="data-params" />
		<xsl:variable name="s34" select="replace($s33, '&#xa;', '\\n')" />
		<xsl:variable name="s35" select="replace($s34, '&#x9;', '\\t')" />
		<xsl:variable name="s36" select="replace($s35, '&quot;', '\\&quot;')" />

		<xsl:text>"@data-params":"</xsl:text>
		<xsl:value-of select="$s36" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s37" select="data-settings" />
		<xsl:variable name="s38" select="replace($s37, '&#xa;', '\\n')" />
		<xsl:variable name="s39" select="replace($s38, '&#x9;', '\\t')" />
		<xsl:variable name="s40" select="replace($s39, '&quot;', '\\&quot;')" />

		<xsl:text>"@data-settings":"</xsl:text>
		<xsl:value-of select="$s40" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s41" select="data-icon" />
		<xsl:variable name="s42" select="replace($s41, '&#xa;', '\\n')" />
		<xsl:variable name="s43" select="replace($s42, '&#x9;', '\\t')" />
		<xsl:variable name="s44" select="replace($s43, '&quot;', '\\&quot;')" />

		<xsl:text>"@data-icon":"</xsl:text>
		<xsl:value-of select="$s44" />
		<xsl:text>",</xsl:text>

		<xsl:variable name="s45" select="data-title" />
		<xsl:variable name="s46" select="replace($s45, '&#xa;', '\\n')" />
		<xsl:variable name="s47" select="replace($s46, '&#x9;', '\\t')" />
		<xsl:variable name="s48" select="replace($s47, '&quot;', '\\&quot;')" />

		<xsl:text>"@data-title":"</xsl:text>
		<xsl:value-of select="$s44" />
		<xsl:text>"</xsl:text>

		<xsl:text>}</xsl:text>
		<xsl:if test="not (position() = last())">,</xsl:if>
	</xsl:template>

</xsl:stylesheet>
