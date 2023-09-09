package com.divinity.hmedia.rgrmechawarden.utils;

import com.google.common.collect.Sets;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MechaWardenUtils {

    private static final float GRAVITY_STRENGTH = 0.075f;

    public static boolean hasItemEitherHands(Player player, Item item) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == item || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == item;
    }

    public static void scanHitWithFollowup(Entity shooter, double range, boolean hitFluids, Consumer<HitResult> followup) {
        Predicate<Entity> filter = e -> true;
        Vec3 eyePos = shooter.getEyePosition(1);
        Vec3 lookDirection = shooter.getLookAngle();
        Vec3 traceVec = eyePos.add(lookDirection.scale(range));
        ClipContext.Fluid mode;
        if (hitFluids) {
            mode = ClipContext.Fluid.ANY;
        } else {
            mode = ClipContext.Fluid.NONE;
        }
        HitResult result = shooter.level().clip(new ClipContext(eyePos, traceVec, ClipContext.Block.COLLIDER, mode, shooter));
        Vec3 resultVec = traceVec;
        if (result.getType() != HitResult.Type.MISS) {
            resultVec = result.getLocation();
        }
        AABB box = new AABB(eyePos, resultVec);
        HitResult projectileResult = ProjectileUtil.getEntityHitResult(shooter.level(), shooter, eyePos, resultVec, box, filter);
        if (projectileResult != null) {
            result = projectileResult;
        }
        followup.accept(result);
    }

    public static void amplifyCurrentEffect(ServerPlayer player, boolean isDivision, MobEffect... effects) {
        Arrays.stream(effects).forEach(effect -> amplifyCurrentEffect(player, isDivision, effect));
    }

    public static void amplifyCurrentEffect(ServerPlayer player, boolean isDivision, MobEffect effect) {
        MobEffectInstance instance = player.getEffect(effect);
        if (instance != null) {
            amplifyCurrentEffect(player, isDivision ? instance.getAmplifier() / 2 : instance.getAmplifier() * 2, effect);
        }
    }

    public static void amplifyCurrentEffect(ServerPlayer player, int amplificationIncreaseFactor, MobEffect... effects) {
        Arrays.stream(effects).forEach(effect -> amplifyCurrentEffect(player, amplificationIncreaseFactor, effect));
    }

    public static void amplifyCurrentEffect(ServerPlayer player, int amplificationIncreaseFactor, MobEffect effect) {
        MobEffectInstance instance = player.getEffect(effect);
        if (instance != null) {
            player.addEffect(new MobEffectInstance(effect, instance.getDuration(), instance.getAmplifier() + amplificationIncreaseFactor, false, false, false));
        }
    }

    public static <T extends QuestGoal> void addToGenericQuestGoal(ServerPlayer player, Class<T> clazz) {
        QuestHolderAttacher.checkAllGoals(player, goal -> {
            if (goal.getClass() == clazz) {
                goal.addProgress(1);
                return true;
            }
            return false;
        });
    }

    public static void fancyExplosion(Vec3 center, Level level, int radius, BlockState afterMathState) {
        Set<BlockPos> set = Sets.newHashSet();
        ExplosionDamageCalculator damageCalculator = new ExplosionDamageCalculator();
        for(int j = 0; j < 20; ++j) {
            for(int k = 0; k < 20; ++k) {
                for(int l = 0; l < 20; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d0 = (float)j / 15.0F * 2.0F - 1.0F;
                        double d1 = (float)k / 15.0F * 2.0F - 1.0F;
                        double d2 = (float)l / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f = radius * (0.7F + level.random.nextFloat() * 0.6F);
                        double d4 = center.x;
                        double d6 = center.y;
                        double d8 = center.z;
                        for(; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos((int) d4, (int) d6, (int) d8);
                            BlockState blockstate = level.getBlockState(blockpos);
                            FluidState fluidstate = level.getFluidState(blockpos);
                            if (!level.isInWorldBounds(blockpos)) {
                                break;
                            }
                            Optional<Float> optional = damageCalculator.getBlockExplosionResistance(null, level, blockpos, blockstate, fluidstate);
                            if (optional.isPresent()) {
                                f -= (optional.get() + 0.3F) * 0.3F;
                            }
                            if (f > 0.0F && damageCalculator.shouldBlockExplode(null, level, blockpos, blockstate, f)) {
                                set.add(blockpos);
                            }
                            d4 += d0 * (double)0.3F;
                            d6 += d1 * (double)0.3F;
                            d8 += d2 * (double)0.3F;
                        }
                    }
                }
            }
        }
        for (BlockPos pos : set) {
            BlockState state = level.getBlockState(pos);
            if (state.isAir())
                continue;
            level.setBlockAndUpdate(pos, afterMathState);
        }
    }

    public static void pullEntityToPoint(LivingEntity livingEntity, Vec3 to) {
        pullEntityToPoint(livingEntity, to, 1);
    }

    public static void pullEntityToPoint(LivingEntity livingEntity, Vec3 to, float strength) {
        Vec3 pullDirection = to.subtract(livingEntity.position());
        double gravity = interpolate(0, GRAVITY_STRENGTH, strength);
        Vec3 pull = pullDirection.normalize().scale(gravity);
        livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().add(pull));
        livingEntity.hurtMarked = true;
    }

    public static void pullEntitiesToPoint(List<LivingEntity> list, Vec3 to) {
        for (LivingEntity living : list) {
            pullEntityToPoint(living, to);
        }
    }

    public static void pullEntitiesToPoint(List<LivingEntity> list, Vec3 to, float strength) {
        for (LivingEntity living : list) {
            pullEntityToPoint(living, to, strength);
        }
    }

    public static EntityHitResult rayTraceEntities(Level level, Entity origin, float range, Predicate<Entity> filter) {
        Vec3 look = origin.getViewVector(0);
        Vec3 startVec = origin.getEyePosition(0);
        Vec3 endVec = startVec.add(look.x * range, look.y * range, look.z * range);
        AABB box = new AABB(startVec, endVec);
        return rayTraceEntities(level,origin,startVec,endVec,box,filter);
    }

    public static EntityHitResult rayTraceEntities(Level level, @Nullable Entity origin, Vec3 startVec, Vec3 endVec, AABB boundingBox, Predicate<Entity> filter) {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;
        for (Entity entity1 : level.getEntities(origin, boundingBox, filter)) {
            if (entity1.isSpectator()) {
                continue;
            }
            AABB aabb = entity1.getBoundingBox();
            if (aabb.getSize() < 0.3) {
                aabb = aabb.inflate(0.3);
            }
            Optional<Vec3> optional = aabb.clip(startVec, endVec);
            if (optional.isPresent()) {
                double d1 = startVec.distanceToSqr(optional.get());
                if (d1 < d0) {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }
        return entity == null ? null : new EntityHitResult(entity);
    }

    public static float interpolate(float start, float end, float scale) {
        return ((end - start) * scale) + start;
    }

    public static Direction findHorizontalDirection(BlockPos pos, Vec3 vector) {
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 direction = vector.subtract(center);
        boolean eastWest = (Math.abs(direction.x()) > Math.abs(direction.z()));
        if (eastWest) {
            if (direction.x >= 0) {
                return Direction.EAST;
            } else {
                return Direction.WEST;
            }
        } else {
            if (direction.z >= 0) {
                return Direction.SOUTH;
            } else {
                return Direction.NORTH;
            }
        }
    }

    @Nullable
    public static Direction getDirectionFromTwoPoints(Vec3 pFrom, Vec3 pTo) {
        AABB instance = new AABB(pFrom, pTo).inflate(5);
        double[] adouble = new double[]{1.0D};
        double d0 = pTo.x - pFrom.x;
        double d1 = pTo.y - pFrom.y;
        double d2 = pTo.z - pFrom.z;
        return getHoirzontalDirection(instance, pFrom, adouble, null, d0, d1, d2);
    }

    @Nullable
    public static Direction getHoirzontalDirection(AABB pAabb, Vec3 pStart, double[] pMinDistance, @Nullable Direction pFacing, double pDeltaX, double pDeltaY, double pDeltaZ) {
        if (pDeltaX > 1.0E-7D) {
            pFacing = clipPoint(pMinDistance, pFacing, pDeltaX, pDeltaY, pDeltaZ, pAabb.minX, pAabb.minY, pAabb.maxY, pAabb.minZ, pAabb.maxZ, Direction.WEST, pStart.x, pStart.y, pStart.z);
        }
        else if (pDeltaX < -1.0E-7D) {
            pFacing = clipPoint(pMinDistance, pFacing, pDeltaX, pDeltaY, pDeltaZ, pAabb.maxX, pAabb.minY, pAabb.maxY, pAabb.minZ, pAabb.maxZ, Direction.EAST, pStart.x, pStart.y, pStart.z);
        }
        if (pDeltaZ > 1.0E-7D) {
            pFacing = clipPoint(pMinDistance, pFacing, pDeltaZ, pDeltaX, pDeltaY, pAabb.minZ, pAabb.minX, pAabb.maxX, pAabb.minY, pAabb.maxY, Direction.NORTH, pStart.z, pStart.x, pStart.y);
        }
        else if (pDeltaZ < -1.0E-7D) {
            pFacing = clipPoint(pMinDistance, pFacing, pDeltaZ, pDeltaX, pDeltaY, pAabb.maxZ, pAabb.minX, pAabb.maxX, pAabb.minY, pAabb.maxY, Direction.SOUTH, pStart.z, pStart.x, pStart.y);
        }
        return pFacing;
    }

    @Nullable
    private static Direction clipPoint(double[] pMinDistance, @Nullable Direction pPrevDirection, double pDistanceSide, double pDistanceOtherA, double pDistanceOtherB, double pMinSide, double pMinOtherA, double pMaxOtherA, double pMinOtherB, double pMaxOtherB, Direction pHitSide, double pStartSide, double pStartOtherA, double pStartOtherB) {
        double d0 = (pMinSide - pStartSide) / pDistanceSide;
        double d1 = pStartOtherA + d0 * pDistanceOtherA;
        double d2 = pStartOtherB + d0 * pDistanceOtherB;
        if (pMinOtherA - 1.0E-7D < d1 && d1 < pMaxOtherA + 1.0E-7D && pMinOtherB - 1.0E-7D < d2 && d2 < pMaxOtherB + 1.0E-7D) {
            pMinDistance[0] = d0;
            return pHitSide;
        }
        else {
            return pPrevDirection;
        }
    }

    public static void rayTraceEntitiesWithAction(Level level, Entity origin, float range, Consumer<Entity> action, boolean shouldStopOnFirstHit) {
        Vec3 look = origin.getViewVector(0);
        Vec3 startVec = origin.getEyePosition(0);
        Vec3 endVec = startVec.add(look.x * range, look.y * range, look.z * range);
        AABB box = new AABB(startVec, endVec);
        rayTraceEntitiesWithAction(level, origin, startVec, endVec, box, action, shouldStopOnFirstHit);
    }

    public static void rayTraceEntitiesWithAction(Level level, @Nullable Entity origin, Vec3 startVec, Vec3 endVec, AABB boundingBox, Consumer<Entity> action, boolean shouldStopOnFirstHit) {
        for (Entity entity : level.getEntities(origin, boundingBox, p -> p instanceof LivingEntity && p != origin)) {
            if (entity.isSpectator()) {
                continue;
            }
            AABB aabb = entity.getBoundingBox();
            aabb = aabb.inflate(2);
            Optional<Vec3> optional = aabb.clip(startVec, endVec);
            if (optional.isPresent()) {
                action.accept(entity);
                if (shouldStopOnFirstHit) break;
            }
        }
    }

    public static BlockHitResult blockTrace(LivingEntity livingEntity, ClipContext.Fluid rayTraceFluid, int range, boolean downOrFace) {
        Level level = livingEntity.level();
        ClipContext context;
        Vec3 start = new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getEyeHeight(), livingEntity.getZ());
        Vec3 look;

        if (!downOrFace) {
            look = livingEntity.getLookAngle();
        } else {
            look = new Vec3(0, -range, 0);
        }
        Vec3 end = new Vec3(livingEntity.getX() + look.x * (double) range, livingEntity.getY() + livingEntity.getEyeHeight() + look.y * (double) range, livingEntity.getZ() + look.z * (double) range);
        context = new ClipContext(start, end, ClipContext.Block.COLLIDER, rayTraceFluid, livingEntity);
        return level.clip(context);
    }

    /**
     * Returns a list of entities (targets) from a relative entity within the specified x, y, and z bounds.
     */
    public static <T extends LivingEntity> List<T> getEntitiesInRange(LivingEntity relativeEntity, Class<T> targets, double xBound, double yBound, double zBound, Predicate<T> filter) {
        return relativeEntity.level().getEntitiesOfClass(targets,
                        new AABB(relativeEntity.getX() - xBound, relativeEntity.getY() - yBound, relativeEntity.getZ() - zBound,
                                relativeEntity.getX() + xBound, relativeEntity.getY() + yBound, relativeEntity.getZ() + zBound))
                .stream().sorted(getEntityComparator(relativeEntity)).filter(filter).collect(Collectors.toList());
    }

    /**
     * Returns a comparator which compares entities' distances to a given LivingEntity
     */
    public static Comparator<Entity> getEntityComparator(LivingEntity other) {
        return Comparator.comparing(entity -> entity.distanceToSqr(other.getX(), other.getY(), other.getZ()));
    }
}
