package crazypants.enderzoo.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.config.Config;

public class ItemGuardiansBow extends ItemBow {

  private static final String NAME = "guardiansBow";

  public static final String[] bowPullIconNameArray = new String[] { "pulling_0", "pulling_1", "pulling_2" };
  @SideOnly(Side.CLIENT)
  private IIcon[] iconArray;

  private int drawTime = Config.guardiansBowDrawTime;
  private float damageBonus = Config.guardiansBowDamageBonus;
  private float forceMultiplier = Config.guardiansBowForceMultiplier;
  private float fovMultiplier = Config.guardiansBowFovMultiplier;

  public static ItemGuardiansBow create() {
    ItemGuardiansBow res = new ItemGuardiansBow();
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return res;
  }

  protected ItemGuardiansBow() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setTextureName("enderzoo:guardiansBow");
    setMaxDamage(800);
    setHasSubtypes(false);
  }

  protected void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int itemInUseCount) {

    int drawDuration = getMaxItemUseDuration(stack) - itemInUseCount;
    boolean infiniteArrows = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
    if (infiniteArrows || player.inventory.hasItem(Items.arrow)) {

      float force = drawDuration / (float) drawTime;
      force = (force * force + force * 2.0F) / 3.0F;

      if (force < 0.2D) {
        return;
      }
      if (force > 1.0F) {
        force = 1.0F;
      }

      EntityArrow entityarrow = new EntityArrow(world, player, force * forceMultiplier);
      if (force == 1.0F) {
        entityarrow.setIsCritical(true);
      }

      entityarrow.setDamage(entityarrow.getDamage() + damageBonus);

      int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
      if (powerLevel > 0) {
        entityarrow.setDamage(entityarrow.getDamage() + powerLevel * 0.5D + 0.5D);
      }

      int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
      if (punchLevel > 0) {
        entityarrow.setKnockbackStrength(punchLevel);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0) {
        entityarrow.setFire(100);
      }
      stack.damageItem(1, player);
      world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + force * 0.5F);

      if (infiniteArrows) {
        entityarrow.canBePickedUp = 2;
      } else {
        player.inventory.consumeInventoryItem(Items.arrow);
      }
      if (!world.isRemote) {
        world.spawnEntityInWorld(entityarrow);
      }
    }
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onFovUpdateEvent(FOVUpdateEvent fovEvt) {
    ItemStack currentItem = fovEvt.entity.getCurrentEquippedItem();
    if (currentItem == null || currentItem.getItem() != this || fovEvt.entity.getItemInUseCount() <= 0) {
      return;
    }

    int drawDuration = getMaxItemUseDuration(currentItem) - fovEvt.entity.getItemInUseCount();
    float ratio = drawDuration / (float) drawTime;

    if (ratio > 1.0F) {
      ratio = 1.0F;
    } else {
      ratio *= ratio;
    }
    fovEvt.newfov = (1.0F - ratio * fovMultiplier);

  }

  @Override
  public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
    return stack;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack p_77626_1_) {
    return 72000;
  }

  /**
   * returns the action that specifies what animation to play when the items is
   * being used
   */
  @Override
  public EnumAction getItemUseAction(ItemStack p_77661_1_) {
    return EnumAction.bow;
  }

  @Override
  public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
    int useTime = stack.getMaxItemUseDuration() - useRemaining;
    IIcon iicon = itemIcon;
    if (usingItem == null) {
      return iicon;
    }
    if (useTime >= drawTime - 2) {
      iicon = EnderZoo.itemGuardiansBow.getItemIconForUseDuration(2);
    } else if (useTime > drawTime * 2 / 3f) {
      iicon = EnderZoo.itemGuardiansBow.getItemIconForUseDuration(1);
    } else if (useTime > 0) {
      iicon = EnderZoo.itemGuardiansBow.getItemIconForUseDuration(0);
    }
    return iicon;
  }

  @Override
  public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
    ArrowNockEvent event = new ArrowNockEvent(player, stack);
    MinecraftForge.EVENT_BUS.post(event);
    if (event.isCanceled()) {
      return event.result;
    }
    if (player.capabilities.isCreativeMode || player.inventory.hasItem(Items.arrow)) {
      player.setItemInUse(stack, getMaxItemUseDuration(stack));
    }
    return stack;
  }

  @Override
  public int getItemEnchantability() {
    return 1;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerIcons(IIconRegister p_94581_1_) {
    itemIcon = p_94581_1_.registerIcon(getIconString() + "_standby");
    iconArray = new IIcon[bowPullIconNameArray.length];

    for (int i = 0; i < iconArray.length; ++i) {
      iconArray[i] = p_94581_1_.registerIcon(getIconString() + "_" + bowPullIconNameArray[i]);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getItemIconForUseDuration(int useDuration) {
    return iconArray[useDuration];
  }

}
