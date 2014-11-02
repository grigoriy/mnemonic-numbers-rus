package com.grigoriyalexeev.mnemonics;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOUtils {

    public static List<String> readWordsFromDictFile(File dictFile, Pattern wordPattern) {

        try (FileInputStream fis = new FileInputStream(dictFile);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            return readWordsFromReader(br, wordPattern);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading dictionary file");
        }
    }

    private static List<String> readWordsFromReader(BufferedReader dictReader, Pattern wordPattern) throws IOException {

        List<String> words = new LinkedList<>();
        String line;
        while ((line = dictReader.readLine()) != null) {
            Matcher wordMatcher = wordPattern.matcher(line);
            if (wordMatcher.matches()) {
                words.add(wordMatcher.group(1));
            }
        }

        return words;
    }

    public static void appendTextToFile(String text, File file) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
            fileOutputStream.write(text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error while appending text to file", e);
        }
    }
}
