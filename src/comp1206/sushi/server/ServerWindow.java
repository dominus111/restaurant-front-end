package comp1206.sushi.server;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import comp1206.sushi.common.*;
import comp1206.sushi.server.ServerInterface.UnableToDeleteException;

/**
 * Provides the Sushi Server user interface
 *
 */
public class ServerWindow extends JFrame implements UpdateListener {

	private static final long serialVersionUID = -4661566573959270000L;
	private ServerInterface server;
	Icon drone = null;
	Icon order = null;
	Icon dish = null;
	Icon ingredient = null;
	Icon staff = null;
	Icon postcode = null;
	Icon map = null;
	Icon user = null;
	Icon supplier = null;

	private AuxiliaryPanel ordersPanel;
	private AuxiliaryPanel staffPanel;
	private AuxiliaryPanel dishesPanel;
	private AuxiliaryPanel dronesPanel;
	private AuxiliaryPanel ingredientsPanel;
	private AuxiliaryPanel usersPanel;
	private AuxiliaryPanel postcodesPanel;
	private AuxiliaryPanel suppliersPanel;
	private MapPanel mapPanel;
	private StockHandler stockHandler;

	private PostcodesTable postcodesTable;
	private SuppliersTable suppliersTable;
	private IngredientsTable ingredientsTable;
	private DishesTable dishesTable;
	private DronesTable dronesTable;
	private OrdersTable ordersTable;
	private StaffTable staffTable;
	private UsersTable usersTable;

	String[] allTheTabs = { "Postcodes", "Orders", "Dishes", "Ingredients", "Suppliers", "Staff", "Drones", "Users" };

	/**
	 * Create a new server window
	 * 
	 * @param server instance of server to interact with
	 */
	public ServerWindow(ServerInterface server) {
		super("Sushi Server");
		this.server = server;
		this.setTitle(server.getRestaurantName() + " Server");
		server.addUpdateListener(this);

		getIcons();

		JTabbedPane sushi = new JTabbedPane(JTabbedPane.LEFT);

		sushi.setBackground(Color.RED);
		Color red = new Color(102, 0, 0);
		getContentPane().setBackground(red);

		ordersPanel = new AuxiliaryPanel();
		dishesPanel = new AuxiliaryPanel();
		ingredientsPanel = new AuxiliaryPanel();
		suppliersPanel = new AuxiliaryPanel();
		staffPanel = new AuxiliaryPanel();
		dronesPanel = new AuxiliaryPanel();
		usersPanel = new AuxiliaryPanel();
		postcodesPanel = new AuxiliaryPanel();
		mapPanel = new MapPanel();
		stockHandler = new StockHandler(server);

		sushi.addTab("Postcodes", postcode, postcodesPanel);
		sushi.addTab("Orders", order, ordersPanel);
		sushi.addTab("Dishes", dish, dishesPanel);
		sushi.addTab("Ingredients", ingredient, ingredientsPanel);
		sushi.addTab("Suppliers", supplier, suppliersPanel);
		sushi.addTab("Staff", staff, staffPanel);
		sushi.addTab("Drones", drone, dronesPanel);
		sushi.addTab("Users", user, usersPanel);
		sushi.addTab("Map", map, mapPanel);

		add(sushi);

		setSpecifications();
		setAllActions();
		setAllTables();

		// Display window
		setSize(800, 630);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		// Start timed updates
		startTimer();
	}

	/**
	 * Start the timer which updates the user interface based on the given interval
	 * to update all panels
	 */
	public void startTimer() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		int timeInterval = 5;

