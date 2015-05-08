package mod.steamnsteel.client.collada.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by codew on 1/05/2015.
 */
public class ColladaScene {
    private final String name;
    private final List<ColladaNode> nodes = new LinkedList<>();

    public ColladaScene(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addNode(ColladaNode node) {
        nodes.add(node);
    }

    public List<ColladaNode> getNodes() {
        return nodes;
    }

    public void addNodes(Iterable<ColladaNode> nodes) {
        for (ColladaNode node : nodes) {
            if (node.getGeometry() != null || node.children.size() > 0) {
                this.nodes.add(node);
            }
        }
    }
}
