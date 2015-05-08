package mod.steamnsteel.client.collada.model;

import java.util.HashMap;

/**
 * Created by Steven on 5/05/2015.
 */
public abstract class ColladaGeometry {

    HashMap<String, ColladaMaterial> mappedMaterials = new HashMap<>();

    public void addMaterialMap(String symbol, ColladaMaterial targetMaterial) {
        mappedMaterials.put(symbol, targetMaterial);
    }

    public HashMap<String, ColladaMaterial> getMaterialMap() {
        return mappedMaterials;
    }
}
