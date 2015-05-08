package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import mod.steamnsteel.client.collada.ColladaAnimation;
import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.IterableNodeList;
import mod.steamnsteel.client.collada.model.ColladaSampler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nullable;
import javax.xml.xpath.XPathExpressionException;

/**
 * Created by Steven on 7/05/2015.
 */
public class AnimationTransformer extends TransformerBase<ColladaAnimation> {
    @Nullable
    @Override
    public ColladaAnimation apply(@Nullable Node node) {
        try {
            ColladaAnimation animation = new ColladaAnimation(getAttributeSafe(node, "name"));
            NodeList childAnimationNodes = findNodes(node, "./animation");
            animation.addChildAnimations(Iterables.transform(new IterableNodeList(childAnimationNodes), this));

            Node channelNode = findNode(node, "./channel");
            if (channelNode == null) {
                //not sure what to do here to be honest.
                return animation;
            }
            String target = getAttributeSafe(channelNode, "target");

            SamplerTransformer transformer = new SamplerTransformer(target, node);

            NodeList samplerNodes = findNodes(node, "./sampler[@id='" + getAttributeSafe(channelNode, "source").substring(1) + "']");
            animation.addSamplers(
                Iterables.transform(new IterableNodeList(samplerNodes), transformer)
            );

            return animation;
        } catch (XPathExpressionException e) {

            e.printStackTrace();
            throw new ColladaException("Could not create Animation", e);
        }
    }
}
