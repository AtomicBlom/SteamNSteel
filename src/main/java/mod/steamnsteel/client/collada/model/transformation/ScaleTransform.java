package mod.steamnsteel.client.collada.model.transformation;

import mod.steamnsteel.client.collada.model.ColladaSampler;

/**
 * Created by Steven on 8/05/2015.
 */
public class ScaleTransform extends TransformationBase<Float> {
    private float x;
    private float y;
    private float z;

    public ScaleTransform(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void applyTo(MatrixTransformation nodeMatrix) {
        nodeMatrix.scale(x, y, z);
    }

    @Override
    protected void setValueInternal(ColladaSampler sampler, Float o) {
        switch (sampler.targetField) {
            case "X":
                x = o;
                break;
            case "Y":
                y = 0;
                break;
            case "Z":
                z = 0;
                break;
        }
    }

    @Override
    public TransformationBase clone() {
        return new ScaleTransform(x, y, z);
    }
}
