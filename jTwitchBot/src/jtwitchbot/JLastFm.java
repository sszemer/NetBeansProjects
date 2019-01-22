/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import de.umass.lastfm.Caller;
import de.umass.lastfm.PaginatedResult;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import java.util.Collection;

/**
 *
 * @author sebastianszemer
 */
public class JLastFm {
    
    private String key;
    private String user;
    
    
    public static void main(String args[]){
        new JLastFm().getLatestSong();
    }
    
    
    public JLastFm(){
        setKey("b25b959554ed76058ac220b7b2e0a026");
        setUser(JTwitchBotMain.getLastFmUserName());
        Caller.getInstance().setUserAgent(null);
    }
    
    public String getLatestSong(){
        String result = "";
        PaginatedResult<Track> currentTrack = User.getRecentTracks(getUser(), 1, getKey(), 1);
        Collection<Track> tracks = currentTrack.getPageResults();

        for (Track track : tracks) {
            
            String artist ="";// = track.getArtist();
            String song ="";//= track.getName();
            String album ="";
            System.out.println("artist "+track.getArtist() + ". song "+ track.getName() + ". album " + track.getAlbum());
            if(!track.getArtist().equals("null")){
                artist = track.getArtist();
            }
            if(!track.getName().equals("null")){
                song = " - " + track.getName();
            }
            if(!track.getAlbum().isEmpty()) {
                album = " ("+track.getAlbum()+")";
            }
            
            if(tracks.size()==1){
                //System.out.println("Last played song was: " + artist + song + album);
                result = "Last played song was: " + artist + song + album;
                return result;
            }
            else{
                //System.out.println("Current song is: " + artist + song + album);
                result = "Current song is: " + artist + song + album;
                return result;
            }
        }
        return result;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }
    
}
