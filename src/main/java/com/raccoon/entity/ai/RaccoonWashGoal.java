package com.raccoon.entity.ai;

import com.raccoon.entity.RaccoonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.EnumSet;

public class RaccoonWashGoal extends Goal {
    private final RaccoonEntity raccoon;
    private final Level level;
    private int washTimer;
    private BlockPos targetWaterPos;

    public RaccoonWashGoal(RaccoonEntity raccoon) {
        this.raccoon = raccoon;
        this.level = raccoon.level();
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.raccoon.isInSittingPose() || this.raccoon.isBaby()) {
            return false;
        }
        if (this.raccoon.getRandom().nextInt(120) != 0) {
            return false;
        }
        BlockPos raccoonPos = this.raccoon.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(raccoonPos.offset(-3, -1, -3), raccoonPos.offset(3, 1, 3))) {
            if (this.level.getBlockState(pos).is(Blocks.WATER) || this.level.getBlockState(pos).is(Blocks.WATER_CAULDRON)) {
                this.targetWaterPos = pos.immutable();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.washTimer > 0 && this.targetWaterPos != null;
    }

    @Override
    public void start() {
        this.washTimer = 60 + this.raccoon.getRandom().nextInt(40);
        this.raccoon.setWashing(true);
        if (this.targetWaterPos != null) {
            this.raccoon.getNavigation().moveTo(this.targetWaterPos.getX() + 0.5, this.targetWaterPos.getY() + 1.0, this.targetWaterPos.getZ() + 0.5, 1.1);
        }
    }

    @Override
    public void stop() {
        this.raccoon.setWashing(false);
        this.targetWaterPos = null;
    }

    @Override
    public void tick() {
        if (this.targetWaterPos != null) {
            this.raccoon.getLookControl().setLookAt(this.targetWaterPos.getX() + 0.5, this.targetWaterPos.getY() + 0.5, this.targetWaterPos.getZ() + 0.5);
            if (this.raccoon.distanceToSqr(this.targetWaterPos.getX() + 0.5, this.targetWaterPos.getY() + 0.5, this.targetWaterPos.getZ() + 0.5) < 4.0) {
                if (this.level.isClientSide) {
                    this.level.addParticle(ParticleTypes.SPLASH,
                            this.targetWaterPos.getX() + 0.5 + (this.level.random.nextDouble() - 0.5) * 0.4,
                            this.targetWaterPos.getY() + 0.8,
                            this.targetWaterPos.getZ() + 0.5 + (this.level.random.nextDouble() - 0.5) * 0.4,
                            0.0, 0.1, 0.0);
                }
                if (this.washTimer % 15 == 0) {
                    this.level.playSound(null, this.raccoon.blockPosition(), SoundEvents.GENERIC_SWIM, SoundSource.NEUTRAL, 0.4F, 1.2F);
                }
            }
        }
        --this.washTimer;
    }
}
