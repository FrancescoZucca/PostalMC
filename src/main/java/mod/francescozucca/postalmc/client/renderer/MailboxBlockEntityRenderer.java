package mod.francescozucca.postalmc.client.renderer;

import mod.francescozucca.postalmc.Postalmc;
import mod.francescozucca.postalmc.block.entity.MailboxBlockEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class MailboxBlockEntityRenderer implements BlockEntityRenderer<mod.francescozucca.postalmc.block.entity.MailboxBlockEntity> {

    public MailboxBlockEntityRenderer(BlockEntityRendererFactory.Context ctx){}

    @Override
    public void render(MailboxBlockEntity mbe, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Text text = new LiteralText(mbe.getName()==null?"null":mbe.getName());
        matrices.push();
        matrices.translate(0.5, 1.35, 0.5);
        matrices.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
        matrices.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
        int j = (int)(g * 255.0F) << 24;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float h = (float)(-textRenderer.getWidth(text) / 2);
        light = WorldRenderer.getLightmapCoordinates(mbe.getWorld(), mbe.getPos().up());
        textRenderer.draw(text, h, 0.25f, 0xffffffff, false, matrix4f, vertexConsumers, false, j, light);
        matrices.pop();
    }


}
