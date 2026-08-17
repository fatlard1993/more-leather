package justfatlard.more_leather.quest;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import justfatlard.village_quests.quest.VillagerQuest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Nine leather, and a reason to stop throwing away rotten flesh.
 *
 * <p>Rotten flesh cooks down into hide. That is the mod's best idea and it is
 * completely invisible: a furnace gives no hint what it will accept, and the one
 * thing every player has too much of is the one thing nobody would ever think to
 * put in one.
 *
 * <p>So the ask is deliberately large. Nine leather is more than a couple of
 * cows, which is exactly the point at which "there is another way to get this"
 * stops being trivia and starts being useful. The leatherworker says the other
 * way out loud, because a hint nobody acts on has taught nobody anything.
 */
public class LeatherBlockQuest extends VillagerQuest {
	private static final Identifier LEATHER_BLOCK =
		Identifier.fromNamespaceAndPath("more-leather-justfatlard", "leather_block");

	public LeatherBlockQuest(String requesterName, UUID villagerUuid) {
		super(VillagerQuest.QuestType.FETCH, requesterName, villagerUuid, 8);
	}

	@Override
	public String getDescription() {
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		String[] lines = {
			this.requesterName + ": \"I need a full block of leather and I am not killing nine cows for it. "
				+ "Cook the rotten flesh you have been throwing away. It comes out as hide. It always has.\"",
			this.requesterName + ": \"Everyone burns rotten flesh or buries it. Put it in a furnace instead "
				+ "and see what comes out. I need a block's worth, pressed together.\"",
			this.requesterName + ": \"A block of leather. Nine of them, pressed. And before you go looking for a herd - "
				+ "the flesh off a zombie cooks down to hide. Nobody believes me until they try it.\""
		};
		return lines[rng.nextInt(lines.length)];
	}

	@Override
	public String getObjective() {
		return "bring " + this.requesterName
			+ " a block of leather - rotten flesh smelts into hide, which is nine leather nobody had to farm";
	}

	@Override
	public Item getSubmissionItem() {
		return BuiltInRegistries.ITEM.getOptional(LEATHER_BLOCK).orElse(null);
	}

	@Override
	public boolean checkCompletion(ServerPlayer player) {
		Item block = getSubmissionItem();
		if (block == null) return false;

		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			if (stack.is(block)) return true;
		}
		return false;
	}

	@Override
	public void onComplete(ServerPlayer player) {
		Item block = getSubmissionItem();
		if (block == null) return;

		for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
			if (stack.is(block)) {
				stack.shrink(1);
				return;
			}
		}
	}
}
