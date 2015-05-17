package crazypants.enderzoo.entity.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import crazypants.enderzoo.entity.EntityWitherCat;
import crazypants.enderzoo.entity.EntityWitherCat.GrowthMode;

public class RenderWitherCat extends RenderLiving {

  private ResourceLocation texture = new ResourceLocation("enderzoo:entity/wither_cat.png");
  private ResourceLocation angryTexture = new ResourceLocation("enderzoo:entity/wither_cat_angry.png");

  public RenderWitherCat() {
    super(new ModelWitherCat(), 0.4F);
    setRenderPassModel(mainModel);
  }

  @Override
  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return texture;
  }

  @Override
  public void doRender(EntityLiving entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
    super.doRender(entity, x, y, z, p_76986_8_, p_76986_9_);
    GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
    // Debug to show hit box    
    //    RenderUtil.renderEntityBoundingBox(entity, x, y, z);
  }

  @Override
  protected void preRenderCallback(EntityLivingBase entity, float partialTick) {

    EntityWitherCat cat = (EntityWitherCat) entity;
    float scale = cat.getScale();
    if (scale > 1) {
      if (cat.getGrowthMode() == GrowthMode.SHRINK) {
        partialTick *= -1;
      }
      scale = Math.min(cat.getAngryScale(), scale + cat.getScaleInc() * partialTick);
      float widthFactor = 1 - (cat.getAngryScale() - scale);
      GL11.glScalef(scale + (0.25f * widthFactor), scale, scale - (0.1f * widthFactor));
    }

  }

  @Override
  protected int shouldRenderPass(EntityLivingBase entity, int p_77032_2_, float p_77032_3_) {

    if (p_77032_2_ != 0) {
      return -1;
    } else {
      bindTexture(angryTexture);

      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);

      float blendFactor = 1.0F;
      EntityWitherCat cat = (EntityWitherCat) entity;
      float scale = cat.getScale();
      blendFactor = 1 - (cat.getAngryScale() - scale);
      GL14.glBlendColor(1.0f, 1.0f, 1.0f, blendFactor);

      GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
      GL11.glPolygonOffset(-1, -1);

      char c0 = 61680;
      int j = c0 % 65536;
      int k = c0 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
      GL11.glEnable(GL11.GL_LIGHTING);

      return 1;
    }
  }

}
