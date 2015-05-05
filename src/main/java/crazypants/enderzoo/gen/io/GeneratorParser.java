package crazypants.enderzoo.gen.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.StructureRegister;
import crazypants.enderzoo.gen.structure.Structure.Rotation;
import crazypants.enderzoo.gen.structure.StructureGenerator.InstanceGen;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.StructureGenerator;
import crazypants.enderzoo.gen.structure.preperation.ISitePreperation;
import crazypants.enderzoo.gen.structure.sampler.ILocationSampler;
import crazypants.enderzoo.gen.structure.validator.ILocationValidator;

public class GeneratorParser {

  private final CompositeRuleFactory ruleFact = new DefaultRuleFactory();

  public GeneratorParser() {
  }

  public CompositeRuleFactory getRuleFactory() {
    return ruleFact;
  }

  public StructureGenerator parseTemplate(StructureRegister reg, String json) throws Exception {
    String uid = null;
    StructureGenerator res = null;
    try {
      JsonObject to = new JsonParser().parse(json).getAsJsonObject();
      to = to.getAsJsonObject("StructureGenerator");

      uid = to.get("uid").getAsString();

      res = new StructureGenerator(uid);
      if(to.has("canSpanChunks")) {
        res.setCanSpanChunks(to.get("canSpanChunks").getAsBoolean());
      }
      if(to.has("maxAttemptsPerChunk")) {
        res.setMaxInChunk(to.get("maxAttemptsPerChunk").getAsInt());
      }
      if(to.has("maxGeneratedPerChunk")) {
        res.setMaxInChunk(to.get("maxGeneratedPerChunk").getAsInt());
      }

      if(!to.has("templates")) {
        throw new Exception("No templates field found in definition for " + uid);
      }
      JsonArray templates = to.get("templates").getAsJsonArray();
      for (JsonElement e : templates) {
        JsonObject valObj = e.getAsJsonObject();
        if(!valObj.isJsonNull() && valObj.has("uid")) {
          String tpUid = valObj.get("uid").getAsString();
          StructureTemplate st = reg.getStructureTemplate(tpUid, true);

          List<Rotation> rots = new ArrayList<Rotation>();
          if(valObj.has("rotations")) {
            for (JsonElement rot : valObj.get("rotations").getAsJsonArray()) {
              if(!rot.isJsonNull()) {
                Rotation r = Rotation.get(rot.getAsInt());
                if(r != null) {
                  rots.add(r);
                }
              }
            }
          }
          InstanceGen gen = new InstanceGen(st, rots);
          res.addInstanceGen(gen);
        }
      }
      if(res.getInstanceGens().isEmpty()) {
        throw new Exception("No valid template found in definition for " + uid);
      }

      if(to.has("LocationSampler")) {
        JsonObject ls = to.getAsJsonObject("LocationSampler");
        String samType = ls.get("type").getAsString();
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
          if(e.isJsonObject()) {
            JsonObject valObj = e.getAsJsonObject();
            if(!valObj.isJsonNull() && valObj.has("type")) {
              String id = valObj.get("type").getAsString();
              ILocationValidator val = ruleFact.createValidator(id, valObj);
              if(val != null) {
                res.addLocationValidator(val);
              } else {
                throw new Exception("Could not parse validator: " + id + " for template: " + uid);
              }
            }
          }
        }
      }

      if(to.has("sitePreperations")) {

        JsonArray arr = to.getAsJsonArray("sitePreperations");
        for (JsonElement e : arr) {
          JsonObject valObj = e.getAsJsonObject();
          if(!valObj.isJsonNull() && valObj.has("type")) {
            String id = valObj.get("type").getAsString();
            ISitePreperation val = ruleFact.createPreperation(id, valObj);
            if(val != null) {
              res.addSitePreperation(val);
            } else {
              throw new Exception("Could not parse validator: " + id + " for template: " + uid);
            }
          }
        }

      }

    } catch (Exception e) {
      throw new Exception("TemplateParser: Could not parse template " + uid, e);
    }

    if(res == null || !res.isValid()) {
      throw new Exception("GeneratorParser: Could not create a valid generator for " + res);
    }

    return res;

  }

}
