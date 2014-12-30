package de.oppermann.bastian.spleef.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.google.common.util.concurrent.FutureCallback;

import de.oppermann.bastian.spleef.arena.SpleefArena;

public class ScoreboardConfiguration {

	private final ArrayList<String> LINES = new ArrayList<>();
	
	public ScoreboardConfiguration() { 
		
	}
	
	public void addLine(String line) {
		LINES.add(line);
	}
	
	public String getLine(int position) {
		return LINES.get(position);
	}
	
	public void setScores(Player player, SpleefArena arena) {
		final SpleefArena ARENA = arena;
		final Player PLAYER = player;
		SpleefPlayerStats.getPlayerStats(player.getUniqueId(), new FutureCallback<SpleefPlayerStats>() {

			@Override
			public void onFailure(Throwable e) {
				e.printStackTrace();
			}

			@Override
			public void onSuccess(SpleefPlayerStats stats) {
				if (PlayerManager.getArena(PLAYER.getUniqueId()) == null) {	// check if the player is still ingame
					return;
				}
				
				Scoreboard board;
				Objective objective;
				
				board = Bukkit.getScoreboardManager().getNewScoreboard();
				objective = board.registerNewObjective(ChatColor.GOLD + "Spleef", "dummy");
				objective.setDisplaySlot(DisplaySlot.SIDEBAR);
				objective.setDisplayName(ChatColor.GOLD + "Spleef");
				
				for (String score : board.getEntries()) {
					try {
						Integer.parseInt(score);
						board.resetScores(score);
					} catch (NumberFormatException e) {
						// everything is fine :)
					}
				}
				
				for (int i = 0; i < LINES.size(); i++) {
					String line = LINES.get(i);
					line = line.replace("%wins%", stats.getWins(ARENA.getName()) + "&0");
					line = line.replace("%totalWins%", stats.getTotalWins() + "&1");
					line = line.replace("%losses%", stats.getLosses(ARENA.getName()) + "&2");
					line = line.replace("%totalLosses%", stats.getTotalLosses() + "&3");
					line = line.replace("%points%", stats.getPoints(ARENA.getName()) + "&4");
					line = line.replace("%totalPoints%", stats.getTotalPoints() + "&5");
					line = line.replace("%totalEarnedPoints%", stats.getTotalEarnedPoints() + "&6");
					line = ChatColor.translateAlternateColorCodes('&', line);
					Score score = objective.getScore(line);
					score.setScore(1);
					score.setScore(LINES.size() - i);
				}
				PLAYER.setScoreboard(board);
			}

		});
	}
	
}
