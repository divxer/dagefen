package controllers;

import org.apache.commons.lang.StringUtils;
import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index(int page, String searchText) {
        List<Album> albumList;
        flash.put("searchText", searchText);
        if (StringUtils.isEmpty(searchText)) {
            albumList = Album.find(
                    "order by id desc"
            ).fetch(page == 0 ? 1 : page, 12);
        } else {
            albumList = Album.find("title like ? or description like ?",
                    "%" + searchText + "%", "%" + searchText + "%").fetch(page == 0 ? 1 : page, 12);
        }

        render(albumList);
    }

}