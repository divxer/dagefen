package models;

import play.data.validation.Email;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

/**
 * 用户
 * User: divxer
 * Date: 12-6-12
 * Time: 上午12:47
 */
@Entity
public class User extends Model {
    public String name;
    public String passWord;
    @Email
    public String email;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Role> roles;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<UserGroup> userGroups;

    public static User connect(String name, String password) {
        return find("byNameAndPassWord", name, password).first();
    }

    public String toString() {
        return name;
    }
}
