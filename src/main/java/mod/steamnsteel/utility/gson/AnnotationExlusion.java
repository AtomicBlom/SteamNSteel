package mod.steamnsteel.utility.gson;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class AnnotationExlusion implements ExclusionStrategy
{
    @Override
    public boolean shouldSkipField(FieldAttributes field)
    {
        Exclude annotation = field.getAnnotation(Exclude.class);
        return annotation == null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz)
    {
        return false;
    }
}
