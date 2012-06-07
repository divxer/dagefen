package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

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
    public boolean upload2upyun;

    @ManyToOne
    @Required
    public Album album;

    public Picture(String title, String description, String fileName, String source, Album album) {
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.source = source;
        this.album = album;
    }
}
