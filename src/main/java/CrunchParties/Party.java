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

	public void setpromoted(List<UUID> promoted) {
		this.promoted = promoted;
	}

	public List<UUID> getPromoted() {
		return promoted;
	}

	public void addMember(UUID uuid) {
		members.add(uuid);
	}

	public void removeMember(UUID uuid) {
		members.remove(uuid);
	}

	public void addPromoted(UUID uuid) {
		promoted.add(uuid);
	}

	public void removePromoted(UUID uuid) {
		promoted.remove(uuid);
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
				partyPlayer.getValue().setPartyUUID(null);
				partyPlayer.getValue().setPartyStatus(false);
				partyPlayer.getValue().setChatStatus(false);
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
