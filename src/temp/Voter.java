package temp;

import java.math.BigInteger;
import java.util.Random;

public class Voter {
    private BigInteger validationNumber = BigInteger.ZERO;
    private int vote, id;

    // Ska den finnas?
    public Voter(int vote, int id) {
        this.vote = vote;
        this.id = id;
    }
    
    // Each voter chooses a random identification number
    public Voter(int vote) {
        Random r = new Random();
        this.id = r.nextInt(1000000000);
        this.vote = vote;
    }
    
    // Kolla upp
    public Voter(BigInteger validationNumber, int vote, int id) {
        this.validationNumber = validationNumber;
        this.vote = vote;
        this.id = id;
    }
    
    public int getId() {
        return id;
    }

    public int getVote() {
        return id;
    }
    
    public int getChoice() {
        return vote;
    }
    
    public String getValidationNr() {
        return validationNumber.toString();
    }
    
    public void setValidationNumber(BigInteger validationNumber) {
        this.validationNumber = validationNumber;
    }
    
    // STEP 4
    public String myVote() {
        return id + "_" + validationNumber.toString() + "_" + vote;
    }
    
}
