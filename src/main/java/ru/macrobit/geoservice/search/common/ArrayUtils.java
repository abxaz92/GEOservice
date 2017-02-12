package ru.macrobit.geoservice.search.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Georgy Davityan.
 */
public class ArrayUtils {

    public static List<String> garbageWords = Arrays.asList(
            "ул", "ул.", "пр", "пр.", "кв", "корп", "а", "б", "в",
            "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п", "р",
            "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "а.", "б.",
            "в.", "г.", "д.", "е.", "ё.", "ж.", "з.", "и.", "й.", "к.", "л.", "м.",
            "н.", "о.", "п.", "р.", "с.", "т.", "у.", "ф.", "х.", "ц.", "ч.", "ш.",
            "щ.", "ъ.", "ы.", "ь.", "улица", "улица.", "проспект", "проспект."
    );

    public static List<String> split(String pattern, List<String> splitters) {
        List<String> words = new ArrayList<>();
        String buffer = "";
        for (int i = 0; i < pattern.length(); i++) {
            String currentChar = pattern.substring(i, i + 1);
            if (!splitters.contains(currentChar)) {
                buffer += currentChar;
            } else {
                if (!garbageWords.contains(buffer) && buffer.length() > 0) {
                    words.add(buffer);
                }
                buffer = "";
            }
        }
        words.add(buffer);
        return words;
    }

    public static String concat(List<String> list, int from, int to, String separator) {
        separator = separator == null ? " " : separator;
        String output = list.get(from);
        int i = from + 1;
        while (i < to) {
            output += separator + list.get(i);
            i++;
        }
        return output;
    }

    public static List<String> merge(List<String> list, int from, int to) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < list.size()) {
            if (i < from || i > to) {
                result.add(list.get(i));
            }
            if (i == from) {
                String o = list.get(from);
                for (int j = from + 1; j <= to; j++) {
                    o += " " + list.get(j);
                }
                result.add(o);
                i = to;
            }
            i++;
        }
        return result;
    }

}
