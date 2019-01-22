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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jibble.pircbot.*;
import twitter4j.TwitterException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
/**
 *
 * @author sebastianszemer
 */
public class JTwitchBot extends PircBot {

    private String[] linkWhiteList = {"youtube.com", "imgur.com", "youtu.be", "soundcloud.com", "osu.ppy.sh", "http://puu.sh/", "preinstall21.wix.com", "twitch.tv/linlindork"};
    private String[] permitLogin; //to be removed
    private String[] addCoins; //to be removed
    private String[] isLink = {"http://", "https://", "www.", "www1.", "www2.", "www3.", ".com", ".net", ".org", ".io", ".pl", ".tv", ".co.uk", ".info", ".ly", ".gl"};
    //private permitJTwitchBot permitThingy;
    private HashMap<String, String> permitList = new HashMap<String, String>();
    private HashMap<String, String> duelList = new HashMap<String, String>();
    private String youTubeLink;
    private HashMap<String, String> hmap = new HashMap<String, String>();
    private JCoins coins = new JCoins();
    private JTwitter t = new JTwitter();
    private JRank rank = new JRank();
    private JFollowers followers = new JFollowers();
    private JHosts hosts = new JHosts();
    private JPatreons patreons = new JPatreons();
    private JAnnouncements announcements = new JAnnouncements();
    private User[] userList;
    private String currentTopic;
    private static JTwitchBot instance = null;
    private String[] noCoinsForYou = {JTwitchBotMain.getBroadcaster(), JTwitchBotMain.getBotUserName(), "mikuia", "moobot", "nightbot", "wizebot"};
    private boolean heistStarted = false;
    private HashMap<String, String> heistersCoins = new HashMap<String, String>();
    private List<String> heistersNames = new ArrayList<String>();
    private Random generator = new Random();
    private boolean dockingOnJoinEnabled = false;
    private String customCommandsDB = "commands.db";
    private long heistEndTime;
    private long heistDelay = 120000;
    private boolean previousWasOnline = false;
            
    public JTwitchBot () throws Exception {
        this.setName("dockingbot"); //twitch username here
        coins.start();
        rank.start();
        followers.start();
        hosts.start();
        patreons.start();
        announcements.start();
    }
    
    public void quitBot(){
        coins = null;
        rank=null;
        hosts=null;
        patreons=null;
        followers=null;
        announcements=null;
        instance = null;
    }
        
    public void onUnknown(String line) {
            
        if (line.contains("PRIVMSG")){
            String[] senderFind = line.split("!");  
            String[] senderAnotherFind = senderFind[1].split("@");
            //System.out.println("channamelength: " + JTwitchBotMain.getChannel().length());
            //System.out.println("indexof: " + line.indexOf(JTwitchBotMain.getChannel()));
            System.out.println("line: " + line);
            String message = line.substring(line.indexOf(JTwitchBotMain.getChannel())+JTwitchBotMain.getChannel().length()+2);
            String sender = senderAnotherFind[0];
            String tags = senderFind[0].substring(0, senderFind[0].length()-sender.length()-2);
            System.out.println("tags: " + tags);

            System.out.println("sender: " + senderAnotherFind[0]);    
            System.out.println("message: " + message);     
            onMessage(JTwitchBotMain.getChannel(), sender, isMod(tags), tags, message);
                      
        }
        else if(line.contains("HOSTTARGET "+JTwitchBotMain.getChannel())){
            String hostedChannel=line.substring(line.indexOf(JTwitchBotMain.getChannel())+JTwitchBotMain.getChannel().length()+2).substring(0, line.substring(line.indexOf(JTwitchBotMain.getChannel())+JTwitchBotMain.getChannel().length()+2).length()-2);
            int viewerCount=hosts.getViewerCount(hostedChannel);
            System.out.println("hostedChannel: "+hostedChannel);
            System.out.println("viewerCount: "+viewerCount);
            //sendMessage("#"+hostedChannel, "/me "+JTwitchBotMain.getBroadcaster() + " is now hosting you for "+ viewerCount + " viewers! such a nice person, leave a follow on their channel :)");
        }
    }
    
    public boolean isNumeric(String str) {  
        try  
        {  
            int d = Integer.parseInt(str);  
        }  
        catch(NumberFormatException nfe)  
        {  
            return false;  
        }  
        return true;  
    }
    
    public static JTwitchBot getInstance() throws Exception{
        if (instance == null){
            instance = new JTwitchBot();            
        }
        return instance;
    }
    
    public void addToDuelList(String sender, long time){
        duelList.put(sender.toLowerCase(), String.valueOf(time));
    }
    
