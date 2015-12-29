
package model;

/**
 *
 * @author Philippe Lüthi & Elia Kocher
 */
public class Role {
    private int id;
    private String name;
    
    public Role(int id){
        this.id = id;
    }
    
    public Role(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    
}
