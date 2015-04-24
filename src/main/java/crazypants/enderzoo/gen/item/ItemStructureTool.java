package crazypants.enderzoo.gen.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.StructureData;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.vec.Point3i;

public class ItemStructureTool extends Item {

  private static final String NAME = "itemStructureTool";

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
      placeStructure(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
      return true;
    }

    StructureData st = BlockStructureMarker.generateTemplate("test", world, x, y, z, player);
    if(st != null) {
      StructureUtil.writeToFile(player, st);
    }

    return true;
  }

  private void placeStructure(World world, int x, int y, int z) {

    StructureTemplate st = new StructureTemplate(StructureUtil.readFromFile());
    Structure s = new Structure(st, new Point3i(x, y, z));
    EnderZoo.structureManager.generate(world, s);

  }

}
