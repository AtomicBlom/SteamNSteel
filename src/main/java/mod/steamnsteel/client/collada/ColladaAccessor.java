package mod.steamnsteel.client.collada;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Steven on 5/05/2015.
 */
public class ColladaAccessor {
    private final ColladaArray source;
    private final int count;
    private final int offset;
    private final int stride;
    private final List<Param> params;
    private final boolean ignoreElementsWithNoName;


    public ColladaAccessor(ColladaArray source, int count, int offset, int stride, List<Param> params, boolean ignoreElementsWithNoName) {
        this.source = source;
        this.count = count;
        this.offset = offset;
        this.stride = stride;
        this.params = params;
        this.ignoreElementsWithNoName = ignoreElementsWithNoName;
    }

    public List<ColladaAccessorElement> getElementsAtIndex(int index) {
        List<ColladaAccessorElement> elements = new ArrayList<>();
        int startPos = stride * index + offset;
        if (index >= count) {
                return null;
        }
        int pos = 0;
        for (Param p : params) {

            if ((p.getName() != null && !p.getName().equals("")) || !ignoreElementsWithNoName) {
                elements.add(createAccessorElement(startPos + pos, p));
            }
            pos++;
        }
        return elements;
    }

    private ColladaAccessorElement createAccessorElement(int currentPos, Param param) {
        return new ColladaAccessorElement(
                source.getSource()[currentPos],
                param.getName(),
                param.getType()
        );
    }

    public static class Param {

        private String name;
        private String type;

        public Param(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
