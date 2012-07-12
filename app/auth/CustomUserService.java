package auth;

import models.Role;
import models.User;
import play.libs.Codec;
import play.libs.Crypto;
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
                return convert2SocialUser(dagefenUser);
            }
        }
        return users.get(id.id + id.provider.toString());
    }

    private SocialUser convert2SocialUser(User dagefenUser) {
        UserId id = new UserId();
        id.id = dagefenUser.name;
        id.provider = ProviderType.userpass;
        SocialUser user = new SocialUser();
        user.id = id;
        user.email = dagefenUser.email;
        user.password = dagefenUser.passWord;
        user.displayName = dagefenUser.displayName;
        user.authMethod = AuthenticationMethod.USER_PASSWORD;
        user.isEmailVerified = dagefenUser.needConfirmation == null;
        return user;
    }

    public SocialUser find(String email) {
        User user = User.find("byEmail", email).first();
        if (user != null) {
            return convert2SocialUser(user);
        }

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

        if (user.id.provider.equals(ProviderType.userpass)) {
            User dagefenUser = User.find("name", user.id.id).first();
            if (dagefenUser == null) {
                Role role = Role.findOrCreateByName("standard-user");
                dagefenUser = new User(user.id.id,
                        Crypto.passwordHash(user.password, Crypto.HashType.MD5), user.email);
                dagefenUser.roles.add(role);
            } else {
                dagefenUser.name = user.id.id;
                dagefenUser.passWord = user.password;
                dagefenUser.email = user.email;
                dagefenUser.save();
            }
        }
    }

    public String createActivation(SocialUser user) {
        final String uuid = Codec.UUID();
        activations.put(uuid, user);

        User dagefenUser = User.find("name", user.id.id).first();
        if (dagefenUser != null) {
            dagefenUser.needConfirmation = uuid;
            dagefenUser.save();
        }

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

    public String createPasswordReset(SocialUser user) {
        final String uuid = Codec.UUID();
        resetRequests.put(uuid, user);
        User dagefenUser = User.find("name", user.id.id).first();
        if (dagefenUser != null) {
            dagefenUser.passwordReset = uuid;
            dagefenUser.save();
        }
        return uuid;
    }

    public SocialUser fetchForPasswordReset(String username, String uuid) {
        User dagefenUser = User.find("byPasswordReset", uuid).first();
        if (dagefenUser != null) {
            return convert2SocialUser(dagefenUser);
        }

        return null;
    }

    public void disableResetCode(String username, String uuid) {
        SocialUser socialUser = fetchForPasswordReset(username, uuid);

        User dagefenUser = User.find("byPasswordReset", uuid).first();
        if (dagefenUser != null && dagefenUser.name.equals(socialUser.id.id)) {
            dagefenUser.passwordReset = null;
        }
    }

    public void deletePendingActivations() {
        // TODO
        activations.clear();
    }
}
