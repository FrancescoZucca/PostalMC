package mod.francescozucca.postalmc.block.entity;

import mod.francescozucca.postalmc.Postalmc;
import mod.francescozucca.postalmc.block.gui.MailboxScreenHandler;
import mod.francescozucca.postalmc.util.IMailbox;
import mod.francescozucca.postalmc.util.ImplementedInventory;
import mod.francescozucca.postalmc.util.MailboxManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MailboxBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, IMailbox {

    private String name;
    private Identifier sprite;

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

    public MailboxBlockEntity(BlockPos pos, BlockState state) {
        super(Postalmc.MAILBOX_BLOCK_ENTITY_BLOCK_ENTITY_TYPE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public Text getDisplayName() {
        return name==null? Text.of(getCachedState().getBlock().getTranslationKey()) :Text.of(name);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new MailboxScreenHandler(syncId, inv, this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.items);
        name = nbt.getString("name");
        sprite = Identifier.tryParse(nbt.getString("texture"));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.items);
        if(name!=null)
            nbt.putString("name", name);
        nbt.putInt("x", pos.getX());
        nbt.putInt("y", pos.getY());
        nbt.putInt("z", pos.getZ());
        nbt.putString("texture", sprite==null?new Identifier("textures/block/grass_block_side.png").toString():sprite.toString());
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Identifier getSprite() {
        return sprite;
    }

    @Override
    public RegistryKey<World> getDimension() {
        assert world != null;
        return world.getRegistryKey();
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSprite(Identifier sprite){
        this.sprite = sprite==null?new Identifier("minecraft", "textures/block/grass_block_side.png"):sprite;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        if(name!=null) {
            buf.writeBoolean(true);
            buf.writeString(name);
        }else
        buf.writeBoolean(false);
    }
}
