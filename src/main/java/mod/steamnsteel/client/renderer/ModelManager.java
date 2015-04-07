package mod.steamnsteel.client.renderer;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.client.renderer.model.SpiderFactoryModel;
import mod.steamnsteel.entity.SpiderFactoryEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.WavefrontObject;

/**
 * Created by steblo on 7/04/2015.
 */
public class ModelManager {
    public static final ModelManager INSTANCE = new ModelManager();

    private static final String MODEL_LOCATION = "models/";
    private static final String MODEL_FILE_EXTENSION = ".obj";

    public SpiderFactoryModel spiderFactoryModel;

    private ModelManager() {
        spiderFactoryModel = new SpiderFactoryModel(getModelResourceLocation(SpiderFactoryEntity.NAME));
    }

    @SubscribeEvent
    public void OnPreTextureStitched(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() != 0) return;
        spiderFactoryModel.reload();

        spiderFactoryModel.registerTextures(event.map);
    }

    @SubscribeEvent
    public void OnPostTextureStitched(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() != 0) return;
        spiderFactoryModel.adjustTextureCoordinates();
    }

    public static ResourceLocation getModelResourceLocation(String name)
    {
        return new ResourceLocation(TheMod.MOD_ID.toLowerCase(), MODEL_LOCATION + name + MODEL_FILE_EXTENSION);
    }
    //private static final String TEXTURE_FILE_EXTENSION = ".png";
    private static final String TEXTURE_FILE_EXTENSION = "";
    public static final String TEXTURE_LOCATION = "models/";

    public static ResourceLocation getTextureResourceLocation(String name)
    {
        ResourceLocation resourceLocation = new ResourceLocation(TheMod.MOD_ID.toLowerCase(), getTexturePath(name));
        return resourceLocation;
    }

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    private static String getTexturePath(String name)
    {
        return TEXTURE_LOCATION + name + TEXTURE_FILE_EXTENSION;
    }
}
