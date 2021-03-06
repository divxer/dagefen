package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PostPersist;
import java.util.Date;

/**
 * User: divxer
 * Date: 12-6-3
 * Time: 下午11:09
 */
@Entity
public class Picture extends Model {
    public String title;
    public String description;
    public String fileName;
    public String source;
    public Date updateTime;
    public String imgUrl;
    public String dagefenUrl;

    @ManyToOne
    @Required
    public Album album;

    public Picture(String title, String description, String fileName, String source, Album album, String imgUrl) {
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.source = source;
        this.album = album;
        this.imgUrl = imgUrl;
        this.updateTime = new Date();
    }

    @PostPersist
    public void postPersist() {
        this.updateTime = new Date();
    }
}
