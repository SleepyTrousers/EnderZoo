package crazypants.enderzoo.entity.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderUtil {

  public static void renderEntityBoundingBox(EntityLiving entity, double x, double y, double z) {

    AxisAlignedBB bb = entity.boundingBox;
    if (bb != null) {

      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_CULL_FACE);

      GL11.glPushMatrix();

      GL11.glTranslatef((float) x, (float) y, (float) z);
      GL11.glPushMatrix();
      GL11.glRotatef(-entity.renderYawOffset, 0, 1, 0);

      Tessellator tes = Tessellator.instance;
      tes.startDrawingQuads();
      tes.setColorOpaque_F(1, 1, 1);

      double width = (bb.maxX - bb.minX) / 2;
      double height = bb.maxY - bb.minY;
      double depth = (bb.maxZ - bb.minZ) / 2;

      tes.addVertex(-width, 0, 0);
      tes.addVertex(width, 0, 0);
      tes.addVertex(width, height, 0);
      tes.addVertex(-width, height, 0);

      tes.addVertex(0, 0, -depth);
      tes.addVertex(0, 0, depth);
      tes.addVertex(0, height, depth);
      tes.addVertex(0, height, -depth);
      tes.draw();

      GL11.glPopMatrix();
      GL11.glPopMatrix();

      GL11.glEnable(GL11.GL_TEXTURE_2D);
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_CULL_FACE);
    }
  }

  private RenderUtil() {
  }

}
