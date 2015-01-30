package de.oppermann.bastian.spleef.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.UnknownDependencyException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.io.Files;

import de.oppermann.bastian.spleef.SpleefMain;

/**
 * Modified version of https://github.com/gravitylow/ServerModsAPI-Example/blob/master/Update.java
 */
public class UpdateChecker {

    // The project's unique ID
    private static final int PROJECT_ID = 88202;

    // no key in use at the moment
    //private final String apiKey = null;

    // Keys for extracting file information from JSON response
    private static final String API_NAME_VALUE = "name";
    private static final String API_LINK_VALUE = "downloadUrl";
    private static final String API_RELEASE_TYPE_VALUE = "releaseType";
    private static final String API_FILE_NAME_VALUE = "fileName";
    private static final String API_GAME_VERSION_VALUE = "gameVersion";

    // Static information for querying the API
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";
    
    /**
     * Gets the newest version.
     * 
     * @param unsafeUpdates Whether it should search for unsafe Updates, too, or not.
     * @return The newest version. Could be null if there is no newer version.
     */
    public static EpicSpleefVersion getNewestVersion(boolean unsafeUpdates) {
    	if (Bukkit.isPrimaryThread()) {
    		throw new IllegalStateException("Must not be invoked from the main thread.");
    	}
    	EpicSpleefVersion[] versions = UpdateChecker.queryVersions();
		
		ArrayList<EpicSpleefVersion> matches = new ArrayList<>();
		
		String serverVersion = Bukkit.getVersion();
		serverVersion = serverVersion.substring(serverVersion.indexOf("(MC: ") + 5, serverVersion.length());
		serverVersion = serverVersion.substring(0, serverVersion.lastIndexOf(")"));
		String[] serverVersionArray = serverVersion.split("\\.");
		
		for (EpicSpleefVersion version : versions) {
			String pluginGameVersion = version.getVersionGameVersion();
			String[] pluginGameVersionArray = pluginGameVersion.split("\\.");

			// just check the first and the second, cause the 3rd is unimportant most of the time (no huge api changes)
			int serverFist = Integer.valueOf(serverVersionArray[0]);
			int serverSecond = Integer.valueOf(serverVersionArray[1]);
			int pluginFist = Integer.valueOf(pluginGameVersionArray[0]);
			int pluginSecond = Integer.valueOf(pluginGameVersionArray[1]);					
			
			if (serverFist > pluginFist) {
				continue;
			}
			
			if (serverFist == pluginFist && serverSecond > pluginSecond) {
				continue;
			}
			
			if (!unsafeUpdates && (serverFist != pluginFist || serverSecond != pluginSecond)) {
				continue;	// no safe match
			}
			
			matches.add(version);
		}
		
		EpicSpleefVersion newest = null;
		versionLoop: for (EpicSpleefVersion version : matches) {
			String[] pluginVersion = version.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "").split("\\.");
			String[] currentVersion = SpleefMain.getInstance().getDescription().getVersion().split("\\.");
			String[] newestVersion = newest == null ? new String[]{"0", "0", "0"} : newest.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "").split("\\.");;
			
			int[] pluginVersionInt = new int[]{ Integer.valueOf(pluginVersion[0]), Integer.valueOf(pluginVersion[1]), Integer.valueOf(pluginVersion[2])};
			int[] currentVersionInt = new int[]{ Integer.valueOf(currentVersion[0]), Integer.valueOf(currentVersion[1]), Integer.valueOf(currentVersion[2])};
			int[] newestVersionInt = new int[]{ Integer.valueOf(newestVersion[0]), Integer.valueOf(newestVersion[1]), Integer.valueOf(newestVersion[2])};
			
			for (int i = 0; i < 3; i++) {
				if (currentVersionInt[i] > pluginVersionInt[i] || newestVersionInt[i] > pluginVersionInt[i]) {
					continue versionLoop;
				}
			}					
			if (currentVersionInt[0] == pluginVersionInt[0] && currentVersionInt[1] == pluginVersionInt[1] && currentVersionInt[2] == pluginVersionInt[2]) {
				continue versionLoop;
			}
			
