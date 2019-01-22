/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jtwitchbot;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sebastianszemer
 */
public class JBankHeist extends Thread {
    
    private int heistDuration = 120000;
    private double heistMultiplier = 1.5;
    private JCoins coins = new JCoins();
    private Random generator = new Random();
    
    public void JBankHeist(){
        
    }
    
    public void stopMe(){
        this.interrupt();
    }
    
    public int random () {
        return generator.nextInt(10)+1;
    }
    
    public void run(){
        
        try {
            while(true){
            Thread.sleep(heistDuration);
            String heistWinners = "";
            int randomValue;
            int y = 0;
            for (int i = 0; i< JTwitchBot.getInstance().getHeistersNames().size(); i++){
                randomValue = generator.nextInt(9+JTwitchBot.getInstance().getHeistersNames().size())+1;
                System.out.println(randomValue);
                if(randomValue > 3){
                    coins.addCoins(JTwitchBot.getInstance().getHeistersNames().get(i), (int) (Double.valueOf(JTwitchBot.getInstance().getHeistersCoins().get(JTwitchBot.getInstance().getHeistersNames().get(i)))*heistMultiplier));
                    heistWinners += JTwitchBot.getInstance().getHeistersNames().get(i) + " (stole " + (int) (Double.valueOf(JTwitchBot.getInstance().getHeistersCoins().get(JTwitchBot.getInstance().getHeistersNames().get(i)))*heistMultiplier) + " coins), ";
                    y++;
                }
            }
            
            System.out.println(heistWinners.equals(""));
            if (heistWinners.equals("")){
                JTwitchBot.getInstance().sendMessage(JTwitchBotMain.getChannel(), "Everyone got Rekt by the cops!");
            }
            else if(y == JTwitchBot.getInstance().getHeistersNames().size()){
                JTwitchBot.getInstance().sendMessage(JTwitchBotMain.getChannel(), "The get-away has ended flawlessly and the payouts are:  " + heistWinners.substring(0, heistWinners.length()-2)+".");
            }
            else{
                JTwitchBot.getInstance().sendMessage(JTwitchBotMain.getChannel(), "The get-away has ended and the ones who survived are:  " + heistWinners.substring(0, heistWinners.length()-2) + ". Everyone else got Rekt by the cops!");
            }            
            JTwitchBot.getInstance().setHeistStarted(false);
            JTwitchBot.getInstance().getHeistersCoins().clear();
            JTwitchBot.getInstance().getHeistersNames().clear();
            JTwitchBot.getInstance().setHeistEndTime(System.currentTimeMillis());
            return;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(JBankHeist.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(JBankHeist.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
