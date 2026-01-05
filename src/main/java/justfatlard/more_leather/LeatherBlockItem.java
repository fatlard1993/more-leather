package justfatlard.more_leather;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.packettweaker.PacketContext;

public class LeatherBlockItem extends BlockItem implements PolymerItem {

	public LeatherBlockItem(LeatherBlock block, Settings settings) {
		super(block, settings);
	}

	@Override
	public Item getPolymerItem(ItemStack stack, PacketContext context) {
		// For vanilla clients without resource pack, show brown wool
		return Items.BROWN_WOOL;
	}
}
