import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DriverAndTripsUtil {

	public final static String PROP_FILE = "src/properties/DriversAndTrips.Properties";
	public final static String DRIVER = "Driver";
	public final static String TRIP = "Trip";
	public final static String INV_REC = "Invalid Entry";
	public final static String INV_DRIVER = "Invalid Driver";
	public final static String INV_TRIP = "Invalid Trip";
	public final static String DN_SPACES = "Driver Name cannot have spaces.";
	public final static String EMPTY_DRIVER = "Please enter a driver's name.";
	public final static String NO_DRIVER= "Driver does not exist.";
	public final static String EMPTY_TRIP = "Please enter all the trip details.";
	public final static String Err_TRIP = "Invalid number of trip details submitted.";
	public final static String EQ_TIME = "StartTime and EndTime can not be equal.";
	public final static String END_TIME = "EndTime can not be earlier than StartTime.";
	public final static String INV_START = "Entry must start with either Driver or Trip.";
	public final static String DIS_ENTRY = "## Discarded below trips that average a speed of less than 5 mph or greater than 100 mph";
	public final static String ERROR_ENTRIES = "** Below are the Error Entries" ;
	public final static String INV_MINSPEED = "Speed is over 100 mph";
	public final static String INV_MAXSPEED = "Speed is below 5 mph";
	

	  /**
	   * Gets the speed for the specific trip entry.
	   * @param startTime start time of the trip
	   * @param endTime end time of the trip
	   * @param miles total distance covered for the trip
	   * @return speed of the trip.
	   */
	public double getSpeed(Date startTime,Date endTime,double miles ) {
		long timeDiff = endTime.getTime() - startTime.getTime();
		double timeDiffHours = ((double) TimeUnit.MILLISECONDS.toMinutes(timeDiff))/60;
		return miles/timeDiffHours;
	}
	
	  /**
	   * Writes the output to file
	   * @param fileName name of the file
	   * @param Content of the file
	   */
	public void writeToFile(String fileName,String value) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
			writer.write(value);
		}catch (Exception e) {
			e.printStackTrace(); 
		}
	}
}
