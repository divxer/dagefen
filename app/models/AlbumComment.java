package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.Date;

/**
 * User: divxer
 * Date: 12-7-25
 * Time: 下午9:52
 */
@Entity
public class AlbumComment extends Model {
    @ManyToOne
    public Album album;

    public String author;

    @Required
    public Date postedAt;

    @Lob
    @Required
    @MaxSize(1000)
    public String content;

    public AlbumComment(Album album, String author, String content) {
        this.album = album;
        this.content = content;
        this.author = author;
        this.postedAt = new Date();
    }
}