			newest = version;
		}
		return newest;
    }
    
    /**
     * Gets all versions newer than the current one.
     */
    public static EpicSpleefVersion[] getUpdates() {
    	if (Bukkit.isPrimaryThread()) {
    		throw new IllegalStateException("Must not be invoked from the main thread.");
    	}    

    	ArrayList<EpicSpleefVersion> newerVersions = new ArrayList<>();
    	EpicSpleefVersion[] versions = UpdateChecker.queryVersions();
    	
    	versionLoop: for (EpicSpleefVersion version : versions) {
			String[] pluginVersion = version.getVersionName().replace("EpicSpleef-", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace(".jar", "").split("\\.");
			String[] currentVersion = SpleefMain.getInstance().getDescription().getVersion().split("\\.");
			
			int[] pluginVersionInt = new int[]{ Integer.valueOf(pluginVersion[0]), Integer.valueOf(pluginVersion[1]), Integer.valueOf(pluginVersion[2])};
			int[] currentVersionInt = new int[]{ Integer.valueOf(currentVersion[0]), Integer.valueOf(currentVersion[1]), Integer.valueOf(currentVersion[2])};
			
			for (int i = 0; i < 3; i++) {
				if (currentVersionInt[i] > pluginVersionInt[i]) {
					continue versionLoop;
				}
			}					
			if (currentVersionInt[0] == pluginVersionInt[0] && currentVersionInt[1] == pluginVersionInt[1] && currentVersionInt[2] == pluginVersionInt[2]) {
				continue versionLoop;
			}			
			
			newerVersions.add(version);
		}
    	return newerVersions.toArray(new EpicSpleefVersion[newerVersions.size()]);
    }
	
    /**
     * Downloads the newest file.
     * 
     * @param version The version to download.
     * @param install Whether the downloaded file should be installed or not.
     * @param messageReceiver The receiver of the messages. Can be null.
     */
	public static void downloadFile(EpicSpleefVersion version, boolean install, CommandSender messageReceiver) {
		if (Bukkit.isPrimaryThread()) {
    		throw new IllegalStateException("Must not be invoked from the main thread.");
    	}
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			URL fileUrl = new URL(version.getVersionLink());
			final int fileLength = fileUrl.openConnection().getContentLength();
			in = new BufferedInputStream(fileUrl.openStream());
			final File file = new File(SpleefMain.getInstance().getDataFolder().getPath() + File.separator + "updates", version.getVersionFileName());
			file.getParentFile().mkdirs();
			file.createNewFile();
			fout = new FileOutputStream(file);
			
			
			final byte[] data = new byte[1024];
			int count;
			long downloaded = 0;
			int nextLogPercentage = 0;
			while ((count = in.read(data, 0, 1024)) != -1) {
				downloaded += count;
				fout.write(data, 0, count);
				final int percent = (int) ((downloaded * 100) / fileLength);
				if (percent == nextLogPercentage) {
					String message = Language.UPDATE_PROGRESS.toString().replace("%percentage%", String.valueOf(percent)).replace("%downloaded%", String.valueOf((int) (downloaded / 1024))).replace("%total%", String.valueOf((int) (fileLength / 1024)));
					if (messageReceiver != null) {
						messageReceiver.sendMessage(message);
					}						
					SpleefMain.getInstance().log(Level.INFO, ChatColor.stripColor(message));
					nextLogPercentage += 10;
				}
			}
			
			if (install) {
				if (messageReceiver != null) {
					messageReceiver.sendMessage(Language.UPDATE_DOWNLOAD_SUCCESSFULLY_NOW_INSTALL.toString());
				}
				SpleefMain.getInstance().log(Level.INFO, ChatColor.stripColor(Language.UPDATE_DOWNLOAD_SUCCESSFULLY_NOW_INSTALL.toString()));
			} else {
				if (messageReceiver != null) {
					messageReceiver.sendMessage(Language.UPDATE_DOWNLOAD_SUCCESSFULLY.toString());
				}
				SpleefMain.getInstance().log(Level.INFO, ChatColor.stripColor(Language.UPDATE_DOWNLOAD_SUCCESSFULLY.toString()));
			}
			// run sync
			if (install) {
				Bukkit.getScheduler().runTask(SpleefMain.getInstance(), new Runnable() {				
					@Override
					public void run() {						
						final File currentFile = SpleefMain.getInstance().getFile();
						final String TO_PATH = SpleefMain.getInstance().getDataFolder().getParentFile().getPath() + File.separator + file.getName();
						Bukkit.getPluginManager().disablePlugin(SpleefMain.getInstance());
						new Thread(new Runnable() {						
							@Override
							public void run() {
								currentFile.delete();
								File to = new File(TO_PATH);
								try {
									Files.move(file, to);
								} catch (IOException e) {
									e.printStackTrace();
								}
								try {
									Bukkit.getPluginManager().enablePlugin(Bukkit.getPluginManager().loadPlugin(to));
								} catch (UnknownDependencyException e) {
									// should not happen (EpicSpleef does not have dependencies!)
									e.printStackTrace();
								} catch (InvalidPluginException e) {
									// should not happen
									e.printStackTrace();
								} catch (InvalidDescriptionException e) {
									// should not happen
									e.printStackTrace();
								}
							}
						}).start();;
					}
				});	
			}
		} catch (Exception e) {
			if (messageReceiver != null) {
				messageReceiver.sendMessage(Language.UPDATE_ERROR.toString());
			}
			SpleefMain.getInstance().log(Level.SEVERE, ChatColor.stripColor(Language.UPDATE_ERROR.toString()));
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
			try {
				if (fout != null) {
					fout.close();
				}
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    /**
     * Query the API to find the approved file's details.
     */
    public static EpicSpleefVersion[] queryVersions() {
    	if (Bukkit.isPrimaryThread()) {
    		throw new IllegalStateException("Must not be invoked from the main thread.");
    	}
    	
        URL url = null;

        try {
            // Create the URL to query using the project's ID
            url = new URL(API_HOST + API_QUERY + PROJECT_ID);
        } catch (MalformedURLException e) {
            // There was an error creating the URL
        	SpleefMain.getInstance().log(Level.SEVERE, "Could not connect to curse api!");
            e.printStackTrace();
            return new EpicSpleefVersion[0];
        }

        try {
            // Open a connection and query the project
            URLConnection conn = url.openConnection();

            /*
            if (apiKey != null) {
                // Add the API key to the request if present
                conn.addRequestProperty("X-API-Key", apiKey);
            }
            */

            // Add the user-agent to identify the program
            conn.addRequestProperty("User-Agent", "EpicSpleef - Update Checker");

            // Read the response of the query
            // The response will be in a JSON format, so only reading one line is necessary.
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();

            // Parse the array of files from the query's response
            JSONArray array = (JSONArray) JSONValue.parse(response);

            EpicSpleefVersion[] versions = new EpicSpleefVersion[array.size()];
            
            for (int i = 0; i < array.size(); i++) {
            	JSONObject jsonObject = (JSONObject) array.get(i);

                // Get the version's title
                String versionName = (String) jsonObject.get(API_NAME_VALUE);

                // Get the version's link
                String versionLink = (String) jsonObject.get(API_LINK_VALUE);

                // Get the version's release type
                String versionType = (String) jsonObject.get(API_RELEASE_TYPE_VALUE);

                // Get the version's file name
                String versionFileName = (String) jsonObject.get(API_FILE_NAME_VALUE);

                // Get the version's game version
                String versionGameVersion = (String) jsonObject.get(API_GAME_VERSION_VALUE);
                
                versions[i] = new EpicSpleefVersion(versionName, versionLink, versionType, versionFileName, versionGameVersion);
			}
            
            return versions;
        } catch (IOException e) {
            // There was an error reading the query
        	SpleefMain.getInstance().log(Level.SEVERE, "Could not get information about plugin updates!");
            e.printStackTrace();
            return new EpicSpleefVersion[0];
        }
    }
}