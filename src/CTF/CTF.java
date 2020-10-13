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

	public static final int CLA_PORT = 8188;
	public static final int CTF_PORT = 8189;

	private SSLSocket incoming;
	private BufferedReader serverInput;
	private PrintWriter serverOutput;

	// CLA's validation numbers
	private Vector<String> possibleVoters = new Vector<>();
	private Vector<Voter> votingVoters = new Vector<>();
	private Map<Integer, Integer> votes = new HashMap<Integer, Integer>();

	public CTF(SSLSocket incoming) {
		this.incoming = incoming;
	}

	public void setPossibleVoters(Vector<String> authorizedVoters) {
		this.possibleVoters = authorizedVoters;
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
		if (!possibleVoters.contains(str)) {
			possibleVoters.add(str);
		}
	}

	public void registerVote() throws IOException {
		String str = serverInput.readLine();
		System.out.println("Vote: " + str);
		String[] totalVote = str.split("_");

		// STEP 5
		if (possibleVoters.contains(totalVote[1])) {
			int id = Integer.parseInt(totalVote[0]);
			int vote = Integer.parseInt(totalVote[2]);
			BigInteger validationNumber = new BigInteger(totalVote[1]);
			Voter v = new Voter(validationNumber, vote, id);
			if (!votingVoters.contains(v)) {
				System.out.println("Voter who is voting: " + v);
				votingVoters.add(v);
				// Save the vote
				// Examples online on why we do this
				votes.put(vote, votes.getOrDefault(vote, 0) + 1);
			}
		}
	}

	public void publishResult() throws IOException {
		int bidenVoteCount = 0;
		int trumpVoteCount = 0;
		int totalVotes = votingVoters.size();
		String[] candidates = {"Biden", "Trump"};

		serverOutput.println("Total votes: " + totalVotes);

		serverOutput.println("All the voting voters:");
		for (Voter v : votingVoters) {
			serverOutput.println("ID: " + v.getId() + ", Vote: " + v.getChoice());
			if (v.getChoice() == 0)
				bidenVoteCount++;
			else
				trumpVoteCount++;
		}
		serverOutput.println("\n");
		
		for (Map.Entry<Integer, Integer> v : votes.entrySet()) {
			// Percentage
			float result = 100 * v.getValue() / totalVotes;
			serverOutput.println("Candidate " + candidates[v.getKey()] + ": " + v.getValue() + " (" + result + "%)");
		}
		serverOutput.println("\n");

		if (bidenVoteCount > trumpVoteCount) {
			serverOutput.println("Biden won!");
		} else if (bidenVoteCount < trumpVoteCount) {
			// System.out.println(bidenVoteCount);
			// System.out.println(trumpVoteCount);
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
			while (str != null) {
				switch (str) {
				case "valid_voter":
					registerValidationNr();
					break;
				case "register_vote":
					registerVote();
					break;
				case "result":
					publishResult();
					break;
				default:
					System.out.println("Error: " + str);
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
			Server s = new Server(CTF_PORT);
			// Shared resources for all threads
			Vector<String> authorizedVoters = new Vector<>();
			Vector<Voter> votingVoters = new Vector<>();
			Map<Integer, Integer> votes = new HashMap<Integer, Integer>();

			while (true) {
				SSLSocket socket = (SSLSocket) s.getServerSocket().accept();
				System.out.println("New CTF client connected");

				CTF c = new CTF(socket);
				c.setPossibleVoters(authorizedVoters);
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