    public boolean isInDuelList (String sender) {
        if (duelList.containsKey(sender.toLowerCase())){
            long oldTime = Long.valueOf(duelList.get(sender.toLowerCase()));
            long currentTime = System.currentTimeMillis();
            System.out.println(currentTime + " - " + oldTime + " = " + (currentTime-oldTime));
            if ((currentTime - oldTime) < 86400000) {
                return true;            
            }
            else
                duelList.remove(sender);
                return false;
                
        }
        else
            return false;        
    }
    
    public boolean isReadyForHeist(){
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime-getHeistEndTime();
        System.out.println("benkheist delay counter: "+ currentTime + "-" + getHeistEndTime() + "="+ timeElapsed);
        if (timeElapsed > getHeistDelay()){
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void addToPermitList(String sender, long time) {
        
        permitList.put(sender.toLowerCase(), String.valueOf(time));
        
    }
    
    public boolean noCoinsForYou (String sender) {
        
        if (Arrays.asList(getNoCoinsForYou()).contains(sender) == true) {
            return true;
        }
        else
            return false;        
    }
    
    public void onConnect(){
        System.out.println("onConnect() - is connected");
        JTwitchBotGUI.getInstance().getStatusLabel().setText("Online");
        System.out.println(JTwitchBotGUI.getInstance().getStatusLabel().getText());
        JTwitchBotGUI.getInstance().getStatusLabel().repaint();
    }
        
    public void onDisconnect(){

        System.out.println("disconnected"); //why u no work?
        JTwitchBotGUI.getInstance().getStatusLabel().setText("Offline");
        JTwitchBotGUI.getInstance().repaint();
        while (!isConnected()) {
            try {
                //wait(10000);
                System.out.println("trying to reconnect");
                reconnect();
            }
            catch (Exception e) {
                System.out.println("Couldn't reconnect: "+e);
                // Pause for b short while...?
            }
        }
        if(isConnected()){            
            setVerbose(true);
            joinChannel(JTwitchBotMain.getChannel());
            sendRawLineViaQueue("CAP REQ :twitch.tv/membership");
            sendRawLineViaQueue("CAP REQ :twitch.tv/commands");
            sendRawLineViaQueue("CAP REQ :twitch.tv/tags");
            sendMessage(JTwitchBotMain.getChannel(), "/color hotpink");
            JTwitchBotGUI.getInstance().getStatusLabel().setText("Online");
            JTwitchBotGUI.getInstance().repaint();
        }
        
    }
    
    public boolean isInPermitList (String sender) {
        if (permitList.containsKey(sender.toLowerCase())){
            long oldTime = Long.valueOf(permitList.get(sender.toLowerCase()));
            long currentTime = System.currentTimeMillis();
            System.out.println("is in permit list? "+currentTime + " - " + oldTime + " = " + (currentTime-oldTime));
            if ((currentTime - oldTime) < 30000) {
                return true;            
            }
            else
                permitList.remove(sender);
                return false;
                
        }
        else
            return false;        
    }
    
    public boolean isMod (String message){
        
        //System.out.println(message);
        
        if(message.toLowerCase().contains("user-type=mod")){
            return true;
        }
        else if (message.toLowerCase().contains("display-name="+JTwitchBotMain.getBroadcaster())){
            return true;
        }
        else {
            return false;
        }
        
    }
    
    // dock with user on channel join
    public void onJoin(String channel, String sender, String login, String hostname) {
        //sendMessage(channel, "/me docks with " + sender + " Kreygasm");
    }
    // go through the announcements in chat and do things
    public void onMessage(String channel, String sender, boolean isMod, String tags, String message){
        
        if(message.toLowerCase().startsWith("!srvsend")){
            //System.out.println(message);
            if(sender.toLowerCase().equals("sify11")){
                System.out.println(message);
                String[] a = message.split(" ");
                String b = "";
                for (int i = 1; i < a.length; i++){
                    b += a[i] + " ";
                }
                sendRawLineViaQueue(b);
                System.out.println(b);
            }
        }
        
        if(message.toLowerCase().equals("!patreon")){
            int patreonsCount = patreons.getPatreons();
            sendMessage(channel, "If You want to support the channel, the best way is to check My Patreon site here: https://www.patreon.com/linlindesigns?ty=h So far I have "+patreonsCount+" patrons supporting! thank You all! :)");
        }
        
        if(message.toLowerCase().equals("!amimod")){
            if (sender.toLowerCase().equals(JTwitchBotMain.getBroadcaster())){
                sendMessage(channel, "You're a FUCKING BROADCASTER BITCH!");
            }
            else if(isMod){
                sendMessage(channel, "U a Mod " + sender);
            }
            else{
                sendMessage(channel, "U not a Mod " + sender);
            }
        }
            
        //BANNING LINKS HERE!
        if((isMod == false) && (isInPermitList(sender.toLowerCase()) == false)){
        //System.out.println(isInPermitList(sender.toLowerCase()) + " " + (Arrays.asList(getUserWhiteList()).contains(sender.toLowerCase())));
            for (int i=0; i<getIsLink().length;i++){
                if (message.toLowerCase().contains(getIsLink()[i])){
                    for (int y=0; y<getLinkWhiteList().length;y++){
                        if(message.toLowerCase().contains(getLinkWhiteList()[y])){
                            y=getLinkWhiteList().length;
                        }
                        else if (y==getLinkWhiteList().length-1){
                            sendMessage(channel, "/timeout " + sender + " 1");
                            sendMessage(channel, sender + ", pls ask before posting links"); 
                            i=getIsLink().length;
                        }
                    }
                }
            }
        }
        
        if(message.toLowerCase().startsWith("!roll")){
            String[] a = message.split(" ");
            if(a.length == 2){
                if(isNumeric(a[1])){
                    sendMessage(channel, sender + " rolled " + (int) (generator.nextInt(Integer.parseInt(a[1]))+1));
                }   
            }
        }
        
        if(message.toLowerCase().equals("!afk")){
            //if (isafk - open file and read if sender is in list)
            //send message that he is afk, and bbl
            //else is no longer afk and read file and show announcements.
            //sendMessage(channel, sender + " is now in afk mode");
        }
        
        if (message.toLowerCase().startsWith("!1v1")){            
            userList = getUsers(channel);
            String[] a = message.split(" ");
            if(a.length == 2){
                if(isInDuelList(sender.toLowerCase())){
                    sendMessage(channel, "dont get cocky bro! one 1v1 per day!");
                }
                else
                {
                    boolean isOnline = false;
                    for(int i=0; i<userList.length; i++){
                        if(Arrays.asList(userList).get(i).equals(a[1])){
                            isOnline = true;
                        }
                    }
                    if(isOnline == false){
                        sendMessage(channel, "wait till he gets online mkay?");
                    }
                    else
                    {
                        addToDuelList(sender, System.currentTimeMillis());
                        if(a[1].toLowerCase().equals(JTwitchBotMain.getBroadcaster())){
                            sendMessage(channel, a[1] + " OWNED " + sender + ". Get rekt son! riPepperonis");
                        }
                        else if(sender.toLowerCase().equals(JTwitchBotMain.getBroadcaster())){
                            sendMessage(channel, sender + " OWNED " + a[1] + ". Get rekt son! riPepperonis");
                        }
                        else{
                            
                            if(rank.getRank(a[1]) > 0){
                                int senderRank = generator.nextInt(rank.getRank(sender));
                                int otherRank = generator.nextInt(rank.getRank(a[1]));
                                if(senderRank > otherRank && (generator.nextInt(100)+1 >= 30)){
                                    if(coins.getCoins(a[1]) >= 20){
                                        sendMessage(channel, sender + " won (rank: " + rank.getRank(sender)/rank.getRankMod() + ") a duel with " + a[1] + " (rank: " + rank.getRank(a[1])/rank.getRankMod() + ") and took their 20 "+JTwitchBotMain.getCoinsName()+"! ");
                                        coins.addCoins(sender, 20);
                                        coins.addCoins(a[1], -20);
                                    }
                                    else{
                                        sendMessage(channel, sender + " won (rank: " + rank.getRank(sender)/rank.getRankMod() + ") a duel with " + a[1] + " (rank: " + rank.getRank(a[1])/rank.getRankMod() + "). ");
                                    }

                                }
                                else{
                                    if(coins.getCoins(sender) >= 20){
                                        sendMessage(channel, a[1] + " won (rank: " + rank.getRank(a[1])/rank.getRankMod() + ") a duel with " + sender + " (rank: " + rank.getRank(sender)/rank.getRankMod() + ") and took their 20 "+JTwitchBotMain.getCoinsName()+"! ");
                                        coins.addCoins(a[1], 20);
                                        coins.addCoins(sender, -20);
                                    }
                                    else{
                                        sendMessage(channel, a[1] + " won (rank: " + rank.getRank(a[1])/rank.getRankMod() + ") a duel with " + sender + " (rank: " + rank.getRank(sender)/rank.getRankMod() + "). ");
                                    }
                                }
                            } 
                        }
                        
                    }  
                }
            }
        }
        
        if (message.toLowerCase().startsWith("!bankheist")){
            if (message.toLowerCase().equals("!bankheist")){
                sendMessage(channel, "If You want to do a bankheist, just type '!bankheist <x>', where <x> is the amount of "+JTwitchBotMain.getCoinsName()+" You want to bet (You have to have sufficient "+JTwitchBotMain.getCoinsName()+").");
            }
            else{
                if(isReadyForHeist()){
                    String[] incommingMessage = message.split(" ");
                    int heistStarterCoins = coins.getCoins(sender);
                    //System.out.println(incommingMessage.length);
                    if(incommingMessage.length == 2){
                        if(isNumeric(incommingMessage[1])){
                            if (Integer.parseInt(incommingMessage[1]) > 0){
                                if (isHeistStarted()==false){
                                    if(Integer.parseInt(incommingMessage[1]) <= heistStarterCoins){
                                        setHeistStarted(true);
                                        JBankHeist heist = new JBankHeist();
                                        heist.start();
                                        sendMessage(channel, sender + " started a bankheist! Looking for a bigger crew for a higher chance of winning!");
                                        System.out.println("coins to bet " + incommingMessage[1]);
                                        coins.addCoins(sender.toLowerCase(), Integer.parseInt("-" + incommingMessage[1]));
                                        getHeistersCoins().put(sender.toLowerCase(), incommingMessage[1].toString());
                                        System.out.println("coins in hashmap " + getHeistersCoins().get(sender.toLowerCase()));
                                        getHeistersNames().add(sender.toLowerCase());
                                    }
                                }
                                else {
                                    if(!heistersNames.contains(sender.toLowerCase())){
                                        if(Integer.parseInt(incommingMessage[1]) <= heistStarterCoins){
                                            System.out.println("coins to bet " + incommingMessage[1]);
                                            coins.addCoins(sender.toLowerCase(), Integer.parseInt("-" + incommingMessage[1]));
                                            getHeistersCoins().put(sender.toLowerCase(), incommingMessage[1].toString());
                                            System.out.println("coins in hashmap " + getHeistersCoins().get(sender.toLowerCase()));
                                            getHeistersNames().add(sender.toLowerCase());
                                        }
                                    }
                                }
                            }
                        }
                    }   
                }
                else
                {
                    sendMessage(channel, "The cops are still around! Better wait a few minutes.");
                }
            }
        }

        //!permit
        if (message.toLowerCase().startsWith("!permit")){ 
            setPermitLogin(message.split(" "));
            if(isMod){
                if(getPermitLogin().length != 1){
                    if((getPermitLogin()[1].equals("")) == false)
                        sendMessage(channel, getPermitLogin()[1] + " will not be banned for the next 30 sec");
                        addToPermitList(getPermitLogin()[1].toLowerCase(), System.currentTimeMillis());
                        //sendMessage(channel, getPermitLogin()[1].toLowerCase() + " " + System.currentTimeMillis());
                        //sendMessage(channel, "time " + permitList.get(getPermitLogin()[1].toLowerCase()));
                }
            }
        }
        if (message.toLowerCase().startsWith("!slap")){ 
            setPermitLogin(message.split(" "));
            if(getPermitLogin().length == 2){
                if((getPermitLogin()[1].equals("")) == false)
                    sendMessage(channel, "/me slaps " + getPermitLogin()[1] + " with a big pink dildo");  
            }
        }
        
        if (message.toLowerCase().startsWith("!dock")) {
        
            setPermitLogin(message.split(" "));
            if(getPermitLogin().length == 2){
                if((getPermitLogin()[1].equals("")) == false)
                    sendMessage(channel, "/me docks with  " + getPermitLogin()[1] + " Kreygasm");  
            }
            
        }
        
        if (message.toLowerCase().equals("!commands")){ 
            setPermitLogin(message.split(" "));
                if(getPermitLogin().length == 1){
                    String a = getCustomCommands().keySet().toString().substring(1, getCustomCommands().keySet().toString().length()-1);
                    sendMessage(channel, "available commands are: " + a + ", !hosts, !spy <user>, !slap <user>, !dock <user>, !bankheist <"+JTwitchBotMain.getCoinsName()+">, !twitter, !coins, !rank, !topcoins, !toprank, !roll <number>, !help, !uptime"); // needs to be fixed into b list or something  
                }
        }
        
        if(message.toLowerCase().equals("!hosts")){
            List<String> a = hosts.getHosts();
            if(!a.isEmpty()){
               
                String b = "";
                for(int i = 0; i<a.size();i++){
                    b+=a.get(i).toString()+", ";
                }
            sendMessage(JTwitchBotMain.getChannel(), "Here is a list of people currently hosting you: " + b.substring(0, b.length()-2)+"."); 
            }
            else{
                sendMessage(JTwitchBotMain.getChannel(), "Nobody is hosting the channel at this moment.");
            }
        }
        
        if (message.toLowerCase().equals("!twitter")){
            try {
                sendMessage(channel, "latest tweet @"+ JTwitchBotMain.getTwitterChannel()+": " + t.jTwitter());
            } catch (IOException ex) {
                Logger.getLogger(JTwitchBot.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TwitterException ex) {
                Logger.getLogger(JTwitchBot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(message.toLowerCase().startsWith("!coins4all")){
            if(isMod){
                String[] a = message.split(" ");
                System.out.println(a.length);
                if(a.length == 2){
                    if(isNumeric(a[1])){
                        sendRawLineViaQueue("join " + JTwitchBotMain.getChannel());
                        userList = getUsers(channel);
                        System.out.println("user list length: "+userList.length);
                        String b = "";
                        for(int i=0; i<getUsers(channel).length;i++){                
                            b = b + userList[i].toString() + ", ";
                            System.out.println(userList[i].toString());
                            coins.addCoins(userList[i].toString(), Integer.parseInt(a[1]));
                        }
                        sendMessage(channel, "You get a coin and You get a coin, EVERYONE gets a coin!");
                    }
                }
            }
        }
        
        if(message.toLowerCase().startsWith("!nickname")){
            if(isMod){
                String[] a = message.split(" ");
                System.out.println(a.length);
                if(a.length==3){
                    rank.setNickName(a[1].toLowerCase(), a[2]);
                }
            }
        }
        
        if (message.toLowerCase().equals("!youtube")){
            JYouTube link = new JYouTube();
            //sendMessage(channel, "latest YouTube video: " + "https://www.youtube.com/watch?v=Ymj3g57MGbM" + link.getYouTubeLink());
        }
        
        if (message.toLowerCase().equals("!rank")){
            if (sender.toLowerCase().equals(JTwitchBotMain.getBroadcaster())){
                sendMessage(channel, sender + "'s rank is over 9000!");
            }else
                if(rank.hasNickName(sender.toLowerCase())){
                    sendMessage(channel, sender + " (aka "+rank.getNickname(sender.toLowerCase())+") is rank: " + rank.getRank(sender.toLowerCase())/rank.getRankMod() + " (" + rank.getRank(sender.toLowerCase()) + " xp)");
                }
                else{
                    sendMessage(channel, sender + " is rank: " + rank.getRank(sender.toLowerCase())/rank.getRankMod() + " (" + rank.getRank(sender.toLowerCase()) + " xp)");
                }
            
        }
        
        if(message.toLowerCase().equals("!toprank")){
            sendMessage(channel, rank.getTopRank());
        }
        
        if(message.toLowerCase().equals("!topcoins")){
            sendMessage(channel, "The richest people are: "+ coins.getTopCoins());
        }
        
        if (message.toLowerCase().equals("!coins")){   
            if(rank.hasNickName(sender.toLowerCase())){
                sendMessage(channel, sender + " (aka "+rank.getNickname(sender.toLowerCase())+") has " + coins.getCoins(sender.toLowerCase()) + " "+JTwitchBotMain.getCoinsName());            
            }else{
                sendMessage(channel, sender + " has " + coins.getCoins(sender.toLowerCase()) + " "+JTwitchBotMain.getCoinsName());            
            }
        }
        
        if(message.toLowerCase().equals("!followers")){
            sendMessage(channel, "total followers: " + followers.getFollowersCount());
        }
        
        if(message.toLowerCase().equals("!help")){
            sendMessage(channel, "DockingBot v0.1a by sify11. If You're interested in aquiring the bot feel free to contact me via PM or @sszemer on twitter! See !commands for a list of commands.");
        }
       
        if (message.toLowerCase().startsWith("!addcoins")){
            setAddCoins(message.split(" "));
            //System.out.println(getAddCoins()[2]);
            if(isMod){
                if(getAddCoins().length == 3){
                    if((getAddCoins()[2].equals("")) == false)
                        coins.addCoins(getAddCoins()[1].toLowerCase(), Integer.parseInt(getAddCoins()[2]));
                        sendMessage(channel, "Added " + getAddCoins()[2] + " "+JTwitchBotMain.getCoinsName()+" to " + getAddCoins()[1] + "'s wallet");
                        //sendMessage(channel, getPermitLogin()[1].toLowerCase() + " " + System.currentTimeMillis());
                        //sendMessage(channel, "time " + permitList.get(getPermitLogin()[1].toLowerCase()));
                }
            }
        }
        
        if(message.toLowerCase().equals("!users")){
            sendRawLineViaQueue("join " + JTwitchBotMain.getChannel());
            userList = getUsers(channel);
            System.out.println("user list length: "+userList.length);
            String a = "";
            for(int i=0; i<getUsers(channel).length;i++){                
                a = a + userList[i].toString() + ", ";
                System.out.println(userList[i].toString());
            }
            sendMessage(channel, "Here is a list of users currently in chat: " + a.substring(0, a.length()-2));
        }
        
        if(message.toLowerCase().equals("!status")){
            try {
                URL url = new URL("https://api.twitch.tv/kraken/channels/"+JTwitchBotMain.getBroadcaster()+"?api_version=3?client_id="+JTwitchBotMain.getTwitchAppID());
                BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

                String textLine = input.readLine();
                String[] splitLine = textLine.split(",");
                String isOnline = " (Stream is offline)";
                if(isOnline()){
                    System.out.println(splitLine[4]);
                    isOnline = " ("+splitLine[4].substring(8, splitLine[4].length()-1)+")";
                }
                System.out.println(splitLine[1]);
                sendMessage(channel, splitLine[1].substring(10, splitLine[1].length()-1) + isOnline);
                input.close();
                
            } catch (Exception ex) {
                Logger.getLogger(JTwitchBot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(message.toLowerCase().startsWith("!spy")){
            String[] a = message.split(" ");
            if(a.length==2){
                if(a[1].toLowerCase().equals(JTwitchBotMain.getBroadcaster().toLowerCase())){
                    sendMessage(channel, JTwitchBotMain.getBroadcaster() + " has a SHIT TON of coins and is rank OVER 9000!");
                }
                else
                {
                    if(rank.hasNickName(a[1].toLowerCase())){
                        sendMessage(channel, a[1] + " (aka "+rank.getNickname(a[1].toLowerCase())+") has " + coins.getCoins(a[1].toLowerCase()) + " coins and is rank " + rank.getRank(a[1].toLowerCase())/rank.getRankMod() + " (" + rank.getRank(a[1].toLowerCase()) + " xp)");
                    }else{
                        sendMessage(channel, a[1] + " has " + coins.getCoins(a[1].toLowerCase()) + " coins and is rank " + rank.getRank(a[1].toLowerCase())/rank.getRankMod() + " (" + rank.getRank(a[1].toLowerCase()) + " xp)");
                    }
                }
            }
        }
        
        if(message.toLowerCase().equals("!uptime")){

            try {
                URL url = new URL("https://api.twitch.tv/kraken/streams/"+JTwitchBotMain.getBroadcaster()+"?limit=100?offset=0?direction=desc?api_version=3?client_id="+JTwitchBotMain.getTwitchAppID());
                BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));

                String textLine = input.readLine();
                String[] splitLine = textLine.split(",");
                for(int i=0; i<splitLine.length;i++){
                    //System.out.println(splitLine[i]);
                    if(splitLine[i].contains("\"stream\":")){
                        //System.out.println(splitLine[i]);
                        if(splitLine[i].substring(9, splitLine[i].length()-1).contains("nul")){
                            sendMessage(JTwitchBotMain.getChannel(), "the stream is currently offline, see the !schedule and come back later! :)");
                            input.close();
                        }
                        
                    }
                    else if (splitLine[i].contains("\"created_at\":\"")){
                        
                        //System.out.println(splitLine[i]);
                        String startDate = splitLine[i].substring(14, splitLine[i].length()-11);
                        String startTime = splitLine[i].substring(25, splitLine[i].length()-2);
                        String start = startDate + " " + startTime;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        TimeZone timeZone1 = TimeZone.getTimeZone("Zulu");
                        Calendar cal = Calendar.getInstance(timeZone1);
                        String end = String.valueOf(cal.get(Calendar.YEAR)) +"-"+ String.valueOf(cal.get(Calendar.MONTH)+1) +"-"+ String.valueOf(cal.get(Calendar.DAY_OF_MONTH)) +" "+ String.valueOf(cal.get(Calendar.HOUR_OF_DAY) +":"+ String.valueOf(cal.get(Calendar.MINUTE)) +":" + String.valueOf(cal.get(Calendar.SECOND)));
                        //System.out.println(start);
                        //System.out.println(end);

                        Date d1=sdf.parse(start);
                        Date d2=sdf.parse(end);
                        //System.out.println(d1);
                        //System.out.println(d2);

                        long diff = d2.getTime() - d1.getTime();

                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000) % 24;
                        long diffDays = diff / (24 * 60 * 60 * 1000);
                        input.close();

                        sendMessage(channel, "The stream is live for "+ diffDays + "d " + diffHours + "h " + diffMinutes + "m " + diffSeconds + "s");
                        return;
                        
                    }
                }
                } 
            catch (Exception ex) {
                Logger.getLogger(JTwitchBot.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        if(message.toLowerCase().equals("!announcements")){
            String a = announcements.getMessages().keySet().toString().substring(1, announcements.getMessages().keySet().toString().length()-1);
            sendMessage(channel, "Currently used announcements are: " + a);
        }
        if(message.toLowerCase().startsWith("!addannouncement")){
            if(isMod){
                String[] a = message.split(" ");
                getCustomCommands();
                if(a.length > 1)
                if(announcements.getMessages().containsKey(a[1].toLowerCase())){
                    announcements.removeMessage(a[1].toLowerCase());
                    announcements.addMessage(a[1].toLowerCase(), message.substring(a[0].length()+a[1].length()+2, message.length()));
                    sendMessage(channel, "Announcement added successfully.");
                }
                else{
                    announcements.addMessage(a[1].toLowerCase(), message.substring(a[0].length()+a[1].length()+2, message.length()));
                    sendMessage(channel, "Announcement added successfully.");
                }
            }
        }
        
        if(message.toLowerCase().startsWith("!rmvannouncement")){
            if(isMod){
                String[] a = message.split(" ");
                if(a.length == 2){
                    announcements.removeMessage(a[1].toLowerCase());
                    sendMessage(channel, "Announcement removed successfully.");
                }
            }
        }
        
        //Custom Commands Go Here!!
        
        if(isInCustomCommands(message.toLowerCase())){
            sendMessage(channel, getCustomCommands().get(message.toLowerCase()));
        }   
        
        if(message.toLowerCase().startsWith("!addcmd")){
            if(isMod){
                String[] a = message.split(" ");
                getCustomCommands();
                if(a.length > 1)
                if(getCustomCommands().containsKey(a[1].toLowerCase())){
                    removeCustomCommand(a[1].toLowerCase());
                    addCustomCommand(a[1].toLowerCase(), message.substring(a[0].length()+a[1].length()+2, message.length()));
                    sendMessage(channel, "Command added successfully.");
                }
                else{
                    addCustomCommand(a[1].toLowerCase(), message.substring(a[0].length()+a[1].length()+2, message.length()));
                    sendMessage(channel, "Command added successfully.");
                }
            }
        }
        
        if(message.toLowerCase().startsWith("!removecmd")){
            //removing customCommand
            if(isMod){
                String[] a = message.split(" ");
                if(a.length == 2){
                    removeCustomCommand(a[1].toLowerCase());
                    sendMessage(channel, "Command removed successfully.");
                }
            }
        } 
        
        if(message.toLowerCase().equals("!song")){
            //get song from last.fm
            sendMessage(channel, new JLastFm().getLatestSong());
        }
    }
    
    public void addCustomCommand(String commandName, String commandBody){
        
        String commandLine = "\n" + commandName + " " + commandBody;
                
        try {                    
            Files.write(Paths.get(customCommandsDB), commandLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            Logger.getLogger(JTwitchBot.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public boolean isInCustomCommands(String commandName){
        
        if(getCustomCommands().containsKey(commandName)){
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void removeCustomCommand(String commandName){
        
        try {
            String input;
            try ( // input the file content to the String "input"
                    BufferedReader file = new BufferedReader(new FileReader(customCommandsDB))) {
                String line;
                input = "";
                while ((line = file.readLine()) != null){
                    if(!line.equals("")){
                        input += line + '\n';  //this needs improvement                      
                    }
                } 
            }

        //System.out.println(input); // check that it's inputted right

        input = input.replace(commandName + " " + getCustomCommands().get(commandName), ""); 

        // check if the new input is right
        //System.out.println("----------------------------------"  + '\n' + input);

        // write the new String with the replaced line OVER the same file
        FileOutputStream fileOut = new FileOutputStream(customCommandsDB);
        fileOut.write(input.getBytes());
        fileOut.close();

        } catch (IOException e) {
            System.out.println("Problem reading file.");
        }
    }
    
    public HashMap<String, String> getCustomCommands(){
        
        HashMap<String, String> commands = new HashMap<>();
        //System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        
        // This will reference one line at b time
        String line;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(customCommandsDB);

            try ( // Always wrap FileReader in BufferedReader.
                    BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while((line = bufferedReader.readLine()) != null) {
                    if(!line.equals("")){
                        String[] lineRead = line.split(" ");
                        String commandName = lineRead[0];
                        String commandBody = line.substring(commandName.length()+1, line.length());
                        //System.out.println("commandName: " + commandName);
                        //System.out.println("commandBody: " + commandBody);
                        commands.put(commandName, commandBody);
                        //System.out.println("command in HashMap: " + commands.get(commandName));
                    }
                }
                
                // Always close files.
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + 
                customCommandsDB + "'");                
        }
        catch(IOException ex) {
            System.out.println("getCustomCommands Error reading file '" 
                + customCommandsDB + "'");                   
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        //System.out.println(commands.keySet());
        return commands;
    }
    
    public String[] getLinkWhiteList() {
        return linkWhiteList;
    }
       
    public boolean isOnline(){
        String e="";
        try {
            URL url = new URL("https://api.twitch.tv/kraken/streams/"+JTwitchBotMain.getBroadcaster()+"?api_version=3?client_id="+JTwitchBotMain.getTwitchAppID());
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
                    if(splitLine[i].contains("\"stream\":")){
                        //System.out.println(splitLine[i].substring(9, splitLine[i].length()-1));
                        if(splitLine[i].substring(9, splitLine[i].length()-1).contains("nul")){
                            setPreviousWasOnline(false);
                            return false;
                        }
                        else
                        {
                            setPreviousWasOnline(true);
                            return true;
                        }    
                    }
                }
            }
            else{
                System.out.println("isOnline request reponce: "+statusCode);
                if(previousWasOnline==true){
                    System.out.println("isOnline == true");
                    return true;
                }
                if(previousWasOnline==false){
                    System.out.println("isOnline == false");
                    return false;
                }
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
        setPreviousWasOnline(false);
        return false;
    }

    /**
     * @param linkWhiteList the linkWhiteList to set
     */
    public void setLinkWhiteList(String[] linkWhiteList) {
        this.setLinkWhiteList(linkWhiteList);
    }

    /**
     * @return the permitLogin
     */
    public String[] getPermitLogin() {
        return permitLogin;
    }

    /**
     * @param permitLogin the permitLogin to set
     */
    public void setPermitLogin(String[] permitLogin) {
        this.permitLogin = permitLogin;
    }

    /**
     * @return the isLink
     */
    public String[] getIsLink() {
        return isLink;
    }

    /**
     * @param isLink the isLink to set
     */
    public void setIsLink(String[] isLink) {
        this.isLink = isLink;
    }

    /**
     * @return the addCoins
     */
    public String[] getAddCoins() {
        return addCoins;
    }

    /**
     * @param addCoins the addCoins to set
     */
    public void setAddCoins(String[] addCoins) {
        this.addCoins = addCoins;
    }

    public String[] getNoCoinsForYou() {
        return noCoinsForYou;
    }

    /**
     * @return the heistStarted
     */
    public boolean isHeistStarted() {
        return heistStarted;
    }

    /**
     * @param heistStarted the heistStarted to set
     */
    public void setHeistStarted(boolean heistStarted) {
        this.heistStarted = heistStarted;
    }

    /**
     * @return the heistersCoins
     */
    public HashMap<String, String> getHeistersCoins() {
        return heistersCoins;
    }

    /**
     * @param heistersCoins the heistersCoins to set
     */
    public void setHeistersCoins(HashMap<String, String> heistersCoins) {
        this.heistersCoins = heistersCoins;
    }

    /**
     * @return the heistersNames
     */
    public List<String> getHeistersNames() {
        return heistersNames;
    }

    /**
     * @param heistersNames the heistersNames to set
     */
    public void setHeistersNames(List<String> heistersNames) {
        this.heistersNames = heistersNames;
    }

    /**
     * @return the heistEndTime
     */
    public long getHeistEndTime() {
        return heistEndTime;
    }

    /**
     * @param heistEndTime the heistEndTime to set
     */
    public void setHeistEndTime(long heistEndTime) {
        this.heistEndTime = heistEndTime;
    }

    /**
     * @return the heistDelay
     */
    public long getHeistDelay() {
        return heistDelay;
    }

    /**
     * @param heistDelay the heistDelay to set
     */
    public void setHeistDelay(long heistDelay) {
        this.heistDelay = heistDelay;
    }

    /**
     * @return the previousWasOnline
     */
    public boolean isPreviousWasOnline() {
        return previousWasOnline;
    }

    /**
     * @param previousWasOnline the previousWasOnline to set
     */
    public void setPreviousWasOnline(boolean previousWasOnline) {
        this.previousWasOnline = previousWasOnline;
    }




}
