package crazypants.enderzoo.gen.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonObject;

import crazypants.enderzoo.gen.structure.rules.ILocationSampler;
import crazypants.enderzoo.gen.structure.rules.ILocationValidator;
import crazypants.enderzoo.gen.structure.rules.ISitePreperation;
import crazypants.enderzoo.gen.structure.rules.SurfaceLocationSampler;

public class DefaultRuleFactory implements IRuleFactory {

  private static final String UID_SURFACE_SAMPLER = "SurfaceSampler";
  
  private final Set<String> uids = new HashSet<String>();

  private final Map<String, ISamplerFactory> samplers = new HashMap<String, ISamplerFactory>();
  
  public DefaultRuleFactory() {
    samplers.put(UID_SURFACE_SAMPLER, new SurfaceSamplerFact());    
  }

  @Override
  public boolean canCreate(String uid) {
    return uids.contains(uid);
  }

  @Override
  public ILocationSampler createSampler(String uid, JsonObject json) {
    ISamplerFactory fact = samplers.get(uid);
    if(fact == null) {
      return null;
    }
    return fact.createSampler(uid, json);
  }

  @Override
  public ILocationValidator createValidator(String uid, JsonObject json) {
    return null;
  }

  @Override
  public ISitePreperation createPreperation(String uid, JsonObject json) {

    return null;
  }

  private class InnerFactory implements IFactory {
    
    final String uid;
    
    InnerFactory(String uid) {
      this.uid = uid;
      uids.add(uid);
    }
    @Override
    public boolean canCreate(String uid) {    
      return uid.equals(uid);
    }    
  }
  
  private class SurfaceSamplerFact extends InnerFactory implements ISamplerFactory {

    SurfaceSamplerFact() {
      super(UID_SURFACE_SAMPLER);
    }

    @Override
    public ILocationSampler createSampler(String uid, JsonObject json) {
      SurfaceLocationSampler res = new SurfaceLocationSampler();
      if(json.has("distanceFromSurface")) {
        res.setDistanceFromSurface(json.get("distanceFromSurface").getAsInt());
      }
      if(json.has("canGenerateOnFluid")) {
        res.setCanGenerateOnFluid("true".equalsIgnoreCase(json.get("canGenerateOnFluid").getAsString()));
      }
      return res;
    }
    
  }

}
