/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.jibble.pircbot.User;

/**
 *
 * @author sebastianszemer
 */
public class JCoins extends Thread {
    
    private String coinsDB = "coins.db";
    private long sleepTimeOnline = 600000; //miliseconds
    private long sleepTimeOffline = 600000;
    private int coinsPerSleepTimeOnline = 10;
    private int coinsPerSleepTimeOffline = 2;
    private String[] lineRead;
    private int oldCoins;
    private int newCoins;
    private String newUserCoins;
    private boolean userFound;
    private User[] currentUserList;
        
    public void JCoins () {
        

    }
    
    public void stopMe(){
        this.interrupt();
    }
    
    public void run(){
        
        
            try {            
                while (!this.isInterrupted()){
                    Thread.sleep(sleepTimeOnline);
                    JTwitchBot bot = JTwitchBot.getInstance();
                    bot.sendRawLineViaQueue("join " + JTwitchBotMain.getChannel());
                    currentUserList = bot.getUsers(JTwitchBotMain.getChannel());
                    //System.out.println("user list length: "+currentUserList.length);
                    String a = "";
                    String[] currentUserListStringArray;
                    currentUserListStringArray = new String[currentUserList.length];
                    for ( int i = 0; i<currentUserList.length; i++){
                        currentUserListStringArray[i] = currentUserList[i].toString();
                        //System.out.println("current user in linst: "+currentUserListStringArray[i]);
                    }
                    if (bot.isOnline() == true){
                        for (int i=0; i<currentUserList.length;i++){
                            //System.out.println(currentUserList[i].toString());
                            if (!bot.noCoinsForYou(currentUserList[i].toString())) {
                                addCoins(currentUserList[i].toString(), coinsPerSleepTimeOnline);
                                a = a + currentUserList[i].toString() + ", ";
                                //System.out.println("coins when online");
                            }
                        }
                    }
                    else {
                        for (int i=0; i<currentUserList.length;i++){
                            if (!bot.noCoinsForYou(currentUserList[i].toString())) {
                                addCoins(currentUserList[i].toString(), coinsPerSleepTimeOffline);
                                a = a + currentUserList[i].toString() + ", ";
                                //System.out.println("coins when offline");
                            }
                        }

                    }
                    if(a.length()>1){
                        bot.sendMessage(JTwitchBotMain.getChannel(), "Makin' it rain for: " + a.substring(0, a.length()-2)+".");
                    }
                    else
                    {
                        System.out.println("no users in chat to get coins");
                    }
                    
                    System.out.println("users to get coins: "+a);
                }
            } catch (Exception ex) {
                Logger.getLogger(JCoins.class.getName()).log(Level.SEVERE, null, ex);
            }
        
                
    }
    
    public String getTopCoins(){
        
        HashMap<String, Integer> topCoins = new HashMap<>();
        
        String line = null;
        String c = "";

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(coinsDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if(!line.equals("")){
                    lineRead = line.split(" ");
                    if(!lineRead[0].equals(JTwitchBotMain.getBroadcaster().toLowerCase())){
                        topCoins.put(lineRead[0], Integer.parseInt(lineRead[1]));
                    }
                }
            }  
            HashMap<Integer, String> sortedMap = sortByValues(topCoins); 
            String a = sortedMap.keySet().toString().substring(1, sortedMap.keySet().toString().length()-1);
            String[] b = a.split(",");
            topCoins.clear();
            for(int i=b.length;i>b.length-10;i--){
                //System.out.println(b[i-1].trim());
                //System.out.println(getCoins(b[i-1].trim()));
                //topCoins.put(b[i-1].trim(), getCoins(b[i-1].trim()));
                c=c+b[i-1].trim()+" ("+getCoins(b[i-1].trim())+" coins), ";
            }
            //System.out.println(c);
            // Always close files.
            bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                coinsDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("getCoins Error reading file '" 
                + coinsDB + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return c.substring(0, c.length()-2)+".";
    }
    
    private HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return ((Comparable) ((Map.Entry) (o1)).getValue())
                  .compareTo(((Map.Entry) (o2)).getValue());
            }
       });

       // Here I am copying the sorted list in sortedMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }
    
    public int getCoins(String user){
        
        int userCoins = 0;
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(coinsDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                lineRead = line.split(" ");
                if (lineRead[0].equals(user.toLowerCase())){
                userCoins = Integer.parseInt(lineRead[1]);
                }
            }    

            // Always close files.
            bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                coinsDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("getCoins Error reading file '" 
                + coinsDB + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return userCoins;
    }
    
    public void addCoins(String user, int coins){
        
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at a time
        String line = null;
        userFound = false;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(coinsDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                lineRead = line.split(" ");
                //System.out.println(lineRead[0]);
                if (lineRead[0].equals(user.toLowerCase())){
                    userFound = true;
                    oldCoins = Integer.parseInt(lineRead[1]);
                    newCoins = coins + oldCoins;
                    //bufferedReader.close();  
                }
                else {
                    newUserCoins = "\n" + user + " " + coins;                    
                }
            }   
            bufferedReader.close(); 
            //System.out.println(userFound);
            if (userFound){
                replaceSelected(user.toLowerCase() + " ", String.valueOf(oldCoins) ,String.valueOf(newCoins));
            }
            else{
                Files.write(Paths.get(coinsDB), newUserCoins.getBytes(), StandardOpenOption.APPEND);                    
            }
            //System.out.println(user + " new coins " + newCoins);

            // Always close files.
            //bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                coinsDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("addCoins Error reading file '" 
                + coinsDB + "'");                   
            // Or we could just do this: 
            ex.printStackTrace();
        }
        
    }
    
    public void replaceSelected(String user, String oldCoins, String newCoins) {
        try {
            // input the file content to the String "input"
            BufferedReader file = new BufferedReader(new FileReader(coinsDB));
            String line;
            String input = "";

            while ((line = file.readLine()) != null) input += line + '\n'; //this needs improvement

            file.close();

            //System.out.println(input); // check that it's inputted right

            input = input.replace(user + String.valueOf(oldCoins), user + String.valueOf(newCoins)); 

            // check if the new input is right
            //System.out.println("----------------------------------"  + '\n' + input);

            // write the new String with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(coinsDB);
            fileOut.write(input.getBytes());
            fileOut.close();

        } catch (IOException e) {
            System.out.println("Problem reading file.");
        }
    }
    
}
