package mod.francescozucca.postalmc.util;

import mod.francescozucca.postalmc.block.entity.MailboxBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class MailboxDestination implements IMailbox{

    private final BlockPos pos;
    private final String name;
    private final MailboxBlockEntity entity;
    private Identifier sprite;

    public MailboxDestination(BlockPos pos, String name){
        this.pos = pos;
        this.name = name;
        this.entity = null;
        this.sprite = new Identifier("minecraft", "textures/block/grass_block_side.png");
    }

    public MailboxDestination(MailboxBlockEntity be){
        entity = be;
        pos = be.getPos();
        name = be.getName();
        sprite = be.getSprite();
    }

    public MailboxBlockEntity getEntity() {
        return entity;
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
        return sprite==null?new Identifier("minecraft", "textures/block/grass_block_side.png"):sprite;
    }

    public void setSprite(Identifier sprite){
        this.sprite = sprite;
    }
}
