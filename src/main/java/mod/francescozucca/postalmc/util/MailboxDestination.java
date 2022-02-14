package mod.francescozucca.postalmc.util;

import mod.francescozucca.postalmc.block.entity.MailboxBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class MailboxDestination implements IMailbox{

    private final BlockPos pos;
    private final String name;
    private final MailboxBlockEntity entity;
    private Identifier sprite;
    private final RegistryKey<World> dimension;

    public MailboxDestination(BlockPos pos, String name, int dim){
        this.pos = pos;
        this.name = name;
        this.entity = null;
        this.sprite = new Identifier("minecraft", "textures/block/grass_block_side.png");
        switch (dim){
            case 0 -> dimension = World.OVERWORLD;
            case 1 -> dimension = World.END;
            case -1 -> dimension = World.NETHER;
            default -> dimension = null;
        }
    }

    public MailboxDestination(MailboxBlockEntity be){
        entity = be;
        pos = be.getPos();
        name = be.getName();
        sprite = be.getSprite();
        dimension = be.getDimension();
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

    @Override
    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public void setSprite(Identifier sprite){
        this.sprite = sprite;
    }
}
