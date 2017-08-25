package crazypants.enderzoo.config;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.config.Config.Section;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class GuiConfigFactoryEnderZoo extends GuiConfig {

  public GuiConfigFactoryEnderZoo(GuiScreen parentScreen) {
    super(parentScreen, getConfigElements(parentScreen), EnderZoo.MODID, false, false, EnderZoo.proxy.translate("enderzoo.config.title"));
  }

  private static List<IConfigElement> getConfigElements(GuiScreen parent) {
    List<IConfigElement> list = new ArrayList<IConfigElement>();
    String prefix = "enderzoo.config.";

    for (Section section : Config.sections) {
      list.add(new ConfigElement(Config.config.getCategory(section.lc()).setLanguageKey(prefix + section.lang)));
    }

    return list;
  }

}
