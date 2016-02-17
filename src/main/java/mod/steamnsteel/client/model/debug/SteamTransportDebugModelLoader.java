package mod.steamnsteel.client.model.debug;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import java.io.IOException;

/**
 * Created by codew on 15/02/2016.
 */
public enum SteamTransportDebugModelLoader implements ICustomModelLoader
{
    instance;

    @Override
    public boolean accepts(ResourceLocation modelLocation)
    {
        final boolean domainMatches = "steamnsteel".equals(modelLocation.getResourceDomain());
        if (!domainMatches) return false;
        final boolean resourceMatches = "models/block/steamTransportDebug".equals(modelLocation.getResourcePath());
        return resourceMatches;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws IOException
    {
        return new SteamTransportDebugModel();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {

    }
}
