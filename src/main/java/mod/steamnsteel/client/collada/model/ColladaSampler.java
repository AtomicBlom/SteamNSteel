package mod.steamnsteel.client.collada.model;

import mod.steamnsteel.client.collada.IPopulatable;

import java.util.LinkedList;

public class ColladaSampler {
    public final String targetTransform;
    public final String targetField;
    public final LinkedList<Entry> samplerEntries = new LinkedList<>();

    public float getMaxTime() {
        return samplerEntries.getLast().timeStamp;
    }

    public ColladaSampler(String target) {


        this.targetTransform = target.substring(0, target.lastIndexOf('.'));
        this.targetField = target.substring(target.indexOf('.') + 1);
    }

    public Entry createEntry() {
        return new Entry();
    }

    public void addEntry(Entry entry) {
        samplerEntries.add(entry);
    }

    public class Entry implements IPopulatable {
        public float timeStamp;
        public float value;
        public String interpolationType;
    }
}
