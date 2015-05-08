package mod.steamnsteel.client.collada;

import com.google.common.collect.Iterables;
import mod.steamnsteel.client.collada.model.ColladaSampler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Steven on 7/05/2015.
 */
public class ColladaAnimation {
    private final String name;
    public final List<ColladaSampler> samplers = new ArrayList<>();
    private final List<ColladaAnimation> childAnimations = new LinkedList<>();

    public ColladaAnimation(String name) {
        this.name = name;
    }

    public void addSamplers(Iterable<ColladaSampler> transform) {
        for (ColladaSampler colladaSampler : transform) {
            samplers.add(colladaSampler);
        }
    }

    public void addChildAnimations(Iterable<ColladaAnimation> childAnimations) {
        Iterables.addAll(this.childAnimations, childAnimations);
    }

    public List<ColladaAnimation> getChildAnimations() {
        return childAnimations;
    }
}
