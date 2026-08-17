package justfatlard.more_leather.integration;

import java.util.Random;
import justfatlard.more_leather.quest.LeatherBlockQuest;
import justfatlard.village_quests.api.QuestRegistry;
import justfatlard.village_quests.quest.VillagerQuest;
import net.minecraft.world.entity.npc.villager.Villager;

/**
 * Offers the leather lesson from the leatherworker and the shepherd, the two who
 * would have opinions about where hide comes from.
 *
 * <p>Names village-quests types directly, so it must only be loaded behind the
 * isModLoaded guard in the entry point.
 */
public final class LeatherQuestRegistration {
	private LeatherQuestRegistration() {}

	private static final float OFFER_CHANCE = 0.12F;

	public static void register() {
		QuestRegistry.registerProfessionQuest("leatherworker", LeatherQuestRegistration::offer);
		QuestRegistry.registerProfessionQuest("shepherd", LeatherQuestRegistration::offer);
	}

	private static VillagerQuest offer(Villager villager, String villagerName, int reputation, Random random) {
		// Nine leather is a real errand, so it wants somebody who would actually
		// ask you for a favour that size.
		if (reputation < 15) return null;
		if (random.nextFloat() > OFFER_CHANCE) return null;

		return new LeatherBlockQuest(villagerName, villager.getUUID());
	}
}
