package crazypants.enderzoo.spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGrass;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import crazypants.enderzoo.config.Config;
import crazypants.enderzoo.entity.EntityDireSlime;
import crazypants.enderzoo.entity.IEnderZooMob;

public class MobSpawnEventHandler {

  private static final String APPLIED_KEY = "ezModsApp";

  private static List<EntityLivingBase> toApplyEZ = new ArrayList<EntityLivingBase>();
  private static List<EntityLivingBase> toApplyOthers = new ArrayList<EntityLivingBase>();

  private Map<EnumDifficulty, Double> ezHealthMods = new HashMap<EnumDifficulty, Double>();
  private Map<EnumDifficulty, Double> ezAttackMods = new HashMap<EnumDifficulty, Double>();

  private Map<EnumDifficulty, Double> otherHealthMods = new HashMap<EnumDifficulty, Double>();
  private Map<EnumDifficulty, Double> otherAttackMods = new HashMap<EnumDifficulty, Double>();

  public MobSpawnEventHandler() {

    ezHealthMods.put(EnumDifficulty.PEACEFUL, 1d);
    ezHealthMods.put(EnumDifficulty.EASY, Config.enderZooEasyHealthModifier);
    ezHealthMods.put(EnumDifficulty.NORMAL, Config.enderZooNormalHealthModifier);
    ezHealthMods.put(EnumDifficulty.HARD, Config.enderZooHardHealthModifier);
    ezAttackMods.put(EnumDifficulty.PEACEFUL, 1d);
    ezAttackMods.put(EnumDifficulty.EASY, Config.enderZooEasyAttackModifier);
    ezAttackMods.put(EnumDifficulty.NORMAL, Config.enderZooNormalAttackModifier);
    ezAttackMods.put(EnumDifficulty.HARD, Config.enderZooHardAttackModifier);

    otherHealthMods.put(EnumDifficulty.PEACEFUL, 1d);
    otherHealthMods.put(EnumDifficulty.EASY, Config.globalEasyHealthModifier);
    otherHealthMods.put(EnumDifficulty.NORMAL, Config.globalNormalHealthModifier);
    otherHealthMods.put(EnumDifficulty.HARD, Config.globalHardHealthModifier);
    otherAttackMods.put(EnumDifficulty.PEACEFUL, 1d);
    otherAttackMods.put(EnumDifficulty.EASY, Config.globalEasyAttackModifier);
    otherAttackMods.put(EnumDifficulty.NORMAL, Config.globalNormalAttackModifier);
    otherAttackMods.put(EnumDifficulty.HARD, Config.globalHardAttackModifier);

  }

  public void init() {
    MinecraftForge.EVENT_BUS.register(this);
    FMLCommonHandler.instance().bus().register(this);
  }

  @SubscribeEvent
  public void onCheckSpawn(CheckSpawn evt) {
    if (evt.entityLiving == null) {
      return;
    }
    String name = EntityList.getEntityString(evt.entityLiving);
    if (name == null) {
      return;
    }
    for (ISpawnEntry ent : MobSpawns.instance.getEntries()) {
      if (name.equals(ent.getMobName())) {
        if (!ent.canSpawnInDimension(evt.world)) {
          evt.setResult(Result.DENY);
        }
      }
    }

  }

  @SubscribeEvent
  public void onEntityJoinWorld(EntityJoinWorldEvent evt) {
    if (evt.world == null || evt.world.isRemote) {
      return;
    }
    if (Config.enderZooDifficultyModifierEnabled && evt.entity instanceof IEnderZooMob) {
      EntityLivingBase ent = (EntityLivingBase) evt.entity;
      if (!ent.getEntityData().getBoolean(APPLIED_KEY)) {
        toApplyEZ.add(ent);
      }
    } else if (Config.globalDifficultyModifierEnabled && evt.entity instanceof IMob && evt.entity instanceof EntityLivingBase) {
      EntityLivingBase ent = (EntityLivingBase) evt.entity;
      if (!ent.getEntityData().getBoolean(APPLIED_KEY)) {
        toApplyOthers.add(ent);
      }
    }
  }

