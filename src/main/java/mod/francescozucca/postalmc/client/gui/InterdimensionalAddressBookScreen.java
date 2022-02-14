package mod.francescozucca.postalmc.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import mod.francescozucca.postalmc.block.gui.InterdimensionalAddressBookGUI;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class InterdimensionalAddressBookScreen extends CottonInventoryScreen<InterdimensionalAddressBookGUI> {
    public InterdimensionalAddressBookScreen(InterdimensionalAddressBookGUI description, PlayerEntity entity, Text title) {
        super(description, entity, title);
    }
}
