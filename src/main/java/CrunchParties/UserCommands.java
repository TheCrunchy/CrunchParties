package CrunchParties;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class UserCommands {
	public static class showParty implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (args.getOne("name").isPresent()){
		
					User otherParty = (User) args.getOne("name").get();
					Party party = PartiesMain.allParties.get(otherParty.getUniqueId());
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

					player.sendMessage(Text.of(TextColors.YELLOW, "---------------- ",
							PartiesMain.getUser(party.getLeader()).get().getName(), " party ----------------"));
				}
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

					player.sendMessage(Text.of(TextColors.YELLOW, "---------------- ",
							PartiesMain.getUser(party.getLeader()).get().getName(), " party ----------------"));
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));
				}
			}
			else {
				if (args.getOne("name").isPresent()){
					
					User otherParty = (User) args.getOne("name").get();
					Party party = PartiesMain.allParties.get(otherParty.getUniqueId());
					src.sendMessage(Text.of(TextColors.YELLOW, "---------------- ",
							PartiesMain.getUser(party.getLeader()).get().getName(), " party ----------------"));
					src.sendMessage(Text.of(TextColors.YELLOW, "Leader : ", TextColors.WHITE,
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
					src.sendMessage(Text.of(TextColors.YELLOW, "Trusted : ", TextColors.WHITE, promotedString));
					src.sendMessage(Text.of(TextColors.YELLOW, "Members : ", TextColors.WHITE, memberString));
					String onlineString = "";
					for (UUID uuid : members) {
						if (PartiesMain.getUser(uuid).get().isOnline()) {
							onlineString += ", " + PartiesMain.getUser(uuid).get().getName();
						}
					}
					onlineString = onlineString.replaceFirst(",", "");
					src.sendMessage(Text.of(TextColors.YELLOW, "Online : ", TextColors.WHITE, onlineString));

					src.sendMessage(Text.of(TextColors.YELLOW, "---------------- ",
							PartiesMain.getUser(party.getLeader()).get().getName(), " party ----------------"));
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
					src.sendMessage(Text.of(TextColors.YELLOW,
							"Successfully created party, now go invite some friends, if you have any."));
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
					} else {
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

	public static class inviteUser implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				Player invitedPlayer = (Player) args.getOne("Player").get();
				PartyPlayer invitedPartyPlayer = PartiesMain.allPartyPlayers.get(invitedPlayer.getUniqueId());
				if (invitedPartyPlayer.inParty) {
					src.sendMessage(Text.of(TextColors.RED, "That player is already in a party."));
					return CommandResult.success();
				}
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					if (party.hasRank(player.getUniqueId())) {
						src.sendMessage(Text.of(TextColors.AQUA, "Invite sent to ", invitedPlayer.getName()));
						party.addInvite(invitedPlayer.getName());
						invitedPlayer.sendMessage(Text.of(TextColors.GREEN, "You have been invited to the party of ",
								TextColors.WHITE, PartiesMain.getUser(party.getLeader()).get().getName(),
								TextColors.GREEN, " by ", TextColors.WHITE, src.getName(), TextColors.GREEN,
								". This invite will expire in 5 minutes."));
						Text.Builder sendInviteButton = Text.builder();
						sendInviteButton.append(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, " [Accept Invite]"))
						.onClick(TextActions.runCommand(
								"/party join " + PartiesMain.getUser(party.getLeader()).get().getName()))
						.onHover(TextActions.showText(Text.of("Click me to accept this invite"))).build();
						invitedPlayer.sendMessage(Text.of(sendInviteButton));
					} else {
						src.sendMessage(Text.of(TextColors.RED, "You do not have the rank to invite that user."));
					}
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));
				}

			}
			// TODO Auto-generated method stub
			return CommandResult.success();
		}
	}

	public static class joinParty implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (partyPlayer.inParty) {
					src.sendMessage(Text.of(TextColors.RED,
							"You are already in a party. You must leave it to be able to join another."));
					return CommandResult.success();
				}
				User player2 = (User) args.getOne("Party Leader Username").get();
				Party party = PartiesMain.allParties
						.get(PartiesMain.getUserFromName(player2.getName()).get().getUniqueId());
				if (party.invites.containsKey(player.getName())) {
					party.addMember(player.getUniqueId());
					partyPlayer.setPartyStatus(true);
					partyPlayer.setPartyUUID(player2.getUniqueId());
					PartiesMain.allPartyPlayers.put(player.getUniqueId(), partyPlayer);
					PartiesMain.allParties.put(party.getLeader(), party);
					// PartiesMain.saveParty(party);
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You have not been invited to this party."));
				}
			}
			return CommandResult.success();

		}
	}

	public static class leaveParty implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					if (party.getLeader().equals(player.getUniqueId())) {
						src.sendMessage(Text.of(TextColors.RED,
								"You must transfer leadership in order to leave the party. Alternatively disband the party."));
						return CommandResult.success();
					}
					src.sendMessage(Text.of(TextColors.RED, "You have left the party."));
					partyPlayer.setPartyStatus(false);
					partyPlayer.setPartyUUID(null);
					party.removeMember(player.getUniqueId());
					return CommandResult.success();
				}
			}
			return CommandResult.success();

		}
	}

	public static class changeLeadership implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				Player newLeader = (Player) args.getOne("Player").get();
				PartyPlayer newPartyPlayer = PartiesMain.allPartyPlayers.get(newLeader.getUniqueId());
				if (!newPartyPlayer.inParty) {

					src.sendMessage(Text.of(TextColors.RED, "That player is not in a party."));
					return CommandResult.success();
				}
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					if (party.getLeader().equals(player.getUniqueId())) {
						if (!newPartyPlayer.getPartyUUID().equals(party.getLeader())) {
							src.sendMessage(Text.of(TextColors.RED, "That player is not in your party."));
							return CommandResult.success();
						}
						src.sendMessage(Text.of(TextColors.AQUA, "Leadership transferred to ", newLeader.getName()));
						party.changeLeader(newLeader.getUniqueId());
						newLeader.sendMessage(Text.of(TextColors.AQUA, "You are now the party leader."));
					} else {
						src.sendMessage(Text.of(TextColors.RED, "You do not have the rank to change leadership."));
					}
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));
				}

			}
			// TODO Auto-generated method stub
			return CommandResult.success();
		}
	}

	public static class promoteUser implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				User promotedPlayer = (User) args.getOne("Player").get();
				PartyPlayer promotedPartyPlayer = PartiesMain.allPartyPlayers.get(promotedPlayer.getUniqueId());
				if (promotedPartyPlayer.inParty) {
					src.sendMessage(Text.of(TextColors.RED, "That player is already in a party."));
					return CommandResult.success();
				}
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					if (promotedPartyPlayer.getPartyUUID().equals(partyPlayer.getPartyUUID())) {
						if (party.getLeader().equals(player.getUniqueId())) {
							src.sendMessage(Text.of(TextColors.AQUA, "Promoted ", promotedPlayer.getName()));
							party.addPromoted(promotedPlayer.getUniqueId());
							if (promotedPlayer.isOnline()) {
								Sponge.getServer().getPlayer(promotedPlayer.getUniqueId()).get()
								.sendMessage(Text.of(TextColors.AQUA, "You were", TextColors.GREEN,
										" promoted ", TextColors.AQUA, "in the party."));
							}
						} else {
							src.sendMessage(Text.of(TextColors.RED, "You must be the leader to promote people."));
						}
					} else {
						src.sendMessage(Text.of(TextColors.RED, "They are not a member of your party."));
					}
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));

				}
			}
			// TODO Auto-generated method stub
			return CommandResult.success();
		}
	}

	public static class demoteUser implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				User demotedPlayer = (User) args.getOne("Player").get();
				PartyPlayer demotedPartyPlayer = PartiesMain.allPartyPlayers.get(demotedPlayer.getUniqueId());
				if (demotedPartyPlayer.inParty) {
					src.sendMessage(Text.of(TextColors.RED, "That player is already in a party."));
					return CommandResult.success();
				}
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					if (demotedPartyPlayer.getPartyUUID().equals(partyPlayer.getPartyUUID())) {
						if (party.getLeader().equals(player.getUniqueId())) {
							src.sendMessage(Text.of(TextColors.AQUA, "Demoted ", demotedPlayer.getName()));
							if (demotedPlayer.isOnline()) {
								Sponge.getServer().getPlayer(demotedPlayer.getUniqueId()).get()
								.sendMessage(Text.of(TextColors.AQUA, "You were ", TextColors.RED, " demoted ",
										TextColors.AQUA, "in the party."));
							}
							party.removePromoted(demotedPlayer.getUniqueId());
						} else {
							src.sendMessage(Text.of(TextColors.RED, "You must be the leader to demote people."));
						}
					} else {
						src.sendMessage(Text.of(TextColors.RED, "They are not a member of your party."));
					}
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));

				}
			}
			// TODO Auto-generated method stub
			return CommandResult.success();
		}
	}

	public static class kickUser implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player player = (Player) src;
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());
				User kickedPlayer = (User) args.getOne("Player").get();
				PartyPlayer kickPartyPlayer = PartiesMain.allPartyPlayers.get(kickedPlayer.getUniqueId());
				if (!kickPartyPlayer.inParty) {
					src.sendMessage(Text.of(TextColors.RED, "That player is not in a party."));
					return CommandResult.success();
				}
				if (partyPlayer.inParty) {
					Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
					if (!party.getMembers().contains(kickedPlayer.getUniqueId())) {
						src.sendMessage(Text.of(TextColors.RED, "They are not a member of your party."));
						return CommandResult.success();
					}
					if (party.hasRank(player.getUniqueId())) {
						if (party.getLeader().equals(player.getUniqueId())
								|| party.hasRank(kickedPlayer.getUniqueId())) {
							src.sendMessage(Text.of(TextColors.AQUA, "You cannot kick the leader or promoted people."));
							return CommandResult.success();
						} else {
							party.removeMember(kickedPlayer.getUniqueId());
							src.sendMessage(Text.of(TextColors.AQUA, "Successfully kicked ", kickedPlayer.getName()));
							// if (kickedPlayer.isOnline()) {
							// Sponge.getServer().getPlayer(kickedPlayer.getUniqueId()).get().sendMessage(Text.of(TextColors.AQUA,
							// "You were ", TextColors.RED, " kicked ", TextColors.AQUA, "from the
							// party."));
							// }
							kickPartyPlayer.setPartyStatus(false);
							kickPartyPlayer.setChatStatus(false);
							kickPartyPlayer.setPartyUUID(null);
						}
					} else {
						src.sendMessage(Text.of(TextColors.RED, "You do not have the required rank to kick people."));

					}
				} else {
					src.sendMessage(Text.of(TextColors.RED, "You are not a member of any party."));

				}
			}
			// TODO Auto-generated method stub
			return CommandResult.success();
		}
	}

	public static class listParties implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			int max = 10;
			if (args.getOne("Page Number").isPresent()) {
				max = (int) args.getOne("Page Number").get() * 10;
				if (PartiesMain.allParties.size() == 0) {
					src.sendMessage(Text.of("There are currently no parties."));
					return CommandResult.success();
				}
				if (PartiesMain.allParties.size() > max - 10) {
					src.sendMessage(Text.of(TextColors.AQUA, "Parties Page ", args.getOne("Page Number").get(), " of ",
							((PartiesMain.allParties.size() / 10)
									+ ((PartiesMain.allParties.size() % 10) > 0 ? 1 : 0))));
				} else {
					src.sendMessage(Text.of(TextColors.AQUA, "There are no more pages to display."));
				}
				for (int i = 10; i > 0; i--) {
					if (PartiesMain.allParties.size() > max - i) {
						Text.Builder sendMessage = Text.builder();
						sendMessage
						.append(Text.of(TextColors.WHITE, max - i + 1, ". ", TextColors.WHITE,
								PartiesMain.sortedParties.get(max - i).getLeaderName(), TextColors.YELLOW,
								" - ", PartiesMain.sortedParties.get(max - i).getMembers().size(), " Members"))
						.onClick(TextActions.runCommand(
								"/party show " + PartiesMain.sortedParties.get(max - i).getLeaderName()))
						.onHover(TextActions.showText(Text.of("Click me to view party info for ",
								PartiesMain.sortedParties.get(max - i).getLeaderName())))
						.build();

						src.sendMessage(Text.of(sendMessage));
					}
				}
			} else {
				max = 10;
				src.sendMessage(Text.of(TextColors.AQUA, "Parties Page 1 of ",
						((PartiesMain.allParties.size() / 10) + ((PartiesMain.allParties.size() % 10) > 0 ? 1 : 0))));
				for (int i = 10; i > 0; i--) {
					if (PartiesMain.allParties.size() > max - i) {
						Text.Builder sendMessage = Text.builder();

						sendMessage
						.append(Text.of(TextColors.WHITE, max - i + 1, ". ", TextColors.WHITE,
								PartiesMain.sortedParties.get(max - i).getLeaderName(), TextColors.YELLOW,
								" - ", PartiesMain.sortedParties.get(max - i).getMembers().size(), " Members"))
						.onClick(TextActions.runCommand(
								"/party show " + PartiesMain.sortedParties.get(max - i).getLeaderName()))
						.onHover(TextActions.showText(Text.of("Click me to view party info for ",
								PartiesMain.sortedParties.get(max - i).getLeaderName())))
						.build();

						src.sendMessage(Text.of(sendMessage));
					}
				}
			}
			// TODO Auto-generated method stub
			return CommandResult.success();
		}
	}
}