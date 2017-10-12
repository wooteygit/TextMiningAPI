package com.assignmentscoring.veiw;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.assignmentscoring.controllers.*;
import com.assignmentscoring.directive.*;
import static com.assignmentscoring.model.app.DEFAULT_PATH;
import java.io.*;
import java.util.*;

import javax.ws.rs.core.*;
import javax.ws.rs.*;
import static javax.ws.rs.HttpMethod.POST;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jboss.resteasy.annotations.cache.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * REST Web Service
 *
 * @author wootey02
 */
@Component
@Path("/data")
public class DataResource extends com.assignmentscoring.model.app{

    @Context
    private UriInfo context;
    @Autowired
    controllers ctr;
    ctr_clustering clustr;
    ctr_classify cfy;
    /**
     * Creates a new instance of DataResource
     */
    public DataResource() {}

    /*
    ------------------------------Clustering---------------------------------------
    */
    
    @GET
    @Path("/wordsDetail")
    @Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
    public Response getWordsDetail(@QueryParam("group") int group) {
        Response response;
        response = Response.status(200).entity(ctr.wordsDetail(group)).build();
        return response;
    }
    
    @GET
    @Path("/answer")
    @Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
    public Response getAnswer(@QueryParam("group") int group,@QueryParam("ans") double ans) {
        Response response;
        response = Response.status(200).entity(ctr.answer(group,ans)).build();
        return response;
    }
    
    @GET
    @Path("/group/{k}")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getModelGroup(@PathParam("k") int k) {
        return Response.status(200).entity(ctr.modelGroup(k)).build();
    }
    
    @GET
    @Path("/loadScoreFromFile")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response loadScoreFromFile(@QueryParam("fileName") String fileName) {
        return Response.status(200).entity(ctr.loadScoreFromFile(fileName)).build();
    }
    
    @GET
    @Path("/word")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getWord(@QueryParam("w") String w,@QueryParam("g") int g) {
        return Response.status(200).entity(ctr.setWordToDB(w,g,0)).build();
    }
    
    @GET
    @Path("/clustering")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response clusteringAns(@QueryParam("fileAns") String fileAns,@QueryParam("k") int k) {
        ReturnTempTable rt = new ReturnTempTable();
        return Response.status(200).entity(ctr.clusterAns(fileAns,k,1)).build();
    }
    /*
    ------------------------------Clustering---------------------------------------
    */

    /*
    ------------------------------classify---------------------------------------
    */    
    @GET
    @Path("/classify")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response classifyAns(@QueryParam("sj_id") int sj_id,@QueryParam("p_sj_id") int p_sj_id
        ,@QueryParam("sj_seq") int sj_seq,@QueryParam("num_k") int num_k,@QueryParam("perc") double perc) {
        return Response.status(200).entity(ctr.classifyAns(sj_id,p_sj_id,sj_seq,num_k,perc)).build();
    }
    
    @GET
    @Path("/writerclassify")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getWriterClassify(@QueryParam("pertext") String pertext,@QueryParam("k") int k
       ,@QueryParam("fold")int fold_clussify) {       
       return Response.status(200).entity(ctr.writerClassiffy(pertext,k,fold_clussify)).build();
    }
    
    @GET
    @Path("/writerPretextOnly")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getWriterPretextOnly(@QueryParam("pertext") String pertext) {       
        return Response.status(200).entity(ctr.writerPertextOnly(pertext)).build();
    }
    
//    @POST
//    @Path("/train_test")
//    @Consumes(MediaType.APPLICATION_JSON+ ";charset=utf-8")
//    @Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
//    public Response train_testModel(MapSvmKernelFlot skt){
//        String filewrongitem = skt.getScoreFile();
//        return Response.status(200).entity(ctr.trainModel(skt,filewrongitem)).build();
//    }
    
//    @GET
//    @Path("/best_svm")
//    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
//    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
//    public Response getBestSvm() {
//        return Response.status(200).entity(ctr.getBestSVM()).build();
//    }
    /*
    ------------------------------classify---------------------------------------
    */ 

