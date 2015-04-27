package crazypants.enderzoo.gen.io;

import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.rules.ILocationSampler;

public interface ISamplerFactory extends IFactory {

  ILocationSampler createSampler(String uid, JsonObject json);
  
}
