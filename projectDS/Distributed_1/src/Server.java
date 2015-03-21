import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import mpi.*;


public class Server implements Runnable {

	private static int news/**/;
	private int numOfAccesses;
	private ArrayList<String> readersLog/**/, writersLog/**/;
	private int seqNo/**/, rseq;
	private int countThreads;
	private int totalThreads;
	private Thread[] threads;

	private Semaphore x, y, z, wSem, rSem;
	private static int readCount, writeCount;

	public Server(short portNo, int numOfAccesses, int rNum, int wNum,
			String[] readersAddrs, String[] writersAddrs) {
		this.numOfAccesses = numOfAccesses;

		readersLog = new ArrayList<String>(rNum * this.numOfAccesses);
		writersLog = new ArrayList<String>(wNum * this.numOfAccesses);

		news = -1;
		seqNo = 0;
		rseq = 0;
		countThreads = 0;
		totalThreads = (rNum + wNum) * numOfAccesses;
		threads = new Thread[totalThreads];
		x = new Semaphore(1);
		y = new Semaphore(1);
		z = new Semaphore(rNum * numOfAccesses);
		wSem = new Semaphore(1);
		rSem = new Semaphore(rNum * numOfAccesses);
		readCount = 0;
		writeCount = 0;
		

		

	}

	@Override
	public void run() {
		
			// ini
			int index = 0;
			while (countThreads < totalThreads) {// main loop
				Object[] message = new String[1];

          		Status stat = MPI.COMM_WORLD.Recv(message, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, MPI.ANY_TAG) ;

				rseq++;
				// here a client connected..process it in its thread
				processing clientThread = new processing(stat.source,(String)message[0]);
				Thread t = new Thread(clientThread);
				t.start();
				threads[index] = t;
				index++;
				countThreads++;
			}
			// check for alive threads
			for (Thread t : threads) {
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		
		// write log
		new LogWriter(readersLog.toArray(new String[readersLog.size()]),
				writersLog.toArray(new String[writersLog.size()]));
	}

	/*
	 * inner class for processing client request in its own thread
	 */
	class processing implements Runnable {
		
		int rank;
		String request;
		

		public processing(int rank , String request) {
		
				this.rank=rank;
				this.request=request;
				

			
		}

		@Override
		public void run() {
			// reading request from client
			
			rseq++;
			String response = "";
			String[] splitR = request.split(",");
			int id = Integer.parseInt(splitR[1]);
			String type = splitR[0];
			if (type.equalsIgnoreCase("read")) {
				response = reader(response, id);
				Object[] result = new String[1];
				result[0]=response;
				MPI.COMM_WORLD.Send(result, 0, 1, MPI.OBJECT,rank, MPI.ANY_TAG);

			} else if (type.equalsIgnoreCase("write")) {
				response = writer(response, id);
				Object[] result = new String[1];
				result[0]=response;

				MPI.COMM_WORLD.Send(result, 0, 1, MPI.OBJECT,rank, MPI.ANY_TAG);

				
			}
				
			

		}

		

	}

	private String reader(String response, int id) {
		try {
			z.acquire();
			rSem.acquire();
			x.acquire();
			readCount++;
			if (readCount == 1)
				wSem.acquire();
			x.release();
			rSem.release();
			z.release();
			/*
			 * processing here
			 */
			long random = (long) (Math.random() * 1000);
			while (random < 500)
				random = (long) (Math.random() * 1000);
			Thread.sleep(random);
			seqNo++;
			response = rseq + "\t" + seqNo + "\t" + news;
			readersLog.add("" + seqNo + "\t" + "" + news + "\t" + "" + id
					+ "\t" + readCount);
			String temp = response;
			x.acquire();
			readCount--;
			if (readCount == 0)
				wSem.release();
			x.release();
			return temp;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Exception in reader");
			return "";
		}

	}

	private String writer(String response, int id) {
		try {
			y.acquire();
			writeCount++;
			if (writeCount == 1)
				rSem.acquire();
			y.release();
			wSem.acquire();
			/*
			 * write processing here
			 */
			long random = (long) (Math.random() * 1000);
			while (random < 500)
				random = (long) (Math.random() * 1000);
			Thread.sleep(random);
			news = id;
			seqNo++;
			response = rseq + "\t" + seqNo;
			writersLog.add("" + seqNo + "\t" + "" + news + "\t" + "" + id);
			String temp = response;
			wSem.release();
			y.acquire();
			writeCount--;
			if (writeCount == 0)
				rSem.release();
			y.release();
			return temp;
		} catch (Exception e) {
			System.err.println("Error in write");
			return "";
		}
	}

}
