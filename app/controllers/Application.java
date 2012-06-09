package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        List<Album> albumList = Album.find(
                "order by id desc"
        ).fetch(8);

        render(albumList);
    }

}