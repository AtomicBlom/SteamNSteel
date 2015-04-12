package mod.steamnsteel.client.renderer.block;

import com.sun.prism.util.tess.Tess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.block.machine.SpiderFactoryBlock;
import mod.steamnsteel.client.renderer.ModelManager;
import mod.steamnsteel.client.renderer.model.SpiderFactoryModel;
import mod.steamnsteel.proxy.Proxies;
import mod.steamnsteel.tileentity.SpiderFactoryTE;
import mod.steamnsteel.utility.math.Matrix4;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix3f;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

/**
 * Created by steblo on 7/04/2015.
 */
public class SpiderFactoryRenderer implements ISimpleBlockRenderingHandler {
    private final int id;

    public static final SpiderFactoryRenderer INSTANCE = new SpiderFactoryRenderer();

    private SpiderFactoryRenderer()
    {
        this.id = RenderingRegistry.getNextAvailableRenderId();

    }

    //public static final ResourceLocation TEXTURE = getResourceLocation(SpiderFactoryBlock.NAME);

    //private final SpiderFactoryModel model = new SpiderFactoryModel();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        SpiderFactoryTE factoryTE = (SpiderFactoryTE) world.getTileEntity(x, y, z);
        if (factoryTE.isSlave()) return false;

        int orientation = world.getBlockMetadata(x, y, z);
        int textureId = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int glTextureId = Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId();
        if (textureId != glTextureId) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
        }

        Tessellator.instance.draw();
        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);

        Matrix4 m2 = new Matrix4()
                .translate(x, y, z)
                .translate(0.5, 0, 0.5)
                .rotate(Math.toRadians(orientation * 90f), 0.0, 1.0, 0.0)
                .translate(-0.5, 0, -0.5)
                ;

        int pass = Proxies.render.getCurrentRenderPass();
        Tessellator.instance.startDrawing(GL11.GL_TRIANGLES);

        ModelManager.INSTANCE.spiderFactoryModel.tessellate(Tessellator.instance, m2, pass);
        GL11.glPopMatrix();
        if (pass == 0) {
            Tessellator.instance.draw();
            Tessellator.instance.startDrawing(GL11.GL_QUADS);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return id;
    }
}
