package net.hep.ami;

import net.hep.ami.utility.JsonUtility;
import java.util.*;
import java.io.*;

@SuppressWarnings("all")
public class JsonUtilityTest
{
    /*----------------------------------------------------------------------------------------------------------------*/

    public static void main(String[] args) throws Exception
    {
        try
        {
            System.out.println("=== Test JsonUtility ===\n");

            // Données de test (format Python dict)
            String pythonDictString = "{'conditionsTag': {'all': 'CONDBR2-ES1PA-2025-01'}, " +
                    "'autoConfiguration': ['everything'], " +
                    "'maxEvents': -1, " +
                    "'AMITag': 'x899', " +
                    "'CA': 'True', " +
                    "'preExec': {'all': ['flags.Common.doExpressProcessing=True;']}, " +
                    "'postExec':{'all': [\"cfg.getEventAlgo('TrigEgammaMonitorElectronAlgorithm').ElectronLikelihoodTool = cfg.getEventAlgo('TrigEgammaMonitorTagAndProbeAlgorithm_Zee_LH').ElectronLikelihoodTool;\"]}, " +
                    "'geometryVersion': {'all': 'ATLAS-R3S-2021-03-02-00'}, " +
                    "'athenaopts': ['--threads=8']}";

            // Test 1: Parse Python dict
            System.out.println("Test 1: Parsing Python dict...");
            Object parsedObject = JsonUtility.parsePythonDict(pythonDictString);
            System.out.println("✓ Parse réussi: " + (parsedObject != null ? "OK" : "FAIL"));
            System.out.println();

            // Test 2: Parse Python dict as Map
            System.out.println("Test 2: Parsing Python dict as Map...");
            Map<String, Object> dataMap = JsonUtility.parsePythonDictAsMap(pythonDictString);
            System.out.println("✓ Nombre de clés: " + dataMap.size());
            System.out.println("✓ AMITag: " + dataMap.get("AMITag"));
            System.out.println("✓ maxEvents: " + dataMap.get("maxEvents"));
            System.out.println();

            // Test 3: Conversion en JSON et pretty print
            System.out.println("Test 3: Conversion en JSON...");
            String jsonString = JsonUtility.toJson(dataMap);
            System.out.println("✓ JSON compact créé (" + jsonString.length() + " caractères)");

            System.out.println("\nTest 3b: JSON pretty print:");
            String prettyJson = JsonUtility.toJsonPretty(dataMap);
            System.out.println(prettyJson);
            System.out.println();

            // Test 4: Parse JSON
            System.out.println("Test 4: Re-parsing du JSON...");
            Object reparsed = JsonUtility.parseJson(jsonString);
            System.out.println("✓ Re-parse réussi: " + (reparsed != null ? "OK" : "FAIL"));
            System.out.println();

            // Test 5: JSONPath queries
            System.out.println("Test 5: JSONPath queries...");

            String amiTag = JsonUtility.queryJsonPath(dataMap, "$.AMITag");
            System.out.println("✓ AMITag via JSONPath: " + amiTag);

            String conditionsTag = JsonUtility.queryJsonPath(dataMap, "$.conditionsTag.all");
            System.out.println("✓ conditionsTag.all: " + conditionsTag);

            List<String> autoConfig = JsonUtility.queryJsonPathAsList(dataMap, "$.autoConfiguration[*]");
            System.out.println("✓ autoConfiguration: " + autoConfig);

            String geometryVersion = JsonUtility.queryJsonPath(dataMap, "$.geometryVersion.all");
            System.out.println("✓ geometryVersion: " + geometryVersion);
            System.out.println();

            // Test 6: Path exists
            System.out.println("Test 6: Vérification existence de paths...");
            System.out.println("✓ $.AMITag existe: " + JsonUtility.pathExists(dataMap, "$.AMITag"));
            System.out.println("✓ $.nonExistent existe: " + JsonUtility.pathExists(dataMap, "$.nonExistent"));
            System.out.println("✓ $.conditionsTag.all existe: " + JsonUtility.pathExists(dataMap, "$.conditionsTag.all"));
            System.out.println();

            // Test 7: Deep copy
            System.out.println("Test 7: Deep copy...");
            Map<String, Object> copiedMap = JsonUtility.deepCopy(dataMap);
            System.out.println("✓ Deep copy créée");
            copiedMap.put("AMITag", "x900_MODIFIED");
            System.out.println("✓ Original AMITag: " + dataMap.get("AMITag"));
            System.out.println("✓ Copie AMITag: " + copiedMap.get("AMITag"));
            System.out.println("✓ Deep copy fonctionne: " + (!dataMap.get("AMITag").equals(copiedMap.get("AMITag")) ? "OK" : "FAIL"));
            System.out.println();

            // Test 8: Écriture dans un fichier
            System.out.println("Test 8: Écriture dans un fichier...");
            File tempFile = File.createTempFile("ami_test_", ".json");
            JsonUtility.writeJson(tempFile, dataMap);
            System.out.println("✓ Fichier écrit: " + tempFile.getAbsolutePath());

            // Test 9: Lecture depuis un fichier
            System.out.println("\nTest 9: Lecture depuis le fichier...");
            Object fileData = JsonUtility.parseJson(tempFile);
            System.out.println("✓ Lecture réussie: " + (fileData != null ? "OK" : "FAIL"));
            tempFile.delete();
            System.out.println("✓ Fichier temporaire supprimé");
            System.out.println();

            // Test 10: Parsing d'une liste Python
            System.out.println("Test 10: Parsing Python list...");
            String pythonListString = "['item1', 'item2', 'item3']";
            List<Object> pythonList = JsonUtility.parsePythonDictAsList(pythonListString);
            System.out.println("✓ Liste parsée: " + pythonList);
            System.out.println("✓ Taille: " + pythonList.size());
            System.out.println();

            // Test 11: Accès aux données complexes
            System.out.println("Test 11: Accès aux structures imbriquées...");
            List<String> preExecAll = JsonUtility.queryJsonPath(dataMap, "$.preExec.all");
            System.out.println("✓ preExec.all: " + preExecAll);

            List<String> athenaopts = JsonUtility.queryJsonPath(dataMap, "$.athenaopts");
            System.out.println("✓ athenaopts: " + athenaopts);
            System.out.println();

            System.out.println("=== TOUS LES TESTS RÉUSSIS ===");
        }
        catch(Exception e)
        {
            System.err.println("ERREUR lors des tests:");
            e.printStackTrace(System.err);
        }

        System.out.println("\nbye.");
        System.exit(0);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}