  @SubscribeEvent
  public void onServerTick(ServerTickEvent evt) {
    if (evt.phase != Phase.END) {
      return;
    }
    for (EntityLivingBase ent : toApplyEZ) {
      if (!ent.isDead && ent.worldObj != null) {
        applyEnderZooModifiers(ent, ent.worldObj);
        ent.getEntityData().setBoolean(APPLIED_KEY, true);
      }
    }
    toApplyEZ.clear();

    for (EntityLivingBase ent : toApplyOthers) {
      if (!ent.isDead && ent.worldObj != null) {
        applyGloablModifiers(ent, ent.worldObj);
        ent.getEntityData().setBoolean(APPLIED_KEY, true);
      }
    }
    toApplyOthers.clear();
  }

  private void applyGloablModifiers(EntityLivingBase entity, World world) {
    if (world == null || world.difficultySetting == null) {
      return;
    }
    double attackModifier = otherAttackMods.get(world.difficultySetting);
    double healthModifier = otherHealthMods.get(world.difficultySetting);
    if (attackModifier != 1) {
      adjustBaseAttack(entity, attackModifier);
    }
    if (healthModifier != 1) {
      addjustBaseHealth(entity, healthModifier);
    }
  }

  private void applyEnderZooModifiers(EntityLivingBase entity, World world) {
    if (world == null || world.difficultySetting == null) {
      return;
    }
    double attackModifier = ezAttackMods.get(world.difficultySetting);
    double healthModifier = ezHealthMods.get(world.difficultySetting);
    if (attackModifier != 1) {
      adjustBaseAttack(entity, attackModifier);
    }
    if (healthModifier != 1) {
      addjustBaseHealth(entity, healthModifier);
    }
  }

  protected void addjustBaseHealth(EntityLivingBase ent, double healthModifier) {
    IAttributeInstance att = ent.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.maxHealth);
    if (att == null) {
      return;
    }
    double curValue = att.getBaseValue();
    //only change in incs of 2 so we dont have 1/2 hearts
    double newValue = (curValue * healthModifier) / 2;
    if (healthModifier >= 1) {
      newValue = Math.ceil(newValue);
    } else {
      newValue = Math.floor(newValue);
    }
    newValue = Math.floor(newValue * 2.0);
    if (newValue < 2) {
      newValue = curValue;
    }
    att.setBaseValue(newValue);
    ent.setHealth((float) newValue);
    //    System.out.println("MobSpawnEventHandler.addjustBaseHealth: Base health changed from: " + curValue + " to " + newValue);
  }

  protected void adjustBaseAttack(EntityLivingBase ent, double attackModifier) {
    IAttributeInstance att = ent.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.attackDamage);
    if (att == null) {
      return;
    }
    double curValue = att.getBaseValue();
    double newValue = curValue * attackModifier;
    att.setBaseValue(newValue);
    //    System.out.println("MobSpawnEventHandler.adjustBaseAttack: base attack changed from " + curValue + " to " + newValue);
  }

  @SubscribeEvent
  public void onBlockHarvest(HarvestDropsEvent event) {
    if (!(event.harvester instanceof FakePlayer)
        && Config.direSlimeEnabled && !event.isCanceled() && (event.block instanceof BlockDirt || event.block instanceof BlockGrass) && event.harvester != null
        && !event.harvester.capabilities.isCreativeMode && event.world != null && !event.world.isRemote && event.harvester.getCurrentEquippedItem() != null
        && !ForgeHooks.isToolEffective(event.harvester.getCurrentEquippedItem(), event.block, event.blockMetadata)
        && Config.direSlimeChance >= event.world.rand.nextFloat()) {
      EntityDireSlime direSlime = new EntityDireSlime(event.world);
      direSlime.setPosition(event.x + 0.5, event.y + 0.0, event.z + 0.5);
      event.world.spawnEntityInWorld(direSlime);
      direSlime.playLivingSound();
      for (ItemStack drop : event.drops) {
        if (drop != null && drop.getItem() != null && drop.getItem() == Item.getItemFromBlock(Blocks.dirt)) {
          if (drop.stackSize > 1) {
            drop.stackSize--;
          } else if (event.drops.size() == 1) {
            event.drops.clear();
          } else {
            event.drops.remove(drop);
          }
          return;
        }
      }
    }
  }
}
