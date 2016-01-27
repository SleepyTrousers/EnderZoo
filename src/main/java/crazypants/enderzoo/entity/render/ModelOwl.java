package crazypants.enderzoo.entity.render;

import crazypants.enderzoo.entity.EntityOwl;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelOwl extends ModelBase {

  ModelRenderer head;
  ModelRenderer body;
  ModelRenderer rightwing;
  ModelRenderer leftwing;
  ModelRenderer rightleg;
  ModelRenderer leftleg;

  public ModelOwl() {
    textureWidth = 64;
    textureHeight = 64;

    head = new ModelRenderer(this, 0, 0);
    head.addBox(-4F, -8F, -3F, 8, 8, 7);
    head.setRotationPoint(0F, 11F, 0F);
    // head.mirror = true;
    setRotation(head, 0F, 0F, 0F);
    // eye1
    head.setTextureOffset(0, 23);
    head.addBox(1F, -7F, -4F, 2, 2, 1);
    // eye1
    head.setTextureOffset(7, 23);
    head.addBox(-3F, -7F, -4F, 2, 2, 1);
    // beak
    head.setTextureOffset(3, 27);
    head.addBox(-1F, -3F, -4F, 2, 3, 1);

    body = new ModelRenderer(this, 16, 16);
    body.addBox(-4F, 0F, -2F, 8, 12, 4);
    body.setRotationPoint(0F, 11F, 0F);

    rightwing = new ModelRenderer(this, 40, 16);
    rightwing.addBox(-1F, -2F, -2F, 2, 11, 5);
    rightwing.setRotationPoint(-5F, 12F, 0F);
    rightwing.setTextureOffset(40, 33);
    rightwing.addBox(-1F, 0F, 3F, 2, 9, 1);
    rightwing.setTextureOffset(40, 44);
    rightwing.addBox(-1F, 4F, 4F, 2, 5, 1);

    leftwing = new ModelRenderer(this, 40, 16);
    leftwing.addBox(-1F, -2F, -2F, 2, 11, 5);
    leftwing.setRotationPoint(5F, 12F, 0F);
    leftwing.setTextureOffset(47, 33);
    leftwing.addBox(-1F, 0F, 3F, 2, 9, 1);
    leftwing.setTextureOffset(47, 44);
    leftwing.addBox(-1F, 4F, 4F, 2, 5, 1);

    rightleg = new ModelRenderer(this, 0, 16);
    rightleg.addBox(-2F, 0F, -4F, 3, 1, 5);
    rightleg.setRotationPoint(-2F, 23F, 0F);

    leftleg = new ModelRenderer(this, 0, 16);
    leftleg.addBox(-1F, 0F, -4F, 3, 1, 5);
    leftleg.setRotationPoint(2F, 23F, 0F);

  }

  @Override
  public void render(Entity entity, float time, float limbSwing, float f2, float headY, float headX, float scale) {
    super.render(entity, time, limbSwing, f2, headY, headX, scale);

    setRotationAngles(time, limbSwing, f2, headY, headX, scale, entity);

    if (isChild) {
      float f = 2.0F;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, 5.0F * scale, 2.0F * scale);
      head.render(scale);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(1.0F / f, 1.0F / f, 1.0F / f);
      GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
      body.render(scale);
      rightwing.render(scale);
      leftwing.render(scale);
      rightleg.render(scale);
      leftleg.render(scale);
      GlStateManager.popMatrix();
    } else {
      head.render(scale);
      body.render(scale);
      rightwing.render(scale);
      leftwing.render(scale);
      rightleg.render(scale);
      leftleg.render(scale);
    }
  }

  private void setRotation(ModelRenderer model, float x, float y, float z) {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }

  @Override
  public void setRotationAngles(float limbSwing1, float limbSwing2, float rotationAngle, float headY, float headX, float yOffset, Entity entity) {

    head.rotateAngleX = headX / (180F / (float) Math.PI);
    head.rotateAngleY = headY / (180F / (float) Math.PI);

    if (entity instanceof EntityOwl) {
      EntityOwl owl = (EntityOwl) entity;
      body.rotateAngleX = owl.getBodyAngle();
      rightwing.rotateAngleZ = owl.getWingAngle();
      leftwing.rotateAngleZ = -owl.getWingAngle();
    } else {
      body.rotateAngleX = 0;
      rightwing.rotateAngleZ = 0;
      leftwing.rotateAngleZ = 0;
    }

    if (!entity.isAirBorne) {
//      rightleg.rotateAngleX = MathHelper.cos(limbSwing1 * 0.6662F) * 1.4F * limbSwing2;
//      leftleg.rotateAngleX = MathHelper.cos(limbSwing1 * 0.6662F + (float) Math.PI) * 1.4F * limbSwing2;
    } else {
      rightleg.rotateAngleX = 0;
      leftleg.rotateAngleX = 0;
    }

  }

}
