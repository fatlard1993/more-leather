package justfatlard.more_leather.integration;

import java.util.List;
import java.util.function.Predicate;
import justfatlard.village_quests.api.LessonApi;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * A leatherworker teaching where hide actually comes from, and what it is worth.
 *
 * <p>Registered with Village Quests when that mod is present. The lessons are
 * the leather economy this mod creates, in the order somebody would need them:
 * what the unit is, where to get it for nothing, what is already made of it,
 * what unpicking costs, and how to carry it.
 *
 * <p>The numbers below were read off this mod's own recipe files rather than
 * its README, which is wrong about harnesses. The load-bearing one is the
 * ratio: every leather thing gives back exactly three quarters of the hide
 * that went into it. Plain leather is the only exact round trip.
 *
 * <p>This class must only be touched behind a mod-loaded check. It refers to
 * Village Quests types directly, so loading it without that mod present throws.
 */
public final class LeatherQuestRegistration {
	private LeatherQuestRegistration() {}

	private static final Identifier LEATHER_BLOCK =
		Identifier.fromNamespaceAndPath("more-leather-justfatlard", "leather_block");

	/** What unpicking leather boots gives back, from {@code deconstruct_leather_boots}. */
	private static final int BOOTS_IN_SCRAPS = 12;

	/** The scrap is vanilla rabbit hide, renamed and reskinned for Pandorical clients. */
	private static final Item SCRAP = Items.RABBIT_HIDE;

	/**
	 * Looked up on every call, never cached, and never resolved while building
	 * the lesson list: this craft is registered from {@code Main} before the
	 * block item is put in the registry, so anything eager here reads as absent
	 * forever. The predicate and the graduation gift both run long afterwards.
	 */
	private static Item leatherBlock() {
		return BuiltInRegistries.ITEM.getOptional(LEATHER_BLOCK).orElse(null);
	}

	private static Predicate<ItemStack> atLeast(Item item, int count) {
		return stack -> stack.is(item) && stack.getCount() >= count;
	}

	public static void register() {
		LessonApi.register(new LessonApi.Craft(
			"more-leather:leatherwork",
			"leatherworker",
			LessonApi.Policy.standard(),
			lessons(),
			new LessonApi.Openings(
				LessonApi.lines(
					"{former} is gone. They had you part-way through the trade and I would rather it did not stop there. ",
					"You were learning off {former}. I would not have said a word while they were working. Now: ",
					"*clears a space on the bench* {former}'s student. I know about where they left you. "),
				LessonApi.lines(
					"Bench is free when you want the next one.",
					"*not looking up from the stitching* There's another when you've time.",
					"Next one's ready. It keeps -- hide always does, that's rather the point."),
				LessonApi.lines(
					"{former} is gone. Their awl is still on the bench where they set it down.",
					"You'll have heard about {former}. They were teaching you what the stuff is worth, weren't they."),
				LessonApi.lines(
					"*glances at your pack* You keep scraps loose and leather stacked. Somebody taught you that.",
					"You've not thrown a rotten scrap of anything away in front of me yet. That's a trained habit.",
					"{mentor} taught you, didn't they. They always start people on what the unit is.")),
			new LessonApi.Hooks() {
				@Override
				public void onLesson(ServerPlayer player, ServerLevel world, int beat, LessonApi.Teacher teacher) {
					// Lesson four is a ratio, and a ratio is better handed back
					// than described. They unpick the boots in front of you.
					if (beat == 4) {
						teacher.give(new ItemStack(SCRAP, BOOTS_IN_SCRAPS));
					}
				}

				@Override
				public void onGraduate(ServerPlayer player, ServerLevel world, LessonApi.Teacher teacher) {
					teacher.says("Keep the block. You made it out of things people bury.");
					Item block = leatherBlock();
					if (block != null) teacher.give(new ItemStack(block));
					teacher.laterInTheVillage("There is a smell of the smoker going at odd hours near "
						+ teacher.name() + "'s place, and nobody in the village has lost a cow.", 0);
				}
			}));
	}

