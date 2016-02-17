package crazypants.enderzoo.entity.render;

import info.loenwind.owlgen.IGene;
import info.loenwind.owlgen.IGeneticsProvider;
import info.loenwind.owlgen.impl.GeneticsProvider;

import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import crazypants.enderzoo.entity.EntityOwl;
import crazypants.enderzoo.entity.genes.OwlBeakColor;
import crazypants.enderzoo.entity.genes.OwlCoatColor;
import crazypants.enderzoo.entity.genes.OwlLegColor;
import crazypants.enderzoo.entity.genes.OwlShade;
import crazypants.enderzoo.entity.genes.OwlSize;

/**
 * ModelOwl - Yulife Created using Tabula 5.1.0
 */
public class ModelOwl extends ModelBase {

  /**
   * Texture is organized in rows that are TEXTURE_U_MAX wide and V_OFFSET high.
   */
  private static final int V_OFFSET = 13, TEXTURE_U_MAX = 64, TEXTURE_V_MAX = 64;
  private static final int BODY_U = 0, BODY_V = 0, HEAD_U = 38, HEAD_V = 1, WING_U = 23, WING_V = 3;
  private static final int TAILBASE_U = 30, TAILBASE_V = 1, TAIL_U = 38, TAIL_V = 2;
  private static final int BEAK_U = 58, BEAK_V = 1, LEG_U = 0, LEG_V = 1, FOOT_U = 17, FOOT_V = 0;

  private ModelRenderer[] body, head, legL, legR, tailBase, footL, footR, wingR, wingL, beak, tail1, tail2, tail3;

  public ModelOwl() {
    textureWidth = TEXTURE_U_MAX;
    textureHeight = TEXTURE_V_MAX;

    body = mkSimple(OwlCoatColor.count(), BODY_U, BODY_V, 0.0F, 15.0F, 0.0F, -3.0F, 0.0F, -3.0F, 6, 7, 5, 0.0F);

    wingR = mkSimple(OwlCoatColor.count(), WING_U, WING_V, -3.0F, 0.0F, 0.0F, -1.0F, 0.0F, -3.0F, 1, 5, 5, 0.0F);
    wingL = mkSimple(OwlCoatColor.count(), WING_U, WING_V, 3.0F, 0.0F, -0.5F, 0.0F, 0.0F, -2.5F, 1, 5, 5, 0.0F);
    mirror(wingL);

    attach(body, wingR);
    attach(body, wingL);

    legR = mkSimple(OwlLegColor.count(), LEG_U, LEG_V, -1.5F, 7.0F, 0.0F, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);
    legL = mkSimple(OwlLegColor.count(), LEG_U, LEG_V, 1.5F, 7.0F, 0.0F, -0.5F, 0.0F, -0.5F, 1, 1, 1, 0.0F);

    attach(body, legR);
    attach(body, legL);

    footR = mkSimple(OwlLegColor.count(), FOOT_U, FOOT_V, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, -2.0F, 2, 1, 3, 0.0F);
    footL = mkSimple(OwlLegColor.count(), FOOT_U, FOOT_V, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, -2.0F, 2, 1, 3, 0.0F);

    attachMatching(legR, footR);
    attachMatching(legL, footL);

    tailBase = mkSimple(OwlCoatColor.count(), TAILBASE_U, TAILBASE_V, 0.0F, 3.5F, 2.2F, -1.5F, 1.0F, -1.8F, 3, 3, 1, 0.0F, 0.6373942428283291F, 0.0F, 0.0F);

    attachMatching(body, tailBase);

    tail1 = mkSimple(OwlCoatColor.count(), TAIL_U, TAIL_V, 0.0F, 0.0F, 0.0F, 0.0F, 2.0F, -1.7F, 2, 3, 1, 0.0F, 0.0F, 0.0F, 0.9599310885968813F);
    tail2 = mkSimple(OwlCoatColor.count(), TAIL_U, TAIL_V, 0.0F, 0.0F, 0.0F, -2.0F, 2.0F, -1.7F, 2, 3, 1, 0.0F, 0.0F, 0.0F, -0.9599310885968813F);
    mirror(tail2);
    tail3 = mkSimple(OwlCoatColor.count(), TAIL_U, TAIL_V, 0.0F, 0.0F, 0.0F, -1.0F, 2.0F, -1.65F, 2, 3, 1, 0.0F);

    attachMatching(tailBase, tail1);
    attachMatching(tailBase, tail2);
    attachMatching(tailBase, tail3);

    head = mkSimple(OwlCoatColor.count(), HEAD_U, HEAD_V, 0.0F, 15.0F, -0.5F, -3.5F, -6.0F, -3.0F, 7, 6, 6, 0.0F, 0.0F, 0.045553093477052F, 0.0F);

    beak = mkSimple(OwlBeakColor.count(), BEAK_U, BEAK_V, -0.5F, -2.3F, -0.4F, 0.0F, -0.8F, -4.0F, 1, 1, 2, 0.0F, 0.36425021489121656F, 0.0F, 0.0F);

    attach(head, beak);
  }

