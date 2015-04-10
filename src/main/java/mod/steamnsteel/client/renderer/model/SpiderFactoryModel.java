package mod.steamnsteel.client.renderer.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mod.steamnsteel.block.machine.SpiderFactoryBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import mod.steamnsteel.utility.math.Matrix4;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.GroupObject;

import java.security.acl.Group;
import java.util.ArrayList;
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

    @Override
    public void tessellate(Tessellator tessellator, Matrix4 m, int pass) {
        tessellate(tessellator, m, passes[pass]);
    }

    ArrayList<GroupObject>[] passes = new ArrayList[2];

    @Override
    protected void preparePasses() {
        passes = new ArrayList[] {
                new ArrayList<GroupObject>(),
                new ArrayList<GroupObject>()
        };

        for (GroupObject groupObject : model.groupObjects) {
            if (groupObject.name.contains("Bulb")) {
                passes[1].add(groupObject);
            } else {
                passes[0].add(groupObject);
            }
        }
    }
}
