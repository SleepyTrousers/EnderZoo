package crazypants.enderzoo.charge;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.enderzoo.EnderZoo;

public class RenderPrimedCharge extends Render {

  public RenderPrimedCharge(RenderManager renderManager) {
    super(renderManager);
    shadowSize = 0.5F;
  }
  
  public void doRender(EntityPrimedCharge entity, double x, double y, double z, float p_76986_8_, float partialTicks) {
    
    
      BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y + 0.5F, (float)z);
      float f2;

      if ((float)entity.fuse - partialTicks + 1.0F < 10.0F)
      {
          f2 = 1.0F - ((float)entity.fuse - partialTicks + 1.0F) / 10.0F;
          f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
          f2 *= f2;
          f2 *= f2;
          float f3 = 1.0F + f2 * 0.3F;
          GlStateManager.scale(f3, f3, f3);
      }

      f2 = (1.0F - ((float)entity.fuse - partialTicks + 1.0F) / 100.0F) * 0.8F;
      this.bindEntityTexture(entity);
      GlStateManager.translate(-0.5F, -0.5F, 0.5F);
      blockrendererdispatcher.renderBlockBrightness(entity.getBlock().getDefaultState(), entity.getBrightness(partialTicks));
      GlStateManager.translate(0.0F, 0.0F, 1.0F);

      if (entity.fuse / 5 % 2 == 0)
      {
          GlStateManager.disableTexture2D();
          GlStateManager.disableLighting();
          GlStateManager.enableBlend();
          GlStateManager.blendFunc(770, 772);
          GlStateManager.color(1.0F, 1.0F, 1.0F, f2);
          GlStateManager.doPolygonOffset(-3.0F, -3.0F);
          GlStateManager.enablePolygonOffset();
          blockrendererdispatcher.renderBlockBrightness(entity.getBlock().getDefaultState(), 1.0F);
          GlStateManager.doPolygonOffset(0.0F, 0.0F);
          GlStateManager.disablePolygonOffset();
          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          GlStateManager.disableBlend();
          GlStateManager.enableLighting();
          GlStateManager.enableTexture2D();
      }

      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, p_76986_8_, partialTicks);
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