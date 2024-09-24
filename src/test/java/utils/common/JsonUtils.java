package utils.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.restassured.response.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for handling JSON operations such as loading data from JSON files,
 * parsing JSON responses, and printing JSON in a pretty format.
 */
public class JsonUtils {

    // Default path where the test data JSON files are stored
    private static final String DEFAULT_PATH = "src/test/resources/test_data/";

    /**
     * Loads data from a JSON file based on the environment and file name.
     * The method deserializes the JSON data into a list of objects of the specified type.
     *
     * @param env      the environment prefix (e.g., "dev", "prod") to be added to the file name.
     * @param fileName the file name without the environment prefix.
     * @param clazz    the class type of the objects to be deserialized.
     * @param <T>      the type of the objects in the list.
     * @return a list of objects of the specified type loaded from the JSON file.
     * @throws IOException if there is an issue reading the JSON file.
     */
    public static <T> List<T> loadDataFromJsonFile(String env, String fileName, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(DEFAULT_PATH + "/" + env + "/" + fileName + ".json");
        return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    /**
     * Parses the JSON response body into a list of objects of the specified type.
     *
     * @param response     the Response object containing the JSON body.
     * @param responseType the class type of the objects to be deserialized.
     * @param <T>          the type of the objects in the list.
     * @return a list of objects of the specified type parsed from the JSON response body.
     * @throws JsonProcessingException if there is an issue parsing the JSON response.
     */
    public static <T> List<T> parseJsonResponseList(Response response, Class<T> responseType) throws JsonProcessingException {
        String jsonResponse = response.getBody().asString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse,
                objectMapper.getTypeFactory().constructCollectionType(List.class, responseType));
    }

    /**
     * Parses the JSON response body into an object of the specified type.
     *
     * @param response     the Response object containing the JSON body.
     * @param responseType the class type of the object to be deserialized.
     * @param <T>          the type of the object.
     * @return an object of the specified type parsed from the JSON response body.
     * @throws JsonProcessingException if there is an issue parsing the JSON response.
     */
    public static <T> T parseJsonResponseObject(Response response, Class<T> responseType) throws JsonProcessingException {
        String jsonResponse = response.getBody().asString();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, responseType);
    }

    /**
     * Pretty prints a JSON string to make it more readable.
     *
     * @param jsonString the JSON string to be formatted.
     * @return a pretty-printed version of the JSON string, or an error message if formatting fails.
     */
    public static String printPrettyJson(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object json = objectMapper.readValue(jsonString, Object.class);
            ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
            return writer.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            return "Failed to pretty-print JSON: " + e.getMessage();
        }
    }

}