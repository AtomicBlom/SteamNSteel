package mod.steamnsteel.client.renderer.model;

import mod.steamnsteel.block.machine.PipeRedstoneValveBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import java.util.HashMap;

public class PipeRedstoneValveModel extends SteamNSteelModel
{
    private static final ResourceLocation MODEL = ModelManager.getModelResourceLocation(PipeRedstoneValveBlock.NAME);

    public PipeRedstoneValveModel()
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
    public void renderRedstoneValve() {
        model.renderPart("VPRedstone");
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
