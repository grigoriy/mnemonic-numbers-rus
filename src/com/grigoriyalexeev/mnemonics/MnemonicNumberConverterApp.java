package com.grigoriyalexeev.mnemonics;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class MnemonicNumberConverterApp {

    public static final Map<File[], String> DICT_FILES_TO_REGEXES = new HashMap<File[], String>() {{
        put(new File("../mnemonic-numbers-rus/resources/dict").listFiles(), "^([^ ]+) [^ ]+ (с|м|ж|мо|жо|со|мн\\.) .*");
    }};
    public static final String OUTPUT_FILENAME = "mnemonic-numbers-map.txt";
    public static final Map<String, String> DIGITS_TO_LETTERS = new TreeMap<String, String>() {{
        put("0", "[НнЦц]");
        put("1", "[РрХх]");
        put("2", "[ДдБб]");
        put("3", "[Тт]");
        put("4", "[КкЧч]");
        put("5", "[ПпФфГг]");
        put("6", "[ЛлШшЩщ]");
        put("7", "[СсЙй]");
        put("8", "[ВвЗз]");
        put("9", "[МмЖж]");
    }};
    public static final String VOWELS_REGEX = "[АЕЁИОУЪЫЬЭЮЯаеёиоуъыьэюя]*?";
    public static final int RADIX = 10;
    public static final int MAX_NUM_DIGITS = 3;


    public static void main(String[] args) {

        List<String> dict = getWordsFromDictFiles(DICT_FILES_TO_REGEXES);

        if (args.length > 0) {
            for (String number : args) {
                Pattern pattern = ParsingUtils.createPatternForNumber(number, DIGITS_TO_LETTERS, VOWELS_REGEX);
                Set<String> words = ParsingUtils.getAndRemoveMatchesFromList(dict, pattern);
                System.out.println(TextUtils.formatNumberAndWords(number, words));
            }

        } else {
            List<String> numbersList = createNumbersList(RADIX, MAX_NUM_DIGITS);
            int countEmptyNumbers = 0;
            Map<Character, Integer> charsEmptinessCount = new HashMap<Character, Integer>() {{
                for (int i = 0; i <= 9; i++) {
                    put(Character.forDigit(i, RADIX), 0);
                }
            }};
            File outFile = new File(OUTPUT_FILENAME);
            String emptyNumbers = "";
            for (String number : numbersList) {
                System.out.println(number);
                Pattern pattern = ParsingUtils.createPatternForNumber(number, DIGITS_TO_LETTERS, VOWELS_REGEX);
                Set<String> wordsForNumber = ParsingUtils.getAndRemoveMatchesFromList(dict, pattern);
                IOUtils.appendTextToFile(TextUtils.formatNumberAndWords(number, wordsForNumber), outFile);

                if (wordsForNumber.size() == 0) {
                    emptyNumbers += (number + " ");
                    for (char c : number.toCharArray()) {
                        charsEmptinessCount.put(c, charsEmptinessCount.get(c) + 1);
                    }
                    countEmptyNumbers++;
                }
            }

            IOUtils.appendTextToFile("\nDIGITS TO CONSONANTS TABLE\n======================\n", outFile);
            IOUtils.appendTextToFile(TextUtils.formatMap(DIGITS_TO_LETTERS), outFile);
            IOUtils.appendTextToFile("\nCHARS EMPTINESS COUNT\n=====================\n", outFile);
            IOUtils.appendTextToFile(TextUtils.formatMap(charsEmptinessCount), outFile);
            IOUtils.appendTextToFile("\nTOTAL EMPTY NUMBERS = " + countEmptyNumbers + "\n", outFile);
            IOUtils.appendTextToFile("\nEMPTY NUMBERS:\n" + emptyNumbers + "\n", outFile);
        }
    }

    private static List<String> getWordsFromDictFiles(Map<File[], String> dictFilesToRegexes) {
        List<String> words = new ArrayList<>();
        for (Map.Entry<File[], String> dictFilesToRegex : dictFilesToRegexes.entrySet()) {
            Pattern dictWordPattern = Pattern.compile(dictFilesToRegex.getValue());
            for (File dictFile : dictFilesToRegex.getKey()) {
                words.addAll(IOUtils.readWordsFromDictFile(dictFile, dictWordPattern));
            }
        }
        return words;
    }

    private static List<String> createNumbersList(int radix, int maxNumDigits) {
        return createNumbersListHelper(radix, maxNumDigits, new ArrayList<String>(), 0);
    }

    private static List<String> createNumbersListHelper(int radix, int maxNumDigits, ArrayList<String> generatedNumbers, int lastGeneratedNumDigits) {
        int numGenerated = generatedNumbers.size();

        if (maxNumDigits <= 0 || lastGeneratedNumDigits < 0 || radix <= 0 || maxNumDigits == lastGeneratedNumDigits) {
            return generatedNumbers;

        } else if (numGenerated <= 0) {
            for (int i = 0; i < radix; i++) {
                generatedNumbers.add(String.valueOf(Character.forDigit(i, radix)));
            }

        } else {
            List<String> newGeneratedNumbers = new ArrayList<>();
            for (int i = numGenerated - (int) Math.pow(radix, lastGeneratedNumDigits); i < numGenerated; i++) {
                for (int j = 0; j < radix; j++) {
                    newGeneratedNumbers.add(generatedNumbers.get(i) + generatedNumbers.get(j));
                }
            }
            generatedNumbers.addAll(newGeneratedNumbers);
        }

        return createNumbersListHelper(radix, maxNumDigits, generatedNumbers, ++lastGeneratedNumDigits);
    }

}
