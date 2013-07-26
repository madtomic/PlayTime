/*
 * Copyright (C) 2013 Spencer Alderman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogue.playtime.runnable.sqlite;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.sqlite.SQLite;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.1
 */
public class SQLiteAddRunnable extends BukkitRunnable {

    Playtime plugin;

    public SQLiteAddRunnable(Playtime p) {
        plugin = p;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        SQLite db = new SQLite();
        StringBuilder sb2 = new StringBuilder("null  ");
        try {
            db.open();
            if (players.length > 0) {
                StringBuilder sb = new StringBuilder("INSERT OR IGNORE INTO `playTime` (`username`, `playtime`, `deathtime`) VALUES ");
                sb2 = new StringBuilder("UPDATE `playTime` ");
                for (Player p : players) {
                    
                    if (!(!plugin.isAFKEnabled() && !plugin.isDeathEnabled()) && !plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                        if (plugin.isDeathEnabled()) {
                            sb2.append("SET `playtime`=`playtime`+1, `deathtime`=`deathtime`+1 WHERE `username`='").append(p.getName()).append("', ");
                        } else {
                            sb2.append("SET `playtime`=`playtime`+1 WHERE `username`='").append(p.getName()).append("', ");
                        }
                    } else {
                        sb2.append("SET `playtime`=`playtime`+1 WHERE `username`='").append(p.getName()).append("', ");
                    }
                    sb.append("('").append(p.getName()).append("', 0, 0), ");
                }
                if (sb.toString().endsWith(" `playTime` ")) {
                    if (plugin.getDebug() >= 1) {
                        plugin.getLogger().info("No players to update.");
                    }
                    db.close();
                    return;
                }
                db.update(sb.substring(0, sb.length() - 2));
                db.update(sb2.substring(0, sb2.length() - 2));
     
                if (plugin.getDebug() >= 1) {
                    plugin.getLogger().info("Players updated!");
                    if (plugin.getDebug() >= 2) {
                        plugin.getLogger().log(Level.INFO, "SQL Query 1 for update: \n {0}", sb.substring(0, sb.length() - 2));
                        plugin.getLogger().log(Level.INFO, "SQL Query 2 for update: \n {0}", sb2.substring(0, sb2.length() - 2));
                    }
                }
            }
            db.close();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            if (plugin.getDebug() == 3) {
                ex.printStackTrace();
            }
            plugin.getLogger().log(Level.INFO, "SQL Query 2 for update: \n {0}", sb2.substring(0, sb2.length() - 2));
        }
    }
}