package space.devport.utils.utility;

public class ArrayUtil {

    @SafeVarargs
    public static <U> U[] fromVararg(U... vararg){
        return vararg;
    }

}
