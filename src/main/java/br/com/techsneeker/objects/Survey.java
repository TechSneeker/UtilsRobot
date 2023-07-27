package br.com.techsneeker.objects;

import java.util.*;

public class Survey {

    private final UUID id;
    private final String owner;
    private final Map<String, Integer> vote;
    private final Set<String> voters = new HashSet<>();

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

    public void addVoter(String userId) {
        voters.add(userId);
    }

    public boolean hasVoted(String userId) {
        return voters.contains(userId);
    }

    public static Survey fromListById(List<Survey> values, UUID id) {
        return values.stream().filter(survey -> survey.getId().equals(id)).findAny().get();
    }

}
