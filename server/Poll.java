import java.util.Map;
import java.util.HashMap;

public class Poll {
    private Map<String, Integer> poll = new HashMap<>();

    Poll(String[] options){
        // Add all vote options with 0 votes
        for (String voteOption : options){
            poll.put(voteOption, 0);
        }
    }

    /** Adds a vote for a given option
     * 
     * @param option vote choice
     * @return String reporting new total for that vote option
     * @throws IllegalArgumentException
     */
    public synchronized String vote(String option) throws IllegalArgumentException {
        if (poll.containsKey(option)){
            int votes = poll.get(option) + 1;
            poll.put(option, votes);
            return option + " now has " + votes + " vote(s).";
        }
        else {
            throw new IllegalArgumentException("Cannot find that vote option");
        }
    }

    @Override
    public String toString() {
        StringBuilder results = new StringBuilder();
        poll.forEach((k,v) -> results.append(k + " has " + v + " vote(s)\n")); 
        return results.toString();
    }

}