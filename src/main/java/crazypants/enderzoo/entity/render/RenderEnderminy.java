package crazypants.enderzoo.entity.render;

import java.util.Random;

import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityEnderminy;

public class RenderEnderminy extends RenderLiving {

  private static final String PATH = Config.enderminyOldTexture ? "entity/old/" : "entity/";
  private static final ResourceLocation endermanEyesTexture = new ResourceLocation("enderzoo:" + PATH + "enderminy_eyes.png");
  private static final ResourceLocation endermanTextures = new ResourceLocation("enderzoo:" + PATH + "enderminy.png");

  private ModelEnderman endermanModel;
  private Random rnd = new Random();

  public RenderEnderminy() {
    super(new ModelEnderman(), 0.5F);
    endermanModel = (ModelEnderman) super.mainModel;
    setRenderPassModel(endermanModel);
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

  protected int shouldRenderPass(EntityEnderminy p_77032_1_, int p_77032_2_, float p_77032_3_) {
    if (p_77032_2_ != 0) {
      return -1;
    } else {
      bindTexture(endermanEyesTexture);
      float f1 = 1.0F;
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
      GL11.glDisable(GL11.GL_LIGHTING);

      if (p_77032_1_.isInvisible()) {
        GL11.glDepthMask(false);
      } else {
        GL11.glDepthMask(true);
      }

      char c0 = 61680;
      int j = c0 % 65536;
      int k = c0 / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0F, k / 1.0F);
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, f1);
      return 1;
    }
  }

  @Override
  protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_) {
    GL11.glScalef(0.5F, 0.25F, 0.5F);
  }

  @Override
  public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    this.doRender((EntityEnderminy) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

  @Override
  protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_) {
    return this.shouldRenderPass((EntityEnderminy) p_77032_1_, p_77032_2_, p_77032_3_);
  }

  @Override
  public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    this.doRender((EntityEnderminy) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

  @Override
  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return this.getEntityTexture((EntityEnderminy) p_110775_1_);
  }

  @Override
  public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    this.doRender((EntityEnderminy) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

}
