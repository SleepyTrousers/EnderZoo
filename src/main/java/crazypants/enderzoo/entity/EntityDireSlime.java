package crazypants.enderzoo.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.config.Config;

public class EntityDireSlime extends EntityMagmaCube implements IEnderZooMob {

  public static final String NAME = "enderzoo.DireSlime";
  public static final int EGG_BG_COL = 0xb9855c;
  public static final int EGG_FG_COL = 0x593d29;

  public enum SlimeConf {

    SMALL(1, Config.direSlimeHealth, Config.direSlimeAttackDamage, 1 - (Config.direSlimeChanceLarge - Config.direSlimeChanceMedium)), MEDIUM(2,
        Config.direSlimeHealthMedium, Config.direSlimeAttackDamageMedium, Config.direSlimeChanceMedium), LARGE(4, Config.direSlimeHealthLarge,
        Config.direSlimeAttackDamageLarge, Config.direSlimeChanceLarge);

    public final int size;
    public final double health;
    public final double attackDamage;
    public final double chance;

    private SlimeConf(int size, double health, double attackDamage, double chance) {
      this.size = size;
      this.health = health;
      this.attackDamage = attackDamage;
      this.chance = chance;
    }

    static SlimeConf getConfForSize(int size) {
      for (SlimeConf conf : values()) {
        if (conf.size == size) {
          return conf;
        }
      }
      return SMALL;
    }

    SlimeConf bigger() {
      int index = ordinal() + 1;
      if (index >= values().length) {
        return null;
      }
      return values()[index];
    }
  }

  public EntityDireSlime(World world) {
    super(world);
    setSlimeSize(1);
  }

  @Override
  public void setSlimeSize(int size) {
    super.setSlimeSize(size);
    SlimeConf conf = SlimeConf.getConfForSize(size);
    getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage).setBaseValue(conf.attackDamage);
    getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(conf.health);
    setHealth(getMaxHealth());
  }

  @Override
  public void onDeath(DamageSource damageSource) {
    super.onDeath(damageSource);
    if (!worldObj.isRemote && damageSource != null && damageSource.getEntity() instanceof EntityPlayer) {
      SlimeConf nextConf = SlimeConf.getConfForSize(getSlimeSize()).bigger();
      if (nextConf != null && worldObj.rand.nextFloat() <= nextConf.chance) {
        EntityDireSlime spawn = new EntityDireSlime(worldObj);
        spawn.setSlimeSize(nextConf.size);
        spawn.setLocationAndAngles(posX, posY, posZ, rotationYaw, 0);
        spawn.onSpawnWithEgg(null);
        if (SpawnUtil.isSpaceAvailableForSpawn(worldObj, spawn, false)) {
          worldObj.spawnEntityInWorld(spawn);
        }
      }
    }
  }

  @Override
  public void setDead() {
    //Override to prevent smaller slimes spawning
    isDead = true;
  }

  @Override
  protected String getSlimeParticle() {
    return "blockcrack_" + Block.getIdFromBlock(Blocks.dirt) + "_0";
  }

  @Override
  protected EntitySlime createInstance() {
    return new EntityDireSlime(this.worldObj);
  }

  @Override
  protected Item getDropItem() {
    return Items.clay_ball;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getBrightnessForRender(float p_70070_1_) {
    int i = MathHelper.floor_double(this.posX);
    int j = MathHelper.floor_double(this.posZ);

    if (this.worldObj.blockExists(i, 0, j)) {
      double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
      int k = MathHelper.floor_double(this.posY - this.yOffset + d0);
      return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
    } else {
      return 0;
    }
  }

  @Override
  public float getBrightness(float p_70013_1_) {
    int i = MathHelper.floor_double(this.posX);
    int j = MathHelper.floor_double(this.posZ);

    if (this.worldObj.blockExists(i, 0, j)) {
      double d0 = (this.boundingBox.maxY - this.boundingBox.minY) * 0.66D;
      int k = MathHelper.floor_double(this.posY - this.yOffset + d0);
      return this.worldObj.getLightBrightness(i, k, j);
    } else {
      return 0.0F;
    }
  }

  @Override
  protected void applyEntityAttributes() {
    super.applyEntityAttributes();
    getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
  }

  @Override
  protected int getAttackStrength() {
    int res = (int) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
    return res;
  }

  // This is called every tick on onUpdate(), so avoid moving the slime around twice per tick.
  @Override
  protected void setSize(float p_70105_1_, float p_70105_2_) {
    int i = this.getSlimeSize();
    super.setSize(i, i);
  }

  @Override
  public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
    int i = this.getSlimeSize();

    if (this.canEntityBeSeen(p_70100_1_) && this.getDistanceSqToEntity(p_70100_1_) < (double) i * (double) i
        && p_70100_1_.attackEntityFrom(DamageSource.causeMobDamage(this), this.getAttackStrength())) {
      this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
    }
  }

}
