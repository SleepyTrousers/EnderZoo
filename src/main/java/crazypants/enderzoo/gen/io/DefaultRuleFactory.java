package crazypants.enderzoo.gen.io;

import java.util.List;

import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import com.google.gson.JsonObject;

import crazypants.enderzoo.Log;
import crazypants.enderzoo.gen.structure.preperation.ClearPreperation;
import crazypants.enderzoo.gen.structure.preperation.FillPreperation;
import crazypants.enderzoo.gen.structure.preperation.ISitePreperation;
import crazypants.enderzoo.gen.structure.sampler.ILocationSampler;
import crazypants.enderzoo.gen.structure.sampler.SurfaceLocationSampler;
import crazypants.enderzoo.gen.structure.validator.BiomeValidator;
import crazypants.enderzoo.gen.structure.validator.DimensionValidator;
import crazypants.enderzoo.gen.structure.validator.ILocationValidator;
import crazypants.enderzoo.gen.structure.validator.LevelGroundValidator;
import crazypants.enderzoo.gen.structure.validator.RandomValidator;
import crazypants.enderzoo.gen.structure.validator.SpacingValidator;
import crazypants.enderzoo.spawn.IBiomeFilter;
import crazypants.enderzoo.spawn.impl.BiomeDescriptor;
import crazypants.enderzoo.spawn.impl.BiomeFilterAll;
import crazypants.enderzoo.spawn.impl.BiomeFilterAny;

public class DefaultRuleFactory extends CompositeRuleFactory {

  public DefaultRuleFactory() {    
    add(new SurfaceSamplerFact());
    add(new RandomValFac());
    add(new DimValFact());    
    add(new SpacingValFact());
    add(new LevGrndFact());
    add(new BiomeValFact());
    add(new FillPrepFact());
    add(new ClearPrepFact());    
  }

  static class InnerFactory implements IRuleFactory {

    final String uid;

    InnerFactory(String uid) {
      this.uid = uid;
    }

    @Override
    public boolean canCreate(String uid) {      
      return this.uid.equals(uid);
    }

    @Override
    public ILocationSampler createSampler(String uid, JsonObject json) {
      return null;
    }

    @Override
    public ILocationValidator createValidator(String uid, JsonObject json) {
      return null;
    }

    @Override
    public ISitePreperation createPreperation(String uid, JsonObject json) {
      return null;
    }
  }

  //-----------------------------------------------------------------
  static class SurfaceSamplerFact extends InnerFactory {

    SurfaceSamplerFact() {
      super("SurfaceSampler");
    }

    @Override
    public crazypants.enderzoo.gen.structure.sampler.ILocationSampler createSampler(String uid, JsonObject json) {
      SurfaceLocationSampler res = new SurfaceLocationSampler();
      res.setDistanceFromSurface(JsonUtil.getIntElement(json, "distanceFromSurface", res.getDistanceFromSurface()));      
      res.setCanGenerateOnFluid(JsonUtil.getBooleanElement(json, "canGenerateOnFluid", res.isCanPlaceInFluid()));      
      return res;
    }

  }

  //-----------------------------------------------------------------
  static class RandomValFac extends InnerFactory {
    RandomValFac() {
      super("RandomValidator");
    }
    
    @Override
    public ILocationValidator createValidator(String uid, JsonObject json) {
      RandomValidator res = new RandomValidator();
      res.setChancePerChunk(JsonUtil.getFloatElement(json, "chancePerChunk", res.getChancePerChunk()));
      return res;
    }
  }
  
  //-----------------------------------------------------------------
  static class DimValFact extends InnerFactory {

    DimValFact() {
      super("DimensionValidator");
    }

    @Override
    public ILocationValidator createValidator(String uid, JsonObject json) {
      DimensionValidator res = new DimensionValidator();
      res.addAll(JsonUtil.getStringArrayElement(json, "names"), false);
      res.addAll(JsonUtil.getStringArrayElement(json, "namesExcluded"), true);      
      return res;
    }
  }
  
  //-----------------------------------------------------------------
  static class SpacingValFact extends InnerFactory {
    
