package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelDragon;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderDragon;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.boss.EntityDragon;
import net.optifine.reflect.Reflector;

public class ModelAdapterDragon extends ModelAdapter
{
    public ModelAdapterDragon()
    {
        super(EntityDragon.class, "dragon", 0.5F);
    }

    public ModelBase makeModel()
    {
        return new ModelDragon(0.0F);
    }

    public ModelRenderer getModelRenderer(ModelBase model, String modelPart)
    {
        if (!(model instanceof ModelDragon))
        {
            return null;
        }
        else
        {
            ModelDragon modeldragon = (ModelDragon)model;
            switch (modelPart) {
                case "head":
                    return modeldragon.getHead();
                case "spine":
                    return modeldragon.getSpine();
                case "jaw":
                    return modeldragon.getJaw();
                case "body":
                    return modeldragon.getBody();
                case "rear_leg":
                    return modeldragon.getRearLeg();
                case "front_leg":
                    return modeldragon.getFrontLeg();
                case "rear_leg_tip":
                    return modeldragon.getRearLegTip();
                case "front_leg_tip":
                    return modeldragon.getFrontLegTip();
                case "rear_foot":
                    return modeldragon.getRearFoot();
                case "front_foot":
                    return modeldragon.getFrontFoot();
                case "wing":
                    return modeldragon.getWing();
                case "wing_tip":
                    return modeldragon.getWingTip();
                default:
                    return null;
            }
        }
    }

    public String[] getModelRendererNames()
    {
        return new String[] {"head", "spine", "jaw", "body", "rear_leg", "front_leg", "rear_leg_tip", "front_leg_tip", "rear_foot", "front_foot", "wing", "wing_tip"};
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderDragon renderdragon = new RenderDragon(rendermanager);
        renderdragon.mainModel = modelBase;
        renderdragon.shadowSize = shadowSize;
        return renderdragon;
    }
}
