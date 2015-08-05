package crazypants.enderzoo.waila;

import java.util.List;

import crazypants.enderzoo.entity.IEnderZooMob;
import mcp.mobius.waila.api.ITaggedList.ITipList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityAccessorServer;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class WailaCompat implements IWailaEntityProvider {

  private static final WailaCompat INSTANCE = new WailaCompat();

  public static void load(IWailaRegistrar registrar) {
    registrar.registerBodyProvider(INSTANCE, IEnderZooMob.class);
  }

  @Override
  public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
    return null; // not used
  }

  @Override
  public ITipList getWailaHead(Entity entity, ITipList currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
    return null;
  }

  public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
    return null; // not used
  }

  @Override
  public ITipList getWailaTail(Entity entity, ITipList currenttip, IWailaEntityAccessor accessor, IWailaConfigHandler config) {
    return null;
  }

  @Override
  public NBTTagCompound getNBTData(Entity ent, NBTTagCompound tag, IWailaEntityAccessorServer accessor) {
    return tag;
  }

  @Override
  public ITipList getWailaBody(Entity entity, ITipList currenttip, IWailaEntityAccessor accessor,
      IWailaConfigHandler config) {
    String name = EntityList.getEntityString(entity);
    String locKey = "entity." + name + ".desc.";
    String loc = null;
    for (int line = 1; !(loc = StatCollector.translateToLocal(locKey + line)).equals(locKey + line); line++) {
      currenttip.add(loc);
    }
    return currenttip;
  }
}
