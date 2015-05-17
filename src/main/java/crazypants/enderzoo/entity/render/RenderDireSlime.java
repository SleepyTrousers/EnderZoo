package crazypants.enderzoo.entity.render;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.entity.EntityDireSlime;

@SideOnly(Side.CLIENT)
public class RenderDireSlime extends RenderLiving {

  private static final ResourceLocation magmaCubeTextures = new ResourceLocation("enderzoo:entity/direslime.png");

  public RenderDireSlime() {
    super(new ModelDireSlime(), 0.25F);
  }

  @Override
  protected void preRenderCallback(EntityLivingBase p_77041_1_, float partialTick) {
    EntityDireSlime direSlime = (EntityDireSlime) p_77041_1_;
    int i = direSlime.getSlimeSize();
    float f1 = (direSlime.prevSquishFactor + (direSlime.squishFactor - direSlime.prevSquishFactor) * partialTick) / (i * 0.5F + 1.0F);
    float f2 = 1.0F / (f1 + 1.0F);
    float f3 = i;
    GL11.glScalef(f2 * f3, 1.0F / f2 * f3, f2 * f3);
  }

  @Override
  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return magmaCubeTextures;
  }

  @Override
  protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
    if (p_77043_1_.deathTime > 0) {
      float f3 = (p_77043_1_.deathTime + p_77043_4_ - 1.0F) / 20.0F * 1.6F;
      f3 = Math.max(MathHelper.sqrt_float(f3), 1.0F);
      GL11.glRotatef(f3 * this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
    }
  }

}
