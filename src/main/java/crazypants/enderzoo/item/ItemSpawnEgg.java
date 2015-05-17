package crazypants.enderzoo.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.entity.MobInfo;

public class ItemSpawnEgg extends Item {

  private static final String NAME = "itemSpawnEggEnderZoo";

  public static ItemSpawnEgg create() {
    ItemSpawnEgg res = new ItemSpawnEgg();
    res.init();
    return res;
  }

  private ItemSpawnEgg() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(true);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public void registerIcons(IIconRegister iconRegister) {
  }

  @Override
  public boolean requiresMultipleRenderPasses() {
    return true;
  }

  @Override
  public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
    return Items.spawn_egg.getIconFromDamageForRenderPass(par1, par2);
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    int damage = MathHelper.clamp_int(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    String s = ("" + StatCollector.translateToLocal(getUnlocalizedName() + ".name")).trim();
    String s1 = MobInfo.values()[damage].getName();
    if (s1 != null) {
      s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
    }
    return s;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void getSubItems(Item item, CreativeTabs tab, List list) {
    for (MobInfo mob : MobInfo.values()) {
      if (mob.isEnabled()) {
        list.add(new ItemStack(item, 1, mob.ordinal()));
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getColorFromItemStack(ItemStack stack, int pass) {
    int damage = MathHelper.clamp_int(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    MobInfo mob = MobInfo.values()[damage];
    return pass == 0 ? mob.getEggBackgroundColor() : mob.getEggForegroundColor();
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int posX, int posY, int posZ, int par7, float par8, float par9, float par10) {
    if (!world.isRemote) {
      activateSpawnEgg(stack, world, posX, posY, posZ, par7);
      if (!player.capabilities.isCreativeMode) {
        --stack.stackSize;
      }
    }
    return true;
  }

  public static EntityLiving activateSpawnEgg(ItemStack stack, World world, double posX, double posY, double posZ, int par7) {
    Block i1 = world.getBlock((int) posX, (int) posY, (int) posZ);
    posX += Facing.offsetsXForSide[par7];
    posY += Facing.offsetsYForSide[par7];
    posZ += Facing.offsetsZForSide[par7];
    if (par7 == 1 && i1 != null && i1.getRenderType() == 11) {
      posY += 0.5;
    }

    int damage = MathHelper.clamp_int(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    EntityLiving entity = (EntityLiving) EntityList.createEntityByName(MobInfo.values()[damage].getName(), world);
    spawnEntity(posX + 0.5, posY, posZ + 0.5, entity, world);
    return entity;
  }

  public static void spawnEntity(double x, double y, double z, EntityLiving entity, World world) {
    if (!world.isRemote) {
      entity.setPosition(x, y, z);
      entity.onSpawnWithEgg(null);
      world.spawnEntityInWorld(entity);
    }
  }
}
