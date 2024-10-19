/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.events;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;

public class MovingEvent {
    public Vector3d from;
    public Vector3d to;
    public Vector3d motion;
    public Vector3d collisionOffset;
    public boolean toGround;
    public AxisAlignedBB aabbFrom;
    public boolean ignoreHorizontal;
    public boolean ignoreVertical;
    public boolean collidedHorizontal;
    public boolean collidedVertical;

    public MovingEvent(Vector3d from, Vector3d to, Vector3d motion, boolean toGround, boolean isCollidedHorizontal, boolean isCollidedVertical, AxisAlignedBB aabbFrom) {
        this.from = from;
        this.to = to;
        this.motion = motion;
        this.toGround = toGround;
        this.collidedHorizontal = isCollidedHorizontal;
        this.collidedVertical = isCollidedVertical;
        this.aabbFrom = aabbFrom;
    }

    public Vector3d collisionOffset() {
        return this.collisionOffset;
    }

    public Vector3d to() {
        return this.to;
    }

    public Vector3d getFrom() {
        return this.from;
    }

    public Vector3d getTo() {
        return this.to;
    }

    public Vector3d getMotion() {
        return this.motion;
    }

    public Vector3d getCollisionOffset() {
        return this.collisionOffset;
    }

    public boolean isToGround() {
        return this.toGround;
    }

    public AxisAlignedBB getAabbFrom() {
        return this.aabbFrom;
    }

    public boolean isIgnoreHorizontal() {
        return this.ignoreHorizontal;
    }

    public boolean isIgnoreVertical() {
        return this.ignoreVertical;
    }

    public boolean isCollidedHorizontal() {
        return this.collidedHorizontal;
    }

    public boolean isCollidedVertical() {
        return this.collidedVertical;
    }

    public void setFrom(Vector3d from) {
        this.from = from;
    }

    public void setTo(Vector3d to) {
        this.to = to;
    }

    public void setMotion(Vector3d motion) {
        this.motion = motion;
    }

    public void setCollisionOffset(Vector3d collisionOffset) {
        this.collisionOffset = collisionOffset;
    }

    public void setToGround(boolean toGround) {
        this.toGround = toGround;
    }

    public void setAabbFrom(AxisAlignedBB aabbFrom) {
        this.aabbFrom = aabbFrom;
    }

    public void setIgnoreHorizontal(boolean ignoreHorizontal) {
        this.ignoreHorizontal = ignoreHorizontal;
    }

    public void setIgnoreVertical(boolean ignoreVertical) {
        this.ignoreVertical = ignoreVertical;
    }

    public void setCollidedHorizontal(boolean collidedHorizontal) {
        this.collidedHorizontal = collidedHorizontal;
    }

    public void setCollidedVertical(boolean collidedVertical) {
        this.collidedVertical = collidedVertical;
    }
}

