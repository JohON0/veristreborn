/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class PlaceObsidianEvent {
    private Block block;
    private BlockPos pos;

    public Block getBlock() {
        return this.block;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PlaceObsidianEvent)) {
            return false;
        }
        PlaceObsidianEvent other = (PlaceObsidianEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Block this$block = this.getBlock();
        Block other$block = other.getBlock();
        if (this$block == null ? other$block != null : !this$block.equals(other$block)) {
            return false;
        }
        BlockPos this$pos = this.getPos();
        BlockPos other$pos = other.getPos();
        return !(this$pos == null ? other$pos != null : !((Object)this$pos).equals(other$pos));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlaceObsidianEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Block $block = this.getBlock();
        result = result * 59 + ($block == null ? 43 : $block.hashCode());
        BlockPos $pos = this.getPos();
        result = result * 59 + ($pos == null ? 43 : ((Object)$pos).hashCode());
        return result;
    }

    public String toString() {
        return "PlaceObsidianEvent(block=" + this.getBlock() + ", pos=" + this.getPos() + ")";
    }

    public PlaceObsidianEvent(Block block, BlockPos pos) {
        this.block = block;
        this.pos = pos;
    }
}

