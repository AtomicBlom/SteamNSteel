package mod.steamnsteel.client.collada.xmltransformer;

import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.IColladaInputStream;
import mod.steamnsteel.client.collada.IterableNodeList;
import mod.steamnsteel.client.collada.model.ColladaSampler;
import org.w3c.dom.Node;

import javax.annotation.Nullable;
import javax.xml.xpath.XPathExpressionException;
import java.util.LinkedList;

/**
 * Created by Steven on 7/05/2015.
 */
public class SamplerTransformer extends TransformerBase<ColladaSampler> {
    private final String target;
    private final Node root;

    public SamplerTransformer(String target, Node root) {
        this.target = target;

        this.root = root;
    }

    @Nullable
    @Override
    public ColladaSampler apply(@Nullable Node node) {
        try {
            LinkedList<IColladaInputStream> inputs = getInputs(node, root, false);

            ColladaSampler sampler = new ColladaSampler(target);

            int index = 0;
            boolean wasValid = true;
            while (wasValid) {
                ColladaSampler.Entry entry = sampler.createEntry();
                for (IColladaInputStream input : inputs) {
                    wasValid &= input.applyElementsAtIndex(index, entry);
                }
                index++;
                if (wasValid) {
                    sampler.addEntry(entry);
                }
            }
            return sampler;
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            throw new ColladaException("Unable to create Sampler", e);
        }
    }
}
