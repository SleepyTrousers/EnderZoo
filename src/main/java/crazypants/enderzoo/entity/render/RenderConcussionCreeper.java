package crazypants.enderzoo.entity.render;

import net.minecraft.client.renderer.entity.RenderCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.ResourceLocation;

public class RenderConcussionCreeper extends RenderCreeper {

  private static final ResourceLocation creeperTextures = new ResourceLocation("enderzoo:entity/concussionCreeper.png");
  
  /**
   * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
   */
  @Override
  protected ResourceLocation getEntityTexture(EntityCreeper p_110775_1_) {
      return creeperTextures;
  }
  
}
