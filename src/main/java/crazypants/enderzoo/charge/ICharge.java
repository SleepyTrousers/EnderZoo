package crazypants.enderzoo.charge;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface ICharge {

  void setID(int id);

  int getID();

  void explode(EntityPrimedCharge entity);

  @SideOnly(Side.CLIENT)
  void explodeEffect(World world, double x, double y, double z);

  Block getBlock();

}
