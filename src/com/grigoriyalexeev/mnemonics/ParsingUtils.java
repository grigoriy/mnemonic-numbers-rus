package com.grigoriyalexeev.mnemonics;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class ParsingUtils {

    public static Pattern createPatternForNumber(String number, Map<String, String> digitsToLetters, String unmappedLettersRegex) {
        String result = unmappedLettersRegex;
        for (int i = 0; i < number.length(); i++) {
            result += digitsToLetters.get(String.valueOf(number.charAt(i))) + unmappedLettersRegex;
        }
        return Pattern.compile(result);
    }

    public static Set<String> getAndRemoveMatchesFromList(List<String> strings, Pattern pattern) {
        Set<String> words = new TreeSet<>();
        Iterator<String> itr = strings.iterator();
        while (itr.hasNext()) {
            String word = itr.next();
            Matcher matcher = pattern.matcher(word);
            if (matcher.matches()) {
                words.add(word);
                itr.remove();
            }
        }
        return words;
    }
}
