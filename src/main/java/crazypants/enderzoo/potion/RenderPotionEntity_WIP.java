package crazypants.enderzoo.potion;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderPotionEntity_WIP extends Render {

  public RenderPotionEntity_WIP() {
  }

  @Override
  public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
    GL11.glScalef(0.5F, 0.5F, 0.5F);
    bindEntityTexture(entity);
    Tessellator tessellator = Tessellator.instance;
    GL11.glColor3f(0, 0, 0);
    GL11.glPushMatrix();
    this.renderIcon(tessellator, ItemPotion.func_94589_d("overlay"));
    GL11.glPopMatrix();
    GL11.glColor3f(1.0F, 1.0F, 1.0F);

    IIcon iicon = ItemPotion.func_94589_d("bottle_splash");
    renderIcon(tessellator, iicon);
    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    GL11.glPopMatrix();

  }

  /**
   * Returns the location of an entity's texture. Doesn't seem to be called
   * unless you call Render.bindEntityTexture.
   */
  @Override
  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return TextureMap.locationItemsTexture;
  }

  private void renderIcon(Tessellator tesselator, IIcon icon) {
    float f = icon.getMinU();
    float f1 = icon.getMaxU();
    float f2 = icon.getMinV();
    float f3 = icon.getMaxV();
    float f4 = 1.0F;
    float f5 = 0.5F;
    float f6 = 0.25F;
    GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    tesselator.startDrawingQuads();
    tesselator.setNormal(0.0F, 1.0F, 0.0F);
    tesselator.addVertexWithUV(0.0F - f5, 0.0F - f6, 0.0D, f, f3);
    tesselator.addVertexWithUV(f4 - f5, 0.0F - f6, 0.0D, f1, f3);
    tesselator.addVertexWithUV(f4 - f5, f4 - f6, 0.0D, f1, f2);
    tesselator.addVertexWithUV(0.0F - f5, f4 - f6, 0.0D, f, f2);
    tesselator.draw();
  }
}