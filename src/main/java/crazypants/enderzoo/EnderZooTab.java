package crazypants.enderzoo;

import static crazypants.enderzoo.EnderZoo.MODID;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderZooTab extends CreativeTabs {

  public static final CreativeTabs tabEnderZoo = new EnderZooTab();

  public EnderZooTab() {
    super(MODID);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTabLabel() {
    return MODID;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public String getTranslatedTabLabel() {
    return MODID;
  }

  @Override
  public Item getTabIconItem() {
    return EnderZoo.itemForCreativeMenuIcon;
  }

}
