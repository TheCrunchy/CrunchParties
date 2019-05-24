package CrunchParties;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
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
	public static HashMap<UUID, Party> allParties = new HashMap<>();
	public static HashMap<UUID, PartyPlayer> allPartyPlayers = new HashMap<>();
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
	@DefaultConfig(sharedRoot = false)
	private Path defaultConfig;

	@Inject
	@ConfigDir(sharedRoot = false)

	private Path privateConfigDir;

	static rootSingleton rootS;
	public static ArrayList <Party> sortedParties = new ArrayList<>();
	@Listener
	public void onServerFinishLoad(GameStartingServerEvent event) {
		Sponge.getEventManager().registerListeners(this, new login());
		Sponge.getEventManager().registerListeners(this, new chatEvent());
		Sponge.getCommandManager().register(this, partyMain, "party");
		rootS = rootS.getInstance();
		rootS.setRoot(root);
		Task removeOffline = Task.builder().execute(new clearOfflines()).delayTicks(1).interval(1, TimeUnit.MINUTES)
				.async().name("Remove offline players from my map").submit(this);
		Task updatePartiesOrder = Task.builder().execute(new sorterTask())
				.delayTicks(1)
				.interval(1, TimeUnit.MINUTES)
				.name("Update factions order task").submit(this);
		File folder = new File(root.toString());
		File[] listOfFiles = folder.listFiles();
		// create a party for every file in the folder
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				Sponge.getServer().getConsole()
				.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "File ", listOfFiles[i].getName()));
				// load the file, fill its data, get all files first
				File file = new File(root.toFile(), listOfFiles[i].getName());
				rootConfig = loadConfig(file.toPath());
				for (Party Party : rootConfig.getParties()) {
					allParties.put(Party.getLeader(), Party);
				}
			}

		}
		System.out.println(allParties.keySet());
	}

	// get the user from uuid to get username and check if online
	public static Optional<User> getUser(UUID owner) {
		Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
		return userStorage.get().get(owner);
	}
	public static Optional<User> getUserFromName(String name) {
		Optional<UserStorageService> userStorage = Sponge.getServiceManager().provide(UserStorageService.class);
		return userStorage.get().get(name);
	}

	// remove offline players from the party maps
	public class clearOfflines implements Runnable {
		public void run() {
			ArrayList<UUID> offlinePlayers = new ArrayList<UUID>();
			Sponge.getServer().getConsole()
			.sendMessage(Text.of(TextColors.DARK_PURPLE, "Clearing offline players from party players cache"));
			for (Entry<UUID, PartyPlayer> partyPlayer : allPartyPlayers.entrySet()) {
				if (!getUser(partyPlayer.getValue().getPlayerUUID()).get().isOnline()) {
					offlinePlayers.add(partyPlayer.getValue().getPlayerUUID());
				}
			}
			for (UUID uuid : offlinePlayers) {
				allPartyPlayers.remove(uuid);
			}
			offlinePlayers.clear();
		}
	}

	//sort the parties by members
	public ArrayList <Party> sortParties() {
		ArrayList <Party> sortedParties = new ArrayList<Party>();
		for (Entry<UUID, Party> party : allParties.entrySet()) {
			sortedParties.add(party.getValue());
			party.getValue().getMembers();
		}
		for (int i = 0 ; i < sortedParties.size() ; i++) {
			for (int j = i+1; j < sortedParties.size() ; j++) {
				if (sortedParties.get(i).getMembers().size() < sortedParties.get(j).getMembers().size()) {
					Collections.swap(sortedParties, i, j);
				}
			}
		}
		return sortedParties;
	}
	//sort the parties every minute
	public class sorterTask implements Runnable {
		public void run() {
			sortedParties = sortParties();
		}
	}

	// setup the party player on login
	public class login {

		@Listener
		public void onLogin(ClientConnectionEvent.Join event, @First Player player) {
			PartyPlayer partyPlayer = new PartyPlayer();
			partyPlayer.setPlayerUUID(player.getUniqueId());
			System.out.println(allParties.keySet());
			for (Entry<UUID, Party> party2 : allParties.entrySet()) {
				System.out.println(allParties.entrySet());
				if (party2.getValue().getMembers().contains(player.getUniqueId())) {
					partyPlayer.setPartyStatus(true);
					partyPlayer.setPartyUUID(party2.getValue().getLeader());
					break;
				} else {
					partyPlayer.setPartyStatus(false);
				}
			}
			allPartyPlayers.put(player.getUniqueId(), partyPlayer);
		}
	}

	// config methods, save the config file
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

	// load a config file
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

	// save a party file
	public static void saveParty(Party party) {
		RootConfig config = new RootConfig();
		File file = new File(rootS.getRoot().toFile(), party.getLeader().toString());
		config.getParties().add(party);
		saveConfig(config, file.toPath());
		allParties.put(party.getLeader(), party);
	}

	// get the party file and delete it
	public static void deleteParty(Party party) {
		File file = new File(rootS.getRoot().toFile(), party.getLeader().toString());
		file.delete();
	}

	CommandSpec create = CommandSpec.builder()
			.description(Text.of("Create a party"))
			.executor(new UserCommands.createParty())
			.build();
	CommandSpec show = CommandSpec.builder()
			.description(Text.of("Show party details"))
			 .arguments(GenericArguments.optional(GenericArguments.user(Text.of("name"))))
			.executor(new UserCommands.showParty())
			.build();
	CommandSpec leave = CommandSpec.builder()
			.description(Text.of("leave a party"))
			.executor(new UserCommands.leaveParty())
			.build();
	CommandSpec list = CommandSpec.builder()
			.description(Text.of("list all parties"))
			.executor(new UserCommands.listParties())
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
	CommandSpec invite = CommandSpec.builder()
			.description(Text.of("Invite a user to the party"))
			.arguments(GenericArguments.player(Text.of("Player")))
			.executor(new UserCommands.inviteUser())
			.build();
	CommandSpec promote = CommandSpec.builder()
			.description(Text.of("Promote a user in the party"))
			.arguments(GenericArguments.user(Text.of("Player")))
			.executor(new UserCommands.promoteUser())
			.build();
	CommandSpec demote = CommandSpec.builder()
			.description(Text.of("Promote a user in the party"))
			.arguments(GenericArguments.user(Text.of("Player")))
			.executor(new UserCommands.demoteUser())
			.build();
	CommandSpec kick = CommandSpec.builder()
			.description(Text.of("Kick a user form the party"))
			.arguments(GenericArguments.user(Text.of("Player")))
			.executor(new UserCommands.kickUser())
			.build();
	CommandSpec changeLeader = CommandSpec.builder()
			.description(Text.of("Change party leadership"))
			.arguments(GenericArguments.player(Text.of("Player")))
			.executor(new UserCommands.changeLeadership())
			.build();
	CommandSpec join = CommandSpec.builder()
			.description(Text.of("Join a party"))
			.arguments(GenericArguments.user(Text.of("Party Leader Username")))
			.executor(new UserCommands.joinParty())
			.build();
	CommandSpec partyMain = CommandSpec.builder()
			.description(Text.of("Main command"))
			.permission("CrunchParties.use")
			.child(show, "show", "s")
			.child(create, "create")
			.child(toggleChat, "toggle")
			.child(sendMessage, "m")
			.child(disbandParty, "disband")
			.child(join, "join")
			.child(invite, "invite")
			.child(leave, "leave")
			.child(changeLeader, "transfer")
			.child(promote, "promote")
			.child(demote, "demote")
			.child(kick, "kick")
			.child(list, "list")
			.build();

}
