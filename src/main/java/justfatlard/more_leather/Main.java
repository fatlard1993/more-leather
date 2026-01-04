package justfatlard.more_leather;

import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;

public class Main implements ModInitializer {
	public static final String MOD_ID = "more-leather-justfatlard";

	// Block of Leather (Polymer-compatible)
	public static final RegistryKey<Block> LEATHER_BLOCK_KEY = RegistryKey.of(
		RegistryKeys.BLOCK,
		Identifier.of(MOD_ID, "leather_block")
	);
	public static final LeatherBlock LEATHER_BLOCK = new LeatherBlock(
		AbstractBlock.Settings.create()
			.registryKey(LEATHER_BLOCK_KEY)
			.mapColor(MapColor.ORANGE)
			.strength(0.8f)
			.sounds(BlockSoundGroup.WOOL)
	);

	public static final RegistryKey<Item> LEATHER_BLOCK_ITEM_KEY = RegistryKey.of(
		RegistryKeys.ITEM,
		Identifier.of(MOD_ID, "leather_block")
	);
	public static final Item LEATHER_BLOCK_ITEM = new PolymerBlockItem(
		LEATHER_BLOCK,
		new Item.Settings().registryKey(LEATHER_BLOCK_ITEM_KEY).useBlockPrefixedTranslationKey(),
		Items.BROWN_WOOL
	);

	private record DropConfig(float leatherMin, float leatherMax, float scrapsMin, float scrapsMax, boolean hasVanillaLeather) {}

