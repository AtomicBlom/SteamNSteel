package mod.steamnsteel.client.collada;

/**
 * Created by Steven on 13/04/2015.
 */
public class ColladaException extends RuntimeException {
    public ColladaException(String s) {
        super(s);
    }

    public ColladaException(String s, Throwable t) {
        super(s, t);
    }
}
