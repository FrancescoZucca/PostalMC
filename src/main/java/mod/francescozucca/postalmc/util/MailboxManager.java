package mod.francescozucca.postalmc.util;

import mod.francescozucca.postalmc.block.entity.MailboxBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;

import java.util.*;

public class MailboxManager extends PersistentState {

    private final ArrayList<MailboxDestination> mailboxes = new ArrayList<>();

    public MailboxManager(){
        super();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        final int[] i = {nbt.getKeys().size()};
        mailboxes.forEach((md)->{
            NbtCompound box = new NbtCompound();
            box.putString("name", md.getName()==null?"":md.getName());
            box.putInt("x", md.getPos().getX());
            box.putInt("y", md.getPos().getY());
            box.putInt("z", md.getPos().getZ());
            box.putString("texture", md.getSprite().toString());
            nbt.put(String.valueOf(i[0]), box);
            i[0]++;
        });
        return nbt;
    }

    public static PersistentState fromNbt(NbtCompound tag){
        MailboxManager mman = new MailboxManager();
        mman.mailboxes.clear();
        tag.getKeys().forEach((s)->{
            NbtCompound box = tag.getCompound(s);
            MailboxDestination md = new MailboxDestination(new BlockPos(box.getInt("x"), box.getInt("y"), box.getInt("z")), Objects.equals(box.getString("name"), "") ?null:box.getString("name"));
            Identifier id = Identifier.tryParse(box.getString("texture"));
            md.setSprite(id==null?new Identifier("minecraft", "textures/block/grass_block_side.png"):id);
            mman.mailboxes.add(md);
        });

        return mman;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public void addMailbox(MailboxBlockEntity mbe){
        mailboxes.add(new MailboxDestination(mbe));
    }

    public void removeMailbox(MailboxBlockEntity mbe){
        mailboxes.removeIf(md1 -> arePosEqual(md1.getPos(), mbe.getPos()));
    }

    public void removeMailbox(MailboxDestination md){
        mailboxes.removeIf(md1 -> arePosEqual(md1.getPos(), md.getPos()));
    }

    public ArrayList<MailboxDestination> getMailboxes(){
        return new ArrayList<>(mailboxes);
    }

    public boolean sendToMailbox(MailboxBlockEntity mbe, ItemStack item){
        if(mbe.getItems().contains(ItemStack.EMPTY)){
            for (int i = 0; i < mbe.getItems().size(); i++) {
                if(mbe.getStack(i)==ItemStack.EMPTY){
                    mbe.setStack(i, item);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean arePosEqual(BlockPos pos1, BlockPos pos2){
        return (pos1.getX() == pos2.getX()) && (pos2.getY() == pos1.getY()) && (pos1.getZ() == pos2.getZ());
    }
}
