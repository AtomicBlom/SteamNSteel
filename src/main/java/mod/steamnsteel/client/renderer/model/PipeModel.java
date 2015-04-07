package mod.steamnsteel.client.renderer.model;

import mod.steamnsteel.block.machine.PipeBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import java.util.HashMap;

public class PipeModel extends SteamNSteelModel
{
    private static final ResourceLocation MODEL = ModelManager.getModelResourceLocation(PipeBlock.NAME);

    public PipeModel()
    {
        super(MODEL);
        reload();
    }

    public void renderAll()
    {
        model.renderAll();
    }

    public void renderPipeCornerNE() {
        model.renderPart("PipeCrnrNE");
    }
    public void renderPipeCornerNW() {
        model.renderPart("PipeCrnrNW");
    }
    public void renderPipeCornerSE() {
        model.renderPart("PipeCrnrSE");
    }
    public void renderPipeCornerSW() {
        model.renderPart("PipeCrnrSW");
    }

    public void renderPipeStraight() { model.renderPart("Pipe0C"); }

    /**
     * Used for where a pipe connects to a machine or junction
     */
    public void renderPipeOpening() { model.renderPart("PipeOpening"); }

    /**
     * Used where a pipe ends.
     */
    public void renderPipeCap() {
        model.renderPart("PipeCap");
    }

    public void renderJunctionBox() {
        model.renderPart("Box002");
    }

    @Override
    protected HashMap<String, ResourceLocation> getGroupObjectTextures() {
        return null;
    }
}
