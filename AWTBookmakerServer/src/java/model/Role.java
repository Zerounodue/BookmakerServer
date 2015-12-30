
package model;

/**
 *
 * @author Philippe Lüthi and Elia Kocher
 */
public class Role {
    private int id;
    private String name;
    
    /**
     *
     * @param id id of the role
     */
    public Role(int id){
        this.id = id;
    }
    
    /**
     *
     * @param id id of the role
     * @param name name of the role
     */
    public Role(int id, String name){
        this.id = id;
        this.name = name;
    }

    /**
     *
     * @return the role id
     */
    public int getId() {
        return id;
    }
    
}
