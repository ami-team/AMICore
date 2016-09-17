<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:output method="text"/>


<xsl:template match="/">AMI#<xsl:apply-templates select="AMIMessage"/>
#</xsl:template>

   <xsl:template match="processName">
   </xsl:template>
   <xsl:template match="databaseName">
   </xsl:template>
   <xsl:template match="sqlForView">
   </xsl:template>
   <xsl:template match="sql">
   </xsl:template>
   <xsl:template match="nbElements">
   </xsl:template>
   <xsl:template match="startIndex">
   </xsl:template>
   <xsl:template match="totalNumberOfElements">
   </xsl:template>
   <xsl:template match="vomsRole"></xsl:template>
   <xsl:template match="executionDate"></xsl:template>
   <xsl:template match="executionTime"></xsl:template>
   <xsl:template match="commandArgs"> <xsl:if test=".=''"></xsl:if></xsl:template>


<xsl:template match="html">
<td><p><b><h2>html : </h2></b></p><p><table border="1"><tr><td><xsl:if test=".=''">
<xsl:text disable-output-escaping="no">&amp;nbsp;</xsl:text>
</xsl:if>

<xsl:copy-of select="."></xsl:copy-of>

</td></tr></table></p></td>
</xsl:template>



<xsl:template match="config">
config  : <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
<xsl:value-of select="."/>
</xsl:template>




<xsl:template match="dumpConfig">
dumpCnf : <xsl:apply-templates select="param"/>
</xsl:template>


<xsl:template match="command">
command : <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
<xsl:value-of select="."/> 
<xsl:if test="@entity!=''">
   -> entity = <xsl:value-of select="@entity"/>
</xsl:if>
</xsl:template>


<xsl:template match="time">
<xsl:if test=". !=''">
time    : <xsl:value-of select="."/>
</xsl:if>
<xsl:if test=". =''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
</xsl:template>

<xsl:template match="info">
info    : <xsl:if test=".!=''">
<xsl:value-of select="."/>
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>

</xsl:template>
<xsl:template match="help">
help    : <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
<xsl:value-of select="."/>
</xsl:template>
<xsl:template match="connection"></xsl:template>
   <!--
<xsl:template match="connection">
access  : 
   -> type = <xsl:value-of select="@type"/>   
   <xsl:if test="@type='database'">
   -> project = <xsl:value-of select="@project"/>
   -> processingStep = <xsl:value-of select="@processingStep"/></xsl:if>
   -> database = <xsl:value-of select="@database"/>
   -> connectionStatus = <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
<xsl:value-of select="."/>
</xsl:template>
   -->
<xsl:template match="query">
   <!-- <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>   -->
<xsl:if test=".!=''">
query   : <xsl:value-of select="."/></xsl:if>
</xsl:template>

<xsl:template match="Result">
result  : <xsl:apply-templates select="rowset"/><xsl:apply-templates select="graph"/>

</xsl:template>

<xsl:template match="entities">
entities: <xsl:apply-templates select="rowset"/>
</xsl:template>

<xsl:template match="associates">
relation: <xsl:apply-templates select="rowset"/>
</xsl:template>

<xsl:template match="rowset">
  -> rowset<xsl:if test="@type and @type!=''"><xsl:text> </xsl:text><xsl:value-of select="@type"/></xsl:if>
<xsl:apply-templates select="row"/>
</xsl:template>



<xsl:template match="graph">
<xsl:apply-templates select="node"/>
<xsl:apply-templates select="edge"/>
</xsl:template>

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


<xsl:template match="node[@totalEvents]">
   -> node name=<xsl:value-of select="@name"/> level=<xsl:value-of select="@level"/>  totalEvents=<xsl:value-of select="@totalEvents"/>
</xsl:template>

<xsl:template match="node">
   -> node name=<xsl:value-of select="@name"/> level=<xsl:value-of select="@level"/> 
</xsl:template>

<xsl:template match="edge">
   -> edge sourceNode=<xsl:value-of select="@sourceNode"/> destNode=<xsl:value-of select="@destNode"/>
</xsl:template>



<xsl:template match="row">
    -> row <xsl:value-of select="@num"/> <xsl:apply-templates select="field"/>
</xsl:template>


<xsl:template match="field">
   <xsl:if test="@name!= 'BROWSE' and @name!= 'AMIELEMENTID' and @name!= 'AMIENTITYNAME'  and @name!= 'PROJECT'  and @name!= 'PROCESS'  ">
      -> <xsl:value-of select="@name"/> = <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
     </xsl:if>
      
<xsl:variable name="myString"><xsl:copy-of select="."></xsl:copy-of></xsl:variable>

<xsl:call-template name="break">
<xsl:with-param name="text" select="$myString"/>
</xsl:call-template>


   </xsl:if>
</xsl:template>
<xsl:template match="version">
version : <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
<xsl:value-of select="."/>
</xsl:template>
<xsl:template match="commandStatus">
status  : <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
<xsl:value-of select="."/>
</xsl:template>
<xsl:template match="error">
error   : <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes"></xsl:text>
</xsl:if>
<xsl:value-of select="."/>
</xsl:template>
<xsl:template match="dumpCommand">
dumpCmd : <xsl:apply-templates select="param"/>
</xsl:template>
<xsl:template match="param">
   -> <xsl:value-of select="@name"/> = <xsl:if test=".=''">
<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
</xsl:if>
<xsl:value-of select="."/>
</xsl:template>
<xsl:template match=
"text()[not(string-length(normalize-space()))]"/>
<xsl:template match=
"text()[string-length(normalize-space()) > 0]">
  <xsl:value-of select="translate(.,'&#xA;&#xD;', '  ')"/>
</xsl:template>
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