package CrunchParties;



import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
public class chatEvent {
	@Listener
	public void onchat(MessageChannelEvent.Chat event, @First Player player){
		Text newMsg = event.getRawMessage();
    	PartyPlayer partyPlayer = PartiesMain.allPartyPlayers.get(player.getUniqueId());

    	System.out.println(event.getContext().toString());
		if (partyPlayer.inParty) {
			//doesnt need to have the == true but i would probably forget if it wasnt there
			if (partyPlayer.chatStatus == true) {
		    	if (event.getContext().toString().equals("Context[]")) {
		    		return;
		    	}
				event.setCancelled(true);
				Party party = PartiesMain.allParties.get(partyPlayer.getPartyUUID());
				party.sendPartyMessage(newMsg, player.getName());
			}
		}
	}


}

