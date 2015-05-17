package crazypants.enderzoo.entity.render;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.renderer.entity.RenderHorse;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Maps;

public class RenderFallenMount extends RenderHorse {

  private static final String[] horseArmorTextures = new String[] { null, "textures/entity/horse/armor/horse_armor_iron.png",
      "textures/entity/horse/armor/horse_armor_gold.png", "textures/entity/horse/armor/horse_armor_diamond.png" };
  private static final String textureName = "textures/entity/horse/horse_zombie.png";
  private static final ResourceLocation zombieHorseTexture = new ResourceLocation(textureName);
  private static final Map<String, ResourceLocation> textureCache = Maps.newHashMap();

  public RenderFallenMount() {
    super(new ModelHorse(), 0.75F);
  }

  @Override
  protected ResourceLocation getEntityTexture(EntityHorse horse) {
    if (horse.getTotalArmorValue() == 0) {
      return zombieHorseTexture;
    } else {
      return getArmoredTexture(horse);
    }
  }

  private ResourceLocation getArmoredTexture(EntityHorse horse) {
    String s = horseArmorTextures[horse.func_110241_cb()];
    ResourceLocation resourcelocation = textureCache.get(s);
    if (resourcelocation == null) {
      resourcelocation = new ResourceLocation("Layered:" + s);
      Minecraft.getMinecraft().getTextureManager().loadTexture(resourcelocation, new LayeredTexture(textureName, s));
      textureCache.put(s, resourcelocation);
    }
    return resourcelocation;
  }

}
