package crazypants.enderzoo.charge;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderPrimedCharge extends Render {

  private RenderBlocks blockRenderer = new RenderBlocks();

  public RenderPrimedCharge() {
    shadowSize = 0.5F;
  }

  public void doRender(EntityPrimedCharge entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    float f2;

    if (entity.getFuse() - p_76986_9_ + 1.0F < 10.0F) {
      f2 = 1.0F - (entity.getFuse() - p_76986_9_ + 1.0F) / 10.0F;
      if (f2 < 0.0F) {
        f2 = 0.0F;
      }
      if (f2 > 1.0F) {
        f2 = 1.0F;
      }
      f2 *= f2;
      f2 *= f2;
      float f3 = 1.0F + f2 * 0.3F;
      GL11.glScalef(f3, f3, f3);
    }

    f2 = (1.0F - (entity.getFuse() - p_76986_9_ + 1.0F) / 100.0F) * 0.8F;
    bindEntityTexture(entity);
    Block blk = entity.getBlock();
    if (blk == null) {
      blk = Blocks.tnt;
    }
    blockRenderer.renderBlockAsItem(blk, 0, entity.getBrightness(p_76986_9_));

    if (entity.getFuse() / 5 % 2 == 0) {
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, f2);
      blockRenderer.renderBlockAsItem(Blocks.tnt, 0, 1.0F);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    GL11.glPopMatrix();
  }

  @Override
  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return TextureMap.locationBlocksTexture;
  }

  @Override
  public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    this.doRender((EntityPrimedCharge) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
}