package crazypants.enderzoo.gen;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.TemplateRegister;
import crazypants.enderzoo.vec.Point3i;

public class StructureManager implements IWorldGenerator {

  public static StructureManager create() {
    StructureManager sm = new StructureManager();
    sm.init();
    return sm;
  }

  private final Map<Integer, WorldManager> worldManagers = new HashMap<Integer, WorldManager>();

  private File saveDir;

  private final Set<Point3i> generating = new HashSet<Point3i>();

  private StructureManager() {
  }

  private void init() {
    //FMLCommonHandler.instance().bus().register(this);
    MinecraftForge.EVENT_BUS.register(this);
    TemplateRegister.instance.loadDefaultTemplates();

    GameRegistry.registerWorldGenerator(this, 50000);

  }

  @Override
  public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
    Point3i p = new Point3i(world.provider.dimensionId, chunkX, chunkZ);
    if(generating.contains(p)) {
      //      System.out.println("StructureManager.generate: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      //      Log.info("StructureManager.generate: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      return;
    }
    generating.add(p);

    try {
      for (StructureTemplate template : TemplateRegister.instance.getTemplates()) {
        if(template.canSpawnHere(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)) {
          Structure s = template.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
          if(s != null) {
            WorldManager wm = getWorldManOrCreate(world);
            wm.addStructure(s);
            //            wm.save();
          }
        }
      }
    } finally {
      generating.remove(p);
    }
  }

  public void serverStopped(FMLServerStoppedEvent event) {
    worldManagers.clear();
    saveDir = null;
    generating.clear();
  }

  @SubscribeEvent
  public void eventWorldSave(WorldEvent.Save evt) {
    //WorldManager wm = getWorldMan(evt.world);
    WorldManager wm = getWorldManOrCreate(evt.world);
    if(wm != null) {
      wm.save();
    }
  }

  private WorldManager getWorldMan(World world) {
    if(world == null) {
      return null;
    }
    return worldManagers.get(world.provider.dimensionId);
  }

  private WorldManager getWorldManOrCreate(World world) {
    WorldManager res = getWorldMan(world);
    if(res == null) {
      res = new WorldManager(world);
      res.load();
      worldManagers.put(world.provider.dimensionId, res);
    }
    return res;
  }

}
