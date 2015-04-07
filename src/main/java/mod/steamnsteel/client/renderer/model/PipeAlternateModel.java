package mod.steamnsteel.client.renderer.model;

import mod.steamnsteel.block.machine.PipeBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import java.util.HashMap;

public class PipeAlternateModel extends SteamNSteelModel
{
    private static final ResourceLocation MODEL = ModelManager.getModelResourceLocation(PipeBlock.NAME + "-alt");

    public PipeAlternateModel()
    {
        super(MODEL);
        reload();
    }

    public void renderAll()
    {
        model.renderAll();
    }

    public void renderPipeCornerNW() {
        model.renderPart("PipeCrnrNWAlt");
    }

    @Override
    protected HashMap<String, ResourceLocation> getGroupObjectTextures() {
        return null;
    }
}
