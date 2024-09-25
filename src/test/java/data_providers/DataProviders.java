package data_providers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConfigManager;
import org.testng.annotations.DataProvider;
import tests.base.BaseTest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class provides various data providers for TestNG test methods.
 * It loads test data from JSON files and converts it into a format
 * that can be used by TestNG tests.
 */
public class DataProviders extends BaseTest {

    private static final String PATH = ConfigManager.getPathProperty("test.data.provider");
    private static final String env = ConfigManager.getEnv();

    /**
     * Provides test data for creating authors.
     * The data is loaded from a JSON file and mapped to an Object array.
     *
     * @return a two-dimensional Object array with test data for creating authors.
     * @throws IOException if the test data file cannot be read.
     */
    @DataProvider(name = "createAuthorDataProvider")
    public Object[][] createAuthorDataProvider() throws IOException {
        return getGenericDataProvider(PATH + "/" + env + "/" + "authors/create_authors_test_data.json", this::mapToAuthorTestData);
    }

    /**
     * Provides test data for updating authors.
     * The data is loaded from a JSON file and mapped to an Object array.
     *
     * @return a two-dimensional Object array with test data for updating authors.
     * @throws IOException if the test data file cannot be read.
     */
    @DataProvider(name = "updateAuthorDataProvider")
    public Object[][] updateAuthorDataProvider() throws IOException {
        return getGenericDataProvider(PATH + "/" + env + "/" + "authors/update_authors_test_data.json", this::mapToAuthorTestData);
    }

    /**
     * Provides test data for creating books.
     * The data is loaded from a JSON file and mapped to an Object array.
     *
     * @return a two-dimensional Object array with test data for creating books.
     * @throws IOException if the test data file cannot be read.
     */
    @DataProvider(name = "createBookDataProvider")
    public Object[][] createBookDataProvider() throws IOException {
        return getGenericDataProvider(PATH + "/" + env + "/" + "books/create_books_test_data.json", this::mapToBookTestData);
    }

    /**
     * Provides test data for updating books.
     * The data is loaded from a JSON file and mapped to an Object array.
     *
     * @return a two-dimensional Object array with test data for creating books.
     * @throws IOException if the test data file cannot be read.
     */
    @DataProvider(name = "updateBookDataProvider")
    public Object[][] updateBookDataProvider() throws IOException {
        return getGenericDataProvider(PATH + "/" + env + "/" + "books/update_books_test_data.json", this::mapToBookTestData);
    }

    /**
     * Generic method to provide test data from a specified JSON file.
     * It uses a custom mapping function to convert the data into a format suitable for TestNG tests.
     *
     * @param filePath        the path to the JSON file containing the test data.
     * @param mappingFunction a function to map each JSON entry to an Object array.
     * @return a two-dimensional Object array containing the test data.
     * @throws IOException if the test data file cannot be read.
     */
    public Object[][] getGenericDataProvider(String filePath, Function<Map<String, Object>, Object[]> mappingFunction) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> dataList = objectMapper.readValue(
                new File(filePath),
                new TypeReference<>() {
                }
        );

        return dataList.stream()
                .map(mappingFunction)
                .toArray(Object[][]::new);
    }

    /**
     * Maps a single test data entry to an Object array for TestNG.
     * This method extracts values such as testName, expectedStatusCode, id, idBook, firstName, and lastName.
     *
     * @param data a Map containing the test data.
     * @return an Object array containing the mapped test data.
     */
    private Object[] mapToAuthorTestData(Map<String, Object> data) {
        return new Object[]{
                data.get("testName"),
                data.get("expectedStatusCode"),
                getLongValue(data, "id"),
                getLongValue(data, "idBook"),
                data.get("firstName"),
                data.get("lastName")
        };
    }

    /**
     * Maps a single test data entry to an Object array for TestNG.
     * This method extracts values such as testName, expectedStatusCode, id, title, description, pageCount, excerpt, and publishDate.
     *
     * @param data a Map containing the test data.
     * @return an Object array containing the mapped test data.
     */
    private Object[] mapToBookTestData(Map<String, Object> data) {
        return new Object[]{
                data.get("testName"),
                data.get("expectedStatusCode"),
                getLongValue(data, "id"),
                data.get("title"),
                data.get("description"),
                getIntValue(data),
                data.get("excerpt"),
                data.get("publishDate")
        };
    }

    /**
     * Helper method to safely extract a Long value from a Map.
     * If the value is not present or is null, it returns null.
     *
     * @param data the Map containing the data.
     * @param key  the key for the value to be extracted.
     * @return the Long value or null if the value is not present.
     */
    private Long getLongValue(Map<String, Object> data, String key) {
        return data.get(key) != null ? ((Number) data.get(key)).longValue() : null;
    }

    /**
     * Helper method to safely extract an integer value from a Map.
     *
     * @param data the Map containing the test data.
     * @return the integer value or 0 if the value is null.
     */
    private int getIntValue(Map<String, Object> data) {
        return data.get("pageCount") != null ? ((Number) data.get("pageCount")).intValue() : 0;
    }

}