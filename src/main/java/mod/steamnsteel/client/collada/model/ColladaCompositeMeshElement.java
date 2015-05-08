package mod.steamnsteel.client.collada.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Steven on 27/04/2015.
 */
public class ColladaCompositeMeshElement extends ColladaMeshElement {

    private List<ColladaSimpleMeshElement> simpleMeshElements = new ArrayList<>();

    public void addChildElements(Collection<ColladaSimpleMeshElement> simpleMeshElements) {

        this.simpleMeshElements.addAll(simpleMeshElements);
    }

    public List<ColladaSimpleMeshElement> getChildElements() {
        return simpleMeshElements;
    }
}
