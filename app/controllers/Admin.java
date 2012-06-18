package controllers;

import controllers.securesocial.SecureSocial;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * User: divxer
 * Date: 12-6-15
 * Time: 下午11:19
 */
@With( SecureSocial.class )
public class Admin extends Controller {
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byName", Security.connected()).first();
            renderArgs.put("user", user.name);
        }
    }

    public static void index() {
        render();
    }
}
