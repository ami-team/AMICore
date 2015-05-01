<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/"><xsl:apply-templates select="AMIMessage/Result"/><xsl:apply-templates select="AMIMessage/error"/></xsl:template>
<xsl:output method="text"/>
<xsl:template match="//error">#ERROR
<xsl:copy-of select="."></xsl:copy-of>
<xsl:text disable-output-escaping="yes">
</xsl:text>
</xsl:template>
<xsl:template match="//info">#INFO
<xsl:copy-of select="."></xsl:copy-of>
<xsl:text disable-output-escaping="yes">
</xsl:text>
</xsl:template>
<xsl:template match="//Result">#AMI RESULT<xsl:apply-templates select="rowset"/></xsl:template>  
<xsl:template match="rowset">
#ROWSET <xsl:value-of select="@type"/>
#FIELDS
<xsl:for-each select="./row[1]/field"><xsl:if test="@name!='BROWSE'"><xsl:value-of select="@name"/><xsl:if test="not (position() = last())">;</xsl:if></xsl:if></xsl:for-each>
#VALUES
<xsl:apply-templates select="row"/></xsl:template>
<xsl:template match="row"><xsl:apply-templates select="field"/><xsl:text disable-output-escaping="yes">
</xsl:text>
</xsl:template>
<xsl:template match="field"><xsl:if test="@name!= 'BROWSE' and @name!= 'AMIELEMENTID' and @name!= 'AMIENTITYNAME'  and @name!= 'PROJECT'  and @name!= 'PROCESS'  "><xsl:if test=".=''"><xsl:text disable-output-escaping="yes"></xsl:text></xsl:if>
<xsl:variable name="myString"><xsl:copy-of select="."></xsl:copy-of></xsl:variable>
<xsl:call-template name="break">
<xsl:with-param name="text" select="$myString"/>
</xsl:call-template><xsl:if test="not (position() = last())">;</xsl:if></xsl:if></xsl:template>
<xsl:template match="fieldDescriptions"></xsl:template>
<xsl:template match="fieldDescriptions"></xsl:template>
<xsl:template match="gLiteCondition"></xsl:template>
<xsl:template match="gLiteEntity"></xsl:template>
<xsl:template match="gLiteQueryForView"></xsl:template>
<xsl:template match="gLiteQuery"></xsl:template>
<xsl:template match="processName"></xsl:template>
<xsl:template match="projectName"></xsl:template>
<xsl:template match="sql"></xsl:template>
<xsl:template match="nbElements"></xsl:template>
<xsl:template match="startIndex"></xsl:template>
<xsl:template match="totalNumberOfElements"></xsl:template>
<xsl:template match="databaseName"></xsl:template>
<xsl:template match="sqlForView"></xsl:template>
<xsl:template match="html"></xsl:template>
<xsl:template match="config"></xsl:template>
<xsl:template match="dumpConfig"></xsl:template>
<xsl:template match="command"></xsl:template>
<xsl:template match="time"></xsl:template>
<xsl:template match="help"></xsl:template>
<xsl:template match="connection"></xsl:template>
<xsl:template match="query"></xsl:template>
<xsl:template match="entities"></xsl:template>
<xsl:template match="associates"></xsl:template>
<xsl:template match="version"></xsl:template>
<xsl:template match="commandStatus"></xsl:template>
<xsl:template match="dumpCommand"></xsl:template>
<xsl:template match="param"></xsl:template>
<xsl:template match="graph"></xsl:template>
<xsl:template match="executionTime"></xsl:template>
<xsl:template name="break">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="contains($text, '&#xa;')">
				<xsl:value-of select="substring-before($text, '&#xa;')"/>\n<xsl:call-template name="break">
					<xsl:with-param name="text" select="substring-after($text,
					'&#xa;')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text"/>
				</xsl:otherwise>
		</xsl:choose>
		</xsl:template>
</xsl:stylesheet>