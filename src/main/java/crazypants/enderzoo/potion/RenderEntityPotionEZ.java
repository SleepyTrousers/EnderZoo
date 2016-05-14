package crazypants.enderzoo.potion;

import crazypants.enderzoo.EnderZoo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEntityPotionEZ extends RenderSnowball<EntityPotionEZ> {

  public static final Factory FACTORY = new Factory();
  
  public RenderEntityPotionEZ(RenderManager renderManagerIn, RenderItem itemRendererIn) {
    super(renderManagerIn, EnderZoo.itemPotionEZ, itemRendererIn);
  }
  
  @Override
  public void doRender(EntityPotionEZ entity, double x, double y, double z, float entityYaw, float partialTicks) {
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
  }

  @Override
  public ItemStack getPotion(EntityPotionEZ entityIn) {    
    return entityIn.getPotion();
  }
  
  public static class Factory implements IRenderFactory<EntityPotionEZ> {

    @Override
    public Render<? super EntityPotionEZ> createRenderFor(RenderManager manager) {
      return new RenderEntityPotionEZ(manager, Minecraft.getMinecraft().getRenderItem());
    }
  }
}
