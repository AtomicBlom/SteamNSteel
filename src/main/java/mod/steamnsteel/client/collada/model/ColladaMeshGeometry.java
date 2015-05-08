package mod.steamnsteel.client.collada.model;

import java.util.ArrayList;
import java.util.List;

public class ColladaMeshGeometry extends ColladaGeometry {

    private List<ColladaMeshElement> meshElements = new ArrayList<>();

    public void addMeshElements(Iterable<ColladaMeshElement> colladaMeshElements) {

        for (ColladaMeshElement colladaMeshElement : colladaMeshElements) {
            this.meshElements.add(colladaMeshElement);
        }

    }

    public List<ColladaMeshElement> getMeshElements() {
        return meshElements;
    }
}

