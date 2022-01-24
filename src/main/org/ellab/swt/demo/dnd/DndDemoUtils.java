package org.ellab.swt.demo.dnd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DndDemoUtils {
    public static String getMatchedFields(int val, Class<?> clazz, String delim, String prefix, int... vals) {
        List<String> result = new ArrayList<>();

        Arrays.stream(vals).forEach(v -> {
            if ((val & v) > 0) {
                String s = getFieldNameFromValue(clazz, v, prefix);
                if (s != null) {
                    result.add(s);
                }
            }
        });

        return String.join(delim, result.toArray(new String[0]));
    }

    public static String getFieldNameFromValue(Class<?> clazz, int val, String prefix) {
        for (Field f : clazz.getDeclaredFields()) {
            try {
                if (prefix == null || prefix.length() == 0 || f.getName().startsWith(prefix)) {
                    if (Integer.TYPE.isAssignableFrom(f.getType()) && f.getInt(null) == val) {
                        return f.getName();
                    }
                }
            }
            catch (Exception ex) {
            }
        }

        return null;
    }
}
