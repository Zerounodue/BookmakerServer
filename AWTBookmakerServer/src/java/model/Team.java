
package model;

/**
 *
 * @author Philippe Lüthi and Elia Kocher
 */
public class Team {
    private int id;
    private String name;
    
    /**
     *
     * @param id id of the Team
     * @param name name of the team
     */
    public Team(int id, String name){
        this.id = id;
        this.name = name;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
