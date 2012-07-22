package controllers;

import models.Album;
import org.apache.commons.lang.StringUtils;
import play.mvc.Controller;

import java.util.List;

public class Application extends Controller {

    public static void index(int page, String searchText) {
        List<Album> albumList;
        flash.put("searchText", searchText);
        if (StringUtils.isEmpty(searchText)) {
            albumList = Album.find(
                    "order by id desc"
            ).fetch(page == 0 ? 1 : page, 24);
        } else {
            albumList = Album.find("title like ? or description like ?",
                    "%" + searchText + "%", "%" + searchText + "%").fetch(page == 0 ? 1 : page, 24);
        }

        render(albumList);
    }

}