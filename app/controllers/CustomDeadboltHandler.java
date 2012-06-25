package controllers;

import auth.AuthRoleHolder;
import controllers.deadbolt.DeadboltHandler;
import controllers.deadbolt.ExternalizedRestrictionsAccessor;
import controllers.deadbolt.RestrictedResourcesHandler;
import controllers.securesocial.SecureSocial;
import models.SocialId;
import models.User;
import models.deadbolt.Role;
import models.deadbolt.RoleHolder;
import play.mvc.Controller;
import securesocial.provider.ProviderType;
import securesocial.provider.SocialUser;

import java.util.ArrayList;
import java.util.List;

/**
 * User: divxer
 * Date: 12-6-25
 * Time: 下午10:21
 */
public class CustomDeadboltHandler extends Controller implements DeadboltHandler {
    public void beforeRoleCheck() {
        try {
            SecureSocial.DeadboltHelper.beforeRoleCheck();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new RuntimeException(throwable);
        }
    }

    public RoleHolder getRoleHolder() {
        // get the current user
        SocialUser user = SecureSocial.getCurrentUser();

        // create a role based on the network the user belongs to.
        List<Role> roles = new ArrayList<Role>();
        if (user.id.provider.equals(ProviderType.userpass)) {
            User userDb = User.find("byName", user.id.id).first();
            roles.addAll(userDb.roles);
        } else {
            SocialId socialId = SocialId.find("byUserIdAndProvider",
                    user.id.id, user.id.provider.toString()).first();
            if (socialId != null && socialId.user != null) {
                roles.addAll(socialId.user.roles);
            }
        }

        System.out.println("roles=" + roles);

        // we're done
        return new AuthRoleHolder(roles);
    }

    public void onAccessFailure(String controllerClassName) {
        forbidden();
    }

    public ExternalizedRestrictionsAccessor getExternalizedRestrictionsAccessor() {
        return null;
    }

    public RestrictedResourcesHandler getRestrictedResourcesHandler() {
        return null;
    }
}
