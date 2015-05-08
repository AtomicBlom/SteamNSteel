package mod.steamnsteel.client.collada;

import mod.steamnsteel.client.collada.model.transformation.MatrixTransformation;

/**
 * Created by Steven on 7/05/2015.
 */
public class ColladaArray {

    private final String[] source;
    private final Type type;

    public ColladaArray(Type arrayType, String arraySource) {
        source = arraySource.trim().split("\\s+");
        this.type = arrayType;
    }

    public MatrixTransformation[] asMatrixArray() {
        try {
            double[] source = asDoubleArray();

            MatrixTransformation[] newArray = new MatrixTransformation[(int)(source.length >> 4)];
            for (int i = 0; i < source.length; i += 16) {
                newArray[i >> 4] = new MatrixTransformation(
                        source[i + 0],
                        source[i + 1],
                        source[i + 2],
                        source[i + 3],
                        source[i + 4],
                        source[i + 5],
                        source[i + 6],
                        source[i + 7],
                        source[i + 8],
                        source[i + 9],
                        source[i + 10],
                        source[i + 11],
                        source[i + 12],
                        source[i + 13],
                        source[i + 14],
                        source[i + 15]
                );
            }
            return newArray;
        } catch (Exception e) {
            throw new ColladaException("Exception retrieving array as Matrices", e);
        }
    }

    public float[] asFloatArray() {
        try {
            float[] newArray = new float[source.length];
            for (int i = 0; i < source.length; ++i) {
                newArray[i] = Float.parseFloat(source[i]);
            }
            return newArray;
        } catch (Exception e) {
            throw new ColladaException("Error retrieving array as float", e);
        }
    }

    public double[] asDoubleArray() {
        try {
            double[] newArray = new double[source.length];
            for (int i = 0; i < source.length; ++i) {
                newArray[i] = Double.parseDouble(source[i]);
            }
            return newArray;
        } catch (Exception e) {
            throw new ColladaException("Error retrieving array as float", e);
        }
    }

    public int[] asIntArray() {
        try {
            int[] newArray = new int[source.length];
            for (int i = 0; i < source.length; ++i) {
                newArray[i] = Integer.parseInt(source[i]);
            }
            return newArray;
        } catch (Exception e) {
            throw new ColladaException("Error retrieving array as float", e);
        }
    }

    public String[] getSource() {
        return source;
    }

    public enum Type {
        DOUBLE, FLOAT, NAME, INTEGER;

        public static Type from(String type) {
            switch (type) {
                case "translate":
                case "rotate":
                case "scale":
                case "float_array":
                    return FLOAT;
                case "double_array": return DOUBLE;
                case "Name_array": return NAME;
                case "p":
                case "vcount":
                    return INTEGER;
            }
            throw new ColladaException("Could not derive Array Type from element name: " + type);
        }
    }
}
