package mod.steamnsteel.client.model.pct;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.library.ModProperties;
import mod.steamnsteel.texturing.api.ProceduralConnectedTexture;
import mod.steamnsteel.utility.log.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;
import javax.vecmath.Matrix4f;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PCTModelInstance implements IPerspectiveAwareModel
{
    private final IModelState state;
    private final VertexFormat format;
    private final Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter;
    private final ProceduralConnectedTexture proceduralConnectedTexture;
    private final Map<Integer, IBakedModel> cache = Maps.newHashMap();
    private IBakedModel bakedModel = null;
    private IModel baseModel = null;

    public PCTModelInstance(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ProceduralConnectedTexture pct)
    {
        this(state, format, bakedTextureGetter, pct, getBaseModel());
        CalculateSides(null, null);
    }

    private static IModel getBaseModel()
    {
        try
        {
            return ModelLoaderRegistry.getModel(new ResourceLocation(TheMod.MOD_ID, "block/cube_mirrored_bottom"));
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private PCTModelInstance(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ProceduralConnectedTexture pct, IModel baseModel) {
        this.state = state;
        this.format = format;
        this.bakedTextureGetter = bakedTextureGetter;
        proceduralConnectedTexture = pct;

        this.baseModel = baseModel;
        bakedModel = baseModel.bake(state, format, bakedTextureGetter);
    }

    public PCTModelInstance(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, ProceduralConnectedTexture pct, BlockPos blockPos, IBlockAccess blockAccess, IModel baseModel)
    {
        this(state, format, bakedTextureGetter, pct, baseModel);
        CalculateSides(blockPos, blockAccess);
    }

    private void CalculateSides(BlockPos blockPos, IBlockAccess blockAccess)
    {
        final Builder<String, String> textures = ImmutableMap.builder();

        int identifier = 31;

        for (final EnumFacing side : EnumFacing.VALUES)
        {

            final String key = side.toString().toLowerCase();
            TextureAtlasSprite sprite = proceduralConnectedTexture.getDefaultTextureForSide(side);
            if (blockAccess != null)
            {
                final IBlockState blockState = blockAccess.getBlockState(blockPos);

                if (blockState.shouldSideBeRendered(blockAccess, blockPos.offset(side), side))
                {
                    try
                    {
                        sprite = proceduralConnectedTexture.getSpriteForSide(blockAccess, blockPos, side);
                    } catch (final Exception e)
                    {
                        Logger.info("blockPos: %s, side: %s", blockPos, side);
                        sprite = proceduralConnectedTexture.getSpriteForSide(blockAccess, blockPos, side);
                    }
                }
            }
            String texture = "missingno";
            if (sprite != null)
            {
                texture = sprite.getIconName();
            } else
            {
                System.out.print("woof");
            }

            identifier ^= texture.hashCode() * 31;
            textures.put(key, texture);
        }

        bakedModel = cache.get(identifier);
        if (bakedModel == null) {

            IModel newModel = baseModel;

            if (baseModel instanceof IRetexturableModel) {
                final ImmutableMap<String, String> build = textures.build();
                newModel = ((IRetexturableModel) baseModel).retexture(build);
            }

            bakedModel = newModel.bake(state, format, bakedTextureGetter);
            cache.put(identifier, bakedModel);
        }
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand)
    {
        return bakedModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return bakedModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d()
    {
        return bakedModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return bakedModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return proceduralConnectedTexture.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return bakedModel.getItemCameraTransforms();
    }

    /*
    @Override
    public IBakedModel handleBlockState(IBlockState blockState)
    {
        if (blockState instanceof IExtendedBlockState) {
            final IExtendedBlockState extendedBlockState = (IExtendedBlockState) blockState;
            if (extendedBlockState.getUnlistedProperties().containsKey(ModProperties.PROPERTY_BLOCK_POS) &&
                    extendedBlockState.getUnlistedProperties().containsKey(ModProperties.PROPERTY_BLOCK_ACCESS)) {

                final BlockPos blockPos = extendedBlockState.getValue(ModProperties.PROPERTY_BLOCK_POS);
                final IBlockAccess blockAccess = extendedBlockState.getValue(ModProperties.PROPERTY_BLOCK_ACCESS);

                return new PCTModelInstance(state, format, bakedTextureGetter, proceduralConnectedTexture, blockPos, blockAccess, baseModel);
            }
        }

        return this;
    }
    */

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
    {
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, state, cameraTransformType);
    }

    public ProceduralConnectedTexture getProceduralConnectedTexture() {
        return proceduralConnectedTexture;
    }

    public ItemOverrideList getOverrides()
    {
        // TODO handle items
        return ItemOverrideList.NONE;
    }
}
