package mod.steamnsteel.client.renderer.item;

import mod.steamnsteel.TheMod;
import mod.steamnsteel.client.renderer.ColladaModelInstance;
import mod.steamnsteel.client.renderer.ColladaRenderer;
import mod.steamnsteel.library.ModItem;
import mod.steamnsteel.utility.log.Logger;
import mod.steamnsteel.client.collada.ColladaModelReader;
import mod.steamnsteel.client.collada.model.ColladaModel;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by codew on 1/05/2015.
 */
public class PipeWrenchItemRenderer implements IItemRenderer {

    private static final String MODEL_LOCATION = "models/";
    private static final String MODEL_FILE_EXTENSION = ".dae";
    private final ColladaRenderer modelRenderer;

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    protected static String getModelPath(String name)
    {
        return MODEL_LOCATION + name + MODEL_FILE_EXTENSION;
    }

    public PipeWrenchItemRenderer() {
        ResourceLocation modelLocation =  new ResourceLocation(TheMod.MOD_ID.toLowerCase(), getModelPath(ModItem.Names.PIPE_WRENCH));
        ColladaModel model = null;
        try {
            model = ColladaModelReader.read(modelLocation);
        } catch (Exception e) {
            Logger.warning("Could not load model: %s because %s", modelLocation, e);
        }

        ColladaModelInstance modelInstance = new ColladaModelInstance(model);

        this.modelRenderer = new ColladaRenderer(modelInstance);
        modelInstance.runAllAnimations();

    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        long l = Minecraft.getMinecraft().theWorld.getWorldTime() % 10;

        GL11.glScalef(2, 2, 2);

        //GL11.glRotatef(180, 1, 0, 0);
        GL11.glTranslated(0.5, 0.5, -0.5);
        //GL11.glRotatef(-45, 0, 1, 0);
        //GL11.glRotatef(90, 1, 0, 0);




        //ColladaRenderer.tesselate(model);
        modelRenderer.tesselate();
        GL11.glPopMatrix();
    }
}
