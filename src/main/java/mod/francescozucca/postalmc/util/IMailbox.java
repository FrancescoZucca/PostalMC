package mod.francescozucca.postalmc.util;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.Iterator;

public interface IMailbox {
    BlockPos getPos();
    String getName();
    Identifier getSprite();
    RegistryKey<World> getDimension();

    static int calculateDistance(BlockPos pos1, BlockPos pos2){
        return (int)Math.ceil(pos1.getManhattanDistance(pos2));
    }

    static int calculateCost(IMailbox m1, BlockPos pos, RegistryKey<World> registryKey){
        return Math.min(((calculateDistance(m1.getPos(), pos)/75)+1)*(m1.getDimension()==registryKey?1:5), 64*3);
    }

    static boolean removeStampsFromPlayer(PlayerInventory inv, int stamps){
        int i = 0;
        for(Iterator<ItemStack> iter = inv.main.iterator();iter.hasNext();){
            ItemStack is = iter.next();
            if(is.getItem() instanceof IStamp) {
                i += is.getCount();
            }
        }
        if(i>=stamps){
            for(Iterator<ItemStack> iter = inv.main.iterator();iter.hasNext();){
                ItemStack is = iter.next();
                if(is.getItem() instanceof IStamp) {
                    is.decrement(Math.min(stamps, Math.min(is.getCount(), is.getMaxCount())));
                    i -= Math.min(stamps, Math.min(is.getCount(), is.getMaxCount()));
                }
                if(i==0) break;
            }
            return true;
        }
        return false;
    }
}
