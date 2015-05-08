package mod.steamnsteel.client.collada;

import mod.steamnsteel.client.collada.model.ColladaSampler;
import mod.steamnsteel.client.collada.model.ColladaVertex;

import java.util.List;

public class ColladaInputStream implements IColladaInputStream {

    public final ColladaAccessor accessor;
    public final Integer offset;
    public final String semantic;
    private final Populator[] populators;

    public ColladaInputStream(ColladaAccessor accessor, String semantic, int offset) {
        this.accessor = accessor;
        this.semantic = semantic;
        this.offset = offset;
        this.populators = getPopulatorsForSemantic(semantic);
    }

    private Populator[] getPopulatorsForSemantic(String semantic) {
        switch (semantic) {
            case "POSITION":
                return new Populator[]{
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.position.x = accessorElement.asFloat();
                            }
                        },
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.position.y = accessorElement.asFloat();
                            }
                        },
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.position.z = accessorElement.asFloat();
                            }
                        }
                };
            case "NORMAL":
                return new Populator[]{
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.normal.x = accessorElement.asFloat();
                            }
                        },
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.normal.y = accessorElement.asFloat();
                            }
                        },
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.normal.z = accessorElement.asFloat();
                            }
                        }
                };
            case "TEXCOORD":
                return new Populator[]{
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.texture.u = accessorElement.asFloat();
                            }
                        },
                        new Populator<ColladaVertex>() {
                            @Override
                            public void populate(ColladaVertex vertex, ColladaAccessorElement accessorElement) {
                                vertex.texture.v = 1 - accessorElement.asFloat();
                            }
                        }
                };
            case "INPUT":
                return new Populator[] {
                        new Populator<ColladaSampler.Entry>() {
                            @Override
                            public void populate(ColladaSampler.Entry sampler, ColladaAccessorElement accessorElement) {
                                sampler.timeStamp = accessorElement.asFloat();
                            }
                        }
                };
            case "OUTPUT":
                return new Populator[] {
                        new Populator<ColladaSampler.Entry>() {
                            @Override
                            public void populate(ColladaSampler.Entry sampler, ColladaAccessorElement accessorElement) {
                                sampler.value = accessorElement.asFloat();
                            }
                        }
                };
            case "INTERPOLATION":
                return new Populator[] {
                        new Populator<ColladaSampler.Entry>() {
                            @Override
                            public void populate(ColladaSampler.Entry sampler, ColladaAccessorElement accessorElement) {
                                sampler.interpolationType = accessorElement.value;
                            }
                        }
                };
        }
        return null;
    }

    @Override
    public boolean applyElementsAtIndex(int index, IPopulatable populatable) {
        List<ColladaAccessorElement> elements = accessor.getElementsAtIndex(index);
        if (elements == null) {
            return false;
        }
        int currentPopulator = 0;
        for (ColladaAccessorElement element : elements) {
            Populator populator = populators[currentPopulator];
            populator.populateObject(populatable, element);
            currentPopulator++;
        }
        return true;
    }

    private abstract class Populator<T extends IPopulatable> {
        abstract void populate(T object, ColladaAccessorElement accessorElement);
        public final void populateObject(IPopulatable object, ColladaAccessorElement accessorElement) {
            populate((T) object, accessorElement);
        }
    }
}
