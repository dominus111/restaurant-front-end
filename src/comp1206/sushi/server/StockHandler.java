package comp1206.sushi.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import comp1206.sushi.common.Drone;
import comp1206.sushi.common.Ingredient;

public class StockHandler {
	private ArrayList<Ingredient> array;
	private ServerInterface server;
	private Thread x;
	private Drone drone;
	private Runnable run;
	private int num;

	public StockHandler(ServerInterface server) {
		this.server = server;
		array = new ArrayList<>();
	}

	public boolean checkIngredientsForRestock(Ingredient ingredient){
		run = new Runnable() {
			public void run() {
				restock(ingredient);
			}
		};
		if (ingredient.getStock().intValue() <= ingredient.getRestockThreshold().intValue()
				&& !array.contains(ingredient)) {
			x = new Thread(run);
			x.start();
			array.add(ingredient);
			return true;
		} else {
			return false;
		}
	}

	public void restock(Ingredient ingredient2) {
		num = 0;
		for (Drone drone : server.getDrones()) {
			if (drone.getStatus().equals("Idle")) {
				this.drone = drone;
				drone.fetch(ingredient2);
				array.remove(ingredient2);
				break;
			} else {
				num++;
			}
		}
		// If all the drones were busy, 
		// makes sure it will deliver later
		if (num == 3) {
			array.remove(ingredient2);
		}
		drone = null;
	}
	public Drone getDrone() {
		return drone;
	}
}
