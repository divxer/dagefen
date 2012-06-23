package models;

import play.data.validation.Email;
import play.db.jpa.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * 用户
 * User: divxer
 * Date: 12-6-12
 * Time: 上午12:47
 */
@Entity
public class User extends Model {
    // 用户名
    public String name;
    // 昵称
    public String displayName;
    // 密码
    public String passWord;
    // 邮件
    @Email
    public String email;
    // 注册确认
    public String needConfirmation;
    // 密码重置
    public String passwordReset;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    public Set<SocialId> socialIds = new HashSet<SocialId>();

    // 角色列表
    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<Role> roles = new HashSet<Role>();

    // 用户组列表
    @ManyToMany(cascade = CascadeType.PERSIST)
    public Set<UserGroup> userGroups = new HashSet<UserGroup>();

    public User(String name, String passWord, String email) {
        this.name = name;
        this.passWord = passWord;
        this.email = email;
        create();
    }

    public static User connect(String name, String password) {
        return find("byNameAndPassWord", name, password).first();
    }

    public String toString() {
        return name;
    }
}
