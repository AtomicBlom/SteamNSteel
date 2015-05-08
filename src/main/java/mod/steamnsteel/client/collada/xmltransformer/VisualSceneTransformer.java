package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.IterableNodeList;
import mod.steamnsteel.client.collada.model.ColladaNode;
import mod.steamnsteel.client.collada.model.ColladaScene;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import javax.xml.xpath.XPathExpressionException;

public class VisualSceneTransformer extends TransformerBase<ColladaScene> {

    private final Node root;

    public VisualSceneTransformer(Node root) {
        this.root = root;
    }

    @Nullable
    @Override
    public ColladaScene apply(Node xmlVisualScene) {
        try {
            String name = getAttributeSafe(xmlVisualScene, "name");
            ColladaScene newScene = new ColladaScene(name);

            Function<Node, ColladaNode> transformNode = new NodeTransformer(root);

            NodeList nodeList = findNodes(xmlVisualScene, "./node");

            newScene.addNodes(
                    Iterables.transform(new IterableNodeList(nodeList), transformNode)
            );

            return newScene;
        } catch (XPathExpressionException e) {
            throw new ColladaException("Error creating Visual Scene", e);
        }
    }
}