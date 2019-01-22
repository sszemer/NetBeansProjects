/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;
import javax.swing.JOptionPane;
import org.jibble.pircbot.*;

/** 
 *
 * @author sebastianszemer
 */

public class JTwitchBotMain{
    
    private static String server = "irc.twitch.tv";
    private static int port = 6667;
    private static String botUserName = "dockingbot";
    private static String password = "oauth:16lq9yzp1u6hq6mwvu9eghj3ve0zhf";
    private static String broadcaster = "sify11";
    private static String channel = "#"+broadcaster;
    private static String twitterChannel = "sszemer";
    private static String TwitchAppID = "cgh1wq8lukv8cw5p214z11mvqsuggrd";
    private static String coinsName = "coins";
    private static String lastFmUserName = "sify11";
    private static boolean enableLastFm = true;
    private static boolean enableTwitter = true;
    private static boolean banLinks = true;
    private static boolean banCaps = true;
    private static boolean banASCII = true;
    private static boolean enableCoins = true;
    private static boolean coinsWhenOffline = true;
    private static boolean enableRanks= true;
    private static long coinsOnlineTimer=600000;
    private static long coinsOfflineTimer=600000;
    private static int amountOfCoinsWhenOnline=10;
    private static int amountOfCoinsWhenOffline=2;
    private static boolean announceCoins=true;
    private static boolean announceRanks=true;
        
    public static void main (String[] args) throws Exception {
        JTwitchBotGUI form = new JTwitchBotGUI();
        form.loadSettings();
        form.setVisible(true);
        //startBot();
        
    }
    
    public static void startBot() throws Exception{
        JTwitchBot bot = JTwitchBot.getInstance();
        bot.connect(getServer(), getPort(), getPassword());
        bot.setVerbose(true);
        bot.joinChannel(getChannel());
        bot.sendRawLineViaQueue("CAP REQ :twitch.tv/membership");
        bot.sendRawLineViaQueue("CAP REQ :twitch.tv/commands");
        bot.sendRawLineViaQueue("CAP REQ :twitch.tv/tags");
        bot.sendMessage(getChannel(), "/color hotpink");
        bot.sendRawLineViaQueue("join " + getChannel());
        System.out.println("twitter: " + getTwitterChannel());
        System.out.println("broadcaster: " + getBroadcaster());
        System.out.println("irc channel: " + getChannel());        
    }
    
    public static void stopBot() throws Exception{
        
        try {
            JTwitchBot bot = JTwitchBot.getInstance();
            bot.quitBot();
            //bot.disconnect();
            bot.dispose();  
            bot=null;
        } catch (Exception e) {
            
        }
  
        
    }

    /**
     * @return the server
     */
    public static String getServer() {
        return server;
    }

    /**
     * @param aServer the server to set
     */
    public static void setServer(String aServer) {
        server = aServer;
    }

    /**
     * @return the port
     */
    public static int getPort() {
        return port;
    }

    /**
     * @param aPort the port to set
     */
    public static void setPort(int aPort) {
        port = aPort;
    }

    /**
     * @return the username
     */
    public static String getBotUserName() {
        return botUserName;
    }

    /**
     * @param aUsername the username to set
     */
    public static void setBotUserName(String aUsername) {
        botUserName = aUsername;
    }

    /**
     * @return the password
     */
    public static String getPassword() {
        return password;
    }

    /**
     * @param aPassword the password to set
     */
    public static void setPassword(String aPassword) {
        password = aPassword;
    }

    /**
     * @return the channel
     */
    public static String getChannel() {
        return channel;
    }

    /**
     * @param aChannel the channel to set
     */
    public static void setChannel(String aChannel) {
        channel = aChannel;
    }

    /**
     * @return the broadcaster
     */
    public static String getBroadcaster() {
        return broadcaster;
    }

    /**
     * @param aBroadcaster the broadcaster to set
     */
    public static void setBroadcaster(String aBroadcaster) {
        broadcaster = aBroadcaster;
    }

    /**
     * @return the twitterChannel
     */
    public static String getTwitterChannel() {
        return twitterChannel;
    }

    /**
     * @param aTwitterChannel the twitterChannel to set
     */
    public static void setTwitterChannel(String aTwitterChannel) {
        twitterChannel = aTwitterChannel;
    }

    /**
     * @return the TwitchAppID
     */
    public static String getTwitchAppID() {
        return TwitchAppID;
    }

    /**
     * @param aTwitchAppID the TwitchAppID to set
     */
    public static void setTwitchAppID(String aTwitchAppID) {
        TwitchAppID = aTwitchAppID;
    }

    /**
     * @return the coinsName
     */
    public static String getCoinsName() {
        return coinsName;
    }

    /**
     * @param aCoinsName the coinsName to set
     */
    public static void setCoinsName(String aCoinsName) {
        coinsName = aCoinsName;
    }

    /**
     * @return the lastFmUserName
     */
    public static String getLastFmUserName() {
        return lastFmUserName;
    }

