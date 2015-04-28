package crazypants.enderzoo.gen.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.Border;

public class JsonUtil {

  public static boolean getBooleanElement(JsonObject json, String field, boolean def) {
    if(json.has(field)) {
      String str = json.get(field).getAsString();      
      if("true".equalsIgnoreCase(str)) {
        return true;
      }
      if("false".equalsIgnoreCase(str)) {
        return false;
      }
      return def;              
    }
    return def;
  }

  public static List<String> getStringArrayElement(JsonObject obj, String element) {
    JsonElement je = obj.get(element);
    if(je == null) {
      return Collections.emptyList();
    }
    if(!je.isJsonArray()) {
      return Collections.emptyList();
    }
    List<String> res = new ArrayList<String>();
    JsonArray arr = je.getAsJsonArray();
    for (int i = 0; i < arr.size(); i++) {
      JsonElement e = arr.get(i);
      if(e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
        res.add(e.getAsString());
      }
    }
    return res;
  }

  public static int getIntElement(JsonObject obj, String element, int def) {
    JsonElement je = obj.get(element);
    if(je == null) {
      return def;
    }
    if(je.isJsonPrimitive() && je.getAsJsonPrimitive().isNumber()) {
      return je.getAsInt();
    }
    return def;
  }
  
  public static float getFloatElement(JsonObject obj, String element, float def) {
    JsonElement je = obj.get(element);
    if(je == null) {
      return def;
    }
    if(je.isJsonPrimitive() && je.getAsJsonPrimitive().isNumber()) {
      return je.getAsFloat();
    }
    return def;
  }

  public static String getStringElement(JsonObject obj, String element, String def) {
    JsonElement je = obj.get(element);
    if(je == null) {
      return def;
    }
    if(je.isJsonPrimitive() && je.getAsJsonPrimitive().isString()) {
      return je.getAsString();
    }
    return def;
  }
  
  public static Border getBorder(JsonObject parent, Border def) {    
    JsonObject obj = parent.getAsJsonObject("Border");
    if(obj == null) {
      return def;
    }    
    Border border = new Border();    
    if(obj.has("sizeXZ")) {
      border.setBorderXZ(getIntElement(obj, "sizeXZ", border.get(ForgeDirection.NORTH)));
    }
    for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      border.set(dir, JsonUtil.getIntElement(obj, dir.name().toLowerCase(), border.get(dir)));
    }
    return border;
  }

}
