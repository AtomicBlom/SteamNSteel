package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import mod.steamnsteel.client.collada.ColladaArray;
import mod.steamnsteel.client.collada.IColladaInputStream;
import mod.steamnsteel.client.collada.IterableNodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Steven on 7/05/2015.
 */
public abstract class TransformerBase<T> implements Function<Node, T> {
    public final static XPathFactory xpathFactory = XPathFactory.newInstance();;
    private final static HashMap<String, XPathExpression> compiledExpressions = new HashMap<>();

    public static Node findNode(Node node, String xPathExpression) throws XPathExpressionException {
        XPathExpression expression = compiledExpressions.get(xPathExpression);
        if (expression == null) {
            expression = xpathFactory.newXPath().compile(xPathExpression);
            compiledExpressions.put(xPathExpression, expression);
        }

        return (Node) expression.evaluate(node, XPathConstants.NODE);
    }

    public static NodeList findNodes(Node node, String xPathExpression) throws XPathExpressionException {
        XPathExpression expression = compiledExpressions.get(xPathExpression);
        if (expression == null) {
            expression = xpathFactory.newXPath().compile(xPathExpression);
            compiledExpressions.put(xPathExpression, expression);
        }

        return (NodeList) expression.evaluate(node, XPathConstants.NODESET);
    }

    public static String getAttributeSafe(Node node, String key, String defaultValue) {
        Node namedItem = node.getAttributes().getNamedItem(key);
        if (namedItem == null) {
            return defaultValue;
        }
        return namedItem.getNodeValue();
    }

    public static String getAttributeSafe(Node node, String key) {
        Node namedItem = node.getAttributes().getNamedItem(key);
        if (namedItem == null) {
            return null;
        }
        return namedItem.getNodeValue();
    }

    protected ColladaArray getArray(Node sourceNode) {
        String type = sourceNode.getNodeName();
        return new ColladaArray(ColladaArray.Type.from(type), sourceNode.getFirstChild().getNodeValue());
    }

    protected LinkedList<IColladaInputStream> getInputs(Node node, Node parent, boolean ignoreElementsWithNoName) throws XPathExpressionException {
        NodeList inputNodes = findNodes(node, "./input");
        InputStreamTransformer transformer = new InputStreamTransformer(parent, ignoreElementsWithNoName);
        return Lists.newLinkedList(Iterables.transform(new IterableNodeList(inputNodes), transformer));
    }
}
