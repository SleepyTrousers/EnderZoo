package crazypants.enderzoo;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderzoo.enchantment.EnchantmentWitherArrow;
import crazypants.enderzoo.enchantment.EnchantmentWitherWeapon;
import crazypants.enderzoo.entity.EntityOwl;
import crazypants.enderzoo.entity.MobInfo;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;

public class RegistryHandler {
	
	public static final List<Item> ITEMS = new ArrayList<Item>();
	public static final List<Block> BLOCKS = new ArrayList<Block>();

	@SubscribeEvent
	public void onBlockRegister(Register<Block> e) {
		e.getRegistry().registerAll(BLOCKS.toArray(new Block[BLOCKS.size()]));
	}
	
	@SubscribeEvent
	public void onItemRegister(Register<Item> e) {
		e.getRegistry().registerAll(ITEMS.toArray(new Item[ITEMS.size()]));
	}
	
	@SubscribeEvent
	public void onPotionTypeRegister(Register<PotionType> e) {
		EnderZoo.potions.registerPotions(e.getRegistry());
	}
	
	@SubscribeEvent
	public void onPotionRegister(Register<Potion> e) {
		e.getRegistry().register(EnderZoo.potions.getFloatingPotion());
	}
	
	@SubscribeEvent
	public void onSoundEventRegister(Register<SoundEvent> e) {
	    IForgeRegistry<SoundEvent> reg = e.getRegistry();
	    reg.register(EntityOwl.SND_HOOT);
	    reg.register(EntityOwl.SND_HOOT2);
	    reg.register(EntityOwl.SND_HURT);
	}
	
	@SubscribeEvent
	public void onEntityRegister(Register<EntityEntry> e) {
	    for (MobInfo mob : MobInfo.values()) {
	    	EntityEntry entry = new EntityEntry(mob.getClz(), mob.getName());
	    	entry.setEgg(new EntityEggInfo(new ResourceLocation(EnderZoo.MODID, mob.getName()),mob.getEggForegroundColor(), mob.getEggBackgroundColor()));
	        e.getRegistry().register(entry);
	      }
	}
	
	@SubscribeEvent
	public void onRecipeRegister(Register<IRecipe> e) {
		EnderZoo.addRecipes();
	}
	
	@SubscribeEvent
	public void onEnchantmentRegister(Register<Enchantment> e) {
		e.getRegistry().register(new EnchantmentWitherArrow());
		e.getRegistry().register(new EnchantmentWitherWeapon());
	}
	
}
