package testProject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TestProject{
    public static void main(String args[]) throws IOException
    {
        String e="";
        try {
            URL url = new URL("https://api.twitch.tv/kraken/channels/sify11/follows?limit=1?offset=0?direction=desc?api_version=3?client_id=cgh1wq8lukv8cw5p214z11mvqsuggrd");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
            e= String.valueOf(statusCode);
            String textLine = input.readLine();
            JSONParser parser=new JSONParser();

            System.out.println("=======decode=======");

            String s="[0,{\"1\":{\"2\":{\"3\":{\"4\":[5,{\"6\":7}]}}}}]";
            Object obj=parser.parse(s);
            JSONArray array=(JSONArray)obj;
            System.out.println("======the 2nd element of array======");
            System.out.println(array.get(1));
            System.out.println();

            JSONObject obj2=(JSONObject)array.get(1);
            System.out.println("======field \"1\"==========");
            System.out.println(obj2.get("1"));    

            s="{}";
            obj=parser.parse(s);
            System.out.println(obj);

            s="[5,]";
            obj=parser.parse(s);
            System.out.println(obj);

            s="[5,,2]";
            obj=parser.parse(s);
            System.out.println(obj);
            
            System.out.println("=====================");
            
            s=textLine;
            obj=parser.parse(s);
            System.out.println(s);
            System.out.println(obj);
            JSONObject obj3 =(JSONObject) obj;
            JSONArray arrayOfFollowers = (JSONArray) obj3.get("follows");
            s=arrayOfFollowers.get(0).toString();
            obj=parser.parse(s);
            JSONObject obj4 = (JSONObject) obj;
            s=obj4.get("user").toString();
            obj=parser.parse(s);
            JSONObject followerName = (JSONObject) obj;
            System.out.println(followerName.get("name"));
            System.out.println(obj4.get("created_at"));
            
        } catch (Exception ex) {
            e = ex.toString();
        } finally {
            if(e.equals("200")){
                System.out.println("finally: "+e);
            }
            else{
                System.out.println("this went wrong: "+e);
            }
        }
    }
}