package mod.steamnsteel.client.collada.model;

import com.google.common.collect.Iterables;
import mod.steamnsteel.client.collada.ColladaAnimation;

import java.util.ArrayList;
import java.util.List;

public class ColladaModel {
    private final List<ColladaScene> scenes = new ArrayList<>();

    public List<ColladaAnimation> getAnimations() {
        return animations;
    }

    private final List<ColladaAnimation> animations = new ArrayList<>();

    public void addScenes(Iterable<ColladaScene> scenes) {
        Iterables.addAll(this.scenes, scenes);
    }

    public List<ColladaScene> getScenes() {
        return scenes;
    }

    public void addAnimations(Iterable<ColladaAnimation> animations) {
        Iterables.addAll(this.animations, animations);
    }
}


