package br.com.techsneeker.objects;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Survey {

    private UUID id;
    private String owner;
    private Map<String, Integer> vote;

    public Survey(UUID id, String owner, Map<String, Integer> vote) {
        this.id = id;
        this.owner = owner;
        this.vote = vote;
    }

    public UUID getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public Map<String, Integer> getVote() {
        return vote;
    }

    public void addVote(String key) {
        Integer currentValue = vote.get(key);
        vote.put(key, currentValue + 1);
    }

    public static Survey fromListById(List<Survey> values, UUID id) {
        return values.stream().filter(survey -> survey.getId().equals(id)).findAny().get();
    }

}
