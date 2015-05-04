package crazypants.enderzoo.gen.item;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.StructureRegister;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.Structure.Rotation;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.StructureGenerator;
import crazypants.enderzoo.gen.structure.validator.LevelGroundValidator;
import crazypants.enderzoo.vec.Point3i;

public class ItemStructureTool extends Item {

  private static final String NAME = "itemStructureTool";

  static final String STRUCT_NAME = "structure";

  static final File EXPORT_DIR = new File("exportedStructureData");

  public static ItemStructureTool create() {
    ItemStructureTool res = new ItemStructureTool();
    res.init();
    return res;
  }

  private ItemStructureTool() {
    setUnlocalizedName(NAME);
    setCreativeTab(EnderZooTab.tabEnderZoo);
    setTextureName("enderzoo:" + NAME);
    setHasSubtypes(false);
  }

  private void init() {
    GameRegistry.registerItem(this, NAME);
  }

  @Override
  public ItemStack onItemRightClick(ItemStack p_77659_1_, World world, EntityPlayer player) {
    
    if(!world.isRemote) {
//      EnderZoo.structureManager.GEN_ENABLED_DEBUG = true; 
//      EnderZoo.structureManager.generate(world.rand, (int)player.posX >> 4, (int)player.posZ >> 4, world, world.getChunkProvider(), world.getChunkProvider());
////      EnderZoo.structureManager.generate(world.rand, 21, 27, world, world.getChunkProvider(), world.getChunkProvider());
//      EnderZoo.structureManager.GEN_ENABLED_DEBUG = false;
//      System.out.println("ItemStructureTool.onItemRightClick: Did gen");
      
//      boolean valid = new LevelGroundRule().isValidLocation(new Point3i(186, 61, 128), TemplateRegister.instance.getTemplate("test"), null, world, null, 186 >> 4, 128 >> 4);
//      System.out.println("ItemStructureTool.onItemRightClick: " + valid);
//      EnderZoo.structureManager.GEN_ENABLED_DEBUG = true; 
    }
    
    return super.onItemRightClick(p_77659_1_, world, player);
  }

  @Override
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    if(world.isRemote) {
      return true;
    }
    if(world.getBlock(x, y, z) != EnderZoo.blockStructureMarker) {
      ForgeDirection dir = ForgeDirection.getOrientation(side);
      placeStructure(player, world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
      return true;
    }

    StructureTemplate st = BlockStructureMarker.generateTemplate(STRUCT_NAME, world, x, y, z, player);
    if(st != null) {
      StructureUtil.writeToFile(player, st, EXPORT_DIR);
    }

    return true;
  }

  private void placeStructure(EntityPlayer player, World world, int x, int y, int z) {

    StructureTemplate sd = StructureUtil.readFromFile(EXPORT_DIR, STRUCT_NAME);
    StructureGenerator st;
    if(sd == null) {

      Collection<StructureGenerator> tmps = StructureRegister.instance.getConfigs();
      if(tmps != null && !tmps.isEmpty()) {
        st = tmps.iterator().next();
        player.addChatComponentMessage(new ChatComponentText("Could not load structure: " + STRUCT_NAME + " using default: " + st.getUid()));
      } else {
        player.addChatComponentMessage(new ChatComponentText("Could not load structure: " + STRUCT_NAME));
        return;
      }

    } else {
      st = new StructureGenerator(sd.getUid(), Collections.singletonList(new StructureGenerator.InstanceGen(sd, null)));
    }

    Structure s = st.createStructure();
    s.setOrigin(new Point3i(x, y, z));
    boolean res = new LevelGroundValidator().isValidLocation(s, EnderZoo.structureManager.getWorldManOrCreate(world), world, world.rand, x >> 4,
        z >> 4);
    System.out.println("ItemStructureTool.placeStructure: " + res);
    EnderZoo.structureManager.generate(world, s);

  }

}
