package com.raccoon.entity.ai;

import com.raccoon.entity.RaccoonEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.EnumSet;

public class RaccoonBegGoal extends Goal {
    private final RaccoonEntity raccoon;
    private final World world;
    private final float distance;
    private PlayerEntity beggingPlayer;
    private int timer;
    private final TargetPredicate validPlayerPredicate;

    public RaccoonBegGoal(RaccoonEntity raccoon, float distance) {
        this.raccoon = raccoon;
        this.world = raccoon.getWorld();
        this.distance = distance;
        this.validPlayerPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(distance);
        this.setControls(EnumSet.of(Control.LOOK, Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (this.raccoon.isInSittingPose()) {
            return false;
        }
        this.beggingPlayer = this.world.getClosestPlayer(this.validPlayerPredicate, this.raccoon);
        return this.beggingPlayer != null && this.isHoldingFood(this.beggingPlayer);
    }

    @Override
    public boolean shouldContinue() {
        if (this.beggingPlayer == null || !this.beggingPlayer.isAlive()) {
            return false;
        }
        if (this.raccoon.squaredDistanceTo(this.beggingPlayer) > (double)(this.distance * this.distance)) {
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
        this.raccoon.getLookControl().lookAt(this.beggingPlayer.getX(), this.beggingPlayer.getEyeY(), this.beggingPlayer.getZ(), 10.0F, (float)this.raccoon.getMaxLookPitchChange());
        --this.timer;
    }

    private boolean isHoldingFood(PlayerEntity player) {
        for (ItemStack stack : player.getHandItems()) {
            if (this.raccoon.isFavoriteFood(stack)) {
                return true;
            }
        }
        return false;
    }
}
