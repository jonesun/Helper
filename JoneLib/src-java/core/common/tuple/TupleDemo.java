package core.common.tuple;

import java.util.ArrayList;
import java.util.List;

public class TupleDemo {
    public static void main(String[] args) {
        List<Amphibian> list = new ArrayList<Amphibian>();
        Tuple4<List<Amphibian>, Integer, String, Vehicle> twoTuple = Tuple.tuple(list, 123, "abc", new Vehicle());
        System.out.println(twoTuple);
        System.out.println(twoTuple.v1);
        System.out.println(twoTuple.v2);
        System.out.println(twoTuple.v3);
        System.out.println(twoTuple.v4);
    }
}

class Amphibian {
}

class Vehicle {
}