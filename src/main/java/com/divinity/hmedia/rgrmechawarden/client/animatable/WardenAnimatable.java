package com.divinity.hmedia.rgrmechawarden.client.animatable;

import com.divinity.hmedia.rgrmechawarden.cap.SkulkHolderAttacher;
import dev._100media.hundredmediageckolib.client.animatable.MotionAttackAnimatable;
import net.minecraft.client.player.AbstractClientPlayer;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class WardenAnimatable extends MotionAttackAnimatable {

    protected RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack");
    protected RawAnimation CROUCH = RawAnimation.begin().thenLoop("crouch");
    protected RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    protected RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    protected RawAnimation SIT = RawAnimation.begin().thenLoop("sit");
    protected RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected RawAnimation HOVERBOARD_ANIM = RawAnimation.begin().then("hoverboard on", Animation.LoopType.PLAY_ONCE).thenLoop("hoverboard");

    public WardenAnimatable() {}

    @Override
    protected PlayState attackAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
        AnimationController<?> controller = state.getController();
        if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
            var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
            controller.transitionLength(0);
            if (player.swingTime > 0 && holder != null && !holder.isMechaBoard()) {
                controller.setAnimation(ATTACK);
                return PlayState.CONTINUE;
            }
            motionAnimationEvent(state);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected PlayState motionAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
        AnimationController<?> controller = state.getController();
        if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
            var holder = SkulkHolderAttacher.getSkulkHolderUnwrap(player);
            controller.transitionLength(0);
            if (holder != null && holder.isMechaBoard()) {
                var currentAnim = controller.getCurrentAnimation();
                var actualCurrentAnim = currentAnim.animation();
                if (currentAnim != null && actualCurrentAnim != null &&
                        !actualCurrentAnim.name().toLowerCase().contains("hoverboard")) {
                    controller.setAnimation(HOVERBOARD_ANIM);
                }
            }
            else {
                if (player.getVehicle() != null) {
                    controller.setAnimation(SIT);
                }
                else if (player.isShiftKeyDown()) {
                    controller.setAnimation(CROUCH);
                }
                else if (state.isMoving()) {
                    controller.setAnimation(player.isSprinting() && !player.isCrouching() ? RUN : WALK);
                }
                else {
                    controller.setAnimation(IDLE);
                }
            }
        }
        return PlayState.CONTINUE;
    }

    public WardenAnimatable attackAnim(RawAnimation other) {
        ATTACK = other;
        return this;
    }

    public WardenAnimatable crouchAnim(RawAnimation other) {
        CROUCH = other;
        return this;
    }

    public WardenAnimatable runAnim(RawAnimation other) {
        RUN = other;
        return this;
    }

    public WardenAnimatable walkAnim(RawAnimation other) {
        WALK = other;
        return this;
    }

    public WardenAnimatable idleAnim(RawAnimation other) {
        IDLE = other;
        return this;
    }

    public WardenAnimatable sitAnim(RawAnimation other) {
        SIT = other;
        return this;
    }

    public WardenAnimatable hoverboardAnim(RawAnimation other) {
        HOVERBOARD_ANIM = other;
        return this;
    }
}
