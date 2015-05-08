package mod.steamnsteel.client.collada.xmltransformer;

import mod.steamnsteel.client.collada.ColladaArray;
import mod.steamnsteel.client.collada.model.transformation.*;
import org.w3c.dom.Node;

import javax.annotation.Nullable;

public class TransformTransformer extends TransformerBase<TransformationBase> {
    @Nullable
    @Override
    public TransformationBase apply(Node xmlTransform) {
        String nodeName = xmlTransform.getNodeName();
        String nodeSid = getAttributeSafe(xmlTransform, "sid");
        ColladaArray colladaArray = getArray(xmlTransform);

        TransformationBase transformation;

        switch (nodeName) {
            case "rotate":
                transformation = readRotateTransform(colladaArray);
                break;
            case "matrix":
                transformation = readMatrixTransform(colladaArray);
                break;
            case "scale":
                transformation = readScaleTransform(colladaArray);
                break;
            case "translate":
                transformation = readTranslateTransform(colladaArray);
                break;
            case "lookat":
            case "skew":
            default:
                //FIXME: Unsupported
                transformation = MatrixTransformation.getIdentity();
                break;
        }

        transformation.setSid(nodeSid);
        return transformation;
    }

    private TransformationBase readMatrixTransform(ColladaArray xmlTransform) {
        return xmlTransform.asMatrixArray()[0];
    }

    private TransformationBase readScaleTransform(ColladaArray xmlTransform) {
        float[] floats = xmlTransform.asFloatArray();
        return new ScaleTransform(
                floats[0],
                floats[1],
                floats[2]
        );
    }

    private TransformationBase readRotateTransform(ColladaArray xmlTransform) {
        float[] floats = xmlTransform.asFloatArray();
        return new RotateTransform(
                floats[3],
                floats[0],
                floats[1],
                floats[2]
        );
    }

    private TransformationBase readTranslateTransform(ColladaArray xmlTransform) {
        float[] floats = xmlTransform.asFloatArray();
        return new TranslateTransform(
                floats[0],
                floats[1],
                floats[2]
        );
    }
}