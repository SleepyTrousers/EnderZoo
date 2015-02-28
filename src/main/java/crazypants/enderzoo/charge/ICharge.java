package crazypants.enderzoo.charge;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICharge {

  void setID(int id);

  int getID();

  void explode(EntityPrimedCharge entity);

  @SideOnly(Side.CLIENT)
  void explodeEffect(World world, double x, double y, double z);

  Block getBlock();

}
