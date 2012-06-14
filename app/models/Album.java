package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Date;
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
    public Date updateTime;
    @Required
    @ManyToOne
    public User author;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    public List<Picture> pictures = new ArrayList<Picture>();

    public Album(String title, String description, String type, User author) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.updateTime = new Date();
        this.author = author;
    }
}
