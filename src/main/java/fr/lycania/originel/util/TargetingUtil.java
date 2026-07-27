package fr.lycania.originel.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public final class TargetingUtil {

    private TargetingUtil() {
    }

    /** Finds the living entity closest to the center of the player's crosshair, within range, ignoring blocks. */
    public static Optional<LivingEntity> getLookedAtEntity(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        Vec3 endPos = eyePos.add(look.scale(range));
        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0);

        LivingEntity closest = null;
        double closestDistSq = range * range;
        for (Entity entity : player.level().getEntities(player, searchBox, e -> e instanceof LivingEntity && e.isAlive())) {
            AABB box = entity.getBoundingBox().inflate(0.3);
            Optional<Vec3> hit = box.clip(eyePos, endPos);
            if (hit.isPresent()) {
                double distSq = eyePos.distanceToSqr(hit.get());
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    closest = (LivingEntity) entity;
                }
            }
        }
        return Optional.ofNullable(closest);
    }
}
