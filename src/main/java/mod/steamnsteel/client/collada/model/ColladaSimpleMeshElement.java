package mod.steamnsteel.client.collada.model;

import java.util.LinkedList;
import java.util.List;

public class ColladaSimpleMeshElement extends ColladaMeshElement {
    private final MeshGeometryType type;
    private final List<ColladaVertex> vertices = new LinkedList<>();

    public ColladaSimpleMeshElement(MeshGeometryType type) {

        this.type = type;
    }

    public void addVertex(ColladaVertex vertex) {
        vertices.add(vertex);
    }

    public List<ColladaVertex> getVertices() {
        return vertices;
    }

    public MeshGeometryType getType() {
        return type;
    }
}
