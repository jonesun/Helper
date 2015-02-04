package core.common.tuple;

public class Tuple4<A, B, C, D> extends Tuple3<A, B, C> {
    public final D v4;

    public Tuple4(A a, B b, C c, D d) {
        super(a, b, c);
        v4 = d;
    }

    public String toString() {
        return "(" + v1 + ", " + v2 + ", " +
                v3 + ", " + v4 + ")";
    }
}