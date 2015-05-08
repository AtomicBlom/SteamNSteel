package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.IterableNodeList;
import mod.steamnsteel.client.collada.model.ColladaMaterial;
import mod.steamnsteel.client.collada.model.ColladaMeshGeometry;
import mod.steamnsteel.client.collada.model.ColladaNode;
import mod.steamnsteel.client.collada.model.transformation.TransformationBase;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import javax.xml.xpath.XPathExpressionException;

public class NodeTransformer extends TransformerBase<ColladaNode> {

    private final static Function<Node, TransformationBase> transformTransform = new TransformTransformer();
    private final static Function<Node, ColladaMeshGeometry> transformGeometry = new GeometryTransformer();
    private final Node root;

    public NodeTransformer(Node root) {
        this.root = root;
    }

    @Nullable
    @Override
    public ColladaNode apply(Node xmlNode) {
        try {
            String name = getAttributeSafe(xmlNode, "name");
            String sid = getAttributeSafe(xmlNode, "sid");
            ColladaNode newNode = new ColladaNode(name, sid);

            Function<Node, ColladaMaterial> transformMaterial = new MaterialTransformer(root);

            NodeList transformNodeList = findNodes(xmlNode, "./*[self::lookat or self::matrix or self::rotate or self::scale or self::skew or self::translate]");

            newNode.addTransformations(Iterables.transform(new IterableNodeList(transformNodeList), transformTransform));

            NodeList geometryNodeList = findNodes(xmlNode, "./instance_geometry");
            for (Node geometryInstanceNode : new IterableNodeList(geometryNodeList)) {
                ColladaMeshGeometry colladaMeshGeometry = transformGeometry.apply(geometryInstanceNode);

                Node instanceMaterialNode = findNode(geometryInstanceNode, "./bind_material/technique_common/instance_material");

                String symbol = getAttributeSafe(instanceMaterialNode, "symbol");
                String target = getAttributeSafe(instanceMaterialNode, "target");

                Node materialNode = findNode(root, "/COLLADA/library_materials/material[@id='" + target.substring(1) + "']");

                ColladaMaterial targetMaterial = transformMaterial.apply(materialNode);

                colladaMeshGeometry.addMaterialMap(symbol, targetMaterial);

                newNode.setGeometry(colladaMeshGeometry);
            }

            NodeList nodeNodeList = findNodes(xmlNode, "./node");

            newNode.addNodes(
                    Iterables.transform(new IterableNodeList(nodeNodeList), this)
            );

            return newNode;
        } catch (XPathExpressionException e) {
            throw new ColladaException("Error creating Node", e);
        }
    }
}
