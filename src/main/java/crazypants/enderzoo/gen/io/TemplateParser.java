package crazypants.enderzoo.gen.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.StructureRegister;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.StructureGenerator;
import crazypants.enderzoo.gen.structure.preperation.ISitePreperation;
import crazypants.enderzoo.gen.structure.sampler.ILocationSampler;
import crazypants.enderzoo.gen.structure.validator.ILocationValidator;

public class TemplateParser {

  private final CompositeRuleFactory ruleFact = new DefaultRuleFactory();

  public TemplateParser() {
  }

  public CompositeRuleFactory getRuleFactory() {
    return ruleFact;
  }

  public StructureGenerator parseTemplate(StructureRegister reg, String json) throws Exception {
    String uid = null;
    try {
      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureTemplate");

      uid = to.get("uid").getAsString();
      String dataUid = to.get("dataUid").getAsString();
      StructureTemplate sd = reg.getStructureData(dataUid);

      StructureGenerator res = new StructureGenerator(uid, sd);
      if(to.has("canSpanChunks")) {
        res.setCanSpanChunks(to.get("canSpanChunks").getAsBoolean());
      }
      if(to.has("maxAttemptsPerChunk")) {
        res.setMaxInChunk(to.get("maxAttemptsPerChunk").getAsInt());
      }
      if(to.has("maxGeneratedPerChunk")) {
        res.setMaxInChunk(to.get("maxGeneratedPerChunk").getAsInt());
      }

      if(to.has("LocationSampler")) {
        JsonObject ls = to.getAsJsonObject("LocationSampler");
        String samType = ls.get("uid").getAsString();
        ILocationSampler samp = ruleFact.createSampler(samType, ls);
        if(samp != null) {
          res.setLocationSampler(samp);
        } else {
          throw new Exception("Could not parse location sampler for " + uid);
        }
      }

      if(to.has("locationValidators")) {
        JsonArray arr = to.getAsJsonArray("locationValidators");
        for (JsonElement e : arr) {
          JsonObject valObj = e.getAsJsonObject();
          String id = valObj.get("uid").getAsString();
          ILocationValidator val = ruleFact.createValidator(id, valObj);
          if(val != null) {
            res.addLocationValidator(val);
          } else {
            throw new Exception("Could not parse validator: " + id + " for template: " + uid);
          }
        }           
      }

      if(to.has("sitePreperations")) {
        
        JsonArray arr = to.getAsJsonArray("sitePreperations");
        for (JsonElement e : arr) {
          JsonObject valObj = e.getAsJsonObject();
          String id = valObj.get("uid").getAsString();
          ISitePreperation val = ruleFact.createPreperation(id, valObj);
          if(val != null) {
            res.addSitePreperation(val);
          } else {
            throw new Exception("Could not parse validator: " + id + " for template: " + uid);
          }
        }
                
      }

      return res;

    } catch (Exception e) {
      throw new Exception("TemplateParser: Could not parse template " + uid, e);
    }

  }

}
