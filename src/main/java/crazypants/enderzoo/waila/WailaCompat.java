package crazypants.enderzoo.waila;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.entity.IEnderZooMob;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class WailaCompat implements IWailaEntityProvider
{

  private static final WailaCompat INSTANCE = new WailaCompat();

  public static void load(IWailaRegistrar registrar) {
    registrar.registerBodyProvider(INSTANCE, IEnderZooMob.class);
  }

  @Override
  public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
    return null;
  }

  @Override
  public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
    return null;
  }
  
  public static final Map<Class<? extends Entity>, String> ENTRY_MAP = new HashMap<>();

  @Override
  public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
	String name = ENTRY_MAP.get(entity.getClass());
	
	if(name == null) {
		name = EntityRegistry.getEntry(entity.getClass()).getName();
		ENTRY_MAP.put(entity.getClass(), name);
	}
	
    String locKey = "entity." + EnderZoo.MODID + "." + name + ".desc.";
    String loc = null;
    for (int line = 1; !(loc = EnderZoo.proxy.translate(locKey + line)).equals(locKey + line); line++) {
      currenttip.add(loc);
    }
    return currenttip;
  }

  @Override
  public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
    return null;
  }

  @Override
  public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
    return tag;
  }
}
