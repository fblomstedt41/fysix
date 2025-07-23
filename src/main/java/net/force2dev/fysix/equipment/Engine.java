
package net.force2dev.fysix.equipment;

public class Engine
{	
	private double engineForce     = 0.0;
	private double engineMass      = 0.0;
	private double fuelConsumption = 0.0;
	private double fuelAmount      = 0.0;
	private double fuelUnitMass    = 0.0;	
	private double mountAngle      = 0.0;
	private double mountDistance   = 0.0;
	private double totalMass       = 0.0;
	
	public Engine(double force, double mass, // Engine
			      double consumption, double amount, double unitWgt, // Fuel
				  double angle, double distance) // Mount data
	{
		// Engine
		this.engineForce = force;
		this.engineMass  = mass;
	
		// Fuel
		this.fuelConsumption = consumption;
		this.fuelAmount = amount;
		this.fuelUnitMass = unitWgt;		
		
		// Mount
		this.mountAngle = angle;
		this.mountDistance = distance;
		
		// Calculated weight
		this.totalMass = this.engineMass + this.fuelAmount*this.fuelUnitMass; 
	}
	
	/**
	 * @return Returns the engineForce.
	 */
	public double getEngineForce() {
		return engineForce;
	}
	/**
	 * @param engineForce The engineForce to set.
	 */
	public void setEngineForce(double engineForce) {
		this.engineForce = engineForce;
	}
	/**
	 * @return Returns the fuelAmount.
	 */
	public double getFuelAmount() {
		return fuelAmount;
	}
	/**
	 * @param fuelAmount The fuelAmount to set.
	 */
	public void setFuelAmount(double fuelAmount) {
		this.fuelAmount = fuelAmount;
	}
	/**
	 * @return Returns the fuelConsumption.
	 */
	public double getFuelConsumption() {
		return fuelConsumption;
	}
	/**
	 * @param fuelConsumption The fuelConsumption to set.
	 */
	public void setFuelConsumption(double fuelConsumption) {
		this.fuelConsumption = fuelConsumption;
	}
	/**
	 * @return Returns the fuelUnitMass.
	 */
	public double getFuelUnitMass() {
		return fuelUnitMass;
	}
	/**
	 * @param fuelUnitMass The fuelUnitMass to set.
	 */
	public void setFuelUnitMass(double fuelUnitMass) {
		this.fuelUnitMass = fuelUnitMass;
	}
	/**
	 * @return Returns the mountAngle.
	 */
	public double getMountAngle() {
		return mountAngle;
	}
	/**
	 * @param mountAngle The mountAngle to set.
	 */
	public void setMountAngle(double mountAngle) {
		this.mountAngle = mountAngle;
	}
	/**
	 * @return Returns the mountDistance.
	 */
	public double getMountDistance() {
		return mountDistance;
	}
	/**
	 * @param mountDistance The mountDistance to set.
	 */
	public void setMountDistance(double mountDistance) {
		this.mountDistance = mountDistance;
	}
	/**
	 * @return Returns the totalMass.
	 */
	public double getTotalMass() {
		return totalMass;
	}
	/**
	 * @param totalMass The totalMass to set.
	 */
	public void setTotalMass(double totalMass) {
		this.totalMass = totalMass;
	}
}
