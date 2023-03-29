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
            while (!response.equals("NONE")) {
                // REDY
                message = "REDY\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // JOBN
                response = reader.readLine();
                System.out.println("Received message from server: " + response);

                // SCHD
                String[] jobData = response.split("\\s+");
                String jobID = jobData[2];
                int estRuntime = Integer.parseInt(jobData[3]);
                int coreCount = Integer.parseInt(jobData[4]);
                int memory = Integer.parseInt(jobData[5]);
                int disk = Integer.parseInt(jobData[6]);

                String server = getLargestServer(coreCount, reader);
                String schdMessage = "SCHD " + jobID + " " + server + "\n";
                byte[] schdBytes = schdMessage.getBytes();
                out.write(schdBytes);
                System.out.println("Sent scheduling message to server: " + schdMessage);

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
                message = "GETS All\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // Receive DATA nRecs recSize // e.g., DATA 5 124
                response = reader.readLine();
                System.out.println("Received message from server: " + response);

                // Receive nRecs records
                for (int i = 0; i < 5; i++) {
                    response = reader.readLine();
                    System.out.println("Received message from server: " + response);
                    // Keep track of the largest server type and the number of servers of that type
                    // available
                }

                // Send OK
                message = "OK\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);

                // Receive

                // If the message received at Step 10 is JOBN then
                if (response.equals("JOBN")) {
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

    private static String getLargestServer(int coreCount, BufferedReader reader) {
        return null;
    }

}
