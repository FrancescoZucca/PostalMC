package mod.francescozucca.postalmc.block;

import mod.francescozucca.postalmc.Postalmc;
import mod.francescozucca.postalmc.block.entity.MailboxBlockEntity;
import mod.francescozucca.postalmc.util.IMailbox;
import mod.francescozucca.postalmc.util.MailboxManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class Mailbox extends BlockWithEntity {

    public Mailbox(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            if(player.getStackInHand(Hand.MAIN_HAND).getItem() != Postalmc.WRENCH){
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        }else{
            if(player.getStackInHand(Hand.MAIN_HAND).getItem() == Postalmc.WRENCH){
                if(player.getStackInHand(Hand.OFF_HAND).getItem() instanceof BlockItem bi) {
                    Block block = Block.getBlockFromItem(bi);
                    BlockState state1 = block.getDefaultState();
                    Identifier sprite = MinecraftClient.getInstance().getBlockRenderManager().getModel(state1).getQuads(state, Direction.NORTH, new Random()).get(0).getSprite().getId();
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(pos);
                    buf.writeIdentifier(sprite);
                    ClientPlayNetworking.send(Postalmc.UPDATE_MAILBOX_SPRITE, buf);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if(world.isClient) return;
        BlockEntity be = world.getBlockEntity(pos);
        if(itemStack.hasCustomName()){
            if (be instanceof MailboxBlockEntity) {
                ((MailboxBlockEntity) be).setName(itemStack.getName()==null?null:itemStack.getName().asString());
            }
        }
        Postalmc.getMMANForWorld((ServerWorld) world).addMailbox((MailboxBlockEntity) be);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(world.isClient) return;
        Postalmc.getMMANForWorld((ServerWorld) world).removeMailbox((MailboxBlockEntity) world.getBlockEntity(pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MailboxBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return super.getTicker(world, state, type);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof MailboxBlockEntity) {
                ItemScatterer.spawn(world, pos, (MailboxBlockEntity)blockEntity);
                // update comparators
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getGameEventListener(World world, T blockEntity) {
        return super.getGameEventListener(world, blockEntity);
    }
}
