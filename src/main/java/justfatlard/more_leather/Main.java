package justfatlard.more_leather;

import justfatlard.pandorical.api.BlockRegistration;
import justfatlard.pandorical.api.ItemRegistration;
import justfatlard.pandorical.api.PandoricalApi;
import justfatlard.pandorical.api.VanillaItemOverride;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class Main implements ModInitializer {
	public static final String MOD_ID = "more-leather-justfatlard";
	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
		// Small animals
		Map.entry("cat", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("ocelot", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("fox", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("wolf", new DropConfig(0, 1, 0, 2, false)),
		Map.entry("bat", new DropConfig(0, 0, 0, 1, false)),

		// Medium animals
		Map.entry("pig", new DropConfig(0, 1, 1, 3, false)),
		Map.entry("sheep", new DropConfig(0, 1, 1, 3, false)),
		Map.entry("goat", new DropConfig(0, 1, 1, 3, false)),
		Map.entry("strider", new DropConfig(0, 1, 1, 2, false)),

		// Large wild animals
		Map.entry("polar_bear", new DropConfig(1, 2, 1, 3, false)),
		Map.entry("panda", new DropConfig(1, 2, 1, 3, false)),
		Map.entry("camel", new DropConfig(1, 2, 1, 2, false)),
		Map.entry("sniffer", new DropConfig(1, 2, 1, 3, false)),

		// Farm/mount animals that already drop vanilla leather
		Map.entry("cow", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("horse", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("donkey", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("mule", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("llama", new DropConfig(0, 0, 1, 2, true)),
		Map.entry("trader_llama", new DropConfig(0, 0, 1, 2, true)),

		// Special mobs
		Map.entry("mooshroom", new DropConfig(0, 0, 2, 4, true)),
		Map.entry("hoglin", new DropConfig(0, 0, 2, 4, true)),
		Map.entry("ravager", new DropConfig(2, 3, 2, 4, false)),

		// Undead: decaying flesh still yields scraps
		Map.entry("zombie", new DropConfig(0, 0, 0, 1, false)),
		Map.entry("husk", new DropConfig(0, 0, 0, 2, false)),
		Map.entry("drowned", new DropConfig(0, 0, 0, 1, false)),
		Map.entry("zombie_villager", new DropConfig(0, 0, 0, 1, false))
	);

	private static final Set<String> ARMOR_WEARING_MOBS = Set.of(
		"zombie", "husk", "drowned", "zombie_villager", "skeleton", "stray", "wither_skeleton", "piglin", "zombified_piglin"
	);

	private record ArmorBonus(Item item, int scraps) {}
	private static final ArmorBonus[] LEATHER_ARMOR = {
		new ArmorBonus(Items.LEATHER_HELMET, 4),
		new ArmorBonus(Items.LEATHER_CHESTPLATE, 6),
		new ArmorBonus(Items.LEATHER_LEGGINGS, 5),
		new ArmorBonus(Items.LEATHER_BOOTS, 3)
	};

	@Override
	public void onInitialize() {
		// Guarded class load: LeatherQuestRegistration names village-quests types.
		if (net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("village-quests-justfatlard")) {
			justfatlard.more_leather.integration.LeatherQuestRegistration.register();
		}

		if (PandoricalApi.isAvailable()) {
			PandoricalApi.content().registerBlock(MOD_ID + ":leather_block", new BlockRegistration()
				.baseBlock("minecraft:white_wool")
				.model(MOD_ID + ":block/leather_block"));
			PandoricalApi.content().registerItem(MOD_ID + ":leather_block", new ItemRegistration()
				.model(MOD_ID + ":item/leather_block"));
			PandoricalApi.content().overrideVanillaItem("minecraft:rabbit_hide",
				new VanillaItemOverride()
					.name("Leather Scraps")
					.textureFrom(MOD_ID, "textures/item/leather_scraps.png"));
			PandoricalApi.content().registerModAssets(MOD_ID);
		}

		Registry.register(BuiltInRegistries.BLOCK, LEATHER_BLOCK_KEY, LEATHER_BLOCK);
		Registry.register(BuiltInRegistries.ITEM, LEATHER_BLOCK_ITEM_KEY, LEATHER_BLOCK_ITEM);

		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			entries.insertAfter(Items.HAY_BLOCK, LEATHER_BLOCK_ITEM);
		});

		// Loot table modifications (leather scraps/bonus drops) are disabled:
		// fabric-loot-api-v3 has no published build for this Minecraft version yet.
		// Re-enable once upstream ports it; MOB_DROPS and LEATHER_ARMOR above feed it.
		// TODO: re-enable when fabric-loot-api-v3 publishes for this MC version

		LOGGER.info("Loaded More Leather mod! (loot table drops temporarily disabled — fabric-loot-api-v3 not yet available for 26.3)");
	}
}
