package models;

import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * User: divxer
 * Date: 12-6-4
 * Time: 下午10:19
 */
@Entity
public class Album extends Model {
    public String title;
    public String description;
    public String type;
    public String source;
    public String thumbnail;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    public List<Picture> pictures = new ArrayList<Picture>();

    public Album(String title, String description, String type) {
        this.title = title;
        this.description = description;
        this.type = type;
    }
}
