package crazypants.enderzoo.gen;

import java.io.File;
import java.io.IOException;

import org.apache.commons.compress.utils.IOUtils;

import crazypants.enderzoo.IoUtil;
import crazypants.enderzoo.Log;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.gen.io.StructureResourceManager;

public class EnderZooStructures {

  public static final File ROOT_DIR = new File(Config.configDirectory + "/structures/");
  public static final String RESOURCE_PATH = "/assets/enderzoo/config/structures/";

  public static void registerStructures() {
    TemplateRegister reg = TemplateRegister.instance;
    reg.getResourceManager().addResourcePath(ROOT_DIR);
    reg.getResourceManager().addResourcePath(RESOURCE_PATH);

    register("test");
  }

  private static void register(String uid) {
    try {
      File copyTo = new File(ROOT_DIR, uid + StructureResourceManager.TEMPLATE_EXT);
      String str = IoUtil.readConfigFile(copyTo, RESOURCE_PATH + uid + StructureResourceManager.TEMPLATE_EXT, false);
      TemplateRegister.instance.registerJsonTemplate(str);
    } catch (Exception e) {
      Log.error("EnderZooStructures: Error occured loading template: " + uid + " Ex: " + e);
      e.printStackTrace();
    }

  }

}
