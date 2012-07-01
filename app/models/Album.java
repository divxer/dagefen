package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
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
    public String dagefenUrl;

    @Required
    @ManyToOne
    public User author;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    public List<Picture> pictures = new ArrayList<Picture>();

    public Album(String title, String description, String type, String source, User author) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.source = source;
        this.updateTime = new Date();
        this.author = author;
    }

    @PostPersist
    public void postPersist() {
        this.updateTime = new Date();
    }
}
