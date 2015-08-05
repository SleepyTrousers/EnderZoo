package crazypants.enderzoo.config;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.spawn.ISpawnEntry;
import crazypants.enderzoo.spawn.impl.SpawnEntry;

public class SpawnConfig {

  public static final String CONFIG_NAME_CORE = "SpawnConfig_Core.xml";
  public static final String CONFIG_NAME_USER = "SpawnConfig_User.xml";

  public static List<SpawnEntry> loadSpawnConfig() {
    File coreFile = new File(Config.configDirectory, CONFIG_NAME_CORE);

    String defaultVals = null;
    try {
      defaultVals = IoUtil.readConfigFile(coreFile, Config.CONFIG_RESOURCE_PATH + CONFIG_NAME_CORE, true);
    } catch (IOException e) {
      Log.error("Could not load core spawn config file " + coreFile + " from EnderZoo jar: " + e.getMessage());
      e.printStackTrace();
      return null;
    }

    if (!coreFile.exists()) {
      Log.error("Could not load core config from " + coreFile + " as the file does not exist.");
      return null;
    }

    List<SpawnEntry> result;
    try {
      result = SpawnConfigParser.parseSpawnConfig(defaultVals);
    } catch (Exception e) {
      Log.error("Error parsing " + CONFIG_NAME_CORE + ":" + e);
      return Collections.emptyList();
    }
    Log.info("Loaded " + result.size() + " entries from core spawn config.");

    File userFile = new File(Config.configDirectory, CONFIG_NAME_USER);
    String userText = null;
    try {
      userText = IoUtil.readConfigFile(userFile, Config.CONFIG_RESOURCE_PATH + CONFIG_NAME_USER, false);
      if (userText == null || userText.trim().length() == 0) {
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
    for (SpawnEntry entry : userEntries) {
      removeFrom(entry, result);
      result.add(entry);
    }
  }

  private static void removeFrom(ISpawnEntry useEntry, List<SpawnEntry> result) {
    ISpawnEntry toRemove = null;
    for (ISpawnEntry entry : result) {
      if (useEntry.getId().equals(entry.getId())) {
        toRemove = entry;
        break;
      }
    }
    if (toRemove != null) {
      Log.info("Replace spawn config for " + toRemove.getId() + " with user supplied entry.");
      result.remove(toRemove);
    }
  }

}
