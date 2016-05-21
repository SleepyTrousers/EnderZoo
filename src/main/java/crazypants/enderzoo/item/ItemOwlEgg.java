package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityOwlEgg;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemOwlEgg extends Item {

  public static final String NAME = "owlEgg";

  public static ItemOwlEgg create() {
    
    EntityRegistry.registerModEntity(EntityOwlEgg.class, "EntityOwlEgg", Config.entityOwlEggId, EnderZoo.instance, 64, 10, true);
    
    ItemOwlEgg res = new ItemOwlEgg();
    res.init();
    return res;
  }

  private ItemOwlEgg() {
    setUnlocalizedName(NAME);
    setRegistryName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.register(this);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    if (!playerIn.capabilities.isCreativeMode) {
      --itemStackIn.stackSize;
    }
    worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.BLOCKS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
    if (!worldIn.isRemote) {         
      worldIn.spawnEntityInWorld(new EntityOwlEgg(worldIn, playerIn));
    }
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
  }

}