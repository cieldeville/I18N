package com.blackypaw.mc.i18n;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Internal helper. Used to uncache player locales after players have left the server.
 *
 * @author BlackyPaw
 * @version 1.0
 */
class PlayerQuitListener implements Listener {

	private I18NUtilities utilities;

	/**
	 * Constructs a new player quit listener.
	 *
	 * @param utilities The I18N plugin instance
	 */
	public PlayerQuitListener( I18NUtilities utilities ) {
		this.utilities = utilities;
	}

	/**
	 * Handles player kick events as late as possible so that all other plugins get a chance of
	 * translating messages before the player actually gets kicked.
	 *
	 * @param event The player kick event
	 */
	@EventHandler( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onPlayerKick( PlayerKickEvent event ) {
		this.onPlayerLeft( event.getPlayer() );
	}

	/**
	 * Handles player quit events as late as possible so that all other plugins get a chance of
	 * translating messages before the player actually quits the server.
	 *
	 * @param event The player quit event
	 */
	@EventHandler( priority = EventPriority.MONITOR, ignoreCancelled = true )
	public void onPlayerQuit( PlayerQuitEvent event ) {
		this.onPlayerLeft( event.getPlayer() );
	}

	private void onPlayerLeft( Player player ) {
		this.utilities.unstoreLocale( player );
	}

}
