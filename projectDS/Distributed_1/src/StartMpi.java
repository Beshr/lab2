import mpi.*;

public class StartMpi {
	static Configuration config = new Configuration("config.txt");
	static String numOfaccesses = config.getNumOfAccesses() + "";

	public static void main(String[] args) {
		MPI.Init(args);

		if (MPI.COMM_WORLD.Rank() == 0) {

			// run the server

			Runnable server = new Server((short) config.getServerPort(),
					config.getNumOfAccesses(), config.getNumberOfReaders(),
					config.getNumberOfWriters(), config.getReadersAdresses(),
					config.getWritersAddresses());
			Thread t = new Thread(server);
			t.start();
		} else {
			if (MPI.COMM_WORLD.Rank() <= config.getNumberOfReaders())
				new Client(MPI.COMM_WORLD.Rank() + "", "read", numOfaccesses);
			else {

				new Client(MPI.COMM_WORLD.Rank() + "", "write", numOfaccesses);

			}

		}

	}

}
