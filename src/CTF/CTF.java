package CTF;

import temp.Server;
import temp.Voter;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;

public class CTF implements Runnable {
	private int port;

	public static final int CLA_PORT = 8188;
	public static final int CTF_PORT = 8189;
	static final String KEYSTORE = "src/CLA/LIUkeystore.ks";
	static final String TRUSTSTORE = "src/CLA/LIUtruststore.ks";
	static final String KEYSTOREPASS = "123456";
	static final String TRUSTSTOREPASS = "abcdef";

	private SSLSocket incoming;
	private BufferedReader serverInput;
	private PrintWriter serverOutput;

	// String versions of CLA's validation numbers
	private Vector<String> authorizedVoters = new Vector<>();
	private Vector<Voter> votingVoters = new Vector<>();
	private Map<Integer, Integer> votes = new HashMap<Integer, Integer>();

	public CTF(SSLSocket incoming) {
		this.incoming = incoming;
	}

	public void setAuthorizedVoters(Vector<String> authorizedVoters) {
		this.authorizedVoters = authorizedVoters;
	}

	public void setVotes(Map<Integer, Integer> votes) {
		this.votes = votes;
	}
	
    public void setVotingVoters(Vector<Voter> votingVoters) {
        this.votingVoters = votingVoters;
    }

	public void registerValidationNr() throws IOException {
		String str = serverInput.readLine();
		System.out.println("Validation Nr: " + str);
		if (!authorizedVoters.contains(str)) {
			authorizedVoters.add(str);
		}
	}

	public void registerVote() throws IOException {
		String str = serverInput.readLine();
		System.out.println("Vote: " + str);
		String[] totalVote = str.split("_");
		
		// STEP 5
		if (authorizedVoters.contains(totalVote[1])) {
			int id = Integer.parseInt(totalVote[0]);
			int vote = Integer.parseInt(totalVote[2]);
			BigInteger validationNumber = new BigInteger(totalVote[1]);
			Voter v = new Voter(validationNumber, vote, id);
			if (!votingVoters.contains(v)) {
				System.out.println("Voter who is voting: " + v);
				votingVoters.add(v);
				// Save the vote
				// Many examples online
				votes.put(vote, votes.getOrDefault(vote, 0) + 1);
			}
		}
	}

	public void publishResult() throws IOException {
		int bidenVoteCount = 0;
		int trumpVoteCount = 0;
        int totalVotes = votingVoters.size();
        
        serverOutput.println("election");
        serverOutput.println("Please, vote 0 for Biden and 1 for Trump.");
        serverOutput.println("Total votes: " + totalVotes);
        
        for (Map.Entry<Integer, Integer> v : votes.entrySet()) {
        		// Percentage
            float result = 100 * v.getValue() / totalVotes;
            serverOutput.println("Candidate " + v.getKey() + ": "
                    + v.getValue() + " (" +  result + "%)");
        }
        
        serverOutput.println("All the voting voters:");
        for (Voter v : votingVoters) {
            serverOutput.println("ID: " + v.getId() + ", Vote: " + v.getChoice());
            if (v.getChoice() == 0) bidenVoteCount++;
            else trumpVoteCount++;
        }
        
        if (bidenVoteCount > trumpVoteCount) {
            serverOutput.println("Biden won!");
        } else if (bidenVoteCount < trumpVoteCount) {
//        	System.out.println(bidenVoteCount);
//        	System.out.println(trumpVoteCount);
            serverOutput.println("Trump won!");
        } else {
            serverOutput.println("It's a tie!!!!");
        }
        serverOutput.println("end");
	}

	public void run() {
		try {
			serverInput = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
			serverOutput = new PrintWriter(incoming.getOutputStream(), true);
			String str = serverInput.readLine();
			System.out.println("BEFORE CTF CASE");
			while (str != null) {
				switch (str) {
				case "valid_voter":
					System.out.println("VALID VOTER");
					registerValidationNr();
					break;
				case "register_vote":
					registerVote();
					break;
				case "result":
					publishResult();
					break;
				default:
					System.out.println("Error, can't recognize command: " + str);
					break;
				}
				str = serverInput.readLine();
			}
			incoming.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("INNAN");
			Server s = new Server(CTF_PORT);
			System.out.println("EFTER");
			// Shared resources for all threads
			Vector<String> authorizedVoters = new Vector<>();
			Vector<Voter> votingVoters = new Vector<>();
			Map<Integer, Integer> votes = new HashMap<Integer, Integer>();

			while (true) {
				SSLSocket socket = (SSLSocket) s.getServerSocket().accept();
				System.out.println("New CTF client connected");

				CTF c = new CTF(socket);
				c.setAuthorizedVoters(authorizedVoters);
				c.setVotingVoters(votingVoters);
				c.setVotes(votes);
				Thread t = new Thread(c);
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
