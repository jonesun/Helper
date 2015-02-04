package core.common.tuple;

public class Tuple5<A, B, C, D, E>
        extends Tuple4<A, B, C, D> {
    public final E v5;

    public Tuple5(A a, B b, C c, D d, E e) {
        super(a, b, c, d);
        v5 = e;
    }

    public String toString() {
        return "(" + v1 + ", " + v2 + ", " +
                v3 + ", " + v4 + ", " + v5 + ")";
    }
}