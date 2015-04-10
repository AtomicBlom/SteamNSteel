/*
 * Copyright (c) 2014 Rosie Alexander and Scott Killen.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses>.
 */

package mod.steamnsteel.client.renderer.model;

import mod.steamnsteel.utility.log.Logger;
import mod.steamnsteel.utility.math.Matrix4;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UtilityClass")
abstract class SteamNSteelModel
{
    protected final String DEFAULT_TEXTURE = "";
    private HashMap<ResourceLocation, IIcon> registeredIcons;

    protected SteamNSteelModel(ResourceLocation resourceLocation) {

        this.resourceLocation = resourceLocation;
    }

    protected WavefrontObject model;
    private ResourceLocation resourceLocation;

    public void reload() {
        Logger.info("Loading Model from %s", resourceLocation);
        model = (WavefrontObject) AdvancedModelLoader.loadModel(resourceLocation);
        preparePasses();
    }

    protected void preparePasses() {}

    public void render()
    {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        for (GroupObject groupObject : model.groupObjects) {
            for (Face face : groupObject.faces) {
                if (face.faceNormal == null)
                {
                    face.faceNormal = face.calculateFaceNormal();
                }

                tessellator.setNormal(face.faceNormal.x, face.faceNormal.y, face.faceNormal.z);

                for (int i = 0; i < face.vertices.length; ++i)
                {
                    Vertex vertex = face.vertices[i];
                    if ((face.textureCoordinates != null) && (face.textureCoordinates.length > 0))
                    {
                        TextureCoordinate textureCoordinate = face.textureCoordinates[i];
                        tessellator.addVertexWithUV(vertex.x, vertex.y, vertex.z, textureCoordinate.u, textureCoordinate.v);

                    }
                    else
                    {
                        tessellator.addVertex(vertex.x, vertex.y, vertex.z);
                    }

                }
            }
        }
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
    }

    public void tessellate(Tessellator tessellator, Matrix4 m, int pass) {
        if (pass == 1) {
            tessellate(tessellator, m, model.groupObjects);
        }
    }

    public void tessellate(Tessellator tessellator, Matrix4 m, ArrayList<GroupObject> groupObjects) {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        Vertex vertexCopy = new Vertex(0,0,0);
        for (GroupObject groupObject : groupObjects) {
            for (Face face : groupObject.faces) {
                if (face.faceNormal == null)
                {
                    face.faceNormal = face.calculateFaceNormal();
                }

                tessellator.setNormal(face.faceNormal.x, face.faceNormal.y, face.faceNormal.z);

                for (int i = 0; i < face.vertices.length; ++i)
                {
                    Vertex vertex = face.vertices[i];
                    vertexCopy.x = vertex.x;
                    vertexCopy.y = vertex.y;
                    vertexCopy.z = vertex.z;

                    m.apply(vertexCopy);

                    if ((face.textureCoordinates != null) && (face.textureCoordinates.length > 0))
                    {
                        TextureCoordinate textureCoordinate = face.textureCoordinates[i];
                        tessellator.addVertexWithUV(vertexCopy.x, vertexCopy.y, vertexCopy.z, textureCoordinate.u, textureCoordinate.v);

                    }
                    else
                    {
                        tessellator.addVertex(vertexCopy.x, vertexCopy.y, vertexCopy.z);
                    }
                }
            }
        }
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void registerTextures(TextureMap map) {
        HashMap<String, ResourceLocation> groupObjectTextures = getGroupObjectTextures();
        if (groupObjectTextures == null) return;
        registeredIcons = new HashMap<>();

        for (Map.Entry<String, ResourceLocation> groupObjectTexture : groupObjectTextures.entrySet()) {
            ResourceLocation resource = groupObjectTexture.getValue();
            if (!registeredIcons.containsKey(resource)) {
                registeredIcons.put(
                        resource,
                        map.registerIcon(resource.getResourceDomain() + ":" + resource.getResourcePath())
                );
            }
        }
    }

    public void adjustTextureCoordinates() {
        HashMap<String, ResourceLocation> groupObjectTextures = getGroupObjectTextures();
        if (groupObjectTextures == null) return;

        for (GroupObject groupObject : model.groupObjects) {
            ResourceLocation textureName = groupObjectTextures.get(groupObject.name);
            if (textureName == null) {
                textureName = groupObjectTextures.get("");
            }

            IIcon icon = registeredIcons.get(textureName);
            if (icon == null) continue;

            float minU = icon.getMinU();
            float sizeU = icon.getMaxU() - minU;
            float minV = icon.getMinV();
            float sizeV = icon.getMaxV() - minV;

            int j = 0;
            for (Face face : groupObject.faces) {
                for (int i = 0; i < face.vertices.length; ++i) {
                    TextureCoordinate textureCoordinate = face.textureCoordinates[i];
                    face.textureCoordinates[i] = new TextureCoordinate(
                            minU + sizeU * textureCoordinate.u,
                            minV + sizeV * textureCoordinate.v
                    );
                }
            }
        }
    }

    protected abstract HashMap<String,ResourceLocation> getGroupObjectTextures();
}
