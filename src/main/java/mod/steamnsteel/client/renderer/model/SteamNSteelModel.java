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
import net.minecraftforge.client.model.obj.Face;
import net.minecraftforge.client.model.obj.GroupObject;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.WavefrontObject;
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
        model.renderAll();
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
        registeredIcons = new HashMap<ResourceLocation, IIcon>();

        for (Map.Entry<String, ResourceLocation> groupObjectTexture : groupObjectTextures.entrySet()) {
            if (!registeredIcons.containsKey(groupObjectTexture.getValue())) {
                registeredIcons.put(
                        groupObjectTexture.getValue(),
                        map.registerIcon(groupObjectTexture.getValue().getResourcePath())
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
