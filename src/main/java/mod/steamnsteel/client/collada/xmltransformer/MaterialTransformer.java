package mod.steamnsteel.client.collada.xmltransformer;

import mod.steamnsteel.client.collada.ColladaException;
import mod.steamnsteel.client.collada.model.ColladaMaterial;
import org.w3c.dom.Node;

import javax.annotation.Nullable;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;

public class MaterialTransformer extends TransformerBase<ColladaMaterial> {
    private final Node root;
    public MaterialTransformer(Node root) {
        this.root = root;
    }

    @Nullable
    @Override
    public ColladaMaterial apply(@Nullable Node node) {
        try {
            Node instanceEffectNode = findNode(node, "./instance_effect");
            String url = getAttributeSafe(instanceEffectNode, "url");
            Node effectTechniqueNode = findNode(root, "/COLLADA/library_effects/effect[@id='" + url.substring(1) + "']/profile_COMMON/technique");
            Node shaderNode = findNode(effectTechniqueNode, "./*[self::blinn or self::lambert or self::phong]");

            ColladaMaterial material = new ColladaMaterial();

            switch (shaderNode.getNodeName()) {
                case "phong":
                    Node textureNode = findNode(shaderNode, "./diffuse/texture");

                    String textureId = getAttributeSafe(textureNode, "texture");
                    Node imageNode = findNode(textureNode, "/COLLADA/library_images/image[@id='" + textureId + "']/init_from");
                    String nodeValue = imageNode.getFirstChild().getNodeValue();

                    material.setDiffuseTexture(new File(nodeValue).getName());
            }
            return material;

        } catch (XPathExpressionException e) {
            throw new ColladaException("Error creating Material", e);
        }
    }
}
