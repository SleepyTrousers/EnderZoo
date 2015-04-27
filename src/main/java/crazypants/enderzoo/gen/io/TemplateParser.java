package crazypants.enderzoo.gen.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.TemplateRegister;
import crazypants.enderzoo.gen.structure.StructureData;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.rules.ILocationSampler;

public class TemplateParser {

  private final CompositeRuleFactory ruleFact = new CompositeRuleFactory();
  
  public TemplateParser() {
    ruleFact.add(new DefaultRuleFactory());
  }

  public CompositeRuleFactory getRuleFactory() {
    return ruleFact;
  }

  public StructureTemplate parseTemplate(TemplateRegister reg, String json) throws Exception {
    String uid = null;
    try {
      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureTemplate");

      uid = to.get("uid").getAsString();
      String dataUid = to.get("dataUid").getAsString();
      StructureData sd = reg.getStructureData(dataUid);

      StructureTemplate res = new StructureTemplate(uid, sd);
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
      
      
      return res;
      
    } catch (Exception e) {
      throw new Exception("TemplateParser: Could not parse template " + uid, e);
    }

  }

}
