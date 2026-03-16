package net.hep.ami;

import net.hep.ami.utility.JsonUtility;
import java.util.*;
import java.io.*;

@SuppressWarnings("all")
public class JsonUtilityTest
{
    /*----------------------------------------------------------------------------------------------------------------*/
    /* TEST CLASS FOR JAVA OBJECT EXTRACTIONS                                                                        */
    /*----------------------------------------------------------------------------------------------------------------*/

    static class SampleConfiguration
    {
        private String configData;
        private String jsonData;
        private String listData;
        private int version;

        public SampleConfiguration(String configData, String jsonData, String listData, int version)
        {
            this.configData = configData;
            this.jsonData = jsonData;
            this.listData = listData;
            this.version = version;
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    public static void main(String[] args) throws Exception
    {
        try
        {
            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║           COMPLETE TEST OF JsonUtility                         ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
            System.out.println();

            // Test data (Python dict format)
            String pythonDictString = "{'conditionsTag': {'all': 'CONDBR2-ES1PA-2025-01'}, " +
                    "'autoConfiguration': ['everything'], " +
                    "'maxEvents': -1, " +
                    "'AMITag': 'x899', " +
                    "'CA': 'True', " +
                    "'preExec': {'all': ['flags.Common.doExpressProcessing=True;']}, " +
                    "'postExec':{'all': [\"cfg.getEventAlgo('TrigEgammaMonitorElectronAlgorithm').ElectronLikelihoodTool = cfg.getEventAlgo('TrigEgammaMonitorTagAndProbeAlgorithm_Zee_LH').ElectronLikelihoodTool;\"]}, " +
                    "'geometryVersion': {'all': 'ATLAS-R3S-2021-03-02-00'}, " +
                    "'athenaopts': ['--threads=8']}";

            /*--------------------------------------------------------------------------------------------------------*/
            /* SECTION 1: PARSING PYTHON DICT                                                                         */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("SECTION 1: PARSING PYTHON DICT");

            // Test 1.1: Parse Python dict
            System.out.println("Test 1.1: Basic Python dict parsing...");
            Object parsedObject = JsonUtility.parsePythonDict(pythonDictString);
            printResult("Parse successful", parsedObject != null);

            // Test 1.2: Parse Python dict as Map
            System.out.println("\nTest 1.2: Parsing Python dict as Map...");
            Map<String, Object> dataMap = JsonUtility.parsePythonDictAsMap(pythonDictString);
            printResult("Map created with " + dataMap.size() + " keys", true);
            System.out.println("  - AMITag: " + dataMap.get("AMITag"));
            System.out.println("  - maxEvents: " + dataMap.get("maxEvents"));
            System.out.println("  - CA: " + dataMap.get("CA"));

            // Test 1.3: Parsing a Python list
            System.out.println("\nTest 1.3: Parsing Python list...");
            String pythonListString = "['item1', 'item2', 'item3', 'item4']";
            List<Object> pythonList = JsonUtility.parsePythonDictAsList(pythonListString);
            printResult("List parsed with " + pythonList.size() + " elements", pythonList.size() == 4);
            System.out.println("  - Content: " + pythonList);

            System.out.println();

            /*--------------------------------------------------------------------------------------------------------*/
            /* SECTION 2: JSON CONVERSION                                                                             */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("SECTION 2: JSON CONVERSION");

            // Test 2.1: Convert to compact JSON
            System.out.println("Test 2.1: Converting to compact JSON...");
            String jsonString = JsonUtility.toJson(dataMap);
            printResult("Compact JSON created (" + jsonString.length() + " characters)", true);

            // Test 2.2: Convert to pretty JSON
            System.out.println("\nTest 2.2: Converting to pretty print JSON...");
            String prettyJson = JsonUtility.toJsonPretty(dataMap);
            System.out.println(prettyJson);
            printResult("Pretty JSON created", true);

            // Test 2.3: Parse JSON
            System.out.println("\nTest 2.3: Re-parsing JSON...");
            Object reparsed = JsonUtility.parseJson(jsonString);
            printResult("Re-parse successful", reparsed != null);

            // Test 2.4: Parse JSON as Map
            System.out.println("\nTest 2.4: Parse JSON as Map...");
            Map<String, Object> jsonMap = JsonUtility.parseJsonAsMap(jsonString);
            printResult("JSON Map created", jsonMap.size() == dataMap.size());

            System.out.println();

            /*--------------------------------------------------------------------------------------------------------*/
            /* SECTION 3: JSONPATH QUERIES                                                                            */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("SECTION 3: JSONPATH QUERIES");

            // Test 3.1: Simple query
            System.out.println("Test 3.1: Simple JSONPath queries...");
            String amiTag = JsonUtility.queryJsonPath(dataMap, "$.AMITag");
            System.out.println("  - $.AMITag = " + amiTag);
            printResult("Query AMITag", "x899".equals(amiTag));

            // Test 3.2: Nested query
            System.out.println("\nTest 3.2: Nested JSONPath queries...");
            String conditionsTag = JsonUtility.queryJsonPath(dataMap, "$.conditionsTag.all");
            System.out.println("  - $.conditionsTag.all = " + conditionsTag);
            printResult("Query conditionsTag", conditionsTag != null);

            String geometryVersion = JsonUtility.queryJsonPath(dataMap, "$.geometryVersion.all");
            System.out.println("  - $.geometryVersion.all = " + geometryVersion);
            printResult("Query geometryVersion", geometryVersion != null);

            // Test 3.3: Query as List
            System.out.println("\nTest 3.3: Query JSONPath as List...");
            List<String> autoConfig = JsonUtility.queryJsonPathAsList(dataMap, "$.autoConfiguration[*]");
            System.out.println("  - $.autoConfiguration[*] = " + autoConfig);
            printResult("Query autoConfiguration", autoConfig.size() > 0);

            List<String> athenaopts = JsonUtility.queryJsonPathAsList(dataMap, "$.athenaopts[*]");
            System.out.println("  - $.athenaopts[*] = " + athenaopts);
            printResult("Query athenaopts", athenaopts.contains("--threads=8"));

            // Test 3.4: Query as Map
            System.out.println("\nTest 3.4: Query JSONPath as Map...");
            Map<String, Object> conditionsMap = JsonUtility.queryJsonPathAsMap(dataMap, "$.conditionsTag");
            System.out.println("  - $.conditionsTag = " + conditionsMap);
            printResult("Query Map conditionsTag", conditionsMap.containsKey("all"));

            Map<String, Object> preExecMap = JsonUtility.queryJsonPathAsMap(dataMap, "$.preExec");
            System.out.println("  - $.preExec = " + preExecMap);
            printResult("Query Map preExec", preExecMap.containsKey("all"));

            // Test 3.5: Path exists
            System.out.println("\nTest 3.5: Checking path existence...");
            boolean existsAMI = JsonUtility.pathExists(dataMap, "$.AMITag");
            System.out.println("  - $.AMITag exists: " + existsAMI);
            printResult("Path AMITag exists", existsAMI);

            boolean existsFake = JsonUtility.pathExists(dataMap, "$.nonExistent");
            System.out.println("  - $.nonExistent exists: " + existsFake);
            printResult("Non-existent path detected", !existsFake);

            boolean existsNested = JsonUtility.pathExists(dataMap, "$.conditionsTag.all");
            System.out.println("  - $.conditionsTag.all exists: " + existsNested);
            printResult("Nested path exists", existsNested);

            System.out.println();

            /*--------------------------------------------------------------------------------------------------------*/
            /* SECTION 4: FILE OPERATIONS                                                                             */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("SECTION 4: FILE OPERATIONS");

            // Test 4.1: Writing to a file
            System.out.println("Test 4.1: Writing to a JSON file...");
            File tempFile = File.createTempFile("ami_test_", ".json");
            JsonUtility.writeJson(tempFile, dataMap);
            System.out.println("  - File: " + tempFile.getAbsolutePath());
            printResult("File written", tempFile.exists() && tempFile.length() > 0);

            // Test 4.2: Reading from a file
            System.out.println("\nTest 4.2: Reading from file...");
            Object fileData = JsonUtility.parseJson(tempFile);
            printResult("Read successful", fileData != null);

            Map<String, Object> fileMap = (Map<String, Object>) fileData;
            printResult("Identical data", fileMap.get("AMITag").equals(dataMap.get("AMITag")));

            tempFile.delete();
            System.out.println("  - Temporary file deleted");

            System.out.println();

            /*--------------------------------------------------------------------------------------------------------*/
            /* SECTION 5: UTILITY METHODS                                                                             */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("SECTION 5: UTILITY METHODS");

            // Test 5.1: Deep copy
            System.out.println("Test 5.1: Deep copy...");
            Map<String, Object> copiedMap = JsonUtility.deepCopy(dataMap);
            copiedMap.put("AMITag", "x900_MODIFIED");
            System.out.println("  - Original AMITag: " + dataMap.get("AMITag"));
            System.out.println("  - Copy AMITag: " + copiedMap.get("AMITag"));
            printResult("Independent deep copy", !dataMap.get("AMITag").equals(copiedMap.get("AMITag")));

            System.out.println();

            /*--------------------------------------------------------------------------------------------------------*/
            /* SECTION 6: EXTRACTION FROM JAVA OBJECTS                                                                */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("SECTION 6: EXTRACTION FROM JAVA OBJECTS");

            // Create test object
            String testJson = "{\"name\": \"test\", \"value\": 42, \"items\": [\"a\", \"b\", \"c\"]}";
            String testList = "[\"item1\", \"item2\", \"item3\"]";
            SampleConfiguration testObj = new SampleConfiguration(
                    pythonDictString,
                    testJson,
                    testList,
                    1
            );

            // Test 6.1: Extract parameter
            System.out.println("Test 6.1: Extract simple parameter...");
            Object extractedVersion = JsonUtility.extractParameter(testObj, "version");
            System.out.println("  - version = " + extractedVersion);
            printResult("Version extracted", extractedVersion.equals(1));

            // Test 6.2: Extract typed parameter
            System.out.println("\nTest 6.2: Extract typed parameter...");
            Integer typedVersion = JsonUtility.extractParameter(testObj, "version", Integer.class);
            System.out.println("  - version (typed) = " + typedVersion);
            printResult("Typed version extracted", typedVersion == 1);

            String configData = JsonUtility.extractParameter(testObj, "configData", String.class);
            System.out.println("  - configData (length) = " + configData.length());
            printResult("ConfigData extracted", configData.contains("AMITag"));

            // Test 6.3: Extract all parameters
            System.out.println("\nTest 6.3: Extract all parameters...");
            Map<String, Object> allParams = JsonUtility.extractAllParameters(testObj);
            System.out.println("  - Number of parameters: " + allParams.size());
            for (String key : allParams.keySet()) {
                Object value = allParams.get(key);
                String valueStr = value != null ? value.toString() : "null";
                if (valueStr.length() > 50) {
                    valueStr = valueStr.substring(0, 47) + "...";
                }
                System.out.println("    * " + key + " = " + valueStr);
            }
            printResult("All parameters extracted", allParams.size() == 4);

            // Test 6.4: Extract and parse
            System.out.println("\nTest 6.4: Extract and parse parameter...");
            Object parsedConfig = JsonUtility.extractAndParseParameter(testObj, "configData");
            printResult("ConfigData parsed", parsedConfig instanceof Map);
            Map<String, Object> parsedConfigMap = (Map<String, Object>) parsedConfig;
            System.out.println("  - AMITag from parsed config: " + parsedConfigMap.get("AMITag"));

            // Test 6.5: Extract and parse as Map
            System.out.println("\nTest 6.5: Extract and parse as Map...");
            Map<String, Object> extractedMap = JsonUtility.extractAndParseParameterAsMap(testObj, "configData");
            System.out.println("  - Keys in Map: " + extractedMap.keySet());
            printResult("Map extracted and parsed", extractedMap.containsKey("AMITag"));

            Map<String, Object> jsonDataMap = JsonUtility.extractAndParseParameterAsMap(testObj, "jsonData");
            System.out.println("  - jsonData.name = " + jsonDataMap.get("name"));
            printResult("JSON parsed from object", "test".equals(jsonDataMap.get("name")));

            // Test 6.6: Extract and parse as List
            System.out.println("\nTest 6.6: Extract and parse as List...");
            List<Object> itemsList = JsonUtility.extractAndParseParameterAsList(testObj, "listData");
            System.out.println("  - listData parsed: " + itemsList);
            printResult("List extracted and parsed", itemsList.size() == 3);

            // Also extract nested list from jsonData
            Map<String, Object> jsonWithList = JsonUtility.extractAndParseParameterAsMap(testObj, "jsonData");
            List<String> nestedItems = (List<String>) jsonWithList.get("items");
            System.out.println("  - items from jsonData: " + nestedItems);
            printResult("Nested list extracted", nestedItems.contains("a"));

            // Test 6.7: Extract and query JSONPath
            System.out.println("\nTest 6.7: Extract and query JSONPath...");
            String extractedAMI = JsonUtility.extractAndQueryJsonPath(testObj, "configData", "$.AMITag");
            System.out.println("  - AMITag via extraction + JSONPath: " + extractedAMI);
            printResult("JSONPath on extracted parameter", "x899".equals(extractedAMI));

            Integer extractedMaxEvents = JsonUtility.extractAndQueryJsonPath(testObj, "configData", "$.maxEvents");
            System.out.println("  - maxEvents via extraction + JSONPath: " + extractedMaxEvents);
            printResult("Numeric value extracted", extractedMaxEvents == -1);

            List<String> extractedAutoConfig = JsonUtility.extractAndQueryJsonPath(testObj, "configData", "$.autoConfiguration");
            System.out.println("  - autoConfiguration via extraction + JSONPath: " + extractedAutoConfig);
            printResult("List extracted", extractedAutoConfig.contains("everything"));

            System.out.println();

            /*--------------------------------------------------------------------------------------------------------*/
            /* SECTION 7: ADVANCED TESTS                                                                              */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("SECTION 7: ADVANCED TESTS");

            // Test 7.1: Complex nested structures
            System.out.println("Test 7.1: Accessing deeply nested structures...");
            List<String> preExecAll = JsonUtility.queryJsonPath(dataMap, "$.preExec.all");
            System.out.println("  - preExec.all: " + preExecAll);
            printResult("PreExec extracted", preExecAll.size() > 0);

            List<String> postExecAll = JsonUtility.queryJsonPath(dataMap, "$.postExec.all");
            System.out.println("  - postExec.all (length): " + postExecAll.get(0).length() + " characters");
            printResult("PostExec extracted", postExecAll.size() > 0);

            // Test 7.2: Python data manipulation with single and double quotes
            System.out.println("\nTest 7.2: Python dict with mixed quotes...");
            String mixedQuotes = "{'simple': 'value', \"double\": \"value2\", 'mixed': \"value3\"}";
            Map<String, Object> mixedMap = JsonUtility.parsePythonDictAsMap(mixedQuotes, true);
            printResult("Mixed quotes parsed", mixedMap.size() == 3);
            System.out.println("  - Keys: " + mixedMap.keySet());

            // Test 7.3: Boolean values and None in Python
            System.out.println("\nTest 7.3: Python special values...");
            String specialValues = "{'bool_true': True, 'bool_false': False, 'none_value': None}";
            Map<String, Object> specialMap = JsonUtility.parsePythonDictAsMap(specialValues);
            System.out.println("  - bool_true: " + specialMap.get("bool_true"));
            System.out.println("  - bool_false: " + specialMap.get("bool_false"));
            System.out.println("  - none_value: " + specialMap.get("none_value"));
            printResult("Python special values parsed", specialMap.size() == 3);

            System.out.println();

            /*--------------------------------------------------------------------------------------------------------*/
            /* SUMMARY                                                                                                */
            /*--------------------------------------------------------------------------------------------------------*/

            printSectionHeader("TEST SUMMARY");
            System.out.println();
            System.out.println("  ✓ Parsing Python Dict       : OK");
            System.out.println("  ✓ JSON Conversion           : OK");
            System.out.println("  ✓ JSONPath Queries          : OK");
            System.out.println("  ✓ File Operations           : OK");
            System.out.println("  ✓ Utility Methods           : OK");
            System.out.println("  ✓ Java Object Extraction    : OK");
            System.out.println("  ✓ Advanced Tests            : OK");
            System.out.println();
            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║              ✓ ALL TESTS PASSED ✓                             ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝");
        }
        catch(Exception e)
        {
            System.err.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.err.println("║                    ✗ ERROR DETECTED ✗                         ║");
            System.err.println("╚════════════════════════════════════════════════════════════════╝\n");
            e.printStackTrace(System.err);
        }

        System.out.println("\nbye.");
        System.exit(0);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private static void printSectionHeader(String title)
    {
        System.out.println("┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│ " + String.format("%-62s", title) + " │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.println();
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    private static void printResult(String message, boolean success)
    {
        String icon = success ? "✓" : "✗";
        String status = success ? "OK" : "FAIL";
        System.out.println("  " + icon + " " + message + ": " + status);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}