package crazypants.enderzoo.entity.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.entity.EntityDireSlime;
import net.minecraft.client.model.ModelMagmaCube;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderMagmaCube;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderDireSlime extends RenderLiving {

  private static final ResourceLocation magmaCubeTextures = new ResourceLocation("enderzoo:entity/direslime.png");

  public RenderDireSlime() {
    super(new ModelDireSlime(), 0.25F);
  }

  protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_){
    EntityDireSlime direSlime = (EntityDireSlime)p_77041_1_;
    float f1 = (direSlime.prevSquishFactor + (direSlime.squishFactor - direSlime.prevSquishFactor) * p_77041_2_) / 1.5F + 1.0F;
    float f2 = 1.0F / f1;
    GL11.glScalef(f2, f1, f2);
  }

  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return magmaCubeTextures;
  }

  protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
    if (p_77043_1_.deathTime > 0) {
      float f3 = ((float)p_77043_1_.deathTime + p_77043_4_ - 1.0F) / 20.0F * 1.6F;
      f3 = Math.max(MathHelper.sqrt_float(f3), 1.0F);
      GL11.glRotatef(f3 * this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
    }
  }

}
