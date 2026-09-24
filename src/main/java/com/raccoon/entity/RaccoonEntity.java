package com.raccoon.entity;

import com.raccoon.entity.ai.RaccoonBegGoal;
import com.raccoon.entity.ai.RaccoonWashGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RaccoonEntity extends TamableAnimal {
    private static final EntityDataAccessor<Boolean> STANDING = SynchedEntityData.defineId(RaccoonEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> WASHING = SynchedEntityData.defineId(RaccoonEntity.class, EntityDataSerializers.BOOLEAN);

    public RaccoonEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(STANDING, false);
        builder.define(WASHING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Standing", this.isStanding());
        tag.putBoolean("Washing", this.isWashing());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setStanding(tag.getBoolean("Standing"));
        this.setWashing(tag.getBoolean("Washing"));
    }

    public boolean isStanding() {
        return this.entityData.get(STANDING);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(STANDING, standing);
    }

    public boolean isWashing() {
        return this.entityData.get(WASHING);
    }

    public void setWashing(boolean washing) {
        this.entityData.set(WASHING, washing);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new RaccoonBegGoal(this, 8.0F));
        this.goalSelector.addGoal(4, new RaccoonWashGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.15, 10.0F, 2.0F));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(8, new TemptGoal(this, 1.1, Ingredient.of(
                Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.BREAD, Items.COD, Items.SALMON, Items.APPLE
        ), false));
        this.goalSelector.addGoal(9, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(12, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Chicken.class, false, target -> !this.isTame()));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Rabbit.class, false, target -> !this.isTame()));
    }

    public boolean isFavoriteFood(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.SWEET_BERRIES
                || item == Items.GLOW_BERRIES
                || item == Items.BREAD
                || item == Items.APPLE
                || item == Items.COD
                || item == Items.SALMON
                || item == Items.COOKED_COD
                || item == Items.COOKED_SALMON
                || item == Items.EGG
                || item == Items.COOKIE
                || item == Items.CHICKEN
                || item == Items.COOKED_CHICKEN
                || item == Items.RABBIT
                || item == Items.COOKED_RABBIT
                || item == Items.GOLDEN_CARROT;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return this.isFavoriteFood(stack);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.level().isClientSide) {
            boolean consume = this.isOwnedBy(player) || this.isTame() || (this.isFavoriteFood(stack) && !this.isTame());
            return consume ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        if (this.isTame()) {
            if (this.isOwnedBy(player)) {
                if (this.isFavoriteFood(stack) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.heal(4.0F);
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    return InteractionResult.SUCCESS;
                }

                if (this.isFood(stack) && this.getAge() == 0 && this.canFallInLove()) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    this.setInLove(player);
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                    return InteractionResult.SUCCESS;
                }

                this.setOrderedToSit(!this.isOrderedToSit());
                this.setInSittingPose(this.isOrderedToSit());
                this.jumping = false;
                this.getNavigation().stop();
                this.setTarget(null);
                return InteractionResult.SUCCESS;
            }
        } else if (this.isFavoriteFood(stack)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
            if (this.random.nextInt(3) == 0) {
                this.tame(player);
                this.getNavigation().stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.setInSittingPose(true);
                this.level().broadcastEntityEvent(this, (byte) 7);
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6);
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void setTame(boolean tame, boolean applyEffects) {
        super.setTame(tame, applyEffects);
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(tame ? 24.0 : 12.0);
            if (tame) {
                this.setHealth(24.0F);
            }
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        RaccoonEntity baby = RaccoonEntities.RACCOON.get().create(level);
        if (baby != null && this.isTame()) {
            UUID owner = this.getOwnerUUID();
            if (owner != null) {
                baby.setOwnerUUID(owner);
                baby.setTame(true, true);
            }
        }
        return baby;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.FOX_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(net.minecraft.world.damagesource.DamageSource source) {
        return SoundEvents.FOX_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.FOX_DEATH;
    }
}
