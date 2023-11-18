package com.example.jakarta.hello;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import jakarta.annotation.Resource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("image")
public class ImageResource {
    
    @Resource(name = "jdbc/h2memorydatasource")
    private DataSource dataSource;

    @GET
    @Path("gen")
    @Produces("image/*")
    public Response generateImage() {
        // return ImageUtil.generateImageBytes("png");
        return Response.ok(ImageUtil.generateImageBytes("png", 64, 64))
            .type("image/png")
            .header("Cache-Control", "no-store")
            .header("Pragma", "no-cache")
            .header("Expires", "0")
            .build();
    }
    
    @GET
    @Path("{id}")
	@Produces("image/*")
    public Response getImage(
        @PathParam("id")
        int id) throws SQLException {
        QueryRunner run = new QueryRunner(dataSource);

        Map<String, Object> map = run.query("SELECT * FROM TESTDB WHERE ID = ?", new MapHandler(), id);

        if(map == null) {
            throw new WebApplicationException("No image found with id " + id, Status.NOT_FOUND);
        }

        InputStream is = null;
        String mimeType = null;
        
        Blob blob = (Blob) map.get("IMAGE");

        if(blob == null) {
            System.out.println("No image found with id " + id + ", generating one");
            is = new ByteArrayInputStream(ImageUtil.generateImageBytes("png"));
            mimeType = "image/png";
        } else {
            is = blob.getBinaryStream();
            mimeType = (String) map.get("MIME");
        }

        return Response.ok(is).type(mimeType).build();
    }
    
    @POST
    @Path("{id}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void setImage(
        @PathParam("id")
        int id,    
        List<EntityPart> parts) throws SQLException {
        
        String name = null;
        String fileName = null;
        InputStream is = null;
        MediaType mediaType = null;

        for(EntityPart part : parts) {
            name = part.getName();
            mediaType = part.getMediaType();
            
            if(name.equals("image") && mediaType.getType().equals("image")) {
                fileName = part.getFileName().orElse(null);
                is = part.getContent();
                break;
            }
        }

        if(is == null) {
            throw new WebApplicationException("No image part found", Status.BAD_REQUEST);
        }

        System.out.println("name=" + name);
        System.out.println("fileName=" + fileName);
        System.out.println("mediaType=" + mediaType);

        QueryRunner run = new QueryRunner(dataSource);
        int updated = run.update("UPDATE TESTDB SET MIME = ?, IMAGE = ? WHERE ID = ?", mediaType.toString(), is, id);

        if(updated == 0) {
            throw new WebApplicationException("No image found with id " + id, Status.NOT_FOUND);
        }
    }
}