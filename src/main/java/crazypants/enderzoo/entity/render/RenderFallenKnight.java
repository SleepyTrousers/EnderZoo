package crazypants.enderzoo.entity.render;

import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.util.ResourceLocation;

public class RenderFallenKnight extends RenderSkeleton {

  private static final ResourceLocation texture = new ResourceLocation("enderzoo:entity/fallen_knight.png");

  @Override
  protected ResourceLocation getEntityTexture(EntitySkeleton p_110775_1_) {
    return texture;
  }

}
