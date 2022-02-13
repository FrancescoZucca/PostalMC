package mod.francescozucca.postalmc.block.gui;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class AddressDestinationGUI extends WPlainPanel {

    WSprite sprite;
    WButton button;

    public AddressDestinationGUI(){
        sprite = new WSprite(new Identifier("minecraft", "textures/block/grass_block_side.png"));
        this.add(sprite, 2, 2, 16, 16);
        button = new WButton(Text.of("nsjnfsdnf"));
        this.add(button, 16+4, 2, 7*18, 20);

        this.setSize(6*18, 18);
    }
}
