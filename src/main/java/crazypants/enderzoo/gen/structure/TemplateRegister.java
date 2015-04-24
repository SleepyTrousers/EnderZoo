package crazypants.enderzoo.gen.structure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;

public class TemplateRegister {

  public static final TemplateRegister instance = new TemplateRegister();

  private final Map<String, StructureTemplate> templates = new HashMap<String, StructureTemplate>();

  private TemplateRegister() {
  }

  public void registerTemplate(String uid) throws IOException {
    InputStream ds = getStreamForStructureData(uid);
    if(ds != null) {
      StructureData data = new StructureData(ds);
      templates.put(uid, new StructureTemplate(data));
    }
  }

  public void registerTemplate(StructureTemplate template) {
    templates.put(template.getUid(), template);
  }

  public void registerTemplateAndLog(String name) {
    try {
      registerTemplate(name);
    } catch (Exception e) {
      Log.error("Could not load structure [" + name + "] Exception: " + e);
    }
  }

  public void loadDefaultTemplates() {
    registerTemplateAndLog("test");
  }

  public StructureTemplate getTemplate(String uid) {
    return templates.get(uid);
  }

  public Collection<StructureTemplate> getTemplates() {
    return templates.values();
  }

  private InputStream getStreamForStructureData(String uid) throws IOException {
    File f = new File(Config.configDirectory + "/structures/" + uid + ".nbt");
    if(f.exists()) {
      return new FileInputStream(f);
    }
    return StructureTemplate.class.getResourceAsStream("/assets/enderzoo/structures/" + uid + ".nbt");
  }

}