package mod.steamnsteel.client.collada.model.transformation;

import mod.steamnsteel.client.collada.model.ColladaSampler;

public class TranslateTransform extends TransformationBase<Float> {
    private float x;
    private float y;
    private float z;

    public TranslateTransform(float x, float y, float z) {

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void applyTo(MatrixTransformation nodeMatrix) {
        nodeMatrix.translate(x, y, z);
    }

    @Override
    protected void setValueInternal(ColladaSampler sampler, Float o) {
        switch (sampler.targetField) {
            case "X":
                x = o;
                break;
            case "Y":
                y = o;
                break;
            case "Z":
                z = o;
                break;
        }
    }

    @Override
    public TransformationBase clone() {
        return new TranslateTransform(x, y, z);
    }
}
