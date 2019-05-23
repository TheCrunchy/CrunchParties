package CrunchParties;

import java.util.UUID;

public class PartyPlayer {
	private UUID partyUUID;

    public UUID getPartyUUID() {
        return partyUUID;
    }
    public void setPartyUUID(UUID uuid) {
        this.partyUUID = uuid;
        update();
    }
    private UUID playerUUID;
    
    public void setPlayerUUID(UUID uuid) {
    	this.playerUUID = uuid;
    	update();
    }
    public UUID getPlayerUUID() {
    	return this.playerUUID;
    }
    

    public boolean inParty;
    
    public void setPartyStatus(boolean bool) {
    		this.inParty = bool;
    	update();
    }
    public boolean chatStatus;
    public void setChatStatus(boolean bool) {
    		this.chatStatus = bool;
    	
    	update();
    }
    public void update() {
    	PartiesMain.allPartyPlayers.put(this.getPlayerUUID(), this);
    }
}
