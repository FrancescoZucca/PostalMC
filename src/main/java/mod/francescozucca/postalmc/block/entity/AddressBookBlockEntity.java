package mod.francescozucca.postalmc.block.entity;

import mod.francescozucca.postalmc.Postalmc;
import mod.francescozucca.postalmc.block.gui.AddressBookGUI;
import mod.francescozucca.postalmc.util.ImplementedInventory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AddressBookBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public AddressBookBlockEntity(BlockPos pos, BlockState state) {
        super(Postalmc.ADDRESS_BOOK_BLOCK_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AddressBookGUI(syncId, inv, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }
}
