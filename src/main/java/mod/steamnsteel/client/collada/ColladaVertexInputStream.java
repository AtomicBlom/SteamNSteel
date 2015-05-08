package mod.steamnsteel.client.collada;

import mod.steamnsteel.client.collada.model.ColladaVertex;

import java.util.LinkedList;

/**
 * Created by Steven on 5/05/2015.
 */
public class ColladaVertexInputStream implements IColladaInputStream {
    private final LinkedList<IColladaInputStream> childStreams;
    private final String semantic;
    private final int offset;

    public ColladaVertexInputStream(LinkedList<IColladaInputStream> childStreams, String semantic, int offset) {
        this.childStreams = childStreams;
        this.semantic = semantic;
        this.offset = offset;
    }

    @Override
    public boolean applyElementsAtIndex(int index, IPopulatable vertex) {
        boolean result = true;
        for (IColladaInputStream childStream : childStreams) {
            result &= childStream.applyElementsAtIndex(index, vertex);
        }
        return result;
    }
}
