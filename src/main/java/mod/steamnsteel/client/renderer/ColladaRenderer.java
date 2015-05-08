package mod.steamnsteel.client.renderer;

import codechicken.lib.vec.Matrix4;
import mod.steamnsteel.TheMod;
import mod.steamnsteel.client.collada.model.transformation.MatrixTransformation;
import mod.steamnsteel.client.collada.model.*;
import mod.steamnsteel.client.collada.model.transformation.TransformationBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.Vertex;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

/**
 * Created by codew on 1/05/2015.
 */
public class ColladaRenderer {

    private final ColladaModelInstance model;

    public ColladaRenderer(ColladaModelInstance model) {

        this.model = model;

    }

    private MeshGeometryType currentType = null;
    private String currentMaterial = null;

    public void tesselate() {
        model.updateRunningAnimations();
        currentType = null;
        currentMaterial = null;
        for (ColladaScene scene : model.getModel().getScenes()) {
            for (ColladaNode node : scene.getNodes()) {
                renderNode(node, MatrixTransformation.getIdentity());
            }
        }
        Tessellator.instance.draw();
    }

    private void renderNode(ColladaNode node, MatrixTransformation identity) {
        MatrixTransformation nodeMatrix = MatrixTransformation.getIdentity();

        for (TransformationBase transform : node.getTransformations()) {
            String s = node.getSid() + "/" + transform.getSid();
            Iterable<ColladaRunningAnimation> runningAnimationsForTransform = model.getRunningAnimationsForTransform(s);
            if (runningAnimationsForTransform != null) {
                for (ColladaRunningAnimation colladaRunningAnimation : runningAnimationsForTransform) {
                    transform = colladaRunningAnimation.getAnimatedTransform(transform);
                }
            }

            transform.applyTo(nodeMatrix);
        }
        nodeMatrix.leftMultiply(identity);

        ColladaMeshGeometry geometry = node.getGeometry();
        if (geometry != null) {
            HashMap<String, ColladaMaterial> materialMap = geometry.getMaterialMap();
            for (ColladaMeshElement colladaMeshElement : geometry.getMeshElements()) {
                if (colladaMeshElement instanceof ColladaCompositeMeshElement) {
                    renderCompositeMesh((ColladaCompositeMeshElement) colladaMeshElement, nodeMatrix, materialMap);
                } else {
                    renderMesh((ColladaSimpleMeshElement) colladaMeshElement, nodeMatrix, materialMap);
                }
            }
        }

        for (ColladaNode colladaNode : node.getChildren()) {
            renderNode(colladaNode, nodeMatrix);
        }
    }

    private void renderCompositeMesh(ColladaCompositeMeshElement colladaMeshElement, MatrixTransformation matrix, HashMap<String, ColladaMaterial> materialMap) {
        for (ColladaSimpleMeshElement colladaSimpleMeshElement : colladaMeshElement.getChildElements()) {
            renderMesh(colladaSimpleMeshElement, matrix, materialMap);
        }
    }

    private static final Vertex WORKING_VERTEX = new Vertex(0, 0, 0);

    private void renderMesh(ColladaSimpleMeshElement colladaMeshElement, MatrixTransformation matrix, HashMap<String, ColladaMaterial> materialMap) {
        startDrawing(colladaMeshElement.getType());
        String material = colladaMeshElement.getMaterialKey();
        if (currentMaterial == null || !currentMaterial.equals(material)) {
            ColladaMaterial material1 = materialMap.get(material);
            bindMaterial(material1);
            currentMaterial = material;
        }

        for (ColladaVertex vertex : colladaMeshElement.getVertices()) {
            WORKING_VERTEX.x = vertex.position.x;
            WORKING_VERTEX.y = vertex.position.y;
            WORKING_VERTEX.z = vertex.position.z;

            matrix.apply(WORKING_VERTEX);

            Tessellator.instance.setNormal(vertex.normal.x, vertex.normal.y, vertex.normal.z);
            Tessellator.instance.setTextureUV(vertex.texture.u, vertex.texture.v);
            Tessellator.instance.addVertex(WORKING_VERTEX.x, WORKING_VERTEX.y, WORKING_VERTEX.z);
        }
    }

    private void bindMaterial(ColladaMaterial material) {
        String texture = material.getDiffuseTexture();

        if (false) {
            ResourceLocation locationSkin = Minecraft.getMinecraft().thePlayer.getLocationSkin();
            Minecraft.getMinecraft().getTextureManager().bindTexture(locationSkin);
        } else {
            ResourceLocation resourceLocation = new ResourceLocation(TheMod.MOD_ID.toLowerCase(), "textures/models/" + texture);
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        }
    }

    private void startDrawing(MeshGeometryType type) {
        if (currentType != type) {
            if (currentType != null) {
                Tessellator.instance.draw();
            }

            if (type == MeshGeometryType.QUADS) {
                Tessellator.instance.startDrawing(GL11.GL_QUADS);
            } else if (type == MeshGeometryType.TRIANGLES) {
                Tessellator.instance.startDrawing(GL11.GL_TRIANGLES);
            }
            currentType = type;
        }
    }
}
