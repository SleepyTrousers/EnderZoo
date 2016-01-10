package crazypants.enderzoo.item;

import crazypants.enderzoo.EnderZoo;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuardiansBowModelLoader {

  public static final ResourceLocation[] NAMES = new ResourceLocation[] { 
      new ResourceLocation(EnderZoo.MODID + ":" + ItemGuardiansBow.NAME),
      new ResourceLocation(EnderZoo.MODID + ":" + ItemGuardiansBow.NAME + "_pulling_0"),
      new ResourceLocation(EnderZoo.MODID + ":" + ItemGuardiansBow.NAME + "_pulling_1"),
      new ResourceLocation(EnderZoo.MODID + ":" + ItemGuardiansBow.NAME + "_pulling_2") };

  public static final ModelResourceLocation[] MODELS;

  static {
    MODELS = new ModelResourceLocation[NAMES.length];
    for (int i = 0; i < NAMES.length; i++) {
      MODELS[i] = new ModelResourceLocation(NAMES[i], "inventory");
    }
  }

  public static ModelResourceLocation getModel(ItemStack stack, EntityPlayer player, int useRemaining) {
    ModelResourceLocation modelresourcelocation = MODELS[0];
    if(player.getItemInUse() != null) {
      int useTime = stack.getMaxItemUseDuration() - useRemaining;
      int drawTime = EnderZoo.itemGuardiansBow.getDrawTime();
      if(useTime >= drawTime - 2) {
        modelresourcelocation = MODELS[3];
      } else if(useTime > drawTime * 2 / 3f) {
        modelresourcelocation = MODELS[2];
      } else if(useTime > 0) {
        modelresourcelocation = MODELS[1];
      }
    }
    return modelresourcelocation;
  }

  public static void registerVariants() {
    ModelBakery.registerItemVariants(EnderZoo.itemGuardiansBow, GuardiansBowModelLoader.NAMES);    
  }

}
