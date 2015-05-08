package mod.steamnsteel.client.collada.model;

import com.google.common.base.Objects;
import mod.steamnsteel.client.collada.IPopulatable;
import net.minecraftforge.client.model.obj.TextureCoordinate;
import net.minecraftforge.client.model.obj.Vertex;

/**
 * Created by Steven on 13/04/2015.
 */
public class ColladaVertex implements IPopulatable {
    public final Vertex position;
    public final Vertex normal;
    public final TextureCoordinate texture;

    public ColladaVertex() {
        position = new Vertex(0, 0, 0);
        normal = new Vertex(0, 0, 0);
        texture = new TextureCoordinate(0, 0, 0);
    }

    @Override
    public String toString() {

        return Objects.toStringHelper(this)
                .add("position",
                        Objects.toStringHelper(position)
                                .add("x", position.x)
                                .add("y", position.y)
                                .add("z", position.z)
                )
                .add("normal",
                        Objects.toStringHelper(normal)
                                .add("x", normal.x)
                                .add("y", normal.y)
                                .add("z", normal.z)
                )
                .add("texture",
                        Objects.toStringHelper(texture)
                                .add("u", texture.u)
                                .add("v", texture.v)
                                .add("w", texture.w)
                )
                .toString();
    }
}
