package controllers;

import controllers.securesocial.SecureSocial;
import models.Role;
import models.SocialId;
import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.With;
import securesocial.provider.SocialUser;

/**
 * User: divxer
 * Date: 12-6-22
 * Time: 上午1:37
 */
@With(SecureSocial.class)
public class SocialConnect extends Controller {
    private static final String ORIGINAL_URL = "originalUrl";
    private static final String GET = "GET";
    private static final String ROOT = "/";

    // 创建新用户
    public static void connect(@Required String userName,
                               @Required String password,
                               @Required @Email(message = "securesocial.invalidEmail") String email) {
        String originalUrl = flash.get(ORIGINAL_URL);

        SocialUser socialUser = SecureSocial.getCurrentUser();

        SocialId socialId = SocialId.find("byUserIdAndProvider",
                socialUser.id.id, socialUser.id.provider.toString()).first();
        if (socialId == null) {
            socialId = new SocialId();
            socialId.userId = socialUser.id.id;
            socialId.provider = socialUser.id.provider.toString();
            socialId.email = socialUser.email;
        } else if (socialId.user == null) {
            flash.keep(ORIGINAL_URL);
            SecureSocial.login();
        }

        Role role = Role.findOrCreateByName("standard-user");

        User user = new User(userName, Crypto.passwordHash(password, Crypto.HashType.MD5), email);
        socialId.user = user;
        user.socialIds.add(socialId);
        user.roles.add(role);
        user.save();

        redirect(originalUrl);
    }

    // 关联已存在的用户
    public static void associateUser(@Required String userName,
                                     @Required String password, String originalUrl) {
        SocialUser socialUser = SecureSocial.getCurrentUser();
        SocialId socialId = SocialId.find("byUserIdAndProvider",
                socialUser.id.id, socialUser.id.provider.toString()).first();
        if (socialId == null) {
            socialId = new SocialId();
            socialId.provider = socialUser.id.provider.toString();
            socialId.userId = socialUser.id.id;
            socialId.email = socialUser.email;
        }

        User user = User.find("byName", userName).first();
        if (user == null || !Crypto.passwordHash(password, Crypto.HashType.MD5).equals(user.passWord)) {
            flash.keep(ORIGINAL_URL);
            renderArgs.put("user", socialUser);
            renderTemplate("SocialConnect/connect.html");
        } else {
            socialId.user = user;
            socialId.save();
            user.socialIds.add(socialId);
            user.save();

            redirect(originalUrl==null ? ROOT : originalUrl);
        }
    }
}
