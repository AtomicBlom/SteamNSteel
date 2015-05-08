package mod.steamnsteel.client.collada;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import mod.steamnsteel.client.collada.model.ColladaModel;
import mod.steamnsteel.client.collada.model.ColladaNode;
import mod.steamnsteel.client.collada.model.ColladaSampler;
import mod.steamnsteel.client.collada.model.ColladaScene;
import mod.steamnsteel.client.collada.model.transformation.TransformationBase;
import mod.steamnsteel.client.collada.xmltransformer.AnimationTransformer;
import mod.steamnsteel.client.collada.xmltransformer.TransformerBase;
import mod.steamnsteel.client.collada.xmltransformer.VisualSceneTransformer;
import mod.steamnsteel.utility.log.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by codew on 2/05/2015.
 */
public class ColladaModelReader {

    private Document doc;

    private ColladaModelReader() {
    }

    public static ColladaModel read(ResourceLocation resourceLocation) throws IOException, ColladaException {
        IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation);
        return read(res.getInputStream());
    }

    public static ColladaModel read(File file) throws IOException, ColladaException {
        Logger.info("Loading %s", file.getName());
        return read(new FileInputStream(file));
    }

    public static ColladaModel read(InputStream file) throws IOException, ColladaException {

        ColladaModelReader modelReader = new ColladaModelReader();
        try {
            return modelReader.readInternal(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ColladaException("Error Reading Collada File", e);
        }
    }

    private ColladaModel readInternal(InputStream file) throws IOException, ColladaException, ParserConfigurationException, SAXException, XPathExpressionException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.doc = builder.parse(file);

        return parseColladaXml();
    }

    private ColladaModel parseColladaXml() throws ColladaException, XPathExpressionException {
        ColladaModel result = new ColladaModel();

        NodeList evaluate = TransformerBase.findNodes(doc, "/COLLADA/library_visual_scenes/visual_scene");
        Function<Node, ColladaScene> transformVisualScene = new VisualSceneTransformer(doc);

        result.addScenes(
                Iterables.transform(new IterableNodeList(evaluate), transformVisualScene)
        );

        NodeList animationNodeList = TransformerBase.findNodes(doc, "/COLLADA/library_animations/animation");

        int length = animationNodeList.getLength();

        result.addAnimations(Iterables.transform(new IterableNodeList(animationNodeList), new AnimationTransformer()));

        HashMap<String, List<ColladaSampler>> allAnimations = new HashMap<>();
        Stack<ColladaAnimation> animationStack = new Stack<>();
        animationStack.addAll(result.getAnimations());

        //Catalog the samplers
        while (!animationStack.empty()) {
            ColladaAnimation animation = animationStack.pop();
            if (animation == null) continue;
            for (ColladaSampler sampler : animation.samplers) {
                List<ColladaSampler> samplers = allAnimations.get(sampler.targetTransform);
                if (samplers == null) {
                    samplers = new LinkedList<>();

                    allAnimations.put(sampler.targetTransform, samplers);
                }
                samplers.add(sampler);
            }
            animationStack.addAll(animation.getChildAnimations());
        }

        Stack<ColladaNode> nodeStack = new Stack<>();

        for (ColladaScene colladaScene : result.getScenes()) {
            for (ColladaNode colladaNode : colladaScene.getNodes()) {
                nodeStack.add(colladaNode);
            }
        }
        //Correlate animation sampler to a transform.
        while (!nodeStack.empty()) {
            ColladaNode nodeEntry = nodeStack.pop();
            if (nodeEntry.getSid() == null) { continue; }

            for (TransformationBase transform : nodeEntry.getTransformations()) {
                if (transform.getSid() == null) { continue; }
                String searchPath = nodeEntry.getSid() + "/" + transform.getSid();
                if (allAnimations.containsKey(searchPath)) {
                    List<ColladaSampler> colladaSamplers = allAnimations.get(searchPath);

                    for (ColladaSampler colladaSampler : colladaSamplers) {
                        Logger.info("Found transform for %s.%s", searchPath, colladaSampler.targetField);
                    }
                }
            }

            for (ColladaNode colladaNode : nodeEntry.getChildren()) {
                nodeStack.add(colladaNode);
            }
        }

        return result;
    }
}
