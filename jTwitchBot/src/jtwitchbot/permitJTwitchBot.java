/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import java.util.*;

/**
 *
 * @author sebastianszemer
 */
public class permitJTwitchBot extends Thread{
    
    private List<String> permitList = new ArrayList<String>();
    private int permitTime = 30000; // milliseconds
    private String sender;
    //private String sender;
    
    public permitJTwitchBot() throws Exception{
        permitList.add(sender);
        System.out.println(sender);
        System.out.println(permitList.size());
        //Thread.sleep(permitTime);
        permitList.remove(0);
        System.out.println(permitList.size());
    }

    public void addToPermitList(String sender) {
        this.sender = sender;
        this.permitList.add(sender);
    }

    /**
     * @return the permitTime
     */
    public void removeFromPermitList() {
        this.permitList.remove(0);
    }

    /**
     * @param permitTime the permitTime to set
     */
    public void setPermitTime(int permitTime) {
        this.permitTime = permitTime;
    }

    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the permitTime
     */
    public int getPermitTime() {
        return permitTime;
    }
    
}
