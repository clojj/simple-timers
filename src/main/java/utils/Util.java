package utils;

import java.util.Optional;
import java.util.function.Consumer;

public class Util {

    private Util() {
    }

    public static void switchType(Object o, Consumer... consumers) {
        for (Consumer consumer : consumers)
            consumer.accept(o);
    }

    public static <T> Consumer caze(Class<T> cls, Consumer<T> c) {
        return obj -> Optional.of(obj).filter(cls::isInstance).map(cls::cast).ifPresent(c);
    }
}
