package crazypants.enderzoo.gen.structure;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.Log;

public class TemplateParser {

  private static final String EXT = ".ezs";

  public TemplateParser(String name) {
    String configText;
    JsonElement root;
    JsonObject rootObj;
    JsonObject costsObj;
    JsonArray blkList;
    try {
      //Core
      File copyTo = new File(TemplateRegister.ROOT_DIR, name + EXT);
      configText = IoUtil.readConfigFile(copyTo, TemplateRegister.RESOURCE_PATH + name + EXT, false);
      root = new JsonParser().parse(configText);
      rootObj = root.getAsJsonObject();

      //      costsObj = rootObj.getAsJsonObject("costMultiplier");      
      //      for (Entry<String, JsonElement> entry : costsObj.entrySet()) {
      //        costs.put(entry.getKey(), Double.valueOf(entry.getValue().getAsDouble()));
      //      }
      //
      //      blkList = rootObj.getAsJsonArray("blackList");
      //      if(blkList != null) {
      //        for (int i = 0; i < blkList.size(); i++) {
      //          String s = blkList.get(i).getAsString();
      //          blackList.add(s);
      //        }
      //      } else {
      //        Log.warn("No black list for powered spawner found in " + IoUtil.getConfigFile(CORE_FILE_NAME).getAbsolutePath());
      //      }

    } catch (Exception e) {
      Log.error("TemplateParser: Could not load Template " + name + " ex: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
