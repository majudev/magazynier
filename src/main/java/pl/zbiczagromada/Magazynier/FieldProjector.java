package pl.zbiczagromada.Magazynier;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldProjector {
    // returns a List of Maps containing projected field names and values
    public static <T> List<Map<String, Object>> projectList(final Collection<T> objectList, final String[] fields) {
        final List<Map<String, Object>> projectedList = new ArrayList<Map<String, Object>>();

        // populate list by adding each projected object individually
        for(final Object object : objectList) {
            projectedList.add(project(object, fields));
        }
        return projectedList;

    }

    // returns a Map containing the object's projected fields
    // note fields is not varargs as it is necessary to plug in some fields
    public static Map<String, Object> project(final Object object, final String[] fields) {
        // retrieve the object's root (non-nested) field names
        final List rootFields = getRootFieldNames(fields);

        // create the Map instance to be returned with root fields populated
        final Map<String, Object> projectedMap = buildWithRootFields(object, rootFields);

        // get list of nestedObjectNames, e.g. "abilityList"
        final List<String> nestedObjectNames = getNestedObjectNames(fields);

        // loop through nested object names
        // and retrieve each nested object
        for(String nestedObjectName : nestedObjectNames) {
            // retrieves the nested object, e.g. List
            final Object nestedObject = getFieldValue(object, nestedObjectName);
            // retrieves the nested object's field names
            final List<String> nestedObjectFieldNames = getNestedObjectFieldNames(nestedObjectName, fields);
            // converts the field name list to array
            final String[] nestedObjectFieldNamesArray = nestedObjectFieldNames.toArray(new String[0]);

            if(nestedObject != null) {
                if (nestedObject instanceof Collection) {
                    // if object is a collection, parse it with projectList()
                    // and add to projectedMap
                    @SuppressWarnings("unchecked") // we know this is a collection
                    final Collection<Object> collection = (Collection<Object>) nestedObject;
                    projectedMap.put(nestedObjectName, projectList(collection, nestedObjectFieldNamesArray));
                } else {
                    // else simply parse it, the same as
                    // the root object, and add to projectedMap
                    projectedMap.put(nestedObjectName, project(nestedObject, nestedObjectFieldNamesArray));
                }
            }
        }

        return projectedMap;

    }

    // returns a list of non-nested fields (effectively, fields without a '.'
    private static List<String> getRootFieldNames(final String[] fields) {
        final List<String> rootFieldNames = new ArrayList<String>();
        for(String field : fields) {
            if (!field.contains(".")) {
                rootFieldNames.add(field);
            }
        }

        return rootFieldNames;
    }

    // returns a Map with provided rootField names and values
    private static Map<String, Object> buildWithRootFields(final Object source, final Collection<String> rootFieldNames) {
        final Map<String, Object> target = new HashMap<String, Object>();
        for(String rootFieldName : rootFieldNames) {
            Object fieldValue = getFieldValue(source, rootFieldName);
            //if(fieldValue != null) {
                target.put(rootFieldName, fieldValue);
            //}
        }
        return target;
    }

    // returns a list of nested object names, e.g.
    // "abilityList.name" would return "abilityList"
    private static List<String> getNestedObjectNames(final String[] fields) {
        final List<String> nestedObjectNameList = new ArrayList<String>();
        for(final String field : fields) {
            // if field name contains '.', this is a nested object
            if (field.contains(".")) {
                // get the string up to the '.'
                final String nestedObjectName = field.substring(0, field.indexOf("."));
                // add to list only if not added yet
                if (!nestedObjectNameList.contains(nestedObjectName))
                    nestedObjectNameList.add(nestedObjectName);
            }
        }
        return nestedObjectNameList;
    }

    // returns a list of nested object field names, e.g.
    // "abilityList.name" would return "name"
    private static List<String> getNestedObjectFieldNames(final String nestedObjectName, final String[] fields) {
        final List<String> nestedObjectFieldNames = new ArrayList<String>();
        final String formattedNestedObjectName = nestedObjectName + ".";
        final int prefixLength = formattedNestedObjectName.length();

        for(final String field : fields) {
            // if field starts with formattedNestedObjectName,
            // insert into list the part after formattedNestedObjectName
            if (field.startsWith(formattedNestedObjectName)) {
                nestedObjectFieldNames.add(field.substring(prefixLength));
            }
        }
        return nestedObjectFieldNames;

    }

    // returns the value of a given field
    private static Object getFieldValue(final Object source, final String fieldName) {
        try {
            Field sourceField = source.getClass().getDeclaredField(fieldName);
            sourceField.setAccessible(true);
            return sourceField.get(source);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // incorrect field name provided
            return null;
        }
    }
}