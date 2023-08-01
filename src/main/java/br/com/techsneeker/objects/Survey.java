package br.com.techsneeker.objects;

import java.util.*;

public class Survey {

    private final UUID id;
    private final String owner;
    private final Map<String, Integer> vote;
    private final Set<String> voters = new HashSet<>();

    public Survey(String id, String owner, Map<String, Integer> vote) {
        this.id = UUID.fromString(id);
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

    public int getVoterCount() {
        return vote.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void addVoter(String userId) {
        voters.add(userId);
    }

    public boolean hasVoted(String userId) {
        return voters.contains(userId);
    }

    public static Survey getFromListById(List<Survey> values, UUID id) {
        return values.stream().filter(survey -> survey.getId().equals(id)).findAny().get();
    }

    public static void removeFromListById(List<Survey> values, String id) {
        values.removeIf(survey -> survey.getId().equals(UUID.fromString(id)));
    }

}
