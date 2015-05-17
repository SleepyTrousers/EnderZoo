package crazypants.enderzoo.entity.render;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelWitch;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.enderzoo.entity.EntityWitherWitch;

//Pretty much a copy / paste from RenderWitch
public class RenderWitherWitch extends RenderLiving {

  private static final ResourceLocation witchTextures = new ResourceLocation("enderzoo:entity/wither_witch.png");
  private final ModelWitch witchModel;

  public RenderWitherWitch() {
    super(new ModelWitch(0.0F), 0.5F);
    this.witchModel = (ModelWitch) this.mainModel;
  }

  public void doRender(EntityWitherWitch p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    ItemStack itemstack = p_76986_1_.getHeldItem();
    this.witchModel.field_82900_g = itemstack != null;
    super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

  @Override
  protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_) {
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    super.renderEquippedItems(p_77029_1_, p_77029_2_);
    ItemStack itemstack = p_77029_1_.getHeldItem();

    if (itemstack != null) {
      GL11.glPushMatrix();
      float f1;
      if (mainModel.isChild) {
        f1 = 0.5F;
        GL11.glTranslatef(0.0F, 0.625F, 0.0F);
        GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
        GL11.glScalef(f1, f1, f1);
      }
      witchModel.villagerNose.postRender(0.0625F);
      GL11.glTranslatef(-0.0625F, 0.53125F, 0.21875F);

      if (itemstack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType())) {
        f1 = 0.5F;
        GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
        f1 *= 0.75F;
        GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(f1, -f1, f1);
      } else if (itemstack.getItem() == Items.bow) {
        f1 = 0.625F;
        GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
        GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(f1, -f1, f1);
        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
      } else if (itemstack.getItem().isFull3D()) {
        f1 = 0.625F;
        if (itemstack.getItem().shouldRotateAroundWhenRendering()) {
          GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
          GL11.glTranslatef(0.0F, -0.125F, 0.0F);
        }
        this.func_82410_b();
        GL11.glScalef(f1, -f1, f1);
        GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
      } else {
        f1 = 0.375F;
        GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
        GL11.glScalef(f1, f1, f1);
        GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
      }
      GL11.glRotatef(-15.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(40.0F, 0.0F, 0.0F, 1.0F);
      renderManager.itemRenderer.renderItem(p_77029_1_, itemstack, 0);
      if (itemstack.getItem().requiresMultipleRenderPasses()) {
        renderManager.itemRenderer.renderItem(p_77029_1_, itemstack, 1);
      }
      GL11.glPopMatrix();
    }
  }

  protected void func_82410_b() {
    GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
  }

  @Override
  protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_) {
    float f1 = 0.9375F;
    GL11.glScalef(f1, f1, f1);
  }

  @Override
  protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
    return witchTextures;
  }

  @Override
  public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    doRender((EntityWitherWitch) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

  @Override
  public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    doRender((EntityWitherWitch) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }

  @Override
  public void doRender(EntityLiving p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
    doRender((EntityWitherWitch) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
  }
}
