package mod.steamnsteel.client.renderer.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mod.steamnsteel.block.machine.SpiderFactoryBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public class SpiderFactoryModel extends SteamNSteelModel {
    private static final HashMap<String, ResourceLocation> groupObjectTextures = new HashMap<String, ResourceLocation>();

    public SpiderFactoryModel(ResourceLocation resourceLocation)
    {
        super(resourceLocation);
        groupObjectTextures.put(DEFAULT_TEXTURE, ModelManager.getTextureResourceLocation(SpiderFactoryBlock.NAME));
    }

    @Override
    protected HashMap<String, ResourceLocation> getGroupObjectTextures() {
        return groupObjectTextures;
    }
}