		scheduler.scheduleAtFixedRate(() -> refreshAll(), 0, timeInterval, TimeUnit.SECONDS);
	}

	/**
	 * Loads all the pictures for the tabs
	 */
	private void getIcons() {
		try {
			BufferedImage myPicture = ImageIO.read(new File("dronePic.png"));
			drone = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image1");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("orderPic.png"));
			order = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image2");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("dishPic.png"));
			dish = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image3");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("ingredientPic.png"));
			ingredient = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image4");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("staffPic.png"));
			staff = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image5");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("postcodePic.png"));
			postcode = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image6");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("mapPic.png"));
			map = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image7");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("userPic.png"));
			user = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image8");
		}
		try {
			BufferedImage myPicture = ImageIO.read(new File("supplierPic.png"));
			supplier = (new ImageIcon(myPicture));
		} catch (IOException e) {
			JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image9");
		}
	}

	/**
	 * Creates all the tables and the tables themselves populate from the server
	 */
	private void setAllTables() {
		postcodesTable = new PostcodesTable();
		postcodesPanel.table.setViewportView(postcodesTable);

		suppliersTable = new SuppliersTable();
		suppliersPanel.table.setViewportView(suppliersTable);

		ingredientsTable = new IngredientsTable();
		ingredientsPanel.table.setViewportView(ingredientsTable);

		dishesTable = new DishesTable();
		dishesPanel.table.setViewportView(dishesTable);

		dronesTable = new DronesTable();
		dronesPanel.table.setViewportView(dronesTable);

		ordersTable = new OrdersTable();
		ordersPanel.table.setViewportView(ordersTable);

		staffTable = new StaffTable();
		staffPanel.table.setViewportView(staffTable);

		usersTable = new UsersTable();
		usersPanel.table.setViewportView(usersTable);
	}

	/**
	 * Gives all the specification each panel will have and what type the editing
	 * for it would be
	 */
	private void setSpecifications() {
		postcodesPanel.addSpecification("Name", "");

		suppliersPanel.addSpecification("Name", "");
		suppliersPanel.addSpecification("Postcode", server.getPostcodes());

		ingredientsPanel.addSpecification("Name", "");
		ingredientsPanel.addSpecification("Unit", "");
		ingredientsPanel.addSpecification("Supplier", server.getSuppliers());
		ingredientsPanel.addSpecification("RestockThreshold", "Number");
		ingredientsPanel.addSpecification("RestockAmount", "Number");

		dishesPanel.addSpecification("Name", "");
		dishesPanel.addSpecification("Description", "");
		dishesPanel.addSpecification("Price", "Number");
		dishesPanel.addSpecification("RestockThreshold", "Number");
		dishesPanel.addSpecification("RestockAmount", "Number");

		dronesPanel.addSpecification("Speed", "Number");

		staffPanel.addSpecification("Name", "");

	}

	/**
	 * Takes care of all the actions
	 */
	private void setAllActions() {
		ordersPanel.add.setText("Completed/Remove");
		ordersPanel.add.addActionListener(e -> {
			Order selectedOrder = (Order) ordersPanel.getSelected(ordersTable);
			try {
				if (selectedOrder == null)
					throw new UnableToDeleteException("Nothing is selected");
				if (server.getOrderStatus(selectedOrder).equals("Complete")) {
					server.removeOrder(selectedOrder);
					ordersTable.populate();
				} else {
					throw new UnableToDeleteException("The order is not complete");
				}
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(staffPanel, "Unable to remove postcode: " + exception.getMessage());
			}
		});

		ordersPanel.edit.setEnabled(false);
		ordersPanel.delete.setEnabled(false);

		/////////////////////////

		staffPanel.add.addActionListener(e -> {
			try {
				String name = (String) staffPanel.get("Name");
				server.addStaff(name);
				staffTable.populate();
				staffPanel.reset();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(staffPanel, "ERROR: " + exception.getMessage());
			}
		});

		staffPanel.edit.setEnabled(false);
		staffPanel.delete.addActionListener(e -> {
			Staff selectedStaff = (Staff) staffPanel.getSelected(staffTable);
			try {
				if (selectedStaff == null)
					throw new UnableToDeleteException("Nothing is selected");
				server.removeStaff(selectedStaff);
				staffTable.populate();
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(staffPanel, "Unable to remove postcode: " + exception.getMessage());
			}
		});

		////////////////////////////

		dronesPanel.add.addActionListener(e -> {
			try {
				Number speed = (Number) dronesPanel.get("Speed");
				server.addDrone(speed);
				dronesTable.populate();
				dronesPanel.reset();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(dronesPanel, "ERROR: " + exception.getMessage());
			}
		});

		dronesPanel.edit.setEnabled(false);
		dronesPanel.delete.addActionListener(e -> {
			Drone selectedDrone = (Drone) dronesPanel.getSelected(dronesTable);
			try {
				if (selectedDrone == null)
					throw new UnableToDeleteException("Nothing is selected");
				server.removeDrone(selectedDrone);
				dronesTable.populate();
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(dronesPanel, "Unable to remove postcode: " + exception.getMessage());
			}
		});

		////////////////////////////

		usersPanel.add.setEnabled(false);
		usersPanel.edit.setEnabled(false);
		usersPanel.delete.setEnabled(false);
		usersPanel.delete.addActionListener(e -> {
			User selectedUser = (User) usersPanel.getSelected(usersTable);
			try {
				server.removeUser(selectedUser);
				usersTable.populate();
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(usersPanel, "Unable to remove user: " + exception.getMessage());
			}
		});

		/////////////////////////////

		postcodesPanel.add.addActionListener(e -> {
			try {
				String name = (String) postcodesPanel.get("Name");
				for (Postcode postcode : server.getPostcodes()) {
					if (postcode.getName().equals(name))
						throw new Exception("Postcode already exists");
				}
				try {
					server.addPostcode(name);
				} catch (Exception exception) {
					JOptionPane.showMessageDialog(postcodesPanel, "ERROR: Invalit Postcode format");
				}
				suppliersPanel.editor.addModel(server.getPostcodes().get(server.getPostcodes().size() - 1));
				postcodesTable.populate();
				postcodesPanel.reset();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(postcodesPanel, "ERROR: " + exception.getMessage());
			}
		});

		postcodesPanel.edit.setEnabled(false);
		postcodesPanel.delete.addActionListener(e -> {
			Postcode selectedPostcode = (Postcode) postcodesPanel.getSelected(postcodesTable);
			try {
				if (selectedPostcode == null)
					throw new UnableToDeleteException("Nothing is selected");
				for (Supplier s : server.getSuppliers()) {
					if (s.getPostcode() == selectedPostcode) {
						throw new UnableToDeleteException("Postcode is being used");
					}
				}
				server.removePostcode(selectedPostcode);
				suppliersPanel.editor.removeModel(selectedPostcode);
				postcodesTable.populate();
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(postcodesPanel, "Unable to remove postcode: " + exception.getMessage());
			}
		});

		//////////////////////////////

		dishesPanel.add.addActionListener(e -> {
			try {
				String name = (String) dishesPanel.get("Name");
				String unit = (String) dishesPanel.get("Description");
				Number supplier = (Number) dishesPanel.get("Price");
				Number restockThreshold = (Number) dishesPanel.get("RestockThreshold");
				Number restockAmount = (Number) dishesPanel.get("RestockAmount");
				Dish dish = server.addDish(name, unit, supplier, restockThreshold, restockAmount);
				dishesTable.populate();
				dishesPanel.reset();
				editDish(dish);
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(dishesPanel, "ERROR: " + exception.getMessage());
			}
		});

		dishesPanel.edit.addActionListener(e -> {
			Dish selectedDish = (Dish) dishesPanel.getSelected(dishesTable);
			try {
				editDish(selectedDish);
				dishesTable.populate();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(dishesPanel, "Unable to edit dish: " + exception.getMessage());
			}
		});
		dishesPanel.delete.addActionListener(e -> {
			Dish selectedDish = (Dish) dishesPanel.getSelected(dishesTable);
			try {
				if (selectedDish == null)
					throw new UnableToDeleteException("Nothing is selected");
				server.removeDish(selectedDish);
				dishesTable.populate();
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(dishesPanel, "Unable to remove ingredient: " + exception.getMessage());
			}
		});

		/////////////////////////////

		ingredientsPanel.add.addActionListener(e -> {
			try {
				String name = (String) ingredientsPanel.get("Name");
				String unit = (String) ingredientsPanel.get("Unit");
				Supplier supplier = (Supplier) ingredientsPanel.get("Supplier");
				Number restockThreshold = (Number) ingredientsPanel.get("RestockThreshold");
				Number restockAmount = (Number) ingredientsPanel.get("RestockAmount");
				server.addIngredient(name, unit, supplier, restockThreshold, restockAmount);
				ingredientsTable.populate();
				ingredientsPanel.reset();

			} catch (Exception exception) {
				JOptionPane.showMessageDialog(ingredientsPanel, "ERROR:1 " + exception.getMessage());
				exception.printStackTrace();
			}
		});

		ingredientsPanel.edit.addActionListener(e -> {
			Ingredient selectedIngredient = (Ingredient) ingredientsPanel.getSelected(ingredientsTable);
			try {
				editIngredient(selectedIngredient);
				ingredientsTable.populate();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(ingredientsPanel, "Unable to edit ingredient: " + exception.getMessage());
			}
		});
		ingredientsPanel.delete.addActionListener(e -> {
			Ingredient selectedIngredient = (Ingredient) ingredientsPanel.getSelected(ingredientsTable);
			try {
				if (selectedIngredient == null)
					throw new UnableToDeleteException("Nothing is selected");
				for (Dish d : server.getDishes()) {
					for (Entry<Ingredient, Number> recipe : d.getRecipe().entrySet()) {
						if (recipe.getKey() == selectedIngredient)
							throw new UnableToDeleteException("Ingredient is being used");
					}
				}
				server.removeIngredient(selectedIngredient);
				ingredientsTable.populate();
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(ingredientsPanel,
						"Unable to remove ingredient: " + exception.getMessage());
			}
		});

		////////////////////////////////

		suppliersPanel.add.addActionListener(e -> {
			try {
				String name = (String) suppliersPanel.get("Name");
				Postcode postcode = (Postcode) suppliersPanel.get("Postcode");
				server.addSupplier(name, postcode);
				ingredientsPanel.editor.addModel(server.getSuppliers().get(server.getSuppliers().size() - 1));
				suppliersTable.populate();
				suppliersPanel.reset();
			} catch (Exception exception) {
				JOptionPane.showMessageDialog(postcodesPanel, "ERROR: " + exception.getMessage());
			}
		});
		suppliersPanel.edit.setEnabled(false);
		suppliersPanel.delete.addActionListener(e -> {
			Supplier selectedSupplier = (Supplier) suppliersPanel.getSelected(suppliersTable);
			try {
				if (selectedSupplier == null)
					throw new UnableToDeleteException("Nothing is selected");
				for (Ingredient ingredient : server.getIngredients()) {
					if (ingredient.getSupplier() == selectedSupplier) {
						throw new UnableToDeleteException("Supplier is being used");
					}
				}
				server.removeSupplier(selectedSupplier);
				ingredientsPanel.editor.removeModel(selectedSupplier);
				suppliersTable.populate();
			} catch (UnableToDeleteException exception) {
				JOptionPane.showMessageDialog(suppliersPanel, "Unable to remove supplier: " + exception.getMessage());
			}
		});
	}

	/**
	 * Creates a new JFrame where a dish can be edited
	 * 
	 * @param selectedDish
	 * @throws UnableToDeleteException
	 */

	private void editDish(Dish selectedDish) throws UnableToDeleteException {
		if (selectedDish == null)
			throw new UnableToDeleteException("Nothing is selected");
		DishFrame dishFrame = new DishFrame(selectedDish);
		dishFrame.setVisible(true);

	}

	/**
	 * Creates a new JFrame where a ingredients can be edited
	 * 
	 * @param selectedIngredient
	 * @throws UnableToDeleteException
	 */
	private void editIngredient(Ingredient selectedIngredient) throws UnableToDeleteException {
		if (selectedIngredient == null)
			throw new UnableToDeleteException("Nothing is selected");
		IngredientsFrame ingredientFrame = new IngredientsFrame(selectedIngredient);
		ingredientFrame.setVisible(true);
	}

	/**
	 * Main JPanel for all the other panels Consists of the JScrollPane for the
	 * table, the specification bit and the tree main buttons
	 * 
	 * @author Martin Kanev
	 *
	 */
	public class AuxiliaryPanel extends JPanel {
		JScrollPane table;
		EditorPanel editor;
		JButton add;
		JButton edit;
		JButton delete;
		Map<String, JComponent> editorMap = new HashMap<String, JComponent>();

		public AuxiliaryPanel() {
			setLayout(new BorderLayout());
			JPanel tabelAndEditor = new JPanel();
			tabelAndEditor.setLayout(new GridLayout(2, 1));

			table = new JScrollPane();
			editor = new EditorPanel();

			tabelAndEditor.add(table);
			tabelAndEditor.add(editor);
			add(tabelAndEditor, BorderLayout.CENTER);

			JPanel options = new JPanel();
			options.setLayout(new GridLayout(1, 3));
			add = new JButton("Add");
			edit = new JButton("Edit");
			delete = new JButton("Delete");

			options.add(delete);
			options.add(edit);
			options.add(add);
			add(options, BorderLayout.SOUTH);

		}

		/**
		 * Calls the addSpecification method of the correct editor Panel and the
		 * editorMap will be used when adding new Models
		 * 
		 * @param string
		 * @param component
		 */
		public void addSpecification(String string, String component) {
			JComponent field = editor.addSpecification(string, component);
			editorMap.put(string, field);
		}

		/**
		 * Similar to the upper method
		 * 
		 * @param string
		 * @param list
		 */
		public void addSpecification(String string, List<? extends Model> list) {
			JComponent field = editor.addSpecification(string, list);
			editorMap.put(string, field);
		}

		/**
		 * Upon editing or deleting it gets the selected row and returns the correct
		 * index from the correct table
		 * 
		 * @param table
		 * @return
		 */
		public Object getSelected(JTable table) {
			int index = table.getSelectedRow();
			if (index < 0)
				return null;
			if (table instanceof PostcodesTable) {
				return ((PostcodesTable) table).list.get(index);
			} else if (table instanceof SuppliersTable) {
				return ((SuppliersTable) table).list.get(index);
			} else if (table instanceof IngredientsTable) {
				return ((IngredientsTable) table).list.get(index);
			} else if (table instanceof DishesTable) {
				return ((DishesTable) table).list.get(index);
			} else if (table instanceof DronesTable) {
				return ((DronesTable) table).list.get(index);
			} else if (table instanceof StaffTable) {
				return ((StaffTable) table).list.get(index);
			} else if (table instanceof OrdersTable) {
				return ((OrdersTable) table).list.get(index);
			} else if (table instanceof UsersTable) {
				return ((UsersTable) table).list.get(index);
			} else
				return null;

		}

		/**
		 * After adding a Model, it resets the adding fields to being empty or on the
		 * first item of a list
		 */
		public void reset() {
			for (JComponent component : editorMap.values()) {
				if (component instanceof JTextField) {
					((JTextField) component).setText("");
				} else if (component instanceof JSpinner) {
					((JSpinner) component).setValue(0);
				}
			}
		}

		/**
		 * When adding a Model, it gets the information from the correct specification
		 * 
		 * @param property
		 * @return
		 * @throws Exception
		 */
		public Object get(String property) throws Exception {
			JComponent component = editorMap.get(property);
			if (component instanceof JTextField) {
				String someSpecification = ((JTextField) component).getText();
				if (someSpecification.isEmpty()) {
					throw new Exception("A text field is empty");
				} else {
					return someSpecification;
				}
			} else if (component instanceof JSpinner) {
				Integer someSpecification = (Integer) ((JSpinner) component).getValue();
				if (someSpecification == 0) {
					throw new Exception("Some value is not set");
				} else {
					return someSpecification;
				}
			} else if (component instanceof JComboBox) {
				if (((JComboBox) component).getItemCount() == 0) {
					throw new Exception("Empty dropdown list");
				} else {
					return ((JComboBox) component).getSelectedItem();
				}
			}
			return null;
		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * TABLES
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	/**
	 * Below are all the tables, all of them have a method populate() that keeps
	 * data on tables up to date
	 * 
	 * @author Martin Kanev
	 *
	 */

	public class OrdersTable extends JTable {
		DefaultTableModel model;
		List<Order> list;

		public OrdersTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			list = server.getOrders();
			String[] columns = { "Name", "Cost", "Status", "Distance" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (Order x : list) {

				data[row][0] = x.getName();
				data[row][1] = String.valueOf(server.getOrderCost(x));
				data[row][2] = server.getOrderStatus(x);
				data[row][3] = String.valueOf(server.getOrderDistance(x));
				row++;
			}
			model = new DefaultTableModel(data, columns);
			setModel(model);
		}
	}

	public class StaffTable extends JTable {
		DefaultTableModel model;
		List<Staff> list;
		boolean special = false;

		public StaffTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			list = server.getStaff();
			String[] columns = { "Name", "Status" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (Staff x : list) {
				data[row][0] = x.getName();
				data[row][1] = server.getStaffStatus(x);
				row++;
			}
			model = new DefaultTableModel(data, columns);
			setModel(model);

		}
	}

	public class UsersTable extends JTable {
		DefaultTableModel model;
		List<User> list;

		public UsersTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			list = server.getUsers();
			String[] columns = { "Name", "Address", "Distance" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (User x : list) {
				data[row][0] = x.getName();
				data[row][1] = String.valueOf(x.getPostcode());
				data[row][2] = String.valueOf(x.getDistance());
				row++;
			}
			model = new DefaultTableModel(data, columns);
			setModel(model);
		}
	}

	public class DronesTable extends JTable {
		DefaultTableModel model;
		List<Drone> list;

		public DronesTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			list = server.getDrones();
			String[] columns = { "Name", "Status" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (Drone x : list) {
				mapPanel.addNewDrone(x);
				data[row][0] = x.getName();
				data[row][1] = x.getStatus();
				row++;
			}
			model = new DefaultTableModel(data, columns);
			setModel(model);
		}
	}

	public class IngredientsTable extends JTable {
		DefaultTableModel model;
		List<Ingredient> list;

		public IngredientsTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			list = server.getIngredients();
			String[] columns = { "Name", "Unit", "Supplier", "Stock", "Restock Threshold", "Restock Amount" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (Ingredient x : list) {
				mapPanel.fetchIngredient(x);
				data[row][0] = x.getName();
				data[row][1] = x.getUnit();
				data[row][2] = x.getSupplier().getName();
				data[row][3] = String.valueOf(x.getStock());
				data[row][4] = String.valueOf(x.getRestockThreshold());
				data[row][5] = String.valueOf(x.getRestockAmount());
				row++;
			}

			model = new DefaultTableModel(data, columns);
			setModel(model);
		}
	}

	public class DishesTable extends JTable {
		DefaultTableModel model;
		List<Dish> list;

		public DishesTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			list = server.getDishes();
			String[] columns = { "Name", "Description", "Price", "Restock Threshold", "Restock Amount" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (Dish x : list) {
				data[row][0] = x.getName();
				data[row][1] = x.getDescription();
				data[row][2] = String.valueOf(x.getPrice());
				data[row][3] = String.valueOf(x.getRestockThreshold());
				data[row][4] = String.valueOf(x.getRestockAmount());
				row++;
			}
			model = new DefaultTableModel(data, columns);
			setModel(model);
		}
	}

	public class SuppliersTable extends JTable {
		DefaultTableModel model;
		List<Supplier> list;

		public SuppliersTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			mapPanel.createList();
			list = server.getSuppliers();
			String[] columns = { "Name", "PostCodes" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (Supplier x : list) {
				mapPanel.getPosition(x.getPostcode());
				x.getPostcode().calculateDistanceForAnyPostcode(server.getRestaurantPostcode());
				data[row][0] = x.getName();
				data[row][1] = x.getPostcode().getName();
				row++;
			}
			model = new DefaultTableModel(data, columns);
			setModel(model);
		}
	}

	public class PostcodesTable extends JTable {
		DefaultTableModel model;
		List<Postcode> list;

		public PostcodesTable() {
			populate();
			setShowGrid(false);
			getTableHeader().setOpaque(false);
			getTableHeader().setBackground(Color.ORANGE);
		}

		public void populate() {
			list = server.getPostcodes();
			String[] columns = { "Name", "Distance (meters)", "Lat", "Lon" };
			String[][] data = new String[list.size()][columns.length];
			int row = 0;
			for (Postcode x : list) {
				x.calculateDistanceForAnyPostcode(server.getRestaurantPostcode());
				data[row][0] = x.getName();
				data[row][1] = String.valueOf(x.getDistance());
				data[row][2] = String.valueOf(x.getLatLong().get("lat"));
				data[row][3] = String.valueOf(x.getLatLong().get("lon"));
				row++;
			}
			model = new DefaultTableModel(data, columns);
			setModel(model);
		}
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * END TABLES
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * A JPanel where specifications can be given and deals with updates between
	 * different editor Panels
	 * 
	 * @author Martin Kanev
	 *
	 */
	public class EditorPanel extends JPanel {
		JComboBox<Model> dropList;

		public EditorPanel() {
			setLayout(new GridLayout(10, 2));
		}

		/**
		 * Takes care of specifications with text fields and with JSpinners
		 * 
		 * @param name
		 * @param component
		 * @return
		 */
		public JComponent addSpecification(String name, String component) {
			JLabel label = new JLabel(name);
			JComponent element;
			if (component.equals("Number")) {
				SpinnerNumberModel limits = new SpinnerNumberModel(0, 0, 10000, 1);
				element = new JSpinner(limits);
			} else {
				element = new JTextField(20);
			}
			addField(label, element);
			return element;
		}

		/**
		 * Takes care of specifications with a list
		 * 
		 * @param name
		 * @param list
		 * @return
		 */
		public JComponent addSpecification(String name, List<? extends Model> list) {
			JLabel label = new JLabel(name);
			dropList = new JComboBox<>();
			for (Model model : list) {
				dropList.addItem(model);
			}
			addField(label, dropList);
			return dropList;
		}

		/**
		 * Puts a JLable with its corresponding component in a parallel group
		 * 
		 * @param name
		 * @param field
		 */
		public void addField(JLabel name, JComponent component) {
			this.add(name);
			this.add(component);
		}

		public void removeModel(Model model) {
			this.dropList.removeItem(model);
		}

		public void addModel(Model model) {
			this.dropList.addItem(model);
		}
	}

	/**
	 * A frame managing when a dish is edited
	 * 
	 * @author Martin Kanev
	 *
	 */
	public class DishFrame extends JFrame {
		private Dish dish;
		private HashMap<Ingredient, JSpinner> mapIngredient = new HashMap<Ingredient, JSpinner>();

		/**
		 * If something is edited it provides the option to save the changes or cancel
		 * them and keep the old ones
		 * 
		 * @param dish
		 */
		public DishFrame(Dish dish) {
			super("Editing Dish Recipe: " + dish.getName());
			this.dish = dish;

			setLayout(new BorderLayout());
			setSize(220, 400);
			this.setLocationRelativeTo(null);

			Map<Ingredient, Number> recipe = server.getRecipe(dish);
			DishEditor ingredientList = new DishEditor(recipe);
			JScrollPane ingredientsJScroll = new JScrollPane(ingredientList);
			add(ingredientsJScroll, BorderLayout.CENTER);

			JPanel buttons = new JPanel(new GridLayout(1, 2));
			JButton save = new JButton("Save");
			JButton cancel = new JButton("Cancel");

			buttons.add(cancel);
			buttons.add(save);

			save.addActionListener(e -> {
				server.setRecipe(dish, getRecipe());
				server.setRestockLevels(dish, ingredientList.getRestockThreshold(), ingredientList.getRestockAmount());
			});

			cancel.addActionListener(e -> {
				this.dispose();
			});
			add(buttons, BorderLayout.SOUTH);

		}

		/**
		 * Return a recepie map with all the ingredients and right numbers for the
		 * corresponding dish
		 * 
		 * @return
		 */
		public Map<Ingredient, Number> getRecipe() {
			HashMap<Ingredient, Number> recipe = new HashMap<Ingredient, Number>();
			for (Entry<Ingredient, JSpinner> editedInfo : mapIngredient.entrySet()) {
				Ingredient ingredient = editedInfo.getKey();
				Integer value = (Integer) editedInfo.getValue().getValue();
				if (value > 0) {
					try {
						if (value <= ingredient.getStock().intValue()) {
							recipe.put(ingredient, value);
							ingredient.setStock(ingredient.getStock().intValue() - value);
							if (value >= ingredient.getRestockThreshold().intValue()) {
								 mapPanel.fetchIngredient(ingredient);
							}
						} else {
							throw new Exception("You don't have enough of " + ingredient.getName());

						}
					} catch (Exception exception) {
						JOptionPane.showMessageDialog(this, "Unable to add: " + exception.getMessage());
					}
				}
			}
			return recipe;
		}

		/**
		 * It shows all the ingredients and the correct numbers Also a restock threshold
		 * and amount values
		 * 
		 * @author Martin Kanev
		 *
		 */
		public class DishEditor extends JPanel {
			private JSpinner restockThreshold;
			private JSpinner restockAmount;

			public DishEditor(Map<Ingredient, Number> recipe) {
				this.setLayout(new FlowLayout());
				setPreferredSize(new Dimension(180, 400));

				// Shows the ingredients
				List<Ingredient> ingredients = server.getIngredients();
				for (Ingredient ingredient : ingredients) {
					JLabel ingredientName = new JLabel(ingredient.getName());
					SpinnerNumberModel limits = new SpinnerNumberModel(0, 0, 10000, 1);
					JSpinner ingredientQuantity = new JSpinner();
					ingredientQuantity.setModel(limits);

					if (recipe.containsKey(ingredient)) {
						ingredientQuantity.setValue(recipe.get(ingredient));
						ingredientName.setForeground(Color.GREEN);
					}

					addField(ingredientName, ingredientQuantity);
					mapIngredient.put(ingredient, ingredientQuantity);
				}

				setLayout(new FlowLayout());

				// Shows the restck threshold and amount
				JLabel restockThresholdLabel = new JLabel("Stock Threshold:");
				SpinnerNumberModel limits = new SpinnerNumberModel(0, 0, 10000, 1);
				restockThreshold = new JSpinner();
				restockThreshold.setModel(limits);
				JLabel restockAmountLabel = new JLabel("Stock Amount:");
				restockAmount = new JSpinner();
				restockThreshold.setModel(limits);

				// Presents the original values for threshold and amount
				Number restockThresholdValue = server.getRestockThreshold(dish);
				Number restockAmountValue = server.getRestockAmount(dish);
				restockThreshold.setValue(restockThresholdValue);
				restockAmount.setValue(restockAmountValue);

				addField(restockThresholdLabel, restockThreshold);
				addField(restockAmountLabel, restockAmount);
			}

			public Number getRestockThreshold() {
				return (Number) restockThreshold.getValue();
			}

			public Number getRestockAmount() {
				return (Number) restockAmount.getValue();
			}

			public void addField(JLabel label, JComponent component) {
				this.add(label);
				this.add(component);

			}

		}
	}

	/**
	 * A frame where ingredients will be edited
	 * 
	 * @author Martin Kanev
	 *
	 */
	public class IngredientsFrame extends JFrame {
		private Ingredient ingredient;

		/**
		 * Displaying restock threshold and amount value Also the chance to edit, save
		 * or cancel
		 * 
		 * @param ingredient
		 */
		public IngredientsFrame(Ingredient ingredient) {
			super("Edit ingredient: " + ingredient.getName());
			this.ingredient = ingredient;
			setLayout(new BorderLayout());
			setSize(400, 200);
			setLocationRelativeTo(null);

			RestockEditor restorckEditor = new RestockEditor();
			JScrollPane ingredientJScroll = new JScrollPane(restorckEditor);
			add(ingredientJScroll, BorderLayout.CENTER);

			JPanel buttons = new JPanel(new GridLayout(1, 2));
			JButton save = new JButton("Save");
			JButton cancel = new JButton("Cancel");
			buttons.add(cancel);
			buttons.add(save);

			save.addActionListener(e -> {
				server.setRestockLevels(ingredient, restorckEditor.getRestockThreshold(),
						restorckEditor.getRestockAmount());
				this.dispose();
			});
			cancel.addActionListener(e -> {
				this.dispose();
			});
			add(buttons, BorderLayout.SOUTH);

		}

		/**
		 * A panel where the restock Threshold and amount will be displayed also with
		 * spinners for a change
		 * 
		 * @author Martin Kanev
		 *
		 */
		public class RestockEditor extends JPanel {
			private JSpinner restockThreshold;
			private JSpinner restockAmount;

			public RestockEditor() {

				setLayout(new FlowLayout());

				// Show restock threshold and amount
				JLabel restockThresholdLabel = new JLabel("Stock Threshold:");
				SpinnerNumberModel limits = new SpinnerNumberModel(0, 0, 10000, 1);
				restockThreshold = new JSpinner();
				restockThreshold.setModel(limits);
				JLabel restockAmountLabel = new JLabel("Stock Amount:");
				restockAmount = new JSpinner();
				restockThreshold.setModel(limits);

				// Set the original values of restock threshold and amount
				Number restockThresholdValue = server.getRestockThreshold(ingredient);
				Number restockAmountValue = server.getRestockAmount(ingredient);
				restockThreshold.setValue(restockThresholdValue);
				restockAmount.setValue(restockAmountValue);

				addField(restockThresholdLabel, restockThreshold);
				addField(restockAmountLabel, restockAmount);
			}

			public Number getRestockThreshold() {
				return (Number) restockThreshold.getValue();
			}

			public Number getRestockAmount() {
				return (Number) restockAmount.getValue();
			}

			public void addField(JLabel label, JComponent component) {
				this.add(label);
				this.add(component);
			}

		}

	}

	/**
	 * Provide a Map showing the restaurant and the suppliers also a drone can make
	 * the restocking visual
	 * 
	 * @author Martin Kanev
	 *
	 */
	public class MapPanel extends JPanel {
		int radius = 10;
		Image uniMap;
		Image dronePic;
		Image sushiPic;
		Image foodPic;
		// Lat and long of the picture
		double minLat = 50.945851;
		double minLon = -1.432133;
		double maxLat = 50.922878;
		double maxLon = -1.364349;
		double ry;
		double rx;
		double droneX;
		double droneY;
		double x2;
		double y2;
		boolean food;
		boolean dronep;

		Map<Postcode, String> list;

		/**
		 * Loads the image of the map drone and food and locates the restaurant
		 */
		public MapPanel() {
			getPosition(server.getRestaurantPostcode());
			dronep = true;
			try {
				BufferedImage myPicture = ImageIO.read(new File("uni_map.png"));
				uniMap = (new ImageIcon(myPicture)).getImage();
			} catch (IOException e) {
				JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image1");
			}
			try {
				BufferedImage myPicture = ImageIO.read(new File("drone.png"));
				dronePic = (new ImageIcon(myPicture)).getImage();
			} catch (IOException e) {
				JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image2");
			}
			try {
				BufferedImage myPicture = ImageIO.read(new File("food.jpg"));
				foodPic = (new ImageIcon(myPicture)).getImage();
			} catch (IOException e) {
				JOptionPane.showMessageDialog((JFrame) SwingUtilities.getRoot(this), "Unable to load an image4");
			}
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.drawImage(uniMap, 0, 0, mapPanel);
			drawCircle(g);
			drawRestaurant(g);
			drawADrone(g);
			if (food) {
				drawIngredient(g);
			}
		}

		/**
		 * Checks if an ingredients needs restocking, if so then it restocks it and it
		 * shows it on the map, using a separate thread
		 * 
		 * @param i
		 */
		public void fetchIngredient(Ingredient i) {
			if (stockHandler.checkIngredientsForRestock(i)) {
				dronep = false;
			}
			if (!dronep) {
				String[] split2 = getPosition(i.getSupplier().getPostcode()).split(",");
				x2 = ((Double.parseDouble(split2[0])));
				y2 = ((Double.parseDouble(split2[1])));
				// gradient
				double m = (y2 - ry) / (x2 - rx);
				// intersection
				double b = ry - (m * rx);
				// related to how often it will repaint
				double steps = 1.0;
				this.droneX = rx;
				// start a separate thread that will call the repainting
				Thread thread = new Thread() {
					public void run() {
						while (stockHandler.getDrone() == null) {
						}
						long time = stockHandler.getDrone().getDestination().getDistance().longValue()
								/ stockHandler.getDrone().getSpeed().longValue();
						long parts = (long) ((Math.sqrt(Math.pow((rx - x2), 2) + Math.pow((ry - y2), 2))) / steps);

						dronep = true;
						if (rx > x2) {
							for (int i = (int) rx; i > x2; i -= steps) {
								droneY = ((m * droneX) + b);
								droneX = i;
								repaint();

								try {
									Thread.sleep((time * 5) / parts);
								} catch (InterruptedException e) {
									System.out.println("Problem with sleeping");
									e.printStackTrace();
								}
							}
							droneY = y2;
							droneX = x2;
							repaint();
							food = true;
							for (int i = (int) droneX; i < rx; i += steps) {
								droneY = ((m * droneX) + b);
								droneX = i;
								repaint();
								try {
									Thread.sleep(time * 5 / parts);
								} catch (InterruptedException e) {
									System.out.println("Problem with sleeping");
									e.printStackTrace();
								}
							}
							droneY = ry;
							droneX = rx;
							food = false;
							repaint();
						} else {
							for (int i = (int) rx; i < x2; i += steps) {
								droneY = ((m * droneX) + b);
								droneX = i;
								repaint();

								try {
									Thread.sleep((time * 5) / parts);
								} catch (InterruptedException e) {
									System.out.println("Problem with sleeping");
									e.printStackTrace();
								}
							}
							droneY = y2;
							droneX = x2;
							repaint();
							food = true;
							for (int i = (int) droneX; i > rx; i -= steps) {
								droneY = ((m * droneX) + b);
								droneX = i;
								repaint();
								try {
									Thread.sleep(time * 5 / parts);
								} catch (InterruptedException e) {
									System.out.println("Problem with sleeping");
									e.printStackTrace();
								}
							}
							droneY = ry;
							droneX = rx;
							food = false;
							repaint();
						}
					}
				};
				thread.start();

			}
		}

		public void drawIngredient(Graphics g) {
			g.drawImage(foodPic, (int) (droneX + radius) + 2, (int) droneY + 5, mapPanel);
		}

		public void addNewDrone(Drone d) {
			d.setSource(server.getRestaurantPostcode());

		}

		public void drawADrone(Graphics g) {
			g.drawImage(dronePic, (int) droneX + radius, (int) droneY, this);
		}

		public void createList() {
			list = new HashMap<>();
		}

		/**
		 * Using lat and long and the height and width of teh picture locate a point on
		 * the picture and return a string of xAndY
		 * 
		 * @param postcode
		 * @return
		 */
		public String getPosition(Postcode postcode) {
			Double latitude = postcode.getLatLong().get("lat");
			Double longitude = postcode.getLatLong().get("lon");

			int MAP_WIDTH = 1519;
			int MAP_HEIGHT = 799;

			double scalelog = (MAP_WIDTH) / (maxLon - minLon);
			double scalelat = (MAP_HEIGHT) / (maxLat - minLat);

			double x = (longitude - minLon) * (scalelog);
			double y = (latitude - minLat) * (scalelat);

			if (postcode == server.getRestaurantPostcode()) {
				this.rx = x;
				this.ry = y;
			} else {
				list.put(postcode, x + "," + y);
			}
			String xAndY = x + "," + y;
			return xAndY;
		}

		/**
		 * Drawing the suppliers on the map
		 * 
		 * @param g
		 */
		public void drawCircle(Graphics g) {
			int posX = 0;
			int posY = 0;
			for (Entry<Postcode, String> map : list.entrySet()) {
				for (Supplier s : server.getSuppliers()) {
					if (s.getPostcode() == map.getKey()) {
						String[] split2 = map.getValue().split(",");
						posX = (int) (Math.round(Double.parseDouble(split2[0]))) - radius;
						posY = (int) (Math.round(Double.parseDouble(split2[1]))) - radius;
						g.setColor(Color.BLUE);
						g.fillOval(posX, posY, radius * 2, radius * 2);
						g.drawString(s.getName(), posX - 5, posY - 3);
						break;
					}
				}
			}
		}

		public void drawRestaurant(Graphics g) {
			g.setColor(Color.RED);
			g.fillOval((int) (Math.round(rx) - radius), (int) (Math.round(ry) - radius), radius * 2, radius * 2);
		}
	}

	/**
	 * Refresh all parts of the server application based on receiving new data,
	 * calling the server afresh
	 */
	public void refreshAll() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ingredientsTable.populate();
			}
		});
		dronesTable.populate();
		staffTable.populate();
		ordersTable.populate();
	}

	@Override
	/**
	 * Respond to the model being updated by refreshing all data displays
	 */
	public void updated(UpdateEvent updateEvent) {
		refreshAll();
	}

}
