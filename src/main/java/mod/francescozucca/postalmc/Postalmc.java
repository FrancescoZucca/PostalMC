package mod.francescozucca.postalmc;

import mod.francescozucca.postalmc.block.AddressBook;
import mod.francescozucca.postalmc.block.Mailbox;
import mod.francescozucca.postalmc.block.entity.AddressBookBlockEntity;
import mod.francescozucca.postalmc.block.entity.MailboxBlockEntity;
import mod.francescozucca.postalmc.block.gui.AddressBookGUI;
import mod.francescozucca.postalmc.block.gui.MailboxScreenHandler;
import mod.francescozucca.postalmc.item.LattiStamp;
import mod.francescozucca.postalmc.item.SardinianStamp;
import mod.francescozucca.postalmc.util.IMailbox;
import mod.francescozucca.postalmc.util.IStamp;
import mod.francescozucca.postalmc.util.MailboxDestination;
import mod.francescozucca.postalmc.util.MailboxManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Postalmc implements ModInitializer {

    public static MailboxManager MMAN;

    public static final Logger LOGGER = LogManager.getLogger();

    public static final Identifier SEND_ITEM_NETWORK_PACKET = id("send_item_network_packet");
    public static final Identifier UPDATE_MAILBOX_SPRITE = id("update_mailbox_sprite");
    public static final Identifier CHECK_MAILBOX = id("check_mailbox");

    public static final Block MAILBOX = new Mailbox(FabricBlockSettings.copyOf(Blocks.CHEST));
    public static final Block ADDRESS_BOOK = new AddressBook(FabricBlockSettings.copyOf(Blocks.LECTERN));
    public static final BlockEntityType<MailboxBlockEntity> MAILBOX_BLOCK_ENTITY_BLOCK_ENTITY_TYPE;
    public static final ScreenHandlerType<MailboxScreenHandler> MAILBOX_SCREEN_HANDLER_SCREEN_HANDLER_TYPE;
    public static final ScreenHandlerType<AddressBookGUI> ADDRESS_BOOK_GUI_SCREEN_HANDLER_TYPE;
    public static final BlockEntityType<AddressBookBlockEntity> ADDRESS_BOOK_BLOCK_ENTITY_BLOCK_ENTITY_TYPE;
    public static Item SARDINIAN_STAMP = new SardinianStamp(new Item.Settings());
    public static final Item WRENCH;
    public static final Item LATTI_STAMP;

    public static final ItemGroup POSTALMC_ITEMGROUP = FabricItemGroupBuilder.build(
            id("general"),
            () -> new ItemStack(SARDINIAN_STAMP)
    );

    static{
        MAILBOX_BLOCK_ENTITY_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("mailbox"), FabricBlockEntityTypeBuilder.create(MailboxBlockEntity::new, MAILBOX).build(null));
        MAILBOX_SCREEN_HANDLER_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(id("mailbox"), MailboxScreenHandler::new);
        SARDINIAN_STAMP = registerItem(new SardinianStamp(new Item.Settings().group(POSTALMC_ITEMGROUP)), "sardinian_stamp");
        LATTI_STAMP = registerItem(new LattiStamp(new Item.Settings().group(POSTALMC_ITEMGROUP)), "latti_stamp");
        WRENCH = registerItem(new Item(new FabricItemSettings().group(POSTALMC_ITEMGROUP)), "wrench");
        ADDRESS_BOOK_GUI_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerExtended(id("address_book"), ((syncId, inventory, buf) -> new AddressBookGUI(syncId, inventory, buf, ScreenHandlerContext.EMPTY)));
        ADDRESS_BOOK_BLOCK_ENTITY_BLOCK_ENTITY_TYPE = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("address_book"), FabricBlockEntityTypeBuilder.create(AddressBookBlockEntity::new, ADDRESS_BOOK).build(null));
    }

    @Override
    public void onInitialize() {
        registerBlock(MAILBOX, "mailbox", true);
        registerBlock(ADDRESS_BOOK, "address_book", true);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> MMAN = (MailboxManager) server.getOverworld().getPersistentStateManager().getOrCreate(MailboxManager::fromNbt, MailboxManager::new, "postalmc"));

        ServerPlayNetworking.registerGlobalReceiver(SEND_ITEM_NETWORK_PACKET, ((server, player, handler, buf, responseSender) -> {
            BlockPos mailboxPos = buf.readBlockPos();
            BlockPos addressBookPos = buf.readBlockPos();
            int stamps = buf.readInt();
            ServerWorld world = player.getWorld();
            server.execute(()-> {
                BlockEntity be = world.getBlockEntity(mailboxPos);
                BlockEntity be2 = world.getBlockEntity(addressBookPos);
                if (be instanceof MailboxBlockEntity mbe && be2 instanceof AddressBookBlockEntity abe) {
                    if(player.getInventory().main.stream().anyMatch(is -> is.getItem() instanceof IStamp)&&!abe.getStack(0).isEmpty()) {
                        if(IMailbox.removeStampsFromPlayer(player.getInventory(), stamps)) {
                            if (MMAN.sendToMailbox(mbe, abe.getStack(0)))
                                abe.setStack(0, ItemStack.EMPTY);
                        }
                    }
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(UPDATE_MAILBOX_SPRITE, ((server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            Identifier sprite = buf.readIdentifier();
            server.execute(()->{
                if(player.getWorld().getBlockEntity(pos) instanceof MailboxBlockEntity mbe){
                    mbe.setSprite(sprite);
                }
            });
        }));

        ServerPlayNetworking.registerGlobalReceiver(CHECK_MAILBOX, ((server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            server.execute(()->{
                if(!(player.getWorld().getBlockEntity(pos) instanceof MailboxBlockEntity)){
                    MMAN.removeMailbox(new MailboxDestination(pos, null));
                }
            });
        }));
    }

    public static Identifier id(String name){
        return new Identifier("postalmc", name);
    }

    private static Block registerBlock(Block block, String name, Item.Settings settings){
        registerItem(new BlockItem(block, settings), name);
        return registerBlock(block, name, false);
    }

    private static Block registerBlock(Block block, String name, boolean hasItem){
        if(hasItem)
            return registerBlock(block, name, new Item.Settings().group(POSTALMC_ITEMGROUP));
        return Registry.register(Registry.BLOCK, id(name), block);
    }

    private static Item registerItem(Item item, String name){
        return Registry.register(Registry.ITEM, id(name), item);
    }
}
