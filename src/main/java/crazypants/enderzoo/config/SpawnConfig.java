package crazypants.enderzoo.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import crazypants.enderzoo.Log;

public class SpawnConfig {

  private static final String RESOURCE_PATH = "/assets/enderzoo/config/";
  public static final String CONFIG_NAME_CORE = "SpawnConfig_Core.xml";
  public static final String CONFIG_NAME_USER = "SpawnConfig_User.xml";

  public static List<SpawnEntry> loadSpawnConfig() {
    File coreFile = new File(Config.configDirectory, CONFIG_NAME_CORE);

    String defaultVals = null;
    try {
      defaultVals = readConfigFile(coreFile, CONFIG_NAME_CORE, true);
    } catch (IOException e) {
      Log.error("Could not load core spawn config file " + coreFile + " from EnderZoo jar: " + e.getMessage());
      e.printStackTrace();
      return null;
    }

    if(!coreFile.exists()) {
      Log.error("Could not load core config from " + coreFile + " as the file does not exist.");
      return null;
    }

    List<SpawnEntry> result;
    try{
      result = SpawnConfigParser.parseSpawnConfig(defaultVals);
    } catch(Exception e) {
      Log.error("Error parsing " + CONFIG_NAME_CORE + ":" + e);
      return Collections.emptyList();
    }
    Log.info("Loaded " + result.size() + " entries from core spawn config.");
    

    File userFile = new File(Config.configDirectory, CONFIG_NAME_USER);
    String userText = null;
    try {
      userText = readConfigFile(userFile, CONFIG_NAME_USER, false);
      if(userText == null || userText.trim().length() == 0) {
        Log.error("Empty user config file: " + userFile.getAbsolutePath());
      } else {
        List<SpawnEntry> userEntries = SpawnConfigParser.parseSpawnConfig(userText);
        Log.info("Loaded " + userEntries.size() + " entries from user spawn config.");
        merge(userEntries, result);
      }
    } catch (Exception e) {
      Log.error("Could not load user defined spawn entries from file: " + CONFIG_NAME_USER);
      e.printStackTrace();
    }
    return result;
  }
  
  private static void merge(List<SpawnEntry> userEntries, List<SpawnEntry> result) {
    for(SpawnEntry entry : userEntries) {
      removeFrom(entry, result);
      result.add(entry);
    }    
  }

  private static void removeFrom(SpawnEntry useEntry, List<SpawnEntry> result) {
    SpawnEntry toRemove = null;
    for(SpawnEntry entry : result) {
      if(useEntry.getId().equals(entry.getId())) {
        toRemove = entry;
        break;
      }
    }
    if(toRemove != null) {
      Log.info("Replace spawn config for " + toRemove.getId() + " with user supplied entry.");
      result.remove(toRemove);
    }    
  }

  private static String readConfigFile(File copyTo, String fileName, boolean replaceIfExists) throws IOException {
    if(!replaceIfExists && copyTo.exists()) {
      return readStream(new FileInputStream(copyTo));
    }
    InputStream in = SpawnConfig.class.getResourceAsStream(RESOURCE_PATH + fileName);
    if(in == null) {
      throw new IOException("Could not load resource "+ RESOURCE_PATH + fileName + " form classpath. ");
    }
    String output = readStream(in);
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(copyTo, false));
      writer.write(output.toString());
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return output.toString();
  }

  private static String readStream(InputStream in) throws IOException {
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
