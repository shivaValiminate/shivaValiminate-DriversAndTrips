import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import static java.util.stream.Collectors.toMap;


public class DriverAndTrips {

	private final static Logger LOGGER = Logger.getLogger(DriverAndTrips.class.getName());
	private HashMap<String,Double> DRIVER_MILES = new HashMap<String,Double>();
	private HashMap<String,LinkedList<Double>> DRIVER_SPEEDS = new HashMap<String,LinkedList<Double>>();
	private HashMap<String,ArrayList<String>> erroredEntrys = new HashMap<String,ArrayList<String>>();
	private ArrayList<String> discardedEntrys = new ArrayList<String>();
	private final static String NEW_LINE = "\n";
	public final static String DELIMITER = " - ";
	private static int MIN_SPEED;
	private static int MAX_SPEED;
	private static String OURPUT_FOLDER;
	

	public static void main(String[] args) {
		try {
			FileReader reader=new FileReader(DriverAndTripsUtil.PROP_FILE);
			Properties p=new Properties();
			p.load(reader); 
			MIN_SPEED = Integer.parseInt(p.getProperty("MinSpeed"));
			MAX_SPEED = Integer.parseInt(p.getProperty("MaxSpeed"));
			OURPUT_FOLDER = p.getProperty("OutputFolder");
			new DriverAndTrips().processFile(p.getProperty("InputFile")); 
		}catch(IOException e){  
			e.printStackTrace();  
		}catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	  /**
	   * Processes the input file by traversing through each line of the file
	   * @param fileName file name of the file that needs to be processed
	   */
	private void processFile(String fileName){  
		LOGGER.log(Level.INFO, "Started processing the input file");
		try{ 
			BufferedReader br =new BufferedReader(new FileReader(fileName));
			String entry;
			while((entry = br.readLine()) != null){ 
				processEachEntry(entry); 
			}
			br.close();  
			processOutput();
			processErrorOutput();
		}catch (Exception e) {
			e.printStackTrace(); 
		}
		LOGGER.log(Level.INFO, "Completed processing the input file");
	}	
	
	  /**
	   * Processes the each record and stores the driver and trip records 
	   * @param entry each line from the file
	   */
	private void processEachEntry(String entry) { 
		try {
			if(entry.startsWith(DriverAndTripsUtil.DRIVER)) {
				processDriverEntry(entry);
			}else if(entry.startsWith(DriverAndTripsUtil.TRIP)) {
				processTripEntry(entry);
			}else {
				String errorMsg = DELIMITER+DriverAndTripsUtil.INV_START;
				processErroredRecords(DriverAndTripsUtil.INV_REC, entry+errorMsg);
			}
		}catch(Exception e) {
			e.printStackTrace(); 
		}
	}

	  /**
	   * Processes each driver record from the file and store them
	   * @param dEntry each line from the file
	   */
	private void processDriverEntry(String dEntry) {
		String[] splitDriver = dEntry.split(" ");
		int splitDriverLength = splitDriver.length;
		if(splitDriverLength == 2 ) { 
			addDriver(splitDriver[1]);
		}else {
			String errorMsg = DELIMITER+ (splitDriverLength > 2 ? DriverAndTripsUtil.DN_SPACES : DriverAndTripsUtil.EMPTY_DRIVER);
			processErroredRecords(DriverAndTripsUtil.INV_DRIVER, dEntry+errorMsg);
		}
	}
	
	  /**
	   * Processes each trip record from the file and store them
	   * @param tEntry each line from the file
	   */
	private void processTripEntry(String tEntry) {
		try {
			String[] splitTrip = tEntry.split(" ");
			int splitTripLength = splitTrip.length;
			if(splitTripLength == 5 ) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				Date startTime = sdf.parse(splitTrip[2]);
				Date endTime = sdf.parse(splitTrip[3]);
				if(startTime.before(endTime)) {
					double miles = Double.parseDouble(splitTrip[4]);
					double speed = new DriverAndTripsUtil().getSpeed(startTime, endTime, miles);
					if(speed > MIN_SPEED && speed < MAX_SPEED) { 
						addMilesAndSpeeds(splitTrip[1], miles, speed);
					}else {
						String errorMsg = DELIMITER+(speed > MAX_SPEED ? DriverAndTripsUtil.INV_MAXSPEED : DriverAndTripsUtil.INV_MINSPEED);
						discardedEntrys.add(tEntry+errorMsg);
						LOGGER.log(Level.WARNING, "Discared : "+tEntry+errorMsg);
					}
				}else {
					String errorMsg = DELIMITER+(startTime.equals(endTime) ? DriverAndTripsUtil.EQ_TIME : DriverAndTripsUtil.END_TIME);
					processErroredRecords(DriverAndTripsUtil.INV_TRIP,tEntry+errorMsg);
				}
			}else {
				String errorMsg = DELIMITER+(splitTripLength == 1 ? DriverAndTripsUtil.EMPTY_TRIP : DriverAndTripsUtil.Err_TRIP);
				processErroredRecords(DriverAndTripsUtil.INV_TRIP,tEntry+errorMsg);
			}
		}catch (Exception e) {
			e.printStackTrace(); 
		}

	}

