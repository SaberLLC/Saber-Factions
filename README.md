# SavageFactions

SavageFactions is a fork of the popular Factions plugin FactionsUUID. The Goal of SavageFactions is to make the ultimate competitive factions experience.

The plugin contains lots of new revolutionary features, which can be looked at in further detail on the wiki.

Currently the plugin is marketed on [Spigot](www.spigotmc.org) ,  a platform for Minecraft Servers which has an API that SavageFactions uses to enhance the game.

The plugin page can be found [here](https://www.spigotmc.org/resources/savagefactions-the-ultimate-factions-plugin-1-7-1-13.52891/), it contains a few gifs which show features of the plugin.

## Users
The installation guide can be found on the [installation page](https://github.com/ProSavage/SavageFactions/wiki/Installation-Guide) of the [wiki](https://github.com/ProSavage/SavageFactions/wiki)

Dependencies
 - [Essentials Or EssentialsX](https://ci.ender.zone/job/EssentialsX/)
 - [Vault](https://www.spigotmc.org/resources/vault.34315/)

Soft Dependencies
 - [CoreProtect (for /f inspect)](https://www.spigotmc.org/resources/coreprotect.8631/)

## Developers
This plugin has an extensive API and viewable Javadocs.
The Javadocs can be found in the javadocs folder, they are generated at every major release.
They are also hosted on my webserver and can be found [here](http://prosavage.net/factions_javadoc/)

If you would like to fork/contribute to SavageFactions I have made a video on how to compile the plugin correctly.
The video can be found [here](https://www.youtube.com/watch?v=fnDwjA2gX-E).


If you would like to use the plugin as a dependency in your project, you can use maven.

   ```xml
   <repository>  
	 <id>savagefactions-repo</id>  
		 <url>https://rawgit.com/ProSavage/SavageFactions/1.6.x</url>  
	 <releases>  
		 <enabled>true</enabled>  
	 </releases>  
		 <snapshots>  
			 <enabled>true</enabled>  
		 </snapshots>  
</repository>

<dependency>  
	 <groupId>com.massivecraft</groupId>  
	 <artifactId>Factions</artifactId>  
	 <version>{version}</version>  
	 <scope>provided</scope>  
</dependency>
```
The {version} has to be replaced with a version you find in the [mvn-repo](https://github.com/ProSavage/SavageFactions/tree/1.6.x/mvn-repo/com/massivecraft/Factions) folder. An example version that can be used is: `1.6.9.5-U0.2.1-RC-1.5-BETA`


If you would like to learn how to use the API there are lots of examples that can be found in the [wiki](https://github.com/ProSavage/SavageFactions/wiki) on the [API-Usage](https://github.com/ProSavage/SavageFactions/wiki/API-Usage) page.
