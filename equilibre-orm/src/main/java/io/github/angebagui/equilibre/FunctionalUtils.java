package io.github.angebagui.equilibre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by angebagui on 12/06/2016.
 */
public class FunctionalUtils {

    public interface Function1<T, R>{
        R call(T t);
    }
    public interface Function2<T>{
        Void call(T t);
    }

    public static <T> List<T> filter(Collection<T> items, Function1<T, Boolean> f ){
        final List<T> filtered = new ArrayList<T>();
        for (T t:items)if (f.call(t))filtered.add(t);
        return filtered;
    }

    public static <T> void forEach(Collection<T> items, Function2<T> f){
        for (T t:items)f.call(t);
    }

    public static <T, R> List<R> map(Collection<T> items, Function1<T, R> f){
        final List<R> results = new ArrayList<R>();
        for (T t:items)results.add(f.call(t));
        return results;
    }
}
