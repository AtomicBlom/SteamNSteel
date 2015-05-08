package mod.steamnsteel.client.collada.model.transformation;

import codechicken.lib.util.Copyable;
import mod.steamnsteel.client.collada.model.ColladaSampler;

/**
 * Created by Steven on 8/05/2015.
 */
public abstract class TransformationBase<T> {
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    String sid;

    abstract public void applyTo(MatrixTransformation nodeMatrix);

    public final void setValue(ColladaSampler sampler, Object o) {
        this.setValueInternal(sampler, (T)o);
    }

    protected abstract void setValueInternal(ColladaSampler sampler, T o);

    public abstract TransformationBase clone();
}
