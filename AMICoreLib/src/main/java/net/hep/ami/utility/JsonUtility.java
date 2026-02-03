package net.hep.ami.utility;

import java.io.*;
import java.util.*;
import java.lang.reflect.Field;

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

    @Nullable
    public static <T> T queryJsonPath(@NotNull Object jsonObject, @NotNull String jsonPath,
                                      @NotNull Configuration configuration) throws Exception
    {
        DocumentContext ctx = JsonPath.using(configuration).parse(jsonObject);
        return ctx.read(jsonPath);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static <T> List<T> queryJsonPathAsList(@NotNull Object jsonObject, @NotNull String jsonPath) throws Exception
    {
        DocumentContext ctx = JsonPath.using(JSON_PATH_CONFIG).parse(jsonObject);
        return ctx.read(jsonPath, new TypeRef<List<T>>() {});
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    @NotNull
    public static <T> Map<String, T> queryJsonPathAsMap(@NotNull Object jsonObject, @NotNull String jsonPath) throws Exception
    {
        DocumentContext ctx = JsonPath.using(JSON_PATH_CONFIG).parse(jsonObject);
        return ctx.read(jsonPath, new TypeRef<Map<String, T>>() {});
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
    /* JAVA OBJECT EXTRACTION                                                                                         */
    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Extracts a parameter from a Java object using reflection
     */
    @Nullable
    public static Object extractParameter(@NotNull Object object, @NotNull String parameterName) throws Exception
    {
        Class<?> clazz = object.getClass();

        try {
            Field field = clazz.getDeclaredField(parameterName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException e) {
            // Try with parent classes
            Class<?> superClass = clazz.getSuperclass();
            while (superClass != null && superClass != Object.class) {
                try {
                    Field field = superClass.getDeclaredField(parameterName);
                    field.setAccessible(true);
                    return field.get(object);
                } catch (NoSuchFieldException ex) {
                    superClass = superClass.getSuperclass();
                }
            }
            throw new NoSuchFieldException("Field '" + parameterName + "' not found in " + clazz.getName());
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Extracts a typed parameter from a Java object
     */
    @Nullable
    public static <T> T extractParameter(@NotNull Object object, @NotNull String parameterName,
                                         @NotNull Class<T> expectedType) throws Exception
    {
        Object value = extractParameter(object, parameterName);
        if (value == null) {
            return null;
        }
        if (!expectedType.isInstance(value)) {
            throw new ClassCastException("Field '" + parameterName + "' is not of type " + expectedType.getName());
        }
        return expectedType.cast(value);
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Extracts all parameters from a Java object as a Map
     */
    @NotNull
    public static Map<String, Object> extractAllParameters(@NotNull Object object) throws Exception
    {
        Map<String, Object> parameters = new HashMap<>();
        Class<?> clazz = object.getClass();

        // Iterate through all fields of the class and its parents
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                parameters.put(field.getName(), field.get(object));
            }
            clazz = clazz.getSuperclass();
        }

        return parameters;
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Extracts and parses a JSON/Python dict parameter from a Java object
     * The field must contain a String in JSON or Python dict format
     */
    @Nullable
    public static Object extractAndParseParameter(@NotNull Object object, @NotNull String parameterName) throws Exception
    {
        Object value = extractParameter(object, parameterName);

        if (value == null) {
            return null;
        }

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Field '" + parameterName + "' is not a String, cannot parse");
        }

        String stringValue = (String) value;

        // Try to parse as JSON first
        try {
            return parseJson(stringValue);
        } catch (Exception e) {
            // If it fails, try as Python dict
            return parsePythonDict(stringValue);
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Extracts and parses a parameter as Map
     */
    @NotNull
    public static Map<String, Object> extractAndParseParameterAsMap(@NotNull Object object,
                                                                    @NotNull String parameterName) throws Exception
    {
        Object value = extractParameter(object, parameterName);

        if (value == null) {
            return new HashMap<>();
        }

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Field '" + parameterName + "' is not a String, cannot parse");
        }

        String stringValue = (String) value;

        // Try to parse as JSON first
        try {
            return parseJsonAsMap(stringValue);
        } catch (Exception e) {
            // If it fails, try as Python dict
            return parsePythonDictAsMap(stringValue);
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Extracts and parses a parameter as List
     */
    @NotNull
    public static List<Object> extractAndParseParameterAsList(@NotNull Object object,
                                                              @NotNull String parameterName) throws Exception
    {
        Object value = extractParameter(object, parameterName);

        if (value == null) {
            return new ArrayList<>();
        }

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Field '" + parameterName + "' is not a String, cannot parse");
        }

        String stringValue = (String) value;

        // Try to parse as JSON first
        try {
            return parseJsonAsList(stringValue);
        } catch (Exception e) {
            // If it fails, try as Python dict
            return parsePythonDictAsList(stringValue);
        }
    }

    /*----------------------------------------------------------------------------------------------------------------*/

    /**
     * Extracts a parameter and applies a JSONPath query on it
     */
    @Nullable
    public static <T> T extractAndQueryJsonPath(@NotNull Object object, @NotNull String parameterName,
                                                @NotNull String jsonPath) throws Exception
    {
        Object parsedValue = extractAndParseParameter(object, parameterName);
        if (parsedValue == null) {
            return null;
        }
        return queryJsonPath(parsedValue, jsonPath);
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