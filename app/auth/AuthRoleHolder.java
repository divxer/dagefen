package auth;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;

import java.util.List;

/**
 * User: divxer
 * Date: 12-6-25
 * Time: 下午10:06
 */
public class AuthRoleHolder implements RoleHolder {
    private List<Role> roles;

    public AuthRoleHolder(List<Role> roles) {
        this.roles = roles;

    }

    @Override
    public List<? extends Role> getRoles() {
        return roles;
    }
}
