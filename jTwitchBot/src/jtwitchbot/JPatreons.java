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
public class JPatreons extends Thread {
    
    private long sleepTime;
    private int previousPatreons;
    private int currentPatreons;

    public JPatreons(){
        setSleepTime(10000);
        setPreviousPatreons(0);
        setCurrentPatreons(0);
    }
    
    
    public void stopMe(){
        this.interrupt();
    }
    
    public void run(){
        
        //setPreviousHosts(getHosts());
        
        while (true){
            try {
                Thread.sleep(getSleepTime());
                previousPatreons = getPatreons();
                if(previousPatreons!=0){
                    if(previousPatreons < currentPatreons){
                        JTwitchBot.getInstance().sendMessage(JTwitchBotMain.getChannel(), "You have a new patreon sub! (total:"+currentPatreons+")");
                    }
                    else{
                        //System.out.println("no new followers");
                    }
                    previousPatreons = currentPatreons;
                    
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(JHosts.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(JHosts.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public int getPatreons(){
        
        int patreons=0;
        String e="";
        try {
            URL url = new URL("http://www.api.patreon.com/user/974612");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            int statusCode = http.getResponseCode();
            e= String.valueOf(statusCode);
            if(statusCode==200){
                //System.out.println("twitch request response code: "+statusCode);
                BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
                String textLine = input.readLine();
                while((textLine = input.readLine()) != null){    
                    
                    String[] splitLine = textLine.split(",");

                    //System.out.println(splitLine[2].substring(9, splitLine[2].length()-1));
                    for(int i=0; i<splitLine.length;i++){
                        //System.out.println(splitLine[i]);
                        if(splitLine[i].contains("\"patron_count\":")){
                            //System.out.println("patreon count: "+splitLine[i]);//.substring(10, splitLine[i].length()-1));
                            patreons=Integer.parseInt(splitLine[i].substring(28, splitLine[i].length()));

                        }
                    }
                    
                }
                input.close();

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
                System.out.println("this went wrong with the patreons request: "+e);
            }
        }
        
        return patreons;
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
     * @return the previousPatreons
     */
    public int getPreviousPatreons() {
        return previousPatreons;
    }

    /**
     * @param previousPatreons the previousPatreons to set
     */
    public void setPreviousPatreons(int previousPatreons) {
        this.previousPatreons = previousPatreons;
    }

    /**
     * @return the currentPatreons
     */
    public int getCurrentPatreons() {
        return currentPatreons;
    }

    /**
     * @param currentPatreons the currentPatreons to set
     */
    public void setCurrentPatreons(int currentPatreons) {
        this.currentPatreons = currentPatreons;
    }
    
}
