package mod.steamnsteel.client.renderer.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mod.steamnsteel.block.machine.SpiderFactoryBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import mod.steamnsteel.client.renderer.model.SpiderFactoryModel;
import mod.steamnsteel.tileentity.SpiderFactoryTE;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class SpiderFactoryTESR extends SteamNSteelTESR
{
    public static final ResourceLocation TEXTURE = getResourceLocation(SpiderFactoryBlock.NAME);

    @Override
    public void renderTileEntityAt(TileEntity te, double x1, double y1, double z1, float partialTickTime)
    {
        SpiderFactoryTE factoryTE = (SpiderFactoryTE) te;
        if (factoryTE.isSlave()) return;

        int orientation = te.getWorldObj().getBlockMetadata(factoryTE.xCoord, factoryTE.yCoord, factoryTE.zCoord);
        int textureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int glTextureId = Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId();
        if (textureId != glTextureId) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glTranslated(x1, y1, z1);
        GL11.glRotatef(orientation * 90f, 0f, 1f, 0f);

        ModelManager.INSTANCE.spiderFactoryModel.render();
        GL11.glPopMatrix();
    }
}
