package mod.steamnsteel.client.renderer.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mod.steamnsteel.block.machine.SpiderFactoryBlock;
import mod.steamnsteel.client.renderer.model.SpiderFactoryModel;
import mod.steamnsteel.tileentity.SpiderFactoryTE;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

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
        /*SpiderFactoryTE factoryTE = (SpiderFactoryTE) world.getTileEntity(x, y, z);
        if (factoryTE.isSlave()) return false;

        int metadata = world.getBlockMetadata(x, y, z);

        bindTexture(TEXTURE);
        GL11.glPushMatrix();
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glTranslated(x, y, z);
        GL11.glTranslated(0.5, 0, 0.5);
        GL11.glRotatef(metadata * 90f, 0f, 1f, 0f);
        GL11.glTranslated(-0.5, 0, .700);
        GL11.glRotatef(180F, 1F, 0F, 0F);

        model.render();
        GL11.glPopMatrix();*/
        return false;
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
