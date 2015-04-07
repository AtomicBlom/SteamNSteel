package mod.steamnsteel.client.renderer.model;

import mod.steamnsteel.block.machine.PipeValveBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import java.util.HashMap;

public class PipeValveModel extends SteamNSteelModel
{
    private static final ResourceLocation MODEL = ModelManager.getModelResourceLocation(PipeValveBlock.NAME);

    public PipeValveModel()
    {
        super(MODEL);
        reload();
    }

    public void renderAll()
    {
        model.renderAll();
    }

    public void renderPipe() {
        model.renderPart("VPPipe");
    }
    public void renderBody() {
        model.renderPart("VPBody");
    }
    public void renderValve() {
        model.renderPart("VPValve");
    }
    public void renderOpeningA() {
        model.renderPart("VPOpening1");
    }
    public void renderOpeningB() {
        model.renderPart("VPOpening2");
    }

    @Override
    protected HashMap<String, ResourceLocation> getGroupObjectTextures() {
        return null;
    }
}
