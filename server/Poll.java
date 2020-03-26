import java.util.Map;
import java.util.HashMap;

public class Poll {
    // Poll votes are stored as a map.  [ option -> no. votes ]
    private Map<String, Integer> poll = new HashMap<>();

    /** Initalises the poll.
     *  @param options options to be voted for
     */
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

    /**
     * @return line for each vote option with the no. votes it has
     */
    @Override
    public String toString() {
        StringBuilder results = new StringBuilder();
        poll.forEach((k,v) -> results.append(k + " has " + v + " vote(s)\n")); 
        return results.toString();
    }

}
