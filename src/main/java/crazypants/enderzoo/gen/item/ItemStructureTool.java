package crazypants.enderzoo.gen.item;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderzoo.EnderZoo;
import crazypants.enderzoo.EnderZooTab;
import crazypants.enderzoo.gen.StructureUtil;
import crazypants.enderzoo.gen.StructureRegister;
import crazypants.enderzoo.gen.io.StructureResourceManager;
import crazypants.enderzoo.gen.structure.Structure;
import crazypants.enderzoo.gen.structure.Structure.Rotation;
import crazypants.enderzoo.gen.structure.StructureTemplate;
import crazypants.enderzoo.gen.structure.StructureGenerator;
import crazypants.enderzoo.gen.structure.validator.LevelGroundValidator;
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

   
    if(world.getBlock(x, y, z) == EnderZoo.blockStructureMarker) {
      return false;
    }
    if(world.isRemote) {
      return true;
    }

    if(player.isSneaking()) {
      String uid = setNextUid(stack);
      player.addChatComponentMessage(new ChatComponentText("Structure Generator set to " + uid));
      return true;
    }
    
    String uid = getGenUid(stack);
    StructureGenerator gen = StructureRegister.instance.getGenerator(uid);
    if(gen != null) {
      ForgeDirection dir = ForgeDirection.getOrientation(side);
      placeStructure(gen, world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
    }
    return true;
  }

  private String setNextUid(ItemStack stack) {
    String curUid = getGenUid(stack);
    if(curUid == null) {
      return null;
    }
    Iterator<StructureGenerator> it = StructureRegister.instance.getGenerators().iterator();
    while(it.hasNext()) {
      StructureGenerator gen = it.next();
      if(curUid.equals(gen.getUid())) {
        if(it.hasNext()) {
          gen = it.next();          
        } else {
          gen = StructureRegister.instance.getGenerators().iterator().next();
        }
        stack.stackTagCompound.setString("genUid", gen.getUid());
        return gen.getUid();
      }
    }
    //No match, so default to first in list
    it = StructureRegister.instance.getGenerators().iterator();
    if(it.hasNext()) {
      StructureGenerator gen = it.next();
      stack.stackTagCompound.setString("genUid", gen.getUid());
      return gen.getUid();
    }    
    return null;
  }

  private String getGenUid(ItemStack stack) {
    String uid = null;
    if(stack.stackTagCompound == null) {
      stack.stackTagCompound = new NBTTagCompound();
    }
    if(!stack.stackTagCompound.hasKey("genUid")) {
      Collection<StructureGenerator> gens = StructureRegister.instance.getGenerators();
      if(gens != null && !gens.isEmpty()) {
        uid = gens.iterator().next().getUid();

      } else {
        StructureGenerator gen = StructureRegister.instance.getGenerator(ExportManager.STRUCT_NAME, true);
        if(gen != null) {
          uid = gen.getUid();
        }
      }
      if(uid != null) {
        stack.stackTagCompound.setString("genUid", uid);
      }
    } else {
      uid = stack.stackTagCompound.getString("genUid");
    }
    return uid;
  }

  private void placeStructure(StructureGenerator gen, World world, int x, int y, int z) {
    Structure s = gen.createStructure();
    s.setOrigin(new Point3i(x, y, z));
    EnderZoo.structureManager.generate(world, s);
  }

}
