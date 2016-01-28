package crazypants.enderzoo.entity.render;

import crazypants.enderzoo.entity.EntityOwl;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

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
    head.addBox(1F, -6F, -4F, 2, 2, 1);
    // eye1
    head.setTextureOffset(7, 23);
    head.addBox(-3F, -6F, -4F, 2, 2, 1);
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

    float height = 25;
    float owlScale = 0.7f;
    float transFactor = 1 - owlScale;
    
    if (isChild) {
      
      
      float headScale = owlScale * 0.7f;
      owlScale *= 0.5f;
      float translateScale = 1 - owlScale;
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, translateScale * (height - 2) * scale , 0.0F);
      GlStateManager.scale(headScale, headScale, headScale);
      head.render(scale);
      GlStateManager.popMatrix();

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, translateScale * height * scale , 0.0F);
      GlStateManager.scale(owlScale, owlScale, owlScale);
      
      body.render(scale);
      rightwing.render(scale);
      leftwing.render(scale);
      
      Vec3 offset = getLegOffset(entity);
      GlStateManager.pushMatrix();
      GlStateManager.translate(offset.xCoord, offset.yCoord, offset.zCoord);
      rightleg.render(scale);
      leftleg.render(scale);      
      GlStateManager.popMatrix();
      
      GlStateManager.popMatrix();
    } else {
            
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, transFactor * height * scale , 0.0F);
      GlStateManager.scale(owlScale, owlScale, owlScale);      
      
      head.render(scale);
      body.render(scale);
      rightwing.render(scale);
      leftwing.render(scale);
      
      GlStateManager.pushMatrix();
      Vec3 offset = getLegOffset(entity);
      GlStateManager.translate(offset.xCoord, offset.yCoord, offset.zCoord);
      rightleg.render(scale);
      leftleg.render(scale);
      GlStateManager.popMatrix();
      
      GlStateManager.popMatrix();
    }
  }

  private Vec3 getLegOffset(Entity entity) {
    if (! (entity instanceof EntityOwl)) { 
      return new Vec3(0,0,0);
    }
        
    EntityOwl owl = (EntityOwl) entity;
    float angle = owl.getBodyAngle();
    double bodyHeight = 16;
    
    double val = Math.cos(angle) * bodyHeight;
    double yDelta = (val - bodyHeight) * 0.0625 * 0.5;    
    val = Math.sin(angle) * bodyHeight * 0.0625 * 0.5;         
    return new Vec3(0,yDelta ,val);
    
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
      float limbSpeed = 2.5f; 
      rightleg.rotateAngleX = MathHelper.cos(limbSwing1 * limbSpeed) * 1.4F * limbSwing2;
      leftleg.rotateAngleX = MathHelper.cos(limbSwing1 * limbSpeed + (float) Math.PI) * 1.4F * limbSwing2;
    } else {
      rightleg.rotateAngleX = 0;
      leftleg.rotateAngleX = 0;
    }

  }

}
