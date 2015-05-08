package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.IColladaInputStream;
import mod.steamnsteel.client.collada.IterableNodeList;
import mod.steamnsteel.client.collada.model.*;
import mod.steamnsteel.utility.log.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Steven on 5/05/2015.
 */
public class MeshTransformer extends TransformerBase<ColladaMeshElement> {
    @Override
    public ColladaMeshElement apply(Node xmlMeshElement) {
        try {
            Logger.info("    Geometry %s", getAttributeSafe(xmlMeshElement, "name"));

            String xpath = "./*[self::lines or self::linestrips or self::polygons or self::polylist or self::triangles or self::trifans or self::tristrips]";
            NodeList meshGeometryNodes = findNodes(xmlMeshElement, xpath);


            for (Node node : new IterableNodeList(meshGeometryNodes)) {
                ColladaMeshElement meshElement;
                switch (node.getNodeName()) {
                    case "polylist":
                        meshElement = processPolyList(node, xmlMeshElement);
                        break;
                    case "triangles":
                        meshElement = processTriangles(node, xmlMeshElement);
                        break;
                    default:
                        throw new ColladaException(node.getNodeName() + " is not implemented as a mesh type");
                }

                meshElement.setMaterialKey(getAttributeSafe(node, "material"));

                return meshElement;
            }

            throw new ColladaException("No Mesh found");
        } catch (XPathExpressionException e) {
            throw new ColladaException("Error creating Mesh", e);
        }
    }

    private ColladaMeshElement processTriangles(Node node, Node parent) {
        try {
            ColladaSimpleMeshElement meshElement = new ColladaSimpleMeshElement(MeshGeometryType.TRIANGLES);

            Node pNode = findNode(node, "./p");

            LinkedList<IColladaInputStream> inputs = getInputs(node, parent, true);
            int indexCount = 0;
            for (Integer index : getArray(pNode).asIntArray()) {
                if (indexCount % 3 == 0) {
                    Logger.info("        Triangle %d", indexCount / 3);
                }
                ColladaVertex vertex = new ColladaVertex();
                for (IColladaInputStream input : inputs) {
                    input.applyElementsAtIndex(index, vertex);
                }
                Logger.info("          vertex (%d/3) - %s", indexCount + 1, vertex);
                meshElement.addVertex(vertex);
                indexCount++;
            }

            return meshElement;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }

        return null;

    }

    private ColladaMeshElement processPolyList(Node node, Node parent) {
        try {
            NamedNodeMap attributes = node.getAttributes();
            int count = Integer.parseInt(attributes.getNamedItem("count").getNodeValue());
            Node materialAttribute = attributes.getNamedItem("material");

            if (materialAttribute != null) {
                //TODO: Resolve material.
            }

            Node vcountNodes = findNode(node, "./vcount");
            Node pNode = findNode(node, "./p");

            LinkedList<IColladaInputStream> inputs = getInputs(node, parent, true);

            HashMap<Integer, ColladaSimpleMeshElement> meshElements = new HashMap<>();
            meshElements.put(3, new ColladaSimpleMeshElement(MeshGeometryType.TRIANGLES));
            meshElements.put(4, new ColladaSimpleMeshElement(MeshGeometryType.QUADS));
            int polygonCount = 0;

            int[] pValues = getArray(pNode).asIntArray();
            int currentPValue = 0;
            for (int vCount : getArray(vcountNodes).asIntArray()) {
                Logger.info("        Polygon %d", polygonCount);
                ColladaSimpleMeshElement meshElement = meshElements.get(vCount);
                if (meshElement == null) {
                    throw new ColladaException("PolyList does not support a vCount of " + vCount);
                }
                for (int i = 0; i < vCount; ++i) {
                    ColladaVertex vertex = new ColladaVertex();
                    StringBuilder readInputIndexes = new StringBuilder();

                    for (IColladaInputStream input : inputs) {
                        Integer index = pValues[currentPValue++];
                        readInputIndexes.append(index).append(" ");
                        input.applyElementsAtIndex(index, vertex);
                    }
                    Logger.info("          vertex (%d/%d) (%s) - %s", i + 1, vCount, readInputIndexes, vertex);
                    meshElement.addVertex(vertex);
                }
                polygonCount++;
            }

            //If there are no Quads
            if (meshElements.get(4).getVertices().size() == 0) {
                Logger.info("        Simplifying Mesh from PolyList to Triangles");
                return meshElements.get(3);
                //If there are no triangles
            } else if (meshElements.get(3).getVertices().size() == 0) {
                Logger.info("        Simplifying Mesh from PolyList to Quads");
                return meshElements.get(4);
            } else {
                ColladaCompositeMeshElement compositeMeshElement = new ColladaCompositeMeshElement();
                compositeMeshElement.addChildElements(meshElements.values());
                return compositeMeshElement;
            }

        } catch (XPathExpressionException e) {
            throw new ColladaException("Error creating Mesh", e);
        }
    }

};