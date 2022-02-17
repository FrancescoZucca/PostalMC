package mod.francescozucca.postalmc.block.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import mod.francescozucca.postalmc.Postalmc;
import mod.francescozucca.postalmc.util.IMailbox;
import mod.francescozucca.postalmc.util.MailboxDestination;
import mod.francescozucca.postalmc.util.MailboxManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class AddressBookGUI extends SyncedGuiDescription {

    private static final int INVENTORY_SIZE = 1;
    private BlockPos pos;

    public AddressBookGUI(int syncId, PlayerInventory playerInventory, PacketByteBuf buf1, ScreenHandlerContext context){
        super(Postalmc.ADDRESS_BOOK_GUI_SCREEN_HANDLER_TYPE, syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));
        pos = buf1.readBlockPos();

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(320, 150);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 4, 1);

        root.add(this.createPlayerInventoryPanel(),0 ,3);

        BiConsumer<MailboxDestination, AddressDestinationGUI> configurator = (MailboxDestination s, AddressDestinationGUI destination) -> {
            destination.sprite.setImage(new Identifier(s.getSprite().getNamespace(),  s.getSprite().getPath().contains(".png")?s.getSprite().getPath():"textures/"+s.getSprite().getPath()+".png"));
            destination.button.setLabel(new LiteralText(s.getName()+" - "+IMailbox.calculateCost(s, pos, world.getRegistryKey())));
            destination.button.setOnClick(()->{
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(s.getPos());
                buf.writeInt(MailboxManager.getIntFromDim(s.getDimension()));
                buf.writeBlockPos(pos);
                buf.writeInt(IMailbox.calculateCost(s, pos, world.getRegistryKey()));
                ClientPlayNetworking.send(Postalmc.SEND_ITEM_NETWORK_PACKET, buf);
            });
        };

        ArrayList<MailboxDestination> mdlist = Postalmc.getMMANForWorld(world.getRegistryKey()).getMailboxes();

        mdlist.forEach((mailboxDestination -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(mailboxDestination.getPos());
            ClientPlayNetworking.send(Postalmc.CHECK_MAILBOX, buf);
        }));

        mdlist = Postalmc.getMMANForWorld(world.getRegistryKey()).getMailboxes();

        WListPanel<MailboxDestination, AddressDestinationGUI> list = new WListPanel<>(mdlist, AddressDestinationGUI::new ,configurator);
        list.setListItemHeight(18);
        root.add(list, 9, 1, 9, 6);

        root.validate(this);
    }

    public AddressBookGUI(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(Postalmc.ADDRESS_BOOK_GUI_SCREEN_HANDLER_TYPE, syncId, playerInventory, getBlockInventory(context, INVENTORY_SIZE), getBlockPropertyDelegate(context));

        pos = BlockPos.ORIGIN;

        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(320, 150);
        root.setInsets(Insets.ROOT_PANEL);

        WItemSlot itemSlot = WItemSlot.of(blockInventory, 0);
        root.add(itemSlot, 4, 1);

        root.add(this.createPlayerInventoryPanel(),0 ,3);

        BiConsumer<MailboxDestination, AddressDestinationGUI> configurator = (MailboxDestination s, AddressDestinationGUI destination) -> {
            destination.sprite.setImage(new Identifier(s.getSprite().getNamespace(),  s.getSprite().getPath().contains(".png")?s.getSprite().getPath():"textures/"+s.getSprite().getPath()+".png"));
            destination.button.setLabel(new LiteralText(s.getName()+" - "+IMailbox.calculateCost(s, pos, world.getRegistryKey())));
            destination.button.setOnClick(()->{
                PacketByteBuf buf = PacketByteBufs.create();
                buf.writeBlockPos(s.getPos());
                buf.writeBlockPos(pos);
                ClientPlayNetworking.send(Postalmc.SEND_ITEM_NETWORK_PACKET, buf);
            });
        };

        ArrayList<MailboxDestination> mdlist = Postalmc.getMMANForWorld(world.getRegistryKey()).getMailboxes();

        mdlist.forEach((mailboxDestination -> {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(mailboxDestination.getPos());
            ClientPlayNetworking.send(Postalmc.CHECK_MAILBOX, buf);
        }));

        mdlist = Postalmc.getMMANForWorld(world.getRegistryKey()).getMailboxes();

        WListPanel<MailboxDestination, AddressDestinationGUI> list = new WListPanel<>(mdlist, AddressDestinationGUI::new ,configurator);
        list.setListItemHeight(18);
        root.add(list, 9, 1, 9, 6);

        root.validate(this);
    }
}
