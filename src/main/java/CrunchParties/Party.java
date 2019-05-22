package CrunchParties;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

@ConfigSerializable
public class Party {
	@Setting("leader")
	private UUID leader;

	public UUID getLeader() {
		return leader;
	}

	public void setLeader(UUID leader) {
		this.leader = leader;
		PartiesMain.saveParty(this);
	}
	public void changeLeader(UUID leader) {
		PartiesMain.deleteParty(this);
		PartiesMain.allParties.remove(this.getLeader());
		this.leader = leader;
		for (UUID keyword : members){	
			if (Sponge.getServer().getPlayer(keyword).isPresent()) {		
				PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(keyword);
				partyPlayer.setPartyUUID(leader);
				Sponge.getServer().getPlayer(keyword).get().sendMessage(Text.of(TextColors.AQUA, PartiesMain.getUser(leader).get().getName(), " Is now the party leader."));
			}
		}
		PartiesMain.saveParty(this);

		PartiesMain.allParties.put(this.getLeader(), this);
	}
	@Setting("members")
	private List<UUID> members = new ArrayList<>();

	public void setMembers(List<UUID> members) {
		this.members = members;
	}

	public List<UUID> getMembers() {
		return members;
	}

	@Setting("promoted")
	private List<UUID> promoted = new ArrayList<>();

	public void setPromoted(List<UUID> promoted) {
		this.promoted = promoted;
	}

	public List<UUID> getPromoted() {
		return promoted;
	}

	public void addMember(UUID uuid) {
		members.add(uuid);
		for (UUID keyword : members){	
			if (Sponge.getServer().getPlayer(keyword).isPresent()) {		
				Sponge.getServer().getPlayer(keyword).get().sendMessage(Text.of(TextColors.AQUA, PartiesMain.getUser(uuid).get().getName(), " has joined the party."));
			}
		}
		PartiesMain.saveParty(this);
	}

	public void removeMember(UUID uuid) {
		members.remove(uuid);
		for (UUID keyword : members){
			if (Sponge.getServer().getPlayer(keyword).isPresent()) {
				Sponge.getServer().getPlayer(keyword).get().sendMessage(Text.of(TextColors.RED, PartiesMain.getUser(uuid).get().getName(), " has left the party or was kicked."));
			}
		}
		PartiesMain.saveParty(this);
	}

	public void addPromoted(UUID uuid) {
		promoted.add(uuid);
		PartiesMain.saveParty(this);
	}

	public void removePromoted(UUID uuid) {
		promoted.remove(uuid);
		PartiesMain.saveParty(this);
	}

	public boolean hasRank(UUID uuid) {
		if (leader.equals(uuid)) {
			return true;
		}
		if (promoted.contains(uuid)) {
			return true;
		}
		return false;
	}
	public HashMap<String, Long> invites = new HashMap<>();
	
	public void addInvite(String string) {
		invites.put(string, System.currentTimeMillis() / 1000 );
	}
	public void removeInvite(String string) {
		invites.remove(string);
	}
	public void disband() {
		PartiesMain.allParties.remove(this.getLeader());
		for (UUID keyword : members){
			
			for (Entry<UUID, PartyPlayer> partyPlayer : PartiesMain.allPartyPlayers.entrySet()) {
				if (partyPlayer.getValue().getPartyUUID().equals(this.getLeader())) {
				partyPlayer.getValue().setPartyUUID(null);
				partyPlayer.getValue().setPartyStatus(false);
				partyPlayer.getValue().setChatStatus(false);
			}
			}
			if (Sponge.getServer().getPlayer(keyword).isPresent()) {
				Sponge.getServer().getPlayer(keyword).get().sendMessage(Text.of(TextColors.RED, "The party has been disbanded."));
			}
		}
		PartiesMain.deleteParty(this);
	}
	public void sendPartyMessage(Text message, String playerName) {
		for (UUID keyword : members){
			if (Sponge.getServer().getPlayer(keyword).isPresent()) {
				String rank = "";
				if (this.hasRank(keyword)) {
					rank = "Promoted";
				}
				if (this.getLeader().equals(keyword)) {
					rank = "Leader";
				}
				Sponge.getServer().getPlayer(keyword).get().sendMessage(Text.of(TextColors.YELLOW, "Party --" , " ", TextColors.YELLOW, rank, " ", TextColors.WHITE, playerName, " >> ", message));
			}
		}
	}
}
