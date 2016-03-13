package mod.steamnsteel.client.renderer.tileentity;

import mod.steamnsteel.client.model.opengex.OpenGEXAnimationFrameProperty;
import mod.steamnsteel.client.model.opengex.OpenGEXState;
import mod.steamnsteel.tileentity.debug.DummySteamTransportTE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.opengl.GL11;

/**
 * Created by codew on 24/02/2016.
 */
public class DummySteamTransportTESR extends TileEntitySpecialRenderer<DummySteamTransportTE>
{
    @Override
    public void renderTileEntityAt(DummySteamTransportTE te, double x, double y, double z, float partialTicks, int destroyStage)
    {
        final BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
        IBlockState blockState = te.getWorld().getBlockState(te.getPos());
        BlockPos blockpos = te.getPos();
        final OpenGEXState openGEXState = new OpenGEXState(null, getWorld().getTotalWorldTime() / 20.0f);
        IBakedModel model = blockRenderer.getModelFromBlockState(blockState, getWorld(), te.getPos());
        blockState = ((IExtendedBlockState)blockState).withProperty(OpenGEXAnimationFrameProperty.instance, openGEXState);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        bindTexture(TextureMap.locationBlocksTexture);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();

        if (Minecraft.isAmbientOcclusionEnabled())
        {
            GlStateManager.shadeModel(7425);
        }
        else
        {
            GlStateManager.shadeModel(7424);
        }

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        worldRenderer.setTranslation(x - blockpos.getX(), y - blockpos.getY(), z - blockpos.getZ());
        worldRenderer.color(255, 255, 255, 255);

        if (model instanceof ISmartBlockModel) {
            model = ((ISmartBlockModel) model).handleBlockState(blockState);
        }

        blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), model, blockState, te.getPos(), worldRenderer);


        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
        worldRenderer.setTranslation(0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean forceTileEntityRender()
    {
        return super.forceTileEntityRender();
    }
}
