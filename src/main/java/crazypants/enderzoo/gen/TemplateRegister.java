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
import crazypants.enderzoo.gen.structure.StructureData;
import crazypants.enderzoo.gen.structure.StructureTemplate;

public class TemplateRegister {

  public static final TemplateRegister instance = createInstance();

  private static TemplateRegister createInstance() {
    TemplateRegister reg = new TemplateRegister();
    reg.init();
    return reg;
  }
  
  private final Map<String, StructureTemplate> templates = new HashMap<String, StructureTemplate>();
  private final Map<String, StructureData> data = new HashMap<String, StructureData>();
  
  private StructureResourceManager resourceManager;

  private TemplateRegister() {
  }

  private void init() {
    resourceManager = new StructureResourceManager(this);   
  }
  
  public StructureResourceManager getResourceManager() {
    return resourceManager;
  }
  
  public void registerJsonTemplate(String json) throws Exception {    
    StructureTemplate tp = resourceManager.parseJsonTemplate(json);
    registerTemplate(tp);    
  }

  public void registerTemplate(StructureTemplate template) {
    templates.put(template.getUid(), template);
  }

  public StructureTemplate getTemplate(String uid) {
    return templates.get(uid);
  }

  public Collection<StructureTemplate> getTemplates() {
    return templates.values();
  }

  public void registerStructureData(String uid, NBTTagCompound nbt) throws IOException {
    data.put(uid, new StructureData(nbt));
  }

  public void registerStructureData(String uid, StructureData sd) {
    data.put(uid, sd);
  }

  public StructureData getStructureData(String uid) {    
    if(data.containsKey(uid)) {
      return data.get(uid);
    }
    StructureData sd = null;
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
