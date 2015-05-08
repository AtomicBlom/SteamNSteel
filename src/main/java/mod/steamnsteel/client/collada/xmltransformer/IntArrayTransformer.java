package mod.steamnsteel.client.collada.xmltransformer;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * Created by Steven on 5/05/2015.
 */
public class IntArrayTransformer implements Function<String, Integer> {
    @Nullable
    @Override
    public Integer apply(String input) {
        return Integer.parseInt(input);
    }
}
