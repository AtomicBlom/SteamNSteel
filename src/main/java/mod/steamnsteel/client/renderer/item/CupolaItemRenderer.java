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

package mod.steamnsteel.client.renderer.item;

import com.google.common.base.Objects;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mod.steamnsteel.client.renderer.model.CupolaModel;
import mod.steamnsteel.client.renderer.tileentity.CupolaTESR;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class CupolaItemRenderer implements IItemRenderer
{
    private static final ImmutableTriple<Float, Float, Float> ENTITY_OFFSET = ImmutableTriple.of(0.0f, -1.0f, 0.0f);
    private static final ImmutableTriple<Float, Float, Float> EQUIPPED_OFFSET = ImmutableTriple.of(1.0f, 0.0f, 1.5f);
    private static final ImmutableTriple<Float, Float, Float> FIRST_PERSON_OFFSET = ImmutableTriple.of(-0.0f, 0.0f, 0.0f);
    private static final ImmutableTriple<Float, Float, Float> INVENTORY_OFFSET = ImmutableTriple.of(-0.0f, -1.0f, 0.0f);

    private static final ImmutableTriple<Float, Float, Float> SCALE = ImmutableTriple.of(0.666667f, 0.666667f, 0.666667f);

    private final CupolaModel model;

    public CupolaItemRenderer()
    {
        model = new CupolaModel();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        switch (type)
        {
            case ENTITY:
                renderCupola(ENTITY_OFFSET);
                break;
            case EQUIPPED:
                renderCupola(EQUIPPED_OFFSET);
                break;
            case EQUIPPED_FIRST_PERSON:
                renderCupola(FIRST_PERSON_OFFSET);
                break;
            case INVENTORY:
                renderCupola(INVENTORY_OFFSET);
                break;
            default:
        }
    }

    private void renderCupola(ImmutableTriple<Float, Float, Float> offset)
    {
        GL11.glPushMatrix();
        GL11.glScalef(SCALE.left, SCALE.middle, SCALE.right);
        GL11.glTranslatef(offset.left, offset.middle, offset.right);

        FMLClientHandler.instance().getClient().renderEngine.bindTexture(CupolaTESR.TEXTURE);

        model.render();

        GL11.glPopMatrix();
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .add("model", model)
                .toString();
    }
}
