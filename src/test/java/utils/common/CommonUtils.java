package utils.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility class that provides common operations for working with collections,
 * such as finding duplicate or missing objects in lists.
 */
public class CommonUtils {

    /**
     * Finds and returns a list of duplicate objects from the provided list.
     * Objects are considered duplicates if they appear more than once in the list.
     *
     * @param objects the list of objects to search for duplicates.
     * @param <T>     the type of objects in the list.
     * @return a list of duplicate objects found in the input list.
     */
    public static <T> List<T> findDuplicateObjects(List<T> objects) {
        Set<T> uniqueObjects = new HashSet<>();
        List<T> duplicateObjects = new ArrayList<>();

        for (T obj : objects) {
            if (!uniqueObjects.add(obj)) {
                duplicateObjects.add(obj);
            }
        }
        return duplicateObjects;
    }

    /**
     * Finds and returns a list of objects that are present in the expected list
     * but missing from the actual list.
     *
     * @param expected the list of expected objects.
     * @param actual   the list of actual objects.
     * @param <T>      the type of objects in the lists.
     * @return a list of missing objects that are present in the expected list but not in the actual list.
     */
    public static <T> List<T> findMissingObjects(List<T> expected, List<T> actual) {
        List<T> missingItems = new ArrayList<>(expected);
        missingItems.removeAll(actual);
        return missingItems;
    }

}