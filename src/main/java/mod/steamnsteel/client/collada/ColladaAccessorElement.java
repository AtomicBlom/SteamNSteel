package mod.steamnsteel.client.collada;

/**
 * Created by Steven on 5/05/2015.
 */
public class ColladaAccessorElement {
    public final String value;
    public final String name;
    public final String type;

    public ColladaAccessorElement(String value, String name, String type) {
        this.value = value;

        this.name = name;
        this.type = type;
    }

    public float asFloat() {
        return Float.parseFloat(value);
    }
}
