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

		String xTitle = arguments.getOrDefault("xTitle", "");
		String yTitle = arguments.getOrDefault("yTitle", "");

		int numberOfBins = arguments.containsKey("numberOfBins") ? Integer.parseInt(arguments.get("numberOfBins")) : 25;

		if(catalog == null || entity == null || (raw == null && sql == null && mql == null))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		RowSet rowSet;

		/**/ if(mql != null) {
			rowSet = querier.executeMQLQuery(entity, mql);
		}
		else if(sql != null) {
			rowSet = querier.executeMQLQuery(entity, sql);
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
		String title = rowSet.getLabelOfField(0);

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

		double binSize = (xMax - xMin + 1) / numberOfBins;

		double[] bins = new double[numberOfBins];

		Arrays.fill(bins, 0.0);

		for(double x: data)
		{
			bins[(int) ((x - xMin) / binSize)]++;
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
		                                        .append("\"fMaximum\":").append(1.1 * yMax).append(",")
		                                        .append("\"fMinimum\":").append(1.1 * yMin).append(",")
		                                        .append(  "\"fSumw2\": [").append("],")
		                                        .append("\"fArray\": [0.0,").append(Arrays.stream(bins).mapToObj(x -> Double.toString(x)).collect(Collectors.joining(","))).append(",0.0]")
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
		return ".";
	}

	/*----------------------------------------------------------------------------------------------------------------*/

	@NotNull
	@Contract(pure = true)
	public static String usage()
	{
		return "-catalog=\"\" -entity=\"\" (-raw=\"\" | -sql=\"\" | -mql=\"\") -name=\"\" -title=\"\"";
	}

	/*----------------------------------------------------------------------------------------------------------------*/
}
