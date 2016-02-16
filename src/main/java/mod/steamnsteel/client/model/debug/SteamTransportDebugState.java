package mod.steamnsteel.client.model.debug;

import com.google.common.base.Optional;
import net.minecraftforge.client.model.IModelPart;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.TRSRTransformation;

/**
 * Created by codew on 15/02/2016.
 */
public class SteamTransportDebugState implements IModelState
{
    @Override
    public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
        return Optional.absent();
    }
}
