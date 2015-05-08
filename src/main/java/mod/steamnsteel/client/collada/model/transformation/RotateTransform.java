package mod.steamnsteel.client.collada.model.transformation;

import codechicken.lib.math.MathHelper;
import mod.steamnsteel.client.collada.model.ColladaSampler;

/**
 * Created by Steven on 8/05/2015.
 */
public class RotateTransform extends TransformationBase<Float> {
    private float angle;
    private float x;
    private float y;
    private float z;

    public RotateTransform(float angle, float x, float y, float z) {

        this.angle = angle;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    @Override
    public void applyTo(MatrixTransformation nodeMatrix) {
        nodeMatrix.rotate(Math.toRadians(angle), x, y, z);
    }

    @Override
    protected void setValueInternal(ColladaSampler sampler, Float o) {
        switch (sampler.targetField) {
            case "ANGLE":
                angle = o;
                break;
        }
    }

    @Override
    public TransformationBase clone() {
        return new RotateTransform(angle, x, y, z);
    }
}
