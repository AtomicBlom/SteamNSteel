package mod.steamnsteel.texturing;

import mod.steamnsteel.utility.position.ChunkCoord;
import mod.steamnsteel.utility.position.WorldBlockCoord;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import java.util.Collection;

public interface IProceduralWallFeature
{
    Iterable<FeatureInstance> getFeatureInstancesFor(ChunkCoord chunkCoord);

    long getFeatureId();

    long getSubProperties(TextureContext context);

    void setFeatureId(long featureId);

    String getName();

    Layer getLayer();

    Behaviour getBehaviourAgainst(IProceduralWallFeature otherLayerFeature, long featureProperties);

    boolean isFeatureValid(TextureContext context);

    long getIncompatibleProperties();
}
