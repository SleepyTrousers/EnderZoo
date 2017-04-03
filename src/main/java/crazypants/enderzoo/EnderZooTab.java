package crazypants.enderzoo;

import static crazypants.enderzoo.EnderZoo.MODID;
import static crazypants.enderzoo.EnderZoo.MOD_NAME;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    return MOD_NAME;
  }

  @Override
  public ItemStack getTabIconItem() {
    return new ItemStack(EnderZoo.itemForCreativeMenuIcon);    
  }

}
