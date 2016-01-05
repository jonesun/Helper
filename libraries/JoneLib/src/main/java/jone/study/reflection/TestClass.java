package jone.study.reflection;

/**
 * Created by jone.sun on 2015/12/21.
 */
public class TestClass {
    private int id;
    private String name;

    public TestClass(){

    }

    public TestClass(int id){
        this.id = id;
    }

    public TestClass(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