    public SpacingValFact() {
      super("SpacingValidator");
    }
    
    
    @Override
    public ILocationValidator createValidator(String uid, JsonObject json) {
      SpacingValidator res = new SpacingValidator();
      res.setMinSpacing(JsonUtil.getIntElement(json, "minSpacing", res.getMinSpacing()));      
      res.setValidateLocation(JsonUtil.getBooleanElement(json, "validateLocation", res.isValidateLocation()));
      res.setValidateChunk(JsonUtil.getBooleanElement(json, "validateChunk", res.isValidateChunk()));      
      res.setTemplateFilter(JsonUtil.getStringArrayElement(json, "templateFilter"));           
      return res;
    }
  }
  
  //-----------------------------------------------------------------
  static class LevGrndFact extends InnerFactory {
    public LevGrndFact() {
      super("LevelGroundValidator");
    }
    
    @Override
    public ILocationValidator createValidator(String uid, JsonObject json) {
      LevelGroundValidator res = new LevelGroundValidator();
      res.setCanSpawnOnWater(JsonUtil.getBooleanElement(json, "canSpawnOnWater", res.isCanSpawnOnWater()));
      res.setTolerance(JsonUtil.getIntElement(json, "tolerance", res.getTolerance()));
      res.setSampleSpacing(JsonUtil.getIntElement(json, "sampleSpacing", res.getTolerance()));
      res.setMaxSampleCount(JsonUtil.getIntElement(json, "maxSamples", res.getTolerance()));
      res.setBorder(JsonUtil.getBorder(json, res.getBorder()));
      return res;
    }
  }
  
  //-----------------------------------------------------------------
  static class BiomeValFact extends InnerFactory {

    BiomeValFact() {
      super("BiomeValidator");
    }

    @Override
    public ILocationValidator createValidator(String uid, JsonObject json) {
      String typeElement = JsonUtil.getStringElement(json, "type", "any");
      IBiomeFilter filter;
      if("all".equals(typeElement)) {
        filter = new BiomeFilterAll();
      } else {
        filter = new BiomeFilterAny();
      }      
      addBiomeTypes(filter, JsonUtil.getStringArrayElement(json, "types"), false);
      addBiomeTypes(filter, JsonUtil.getStringArrayElement(json, "typesExcluded"), true);
      addBiomesByName(filter, JsonUtil.getStringArrayElement(json, "names"), false);
      addBiomesByName(filter, JsonUtil.getStringArrayElement(json, "namesExcluded"), true);

      return new BiomeValidator(filter);
    }

    private void addBiomesByName(IBiomeFilter filter, List<String> names, boolean isExcluded) {
      for (String name : names) {
        filter.addBiomeDescriptor(new BiomeDescriptor(name, isExcluded));
      }
    }

    private boolean addBiomeTypes(IBiomeFilter filter, List<String> types, boolean isExclude) {
      for (String typeStr : types) {
        try {
          Type type = BiomeDictionary.Type.valueOf(typeStr);
          filter.addBiomeDescriptor(new BiomeDescriptor(type, isExclude));
        } catch (Exception e) {
          Log.error("DefaultRuleFactory.BiomeValFact: Could not create biome type : " + typeStr);
          return false;
        }
      }
      return true;
    }
  }
  
  //-----------------------------------------------------------------
  static class ClearPrepFact extends InnerFactory {

    ClearPrepFact() {
      super("ClearPreperation");
    }

    @Override
    public ISitePreperation createPreperation(String uid, JsonObject json) {
      ClearPreperation res = new ClearPreperation();
      res.setClearPlants(JsonUtil.getBooleanElement(json, "clearPlants", res.isClearPlants()));
      res.setBorder(JsonUtil.getBorder(json, res.getBorder()));
      return res;
    }
        
  }

  //-----------------------------------------------------------------
  static class FillPrepFact extends InnerFactory {

    FillPrepFact() {
      super("FillPreperation");
    }

    @Override
    public ISitePreperation createPreperation(String uid, JsonObject json) {
      FillPreperation res = new FillPreperation();
      res.setClearPlants(JsonUtil.getBooleanElement(json, "clearPlants", res.isClearPlants()));
      res.setBorder(JsonUtil.getBorder(json, res.getBorder()));
      return res;
    }
        
  }
  
 


}
