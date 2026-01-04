package justfatlard.more_leather;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

public class LeatherBlock extends PillarBlock implements PolymerTexturedBlock {
	private BlockState polymerBlockState;

	public LeatherBlock(Settings settings) {
		super(settings);
	}

	@Override
	public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
		return polymerBlockState;
	}

	public void registerPolymerBlockStates() {
		// Register our custom model with Polymer's block system
		polymerBlockState = PolymerBlockResourceUtils.requestBlock(
			BlockModelType.FULL_BLOCK,
			PolymerBlockModel.of(Identifier.of(Main.MOD_ID, "block/leather_block"))
		);
	}
}
