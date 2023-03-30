import java.net.*;
import java.io.*;

public class MyClient {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 500000;

        // Create a socket to make the connection to the server
        Socket socket = new Socket(hostName, portNumber);
        System.out.println("Connected to server: " + socket.getInetAddress().getHostName());

        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // HELO from my week 2 workshop
        String message = "HELO\n";
        byte[] bytes = message.getBytes();
        out.write(bytes);
        System.out.println("Sent message to server: " + message);

        // Recieve OK
        String response = reader.readLine();
        System.out.println("Received message from server: " + response);

        // Send AUTH username - code from the week 4 workshop
        String username = System.getProperty("user.name");
        String authMessage = "AUTH " + username + "\n";
        byte[] authBytes = authMessage.getBytes();
        out.write(authBytes);
        System.out.println("Sent authentication message to server: " + authMessage);

        // Recieve OK
        response = reader.readLine();
        System.out.println("Received message from server: " + response);

        // While the last message from ds-server is not NONE do
        int s = 0;
        while (!response.contains("NONE")) {
            // Send REDY
            message = "REDY\n";
            bytes = message.getBytes();
            out.write(bytes);
            System.out.println("Sent message to server: " + message);

            // Receive a message JOBN, JCPL and NONE
            response = reader.readLine();
            System.out.println("Received message from server: " + response);

            // identify largest server type
            // SCHD
            String[] jobData = response.split("\\s+");
            String jobID = jobData[2];
            int estRuntime = Integer.parseInt(jobData[3]);
            int coreCount = Integer.parseInt(jobData[4]);
            int memory = Integer.parseInt(jobData[5]);
            int disk = Integer.parseInt(jobData[6]);
            int c = 0;
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

        // Send QUIT
        message = "QUIT\n";
        bytes = message.getBytes();
        out.write(bytes);
        System.out.println("Sent message to server: " + message);

        // Recieve QUIT
        response = reader.readLine();
        System.out.println("Received message from server: " + response);

        // Close the socket
        socket.close();

    }

}
