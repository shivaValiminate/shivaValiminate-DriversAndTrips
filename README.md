# shivaValiminate-DriversAndTrips
SafeAuto Code Kata
Processed the input file and loaded the data into two hashmaps.
1) DRIVER_MILES: To store the driver's name as key and miles are driven by the driver as value.
2) DRIVER_SPEEDS: To store the driver's name as key and used a LinkedList to store Cummulative speed and a Counter to determine the number of trips.
After processing the data, derived the output
1) by sorting the DRIVER_MILES in descending order based on the value(Miles).
2)Then iterated through each DRIVER_MILES and retrieved respective driver details from the DRIVER_SPEEDS

From a maintenance standpoint, saved all the discarded and error records for future reference.

Artifacts :
1) DriverAndTrips: Main class in which we read the file, process the data, and render the output.
2) DriverAndTripsUtil: Utility class to define static constants and utility methods.
3) DriversAndTrips.Properties: To provide the required properties for processing the files.

Key points on Input :
Used a property file to provide the below details.
1) InputFile: Please provide the input file path here. (Please do not forget to add an extra '\'(the escape character) in the file path)
2) MinSpeed: Added this property to specify the minimum speed to consider a trip. Added this property, if in future there is a change in minimum speed requirement we just have to update the property.
3) MaxSpeed: Added this property to specify the maximum speed to consider a trip. Added this property, if in future there is a change in maximum speed requirement we just have to update the property.
4) OutputFolder: Please provide the output folder where we have to store the output files. (Please do not forget to add an extra '\'(the escape character) in the file path)

Key Points on Output :
As the requirement on where to display the output was not clear, implemented output in below two ways.
1) Displayed the expected output in the console, followed by discarded and error entries.
2) Wrote the output to two files, one is Output file which contains the expected output and the other is the Error file which contains the error entries.

Unit-Test scenarios :
1) Driver and Trip with the respective drivers.
2) Driver with no driver's name.
3) Driver with a driver's name with spaces.
4) Trip with a valid driver.
5) Trip with no details.
6) Trip with a driver who is not defined.
7) Trip with speed not in an acceptable range
8) Trip with end time before the start time.

Input provided:
Driver Dan
Driver Alex
Driver Bob
Driver
Driver Test Driver
Trip Dan 07:15 07:45 17.3
Trip Dan 06:12 06:32 21.8
Trip Dan 08:10 08:22 21.8
Trip Alex 12:01 13:16 42.0
Trip
Trip UNK 12:01 13:16 42.0
Trip Bob 12:25 12:16 42.0
Trp Alex 12:01 13:16 42.0

Output achieved:
Alex: 42 miles @ 34 mph
Dan: 39 miles @ 50 mph
Bob: 0 miles
Aug 12, 2020 3:31:02 AM DriverAndTrips processErrorOutput
WARNING: ## Discarded below trips that average a speed of less than 5 mph or greater than 100 mph
Trip Dan 08:10 08:22 21.8 - Speed is below 5 mph
** Below are the Error Entries
Invalid Driver
> Driver - Please enter a driver's name.
> Driver Test Driver - Driver Name cannot have spaces.
Invalid Entry
> Trp Alex 12:01 13:16 42.0 - Entry must start with either Driver or Trip.
Invalid Trip
> Trip - Please enter all the trip details.
> UNK - Driver does not exist.
> Trip Bob 12:25 12:16 42.0 - EndTime can not be earlier than StartTime.
