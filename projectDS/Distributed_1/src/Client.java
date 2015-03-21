import mpi.*;

public class Client {

	public Client(String ID, String type, String numOfaccess) {
		// initialization`
		Object[] message = new String[1];

		String log = "Client type: ";
		if (type.equals("write")) {
			message[0] = "write," + ID;
			log += "Writer\nClient Name: " + ID + "\nrSeq\tsSeq\n";
		} else {
			message[0] = "read," + ID;
			log += "Reader\nClient Name: " + ID + "\nrSeq\tsSeq\toVal\n";
		}
		try {
			long random = (long) (Math.random() * 500);
			while (random < 250)
				random = (long) (Math.random() * 500);
			for (int i = 0; i < Integer.parseInt(numOfaccess); i++) {
				// to
				MPI.COMM_WORLD.Send(message, 0, 1, MPI.OBJECT, 0, MPI.ANY_TAG);
				// connect
				// here connection established
				// in = new DataInputStream(socket.getInputStream());
				// out = new DataOutputStream(socket.getOutputStream());
				// // sending PImsg and get response then close..just for
				// // testing
				// out.writeUTF(message);
				// out.flush();
				String[] response = new String[1];
				Status stat = MPI.COMM_WORLD.Recv(response, 0, 1, MPI.OBJECT,
						0, MPI.ANY_TAG);
				log += response[0] + "\n";
				// closing
				// closingStreams(in, out);
				while (random < 250)
					random = (long) (Math.random() * 500);
				Thread.sleep(random);
			}
			new LogWriter(log, "log" + ID + ".txt");
		} catch (Exception e) {
			System.err.println("Error in initializeing socket");
			// closing it
			// closingStreams(in, out);
		}
	}

	// private void closingStreams(DataInputStream in, DataOutputStream out) {
	// try {
	// if (in != null)
	// in.close();
	// if (out != null)
	// out.close();
	// if (socket != null && !socket.isClosed())
	// socket.close();
	// } catch (Exception e) {
	// System.err.println("Error in closing streams");
	// }
	// }

}
