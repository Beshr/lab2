import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Configuration {

	private String serverAddrs;
	private int serverPort;
	private int numberOfReaders, numberOfWriters;
	String[] readersAdresses, writersAddresses;
	private int numOfAccesses;

	public Configuration(String fileName) {

		File file = new File(fileName);
		if (file.exists()) {
			// reading the file
			Scanner sc = null;
			try {
				sc = new Scanner(file);
				byte count = 0;
				while (sc.hasNextLine()) {
					String line = sc.nextLine().trim();
					String[] data = line.split("=");
					if (count == 0) {// server address
						serverAddrs = data[1].trim();
					} else if (count == 1) {
						serverPort = Integer.parseInt(data[1].trim());
					} else {
						// get number of readers
						numberOfReaders = Integer.parseInt(data[1].trim());
						readersAdresses = new String[numberOfReaders];
						// get addresses of all readers
						fill(numberOfReaders, readersAdresses, sc);
						// get number of writers
						numberOfWriters = Integer.parseInt(sc.nextLine().trim()
								.split("=")[1].trim());
						writersAddresses = new String[numberOfWriters];
						fill(numberOfWriters, writersAddresses, sc);

						// read number of accesses
						numOfAccesses = Integer.parseInt(sc.nextLine().trim()
								.split("=")[1].trim());
						break;

					}
					count++;
				}
				sc.close();

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Error in reading file");
			}
		} else
			System.err.println("File Not Found");
	}

	private void fill(int count, String[] array, Scanner sc) {
		for (int i = 0; i < count; i++)
			array[i] = sc.nextLine().trim().split("=")[1].trim();
	}

	public String getServerAddrs() {
		return serverAddrs;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getNumberOfReaders() {
		return numberOfReaders;
	}

	public int getNumberOfWriters() {
		return numberOfWriters;
	}

	public String[] getReadersAdresses() {
		return readersAdresses;
	}

	public String[] getWritersAddresses() {
		return writersAddresses;
	}

	public int getNumOfAccesses() {
		return numOfAccesses;
	}

	public static void main(String[] args) {
		Configuration configuration = new Configuration("config.txt");
		System.out.println(configuration.getServerAddrs());
		System.out.println(configuration.getServerPort());
		System.out.println(configuration.getNumberOfReaders());
		System.out.println(Arrays.toString(configuration.getReadersAdresses()));
		System.out.println(configuration.getNumberOfWriters());
		System.out
				.println(Arrays.toString(configuration.getWritersAddresses()));
		System.out.println(configuration.getNumOfAccesses());
	}
}
