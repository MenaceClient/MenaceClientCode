package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelEnderMite extends ModelBase
{
    private static final int[][] field_178716_a = new int[][] {{4, 3, 2}, {6, 4, 5}, {3, 3, 1}, {1, 2, 1}};
    private static final int[][] field_178714_b = new int[][] {{0, 0}, {0, 5}, {0, 14}, {0, 18}};
    private static final int field_178715_c = field_178716_a.length;
    private final ModelRenderer[] bodyparts;

    public ModelEnderMite()
    {
        this.bodyparts = new ModelRenderer[field_178715_c];
        float f = -3.5F;

        for (int i = 0; i < this.bodyparts.length; ++i)
        {
            this.bodyparts[i] = new ModelRenderer(this, field_178714_b[i][0], field_178714_b[i][1]);
            this.bodyparts[i].addBox((float)field_178716_a[i][0] * -0.5F, 0.0F, (float)field_178716_a[i][2] * -0.5F, field_178716_a[i][0], field_178716_a[i][1], field_178716_a[i][2]);
            this.bodyparts[i].setRotationPoint(0.0F, (float)(24 - field_178716_a[i][1]), f);

            if (i < this.bodyparts.length - 1)
            {
                f += (float)(field_178716_a[i][2] + field_178716_a[i + 1][2]) * 0.5F;
            }
        }
    }

    public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
    {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);

        for (int i = 0; i < this.bodyparts.length; ++i)
        {
            this.bodyparts[i].render(scale);
        }
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        for (int i = 0; i < this.bodyparts.length; ++i)
        {
            this.bodyparts[i].rotateAngleY = MathHelper.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.01F * (float)(1 + Math.abs(i - 2));
            this.bodyparts[i].rotationPointX = MathHelper.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.1F * (float)Math.abs(i - 2);
        }
    }

    public ModelRenderer[] getBodyParts()
    {
        return this.bodyparts;
    }

}
