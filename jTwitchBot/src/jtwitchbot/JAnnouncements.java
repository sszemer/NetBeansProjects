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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebastianszemer
 */
public class JAnnouncements extends Thread{
    
    private long sleepTime;
    private String messagesDB;
    private int currentMessage;
    private boolean announcementsON;
    
    public JAnnouncements(){
        setMessagesDB("messages.db");
        setSleepTime(600000);
        setCurrentMessage(getMessages().size());
        System.out.println("announcements on? "+JTwitchBotGUI.getInstance().getEnableAnnouncements().isSelected());
    }
    
    public void stopMe(){
        this.interrupt();
    }
    
    public void run(){
        try {            
            Thread.sleep(getSleepTime()/10);
            while (true){
                Thread.sleep(getSleepTime());
                JTwitchBot bot = JTwitchBot.getInstance();
                //System.out.println(getMessages().size());
                String a = getMessages().keySet().toString().substring(1, getMessages().keySet().toString().length()-1);
                String[] b = a.split(",");
                for(int i = 0; i<b.length;i++){
                    //System.out.println(b[i].trim());
                    if(getCurrentMessage()<=getMessages().size()){
                    bot.sendMessage(JTwitchBotMain.getChannel(), getMessages().get(b[getCurrentMessage()-1].trim()));
                    if(getCurrentMessage()==getMessages().size()){
                        setCurrentMessage(1);
                        break;
                    }
                    else{
                        setCurrentMessage(getCurrentMessage()+1);
                        break;
                    }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(JCoins.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addMessage(String messageName, String messageBody){
        
        String commandLine = "\n" + messageName + " " + messageBody;
                
        try {                    
            Files.write(Paths.get(getMessagesDB()), commandLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            Logger.getLogger(JTwitchBot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public boolean isInMessages(String messageName){
        
        if(getMessages().containsKey(messageName)){
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void removeMessage(String messageName){
        
        try {
            String input;
            try ( // input the file content to the String "input"
                    BufferedReader file = new BufferedReader(new FileReader(getMessagesDB()))) {
                String line;
                input = "";
                while ((line = file.readLine()) != null){
                    if(!line.equals("")){
                        input += line + '\n';  //this needs improvement                      
                    }
                } 
            }

        //System.out.println(input); // check that it's inputted right

        input = input.replace(messageName + " " + getMessages().get(messageName), ""); 

        // check if the new input is right
        //System.out.println("----------------------------------"  + '\n' + input);

        // write the new String with the replaced line OVER the same file
        FileOutputStream fileOut = new FileOutputStream(getMessagesDB());
        fileOut.write(input.getBytes());
        fileOut.close();

        } catch (IOException e) {
            System.out.println("Problem reading file.");
        }
    }
    
    public HashMap<String, String> getMessages(){
        
        HashMap<String, String> messages = new HashMap<>();
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at b time
        String line;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(getMessagesDB());

            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while((line = bufferedReader.readLine()) != null) {
                    if(!line.equals("")){
                        String[] lineRead = line.split(" ");
                        String messageName = lineRead[0];
                        String messageBody = line.substring(messageName.length()+1, line.length());
                        //System.out.println("messageName: " + messageName);
                        //System.out.println("messageBody: " + messageBody);
                        messages.put(messageName, messageBody);
                        //System.out.println("command in HashMap: " + messages.get(messageName));
                    }
                }
                // Always close files.
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                getMessagesDB() + "'");                
        }
        catch(IOException ex) {
            System.out.println("getCustomCommands Error reading file '" 
                + getMessagesDB() + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        //System.out.println(messages.keySet());
        return messages;
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
     * @return the messagesDB
     */
    public String getMessagesDB() {
        return messagesDB;
    }

    /**
     * @param messagesDB the messagesDB to set
     */
    public void setMessagesDB(String messagesDB) {
        this.messagesDB = messagesDB;
    }

    /**
     * @return the currentMessage
     */
    public int getCurrentMessage() {
        return currentMessage;
    }

    /**
     * @param currentMessage the currentMessage to set
     */
    public void setCurrentMessage(int currentMessage) {
        this.currentMessage = currentMessage;
    }

    /**
     * @return the announcementsON
     */
    public boolean isAnnouncementsON() {
        return announcementsON;
    }

    /**
     * @param announcementsON the announcementsON to set
     */
    public void setAnnouncementsON(boolean announcementsON) {
        this.announcementsON = announcementsON;
    }
    
}
