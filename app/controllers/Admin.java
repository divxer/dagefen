package controllers;

import controllers.securesocial.SecureSocial;
import models.SocialId;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import securesocial.provider.SocialUser;

/**
 * User: divxer
 * Date: 12-6-15
 * Time: 下午11:19
 */
@With(SecureSocial.class)
public class Admin extends Controller {
    @Before
    static void setConnectedUser() {
        SocialUser user = SecureSocial.getCurrentUser();
        if (user != null) {
            SocialId socialId = SocialId.find("byUserIdAndProvider",
                    user.id.id, user.id.provider.toString()).first();
            if (socialId == null) {
                socialId = new SocialId();
                socialId.userId = user.id.id;
                socialId.provider = user.id.provider.toString();
                renderTemplate("SocialConnect/connect.html", user);
            } else if (socialId.user == null) {
                renderTemplate("SocialConnect/connect.html", user, socialId);
            }
            renderArgs.put("user", user);
        }
    }

    public static void index() {
        render();
    }
}
