package crazypants.enderzoo.entity.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityEnderminy;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderEnderminy extends RenderLiving<EntityEnderminy> {


  private static final String PATH = Config.enderminyOldTexture ? "entity/old/" : "entity/";
  private static final ResourceLocation endermanEyesTexture = new ResourceLocation("enderzoo:" + PATH + "enderminy_eyes.png");
  private static final ResourceLocation endermanTextures = new ResourceLocation("enderzoo:" + PATH + "enderminy.png");


  private ModelEnderman endermanModel;
  private Random rnd = new Random();

  public RenderEnderminy(RenderManager rm) {
    super(rm, new ModelEnderman(0), 0.5F);
    endermanModel = (ModelEnderman) super.mainModel;
    addLayer(new EyesLayer());
  }

  public void doRender(EntityEnderminy p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {

    endermanModel.isAttacking = p_76986_1_.isScreaming();
    if (p_76986_1_.isScreaming()) {
      double d3 = 0.02D;
      p_76986_2_ += rnd.nextGaussian() * d3;
      p_76986_6_ += rnd.nextGaussian() * d3;
    }
    super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

  protected ResourceLocation getEntityTexture(EntityEnderminy p_110775_1_) {
    return endermanTextures;
  }

  @Override
  protected void preRenderCallback(EntityEnderminy p_77041_1_, float p_77041_2_) {
    GL11.glScalef(0.5F, 0.25F, 0.5F);
  }

  private class EyesLayer implements LayerRenderer<EntityEnderminy> {

    @Override
    public void doRenderLayer(EntityEnderminy em, float p_177201_2_, float p_177201_3_, float p_177201_4_, float p_177201_5_, float p_177201_6_,
        float p_177201_7_, float p_177201_8_) {

      

      bindTexture(endermanEyesTexture);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.blendFunc(1, 1);
      GlStateManager.disableLighting();

      if(em.isInvisible()) {
        GlStateManager.depthMask(false);
      } else {
        GlStateManager.depthMask(true);
      }

      char c0 = 61680;
      int i = c0 % 65536;
      int j = c0 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) i / 1.0F, (float) j / 1.0F);
      GlStateManager.enableLighting();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      getMainModel().render(em, p_177201_2_, p_177201_3_, p_177201_5_, p_177201_6_, p_177201_7_, p_177201_8_);
      func_177105_a(em, p_177201_4_);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();

    }

    @Override
    public boolean shouldCombineTextures() {
      return false;
    }

  }

}
