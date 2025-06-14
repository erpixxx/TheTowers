package dev.erpix.thetowers;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Bootstrap class for the TheTowers plugin.
 */
public class TheTowersBootstrap extends JavaPlugin {

    private TheTowers theTowers;

    @Override
    public void onDisable() {
        theTowers.disable();
    }

    @Override
    public void onEnable() {
        theTowers = new TheTowers(this);
        theTowers.enable();
    }

}
