package com.raccoon.client.model;

import com.raccoon.entity.RaccoonEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class RaccoonEntityModel<T extends RaccoonEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;

    public RaccoonEntityModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Head at pivot (0.0F, 15.0F, -3.0F)
        ModelPartData headData = modelPartData.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-3.0F, -2.5F, -4.0F, 6.0F, 5.0F, 5.0F)
                        .uv(0, 11).cuboid(-2.0F, 0.5F, -6.0F, 4.0F, 2.0F, 2.0F),
                ModelTransform.pivot(0.0F, 15.0F, -3.0F));

        // Ears on head
        headData.addChild("right_ear",
                ModelPartBuilder.create().uv(15, 0).cuboid(-2.5F, -4.5F, -2.0F, 2.0F, 2.0F, 1.0F),
                ModelTransform.NONE);
        headData.addChild("left_ear",
                ModelPartBuilder.create().uv(21, 0).cuboid(0.5F, -4.5F, -2.0F, 2.0F, 2.0F, 1.0F),
                ModelTransform.NONE);

        // Body at pivot (0.0F, 15.0F, 0.0F)
        ModelPartData bodyData = modelPartData.addChild("body",
                ModelPartBuilder.create()
                        .uv(24, 15).cuboid(-3.0F, -3.0F, -5.5F, 6.0F, 6.0F, 11.0F),
                ModelTransform.pivot(0.0F, 15.0F, 0.0F));

        // Tail attached to body at rear
        bodyData.addChild("tail",
                ModelPartBuilder.create()
                        .uv(30, 0).cuboid(-2.0F, -1.0F, 0.0F, 4.0F, 4.0F, 9.0F),
                ModelTransform.pivot(0.0F, -1.0F, 5.0F));

        // Hind Legs
        modelPartData.addChild("right_hind_leg",
                ModelPartBuilder.create().uv(0, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                ModelTransform.pivot(-2.0F, 18.0F, 4.0F));
        modelPartData.addChild("left_hind_leg",
                ModelPartBuilder.create().uv(8, 18).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                ModelTransform.pivot(2.0F, 18.0F, 4.0F));

        // Front Legs
        modelPartData.addChild("right_front_leg",
                ModelPartBuilder.create().uv(0, 26).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                ModelTransform.pivot(-2.0F, 18.0F, -4.0F));
        modelPartData.addChild("left_front_leg",
                ModelPartBuilder.create().uv(8, 26).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                ModelTransform.pivot(2.0F, 18.0F, -4.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.resetPositions();

        this.head.pitch = headPitch * 0.017453292F;
        this.head.yaw = headYaw * 0.017453292F;

        if (entity.isInSittingPose() || entity.isSitting()) {
            // SITTING POSE
            this.body.pivotY = 17.5F;
            this.body.pivotZ = 1.0F;
            this.body.pitch = -0.7853982F; // -45 degrees

            this.head.pivotY = 13.0F;
            this.head.pivotZ = -2.0F;
            this.head.pitch += 0.35F;

            this.rightHindLeg.pivotY = 21.5F;
            this.rightHindLeg.pivotZ = 4.5F;
            this.rightHindLeg.pitch = -1.35F;
            this.rightHindLeg.yaw = -0.2F;

            this.leftHindLeg.pivotY = 21.5F;
            this.leftHindLeg.pivotZ = 4.5F;
            this.leftHindLeg.pitch = -1.35F;
            this.leftHindLeg.yaw = 0.2F;

            this.rightFrontLeg.pivotY = 18.0F;
            this.rightFrontLeg.pivotZ = -3.5F;
            this.rightFrontLeg.pitch = 0.55F;

            this.leftFrontLeg.pivotY = 18.0F;
            this.leftFrontLeg.pivotZ = -3.5F;
            this.leftFrontLeg.pitch = 0.55F;

            this.tail.pitch = 0.65F;
            this.tail.yaw = MathHelper.cos(animationProgress * 0.1F) * 0.15F;

        } else if (entity.isStanding()) {
            // STANDING UP ON TWO HIND LEGS POSE
            this.body.pivotY = 12.0F;
            this.body.pivotZ = 0.0F;
            this.body.pitch = -1.5707964F; // -90 degrees upright

            this.head.pivotY = 5.5F;
            this.head.pivotZ = 0.0F;
            this.head.pitch += 0.1F;

            this.rightHindLeg.pivotY = 18.0F;
            this.rightHindLeg.pivotZ = 0.0F;
            this.rightHindLeg.pitch = 0.0F;

            this.leftHindLeg.pivotY = 18.0F;
            this.leftHindLeg.pivotZ = 0.0F;
            this.leftHindLeg.pitch = 0.0F;

            this.rightFrontLeg.pivotY = 9.0F;
            this.rightFrontLeg.pivotZ = -2.0F;
            this.rightFrontLeg.pitch = -1.1F + MathHelper.cos(animationProgress * 0.2F) * 0.08F;
            this.rightFrontLeg.roll = -0.2F;

            this.leftFrontLeg.pivotY = 9.0F;
            this.leftFrontLeg.pivotZ = -2.0F;
            this.leftFrontLeg.pitch = -1.1F - MathHelper.cos(animationProgress * 0.2F) * 0.08F;
            this.leftFrontLeg.roll = 0.2F;

            this.tail.pitch = 1.35F;
            this.tail.yaw = MathHelper.cos(animationProgress * 0.1F) * 0.1F;

        } else {
            // ON ALL 4'S (NORMAL QUADRUPED POSE)
            this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
            this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
            this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
            this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;

            this.tail.pitch = -0.35F + MathHelper.cos(limbAngle * 0.4F) * 0.1F * limbDistance;
            this.tail.yaw = MathHelper.cos(animationProgress * 0.12F) * 0.15F;
        }
    }

    private void resetPositions() {
        this.body.pivotX = 0.0F;
        this.body.pivotY = 15.0F;
        this.body.pivotZ = 0.0F;
        this.body.pitch = 0.0F;
        this.body.yaw = 0.0F;
        this.body.roll = 0.0F;

        this.head.pivotX = 0.0F;
        this.head.pivotY = 15.0F;
        this.head.pivotZ = -3.0F;
        this.head.pitch = 0.0F;
        this.head.yaw = 0.0F;
        this.head.roll = 0.0F;

        this.rightHindLeg.pivotX = -2.0F;
        this.rightHindLeg.pivotY = 18.0F;
        this.rightHindLeg.pivotZ = 4.0F;
        this.rightHindLeg.pitch = 0.0F;
        this.rightHindLeg.yaw = 0.0F;
        this.rightHindLeg.roll = 0.0F;

        this.leftHindLeg.pivotX = 2.0F;
        this.leftHindLeg.pivotY = 18.0F;
        this.leftHindLeg.pivotZ = 4.0F;
        this.leftHindLeg.pitch = 0.0F;
        this.leftHindLeg.yaw = 0.0F;
        this.leftHindLeg.roll = 0.0F;

        this.rightFrontLeg.pivotX = -2.0F;
        this.rightFrontLeg.pivotY = 18.0F;
        this.rightFrontLeg.pivotZ = -4.0F;
        this.rightFrontLeg.pitch = 0.0F;
        this.rightFrontLeg.yaw = 0.0F;
        this.rightFrontLeg.roll = 0.0F;

        this.leftFrontLeg.pivotX = 2.0F;
        this.leftFrontLeg.pivotY = 18.0F;
        this.leftFrontLeg.pivotZ = -4.0F;
        this.leftFrontLeg.pitch = 0.0F;
        this.leftFrontLeg.yaw = 0.0F;
        this.leftFrontLeg.roll = 0.0F;

        this.tail.pitch = -0.2F;
        this.tail.yaw = 0.0F;
        this.tail.roll = 0.0F;
    }
}