  /**
   * This is a helper function from Tabula to set the rotation of model parts
   */
  public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
    modelRenderer.rotateAngleX = x;
    modelRenderer.rotateAngleY = y;
    modelRenderer.rotateAngleZ = z;
  }

  @Override
  public void render(Entity entity, float time, float limbSwing, float f2, float headY, float headX, float scale) {

    float height = 25;
    float owlScale = 1f;
    float transFactor = 1 - owlScale;

    List<IGene> genes;
    if (entity instanceof EntityOwl) {
      genes = ((EntityOwl) entity).getGenome().getGenesE();
    } else {
      IGeneticsProvider gp = new GeneticsProvider();
      genes = gp.makeGenes(EntityOwl.genetemplate);
    }

    select(wingL, genes.get(2));
    select(wingR, genes.get(2));
    select(legL, genes.get(3));
    select(legR, genes.get(3));
    select(beak, genes.get(4));

    float size = ((OwlSize) genes.get(5)).getSize(); // TODO: apply to rendering
    float shade = ((OwlShade) genes.get(6)).getShade();

    setRotationAngles(time, limbSwing, f2, headY, headX, scale, entity);

    GlStateManager.pushMatrix();
    GlStateManager.color(shade, shade, shade);
    if (isChild) {
      float headScale = owlScale * 0.6f;
      owlScale *= 0.5f;
      float translateScale = 1 - owlScale;
      GlStateManager.translate(0.0F, translateScale * (height - 2) * scale, 0.0F);
      GlStateManager.scale(headScale, headScale, headScale);
      render(head, genes.get(1), scale);
      GlStateManager.popMatrix();

      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0F, translateScale * height * scale, 0.0F);
      GlStateManager.scale(owlScale, owlScale, owlScale);

      render(body, genes.get(0), scale);
    } else {
      GlStateManager.translate(0.0F, transFactor * height * scale, 0.0F);
      GlStateManager.scale(owlScale, owlScale, owlScale);

      render(head, genes.get(1), scale);
      render(body, genes.get(0), scale);
    }
    GlStateManager.popMatrix();
  }

  @Override
  public void setRotationAngles(float limbSwing1, float limbSwing2, float rotationAngle, float headY, float headX, float yOffset, Entity entity) {

    setRotXY(head, headX / (180F / (float) Math.PI), headY / (180F / (float) Math.PI));

    if (entity instanceof EntityOwl) {
      EntityOwl owl = (EntityOwl) entity;
      setRotX(body, owl.getBodyAngle());
      setRotZ(wingL, -owl.getWingAngle());
      setRotZ(wingR, owl.getWingAngle());
    } else {
      setRotX(body, 0);
      setRotZ(wingL, 0);
      setRotZ(wingR, 0);
    }

    if (!entity.isAirBorne) {
      float limbSpeed = 2.5f;
      setRotX(legR, MathHelper.cos(limbSwing1 * limbSpeed) * 1.4F * limbSwing2);
      setRotX(legL, MathHelper.cos(limbSwing1 * limbSpeed + (float) Math.PI) * 1.4F * limbSwing2);
    } else {
      setRotX(legR, 0);
      setRotX(legL, 0);
    }
  }

  private void setRotXYZ(ModelRenderer[] models, float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
    for (ModelRenderer model : models) {
      model.rotateAngleX = rotateAngleX;
      model.rotateAngleY = rotateAngleY;
      model.rotateAngleZ = rotateAngleZ;
    }
  }

  private void setRotXY(ModelRenderer[] models, float rotateAngleX, float rotateAngleY) {
    for (ModelRenderer model : models) {
      model.rotateAngleX = rotateAngleX;
      model.rotateAngleY = rotateAngleY;
    }
  }

  private void setRotX(ModelRenderer[] models, float rotateAngleX) {
    for (ModelRenderer model : models) {
      model.rotateAngleX = rotateAngleX;
    }
  }

  private void setRotZ(ModelRenderer[] models, float rotateAngleZ) {
    for (ModelRenderer model : models) {
      model.rotateAngleX = rotateAngleZ;
    }
  }

  private void render(ModelRenderer[] models, IGene selector, float scale) {
    models[selector.ordinal()].render(scale);
  }

  private void select(ModelRenderer[] models, IGene selector) {
    for (int i = 0; i < models.length; i++) {
      models[i].isHidden = i != selector.ordinal();
    }
  }

  private void mirror(ModelRenderer[] models) {
    for (int i = 0; i < models.length; i++) {
      models[i].mirror = true;
    }
  }

  private void attach(ModelRenderer parent, ModelRenderer child) {
    parent.addChild(child);
  }

  private void attach(ModelRenderer parent, ModelRenderer[] children) {
    for (int i = 0; i < children.length; i++) {
      parent.addChild(children[i]);
    }
  }

  private void attach(ModelRenderer[] parents, ModelRenderer child) {
    for (ModelRenderer parent : parents) {
      parent.addChild(child);
    }
  }

  private void attach(ModelRenderer[] parents, ModelRenderer[] children) {
    for (ModelRenderer parent : parents) {
      for (ModelRenderer child : children) {
        parent.addChild(child);
      }
    }
  }

  private void attachMatching(ModelRenderer[] parents, ModelRenderer[] children) {
    for (int i = 0; i < parents.length && i < children.length; i++) {
      parents[i].addChild(children[i]);
    }
  }

  private ModelRenderer mkSimple(int texOffX, int texOffY, float rotationPointXIn, float rotationPointYIn, float rotationPointZIn, float offX, float offY,
      float offZ, int width, int height, int depth, float scaleFactor, float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
    ModelRenderer m = new ModelRenderer(this, texOffX, texOffY);
    m.setRotationPoint(rotationPointXIn, rotationPointYIn, rotationPointZIn);
    m.addBox(offX, offY, offZ, width, height, depth, scaleFactor);
    setRotateAngle(m, rotateAngleX, rotateAngleY, rotateAngleZ);
    return m;
  }

  private ModelRenderer mkSimple(int texOffX, int texOffY, float rotationPointXIn, float rotationPointYIn, float rotationPointZIn, float offX, float offY,
      float offZ, int width, int height, int depth, float scaleFactor) {
    ModelRenderer m = new ModelRenderer(this, texOffX, texOffY);
    m.setRotationPoint(rotationPointXIn, rotationPointYIn, rotationPointZIn);
    m.addBox(offX, offY, offZ, width, height, depth, scaleFactor);
    return m;
  }

  private ModelRenderer[] mkSimple(int count, int texOffX, int texOffY, float rotationPointXIn, float rotationPointYIn, float rotationPointZIn, float offX,
      float offY, float offZ, int width, int height, int depth, float scaleFactor, float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
    ModelRenderer[] m = new ModelRenderer[count];
    for (int i = 0; i < m.length; i++) {
      m[i] = mkSimple(texOffX, texOffY + i * V_OFFSET, rotationPointXIn, rotationPointYIn, rotationPointZIn, offX, offY, offZ, width, height, depth,
          scaleFactor, rotateAngleX, rotateAngleY, rotateAngleZ);
    }
    return m;
  }

  private ModelRenderer[] mkSimple(int count, int texOffX, int texOffY, float rotationPointXIn, float rotationPointYIn, float rotationPointZIn, float offX,
      float offY, float offZ, int width, int height, int depth, float scaleFactor) {
    ModelRenderer[] m = new ModelRenderer[count];
    for (int i = 0; i < m.length; i++) {
      m[i] = mkSimple(texOffX, texOffY + i * V_OFFSET, rotationPointXIn, rotationPointYIn, rotationPointZIn, offX, offY, offZ, width, height, depth,
          scaleFactor);
    }
    return m;
  }

}
