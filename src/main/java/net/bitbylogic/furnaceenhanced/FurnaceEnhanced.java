package net.bitbylogic.furnaceenhanced;

import com.jeff_media.updatechecker.UpdateCheckSource;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import net.bitbylogic.furnaceenhanced.events.FurnaceListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class FurnaceEnhanced extends JavaPlugin {

    public static final int METRICS_ID = 28214;

    private Metrics metrics;

    @Override
    public void onEnable() {
        this.metrics = new Metrics(this, METRICS_ID);

        getServer().getPluginManager().registerEvents(new FurnaceListener(this), this);

        if (!getConfig().getBoolean("Settings.Update-Checker.Enabled")) {
            return;
        }

        int updateInterval = getConfig().getInt("Settings.Update-Checker.Check-Interval", 12);

        new UpdateChecker(this, UpdateCheckSource.SPIGET, "103489")
                .setDownloadLink("https://www.spigotmc.org/resources/furnaceenhanced-1-20-to-1-21-4-1-17-to-1-19-4-use-1-0-2.103489/")
                .setDonationLink("https://buymeacoffee.com/bitbylogic")
                .setNotifyOpsOnJoin(true)
                .setUserAgent(new UserAgentBuilder().addPluginNameAndVersion()).checkEveryXHours(updateInterval)
                .checkNow();

        getLogger().info("Update checker is enabled and will check every " + updateInterval + " hours");
    }

    @Override
    public void onDisable() {
        this.metrics.shutdown();
    }

}