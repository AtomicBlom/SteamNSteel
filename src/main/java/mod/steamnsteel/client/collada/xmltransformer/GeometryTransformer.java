package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.IterableNodeList;
import mod.steamnsteel.client.collada.model.ColladaMeshElement;
import mod.steamnsteel.client.collada.model.ColladaMeshGeometry;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;

/**
 * Created by Steven on 5/05/2015.
 */
public class GeometryTransformer extends TransformerBase<ColladaMeshGeometry> {

    private final static Function<Node, ColladaMeshElement> transformMesh = new MeshTransformer();

    @Override
    public ColladaMeshGeometry apply(Node xmlGeometryInstance) {
        try {
            String geometryName = getAttributeSafe(xmlGeometryInstance, "url");

            NodeList meshNode = findNodes(xmlGeometryInstance, "/COLLADA/library_geometries/geometry[@id='" + geometryName.substring(1) + "']/mesh");

            ColladaMeshGeometry geometry = new ColladaMeshGeometry();
            geometry.addMeshElements(
                    Iterables.transform(new IterableNodeList(meshNode), transformMesh)
            );
            return geometry;

        } catch (XPathExpressionException e) {
            throw new ColladaException("Error creating Geometry", e);
        }
    }
};