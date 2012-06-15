package controllers;

import models.User;

/**
 * User: divxer
 * Date: 12-6-15
 * Time: 下午11:15
 */
public class Security extends Secure.Security {
    static boolean authenticate(String username, String password) {
        return User.connect(username, password) != null;
    }
}
