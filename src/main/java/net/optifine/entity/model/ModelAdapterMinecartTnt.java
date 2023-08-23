package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTntMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.src.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecartTnt extends ModelAdapterMinecart
{
    public ModelAdapterMinecartTnt()
    {
        super(EntityMinecartTNT.class, "tnt_minecart", 0.5F);
    }

    public IEntityRenderer makeEntityRender(ModelBase modelBase, float shadowSize)
    {
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        RenderTntMinecart rendertntminecart = new RenderTntMinecart(rendermanager);


        rendertntminecart.setModelMinecart(modelBase);
        rendertntminecart.shadowSize = shadowSize;
        return rendertntminecart;

    }
}
