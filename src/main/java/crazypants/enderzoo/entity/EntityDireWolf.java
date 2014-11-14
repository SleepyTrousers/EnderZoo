package crazypants.enderzoo.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class EntityDireWolf extends EntityMob implements IEnderZooMob {

  public static final String NAME = "enderzoo.DireWolf";
  public static final int EGG_BG_COL = 0x606060;
  public static final int EGG_FG_COL = 0xA0A0A0;

  private static final float DEF_HEIGHT = 0.8F;
  private static final float DEF_WIDTH = 0.6F;

  public EntityDireWolf(World world) {
    super(world);
    setSize(0.8F, 1.2F);
    //setSize(0.9F, 1.2F);
    getNavigator().setAvoidsWater(true);
  }

  @Override
  protected boolean isAIEnabled() {
    return true;
  }

  public boolean isAngry() {
    return false;
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
    MobInfo.DIRE_WOLF.applyAttributes(this);
  }

  @Override
  protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
    playSound("mob.wolf.step", 0.15F, 1.0F);
  }

  @Override
  protected String getLivingSound() {
    return "mob.wolf.growl";
  }

  @Override
  protected String getHurtSound() {
    return "mob.wolf.hurt";
  }

  @Override
  protected String getDeathSound() {
    return "mob.wolf.death";
  }

  @Override
  public float getEyeHeight() {
    return height * 0.8F;
  }

  @Override
  protected float getSoundVolume() {
    return 0.4F;
  }

  @Override
  protected Item getDropItem() {
    return Item.getItemById(-1);
  }

  public float getTailRotation() {
    //straight back
    //return (float) Math.PI / 2;
    //straight up
    //return (float) Math.PI;
    return (float) Math.PI / 4;
  }

  @Override
  public void setPosition(double x, double y, double z) {
    posX = x;
    posY = y;
    posZ = z;
    //Correct misalignment of bounding box    
    double hw = width / 2.0F;
    double hd = hw * 2.25;
    float f1 = height;
    boundingBox.setBounds(
        x - hw, y - yOffset + ySize, z - hd,
        x + hw, y - yOffset + ySize + f1, z + hd);
  }

}
