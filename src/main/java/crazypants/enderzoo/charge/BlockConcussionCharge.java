package crazypants.enderzoo.charge;

import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.config.Config;

public class BlockConcussionCharge extends BlockConfusingCharge {

  private static final String NAME = "blockConcussionCharge";

  public static BlockConcussionCharge create() {
    if (!Config.concussionChargeEnabled) {
      return null;
    }

    BlockConcussionCharge res = new BlockConcussionCharge();
    res.init();
    return res;
  }

  protected BlockConcussionCharge() {
    super(NAME);
  }

  @Override
  public void explode(EntityPrimedCharge entity) {
    super.explode(entity);
    BlockEnderCharge.doEntityTeleport(entity);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void explodeEffect(World world, double x, double y, double z) {
    super.explodeEffect(world, x, y, z);
    BlockEnderCharge.doTeleportEffect(world, x, y, z);
  }

}
