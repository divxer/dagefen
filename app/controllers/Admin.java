package controllers;

import controllers.deadbolt.Deadbolt;
import controllers.deadbolt.Restrict;
import controllers.deadbolt.Restrictions;
import controllers.securesocial.SecureSocial;
import models.SocialId;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import securesocial.provider.ProviderType;
import securesocial.provider.SocialUser;

/**
 * User: divxer
 * Date: 12-6-15
 * Time: 下午11:19
 */
//@With(SecureSocial.class)
@With(Deadbolt.class)
public class Admin extends Controller {
    private static final String GET = "GET";
    private static final String ROOT = "/";
    private static final String ORIGINAL_URL = "originalUrl";

    @Before
    static void setConnectedUser() {
        SocialUser user = SecureSocial.getCurrentUser();
        if (user != null) {
            if (!user.id.provider.equals(ProviderType.userpass)) {
                final String originalUrl = request.method.equals(GET) ? request.url : ROOT;
                flash.put(ORIGINAL_URL, originalUrl);

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
            }
            renderArgs.put("user", user);
        }
    }

    @Restrictions({@Restrict("superadmin"), @Restrict("standard-user")})
    public static void index() {
        render();
    }
}
