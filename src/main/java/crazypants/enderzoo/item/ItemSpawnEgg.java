package crazypants.enderzoo.item;

import java.util.List;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.entity.MobInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemSpawnEgg extends Item {

  public static final String NAME = "spawnEgg";

  public static ItemSpawnEgg create() {
    ItemSpawnEgg res = new ItemSpawnEgg();
    res.init();
    return res;
  }

  private ItemSpawnEgg() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(true);
    setRegistryName(NAME);
  }

  private void init() {
    GameRegistry.register(this);
    //ItemColors.
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    int damage = MathHelper.clamp(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    String s = ("" + I18n.translateToLocal(getUnlocalizedName() + ".name")).trim();
    String s1 = EnderZoo.MODID + "." + MobInfo.values()[damage].getName();
    if(s1 != null) {
      s = s + " " + I18n.translateToLocal("entity." + s1 + ".name");
    }
    return s;
  }

  @Override
  public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
    for (MobInfo mob : MobInfo.values()) {
      if(mob.isEnabled()) {
        list.add(new ItemStack(item, 1, mob.ordinal()));
      }
    }
  }

  @Override
  public EnumActionResult onItemUse( EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if(!world.isRemote) {
      activateSpawnEgg(stack, world, pos.getX(), pos.getY(), pos.getZ(), facing.ordinal());
      if(!player.capabilities.isCreativeMode) {
        stack.shrink(1);
      }
    }
    return EnumActionResult.SUCCESS;
  }

  //TODO: Copied from MC1.7. Used to be in Facing class but cant find it now
  public static final int[] offsetsXForSide = new int[] { 0, 0, 0, 0, -1, 1 };
  public static final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0 };
  public static final int[] offsetsZForSide = new int[] { 0, 0, -1, 1, 0, 0 };

  public static EntityLiving activateSpawnEgg(ItemStack stack, World world, double posX, double posY, double posZ, int side) {
   
    posX += offsetsXForSide[side];
    posY += offsetsYForSide[side];
    posZ += offsetsZForSide[side];

    int damage = MathHelper.clamp(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    
    EntityLiving entity = (EntityLiving) EntityList.createEntityByIDFromName(new ResourceLocation(EnderZoo.MODID, MobInfo.values()[damage].getName()), world);
    spawnEntity(posX + 0.5, posY, posZ + 0.5, entity, world);
    return entity;
  }

  public static void spawnEntity(double x, double y, double z, EntityLiving entity, World world) {
    if(!world.isRemote && entity != null) {
      entity.setPosition(x, y, z);
      entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(x, y, z)), null);
      world.spawnEntity(entity);
    }
  }
}
