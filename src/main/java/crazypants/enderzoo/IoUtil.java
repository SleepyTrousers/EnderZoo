package crazypants.enderzoo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

public class IoUtil {

  public static String readConfigFile(File copyTo, String resourcePath, boolean replaceIfExists) throws IOException {
    return readConfigFile(copyTo, IoUtil.class.getResourceAsStream(resourcePath), replaceIfExists);
  }

  public static String readConfigFile(File copyTo, InputStream in, boolean replaceIfExists) throws IOException {
    if (!replaceIfExists && copyTo.exists()) {
      return readStream(new FileInputStream(copyTo));
    }
    String output = copyTextTo(copyTo, in);
    return output.toString();
  }

  public static String copyTextTo(File copyTo, InputStream from) throws IOException {
    String output = readStream(from);
    BufferedWriter writer = null;
    try {
      makePath(copyTo);
      writer = new BufferedWriter(new FileWriter(copyTo, false));
      writer.write(output.toString());
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return output;
  }

  public static void makePath(File copyTo) throws IOException {
    File p = copyTo.getParentFile();
    if (p == null) {
      return;
    }
    if (!p.exists()) {
      if (!p.mkdirs()) {
        throw new IOException("Could not create directory: " + p.getAbsolutePath());
      }
    }
  }

  public static String readStream(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder output = new StringBuilder();
    try {
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        output.append("\n");
        line = reader.readLine();
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
    return output.toString();
  }

}
