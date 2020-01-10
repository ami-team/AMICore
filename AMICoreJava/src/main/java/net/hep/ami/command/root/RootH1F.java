package net.hep.ami.command.root;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
import net.hep.ami.utility.*;
import net.hep.ami.utility.parser.*;

import org.jetbrains.annotations.*;

@CommandMetadata(role = "AMI_USER", visible = true, secured = false)
public class RootH1F extends AbstractCommand
{
	/*----------------------------------------------------------------------------------------------------------------*/

	public RootH1F(@NotNull Set<String> userRoles, @NotNull Map<String, String> arguments, long transactionId)
	{
		super(userRoles, arguments, transactionId);
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Override
	public StringBuilder main(@NotNull Map<String, String> arguments) throws Exception
	{
		StringBuilder result = new StringBuilder();

		String catalog = arguments.get("catalog");
		String entity = arguments.get("entity");

		String raw = arguments.get("raw");
		String sql = arguments.get("sql");
		String mql = arguments.get("mql");

		String title = arguments.getOrDefault("title", "");

		String xTitle = arguments.getOrDefault("xTitle", "");
		String yTitle = arguments.getOrDefault("yTitle", "");

		Double _xMin = arguments.containsKey("xMin") ? Double.valueOf(arguments.get("xMin")) : null;
		Double _xMax = arguments.containsKey("xMax") ? Double.valueOf(arguments.get("xMax")) : null;

		Double _yMin = arguments.containsKey("yMin") ? Double.valueOf(arguments.get("yMin")) : null;
		Double _yMax = arguments.containsKey("yMax") ? Double.valueOf(arguments.get("yMax")) : null;

		Double sizeOfBins = arguments.containsKey("sizeOfBins") ? Double.parseDouble(arguments.get("sizeOfBins")) : null;

		if(catalog == null || entity == null || (raw == null && sql == null && mql == null))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		RowSet rowSet;

		Querier querier = getQuerier(catalog);

		/**/ if(mql != null) {
			rowSet = querier.executeMQLQuery(entity, mql);
		}
		else if(sql != null) {
			rowSet = querier.executeSQLQuery(entity, sql);
		}
		else {
			rowSet = querier.executeRawQuery(entity, raw);
		}

		if(rowSet.getNumberOfFields() != 1)
		{
			throw new Exception("query must return one field only");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		String name = rowSet.getLabelOfField(0);

		/*------------------------------------------------------------------------------------------------------------*/

		double val;

		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;

		List<Double> data = new ArrayList<>();

		for(Row row: rowSet.iterate())
		{
			val = row.getValue(0, 0.0);

			if(xMin > val) {
				xMin = val;
			}

			if(xMax < val) {
				xMax = val;
			}

			data.add(val);
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(_xMin == null) {
			xMin = Math.floor(xMin);
		}
		else {
			xMin = _xMin;
		}

		if(_xMax == null)
		{
			xMax = Math.ceil(xMax);
		}
		else {
			xMax = _xMax;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(sizeOfBins == null)
		{
			BigDecimal[] data2 = data.stream().sorted().map(BigDecimal::valueOf).toArray(BigDecimal[]::new);

			double iqr = Stats.quartile3(data2).subtract(Stats.quartile1(data2)).abs().doubleValue();

			/**/ if(iqr > 0.0)
			{
				sizeOfBins = 2.0 * iqr / Math.pow(data2.length, 1.0 / 3.0);

				sizeOfBins = Math.ceil(sizeOfBins);
			}
			else
			{
				if(xMin != xMax)
				{
					sizeOfBins = (xMax - xMin) / 80.0;
				}
				else
				{
					sizeOfBins = 1.0;
				}
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		int numberOfBins = (int) ((xMax - xMin + sizeOfBins) / sizeOfBins);

		int[] bins = new int[1 + numberOfBins + 1];

		Arrays.fill(bins, 0);

		for(double x: data)
		{
			/**/ if(x < xMin) {
				bins[0x00000000000000]++;
			}
			else if(x > xMax) {
				bins[numberOfBins + 1]++;
			}
			else {
				bins[1 + (int) ((x - xMin) / sizeOfBins)]++;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		double yMin = 0.0;
		double yMax = 0.0;

		for(double bin: bins)
		{
			if(yMin > bin) {
				yMin = bin;
			}

			if(yMax < bin) {
				yMax = bin;
			}
		}

		/*------------------------------------------------------------------------------------------------------------*/

		if(_yMin != null) {
			yMin = _yMin;
		}

		if(_yMax != null) {
			yMax = _yMax;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		StringBuilder json = new StringBuilder().append("{")
		                                        .append("\"_typename\":\"TH1F\",")
		                                        .append("\"fUniqueID\": ").append(0).append(",")
		                                        .append("\"fBits\": ").append(50331656).append(",")
		                                        .append("\"fName\": \"").append(Utility.escapeJSONString(name, false)).append("\",")
		                                        .append("\"fTitle\": \"").append(Utility.escapeJSONString(title, false)).append("\",")
		                                        .append("\"fLineColor\": \"").append(602).append("\",")
		                                        .append("\"fLineStyle\": \"").append(1).append("\",")
		                                        .append("\"fLineWidth\": \"").append(1).append("\",")
		                                        .append("\"fFillColor\": \"").append(7).append("\",")
		                                        .append("\"fFillStyle\": \"").append(3144).append("\",")
		                                        .append("\"fMarkerColor\": \"").append(1).append("\",")
		                                        .append("\"fMarkerStyle\": \"").append(1).append("\",")
		                                        .append("\"fMarkerSize\": \"").append(1.0).append("\",")
		                                        .append("\"fNcells\": ").append(numberOfBins).append(",")
		                                        .append("\"fXaxis\": {")
		                                        .append(  "\"_typename\": \"").append("TAxis").append("\",")
		                                        .append(  "\"fUniqueID\": ").append(0).append(",")
		                                        .append(  "\"fBits\": ").append(50331648).append(",")
		                                        .append(  "\"fName\": \"").append("xaxis").append("\",")
		                                        .append(  "\"fTitle\": \"").append(Utility.escapeJSONString(xTitle, false)).append("\",")
		                                        .append(  "\"fNdivisions\": ").append(510).append(",")
		                                        .append(  "\"fAxisColor\": ").append(1).append(",")
		                                        .append(  "\"fLabelColor\": ").append(1).append(",")
		                                        .append(  "\"fLabelFont\": ").append(42).append(",")
		                                        .append(  "\"fLabelOffset\": ").append(5.0e-03).append(",")
		                                        .append(  "\"fLabelSize\": ").append(3.5e-02).append(",")
		                                        .append(  "\"fTickLength\": ").append(3.0e-02).append(",")
		                                        .append(  "\"fTitleOffset\": ").append(1.0e+00).append(",")
		                                        .append(  "\"fTitleSize\": ").append(3.5e-02).append(",")
		                                        .append(  "\"fTitleColor\": ").append(1).append(",")
		                                        .append(  "\"fTitleFont\": ").append(42).append(",")
		                                        .append(  "\"fNbins\": ").append(numberOfBins).append(",")
		                                        .append(  "\"fXmin\": ").append(xMin + 0.00000000).append(",")
		                                        .append(  "\"fXmax\": ").append(xMax + sizeOfBins).append(",")
		                                        .append(  "\"fXbins\": [").append("]")
		                                        .append("},")
		                                        .append("\"fYaxis\": {")
		                                        .append(  "\"_typename\": \"").append("TAxis").append("\",")
		                                        .append(  "\"fUniqueID\": ").append(0).append(",")
		                                        .append(  "\"fBits\": ").append(50331648).append(",")
		                                        .append(  "\"fName\": \"").append("yaxis").append("\",")
		                                        .append(  "\"fTitle\": \"").append(Utility.escapeJSONString(yTitle, false)).append("\",")
		                                        .append(  "\"fNdivisions\": ").append(510).append(",")
		                                        .append(  "\"fAxisColor\": ").append(1).append(",")
		                                        .append(  "\"fLabelColor\": ").append(1).append(",")
		                                        .append(  "\"fLabelFont\": ").append(42).append(",")
		                                        .append(  "\"fLabelOffset\": ").append(5.0e-03).append(",")
		                                        .append(  "\"fLabelSize\": ").append(3.5e-02).append(",")
		                                        .append(  "\"fTickLength\": ").append(3.0e-02).append(",")
		                                        .append(  "\"fTitleOffset\": ").append(1.0e+00).append(",")
		                                        .append(  "\"fTitleSize\": ").append(3.5e-02).append(",")
		                                        .append(  "\"fTitleColor\": ").append(1).append(",")
		                                        .append(  "\"fTitleFont\": ").append(42).append(",")
		                                        .append(  "\"fNbins\": ").append(1).append(",")
		                                        .append(  "\"fXmin\": ").append(yMin).append(",")
		                                        .append(  "\"fXmax\": ").append(yMax).append(",")
		                                        .append(  "\"fXbins\": [").append("]")
		                                        .append("},")
		                                        .append("\"fMinimum\":").append(yMin).append(",")
		                                        .append("\"fMaximum\":").append(1.1 * yMax).append(",")
		                                        .append("\"fSumw2\": [").append("],")
		                                        .append("\"fArray\": [").append(Arrays.stream(bins).mapToObj(Double::toString).collect(Collectors.joining(","))).append("]")
		                                        .append("}")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<fieldDescriptions rowset=\"hist\">")
		      .append(  "<fieldDescription")
		      .append(  " catalog=\"N/A\"")
		      .append(  " entity=\"N/A\"")
		      .append(  " field=\"json\"")
		      .append(  " label=\"json\"")
		      .append(  " type=\"VARCHAR\"")
		      .append(  " displayable=\"true\"")
		      .append(  " base64=\"false\"")
		      .append(  " mime=\"application/json\"")
		      .append(  " ctrl=\"Root\"><![CDATA[H1F]]></fieldDescription>")
		      .append("</fieldDescriptions>")
		;

		result.append("<rowset type=\"hist\">")
		      .append("<row>")
		      .append("<field name=\"xMin\"><![CDATA[").append(xMin).append("]]></field>")
		      .append("<field name=\"xMax\"><![CDATA[").append(xMax).append("]]></field>")
		      .append("<field name=\"yMin\"><![CDATA[").append(yMin).append("]]></field>")
		      .append("<field name=\"yMax\"><![CDATA[").append(yMax).append("]]></field>")
		      .append("<field name=\"sizeOfBins\"><![CDATA[").append(sizeOfBins).append("]]></field>")
		      .append("<field name=\"numberOfBins\"><![CDATA[").append(numberOfBins).append("]]></field>")
		      .append("<field name=\"json\"><![CDATA[").append(json).append("]]></field>")
		      .append("</row>")
		      .append("</rowset>")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		return result;
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String help()
	{
		return "Generate a 1-dimension ROOT histogram.";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-raw=\"\" | -sql=\"\" | -mql=\"\") -name=\"\" (-title=\"\")? (-xTitle=\"\")? (-yTitle=\"\")? (-xMin=\"\") (-xMax=\"\") (-yMin=\"\") (-yMax=\"\") (-sizeOfBins=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
