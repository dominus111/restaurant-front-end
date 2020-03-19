package comp1206.sushi.common;

import javax.swing.Timer;

import comp1206.sushi.common.Drone;

public class Drone extends Model {

	private Number speed;
	private Number progress;

	private Number capacity;
	private Number battery;

	private String status;

	private Postcode source;
	private Postcode destination;
	Ingredient ingredient;
	boolean shouldRun;
	Thread x = null;

	public Drone(Number speed) {
		this.setSpeed(speed);
		this.setCapacity(1);
		setStatus("Idle");
		this.setBattery(100);
	}

	public Number getSpeed() {
		return speed;
	}

	public void fetch(Ingredient ingredient) {
		shouldRun = true;
		deliver(ingredient);
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void deliver(Ingredient ingredient) {
		if (shouldRun) {
			if (ingredient != null && getStatus().equals("Idle")) {
				setDestination(ingredient.getSupplier().getPostcode());
				setStatus("Restocking ingredient " + ingredient.getName());
				long time = 2 * getDestination().getDistance().longValue() / getSpeed().longValue();
				try {
					Thread.sleep(time * 5);
				} catch (InterruptedException e) {
					System.out.println("Problem with sleeping");
					e.printStackTrace();
				}
				setStatus("Idle");
				ingredient.setStock(ingredient.getStock().intValue() + ingredient.getRestockAmount().intValue());
				setIngredient();
				shouldRun = false;
			}
		}
	}

	private void setIngredient() {
		ingredient = null;

	}

	public Number getProgress() {
		return progress;
	}

	public void setProgress(Number progress) {
		this.progress = progress;
	}

	public void setSpeed(Number speed) {
		this.speed = speed;
	}

	@Override
	public String getName() {
		return "Drone (" + getSpeed() + " speed)";
	}

	public Postcode getSource() {
		return source;
	}

	public void setSource(Postcode source) {
		this.source = source;
	}

	public Postcode getDestination() {
		return destination;
	}

	public void setDestination(Postcode destination) {
		this.destination = destination;
	}

	public Number getCapacity() {
		return capacity;
	}

	public void setCapacity(Number capacity) {
		this.capacity = capacity;
	}

	public Number getBattery() {
		return battery;
	}

	public void setBattery(Number battery) {
		this.battery = battery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		notifyUpdate("status", this.status, status);
		this.status = status;
	}

}
