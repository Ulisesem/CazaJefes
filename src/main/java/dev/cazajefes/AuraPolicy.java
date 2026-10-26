package dev.cazajefes;
import java.util.*;
import java.util.function.ToDoubleFunction;
final class AuraPolicy {
 static int interval(int value){value=Math.max(5,Math.min(40,value));return ((value+2)/5)*5;}
 static int radius(int value){return Math.max(8,Math.min(48,value));}
 static int bosses(int value){return Math.max(1,Math.min(4,value));}
 static <T> List<T> nearest(Collection<T> values,ToDoubleFunction<T> distance,int limit,double rangeSquared){return values.stream().filter(v->Double.isFinite(distance.applyAsDouble(v))&&distance.applyAsDouble(v)<=rangeSquared).sorted(Comparator.comparingDouble(distance)).limit(limit).toList();}
}
