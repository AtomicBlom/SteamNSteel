package mod.steamnsteel.client.model.debug;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import mod.steamnsteel.block.debug.DummySteamTransportBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.Collections;
import java.util.List;

public class SteamTransportDebugModelInstance implements IFlexibleBakedModel, ISmartBlockModel, IPerspectiveAwareModel
{
    private final IModelState state;
    private final VertexFormat format;
    private final IExtendedBlockState blockState;
    private final Function<ResourceLocation, TextureAtlasSprite> textureResolver;

    public SteamTransportDebugModelInstance(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureResolver) {
        this(state, null, format, textureResolver);
    }

    public SteamTransportDebugModelInstance(IModelState state, IExtendedBlockState blockState, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> textureResolver)
    {
        this.state = state;
        this.format = format;
        this.blockState = blockState;
        this.textureResolver = textureResolver;
    }

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing side)
    {
        final ImmutableList.Builder<BakedQuad> builder = ImmutableList.<BakedQuad>builder();

        final TextureAtlasSprite sprite = textureResolver.apply(new ResourceLocation("minecraft", "blocks/wool_colored_white"));

        Float condensation = blockState.getValue(DummySteamTransportBlock.CondensationProperty);
        condensation = condensation == null ? 0 : condensation;

        Float steamDensity = blockState.getValue(DummySteamTransportBlock.SteamDensityProperty);
        steamDensity = steamDensity == null ? 0 : steamDensity;

        float height = condensation / 1000.0f;

        final Vector3f faceNormal = new Vector3f(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());

        final float[] condensateColour = {0.0f, 0.0f, 1f, 0.9f};
        final float[] steamColour = {1f, 1f, 1f, steamDensity};
        final float[] UVs = {
                0,
                0
        };
        UnpackedBakedQuad.Builder quadBuilder;

        float margin = 1f / 16f;

        float steamMin = 0.0f + margin;
        float steamMax = 1.0f - margin;

        switch (side) {
            case UP:
                if (height > 0)
                {
                    quadBuilder = new UnpackedBakedQuad.Builder(format);
                    quadBuilder.setQuadColored();
                    quadBuilder.setQuadOrientation(side);
                    putVertexData(quadBuilder, new Vector4f(0, height, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, height, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, height, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, height, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    builder.add(quadBuilder.build());
                }

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setQuadColored();
                quadBuilder.setQuadOrientation(side);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMax, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMax, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMax, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMax, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                builder.add(quadBuilder.build());

                break;
            case DOWN:
                if (height > 0)
                {
                    quadBuilder = new UnpackedBakedQuad.Builder(format);
                    quadBuilder.setQuadColored();
                    quadBuilder.setQuadOrientation(side);
                    putVertexData(quadBuilder, new Vector4f(1, 0, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, 0, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, 0, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, 0, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    builder.add(quadBuilder.build());
                }

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setQuadColored();
                quadBuilder.setQuadOrientation(side);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMin, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMin, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMin, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMin, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                builder.add(quadBuilder.build());
                break;
            case NORTH:
                if (height > 0)
                {
                    quadBuilder = new UnpackedBakedQuad.Builder(format);
                    quadBuilder.setQuadColored();
                    quadBuilder.setQuadOrientation(side);
                    putVertexData(quadBuilder, new Vector4f(0, 0,      0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, height, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, height, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, 0,      0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    builder.add(quadBuilder.build());
                }

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setQuadColored();
                quadBuilder.setQuadOrientation(side);
                putVertexData(quadBuilder, new Vector4f(steamMin, height, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMax, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMax, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, height, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                builder.add(quadBuilder.build());
                break;
            case SOUTH:
                if (height > 0)
                {
                    quadBuilder = new UnpackedBakedQuad.Builder(format);
                    quadBuilder.setQuadColored();
                    quadBuilder.setQuadOrientation(side);
                    putVertexData(quadBuilder, new Vector4f(1, 0, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, height, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, height, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, 0, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    builder.add(quadBuilder.build());
                }

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setQuadColored();
                quadBuilder.setQuadOrientation(side);
                putVertexData(quadBuilder, new Vector4f(steamMax, height, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMax, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMax, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, height, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                builder.add(quadBuilder.build());
                break;
            case EAST:
                if (height > 0)
                {
                    quadBuilder = new UnpackedBakedQuad.Builder(format);
                    quadBuilder.setQuadColored();
                    quadBuilder.setQuadOrientation(side);
                    putVertexData(quadBuilder, new Vector4f(0, 0, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, height, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, height, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(0, 0, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    builder.add(quadBuilder.build());
                }

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setQuadColored();
                quadBuilder.setQuadOrientation(side);
                putVertexData(quadBuilder, new Vector4f(steamMin, height, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMax, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, steamMax, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMin, height, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                builder.add(quadBuilder.build());
                break;
            case WEST:
                if (height > 0)
                {
                    quadBuilder = new UnpackedBakedQuad.Builder(format);
                    quadBuilder.setQuadColored();
                    quadBuilder.setQuadOrientation(side);
                    putVertexData(quadBuilder, new Vector4f(1, 0,      0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, height, 0, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, height, 1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    putVertexData(quadBuilder, new Vector4f(1, 0,      1, 1), faceNormal, null, UVs, condensateColour, sprite);
                    builder.add(quadBuilder.build());
                }

                quadBuilder = new UnpackedBakedQuad.Builder(format);
                quadBuilder.setQuadColored();
                quadBuilder.setQuadOrientation(side);
                putVertexData(quadBuilder, new Vector4f(steamMax, height, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMax, steamMin, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, steamMax, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                putVertexData(quadBuilder, new Vector4f(steamMax, height, steamMax, 1), faceNormal, null, UVs, steamColour, sprite);
                builder.add(quadBuilder.build());
                break;
            default:
                return Collections.emptyList();
        }
        return builder.build();
    }

    private void putVertexData(UnpackedBakedQuad.Builder builder, Vector4f vertex, Vector3f faceNormal, Vector3f vertexNormal, float[] textureCoordinates, float[] color, TextureAtlasSprite sprite)
    {
        // TODO handle everything not handled (texture transformations, bones, transformations, normals, e.t.c)
        for (int e = 0; e < format.getElementCount(); e++)
        {
            switch (format.getElement(e).getUsage())
            {
                case POSITION:
                    builder.put(e, vertex.x, vertex.y, vertex.z, 1);
                    break;
                case COLOR:
                    float d = LightUtil.diffuseLight(faceNormal.x, faceNormal.y, faceNormal.z);
                    if (color != null)
                    {
                        //If color.length != 4, then input was RGB, use Alpha of 1.0
                        final float v = color.length == 4 ? color[3] : 1.0f;
                        builder.put(e, d * color[0], d * color[1], d * color[2], v);
                    } else
                    {
                        builder.put(e, d, d, d, 1);
                    }
                    break;
                case UV:
                    final int index = format.getElement(e).getIndex();
                    if (index < (textureCoordinates.length / 2))
                    {
                        builder.put(e,
                                sprite.getInterpolatedU(textureCoordinates[index * 2] * 16),
                                sprite.getInterpolatedV((1 - textureCoordinates[index * 2 + 1]) * 16),
                                0,
                                1
                        );
                    } else
                    {
                        builder.put(e, 0, 0, 0, 1);
                    }
                    break;
                case NORMAL:
                    //w changed to 0, Fry assures me it's a bug in B3d.
                    if (vertexNormal != null)
                    {
                        builder.put(e, vertexNormal.x, vertexNormal.y, vertexNormal.z, 0);
                    } else
                    {
                        builder.put(e, faceNormal.x, faceNormal.y, faceNormal.z, 0);
                    }
                    break;
                default:
                    builder.put(e);
            }
        }
    }

    @Override
    public List<BakedQuad> getGeneralQuads()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return false;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public VertexFormat getFormat()
    {
        return format;
    }

    @Override
    public Pair<? extends IFlexibleBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType)
    {
        return MapWrapper.handlePerspective(this, state, cameraTransformType);
    }

    @Override
    public IBakedModel handleBlockState(IBlockState state)
    {
        if (state instanceof IExtendedBlockState) {
            IExtendedBlockState extendedState = (IExtendedBlockState)state;
            return new SteamTransportDebugModelInstance(this.state, extendedState, format, textureResolver);
        }

        return this;
    }
}