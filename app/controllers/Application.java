package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index(int page) {
        List<Album> albumList = Album.find(
                "order by id desc"
        ).fetch(page == 0 ? 1 : page, 12);

        render(albumList);
    }

}