package crazypants.enderzoo.entity.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;

@SideOnly(Side.CLIENT)
public class ModelDireSlime extends ModelBase {
  ModelRenderer[] sliceRenderers = new ModelRenderer[16];
  ModelRenderer coreRenderer;

  public ModelDireSlime() {
    for (int i = 0; i < this.sliceRenderers.length; ++i) {
      this.sliceRenderers[i] = new ModelRenderer(this, 0, i);
      this.sliceRenderers[i].setTextureSize(64, 64);
      this.sliceRenderers[i].addBox(-8.0F, (float) (8 + i), -8.0F, 16, 1, 16);
    }

    this.coreRenderer = new ModelRenderer(this, 0, 32);
    this.coreRenderer.setTextureSize(64, 64);
    this.coreRenderer.addBox(-2.0F, 18.0F, -2.0F, 4, 4, 4);
  }

  public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float p_78086_4_) {
    EntityMagmaCube entitymagmacube = (EntityMagmaCube) p_78086_1_;
    float f3 = entitymagmacube.prevSquishFactor + (entitymagmacube.squishFactor - entitymagmacube.prevSquishFactor) * p_78086_4_;

    if (f3 < 0.0F) {
      f3 = 0.0F;
    }

    for (int i = 0; i < this.sliceRenderers.length; ++i) {
      this.sliceRenderers[i].rotationPointY = (float) (-(8 - i)) * f3 * 1.7F;
    }
  }

  public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_) {
    this.coreRenderer.render(p_78088_7_);

    for (int i = 0; i < this.sliceRenderers.length; ++i) {
      this.sliceRenderers[i].render(p_78088_7_);
    }
  }
}