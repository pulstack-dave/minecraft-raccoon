package com.raccoon.client.model;

import com.raccoon.client.render.RaccoonRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

public class RaccoonEntityModel extends EntityModel<RaccoonRenderState> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;

    public RaccoonEntityModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.rightHindLeg = root.getChild("right_hind_leg");
        this.leftHindLeg = root.getChild("left_hind_leg");
        this.rightFrontLeg = root.getChild("right_front_leg");
        this.leftFrontLeg = root.getChild("left_front_leg");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -2.5F, -4.0F, 6.0F, 5.0F, 5.0F)
                        .texOffs(0, 11).addBox(-2.0F, 0.5F, -6.0F, 4.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 15.0F, -3.0F));
        head.addOrReplaceChild("right_ear",
                CubeListBuilder.create().texOffs(15, 0).addBox(-2.5F, -4.5F, -2.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);
        head.addOrReplaceChild("left_ear",
                CubeListBuilder.create().texOffs(21, 0).addBox(0.5F, -4.5F, -2.0F, 2.0F, 2.0F, 1.0F),
                PartPose.ZERO);

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, -3.0F, -5.5F, 6.0F, 6.0F, 11.0F),
                PartPose.offset(0.0F, 15.0F, 0.0F));
        body.addOrReplaceChild("tail",
                CubeListBuilder.create().texOffs(30, 0).addBox(-2.0F, -1.0F, 0.0F, 4.0F, 4.0F, 9.0F),
                PartPose.offset(0.0F, -1.0F, 5.0F));

        root.addOrReplaceChild("right_hind_leg",
                CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(-2.0F, 18.0F, 4.0F));
        root.addOrReplaceChild("left_hind_leg",
                CubeListBuilder.create().texOffs(8, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(2.0F, 18.0F, 4.0F));
        root.addOrReplaceChild("right_front_leg",
                CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(-2.0F, 18.0F, -4.0F));
        root.addOrReplaceChild("left_front_leg",
                CubeListBuilder.create().texOffs(8, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offset(2.0F, 18.0F, -4.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(RaccoonRenderState state) {
        this.resetPositions();

        this.head.xRot = state.xRot * ((float) Math.PI / 180F);
        this.head.yRot = state.yRot * ((float) Math.PI / 180F);

        if (state.sitting) {
            this.body.y = 17.5F;
            this.body.z = 1.0F;
            this.body.xRot = -0.7853982F;

            this.head.y = 13.0F;
            this.head.z = -2.0F;
            this.head.xRot += 0.35F;

            this.rightHindLeg.y = 21.5F;
            this.rightHindLeg.z = 4.5F;
            this.rightHindLeg.xRot = -1.35F;
            this.rightHindLeg.yRot = -0.2F;

            this.leftHindLeg.y = 21.5F;
            this.leftHindLeg.z = 4.5F;
            this.leftHindLeg.xRot = -1.35F;
            this.leftHindLeg.yRot = 0.2F;

            this.rightFrontLeg.y = 18.0F;
            this.rightFrontLeg.z = -3.5F;
            this.rightFrontLeg.xRot = 0.55F;

            this.leftFrontLeg.y = 18.0F;
            this.leftFrontLeg.z = -3.5F;
            this.leftFrontLeg.xRot = 0.55F;

            this.tail.xRot = 0.65F;
            this.tail.yRot = Mth.cos(state.ageInTicks * 0.1F) * 0.15F;
        } else if (state.standing) {
            this.body.y = 12.0F;
            this.body.z = 0.0F;
            this.body.xRot = -1.5707964F;

            this.head.y = 5.5F;
            this.head.z = 0.0F;
            this.head.xRot += 0.1F;

            this.rightHindLeg.y = 18.0F;
            this.rightHindLeg.z = 0.0F;
            this.rightHindLeg.xRot = 0.0F;

            this.leftHindLeg.y = 18.0F;
            this.leftHindLeg.z = 0.0F;
            this.leftHindLeg.xRot = 0.0F;

            this.rightFrontLeg.y = 9.0F;
            this.rightFrontLeg.z = -2.0F;
            this.rightFrontLeg.xRot = -1.1F + Mth.cos(state.ageInTicks * 0.2F) * 0.08F;
            this.rightFrontLeg.zRot = -0.2F;

            this.leftFrontLeg.y = 9.0F;
            this.leftFrontLeg.z = -2.0F;
            this.leftFrontLeg.xRot = -1.1F - Mth.cos(state.ageInTicks * 0.2F) * 0.08F;
            this.leftFrontLeg.zRot = 0.2F;

            this.tail.xRot = 1.35F;
            this.tail.yRot = Mth.cos(state.ageInTicks * 0.1F) * 0.1F;
        } else {
            float limbSwing = state.walkAnimationPos;
            float limbSwingAmount = state.walkAnimationSpeed;
            this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
            this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
            this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

            this.tail.xRot = -0.35F + Mth.cos(limbSwing * 0.4F) * 0.1F * limbSwingAmount;
            this.tail.yRot = Mth.cos(state.ageInTicks * 0.12F) * 0.15F;
        }
    }

    private void resetPositions() {
        this.body.x = 0.0F;
        this.body.y = 15.0F;
        this.body.z = 0.0F;
        this.body.xRot = 0.0F;
        this.body.yRot = 0.0F;
        this.body.zRot = 0.0F;

        this.head.x = 0.0F;
        this.head.y = 15.0F;
        this.head.z = -3.0F;
        this.head.xRot = 0.0F;
        this.head.yRot = 0.0F;
        this.head.zRot = 0.0F;

        this.rightHindLeg.x = -2.0F;
        this.rightHindLeg.y = 18.0F;
        this.rightHindLeg.z = 4.0F;
        this.rightHindLeg.xRot = 0.0F;
        this.rightHindLeg.yRot = 0.0F;
        this.rightHindLeg.zRot = 0.0F;

        this.leftHindLeg.x = 2.0F;
        this.leftHindLeg.y = 18.0F;
        this.leftHindLeg.z = 4.0F;
        this.leftHindLeg.xRot = 0.0F;
        this.leftHindLeg.yRot = 0.0F;
        this.leftHindLeg.zRot = 0.0F;

        this.rightFrontLeg.x = -2.0F;
        this.rightFrontLeg.y = 18.0F;
        this.rightFrontLeg.z = -4.0F;
        this.rightFrontLeg.xRot = 0.0F;
        this.rightFrontLeg.yRot = 0.0F;
        this.rightFrontLeg.zRot = 0.0F;

        this.leftFrontLeg.x = 2.0F;
        this.leftFrontLeg.y = 18.0F;
        this.leftFrontLeg.z = -4.0F;
        this.leftFrontLeg.xRot = 0.0F;
        this.leftFrontLeg.yRot = 0.0F;
        this.leftFrontLeg.zRot = 0.0F;

        this.tail.xRot = -0.2F;
        this.tail.yRot = 0.0F;
        this.tail.zRot = 0.0F;
    }
}
