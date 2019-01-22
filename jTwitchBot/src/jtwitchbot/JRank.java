/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.User;

/**
 *
 * @author sebastianszemer
 */
public class JRank extends Thread {
    
    
    
    private String rankDB = "rank.db";
    private long sleepTimeOnline = 10000; //miliseconds
    private long sleepTimeOffline = 10000;
    private int rankPerSleepTimeOnline = 1;
    private int rankPerSleepTimeOffline = 1;
    private String[] lineRead;
    private int oldRank;
    private int newRank;
    private String newUserRank;
    private boolean userFound;
    private User[] currentUserList;
    private int rankMod = 360;
        
    public void JRank () {
        
    }
    
    public void stopMe(){
        this.interrupt();
    }
    
    public void run(){
        
            try {            
                while (true){
                    Thread.sleep(sleepTimeOnline);
                    JTwitchBot bot = JTwitchBot.getInstance();
                    //System.out.println("is stream online? " + bot.isOnline());
                    if(bot.isOnline() == true){
                        currentUserList = bot.getUsers(JTwitchBotMain.getChannel());
                        String a = "";
                        for (int i=0; i<currentUserList.length;i++){
                            if (!bot.noCoinsForYou(currentUserList[i].toString())) {
                            addRank(currentUserList[i].toString(), rankPerSleepTimeOnline);
                            //System.out.println("dropping xp for: " + currentUserList[i]);
                            if((getRank(currentUserList[i].toString()) % getRankMod()) == 0){
                                int currentLvl = getRank(currentUserList[i].toString()) / getRankMod();
                                bot.sendMessage(JTwitchBotMain.getChannel(), currentUserList[i].toString() + " reached rank " + currentLvl);
                            }
                            a = a + currentUserList[i].toString() + ", ";
                            }
                        }
                        //System.out.println("Dropping XP for: " + a);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(JCoins.class.getName()).log(Level.SEVERE, null, ex);
            }
        
                
    }
    
    public String getTopRank(){
        
        HashMap<String, Integer> topCoins = new HashMap<>();
        
        String line = null;
        String c = "";

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(rankDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line);
                if(!line.equals("")){
                    lineRead = line.split(" ");
                    topCoins.put(lineRead[0], Integer.parseInt(lineRead[1]));
                }
            }  
            HashMap<Integer, String> sortedMap = sortByValues(topCoins); 
            String a = sortedMap.keySet().toString().substring(1, sortedMap.keySet().toString().length()-1);
            String[] b = a.split(",");
            topCoins.clear();
            for(int i=b.length;i>b.length-10;i--){
                //System.out.println(b[i-1].trim());
                //System.out.println(getCoins(b[i-1].trim()));
                //topCoins.put(b[i-1].trim(), getRank(b[i-1].trim()));
                c=c+b[i-1].trim()+" (rank "+getRank(b[i-1].trim())/rankMod+"), ";
            }
            //System.out.println(c);
            // Always close files.
            bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                rankDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("getCoins Error reading file '" 
                + rankDB + "'");                   
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
    
    public boolean hasNickName(String user){
        
        String userNickName = "";
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(rankDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                lineRead = line.split(" ");
                if (lineRead[0].equals(user.toLowerCase())){
                    if(lineRead.length==3){
                        return true;
                    }
                }
            }    

            // Always close files.
            bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                rankDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("getRank Error reading file '" 
                + rankDB + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return false;
    }
    
    public String getNickname(String user){
        
        String userNickName = "";
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(rankDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                lineRead = line.split(" ");
                if (lineRead[0].equals(user.toLowerCase())){
                    if(lineRead.length==3){
                        userNickName = lineRead[2];
                        System.out.println(lineRead[2]);
                    }
                }
            }    

            // Always close files.
            bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                rankDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("getRank Error reading file '" 
                + rankDB + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return userNickName;
    }
    
    public int getRank(String user){
        
        int userRank = 0;
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(rankDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                lineRead = line.split(" ");
                if (lineRead[0].equals(user.toLowerCase())){
                userRank = Integer.parseInt(lineRead[1]);
                }
            }    

            // Always close files.
            bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                rankDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("getRank Error reading file '" 
                + rankDB + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return userRank;
    }
    
    public void setNickName(String user, String nickName){
        
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at a time
        String line = null;
        userFound = false;
        boolean nickNameFound=false;
        String oldNickname = "";

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(rankDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                lineRead = line.split(" ");
                //System.out.println(lineRead[0]);
                if (lineRead[0].equals(user.toLowerCase())){
                    userFound = true;
                    oldRank = Integer.parseInt(lineRead[1]);
                    if(lineRead.length==3){
                        oldNickname = lineRead[2];
                        nickNameFound = true;
                    }
                    //bufferedReader.close();  
                }
                else {
                    newUserRank = "\n" + user + " " + "0" + " " + nickName;                    
                }
            }   
            bufferedReader.close(); 
            //System.out.println(userFound);
            if (userFound){
                if(nickNameFound){
                    replaceSelected(user.toLowerCase() + " ", String.valueOf(oldRank) + " " + oldNickname ,String.valueOf(oldRank) + " " + nickName);
                }else{
                    replaceSelected(user.toLowerCase() + " ", String.valueOf(oldRank),String.valueOf(oldRank) + " " + nickName);
                }
            }
            else{
                Files.write(Paths.get(rankDB), newUserRank.getBytes(), StandardOpenOption.APPEND);                    
            }
            //System.out.println(user + " new rank " + newRank);

            // Always close files.
            //bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                rankDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("addRank Error reading file '" 
                + rankDB + "'");                   
            // Or we could just do this: 
            ex.printStackTrace();
        }
        
    }
    
    public void addRank(String user, int rank){
        
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at a time
        String line = null;
        userFound = false;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(rankDB);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                lineRead = line.split(" ");
                //System.out.println(lineRead[0]);
                if (lineRead[0].equals(user.toLowerCase())){
                    userFound = true;
                    oldRank = Integer.parseInt(lineRead[1]);
                    newRank = rank + oldRank;
                    //bufferedReader.close();  
                }
                else {
                    newUserRank = "\n" + user + " " + rank;                    
                }
            }   
            bufferedReader.close(); 
            //System.out.println(userFound);
            if (userFound){
                replaceSelected(user.toLowerCase() + " ", String.valueOf(oldRank) ,String.valueOf(newRank));
            }
            else{
                Files.write(Paths.get(rankDB), newUserRank.getBytes(), StandardOpenOption.APPEND);                    
            }
            //System.out.println(user + " new rank " + newRank);

            // Always close files.
            //bufferedReader.close();            
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                rankDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("addRank Error reading file '" 
                + rankDB + "'");                   
            // Or we could just do this: 
            ex.printStackTrace();
        }
        
    }
    
    public void replaceSelected(String replaceWith, String oldRank, String newRank) {
    try {
        // input the file content to the String "input"
        BufferedReader file = new BufferedReader(new FileReader(rankDB));
        String line;String input = "";

        while ((line = file.readLine()) != null) input += line + '\n'; //this needs improvement

        file.close();

        //System.out.println(input); // check that it's inputted right

        input = input.replace(replaceWith + String.valueOf(oldRank), replaceWith + String.valueOf(newRank)); 

        // check if the new input is right
        //System.out.println("----------------------------------"  + '\n' + input);

        // write the new String with the replaced line OVER the same file
        FileOutputStream fileOut = new FileOutputStream(rankDB);
        fileOut.write(input.getBytes());
        fileOut.close();

    } catch (IOException e) {
        System.out.println("Problem reading file.");
    }
}

    /**
     * @return the rankMod
     */
    public int getRankMod() {
        return rankMod;
    }

    /**
     * @param rankMod the rankMod to set
     */
    public void setRankMod(int rankMod) {
        this.rankMod = rankMod;
    }
    
}
