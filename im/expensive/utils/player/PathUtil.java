/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.utils.player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.AirBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

public class PathUtil {
    public Vector3d start;
    public Vector3d end;
    public List<Vector3d> path = new ArrayList<Vector3d>();

    public PathUtil(Vector3d from, Vector3d to) {
        this.start = from;
        this.end = to;
    }

    public Vector3d getStart() {
        return this.start;
    }

    public Vector3d getEnd() {
        return this.end;
    }

    public List<Vector3d> getPath() {
        return this.path;
    }

    public void calculatePath(float step) {
        float totalDistance = (float)this.start.distanceTo(this.end);
        for (float i = 0.0f; i <= totalDistance; i += step) {
            float x = (float)(this.start.x + (double)i * (this.end.x - this.start.x) / (double)totalDistance);
            float z = (float)(this.start.z + (double)i * (this.end.z - this.start.z) / (double)totalDistance);
            float t = i / totalDistance;
            float currentY = (float)(this.start.y * (double)(1.0f - t) + this.end.y * (double)t);
            while (!(Minecraft.getInstance().world.getBlockState(new BlockPos(x, currentY, z)).getBlock() instanceof AirBlock) && !(Minecraft.getInstance().world.getBlockState(new BlockPos(x, currentY, z)).getBlock() instanceof FlowingFluidBlock)) {
                currentY += 1.0f;
            }
            this.path.add(new Vector3d(x, currentY, z));
        }
    }
}

