package crazypants.enderzoo.gen.item;

import java.io.File;
import java.util.Collection;

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
import crazypants.enderzoo.gen.rules.LevelGroundRule;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureData;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.TemplateRegister;
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
  public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {

    if(world.isRemote) {
      return true;
    }
    if(world.getBlock(x, y, z) != EnderZoo.blockStructureMarker) {
      ForgeDirection dir = ForgeDirection.getOrientation(side);
      placeStructure(player, world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
      return true;
    }

    StructureData st = BlockStructureMarker.generateTemplate(STRUCT_NAME, world, x, y, z, player);
    if(st != null) {
      StructureUtil.writeToFile(player, st, EXPORT_DIR);
    }

    return true;
  }

  private void placeStructure(EntityPlayer player, World world, int x, int y, int z) {

    StructureData sd = StructureUtil.readFromFile(EXPORT_DIR, STRUCT_NAME);
    StructureTemplate st;
    if(sd == null) {

      Collection<StructureTemplate> tmps = TemplateRegister.instance.getTemplates();
      if(tmps != null && !tmps.isEmpty()) {
        st = tmps.iterator().next();
        player.addChatComponentMessage(new ChatComponentText("Could not load structure: " + STRUCT_NAME + " using default: " + st.getUid()));
      } else {
        player.addChatComponentMessage(new ChatComponentText("Could not load structure: " + STRUCT_NAME));
        return;
      }

    } else {
      st = new StructureTemplate(sd);
    }

    Structure s = new Structure(st, new Point3i(x, y, z));
    boolean res = new LevelGroundRule().isValidLocation(new Point3i(x, y, z), st, EnderZoo.structureManager.getWorldManOrCreate(world), world, world.rand,
        x >> 4, z >> 4);
    System.out.println("ItemStructureTool.placeStructure: " + res);
    EnderZoo.structureManager.generate(world, s);

  }

}
