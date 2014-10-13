package com.grigoriyalexeev.mnemonics;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MnemonicNumberConverter {

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
    public static final File[] DICTS = new File("resources/dict").listFiles();
    public static final String OUTPUT_FILENAME = "mnemonic-numbers-map.txt";
    public static final String ZALIZNYAK_REGEX = "с|м|ж|мо|жо|со|мн.";


    public static void main(String[] args) {

        List<String> dict = readDictFiles(Arrays.asList(DICTS));

        if (args.length > 0) {
            for (String arg : args) {
                Set<String> words = findWordsForNumber(arg, dict);
                System.out.println(formatNumberAndWords(arg, words));
            }

        } else {
            List<String> numbersList = createNumbersList();
            int countEmptyNumbers = 0;
            Map<Character, Integer> charsEmptinessCount = new HashMap<Character, Integer>() {{
                put('0', 0);
                put('1', 0);
                put('2', 0);
                put('3', 0);
                put('4', 0);
                put('5', 0);
                put('6', 0);
                put('7', 0);
                put('8', 0);
                put('9', 0);
            }};
//            for (String number : numbersList) {
//                Set<String> words = findWordsForNumber(number);
//                if (words.size() == 0) {
//                    System.out.println(number);
//                    for (char c : number.toCharArray()) {
//                        charsEmptinessCount.put(c, charsEmptinessCount.get(c) + 1);
//                    }
//                    countEmptyNumbers++;
//                }
////                System.out.println(formatNumberAndWords(number, findWordsForNumber(number)));
//            }
//            System.out.println("\nDIGITS TO CONSONANTS TABLE\n======================");
//            System.out.println(formatMap(DIGITS_TO_LETTERS));
//            System.out.println("\nCHARS EMPTINESS COUNT\n=====================");
//            System.out.println(formatMap(charsEmptinessCount));
//            System.out.println("\nTOTAL EMPTY NUMBERS = " + countEmptyNumbers);
            File outFile = new File(OUTPUT_FILENAME);
            try {
                String emptyNumbers = "";
                for (String number : numbersList) {
                    System.out.println(number);
                    Set<String> words = findWordsForNumber(number, dict);
                    appendTextToFile(formatNumberAndWords(number, words), outFile);
                    if (words.size() == 0) {
                        emptyNumbers += (number + " ");
                        for (char c : number.toCharArray()) {
                            charsEmptinessCount.put(c, charsEmptinessCount.get(c) + 1);
                        }
                        countEmptyNumbers++;
                    }
                }
                appendTextToFile("\nDIGITS TO CONSONANTS TABLE\n======================\n", outFile);
                appendTextToFile(formatMap(DIGITS_TO_LETTERS), outFile);
                appendTextToFile("\nCHARS EMPTINESS COUNT\n=====================\n", outFile);
                appendTextToFile(formatMap(charsEmptinessCount), outFile);
                appendTextToFile("\nTOTAL EMPTY NUMBERS = " + countEmptyNumbers + "\n", outFile);
                appendTextToFile("\nEMPTY NUMBERS:\n" + emptyNumbers + "\n", outFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static List<String> readDictFiles(List<File> dictFiles) {

        Pattern zaliznyakPattern = Pattern.compile(ZALIZNYAK_REGEX);
        List<String> words = new LinkedList<String>();
        for (File dictFile : dictFiles) {
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(dictFile);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    String word = parseZaliznyakHelper(line, zaliznyakPattern);
                    if (word != null) {
                        words.add(word);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (isr != null) {
                    try {
                        isr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return words;
    }

    private static Set<String> findWordsForNumber(String number, List<String> dict) {
        Set<String> words = new TreeSet<String>();
        Pattern pattern = createPatternForNumber(number);
        Iterator<String> itr = dict.iterator();
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

    private static String formatNumberAndWords(String number, Collection<String> words) {
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

    private static List<String> createNumbersList() {
        List<String> numbers = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            numbers.add(String.valueOf(i));
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                numbers.add(String.valueOf(i) + String.valueOf(j));
            }
        }
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    numbers.add(String.valueOf(i) + String.valueOf(j) + String.valueOf(k));
                }
            }
        }
        return numbers;
    }

    private static void appendTextToFile(String text, File file) throws FileNotFoundException {
        FileOutputStream fileOutputStream = new FileOutputStream(file, true);
        try {
            fileOutputStream.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static <K,V> String formatMap(Map<K, V> map) {
        String result = "";
        for (Map.Entry<K,V> entry : map.entrySet()) {
            result += String.format("%-5s = %s%n", entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }

    private static Pattern createPatternForNumber(String number) {
        String regex = VOWELS_REGEX;
        for (int i = 0; i < number.length(); i++) {
            regex += DIGITS_TO_LETTERS.get(String.valueOf(number.charAt(i))) + VOWELS_REGEX;
        }
        return Pattern.compile(regex);
    }

    private static String parseZaliznyakHelper(String line, Pattern regex) {

        String[] split = line.split(" ");
        String firstWord = split[0];

        if (split.length >= 3) {
            String thirdWord = split[2];
            Matcher matcher = regex.matcher(thirdWord);
            if (matcher.matches()) {
                return firstWord;
            }
        }
        return null;
    }
}
