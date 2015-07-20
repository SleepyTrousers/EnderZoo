package crazypants.enderzoo.charge;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.PacketHandler;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityUtil;

public class BlockConfusingCharge extends BlockTNT implements ICharge {

  private static final String NAME = "blockConfusingCharge";

  @SideOnly(Side.CLIENT)
  private IIcon bottomIcon;
  @SideOnly(Side.CLIENT)
  private IIcon topIcon;

  private int chargeId;

  public static BlockConfusingCharge create() {

    PacketHandler.INSTANCE.registerMessage(PacketExplodeEffect.class, PacketExplodeEffect.class, PacketHandler.nextID(), Side.CLIENT);

    int entityID = EntityRegistry.findGlobalUniqueEntityId();
    EntityRegistry.registerGlobalEntityID(EntityPrimedCharge.class, "EntityPrimedCharge", entityID);
    EntityRegistry.registerModEntity(EntityPrimedCharge.class, "EntityPrimedCharge", entityID, EnderZoo.instance, 64, 100, false);

    if (!Config.confusingChargeEnabled) {
      return null;
    }

    BlockConfusingCharge res = new BlockConfusingCharge();
    res.init();
    return res;
  }

  private String name;

  protected BlockConfusingCharge() {
    this(NAME);
  }

  protected BlockConfusingCharge(String name) {
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setBlockName(name);
    this.name = name;
  }

  protected void init() {
    GameRegistry.registerBlock(this, name);
    ChargeRegister.instance.registerCharge(this);
  }

  @Override
  public int getID() {
    return chargeId;
  }

  @Override
  public void setID(int id) {
    chargeId = id;
  }

  @Override
  public Block getBlock() {
    return this;
  }

  @Override
  public void explode(EntityPrimedCharge entity) {
    World world = entity.worldObj;

    world.playSoundEffect(entity.posX, entity.posY, entity.posZ, "random.explode", 3F, 1.4f + ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F));

    PacketHandler.sendToAllAround(new PacketExplodeEffect(entity, this), entity);
  }

  @SuppressWarnings("unchecked")
  @Override
  @SideOnly(Side.CLIENT)
  public void explodeEffect(World world, double x, double y, double z) {

    List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, EntityUtil.getBoundsAround(x, y, z, Config.confusingChargeRange));
    if (players != null) {

      double maxDistanceSq = Config.confusingChargeRange * Config.confusingChargeRange;
      for (EntityPlayer player : players) {
        double playerDistSq = player.getDistanceSq(x, y, z);
        if (playerDistSq < maxDistanceSq) {
          double scale = 1 - playerDistSq / maxDistanceSq;
          scale = Math.exp(scale) / Math.E;
          int duration = (int) Math.ceil(Config.confusingChargeEffectDuration * scale);
          EnderZoo.proxy.setInstantConfusionOnPlayer(player, duration);
        }
      }
    }

    world.spawnParticle("hugeexplosion", x, y, z, 1.0D, 0.0D, 0.0D);

    int col = Items.potionitem.getColorFromDamage(8231);
    float r = (col >> 16 & 255) / 255.0F;
    float g = (col >> 8 & 255) / 255.0F;
    float b = (col >> 0 & 255) / 255.0F;
    Random random = world.rand;
    for (int i = 0; i < 100; ++i) {
      //double seed = random.nextDouble() * 20.0D;

      double d = random.nextDouble() * 2D;
      double mag = 25;
      double motionX = (0.5 - random.nextDouble()) * mag * d;
      double motionY = (0.5 - random.nextDouble()) * mag;
      double motionZ = (0.5 - random.nextDouble()) * mag * d;

      EntitySpellParticleFX entityfx = new EntitySpellParticleFX(world, x + motionX * 0.1, y + motionY * 0.1, z + motionZ * 0.1, motionX, motionY, motionZ);
      float colRan = 0.75F + random.nextFloat() * 0.25F;
      entityfx.setRBGColorF(r * colRan, g * colRan, b * colRan);
      //entityfx.multiplyVelocity((float) (random.nextDouble() * 4.0D));
      //entityfx.multiplyVelocity(0.1f);
      Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);

    }

  }

  @Override
  public IIcon getIcon(int side, int meta) {
    return side == 0 ? bottomIcon : (side == 1 ? topIcon : blockIcon);
  }

  @Override
  public void registerBlockIcons(IIconRegister p_149651_1_) {
    blockIcon = p_149651_1_.registerIcon("enderzoo:" + name + "_side");
    topIcon = p_149651_1_.registerIcon("enderzoo:" + name + "_top");
    bottomIcon = p_149651_1_.registerIcon("enderzoo:" + name + "_bottom");
  }

  @Override
  public void func_150114_a(World world, int x, int y, int z, int meta, EntityLivingBase placedBy) {
    if (!world.isRemote) {
      if ((meta & 1) == 1) {
        EntityPrimedCharge entity = new EntityPrimedCharge(this, world, x + 0.5F, y + 0.5F, z + 0.5F, placedBy);
        world.spawnEntityInWorld(entity);
        world.playSoundAtEntity(entity, "game.tnt.primed", 1.0F, 1.0F);
        world.updateEntity(entity);
      }
    }
  }

  @Override
  public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
    if (!world.isRemote) {
      EntityLivingBase placedBy = explosion.getExplosivePlacedBy();
      onIgnitedByNeighbour(world, x, y, z, placedBy);
    }
  }

  protected void onIgnitedByNeighbour(World world, int x, int y, int z, EntityLivingBase placedBy) {
    EntityPrimedCharge entity = new EntityPrimedCharge(this, world, x + 0.5F, y + 0.5F, z + 0.5F, placedBy);
    entity.setFuse(world.rand.nextInt(entity.getFuse() / 4) + entity.getFuse() / 8);
    world.spawnEntityInWorld(entity);
  }

}
