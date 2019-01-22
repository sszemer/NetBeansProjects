/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebastianszemer
 */
public class JHosts extends Thread{
    
    private long sleepTime;
    private List<String> previousHosts;
    private List<String> currentHosts;
    private List<String> newHosts;
    private String twitchID;
    
    public JHosts(){
        setSleepTime(10000);
        setPreviousHosts(new ArrayList<String>());
        setCurrentHosts(new ArrayList<String>());
        setNewHosts(new ArrayList<String>());
        setTwitchID("");
        setTwitchID(downloadTwitchID());
    }
    
    public void stopMe(){
        this.interrupt();
    }
    
    public void run(){
        
        //setPreviousHosts(getHosts());
        
        while (true){
            try {
                if(getTwitchID().equals("")){
                    setTwitchID(downloadTwitchID());
                }
                Thread.sleep(getSleepTime());
                currentHosts = getHosts();
                if(!currentHosts.isEmpty()){
                    if(!previousHosts.equals(currentHosts)){
                        for(int i=0; i<currentHosts.size();i++){
                            if(!previousHosts.contains(currentHosts.get(i))){
                                JTwitchBot.getInstance().sendMessage(JTwitchBotMain.getChannel(), currentHosts.get(i) + " is now hosting the channel with "+getViewerCount(currentHosts.get(i))+" viewers, thx!");
                                newHosts.add(currentHosts.get(i));
                            }
                        }
                    }
                    else{
                        //System.out.println("no new followers");
                    }
                    previousHosts = currentHosts;
                    
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(JHosts.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(JHosts.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public int getViewerCount(String host){
        int viewerCount = 0;
        String e="";
        try {
            URL url = new URL("https://api.twitch.tv/kraken/streams/"+host+"/?limit=100?offset=0?direction=desc?api_version=3");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            e= String.valueOf(statusCode);
            if(statusCode==200){
                //System.out.println("twitch request response code: "+statusCode);
                BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
                String textLine = input.readLine();
                String[] splitLine = textLine.split(",");

                input.close();

                //System.out.println(splitLine[2].substring(9, splitLine[2].length()-1));

                for(int i=0; i<splitLine.length;i++){
                    if(splitLine[i].contains("\"viewers\":")){
                        //System.out.println(splitLine[i]);
                        //System.out.println(splitLine[i].substring(10, splitLine[i].length()));
                        viewerCount = Integer.parseInt(splitLine[i].substring(10, splitLine[i].length()));
                        //JTwitchBot.getInstance().sendRawLineViaQueue("join #" + host);
                        //viewerCount = JTwitchBot.getInstance().getUsers("#"+host).length;
    
                    }
                }
            }
            else{
                System.out.println("getViewerCount() request reponse: "+statusCode);
                //JTwitchBot.getInstance().sendRawLineViaQueue("join #" + host);
                viewerCount = JTwitchBot.getInstance().getUsers("#"+host).length;
            }

        } catch (Exception ex) {
            e = ex.toString();
        } finally {
            if(e.equals("200")){
                //System.out.println("finally: "+e);
            }
            else{
                System.out.println("this went wrong with the getViewerCount() request: "+e);
            }
        }
        
        return viewerCount;
    }
    
    public List<String> getHosts(){
        
        List<String> hosts = new ArrayList<String>();
        String e="";
        try {
            URL url = new URL("https://tmi.twitch.tv/hosts?include_logins=1&target="+getTwitchID());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            e= String.valueOf(statusCode);
            if(statusCode==200){
                //System.out.println("twitch request response code: "+statusCode);
                BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
                String textLine = input.readLine();
                String[] splitLine = textLine.split(",");

                input.close();

                //System.out.println(splitLine[2].substring(9, splitLine[2].length()-1));

                for(int i=0; i<splitLine.length;i++){
                    //System.out.println(splitLine[i]);
                    if(splitLine[i].contains("\"host_login\":\"")){
                        //System.out.println(splitLine[i].substring(14, splitLine[i].length()-1));
                        hosts.add(splitLine[i].substring(14, splitLine[i].length()-1));
    
                    }
                }
            }
            else{
                System.out.println("getHosts() request reponse: "+statusCode);
            }

        } catch (Exception ex) {
            e = ex.toString();
        } finally {
            if(e.equals("200")){
                //System.out.println("finally: "+e);
            }
            else{
                System.out.println("this went wrong with the twitch request: "+e);
            }
        }
        
        return hosts;
    }
    
    public String downloadTwitchID(){
        String twitchID="";
        String e="";
        try {
            URL url = new URL("https://api.twitch.tv/kraken/channels/"+JTwitchBotMain.getBroadcaster()+"?api_version=3?client_id="+JTwitchBotMain.getTwitchAppID());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            e= String.valueOf(statusCode);
            if(statusCode==200){
                //System.out.println("twitch request response code: "+statusCode);
                BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
                String textLine = input.readLine();
                String[] splitLine = textLine.split(",");

                input.close();

                //System.out.println(splitLine[2].substring(9, splitLine[2].length()-1));

                for(int i=0; i<splitLine.length;i++){
                    //System.out.println(splitLine[i]);
                    if(splitLine[i].contains("\"_id\":")){
                        System.out.println("TwitchID: "+splitLine[i].substring(6, splitLine[i].length()));
                        twitchID=splitLine[i].substring(6, splitLine[i].length());
                    }
                }
            }
            else{
                System.out.println("getTwitchID() request reponse: "+statusCode);
            }

        } catch (Exception ex) {
            e = ex.toString();
        } finally {
            if(e.equals("200")){
                //System.out.println("finally: "+e);
            }
            else{
                System.out.println("this went wrong with the twitch request: "+e);
            }
        }
        return twitchID;
    }

    /**
     * @return the sleepTime
     */
    public long getSleepTime() {
        return sleepTime;
    }

    /**
     * @param sleeptime the sleepTime to set
     */
    public void setSleepTime(long sleeptime) {
        this.sleepTime = sleeptime;
    }

    /**
     * @return the previousHosts
     */
    public List<String> getPreviousHosts() {
        return previousHosts;
    }

    /**
     * @param previousHosts the previousHosts to set
     */
    public void setPreviousHosts(List<String> previousHosts) {
        this.previousHosts = previousHosts;
    }

    /**
     * @return the currentHosts
     */
    public List<String> getCurrentHosts() {
        return currentHosts;
    }

    /**
     * @param currentHosts the currentHosts to set
     */
    public void setCurrentHosts(List<String> currentHosts) {
        this.currentHosts = currentHosts;
    }

    /**
     * @return the newHosts
     */
    public List<String> getNewHosts() {
        return newHosts;
    }

    /**
     * @param newHosts the newHosts to set
     */
    public void setNewHosts(List<String> newHosts) {
        this.newHosts = newHosts;
    }

    /**
     * @return the twitchID
     */
    public String getTwitchID() {
        return twitchID;
    }

    /**
     * @param twitchID the twitchID to set
     */
    public void setTwitchID(String twitchID) {
        this.twitchID = twitchID;
    }
    
}
