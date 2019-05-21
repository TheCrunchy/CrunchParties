package CrunchParties;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public class RootConfig {

    @Setting("party")
    private List<Party> party = new ArrayList<>();

    public List<Party> getParties() {
        return party;
    }
}
