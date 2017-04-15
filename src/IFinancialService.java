
/**
 * 
 * 
 * @author Alex Truong (101265224) 
 * @version 0.1
 */

import CException.*;

public interface IFinancialService {
	/**
	 * Getu usage fee
	 * @return Double value
	 */
	public double getUsageFee();
	
	/**
	 * Set new usage fee
	 * @param usageFee usage fee
	 * @throws InvalidNumberException when usage fee value is invalid
	 */
	public void setUsageFee(double usageFee) throws InvalidNumberException;
	
	/**
	 * Get insurance fee
	 * @return double value
	 */
	public double getInsuranceFee();
	
	/**
	 * Set new insurance fee
	 * @param insuranceFee new insurance fee
	 * @throws InvalidNumberException if new value is invalid
	 */
	public void setInsuranceFee(double insuranceFee) throws InvalidNumberException;
	
	/**
	 * Get affiliation fee
	 * @return double value
	 */
	public double getAffiliationFee();
	
	/**
	 * Set new affiliation fee value
	 * @param affiliationFee new affiliation value
	 * @throws InvalidNumberException if new value is invalid
	 */
	public void setAffiliationFee(double affiliationFee) throws InvalidNumberException;
}
