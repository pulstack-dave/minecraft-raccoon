package com.raccoon.entity;

import com.raccoon.entity.ai.RaccoonBegGoal;
import com.raccoon.entity.ai.RaccoonWashGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RaccoonEntity extends TameableEntity {
    private static final TrackedData<Boolean> STANDING = DataTracker.registerData(RaccoonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> WASHING = DataTracker.registerData(RaccoonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public RaccoonEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.setTamed(false);
    }

    public static DefaultAttributeContainer.Builder createRaccoonAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(STANDING, false);
        this.dataTracker.startTracking(WASHING, false);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Standing", this.isStanding());
        nbt.putBoolean("Washing", this.isWashing());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setStanding(nbt.getBoolean("Standing"));
        this.setWashing(nbt.getBoolean("Washing"));
    }

    public boolean isStanding() {
        return this.dataTracker.get(STANDING);
    }

    public void setStanding(boolean standing) {
        this.dataTracker.set(STANDING, standing);
    }

    public boolean isWashing() {
        return this.dataTracker.get(WASHING);
    }

    public void setWashing(boolean washing) {
        this.dataTracker.set(WASHING, washing);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(3, new RaccoonBegGoal(this, 8.0F));
        this.goalSelector.add(4, new RaccoonWashGoal(this));
        this.goalSelector.add(5, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.15, 10.0F, 2.0F, false));
        this.goalSelector.add(7, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(8, new TemptGoal(this, 1.1, Ingredient.ofItems(
                Items.SWEET_BERRIES, Items.GLOW_BERRIES, Items.BREAD, Items.COD, Items.SALMON, Items.APPLE
        ), false));
        this.goalSelector.add(9, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(10, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(11, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(12, new LookAroundGoal(this));

        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, new RevengeGoal(this).setGroupRevenge());
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, ChickenEntity.class, false, (target) -> !this.isTamed()));
        this.targetSelector.add(5, new ActiveTargetGoal<>(this, RabbitEntity.class, false, (target) -> !this.isTamed()));
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
    public net.minecraft.world.EntityView method_48926() {
        return super.getWorld();
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return this.isFavoriteFood(stack);
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (this.getWorld().isClient) {
            boolean consume = this.isOwner(player) || this.isTamed() || (this.isFavoriteFood(itemStack) && !this.isTamed());
            return consume ? ActionResult.CONSUME : ActionResult.PASS;
        }

        if (this.isTamed()) {
            if (this.isOwner(player)) {
                if (this.isFavoriteFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    this.heal(4.0F);
                    this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                    this.getWorld().sendEntityStatus(this, (byte) 7); // Heart particles
                    return ActionResult.SUCCESS;
                }

                if (this.isBreedingItem(itemStack) && this.getBreedingAge() == 0 && this.canEat()) {
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    this.lovePlayer(player);
                    this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);
                    this.getWorld().sendEntityStatus(this, (byte) 7);
                    return ActionResult.SUCCESS;
                }

                // Toggle sitting / standing / following
                ActionResult result = super.interactMob(player, hand);
                if (!result.isAccepted() || this.isBaby()) {
                    this.setSitting(!this.isSitting());
                    this.setInSittingPose(!this.isInSittingPose());
                    this.jumping = false;
                    this.navigation.stop();
                    this.setTarget(null);
                    return ActionResult.SUCCESS;
                }
                return result;
            }
        } else if (this.isFavoriteFood(itemStack)) {
            if (!player.getAbilities().creativeMode) {
                itemStack.decrement(1);
            }

            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 1.0F, 1.0F);

            if (this.random.nextInt(3) == 0) {
                this.setOwner(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setSitting(true);
                this.setInSittingPose(true);
                this.getWorld().sendEntityStatus(this, (byte) 7); // Hearts
            } else {
                this.getWorld().sendEntityStatus(this, (byte) 6); // Smoke
            }
            return ActionResult.SUCCESS;
        }

        return super.interactMob(player, hand);
    }

    @Override
    public void setTamed(boolean tamed) {
        super.setTamed(tamed);
        if (tamed) {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(24.0);
            this.setHealth(24.0F);
        } else {
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(12.0);
        }
    }

    @Nullable
    @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        RaccoonEntity baby = RaccoonEntities.RACCOON.create(world);
        if (baby != null && this.isTamed()) {
            baby.setOwnerUuid(this.getOwnerUuid());
            baby.setTamed(true);
        }
        return baby;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_FOX_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_FOX_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_FOX_DEATH;
    }
}