    /**
     * @param aLastFmUserName the lastFmUserName to set
     */
    public static void setLastFmUserName(String aLastFmUserName) {
        lastFmUserName = aLastFmUserName;
    }

    /**
     * @return the enableLastFm
     */
    public static boolean getEnableLastFm() {
        return enableLastFm;
    }

    /**
     * @param aEnableLastFm the enableLastFm to set
     */
    public static void setEnableLastFm(boolean aEnableLastFm) {
        enableLastFm = aEnableLastFm;
    }

    /**
     * @return the enableTwitter
     */
    public static boolean getEnableTwitter() {
        return enableTwitter;
    }

    /**
     * @param aEnableTwitter the enableTwitter to set
     */
    public static void setEnableTwitter(boolean aEnableTwitter) {
        enableTwitter = aEnableTwitter;
    }

    /**
     * @return the banLinks
     */
    public static boolean getBanLinks() {
        return banLinks;
    }

    /**
     * @param aBanLinks the banLinks to set
     */
    public static void setBanLinks(boolean aBanLinks) {
        banLinks = aBanLinks;
    }

    /**
     * @return the banCaps
     */
    public static boolean getBanCaps() {
        return banCaps;
    }

    /**
     * @param aBanCaps the banCaps to set
     */
    public static void setBanCaps(boolean aBanCaps) {
        banCaps = aBanCaps;
    }

    /**
     * @return the banASCII
     */
    public static boolean getBanASCII() {
        return banASCII;
    }

    /**
     * @param aBanASCII the banASCII to set
     */
    public static void setBanASCII(boolean aBanASCII) {
        banASCII = aBanASCII;
    }

    /**
     * @return the enableCoins
     */
    public static boolean isEnableCoins() {
        return enableCoins;
    }

    /**
     * @param aEnableCoins the enableCoins to set
     */
    public static void setEnableCoins(boolean aEnableCoins) {
        enableCoins = aEnableCoins;
    }

    /**
     * @return the coinsWhenOffline
     */
    public static boolean isCoinsWhenOffline() {
        return coinsWhenOffline;
    }

    /**
     * @param aCoinsWhenOffline the coinsWhenOffline to set
     */
    public static void setCoinsWhenOffline(boolean aCoinsWhenOffline) {
        coinsWhenOffline = aCoinsWhenOffline;
    }

    /**
     * @return the enableRanks
     */
    public static boolean isEnableRanks() {
        return enableRanks;
    }

    /**
     * @param aEnableRanks the enableRanks to set
     */
    public static void setEnableRanks(boolean aEnableRanks) {
        enableRanks = aEnableRanks;
    }

    /**
     * @return the coinsOnlineTimer
     */
    public static long getCoinsOnlineTimer() {
        return coinsOnlineTimer;
    }

    /**
     * @param aCoinsOnlineTimer the coinsOnlineTimer to set
     */
    public static void setCoinsOnlineTimer(long aCoinsOnlineTimer) {
        coinsOnlineTimer = aCoinsOnlineTimer;
    }

    /**
     * @return the coinsOfflineTimer
     */
    public static long getCoinsOfflineTimer() {
        return coinsOfflineTimer;
    }

    /**
     * @param aCoinsOfflineTimer the coinsOfflineTimer to set
     */
    public static void setCoinsOfflineTimer(long aCoinsOfflineTimer) {
        coinsOfflineTimer = aCoinsOfflineTimer;
    }

    /**
     * @return the amountOfCoinsWhenOnline
     */
    public static int getAmountOfCoinsWhenOnline() {
        return amountOfCoinsWhenOnline;
    }

    /**
     * @param aAmountOfCoinsWhenOnline the amountOfCoinsWhenOnline to set
     */
    public static void setAmountOfCoinsWhenOnline(int aAmountOfCoinsWhenOnline) {
        amountOfCoinsWhenOnline = aAmountOfCoinsWhenOnline;
    }

    /**
     * @return the amountOfCoinsWhenOffline
     */
    public static int getAmountOfCoinsWhenOffline() {
        return amountOfCoinsWhenOffline;
    }

    /**
     * @param aAmountOfCoinsWhenOffline the amountOfCoinsWhenOffline to set
     */
    public static void setAmountOfCoinsWhenOffline(int aAmountOfCoinsWhenOffline) {
        amountOfCoinsWhenOffline = aAmountOfCoinsWhenOffline;
    }

    /**
     * @return the announceCoins
     */
    public static boolean isAnnounceCoins() {
        return announceCoins;
    }

    /**
     * @param aAnnounceCoins the announceCoins to set
     */
    public static void setAnnounceCoins(boolean aAnnounceCoins) {
        announceCoins = aAnnounceCoins;
    }

    /**
     * @return the announceRanks
     */
    public static boolean isAnnounceRanks() {
        return announceRanks;
    }

    /**
     * @param aAnnounceRanks the announceRanks to set
     */
    public static void setAnnounceRanks(boolean aAnnounceRanks) {
        announceRanks = aAnnounceRanks;
    }
    
}
