package models;

import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.Set;

/**
 * 角色
 * User: divxer
 * Date: 12-6-12
 * Time: 上午12:47
 */
@Entity
public class Role extends Model implements models.deadbolt.Role {
    @Required
    public String name;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Privilege> privileges;

    public Role(String name) {
        this.name = name;
    }

    public static Role findOrCreateByName(String name) {
        Role role = Role.find("byName", name).first();
        if (role == null) {
            role = new Role(name);
        }
        return role;
    }

    @Override
    public String getRoleName() {
        return name;
    }
}