	private static final Map<String, DropConfig> MOB_DROPS = Map.ofEntries(
		// Small animals - 0-1 leather, 0-2 scraps
		Map.entry("cat", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("ocelot", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("fox", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("wolf", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("bat", new DropConfig(0, 0, 0, 1, false)),

		// Medium animals - 0-1 leather, 1-3 scraps
		Map.entry("pig", new DropConfig(0, 1, 1, 3, false)),
		Map.entry("sheep", new DropConfig(0, 1, 1, 3, false)),
		Map.entry("goat", new DropConfig(0, 1, 1, 3, false)),
		Map.entry("strider", new DropConfig(0, 1, 1, 2, false)),

		// Large wild animals - 1-2 leather, 1-3 scraps
		Map.entry("polar_bear", new DropConfig(1, 2, 1, 3, false)),
		Map.entry("panda", new DropConfig(1, 2, 1, 3, false)),
		Map.entry("camel", new DropConfig(1, 2, 1, 2, false)),
		Map.entry("sniffer", new DropConfig(1, 2, 1, 3, false)),

		// Farm/mount animals with vanilla leather - just add scraps
		Map.entry("cow", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("horse", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("donkey", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("mule", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("llama", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("trader_llama", new DropConfig(0, 0, 1, 2, true)),

		// Special mobs - extra scraps
		Map.entry("mooshroom", new DropConfig(0, 0, 2, 4, true)),
		Map.entry("hoglin", new DropConfig(0, 0, 2, 4, true)),
		Map.entry("ravager", new DropConfig(2, 3, 2, 4, false)),

		// Undead - base scraps (were once human/have decaying flesh)
		Map.entry("zombie", new DropConfig(0, 0, 0, 1, false)),
		Map.entry("husk", new DropConfig(0, 0, 0, 2, false)),
		Map.entry("drowned", new DropConfig(0, 0, 0, 1, false)),
		Map.entry("zombie_villager", new DropConfig(0, 0, 0, 1, false))
	);

	// Mobs that can wear armor
	private static final Set<String> ARMOR_WEARING_MOBS = Set.of(
		"zombie", "husk", "drowned", "zombie_villager", "skeleton", "stray", "wither_skeleton", "piglin", "zombified_piglin"
	);

	// Leather armor pieces and their scrap bonus when worn
	private record ArmorBonus(Item item, int scraps) {}
	private static final ArmorBonus[] LEATHER_ARMOR = {
		new ArmorBonus(Items.LEATHER_HELMET, 4),
		new ArmorBonus(Items.LEATHER_CHESTPLATE, 6),
		new ArmorBonus(Items.LEATHER_LEGGINGS, 5),
		new ArmorBonus(Items.LEATHER_BOOTS, 3)
	};

	@Override
	public void onInitialize() {
		// Enable Polymer resource pack generation
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		PolymerResourcePackUtils.markAsRequired();

		// Register block and item
		Registry.register(Registries.BLOCK, LEATHER_BLOCK_KEY, LEATHER_BLOCK);
		Registry.register(Registries.ITEM, LEATHER_BLOCK_ITEM_KEY, LEATHER_BLOCK_ITEM);

		// Register Polymer block states
		LEATHER_BLOCK.registerPolymerBlockStates();

		// Add to creative tab (Building Blocks, after leather)
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(content -> {
			content.addAfter(Items.HAY_BLOCK, LEATHER_BLOCK_ITEM);
		});

		// Loot table modifications
		LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
			if (!source.isBuiltin()) {
				return;
			}

			String path = key.getValue().getPath();

			// Handle fishing loot
			if (path.equals("gameplay/fishing/junk")) {
				LootPool.Builder fishingScrapsPool = LootPool.builder()
					.with(ItemEntry.builder(Items.RABBIT_HIDE).weight(10))
					.apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2)));
				tableBuilder.pool(fishingScrapsPool);
				return;
			}

			// Handle entity loot tables
			if (!path.startsWith("entities/")) {
				return;
			}

			String mobName = path.substring("entities/".length());

			// Standard mob drops
			DropConfig config = MOB_DROPS.get(mobName);
			if (config != null) {
				// Add leather if mob doesn't have vanilla leather drops
				if (!config.hasVanillaLeather && config.leatherMax > 0) {
					LootPool.Builder leatherPool = LootPool.builder()
						.with(ItemEntry.builder(Items.LEATHER))
						.apply(SetCountLootFunction.builder(
							UniformLootNumberProvider.create(config.leatherMin, config.leatherMax)));
					tableBuilder.pool(leatherPool);
				}

				// Add leather scraps (rabbit_hide)
				if (config.scrapsMax > 0) {
					LootPool.Builder scrapsPool = LootPool.builder()
						.with(ItemEntry.builder(Items.RABBIT_HIDE))
						.apply(SetCountLootFunction.builder(
							UniformLootNumberProvider.create(config.scrapsMin, config.scrapsMax)));
					tableBuilder.pool(scrapsPool);
				}
			}

			// Leather armor bonus drops for mobs that can wear armor
			if (ARMOR_WEARING_MOBS.contains(mobName)) {
				var itemLookup = wrapperLookup.getOrThrow(RegistryKeys.ITEM);

				for (ArmorBonus armor : LEATHER_ARMOR) {
					ItemPredicate.Builder itemPredicate = ItemPredicate.Builder.create()
						.items(itemLookup, armor.item);

					EntityEquipmentPredicate.Builder equipmentBuilder = EntityEquipmentPredicate.Builder.create();

					// Determine which slot to check based on armor type
					if (armor.item == Items.LEATHER_HELMET) {
						equipmentBuilder.head(itemPredicate);
					} else if (armor.item == Items.LEATHER_CHESTPLATE) {
						equipmentBuilder.chest(itemPredicate);
					} else if (armor.item == Items.LEATHER_LEGGINGS) {
						equipmentBuilder.legs(itemPredicate);
					} else if (armor.item == Items.LEATHER_BOOTS) {
						equipmentBuilder.feet(itemPredicate);
					}

					LootPool.Builder armorBonusPool = LootPool.builder()
						.with(ItemEntry.builder(Items.RABBIT_HIDE))
						.apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(armor.scraps)))
						.conditionally(EntityPropertiesLootCondition.builder(
							LootContext.EntityReference.THIS,
							EntityPredicate.Builder.create().equipment(equipmentBuilder)
						));

					tableBuilder.pool(armorBonusPool);
				}
			}
		});

		System.out.println("[more-leather] Loaded More Leather mod!");
	}
}
