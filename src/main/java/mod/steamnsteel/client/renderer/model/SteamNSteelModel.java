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

import mod.steamnsteel.TheMod;
import mod.steamnsteel.utility.log.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.obj.*;
import org.lwjgl.opengl.GL11;

import javax.annotation.Resource;
import java.io.IOException;
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
    }

    public void render()
    {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        //model.renderAll();
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        for (GroupObject groupObject : model.groupObjects) {
            if (groupObject.name.equals("SSSpiderFactoryBody")) continue;
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

    public void tessellate(Tessellator tessellator) {
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        model.tessellateAll(tessellator);
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

            for (Face face : groupObject.faces) {
                for (TextureCoordinate textureCoordinate : face.textureCoordinates) {
                    textureCoordinate.u = minU + sizeU * textureCoordinate.u;
                    textureCoordinate.v = minV + sizeV * textureCoordinate.v;
                }
            }
        }
    }

    protected abstract HashMap<String,ResourceLocation> getGroupObjectTextures();
}
