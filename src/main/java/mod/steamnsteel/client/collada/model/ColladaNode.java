package mod.steamnsteel.client.collada.model;

import com.google.common.collect.Iterables;
import mod.steamnsteel.client.collada.model.transformation.TransformationBase;
import mod.steamnsteel.client.collada.model.transformation.MatrixTransformation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven on 30/04/2015.
 */
public class ColladaNode {
    //private final MatrixTransformation matrix;
    private final String name;
    private final String sid;
    private ColladaMeshGeometry geometry;

    private final LinkedList<TransformationBase> transformations = new LinkedList<>();

    public ColladaNode(String name, String sid) {
        this.name = name;
        this.sid = sid;
        //this.matrix = new MatrixTransformation();
    }

    LinkedList<ColladaNode> children = new LinkedList<>();
    private List<String> layers;

    public void addNodes(Iterable<ColladaNode> children) {
        Iterables.addAll(this.children, children);
    }

    public void setLayers(List<String> layers) {
        this.layers = layers;
    }

    public List<String> getLayers() {
        return layers;
    }

    public void setGeometry(ColladaMeshGeometry geometry) {
        this.geometry = geometry;
    }

    public ColladaMeshGeometry getGeometry() {
        return geometry;
    }

    public LinkedList<ColladaNode> getChildren() {
        return children;
    }

    public void addTransformations(Iterable<TransformationBase> transformations) {
        Iterables.addAll(this.transformations, transformations);
    }

    public Iterable<TransformationBase> getTransformations() {
        return transformations;
    }

    public String getSid() {
        return sid;
    }
}
