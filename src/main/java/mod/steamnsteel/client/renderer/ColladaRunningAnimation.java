package mod.steamnsteel.client.renderer;

import codechicken.lib.math.MathHelper;
import mod.steamnsteel.client.collada.ColladaAnimation;
import mod.steamnsteel.client.collada.model.ColladaSampler;
import mod.steamnsteel.client.collada.model.transformation.MatrixTransformation;
import mod.steamnsteel.client.collada.model.transformation.TransformationBase;
import net.minecraft.client.Minecraft;

import java.util.LinkedList;

/**
 * Created by Steven on 8/05/2015.
 */
public class ColladaRunningAnimation {
    private long lastUpdateTime;

    private LinkedList<SamplerInstances> samplerInstances = new LinkedList<>();

    public ColladaRunningAnimation(ColladaAnimation animation) {
        for (ColladaSampler sampler : animation.samplers) {
            samplerInstances.add(new SamplerInstances(sampler));
        }
    }

    public void updateSamplers() {
        long systemTime = Minecraft.getSystemTime();
        long timeDifference = systemTime - lastUpdateTime;

        double march = timeDifference / 1000.0;
        for (SamplerInstances samplerInstance : this.samplerInstances) {
            if (Float.isNaN(samplerInstance.currentTime)) {
                samplerInstance.currentTime = 0;
            }
            samplerInstance.currentTime += march;

            float maxTime = samplerInstance.sampler.getMaxTime();
            if (samplerInstance.currentTime > maxTime) { //MAX_ANIMATION TIME
                samplerInstance.currentTime %= maxTime;
            }
        }

        lastUpdateTime = systemTime;
    }

    public TransformationBase getAnimatedTransform(TransformationBase transform) {
        transform = transform.clone();
        for (SamplerInstances samplerInstance : samplerInstances) {
            Object o = samplerInstance.getValueForCurrentTime();
            transform.setValue(samplerInstance.sampler, o);
        }
        return transform;
    }

    private class SamplerInstances {
        public float currentTime;
        public ColladaSampler sampler;
        private Object valueForCurrentTime;

        public SamplerInstances(ColladaSampler sampler) {

            this.sampler = sampler;
        }

        public Object getValueForCurrentTime() {

            //currentTime = 0;
            ColladaSampler.Entry previous = null;
            ColladaSampler.Entry next = null;
            for (ColladaSampler.Entry samplerEntry : sampler.samplerEntries) {
                if (samplerEntry.timeStamp > currentTime) {
                    next = samplerEntry;
                    break;
                }
                previous = samplerEntry;
            }

            if (next == null && previous == null) {
                return null;
            }

            if (next == null) {
                //Animation has finished, previous should have the last value
                return previous.value;
            }



            double previousTimestamp = previous != null ? previous.timeStamp : 0;
            float nextValue = next.value;
            double previousValue = previous != null ? previous.value : nextValue;

            double midwaySection = currentTime - previousTimestamp;
            double percentage = midwaySection / (next.timeStamp - previousTimestamp);
            valueForCurrentTime = (float)MathHelper.interpolate(previousValue, nextValue, percentage);

            return valueForCurrentTime;
        }
    }
}
