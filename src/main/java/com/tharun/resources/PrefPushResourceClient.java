package com.tharun.resources;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
public class PrefPushResourceClient {
 public static void main(String[] argv) throws JSONException, IOException {
 JSONStringer jsRequest = new JSONStringer();
 JSONStringer js = new JSONStringer();
 try {
 js.object();
 jsRequest.object();
 jsRequest.key("user_email").value("tharuntej@gmail.com");
// jsRequest.key("item_url").value("http://thoughtclicks.com/status");
// jsRequest.key("source").value("tap");
// jsRequest.key("action").value("try");
// jsRequest.key("score").value(2.0);
 jsRequest.endObject();
 js.key("request").value(jsRequest);
 js.endObject();
 } catch (JSONException e1) {
 // TODO Auto-generated catch block
 e1.printStackTrace();
 }
 System.out.println(jsRequest.toString());
 System.out.println(js.toString());
 ClientResource requestResource = new ClientResource(
 "http://localhost:8183/discovery/pref/routes");
 Representation rep;
 rep = new JsonRepresentation(js);
 rep.setMediaType(MediaType.APPLICATION_JSON);
 Representation reply = requestResource.post(rep);
 String replyText = reply.getText();
 System.out.println("Reply Text:" + replyText);
 System.out.println("Reply Media Type:" + reply.getMediaType());
 
 
// reply.write(System.out);
// if (reply.getMediaType().equals(new MediaType("application/json"))) {
// JSONObject jsObj = new JSONObject(replyText);
// String code = jsObj.getString("CODE");
// String desc = jsObj.getString("DESC");
// System.out.println("Code:" + code + ",DESC:" + desc);
// }
 reply.release();
 }
}