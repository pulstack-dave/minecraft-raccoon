package com.raccoon.entity.ai;

import com.raccoon.entity.RaccoonEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class RaccoonWashGoal extends Goal {
    private final RaccoonEntity raccoon;
    private final World world;
    private int washTimer;
    private BlockPos targetWaterPos;

    public RaccoonWashGoal(RaccoonEntity raccoon) {
        this.raccoon = raccoon;
        this.world = raccoon.getWorld();
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (this.raccoon.isInSittingPose() || this.raccoon.isBaby()) {
            return false;
        }
        if (this.raccoon.getRandom().nextInt(120) != 0) {
            return false;
        }
        BlockPos raccoonPos = this.raccoon.getBlockPos();
        for (BlockPos pos : BlockPos.iterate(raccoonPos.add(-3, -1, -3), raccoonPos.add(3, 1, 3))) {
            if (this.world.getBlockState(pos).isOf(Blocks.WATER) || this.world.getBlockState(pos).isOf(Blocks.CAULDRON)) {
                this.targetWaterPos = pos.toImmutable();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return this.washTimer > 0 && this.targetWaterPos != null;
    }

    @Override
    public void start() {
        this.washTimer = 60 + this.raccoon.getRandom().nextInt(40);
        this.raccoon.setWashing(true);
        if (this.targetWaterPos != null) {
            this.raccoon.getNavigation().startMovingTo(this.targetWaterPos.getX() + 0.5, this.targetWaterPos.getY() + 1.0, this.targetWaterPos.getZ() + 0.5, 1.1);
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
            this.raccoon.getLookControl().lookAt(this.targetWaterPos.getX() + 0.5, this.targetWaterPos.getY() + 0.5, this.targetWaterPos.getZ() + 0.5);
            if (this.raccoon.squaredDistanceTo(this.targetWaterPos.getX() + 0.5, this.targetWaterPos.getY() + 0.5, this.targetWaterPos.getZ() + 0.5) < 4.0) {
                // Splash particles
                if (this.world.isClient) {
                    this.world.addParticle(ParticleTypes.SPLASH,
                            this.targetWaterPos.getX() + 0.5 + (this.world.random.nextDouble() - 0.5) * 0.4,
                            this.targetWaterPos.getY() + 0.8,
                            this.targetWaterPos.getZ() + 0.5 + (this.world.random.nextDouble() - 0.5) * 0.4,
                            0.0, 0.1, 0.0);
                }
                if (this.washTimer % 15 == 0) {
                    this.world.playSound(null, this.raccoon.getBlockPos(), SoundEvents.ENTITY_GENERIC_SWIM, SoundCategory.NEUTRAL, 0.4F, 1.2F);
                }
            }
        }
        --this.washTimer;
    }
}