	private static List<LessonApi.Lesson> lessons() {
		return List.of(
			new LessonApi.Lesson(
				"Before anything else: bring me four scraps. Rabbits, if you must, though I will show you better shortly. Four. Not three, "
					+ "not a whole hide -- four scraps, so you have them in your hand while I tell you what they are.",
				"bring {name} four leather scraps",
				"Four scraps make a leather. A leather makes four scraps. Exactly.",
				"*lines them up on the bench* There. Four of these is one leather, and one leather comes apart into four of these again. "
					+ "Exactly four, both directions, nothing lost either way. Leather is not the unit. This is. Leather is just four of "
					+ "them tied up so they stack better.",
				"Which means you are never stuck. Too many scraps and not enough leather, or the other way about -- either is a moment's "
					+ "work at a bench. Nobody ever thinks to go backwards.",
				SCRAP, atLeast(SCRAP, 4), 6),

			new LessonApi.Lesson(
				"Now the better way I mentioned. Four leather this time, and I want you to get them without troubling a single cow. "
					+ "-- I will not tell you how. You have the answer in your pack already and have been throwing it away for months.",
				"bring {name} four leather -- without killing anything for it",
				"Rotten flesh. In a furnace. It comes out as hide. It always has.",
				"*takes them* Rotten flesh. That is the answer. Put it in a furnace or a smoker like it were a joint of meat and it comes "
					+ "out hide. It has always done that, and nothing in the world would ever tell you so -- a furnace gives no hint what "
					+ "it will take.",
				"And you have too much of it. Everyone does. It is the one thing nobody carries home and the only thing here that is "
					+ "genuinely without limit -- the dark makes more of it every night whether you turn up or not.",
				Items.LEATHER, atLeast(Items.LEATHER, 4), 6),

			new LessonApi.Lesson(
				"A book. One book, any book -- off a shelf, out of a chest, off a librarian, I do not care where. Bring it here and do not "
					+ "read it on the way.",
				"bring {name} a book",
				"That's three scraps in your hand. A book is mostly hide.",
				"*taps the cover* What do you think that is? Hide. A book is paper and a wrap of hide, and it comes apart into three "
					+ "scraps at a bench like anything else.",
				"So a librarian's shelf is a herd, and so is every chest in every ruin you have ever emptied and left the books in. Item "
					+ "frames, the same. Bundles, the same. People walk past leather every day because it has been made into something.",
				Items.BOOK, stack -> stack.is(Items.BOOK), 8),

			new LessonApi.Lesson(
				"Bring me a pair of leather boots. Made, bought, looted, I do not mind which. I am going to take them apart in front of "
					+ "you, and you are going to watch the count.",
				"bring {name} a pair of leather boots",
				"Four leather in. Twelve scraps back. Three quarters. Always three quarters.",
				"*unpicks them without hurrying, counts the pile* Twelve. Boots cost four leather to make, which is sixteen scraps, and "
					+ "they give back twelve. Three quarters. -- Now: the helmet is three quarters. The chestplate is three quarters. The "
					+ "leggings, the horse armour, the saddle, the book, the frame. Every one of them, three quarters.",
				"So never make a thing to store hide in -- you pay a quarter for the privilege. But a pair of boots you found in a chest "
					+ "cost you nothing at all, and three quarters of nothing is still twelve scraps. Strip the ruins. Leave the cows.",
				Items.LEATHER_BOOTS, stack -> stack.is(Items.LEATHER_BOOTS), 8),

			new LessonApi.Lesson(
				"Last thing. A full block of leather -- nine, pressed together. And I will know if you bought it. By now you should be "
					+ "able to make nine without going near a living animal, and that is the only reason I am asking for nine.",
				"bring {name} a block of leather",
				"Nine in, nine out. The press is the one thing that costs you nothing.",
				"*sets it on the shelf, satisfied* Nine leather in and nine leather out again whenever you want them -- the press is the "
					+ "one thing in this trade that takes no cut at all. Everything else you now know rounds down. This does not.",
				"And you got there off flesh nobody wanted and books nobody read. That is the whole trade. Not making hide out of "
					+ "nothing -- noticing it was already hide.",
				null, stack -> {
					Item block = leatherBlock();
					return block != null && stack.is(block);
				}, 12));
	}
}
