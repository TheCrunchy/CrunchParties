package CrunchParties;

import java.util.List;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class UserCommands {
	public static class showParty implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					player.sendMessage(Text.of(TextColors.YELLOW, "---------------- ",
							PartiesMain.getUser(party.getLeader()).get().getName(), " party ----------------"));
					player.sendMessage(Text.of(TextColors.YELLOW, "Leader : ", TextColors.WHITE,
							PartiesMain.getUser(party.getLeader()).get().getName()));

					// get names from the UUIDs, could put this in a method but cant be bothered atm
					List<UUID> promoted = party.getPromoted();
					String promotedString = "";
					for (UUID uuid : promoted) {
						promotedString += ", " + PartiesMain.getUser(uuid).get().getName();
					}
					promotedString = promotedString.replaceFirst(",", "");
					List<UUID> members = party.getMembers();
					String memberString = "";
					for (UUID uuid : members) {
						memberString += ", " + PartiesMain.getUser(uuid).get().getName();
					}

					memberString = memberString.replaceFirst(",", "");
					player.sendMessage(Text.of(TextColors.YELLOW, "Trusted : ", TextColors.WHITE, promotedString));
					player.sendMessage(Text.of(TextColors.YELLOW, "Members : ", TextColors.WHITE, memberString));
					String onlineString = "";
					for (UUID uuid : members) {
						if (PartiesMain.getUser(uuid).get().isOnline()) {
							onlineString += ", " + PartiesMain.getUser(uuid).get().getName();
						}
					}
					onlineString = onlineString.replaceFirst(",", "");
					player.sendMessage(Text.of(TextColors.YELLOW, "Online : ", TextColors.WHITE, onlineString));
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));
				}
			}
			return CommandResult.success();
		}
	}

	public static class createParty implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (!partyPlayer.inParty) {
					Party party = new Party();
					party.setLeader(player.getUniqueId());
					party.addMember(player.getUniqueId());
					partyPlayer.setPartyUUID(player.getUniqueId());
					partyPlayer.setPartyStatus(true);
					PartiesMain.saveParty(party);
					PartiesMain.allParties.put(player.getUniqueId(), party);
					src.sendMessage(Text.of(TextColors.YELLOW, "Successfully created party, now go invite some friends, if you have any."));
				} else {
					src.sendMessage(Text.of(TextColors.RED,
							"You are already in a party. You must leave it to be able to create one."));
				}
			}
			return CommandResult.success();
		}
	}

	public static class togglePartyChat implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (partyPlayer.inParty) {
					if (partyPlayer.chatStatus) {
					partyPlayer.setChatStatus(false);
					src.sendMessage(Text.of(TextColors.YELLOW, "Toggled party chat to ", TextColors.RED, "off"));
					}
					else {
						partyPlayer.setChatStatus(true);
						src.sendMessage(Text.of(TextColors.YELLOW, "Toggled party chat to ", TextColors.GREEN, "on"));
					}
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));
				}
			}
			return CommandResult.success();
		}
	}
	public static class sendPartyMessage implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				Text message = Text.of(args.getOne("Message").get());
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					party.sendPartyMessage(message, player.getName());
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));
				}
			}
			return CommandResult.success();
		}
	}
	public static class disbandParty implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					if (party.getLeader().equals(player.getUniqueId())) {
						party.disband();
					}
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));
				}
			}
			return CommandResult.success();
		}
	}
}