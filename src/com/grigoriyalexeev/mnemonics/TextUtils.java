package com.grigoriyalexeev.mnemonics;

import java.util.Collection;
import java.util.Map;


public class TextUtils {

    public static <K,V> String formatMap(Map<K, V> map) {
        String result = "";
        for (Map.Entry<K,V> entry : map.entrySet()) {
            result += String.format("%-5s = %s%n", entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }

    public static String formatNumberAndWords(String number, Collection<String> words) {
        String result = String.format("%-5s", number);
        int numWordsLeft = words.size();
        int wordsPerLineLeft = 15;
        for (String word : words) {
            result += word;
            numWordsLeft--;
            if (numWordsLeft > 0) {
                result += ", ";
                if (--wordsPerLineLeft <= 0) {
                    result += String.format("%n%-5s", "");
                    wordsPerLineLeft = 15;
                }
            }
        }
        return result + "\n\n";
    }
}
