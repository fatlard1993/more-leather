package justfatlard.more_leather;

import eu.pb4.polymer.blocks.api.BlockModelType;
import eu.pb4.polymer.blocks.api.PolymerBlockModel;
import eu.pb4.polymer.blocks.api.PolymerBlockResourceUtils;
import eu.pb4.polymer.blocks.api.PolymerTexturedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PillarBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import xyz.nucleoid.packettweaker.PacketContext;

public class LeatherBlock extends PillarBlock implements PolymerTexturedBlock {
	private BlockState polymerStateY;  // axis=y (default, upright)
	private BlockState polymerStateX;  // axis=x (horizontal, rotated)
	private BlockState polymerStateZ;  // axis=z (horizontal)

	public LeatherBlock(Settings settings) {
		super(settings);
	}

	public void initPolymerState() {
		// Use the same model with different rotations (like vanilla logs)
		Identifier model = Identifier.of(Main.MOD_ID, "block/leather_block");

		// Request block states for each axis orientation
		// Y axis = upright (no rotation)
		this.polymerStateY = PolymerBlockResourceUtils.requestBlock(
			BlockModelType.FULL_BLOCK,
			PolymerBlockModel.of(model, 0, 0)
		);
		// X axis = rotated 90 on X, then 90 on Y
		this.polymerStateX = PolymerBlockResourceUtils.requestBlock(
			BlockModelType.FULL_BLOCK,
			PolymerBlockModel.of(model, 90, 90)
		);
		// Z axis = rotated 90 on X only
		this.polymerStateZ = PolymerBlockResourceUtils.requestBlock(
			BlockModelType.FULL_BLOCK,
			PolymerBlockModel.of(model, 90, 0)
		);
	}

	@Override
	public BlockState getPolymerBlockState(BlockState state, PacketContext context) {
		Direction.Axis axis = state.get(AXIS);

		BlockState result = switch (axis) {
			case X -> polymerStateX;
			case Z -> polymerStateZ;
			default -> polymerStateY;
		};

		if (result == null) {
			return Blocks.BROWN_WOOL.getDefaultState();
		}
		return result;
	}
}
