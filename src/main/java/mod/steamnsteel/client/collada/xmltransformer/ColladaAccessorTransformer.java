package mod.steamnsteel.client.collada.xmltransformer;

import mod.steamnsteel.client.collada.ColladaAccessor;
import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.IterableNodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.LinkedList;

/**
 * Created by Steven on 5/05/2015.
 */
public class ColladaAccessorTransformer extends TransformerBase<ColladaAccessor> {
    private final Node scopeRoot;
    private final boolean ignoreElementsWithNoName;

    public ColladaAccessorTransformer(Node scopeRoot, boolean ignoreElementsWithNoName) {

        this.scopeRoot = scopeRoot;
        this.ignoreElementsWithNoName = ignoreElementsWithNoName;
    }

    @Nullable
    @Override
    public ColladaAccessor apply(Node source) {
        try {
            Node accessorNode = findNode(source, "./technique_common/accessor");

            int count = Integer.parseInt(getAttributeSafe(accessorNode, "count"));
            int offset = Integer.parseInt(getAttributeSafe(accessorNode, "offset", "0"));
            int stride = Integer.parseInt(getAttributeSafe(accessorNode, "stride", "1"));
            String sourceArrayId = getAttributeSafe(accessorNode, "source");
            NodeList paramsNodeList = findNodes(accessorNode, "./param");

            LinkedList<ColladaAccessor.Param> params = new LinkedList<>();
            for (Node xmlParam : new IterableNodeList(paramsNodeList)) {
                ColladaAccessor.Param param = new ColladaAccessor.Param(
                        getAttributeSafe(xmlParam, "name"),
                        getAttributeSafe(xmlParam, "type")
                );
                params.add(param);
            }

            Node sourceArrayNode = findNode(scopeRoot, ".//*[@id='" + sourceArrayId.substring(1) + "']");

            ColladaAccessor accessor = new ColladaAccessor(getArray(sourceArrayNode), count, offset, stride, params, ignoreElementsWithNoName);
            return accessor;

        } catch (XPathExpressionException e) {
            e.printStackTrace();
            throw new ColladaException("Error creating Accessor", e);
        }
    }

}
