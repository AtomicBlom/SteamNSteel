package mod.steamnsteel.client.model.debug;

import com.google.common.base.Function;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by codew on 15/02/2016.
 */
public class SteamTransportDebugModel implements IModel
{
    @Override
    public Collection<ResourceLocation> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public Collection<ResourceLocation> getTextures()
    {
        return Collections.emptyList();
    }

    @Override
    public IFlexibleBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureResolver)
    {
        return new SteamTransportDebugModelInstance(state, format, textureResolver);
    }

    @Override
    public IModelState getDefaultState()
    {
        return new SteamTransportDebugState();
    }
}
