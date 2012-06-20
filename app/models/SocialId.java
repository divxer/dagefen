package models;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * 社交网站平台ID
 * User: divxer
 * Date: 12-6-20
 * Time: 上午12:32
 */
@Entity
public class SocialId extends Model {
    public String userId;
    public String provider;
    @Email
    public String email;

    @ManyToOne
    @Required
    public User user;
}
