package mod.steamnsteel.client.renderer;

import mod.steamnsteel.client.collada.ColladaAnimation;
import mod.steamnsteel.client.collada.model.ColladaModel;
import mod.steamnsteel.client.collada.model.ColladaSampler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven on 8/05/2015.
 */
public class ColladaModelInstance {

    public ColladaModelInstance(ColladaModel model) {
        this.model = model;
    }

    private ColladaModel model;

    public ColladaModel getModel() {
        return model;
    }

    public void setModel(ColladaModel model) {
        this.model = model;
    }

    HashMap<String, List<ColladaRunningAnimation>> animationsPerTarget = new HashMap<>();

    LinkedList<ColladaRunningAnimation> emptyList = new LinkedList<>();

    public Iterable<ColladaRunningAnimation> getRunningAnimationsForTransform(String path) {
        Iterable<ColladaRunningAnimation> animationList = animationsPerTarget.get(path);
        if (animationList == null) return emptyList;
        return animationList;
    }

    final LinkedList<ColladaRunningAnimation> runningAnimations = new LinkedList<>();

    public void runAllAnimations() {
        for (ColladaAnimation colladaAnimation : model.getAnimations()) {
            ColladaRunningAnimation e = new ColladaRunningAnimation(colladaAnimation);
            for (ColladaSampler sampler : colladaAnimation.samplers) {
                List<ColladaRunningAnimation> animations = animationsPerTarget.get(sampler.targetTransform);
                if (animations == null) {
                    animations = new LinkedList<>();
                    animationsPerTarget.put(sampler.targetTransform, animations);
                }
                animations.add(e);
            }

            runningAnimations.add(e);
        }
    }

    public void updateRunningAnimations() {
        for (ColladaRunningAnimation runningAnimation : runningAnimations) {
            runningAnimation.updateSamplers();
        }
    }
}
