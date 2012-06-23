package controllers;

import controllers.securesocial.SecureSocial;
import models.SocialId;
import models.User;
import play.data.validation.Email;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import securesocial.provider.SocialUser;

import java.util.Iterator;
import java.util.Map;

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

    public static void connect(@Required String userName,
                               @Required String password,
                               @Required @Email(message = "securesocial.invalidEmail") String email) {
        final String originalUrl = request.method.equals(GET) ? request.url : ROOT;
        flash.put(ORIGINAL_URL, originalUrl);

        Map<String, String> map = Controller.params.allSimple();

        for (String s : map.keySet()) {
            String value = map.get(s);

            System.out.println(s + " " + value);
        }

        SocialUser socialUser = SecureSocial.getCurrentUser();

        SocialId socialId = SocialId.find("byUserIdAndProvider",
                socialUser.id.id, socialUser.id.provider.toString()).first();
        if (socialId == null) {
            socialId = new SocialId();
            socialId.userId = socialUser.id.id;
            socialId.provider = socialUser.id.provider.toString();
            socialId.email = socialUser.email;
        } else if (socialId.user == null) {
            SecureSocial.login();
        }

        User user = new User(userName, password, email);
        socialId.user = user;
        user.socialIds.add(socialId);
        user.save();

        redirect(ORIGINAL_URL);
    }

    // 创建新用户
    public static void createUser() {
        render();
    }

    // 关联已存在的用户
    public static void associateUser() {
        render();
    }
}