	  /**
	   * Processes error records and stores them with the error reason
	   * @param errorKey what kind of error is made on the entry
	   * @param entry on which the error is made
	   */
	private void processErroredRecords(String errorKey,String entry) {
		if(erroredEntrys.containsKey(errorKey)) {
			erroredEntrys.get(errorKey).add(entry);
		}else {
			erroredEntrys.put(errorKey, new ArrayList<String>(Arrays.asList(entry)) );
		}
		LOGGER.log(Level.WARNING, "Error Entry : "+entry );
	}
	
	  /**
	   * stores the driver details from the entry on the file
	   * @param driverName Name of the driver
	   */
	private void addDriver(String driverName) {
		if(!DRIVER_MILES.containsKey(driverName)) {
			DRIVER_MILES.put(driverName, 0.0);
			DRIVER_SPEEDS.put(driverName, new LinkedList<Double>(Arrays.asList(0.0,0.0)));
		}		
	}

	  /**
	   * stores the miles and speed of the driver
	   * @param driverName Name of the driver
	   * @param miles distance covered in the trip
	   * @param speed of the trip
	   */
	private void addMilesAndSpeeds(String driverName,double miles,double speed) {
		if(DRIVER_MILES.containsKey(driverName)) {
			DRIVER_MILES.replace(driverName, DRIVER_MILES.get(driverName)+miles);
			LinkedList<Double> driverspeeds = DRIVER_SPEEDS.get(driverName);
			driverspeeds.set(0, driverspeeds.get(0)+speed);
			driverspeeds.set(1, driverspeeds.get(1)+1);
			DRIVER_SPEEDS.replace(driverName, driverspeeds);
		}else {
			String errorMsg = DELIMITER+DriverAndTripsUtil.NO_DRIVER;
			processErroredRecords(DriverAndTripsUtil.INV_TRIP, driverName+errorMsg);
		}		
	}
	
	  /**
	   * To display the drivers sorted in descending order based on 
	   * the total miles covered and also display the average speed
	   */	
	private void processOutput() {
		HashMap<String,Double> sortedDrivers = DRIVER_MILES.entrySet().stream() 
											   .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
											   .collect( toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
		StringBuilder sb = new StringBuilder();
		sortedDrivers.forEach((driver,miles) -> {
			LinkedList<Double> speedInfo = DRIVER_SPEEDS.get(driver);
			int avgSpeed = (int) Math.round(speedInfo.get(0) /speedInfo.get(1));
			String result = driver + ": " + Math.round(miles) + " miles";
			if(avgSpeed > 0) {
				result = result+" @ "+avgSpeed+" mph";
			}
			sb.append(NEW_LINE+result);
		});
		String output = sb.toString();
		LOGGER.log(Level.INFO, output);
		new DriverAndTripsUtil().writeToFile(OURPUT_FOLDER+"OutputFile.txt", output.trim());
}
	
	  /**
	   * Displays the error records
	   */
	private void processErrorOutput() {
		StringBuilder sb = new StringBuilder();
		if(discardedEntrys.size() > 0) {
		sb.append(NEW_LINE+NEW_LINE+DriverAndTripsUtil.DIS_ENTRY);
		discardedEntrys.forEach(val -> sb.append(NEW_LINE+val)); 
		}
		if(erroredEntrys.size() > 0) {
			sb.append(NEW_LINE+DriverAndTripsUtil.ERROR_ENTRIES); 
		erroredEntrys.forEach( (reason,records) -> {
			sb.append(NEW_LINE+reason);
			records.forEach( rec -> sb.append(NEW_LINE+"> "+rec));
		} );
		}
		String errorOutput = sb.toString().trim();
		LOGGER.log(Level.WARNING, errorOutput);
		new DriverAndTripsUtil().writeToFile(OURPUT_FOLDER+"ErrorFile.txt", errorOutput.trim());
	}
}
