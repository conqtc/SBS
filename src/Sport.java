
/**
 * Abstract class Sport - 
 * 
 * @author (your name here)
 * @version (version number or date here)
 */

import java.util.*;
import CException.*;

public abstract class Sport implements IFinancialService  {
	//
	private String name;
	
	//
	private double usageFee;
	
	//
	private double insuranceFee;
	
	//
	private double affiliationFee;
	
	/* 
	 * Since requirement states: all instance variables must be private, it can't be declared as protected
	 * thus I had to add a setter function for this variable and call setMaximumBookingMinutes from derived classes
	 */
	private int maximumBookingMinutes;
	
	//
	private ArrayList<Court> courtList;
	
	/**
	 * Abstract method called in derived classes
	 * @throws InvalidNumberException if number value (maximum booking times) is invalid
	 */
	protected abstract void initializeSportAttributes() throws InvalidNumberException;

	/**
	 * Sport constructor 
	 * @param sportName Sport name
	 * @throws NullPointerException If sport name is null
	 * @throws EmptyStringException If sport name is empty
	 * @throws InvalidNumberException if maximum booking duration time is invalid
	 */
	public Sport(String sportName) throws NullPointerException, EmptyStringException, InvalidNumberException {
		if (sportName == null) {
			throw new NullPointerException("Sport name is null.");
		}
		
		sportName = sportName.trim();
		if (sportName.isEmpty()) {
			throw new EmptyStringException("Sport name is empty.");
		}
		
		this.name = sportName;
		this.courtList = new ArrayList<>();
		
		// calls abstract method that will be implemented by derived classes
		initializeSportAttributes();
	}
	

	/**
	 * Sport constructor 
	 * @param sportName Sport name
	 * @param usage Usage fee
	 * @param insurance Insurance fee
	 * @param affiliation Affiliation fee
	 * @throws NullPointerException If sport name is null
	 * @throws EmptyStringException If sport name is empty
	 * @throws InvalidNumberException If fee values is invalid
	 */
	public Sport(String sportName, double usage, double insurance, double affiliation) throws 
	             NullPointerException, EmptyStringException, InvalidNumberException {
		this(sportName);
		
		if (usage < 0.0) {
			throw new InvalidNumberException("New Sport: usage fee is negative [" + usage + "].");
		}
		
		if (insurance < 0.0) {
			throw new InvalidNumberException("New Sport: insurance fee is negative [" + insurance + "].");
		}
		
		if (affiliation < 0.0) {
			throw new InvalidNumberException("New Sport: affiliation fee is negative [" + affiliation + "].");
		}
		
		this.usageFee = usage;
		this.insuranceFee = insurance;
		this.affiliationFee = affiliation;
	}
	
	/**
	 * Get sport name
	 * @return String sport name
	 */
	public String getSportName() {
		return this.name;
	}
	
	/**
	 * Get maximum booking duration in minutes
	 * @return int value
	 */
	public int getMaximumBookingMinutes() {
		return this.maximumBookingMinutes;
	}
	
	/**
	 * Set maximum booking value
	 * @param minutes New value
	 * @throws InvalidNumberException If new value is invalid (negative)
	 */
	public void setMaximumBookingMinutes(int minutes) throws InvalidNumberException {
		if (minutes <= 0) {
			throw new InvalidNumberException("Sport.setMaximumBookingMinutes: maximum booking is NOT positive [" + minutes + "].");
		}
		this.maximumBookingMinutes = minutes;
	}

	/**
	 * Get usage fee
	 * @return usage fee
	 */
	public double getUsageFee() {
		return this.usageFee;
	}

	/**
	 * Set usage fee
	 * @param usageFee New usage fee value
	 * @throws InvalidNumberException If usage fee is invalid
	 */
	public void setUsageFee(double usageFee) throws InvalidNumberException {
		if (usageFee < 0.0) {
			throw new InvalidNumberException("Sport.setUsageFee: usage fee is negative [" + usageFee + "].");
		}
		
		this.usageFee = usageFee;
	}
	
	/**
	 * Get insurance fee
	 * @return insurance fee
	 */
	public double getInsuranceFee() {
		return this.insuranceFee;
	}
	
	/**
	 * Set insurance fee
	 * @param insuranceFee New insurance fee
	 * @throws InvalidNumberException If insurance fee is invalid
	 */
	public void setInsuranceFee(double insuranceFee) throws InvalidNumberException {
		if (insuranceFee < 0.0) {
			throw new InvalidNumberException("Sport.setInsuranceFee: insurrance fee is negative [" + insuranceFee + "].");
		}
		
		this.insuranceFee = insuranceFee;
	}
	
