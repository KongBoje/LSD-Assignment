/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import DBLayer.PostQueries;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entities.Post;
import helper_entities.LoginInfo;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Lasse
 */
@Path("/post")
public class PostResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PostResource
     */
    public PostResource() {
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response makeNewPost(@HeaderParam("Authorization") String auth, String body) {
        LoginInfo li = new LoginInfo();
        if (!li.parseAuth(auth)) {
            return Response.status(403).build();
        }

        Post p = getValidUserInput(body);
        PostQueries pq = new PostQueries();

        if (p == null) {
            return Response.status(400).build();
        }

        if (p.posttype.equals("story")) {
            try {
                pq.insertStory(p, li.username, li.password);
                return Response.status(200).build();
            } catch (SQLException ex) {
                Logger.getLogger(PostResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.status(500).build();
            }
        }

        /* if (p.posttype.equals("comment")) { */
        try {
            pq.insertCommentWithLookup(p, li.username, li.password);
            return Response.status(200).build();
        } catch (SQLException ex) {
            Logger.getLogger(PostResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(500).build();
        }

        /* return Response.status(500).build(); */
    }

    private Post getValidUserInput(String body) {
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(body);

        if (je == null) {
            return null;
        }

        JsonObject jo = je.getAsJsonObject();
        JsonElement post_type = jo.get("post_type");
        if (post_type == null) {
            return null;
        }

        JsonElement post_parent = jo.get("post_parent");
        if (post_parent == null) {
            return null;
        }

        JsonElement hanesst_id = jo.get("hanesst_id");
        if (hanesst_id == null) {
            return null;
        }

        JsonElement post_text = jo.get("post_text");
        if (post_text == null) {
            return null;
        }

        String _post_type = post_type.getAsString();
        int _post_parent = post_parent.getAsInt();
        int _postid = hanesst_id.getAsInt();
        String _post_text = post_text.getAsString();

        if (_post_type.equals("comment")) {
            /* comments har ingen post_title eller post_url */
            Post ret = new Post();
            ret.initComment(_postid, _post_parent, _post_text);
            return ret;
        } else if (_post_type.equals("story")) {
            JsonElement post_title = jo.get("post_title");
            if (post_title == null) {
                return null;
            }

            JsonElement post_url = jo.get("post_url");
            if (post_url == null) {
                return null;
            }

            _post_text = post_title.getAsString() + " " + post_url.getAsString();
            Post ret = new Post();
            ret.initStory(_postid, _post_text);
            return ret;
        }

        return null;
    }

}
