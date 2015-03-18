package org.elasticsearch.index.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.lucene.analysis.util.WordlistLoader;

public class UpdateStemmingSamples {
  private final static TurkishStemmer stemmer = new TurkishStemmer();

  public static void main(String args[])
      throws java.io.IOException, java.io.FileNotFoundException
  {
    List<String> lines = WordlistLoader.getLines(
        new FileInputStream("src/test/resources/stemming_samples.txt"),
        StandardCharsets.UTF_8);

    char[] token;
    int tokenLength;
    String stem;
    File file = new File("src/test/resources/stemming_samples.txt");
    FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
    BufferedWriter writer = new BufferedWriter(fileWriter);

    try {
      for(String line : lines) {
        String[] sample =  line.split(",");
        token = sample[0].toCharArray();
        tokenLength = sample[0].length();
        stem = stemmer.stem(token, tokenLength);
        writer.write(sample[0] + "," + stem);
        writer.newLine();
      }
      writer.close();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}
