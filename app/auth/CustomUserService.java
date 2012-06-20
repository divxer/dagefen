package auth;

import models.User;
import play.libs.Codec;
import securesocial.provider.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: divxer
 * Date: 12-6-19
 * Time: 上午8:11
 */
public class CustomUserService implements UserService.Service {
    private Map<String, SocialUser> users = Collections.synchronizedMap(new HashMap<String, SocialUser>());
    private Map<String, SocialUser> activations = Collections.synchronizedMap(new HashMap<String, SocialUser>());
    private Map<String, SocialUser> resetRequests = Collections.synchronizedMap(new HashMap<String, SocialUser>());

    public SocialUser find(UserId id) {
        if (id.provider.equals(ProviderType.userpass)) {
            User dagefenUser = User.find("byName", id.id).first();
            if (dagefenUser != null) {
                SocialUser user = new SocialUser();
                user.id = id;
                user.email = dagefenUser.email;
                user.password = dagefenUser.passWord;
                user.displayName = dagefenUser.displayName;
                user.authMethod = AuthenticationMethod.USER_PASSWORD;
                user.isEmailVerified = dagefenUser.needConfirmation == null;
                return user;
            }
        }
        return users.get(id.id + id.provider.toString());
    }

    public SocialUser find(String email) {
        for (SocialUser su : users.values()) {
            if (su.id.provider.equals(ProviderType.userpass)) {
                User dagefenUser = User.find("byName", su.id.id).first();
                if (dagefenUser != null) {
                    return su;
                } else {
                    if (su.email.equals(email)) {
                        return su;
                    }
                }
            } else {
                if (su.email.equals(email)) {
                    return su;
                }
            }
        }

        return null;
    }

    public void save(SocialUser user) {
        users.put(user.id.id + user.id.provider.toString(), user);
    }

    public String createActivation(SocialUser user) {
        final String uuid = Codec.UUID();
        activations.put(uuid, user);

        User dagefenUser = new User(user.id.id, user.password, user.email);
        dagefenUser.needConfirmation = uuid;
        dagefenUser.save();

        return uuid;
    }

    public boolean activate(String uuid) {
        SocialUser user = activations.get(uuid);
        boolean result = false;

        if (user != null) {
            user.isEmailVerified = true;
            save(user);
            activations.remove(uuid);
            result = true;
        }

        User dagefenUser = User.find("byNeedConfirmation", uuid).first();
        if (dagefenUser != null) {
            dagefenUser.needConfirmation = null;
            dagefenUser.save();
        }

        return result;
    }

    @Override
    public String createPasswordReset(SocialUser user) {
        final String uuid = Codec.UUID();
        resetRequests.put(uuid, user);
        User dagefenUser = User.find("name", user.id.id).first();
        if (dagefenUser != null) {
            dagefenUser.passwordReset = uuid;
        }
        return uuid;
    }

    @Override
    public SocialUser fetchForPasswordReset(String username, String uuid) {
        User dagefenUser = User.find("passwordReset", uuid).first();
        if (dagefenUser != null) {
            SocialUser socialUser = new SocialUser();
            socialUser.id.id = dagefenUser.name;
            return socialUser;
        }

        return null;
    }

    @Override
    public void disableResetCode(String username, String uuid) {
        SocialUser socialUser = fetchForPasswordReset(username, uuid);

        User dagefenUser = User.find("passwordReset", uuid).first();
        if (dagefenUser != null && dagefenUser.name.equals(socialUser.id.id)) {
            dagefenUser.passwordReset = null;
        }
    }

    public void deletePendingActivations() {
        // TODO
        activations.clear();
    }
}
