package org.desmin88.simplesave;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;

//import static org.desmin88.simplesave.FileUtils.FILE_SEPARATOR;
public class Backup {

	public SimpleSave plugin;
	public Backup(SimpleSave instance){
		plugin = instance;
	}
	// All credit for this class file goes to Meldanor for allowing me to modify his code.
	public void backup() {
		// the Player Position are getting stored
		String[] worldfilter = plugin.ConfigArray[19].split(",");
		try {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run() {
					plugin.setY(true);
					plugin.saveWorlds();

				}
			});

			for (World world : plugin.getServer().getWorlds()) {
				if(Arrays.asList(worldfilter).contains(world.getName())) {
					continue;
				}

				String FILE_SEPARATOR = "/";
				String backupDir = plugin.ConfigArray[16].concat(FILE_SEPARATOR).concat(world.getName());

				FileUtils.copyDirectory(new File(world.getName()), new File(backupDir));

				String targetName = world.getName();
				String targetDir = plugin.ConfigArray[16].concat(FILE_SEPARATOR);

				targetName = world.getName();
				FileUtils.zipDirectory(world.getName(), targetDir.concat(targetName).concat(getDate()));
				FileUtils.deleteDirectory(new File(backupDir));
			}
		}
		catch (Exception e) {
			e.printStackTrace(System.out);
		}

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				if(!plugin.ConfigArray[17].equals("true")) {
					plugin.setY(false);
				}
			}
		});

		deleteOldBackups();
	}


	private String getDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(plugin.ConfigArray[18]);
		return sdf.format(cal.getTime());
	}

	private void deleteOldBackups () {
		try {
			//
			File backupDir = new File(plugin.ConfigArray[16]);
			// get every zip file in the backup Dir
			File[] tempArray = backupDir.listFiles();
			// when are more backups existing as allowed as to store
			if (tempArray.length > Integer.parseInt(plugin.ConfigArray[13])) {
				plugin.log.info("SimpleSave: Deleting old backups");

				// Store the to delete backups in a list
				ArrayList<File> backups = new ArrayList<File>(tempArray.length);
				// For this add all backups in the list and remove later the newest ones
				backups.addAll(Arrays.asList(tempArray));

				// the current index of the newest backup
				int maxModifiedIndex;
				// the current time of the newest backup
				long maxModified;

				//remove all newest backups from the list to delete
				for(int i = 0 ; i < Integer.parseInt(plugin.ConfigArray[13]) ; ++i) {
					maxModifiedIndex = 0;
					maxModified = backups.get(0).lastModified();
					for(int j = 1 ; j < backups.size(); ++j) {
						File currentFile = backups.get(j);
						if (currentFile.lastModified() > maxModified) {
							maxModified = currentFile.lastModified();
							maxModifiedIndex = j;
						}
					}
					backups.remove(maxModifiedIndex);
				}
				// this are the oldest backups, so delete them
				for(File backupToDelete : backups)
					backupToDelete.delete();
			}
		}
		catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
