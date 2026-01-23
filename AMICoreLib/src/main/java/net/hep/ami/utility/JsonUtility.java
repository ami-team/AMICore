package net.hep.ami.utility;

import java.io.*;
import java.util.*;

import com.jayway.jsonpath.*;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.core.type.*;

import org.jetbrains.annotations.*;

public class JsonUtility
{
    /*----------------------------------------------------------------------------------------------------------------*/

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Configuration JSON_PATH_CONFIG = Configuration.builder()
            .jsonProvider(new JacksonJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    /*----------------------------------------------------------------------------------------------------------------*/

    @Contract(pure = true)
    private JsonUtility() {}

    /*----------------------------------------------------------------------------------------------------------------*/
    /* JSON PARSING                                                                                                   */
    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parseJson(@NotNull String jsonString) throws Exception
    {
        return OBJECT_MAPPER.readValue(jsonString, Object.class);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parseJson(@NotNull File file) throws Exception
    {
        return parseJson(new FileInputStream(file));
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parseJson(@NotNull InputStream inputStream) throws Exception
    {
        return OBJECT_MAPPER.readValue(inputStream, Object.class);
    }

    /*----------------------------------------------------------------------------------------------------------------*/


    @NotNull
    public static Map<String, Object> parseJsonAsMap(@NotNull String jsonString) throws Exception
    {
        return OBJECT_MAPPER.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
    }

    /*----------------------------------------------------------------------------------------------------------------*/


    @NotNull
    public static List<Object> parseJsonAsList(@NotNull String jsonString) throws Exception
    {
        return OBJECT_MAPPER.readValue(jsonString, new TypeReference<List<Object>>() {});
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /* PYTHON DICT PARSING (ANTLR)                                                                                    */
    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parsePythonDict(@NotNull String pythonDictString) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(pythonDictString, Object.class, true);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parsePythonDict(@NotNull String pythonDictString, boolean allowSimpleQuotes) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(pythonDictString, Object.class, allowSimpleQuotes);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parsePythonDict(@NotNull File file) throws Exception
    {
        return parsePythonDict(new FileInputStream(file));
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parsePythonDict(@NotNull InputStream inputStream) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(inputStream, Object.class, true);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static Object parsePythonDict(@NotNull InputStream inputStream, boolean allowSimpleQuotes) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(inputStream, Object.class, allowSimpleQuotes);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static Map<String, Object> parsePythonDictAsMap(@NotNull String pythonDictString) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(pythonDictString, Map.class, true);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static Map<String, Object> parsePythonDictAsMap(@NotNull String pythonDictString, boolean allowSimpleQuotes) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(pythonDictString, Map.class, allowSimpleQuotes);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static List<Object> parsePythonDictAsList(@NotNull String pythonDictString) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(pythonDictString, List.class, true);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static List<Object> parsePythonDictAsList(@NotNull String pythonDictString, boolean allowSimpleQuotes) throws Exception
    {
        return net.hep.ami.utility.parser.PythonDict.parse(pythonDictString, List.class, allowSimpleQuotes);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /* JSON PATH NAVIGATION                                                                                           */
    /*----------------------------------------------------------------------------------------------------------------*/


    @Nullable
    public static <T> T queryJsonPath(@NotNull Object jsonObject, @NotNull String jsonPath) throws Exception
    {
        DocumentContext ctx = JsonPath.using(JSON_PATH_CONFIG).parse(jsonObject);
        return ctx.read(jsonPath);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static <T> T queryJsonPath(@NotNull String jsonString, @NotNull String jsonPath) throws Exception
    {
        return JsonPath.read(jsonString, jsonPath);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static <T> List<T> queryJsonPathAsList(@NotNull Object jsonObject, @NotNull String jsonPath) throws Exception
    {
        DocumentContext ctx = JsonPath.using(JSON_PATH_CONFIG).parse(jsonObject);
        return ctx.read(jsonPath, new TypeRef<List<T>>() {});
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    public static boolean pathExists(@NotNull Object jsonObject, @NotNull String jsonPath)
    {
        try
        {
            Object result = queryJsonPath(jsonObject, jsonPath);
            return result != null;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static <T> T queryJsonPath(@NotNull Object jsonObject, @NotNull String jsonPath,
                                      @NotNull Configuration configuration) throws Exception
    {
        DocumentContext ctx = JsonPath.using(configuration).parse(jsonObject);
        return ctx.read(jsonPath);
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /* UTILITY METHODS                                                                                                */
    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static String toJson(@NotNull Object object) throws Exception
    {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static String toJsonPretty(@NotNull Object object) throws Exception
    {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    public static void writeJson(@NotNull File file, @NotNull Object object) throws Exception
    {
        OBJECT_MAPPER.writeValue(file, object);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @Nullable
    public static <T> T deepCopy(@NotNull T object) throws Exception
    {
        String json = toJson(object);
        @SuppressWarnings("unchecked")
        T copy = (T) parseJson(json);
        return copy;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
}
