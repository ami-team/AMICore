package net.hep.ami.command.root;

import net.hep.ami.command.AbstractCommand;
import net.hep.ami.command.CommandMetadata;
import net.hep.ami.jdbc.Querier;
import net.hep.ami.jdbc.Row;
import net.hep.ami.jdbc.RowSet;
import net.hep.ami.jdbc.reflexion.SchemaSingleton;
import net.hep.ami.utility.parser.Utility;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

		String name = arguments.getOrDefault("name", "???");
		String title = arguments.getOrDefault("title", "???");

		int numberOfBins = arguments.containsKey("numberOfBins") ? Integer.parseInt(arguments.get("numberOfBins")) : 50;

		if(catalog == null || entity == null || (raw == null && sql == null && mql == null))
		{
			throw new Exception("invalid usage");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		Querier querier = getQuerier(catalog);

		RowSet rowSet;

		/**/ if(mql != null) {
			rowSet = querier.executeMQLQuery(catalog, mql);
		}
		else if(sql != null) {
			rowSet = querier.executeMQLQuery(catalog, sql);
		}
		else {
			rowSet = querier.executeRawQuery(catalog, raw);
		}

		if(rowSet.getNumberOfFields() != 1)
		{
			throw new Exception("query must return one field only");
		}

		/*------------------------------------------------------------------------------------------------------------*/

		double val;

		double sum = 0.0;
		double sum2 = 0.0;

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

			sum += val;
			sum2 += val * val;
		}

		/*------------------------------------------------------------------------------------------------------------*/

		double binSize = (xMax - xMin) / numberOfBins;

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
		                                        .append("\"_typename\": \"TH1F\",")
		                                        .append("\"fUniqueID\": \"0\",")
		                                        .append("\"fName\": \"").append(Utility.escapeJSONString(name, false)).append("\",")
		                                        .append("\"fTitle\": \"").append(Utility.escapeJSONString(title, false)).append("\",")
		                                        .append("\"fLineColor\": \"").append(602).append("\",")
		                                        .append("\"fLineStyle\": \"").append(1).append("\",")
		                                        .append("\"fLineWidth\": \"").append(1).append("\",")
		                                        .append("\"fFillColor\": \"").append(48).append("\",")
		                                        .append("\"fFillStyle\": \"").append(1001).append("\",")
		                                        .append("\"fMarkerColor\": \"").append(1).append("\",")
		                                        .append("\"fMarkerStyle\": \"").append(1).append("\",")
		                                        .append("\"fMarkerSize\": \"").append(1.0).append("\",")
		                                        .append("\"fNcells\": ").append(data.size()).append(",")
		                                        .append("\"fXaxis\": {")
		                                        .append(  "\"f_typename\":").append("TAxis").append(",")
		                                        .append(  "\"fName\":").append("xaxis").append(",")
		                                        .append(  "\"fTitle\":").append("X").append(",")
		                                        .append(  "\"fNdivisions\":").append(numberOfBins).append(",")
		                                        .append(  "\"fAxisColor\":").append(1).append(",")
		                                        .append(  "\"fLabelColor\":").append(1).append(",")
		                                        .append(  "\"fLabelFont\":").append(42).append(",")
		                                        .append(  "\"fLabelOffset\":").append(5.0e-03).append(",")
		                                        .append(  "\"fLabelSize\":").append(3.5e-02).append(",")
		                                        .append(  "\"fTickLength\":").append(3.0e-02).append(",")
		                                        .append(  "\"fTitleOffset\":").append(1.0e+00).append(",")
		                                        .append(  "\"fTitleSize\":").append(3.5e-02).append(",")
		                                        .append(  "\"fTitleColor\":").append(1).append(",")
		                                        .append(  "\"fTitleFont\":").append(42).append(",")
		                                        .append(  "\"fNbins\":").append(numberOfBins).append(",")
		                                        .append(  "\"fXmin\":").append(xMin).append(",")
		                                        .append(  "\"fXmax\":").append(xMax).append(",")
		                                        .append(  "\"fXbins\":").append("[]").append(",")
		                                        .append(  "\"fFirst\":").append(0).append(",")
		                                        .append(  "\"fLast\":").append(0).append(",")
		                                        .append(  "\"fTimeDisplay\":").append("false").append(",")
		                                        .append(  "\"fTimeFormat\":").append("\"\"").append(",")
		                                        .append(  "\"fLabels\":").append("null")
		                                        .append("},")
		                                        .append("\"fYaxis\": {")
		                                        .append(  "\"f_typename\":").append("TAxis").append(",")
		                                        .append(  "\"fName\":").append("yaxis").append(",")
		                                        .append(  "\"fTitle\":").append("Y").append(",")
		                                        .append(  "\"fNdivisions\":").append(numberOfBins).append(",")
		                                        .append(  "\"fAxisColor\":").append(1).append(",")
		                                        .append(  "\"fLabelColor\":").append(1).append(",")
		                                        .append(  "\"fLabelFont\":").append(42).append(",")
		                                        .append(  "\"fLabelOffset\":").append(5.0e-03).append(",")
		                                        .append(  "\"fLabelSize\":").append(3.5e-02).append(",")
		                                        .append(  "\"fTickLength\":").append(3.0e-02).append(",")
		                                        .append(  "\"fTitleOffset\":").append(1.0e+00).append(",")
		                                        .append(  "\"fTitleSize\":").append(3.5e-02).append(",")
		                                        .append(  "\"fTitleColor\":").append(1).append(",")
		                                        .append(  "\"fTitleFont\":").append(42).append(",")
		                                        .append(  "\"fNbins\":").append(numberOfBins).append(",")
		                                        .append(  "\"fXmin\":").append(yMin).append(",")
		                                        .append(  "\"fXmax\":").append(yMax).append(",")
		                                        .append(  "\"fXbins\":").append("[]").append(",")
		                                        .append(  "\"fFirst\":").append(0).append(",")
		                                        .append(  "\"fLast\":").append(0).append(",")
		                                        .append(  "\"fTimeDisplay\":").append("false").append(",")
		                                        .append(  "\"fTimeFormat\":").append("\"\"").append(",")
		                                        .append(  "\"fLabels\":").append("null")
		                                        .append("},")
		                                        .append("\"fEntries\":").append(data.size()).append(",")
		                                        .append("\"fTsumw\":").append(sum).append(",")
		                                        .append("\"fTsumw2\":").append(sum2).append(",")
		                                        .append("\"fTsumwx\":").append(0.0).append(",")
		                                        .append("\"fTsumwx2\":").append(0.0).append(",")
		                                        .append("\"fMaximum\":").append(xMax).append(",")
		                                        .append("\"fMinimum\":").append(xMin).append(",")
		                                        .append("\"fNormFactor\":").append(0.0).append(",")
		                                        .append("\"fFunctions\": {")
		                                        .append(  "\"_typename\": \"TList\",")
		                                        .append(  "\"name\": \"TList\",")
		                                        .append(  "\"arr\": [],\"")
		                                        .append(  "\"opt\": []\"")
		                                        .append("},")
		                                        .append("},")
		                                        .append("\"fArray\": [").append(String.join(",")).append("]")
		                                        .append("}")
		;

		/*------------------------------------------------------------------------------------------------------------*/

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
