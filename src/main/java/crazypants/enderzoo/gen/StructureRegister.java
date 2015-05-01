package crazypants.enderzoo.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.gen.io.StructureResourceManager;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.StructureGenerator;

public class StructureRegister {

  public static final StructureRegister instance = createInstance();

  private static StructureRegister createInstance() {
    StructureRegister reg = new StructureRegister();
    reg.init();
    return reg;
  }
  
  private final Map<String, StructureGenerator> configs = new HashMap<String, StructureGenerator>();
  private final Map<String, StructureTemplate> data = new HashMap<String, StructureTemplate>();
  
  private StructureResourceManager resourceManager;

  private StructureRegister() {
  }

  private void init() {
    resourceManager = new StructureResourceManager(this);   
  }
  
  public StructureResourceManager getResourceManager() {
    return resourceManager;
  }
  
  public void registerJsonConfig(String json) throws Exception {    
    StructureGenerator tp = resourceManager.parseJsonTemplate(json);
    registerConfig(tp);    
  }

  public void registerConfig(StructureGenerator template) {
    configs.put(template.getUid(), template);
  }

  public StructureGenerator getConfig(String uid) {
    return configs.get(uid);
  }

  public Collection<StructureGenerator> getConfigs() {
    return configs.values();
  }

  public void registerStructureData(String uid, NBTTagCompound nbt) throws IOException {
    data.put(uid, new StructureTemplate(nbt));
  }

  public void registerStructureData(String uid, StructureTemplate sd) {
    data.put(uid, sd);
  }

  public StructureTemplate getStructureData(String uid) {    
    if(data.containsKey(uid)) {
      return data.get(uid);
    }
    StructureTemplate sd = null;
    try {
      sd = resourceManager.loadStructureData(uid);
    } catch (IOException e) {
      Log.error("TemplateRegister: Could not load structure data for " + uid + " Ex: " + e);
      e.printStackTrace();
    } finally {
      data.put(uid, sd);
    }
    return sd;
  }

}