	/**
	 * Get affiliation fee
	 * @return Affiliation fee
	 */
	public double getAffiliationFee() {
		return this.affiliationFee;
	}
	
	/**
	 * Set new affiliation fee value
	 * @param affiliationFee New value
	 * @throws InvalidNumberException If new value is invalid
	 */
	public void setAffiliationFee(double affiliationFee) throws InvalidNumberException {
		if (affiliationFee < 0.0) {
			throw new InvalidNumberException("Sport.setAffiliationFee: affiliation fee is negative [" + affiliationFee + "].");
		}
		
		this.affiliationFee = affiliationFee;
	}
	
	/**
	 * Get court list of this sport
	 * @return ArrayList of Court
	 */
	public ArrayList<Court> getCourtList() {
		return this.courtList;
	}
	
	/**
	 * Add new court
	 * @param court Court to be added
	 * @throws NullPointerException If court is null
	 * @throws DuplicateException If another court with same id already exists.
	 */
	public void addCourt(Court court) throws NullPointerException, DuplicateException {
		if (court == null) {
			throw new NullPointerException("Sport.addCourt: court is null.");
		}
		
		if (searchCourtByNumber(court.getNumber()) != null) {
			throw new DuplicateException("Sport.addCourt: court with same number [" + court.getNumber() + "] already exist for sport [" + this.name  + "].");
		}
		
		this.courtList.add(court);
	}
	
	/**
	 * Search court by number 
	 * @param number Number of court to search
	 * @return Court found or null it not
	 */
	public Court searchCourtByNumber(int number) {
		for (Court court: courtList) {
			if (court.getNumber() == number) {
				return court;
			}
		}
		
		return null;
	}
	
	/**
	 * toString method
	 * @return String
	 */
	public String toString() {
		String result = this.getSportName() + "," + this.getUsageFee() + "," + this.getInsuranceFee() + "," + this.getAffiliationFee();
		
		for (Court court: this.getCourtList()) {
			result += "," + court.getNumber();
 		}
		
		return result;
	}
	
	/**
	 * Save data to write to file
	 * @return String saved data
	 */
	public String saveData() {
		String result = this.getSportName() + "," + this.getUsageFee() + "," + this.getInsuranceFee() + "," + this.getAffiliationFee();
		
		for (Court court: this.getCourtList()) {
			result += "," + court.getNumber();
 		}
		
		return result;
	}

	/**
	 * Get court list as string
	 * @return String
	 */
	public String getCourtListAsString() {
		String listString = "";
		Iterator<Court> iterator = this.courtList.iterator();
		
		if (iterator.hasNext()) {
			listString = "" + iterator.next().getNumber();
		}
		
		while (iterator.hasNext()) {
			listString += ", " + iterator.next().getNumber();
		}
		
		return listString;
	}

	/**
	 * Static method construct Sport from a saved string data
	 * @param line Saved string data
	 * @param club Referenced Club object
	 * @throws Exception If something goes wrong
	 * @return Sport or null if failed
	 */
	public static Sport constructFromString(Club club, String line) throws Exception {
		String[] stringList = line.split("\\s*,\\s*");
		Sport sport = null;
		
		try {
			String sportName = stringList[0];
			// search for existing sport name
			sport = club.searchSportByName(sportName, true);
			if (sport != null) {
				throw new DuplicateException("Sport name '" + sportName + "' already exists.");
			}

			double usage = Double.parseDouble(stringList[1]);
			double insurance = Double.parseDouble(stringList[2]);
			double affiliation = Double.parseDouble(stringList[3]);
			
			if (sportName.equalsIgnoreCase("Squash")) {
				sport = new Squash(usage, insurance, affiliation);
			} else if (sportName.equalsIgnoreCase("Tennis")) { 				
				sport = new Tennis(usage, insurance, affiliation);
			} else if (sportName.equalsIgnoreCase("Badminton")) {				
				sport = new Badminton(usage, insurance, affiliation);
			} else {				
				throw new ParseLineException("Unknown sport name '" + sportName + "'");
			}
			
			// read the court list
			for (int index = 4; index < stringList.length; index++) {
				int courtNumber = Integer.parseInt(stringList[index]);
				// check for unity of the court in the whole system
				if (club.searchCourtByNumber(courtNumber) == null) {
					Court court = new Court(courtNumber);
					sport.addCourt(court);
				}
			}
		} catch (IndexOutOfBoundsException ioobe) {
			throw new ParseLineException("Wrong format, must be name,usage,insurance,affiliation,court1,court2...");
		} catch (NumberFormatException nfe) {
			throw new ParseLineException("Unable to parse number " + nfe.getMessage());
		}
		
		return sport;
	}
}