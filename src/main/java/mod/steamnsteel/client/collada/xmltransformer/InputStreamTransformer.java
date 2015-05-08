package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import mod.steamnsteel.client.collada.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * Created by Steven on 5/05/2015.
 */
public class InputStreamTransformer extends TransformerBase<IColladaInputStream> {

    private final Node scopeRoot;
    private final boolean ignoreElementsWithNoName;

    public InputStreamTransformer(Node scopeRoot, boolean ignoreElementsWithNoName) {

        this.scopeRoot = scopeRoot;
        this.ignoreElementsWithNoName = ignoreElementsWithNoName;
    }

    @Nullable
    @Override
    public IColladaInputStream apply(Node input) {
        try {
            String semantic = getAttributeSafe(input, "semantic");
            int offset = Integer.parseInt(getAttributeSafe(input, "offset", "0"));
            String source = getAttributeSafe(input, "source");

            Node sourceNode = findNode(scopeRoot, ".//*[@id='" + source.substring(1) + "']");

            IColladaInputStream inputStream;
            if (!"vertices".equals(sourceNode.getNodeName())) {
                ColladaAccessorTransformer accessorTransformer = new ColladaAccessorTransformer(scopeRoot, ignoreElementsWithNoName);
                ColladaAccessor accessor = accessorTransformer.apply(sourceNode);

                inputStream = new ColladaInputStream(accessor, semantic, offset);

            } else {
                NodeList inputNodeList = findNodes(sourceNode, "./input");

                LinkedList<IColladaInputStream> childStreams = Lists.newLinkedList(
                        Iterables.transform(new IterableNodeList(inputNodeList), this)
                );

                inputStream = new ColladaVertexInputStream(childStreams, semantic, offset);
            }
            return inputStream;

        } catch (Exception e) {
            throw new ColladaException("Error creating InputStream", e);
        }
    }
}