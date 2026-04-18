package justfatlard.more_leather;

import justfatlard.pandorical.api.BlockRegistration;
import justfatlard.pandorical.api.ItemRegistration;
import justfatlard.pandorical.api.PandoricalApi;
import justfatlard.pandorical.api.VanillaItemOverride;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class Main implements ModInitializer {
	public static final String MOD_ID = "more-leather-justfatlard";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Block of Leather
	public static final ResourceKey<Block> LEATHER_BLOCK_KEY = ResourceKey.create(
		Registries.BLOCK,
		Identifier.fromNamespaceAndPath(MOD_ID, "leather_block")
	);
	public static final LeatherBlock LEATHER_BLOCK = new LeatherBlock(
		BlockBehaviour.Properties.of()
			.setId(LEATHER_BLOCK_KEY)
			.mapColor(MapColor.COLOR_ORANGE)
			.strength(0.8f)
			.sound(SoundType.WOOL)
	);

	public static final ResourceKey<Item> LEATHER_BLOCK_ITEM_KEY = ResourceKey.create(
		Registries.ITEM,
		Identifier.fromNamespaceAndPath(MOD_ID, "leather_block")
	);
	public static final LeatherBlockItem LEATHER_BLOCK_ITEM = new LeatherBlockItem(
		LEATHER_BLOCK,
		new Item.Properties().setId(LEATHER_BLOCK_ITEM_KEY).useBlockDescriptionPrefix()
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
		// Register with Pandorical if available
		if (PandoricalApi.isAvailable()) {
			PandoricalApi.content().registerBlock(MOD_ID + ":leather_block", new BlockRegistration()
				.model(MOD_ID + ":block/leather_block"));
			PandoricalApi.content().registerItem(MOD_ID + ":leather_block", new ItemRegistration()
				.model(MOD_ID + ":item/leather_block"));
			PandoricalApi.content().overrideVanillaItem("minecraft:rabbit_hide",
				new VanillaItemOverride()
					.name("Leather Scraps")
					.textureFrom(MOD_ID, "textures/item/leather_scraps.png"));
			PandoricalApi.content().registerModAssets(MOD_ID);
		}

		// Register block
		Registry.register(BuiltInRegistries.BLOCK, LEATHER_BLOCK_KEY, LEATHER_BLOCK);
		Registry.register(BuiltInRegistries.ITEM, LEATHER_BLOCK_ITEM_KEY, LEATHER_BLOCK_ITEM);

		// Add to creative tab (Building Blocks, after hay block)
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			entries.insertAfter(Items.HAY_BLOCK, LEATHER_BLOCK_ITEM);
		});

		// Loot table modifications
		LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
			if (!source.isBuiltin()) {
				return;
			}

			String path = key.identifier().getPath();

			// Handle fishing loot - 50% chance of scraps from junk catches
			if (path.equals("gameplay/fishing/junk")) {
				LootPool.Builder fishingScrapsPool = LootPool.lootPool()
					.add(LootItem.lootTableItem(Items.RABBIT_HIDE))
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
					.when(LootItemRandomChanceCondition.randomChance(0.5f));
				tableBuilder.pool(fishingScrapsPool.build());
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
					LootPool.Builder leatherPool = LootPool.lootPool()
						.add(LootItem.lootTableItem(Items.LEATHER))
						.apply(SetItemCountFunction.setCount(
							UniformGenerator.between(config.leatherMin, config.leatherMax)));
					tableBuilder.pool(leatherPool.build());
				}

				// Add leather scraps
				if (config.scrapsMax > 0) {
					LootPool.Builder scrapsPool = LootPool.lootPool()
						.add(LootItem.lootTableItem(Items.RABBIT_HIDE))
						.apply(SetItemCountFunction.setCount(
							UniformGenerator.between(config.scrapsMin, config.scrapsMax)));
					tableBuilder.pool(scrapsPool.build());
				}
			}

			// Leather armor bonus drops for mobs that can wear armor
			if (ARMOR_WEARING_MOBS.contains(mobName)) {
				var itemLookup = wrapperLookup.lookupOrThrow(Registries.ITEM);

				for (ArmorBonus armor : LEATHER_ARMOR) {
					ItemPredicate.Builder itemPredicate = ItemPredicate.Builder.item()
						.of(itemLookup, armor.item);

					EntityEquipmentPredicate.Builder equipmentBuilder = EntityEquipmentPredicate.Builder.equipment();

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

					LootPool.Builder armorBonusPool = LootPool.lootPool()
						.add(LootItem.lootTableItem(Items.RABBIT_HIDE))
						.apply(SetItemCountFunction.setCount(ConstantValue.exactly(armor.scraps)))
						.when(LootItemEntityPropertyCondition.hasProperties(
							LootContext.EntityTarget.THIS,
							EntityPredicate.Builder.entity().equipment(equipmentBuilder)
						));

					tableBuilder.pool(armorBonusPool.build());
				}
			}
		});

		LOGGER.info("Loaded More Leather mod!");
	}
}
