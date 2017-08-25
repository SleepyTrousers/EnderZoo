package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.RegistryHandler;
import crazypants.enderzoo.entity.MobInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.world.World;

public class ItemSpawnEgg extends Item {

  public static final String NAME = "spawnegg";

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
	RegistryHandler.ITEMS.add(this);
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    int damage = MathHelper.clamp(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    String s = ("" + EnderZoo.proxy.translate(getUnlocalizedName() + ".name")).trim();
    String s1 = EnderZoo.MODID + "." + MobInfo.values()[damage].getName();
    if(s1 != null) {
      s = s + " " + EnderZoo.proxy.translate("entity." + s1 + ".name");
    }
    return s;
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
    for (MobInfo mob : MobInfo.values()) {
      if(isInCreativeTab(tab) && mob.isEnabled()) {
        list.add(new ItemStack(this, 1, mob.ordinal()));
      }
    }
  }

  @Override
  public EnumActionResult onItemUse( EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if(!world.isRemote) {
      activateSpawnEgg(stack, world, pos.getX(), pos.getY(), pos.getZ(), facing.ordinal(), false);
      if(!player.capabilities.isCreativeMode) {
        stack.shrink(1);
      }
    }
    return EnumActionResult.SUCCESS;
  }
  @Override
  public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand){
  
    int damage = MathHelper.clamp(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    String name = EnderZoo.MODID + ":" + MobInfo.values()[damage].getName();
    String key = EntityList.getKey(target).toString();
    //if i clicked a spawn egg on an entity of the matching type
    //AND that entity has child types, then spawn a baby // https://github.com/SleepyTrousers/EnderZoo/issues/151
    if(key.equals(name) && target instanceof EntityAgeable){
      BlockPos pos = target.getPosition();
      activateSpawnEgg(stack, playerIn.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ(), target.getHorizontalFacing().ordinal(), true);
      return true;
    }
    
    return false;
  }

  //TODO: Copied from MC1.7. Used to be in Facing class but cant find it now
  public static final int[] offsetsXForSide = new int[] { 0, 0, 0, 0, -1, 1 };
  public static final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0 };
  public static final int[] offsetsZForSide = new int[] { 0, 0, -1, 1, 0, 0 };

  public static EntityLiving activateSpawnEgg(ItemStack stack, World world, double posX, double posY, double posZ, int side, boolean isBaby) {
   
    posX += offsetsXForSide[side];
    posY += offsetsYForSide[side];
    posZ += offsetsZForSide[side];

    int damage = MathHelper.clamp(stack.getItemDamage(), 0, MobInfo.values().length - 1);
    
    EntityLiving entity = (EntityLiving) EntityList.createEntityByIDFromName(new ResourceLocation(EnderZoo.MODID, MobInfo.values()[damage].getName()), world);
    if(isBaby && entity instanceof EntityAgeable){
      ((EntityAgeable)entity).setGrowingAge(-24000);
    }
    
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
