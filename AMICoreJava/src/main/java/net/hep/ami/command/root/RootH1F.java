package net.hep.ami.command.root;

import java.util.*;
import java.util.stream.*;

import net.hep.ami.jdbc.*;
import net.hep.ami.command.*;
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

		int numberOfBins = arguments.containsKey("numberOfBins") ? Integer.parseInt(arguments.get("numberOfBins")) : 50;

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

		double xMin = 0.0;
		double xMax = 0.0;

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

		if(_xMin != null && xMin < _xMin) {
			xMin = _xMin;
		}

		if(_xMax != null && xMax < _xMax) {
			xMax = _xMax;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		double binSize = (xMax - xMin + 1) / numberOfBins;

		double[] bins = new double[1 + numberOfBins + 1];

		Arrays.fill(bins, 0.0);

		for(double x: data)
		{
			/**/ if(x < xMin) {
				bins[0x00000000000000]++;
			}
			else if(x > xMax) {
				bins[numberOfBins + 1]++;
			}
			else {
				bins[(int) ((x - xMin) / binSize) + 1]++;
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

		yMax *= 1.1;

		/*------------------------------------------------------------------------------------------------------------*/

		if(_yMin != null && yMin < _yMin) {
			yMin = _yMin;
		}

		if(_yMax != null && yMax < _yMax) {
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
		                                        .append("\"fNcells\": ").append(bins.length).append(",")
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
		                                        .append(  "\"fXmin\": ").append(xMin).append(",")
		                                        .append(  "\"fXmax\": ").append(xMax).append(",")
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
		                                        .append("\"fMaximum\":").append(yMax).append(",")
		                                        .append(  "\"fSumw2\": [").append("],")
		                                        .append("\"fArray\": [").append(Arrays.stream(bins).mapToObj(Double::toString).collect(Collectors.joining(","))).append("]")
		                                        .append("}")
		;

		/*------------------------------------------------------------------------------------------------------------*/

		result.append("<fieldDescriptions rowset=\"root_file\">")
		      .append(  "<fieldDescription")
		      .append(  " catalog=\"N/A\"")
		      .append(  " entity=\"N/A\"")
		      .append(  " field=\"json\"")
		      .append(  " label=\"json\"")
		      .append(  " type=\"VARCHAR\"")
		      .append(  " displayable=\"true\"")
		      .append(  " base64=\"false\"")
		      .append(  " mime=\"application/json\"")
		      .append(  " ctrl=\"RootCtrl\"><![CDATA[TH1]]></fieldDescription>")
		      .append("</fieldDescriptions>")
		;

		result.append("<rowset type=\"root_file\">")
		      .append("<row><field name=\"json\"><![CDATA[").append(json).append("]]></field></row>")
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
		return "-catalog=\"\" -entity=\"\" (-raw=\"\" | -sql=\"\" | -mql=\"\") -name=\"\" (-title=\"\")? (-xTitle=\"\")? (-yTitle=\"\")? (-xMin=\"\") (-xMax=\"\") (-yMin=\"\") (-yMax=\"\") (-numberOfBins=\"\")?";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
