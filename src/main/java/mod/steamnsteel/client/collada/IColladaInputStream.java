package mod.steamnsteel.client.collada;

import mod.steamnsteel.client.collada.model.ColladaVertex;

/**
 * Created by Steven on 5/05/2015.
 */
public interface IColladaInputStream {
    boolean applyElementsAtIndex(int index, IPopulatable populatable);
}
