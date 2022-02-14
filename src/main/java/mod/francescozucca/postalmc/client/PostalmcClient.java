package mod.francescozucca.postalmc.client;

import mod.francescozucca.postalmc.Postalmc;
import mod.francescozucca.postalmc.block.gui.AddressBookGUI;
import mod.francescozucca.postalmc.client.gui.AddressBookScreen;
import mod.francescozucca.postalmc.client.gui.MailboxScreen;
import mod.francescozucca.postalmc.client.renderer.MailboxBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class PostalmcClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(Postalmc.MAILBOX_SCREEN_HANDLER_SCREEN_HANDLER_TYPE, MailboxScreen::new);
        ScreenRegistry.<AddressBookGUI, AddressBookScreen>register(Postalmc.ADDRESS_BOOK_GUI_SCREEN_HANDLER_TYPE, (gui, inventory, title) -> new AddressBookScreen(gui, inventory.player, title));
        BlockEntityRendererRegistry.register(Postalmc.MAILBOX_BLOCK_ENTITY_BLOCK_ENTITY_TYPE, MailboxBlockEntityRenderer::new);
    }
}
