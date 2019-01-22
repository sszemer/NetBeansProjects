package jtwitchbot;

import java.io.IOException;
import twitter4j.*;
import java.util.List;
import twitter4j.auth.AccessToken;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.7
 */
public class JTwitter {

    private String consumerKey = "EbHlwEzODoFwc0XKBlp5shYNR";
    //Your Twitter App's Consumer Secret
    private String consumerSecret = "vBDS6NRF0cpKwIOtx0uGXmg5r7DwPmCz0qyjyUiKklLV9EX1di";
    //Your Twitter Access Token
    private String accessToken = "123513206-yYKM5Z6IZmrXfO4Fraz3Kco4jl87YzIHYzib4Q7U";
    //Your Twitter Access Token Secret
    private String accessTokenSecret = "rFs9fXKFWZKBUP07sKQYg60tgQC5JheW6IUpWdTyS3Rap";
    
    public String jTwitter() throws IOException, TwitterException {
        //Instantiate a re-usable and thread-safe factory
        Twitter twitter = new TwitterFactory().getInstance();
        //setup OAuth Consumer Credentials
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        //setup OAuth Access Token
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        User user = twitter.verifyCredentials();
        List<Status> statuses = twitter.getUserTimeline(JTwitchBotMain.getTwitterChannel());
        
        return statuses.get(0).getText();
    }
}