package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZooTab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemOwlEgg extends Item {

  public static final String NAME = "owlEgg";

  public static ItemOwlEgg create() {
    ItemOwlEgg res = new ItemOwlEgg();
    res.init();
    return res;
  }

  private ItemOwlEgg() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
    // BrewingRecipeRegistry.addRecipe(BrewingUtil.createAwkwardPotion(), new
    // ItemStack(this), new ItemStack(Items.apple));
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
//    if (!playerIn.capabilities.isCreativeMode) {
//      --itemStackIn.stackSize;
//    }
//    worldIn.playSoundAtEntity(playerIn, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
//    worldIn.spawnEntityInWorld(new EntityPotionEZ(worldIn, playerIn, new PotionEffect(EnderZoo.floatingPotion.id, 50)));
        
//    ItemStack stack = new ItemStack(EnderZoo.itemPotionEZ, 1, 0);
//    ItemStack stack = new ItemStack(Items.potionitem, 1, 0);
//    PotionEffect effect = new PotionEffect(EnderZoo.floatingPotion.id, 50);
//    BrewingUtil.writeCustomEffectToNBT(effect, stack);
//        
//    playerIn.inventory.setInventorySlotContents(0, stack);
    
    
    
//    if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9))
//    {
//        List<PotionEffect> list1 = Lists.<PotionEffect>newArrayList();
//        NBTTagList nbttaglist = stack.getTagCompound().getTagList("CustomPotionEffects", 10);
//
//        for (int i = 0; i < nbttaglist.tagCount(); ++i)
//        {
//            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
//            PotionEffect potioneffect = PotionEffect.readCustomPotionEffectFromNBT(nbttagcompound);
//
//            if (potioneffect != null)
//            {
//                list1.add(potioneffect);
//            }
//        }
//
////        return list1;
//    }
    
    
    return itemStackIn;
  }

  

}