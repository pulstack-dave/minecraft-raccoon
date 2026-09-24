package com.raccoon.entity.ai;

import com.raccoon.entity.RaccoonEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class RaccoonBegGoal extends Goal {
    private final RaccoonEntity raccoon;
    private final ServerLevel level;
    private final float distance;
    private Player beggingPlayer;
    private int timer;
    private final TargetingConditions validPlayerPredicate;

    public RaccoonBegGoal(RaccoonEntity raccoon, float distance) {
        this.raccoon = raccoon;
        this.level = getServerLevel(raccoon);
        this.distance = distance;
        this.validPlayerPredicate = TargetingConditions.forNonCombat().range(distance);
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.raccoon.isInSittingPose()) {
            return false;
        }
        this.beggingPlayer = this.level.getNearestPlayer(this.validPlayerPredicate, this.raccoon);
        return this.beggingPlayer != null && this.isHoldingFood(this.beggingPlayer);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.beggingPlayer == null || !this.beggingPlayer.isAlive()) {
            return false;
        }
        if (this.raccoon.distanceToSqr(this.beggingPlayer) > (double) (this.distance * this.distance)) {
            return false;
        }
        return this.timer > 0 && this.isHoldingFood(this.beggingPlayer);
    }

    @Override
    public void start() {
        this.raccoon.setStanding(true);
        this.timer = 40 + this.raccoon.getRandom().nextInt(40);
    }

    @Override
    public void stop() {
        this.raccoon.setStanding(false);
        this.beggingPlayer = null;
    }

    @Override
    public void tick() {
        this.raccoon.getLookControl().setLookAt(this.beggingPlayer.getX(), this.beggingPlayer.getEyeY(), this.beggingPlayer.getZ(), 10.0F, (float) this.raccoon.getMaxHeadXRot());
        --this.timer;
    }

    private boolean isHoldingFood(Player player) {
        return this.raccoon.isFavoriteFood(player.getMainHandItem()) || this.raccoon.isFavoriteFood(player.getOffhandItem());
    }
}
