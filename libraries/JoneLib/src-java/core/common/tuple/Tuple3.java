package core.common.tuple;

public class Tuple3<A, B, C> extends Tuple2<A, B> {
    public final C v3;

    public Tuple3(A a, B b, C c) {
        super(a, b);
        v3 = c;
    }

    public String toString() {
        return "(" + v1 + ", " + v2 + ", " + v3 + ")";
    }
}