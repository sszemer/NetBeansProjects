/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebastianszemer
 */
public class JFollowers extends Thread {
    
    private long sleepTime = 60000; //miliseconds
    private List<String> previousFollowers = new ArrayList<String>();
    private List<String> currentFollowers = new ArrayList<String>();
    private List<String> newFollowers = new ArrayList<String>();
    private int followersCount;
    
    public void JFollowers(){
        
    }
    
    public void stopMe(){
        this.interrupt();
    }
    
    public void run(){
        
        try {
            previousFollowers = getFollowers();
        } catch (IOException ex) {
            Logger.getLogger(JFollowers.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        while(true){
                
            try {            
                while (JTwitchBot.getInstance().isConnected()){
                    Thread.sleep(getSleepTime());
                    currentFollowers = getFollowers();
                    if(!currentFollowers.isEmpty()){

                        if(!previousFollowers.equals(currentFollowers)){
                            for(int i=0; i<currentFollowers.size();i++){
                                if(!previousFollowers.contains(currentFollowers.get(i))){
                                    JTwitchBot.getInstance().sendMessage(JTwitchBotMain.getChannel(), "Welcome new follower: " + currentFollowers.get(i) + "! PogChamp (total:" + getFollowersCount() + ")");
                                    newFollowers.add(currentFollowers.get(i));
                                    
                                }
                            }
                        }
                        else{
                            //System.out.println("no new followers");
                        }
                        previousFollowers = currentFollowers;
                    }
                    else{
                        System.out.println("it appears that we didn't get a responce from twitch");
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(JCoins.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    public List<String> getFollowers() throws UnsupportedEncodingException, IOException{
        
        List<String> followers = new ArrayList<String>();
        String e="";

        try {
            URL url = new URL("https://api.twitch.tv/kraken/channels/"+JTwitchBotMain.getBroadcaster()+"/follows?direction=DESC&limit=1&offset=0?client_id="+JTwitchBotMain.getTwitchAppID());
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            e= String.valueOf(statusCode);
            
            if(statusCode==200){
                
                BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

                String textLine = input.readLine();
                if(!textLine.equals("null")){

                    String[] splitLine = textLine.split(",");
                    for(int i = 0; i<splitLine.length; i++){
                        if(splitLine[i].contains("display_name")){
                            followers.add(splitLine[i].substring(16, splitLine[i].length()-1));
                            //System.out.println(splitLine[i].substring(16, splitLine[i].length()-1));
                        }
                        if(splitLine[i].contains("_total")){
                            setFollowersCount(Integer.parseInt(splitLine[i].substring(9, splitLine[i].length())));
                            //System.out.println(splitLine[i].substring(9, splitLine[i].length()));
                        }
                    }

                }
                input.close();
            }
            else{
                System.out.println("getFollowers responce code: "+ statusCode);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(JFollowers.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return followers;
        
    }

    /**
     * @return the sleepTime
     */
    public long getSleepTime() {
        return sleepTime;
    }

    /**
     * @param sleepTime the sleepTime to set
     */
    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * @return the previousFollowers
     */
    public List<String> getPreviousFollowers() {
        return previousFollowers;
    }

    /**
     * @param previousFollowers the previousFollowers to set
     */
    public void setPreviousFollowers(List<String> previousFollowers) {
        this.previousFollowers = previousFollowers;
    }

    /**
     * @return the currentFollowers
     */
    public List<String> getCurrentFollowers() {
        return currentFollowers;
    }

    /**
     * @param currentFollowers the currentFollowers to set
     */
    public void setCurrentFollowers(List<String> currentFollowers) {
        this.currentFollowers = currentFollowers;
    }

    /**
     * @return the newFollowers
     */
    public List<String> getNewFollowers() {
        return newFollowers;
    }

    /**
     * @param newFollowers the newFollowers to set
     */
    public void setNewFollowers(List<String> newFollowers) {
        this.newFollowers = newFollowers;
    }

    /**
     * @return the followersCount
     */
    public int getFollowersCount() {
        return followersCount;
    }

    /**
     * @param followersCount the followersCount to set
     */
    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }
    
}
