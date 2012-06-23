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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SocialId socialId = (SocialId) o;

        if (email != null ? !email.equals(socialId.email) : socialId.email != null) return false;
        if (provider != null ? !provider.equals(socialId.provider) : socialId.provider != null) return false;
        if (userId != null ? !userId.equals(socialId.userId) : socialId.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