    /*
    ------------------------------Reraining---------------------------------------
    */ 

    @GET
    @Path("/retraining")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getRetraining(@QueryParam("sj_id") int sj_id,@QueryParam("p_sj_id") int p_sj_id
        ,@QueryParam("sj_seq") int sj_seq,@QueryParam("num_k") int num_k) {       
        return Response.status(200).entity(ctr.Retraining(sj_id,p_sj_id,sj_seq,num_k)).build();
    }
    
    @GET
    @Path("/perdict")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getPredict() {       
        return Response.status(200).entity(ctr.PerdictSVM()).build();
    }
    
    @POST
    @Path("/multiparam")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getSvmMultiParam(ArrayList<String> strargv) {       
        return Response.status(200).entity(ctr.svmMultiParam(strargv)).build();
    }
    
    @GET
    @Path("/perscore")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getPerScore(@QueryParam("sql") String sql) {       
        return Response.status(200).entity(ctr.perScore(sql)).build();
    }

    /*
    ------------------------------Reraining---------------------------------------
    */ 
    
    /*
    ------------------------------Report---------------------------------------
    */
    @GET
    @Path("/scorereport")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response setScoreReport() {       
        return Response.status(200).entity(ctr.setScoreReport()).build();
    }
    /*
    ------------------------------Report---------------------------------------
    */
    

    /*
    ------------------------------Config---------------------------------------
    */
    @GET
    @Path("/datasql")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getDataSql(@QueryParam("sql") String sql) {
        return Response.status(200).entity(ctr.getDataForSql(sql)).build();
    }
    
    @GET
    @Path("/excsql")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response excSql(@QueryParam("sql") String sql) {
        return Response.status(200).entity(ctr.setExcSql(sql)).build();
    }
    
    @GET
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getStatus() {
        return Response.status(200).entity(ctr.getConnect()).build();
    } 
    
    @GET
    @Path("/config")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response setData() {
        return Response.status(200).entity(ctr.getConfig()).build();
    } 
    
    @GET
    @Path("/path")
    @Consumes(MediaType.TEXT_PLAIN + ";charset=utf-8")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public Response getPath() {
        return Response.status(200).entity(DEFAULT_PATH).build();
    }
    
    @GET
    @Path("/test")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getTest() {
        ArrayList<String> aa = new ArrayList<String>();
        aa.add("-s,0,-c,1,-wi,1,-b,0,-t,1,-g,1");
        aa.add("-s,0,-c,1,-wi,1,-b,0,-t,1,-g,1");
        return Response.status(200).entity(aa).build();
    }

    @GET
    @Path("/download")
    @Produces("application/octet-stream")
    public Response getFile(@QueryParam("filename") String filename) {
        File file = new File(DEFAULT_PATH+"/"+filename);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                        "attachment; filename="+filename);
        return response.build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream inputS
                              ,@FormDataParam("file") FormDataContentDisposition fileDetail) {
        String fileName = fileDetail.getFileName();
        return Response.status(200).entity(ctr.getClusterFile(inputS,fileName)).build();
    }

    @GET
    @Path("/MysqlScriptNonResponse")
    @Produces(MediaType.APPLICATION_JSON+ ";charset=utf-8")
    public Response MysqlScriptNonResponse(@QueryParam("str") String str) {
        Response response;
        response = Response.status(200).entity(ctr.mySqlNonResponse(str)).build();
        return response;
    }
    
    @GET
    @Path("/MysqlCombo")
    @Produces(MediaType.APPLICATION_JSON)
    public Response MysqlCombo(@QueryParam("str") String str) {
        Response response;
        response = Response.status(200).entity(ctr.getComboData(str)).build();
        return response;
    }

    @GET
    @Path("/datafile")
    @Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response getDataFile(@QueryParam("sql") String sql) {       
        return Response.status(200).entity(ctr.DataFile(sql)).build();
    }
    /*
    ------------------------------Config---------------------------------------
    */
}
