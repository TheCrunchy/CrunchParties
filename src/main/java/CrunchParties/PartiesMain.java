package CrunchParties;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

import ninja.leaping.configurate.SimpleConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;


@Plugin(id = "crunchparties", name = "Parties by Crunch", version = "1.0", description = "Simple party system with party chat")
public class PartiesMain {
	public static  HashMap<UUID, Party> allParties = new HashMap<>();
	public static  HashMap<UUID, PartyPlayer> allPartyPlayers = new HashMap<>();
	private RootConfig rootConfig;

	@Inject
	private Logger logger;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path root;

	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;


	@Inject
	@DefaultConfig(sharedRoot =false)
	private Path defaultConfig;

	@Inject
	@ConfigDir(sharedRoot = false)

	private Path privateConfigDir;
	
	static rootSingleton rootS;
	@Listener
	public void onServerFinishLoad(GameStartingServerEvent event) {
		Sponge.getEventManager().registerListeners(this, new login());
		Sponge.getEventManager().registerListeners(this, new chatEvent());
	 	Sponge.getCommandManager().register(this, partyMain, "party");
	 	rootS = rootS.getInstance();
	 	rootS.setRoot(root);
		File folder = new File(root.toString());
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.LIGHT_PURPLE, "File " , listOfFiles[i].getName()));
				//load the file, fill its data, get all files first
				File file = new File(root.toFile(), listOfFiles[i].getName());
				rootConfig = loadConfig(file.toPath());
				for (Party Party : rootConfig.getParties()) {
					allParties.put(Party.getLeader(), Party);
				}
			}

		}
	}
    CommandSpec create = CommandSpec.builder()
    	    .description(Text.of("Create a party"))
    	    .executor(new UserCommands.createParty())
    	    .build();
    CommandSpec show = CommandSpec.builder()
    	    .description(Text.of("Show party details"))
    	    .executor(new UserCommands.showParty())
    	    .build();
    CommandSpec toggleChat = CommandSpec.builder()
    	    .description(Text.of("Toggle chat"))
    	    .executor(new UserCommands.togglePartyChat())
    	    .build();
    CommandSpec disbandParty = CommandSpec.builder()
    	    .description(Text.of("Delete the party"))
    	    .executor(new UserCommands.disbandParty())
    	    .build();
    CommandSpec sendMessage = CommandSpec.builder()
    	    .description(Text.of("Send a party message without being in party chat"))
    	    .arguments(GenericArguments.remainingJoinedStrings(Text.of("Message")))
    	    .executor(new UserCommands.sendPartyMessage())
    	    .build();
	CommandSpec partyMain = CommandSpec.builder()
			.description(Text.of("Main command"))
			.permission("CrunchParties.use")
			.child(show, "show", "s")   	  
			.child(create, "create")
			.child(toggleChat, "toggle")
			.child(sendMessage, "m")
			.child(disbandParty, "disband")
			.build();
	
	private static void saveConfig(RootConfig config, Path path) {
		try {
			if (!path.toFile().getParentFile().exists()) {
				Files.createDirectories(path.toFile().getParentFile().toPath());
			}
			ObjectMapper.BoundInstance configMapper = ObjectMapper.forObject(config);
			HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(path).build();
			SimpleConfigurationNode scn = SimpleConfigurationNode.root();
			configMapper.serialize(scn);
			hcl.save(scn);
		} catch (Exception e) {
			throw new RuntimeException("Could not write file. ", e);
		}
	}
	private RootConfig loadConfig(Path path) {
		try {
			logger.info("Loading config...");
			ObjectMapper<RootConfig> mapper = ObjectMapper.forClass(RootConfig.class);
			HoconConfigurationLoader hcl = HoconConfigurationLoader.builder().setPath(path).build();
			return mapper.bind(new RootConfig()).populate(hcl.load());
		} catch (Exception e) {
			throw new RuntimeException("Could not load file " + path, e);
		}
	}
	
	public static void saveParty(Party party) {
		RootConfig config = new RootConfig();
		File file = new File(rootS.getRoot().toFile(), party.getLeader().toString());
        config.getParties().add(party);
        saveConfig(config, file.toPath());
	}
	public static void deleteParty(Party party) {
		File file = new File(rootS.getRoot().toFile(), party.getLeader().toString());
		file.delete();
	}
    public static Optional<User> getUser(UUID owner) {
	       Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
	        return userStorage.get().get(owner);
	    }
	public class login {

		@Listener
		public void onLogin(ClientConnectionEvent.Join event, @First Player player) {
			Party party = new Party();
			PartyPlayer partyPlayer = new PartyPlayer();
			for (Entry<UUID, Party> party2 : allParties.entrySet()) {
				if (party2.getValue().getMembers().contains(player.getUniqueId())) {
					partyPlayer.setPartyStatus(true);
					partyPlayer.setPlayerUUID(player.getUniqueId());
					partyPlayer.setPartyUUID(party2.getValue().getLeader());
				}
				else {
					partyPlayer.setPartyStatus(false);
					
				}
			}
			allPartyPlayers.put(player.getUniqueId(), partyPlayer);
		}
	}

}
