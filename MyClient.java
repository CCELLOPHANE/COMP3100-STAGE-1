import java.net.*;
import java.io.*;

public class MyClient {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 500000;

        try (Socket socket = new Socket(hostName, portNumber)) {
            System.out.println("Connected to server: " + socket.getInetAddress().getHostName());

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            // HELO
            String message = "HELO\n";
            byte[] bytes = message.getBytes();
            out.write(bytes);
            System.out.println("Sent message to server: " + message);

            // OK
            String response = reader.readLine();
            System.out.println("Received message from server: " + response);

            // AUTH
            String username = System.getProperty("user.name");
            String authMessage = "AUTH " + username + "\n";
            byte[] authBytes = authMessage.getBytes();
            out.write(authBytes);
            System.out.println("Sent authentication message to server: " + authMessage);

            // OK
            response = reader.readLine();
            System.out.println("Received message from server: " + response);

            // While the last message from ds-server is not NONE do // jobs 1 - n
            int s = 0;
            while (!response.contains("NONE")) {
                // REDY
                message = "REDY\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // JOBN
                response = reader.readLine();
                System.out.println("Received message from server: " + response);

                // OK
                response = reader.readLine();
                System.out.println("Received message from server: " + response);

                // OK
                message = "OK\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // JCPL
                response = reader.readLine();
                System.out.println("Received message from server: " + response);

                // NONE
                response = reader.readLine();
                System.out.println("Received message from server: " + response);

                // Send a GETS message
                message = "GETS s \n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // Receive DATA nRecs recSize // e.g., DATA 5 124
                int nRecs = 0;
                if ((response.contains("DATA"))) {
                    String[] data = response.split("\\s+");
                    nRecs = Integer.parseInt(data[1]);
                }

                // Send OK
                message = "OK\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // For loop to store records
                for (int i = 0; i < nRecs; i++) {
                    // Receive a record
                    response = reader.readLine();
                    System.out.println("Received message from server: " + response);
                    // keep track of the number of records received
                    s++;
                }

                // Send OK
                message = "OK\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // Recieve
                response = reader.readLine();
                System.out.println("Received message from server: " + response);

                // If the message received at Step 10 is JOBN then schedule job
                if (response.contains("JOBN")) {
                    // SCHD
                    String[] jobData = response.split("\\s+");
                    String jobID = jobData[2];
                    int estRuntime = Integer.parseInt(jobData[3]);
                    int coreCount = Integer.parseInt(jobData[4]);
                    int memory = Integer.parseInt(jobData[5]);
                    int disk = Integer.parseInt(jobData[6]);

                    // Identify the largest server type; you may do this only once
                    int c = 0; // finding largest server and if its the same checking other factors
                    if (coreCount > c) {
                        c = coreCount;
                    }
                    if (memory > c) {
                        c = memory;
                    }
                    if (disk > c) {
                        c = disk;
                    }
                    if (estRuntime > c) {
                        c = estRuntime;
                    }

                    int server = c;
                    String schdMessage = "SCHD " + jobID + " " + server + "\n";
                    byte[] schdBytes = schdMessage.getBytes();
                    out.write(schdBytes);
                    System.out.println("Sent scheduling message to server: " + schdMessage);

                    message = "SCHD " + jobID + " " + server + "\n";
                    bytes = message.getBytes();
                    out.write(bytes);
                    System.out.println("Sent scheduling message to server: " + schdMessage);
                }
            }

            // Quit
            message = "QUIT\n";
            bytes = message.getBytes();
            out.write(bytes);
            System.out.println("Sent message to server: " + message);

            // None
            response = reader.readLine();
            System.out.println("Received message from server: " + response);
        }
    }

}
