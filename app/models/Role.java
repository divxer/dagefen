package models;

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
public class Role extends Model {
    public String name;

    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Privilege> privileges;
}
