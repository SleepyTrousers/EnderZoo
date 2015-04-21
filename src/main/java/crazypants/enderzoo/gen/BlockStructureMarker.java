package crazypants.enderzoo.gen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.vec.Point3i;

public class BlockStructureMarker extends Block {

  public static final String NAME = "blockStructureMarker";

  public static BlockStructureMarker create() {
    BlockStructureMarker res = new BlockStructureMarker();
    res.init();
    return res;
  }

  protected BlockStructureMarker() {
    super(Material.rock);
    setHardness(0.5F);
    setBlockName(NAME);
    setStepSound(Block.soundTypeStone);
    setHarvestLevel("pickaxe", 0);
    setCreativeTab(EnderZooTab.tabEnderZoo);
  }

  protected void init() {
    GameRegistry.registerBlock(this, NAME);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {
    blockIcon = iIconRegister.registerIcon("enderzoo:" + NAME);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {
    if(entityPlayer.isSneaking()) {
      return false;
    }

    //have to do it on the server to get TileEntity data
    if(world.isRemote) {
      return true;
    }

    StructureTemplate st = generateTemplate("test", world, x, y, z, entityPlayer);
    if(st != null) {
      StructureUtil.writeToFile(entityPlayer, st);
    }
    return true;
  }



  public static AxisAlignedBB getStructureBounds(IBlockAccess world, int x, int y, int z) {
    short scanDistance = 100;

    short xSize;
    short ySize;
    short zSize;
    Point3i axis = new Point3i(1, 0, 0);
    xSize = getDistance(world, x, y, z, scanDistance, axis);
    axis = new Point3i(0, 1, 0);
    ySize = getDistance(world, x, y, z, scanDistance, axis);
    axis = new Point3i(0, 0, 1);
    zSize = getDistance(world, x, y, z, scanDistance, axis);

    boolean found = true;
    if(xSize == 0) {
      //      entityPlayer.addChatComponentMessage(new ChatComponentText("No marker found along the x axis"));
      return null;
    }
    if(ySize == 0) {
      //      entityPlayer.addChatComponentMessage(new ChatComponentText("No marker found along the y axis"));
      return null;
    }
    if(zSize == 0) {
      //      entityPlayer.addChatComponentMessage(new ChatComponentText("No marker found along the z axis"));
      return null;
    }

    if(!found) {
      return null;
    }

    //go inside one block from marker clicked on
    x += xSize > 0 ? 1 : 0;
    y += ySize > 0 ? 1 : 0;
    z += zSize > 0 ? 1 : 0;

    return AxisAlignedBB.getBoundingBox(
        xSize < 0 ? xSize : 0, ySize < 0 ? ySize : 0, zSize < 0 ? zSize : 0,
        xSize < 0 ? 0 : xSize, ySize < 0 ? 0 : ySize, zSize < 0 ? 0 : zSize).getOffsetBoundingBox(x, y, z);
  }

  public static StructureTemplate generateTemplate(String name, IBlockAccess world, int x, int y, int z, EntityPlayer entityPlayer) {
    AxisAlignedBB bb = getStructureBounds(world, x, y, z);
    if(bb == null) {
      entityPlayer.addChatComponentMessage(new ChatComponentText("Could not find matching markers"));
      return null;
    }

    return new StructureTemplate(name, world, bb);
  }

  private static short getDistance(IBlockAccess world, int x, int y, int z, short scanDistance, Point3i axis) {
    short res = getStepCount(world, x, y, z, scanDistance, axis);
    if(res != 0) {
      return res;
    }
    axis.x *= -1;
    axis.y *= -1;
    axis.z *= -1;
    res = getStepCount(world, x, y, z, scanDistance, axis);
    if(res != 0) {
      return (short) -res;
    }
    return 0;
  }

  private static short getStepCount(IBlockAccess world, int x, int y, int z, short scanDistance, Point3i inc) {
    Point3i tst = new Point3i(x, y, z);
    for (short i = 1; i <= scanDistance; i++) {
      tst.add(inc);
      if(world.getBlock(tst.x, tst.y, tst.z) == EnderZoo.blockStructureMarker) {
        return --i;
      }
    }
    return 0;
  }

}
