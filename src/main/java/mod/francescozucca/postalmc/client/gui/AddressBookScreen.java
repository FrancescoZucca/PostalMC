package mod.francescozucca.postalmc.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import mod.francescozucca.postalmc.block.gui.AddressBookGUI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class AddressBookScreen extends CottonInventoryScreen<AddressBookGUI> {
    public AddressBookScreen(AddressBookGUI description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
