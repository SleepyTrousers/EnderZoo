package crazypants.enderzoo.entity;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.config.Config;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityDireSlime extends EntityMagmaCube implements IEnderZooMob {

  public static final String NAME = "enderzoo.DireSlime";
  public static final int EGG_BG_COL = 0xb9855c;
  public static final int EGG_FG_COL = 0x593d29;

  public EntityDireSlime(World p_i1737_1_) {
    super(p_i1737_1_);
    int i = this.rand.nextInt(100);
    setSlimeSize(i < (100 - Config.direSlimeBigPercentage - Config.direSlimeMediumPercentage) ? 1 : i < (100 - Config.direSlimeBigPercentage) ? 2 : 4);
  }

  protected String getSlimeParticle() {
    return "blockcrack_" + Block.getIdFromBlock(Blocks.dirt) + "_0";
  }

  protected EntitySlime createInstance() {
    return new EntityDireSlime(this.worldObj);
  }

  protected Item getDropItem() {
    return Items.clay_ball;
  }

  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender(float p_70070_1_) {
    int i = MathHelper.floor_double(this.posX);
    int j = MathHelper.floor_double(this.posZ);

    if (this.worldObj.blockExists(i, 0, j)) {
      double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
      int k = MathHelper.floor_double(this.posY - (double) this.yOffset + d0);
      return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
    } else {
      return 0;
    }
  }

  public float getBrightness(float p_70013_1_) {
    int i = MathHelper.floor_double(this.posX);
    int j = MathHelper.floor_double(this.posZ);

    if (this.worldObj.blockExists(i, 0, j)) {
      double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
      int k = MathHelper.floor_double(this.posY - (double) this.yOffset + d0);
      return this.worldObj.getLightBrightness(i, k, j);
    } else {
      return 0.0F;
    }
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
  }

  @Override
  protected int getAttackStrength() {
    return (int) (this.getSlimeSize() + this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue());
  }

  @Override
  protected void setSlimeSize(int p_70799_1_) {
    super.setSlimeSize(p_70799_1_);
    this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(Config.direSlimeHealth + (double) (p_70799_1_ * p_70799_1_));
    this.setHealth(this.getMaxHealth());
    this.setSize(1f, 1f);
    this.setPosition(this.posX, this.posY, this.posZ);
  }

  @Override
  public void onUpdate() {
    super.onUpdate();
    if (this.worldObj.isRemote) {
      this.setSize(1f, 1f);
    }
  }

}
