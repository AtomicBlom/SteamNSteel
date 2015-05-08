package mod.steamnsteel.client.collada.model;

public abstract class ColladaMeshElement {
    private String materialKey;

    public void setMaterialKey(String materialKey) {
        this.materialKey = materialKey;
    }

    public String getMaterialKey() {
        return materialKey;
    }
}

