package mod.francescozucca.postalmc.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.francescozucca.postalmc.block.gui.MailboxScreenHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class MailboxScreen extends HandledScreen<MailboxScreenHandler> {

    public static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/container/dispenser.png");

    public MailboxScreen(MailboxScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, getTitle(handler).orElse(title));
    }

    private static Optional<Text> getTitle(ScreenHandler handler){
        if(handler instanceof MailboxScreenHandler){
            BlockPos pos = ((MailboxScreenHandler) handler).getPos();
            String name = ((MailboxScreenHandler) handler).getName();
            return pos==null||name==null?Optional.empty(): Optional.of(new LiteralText(name + " - (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")"));
        }
        return Optional.empty();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width-backgroundWidth)/2;
        int y = (height-backgroundHeight)/2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth-textRenderer.getWidth(title))/2;
    }
}
