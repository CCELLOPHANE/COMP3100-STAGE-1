import java.net.*;
import java.io.*;

public class MyClient {
    public static void main(String[] args) throws IOException {
        String hostName = "localhost";
        int portNumber = 50000;

        // Create a socket to make the connection to the server
        Socket socket = new Socket(hostName, portNumber);
        System.out.println("Connected to server: " + socket.getInetAddress().getHostName());

        OutputStream out = socket.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        // HELO
        String message = "HELO\n";
        byte[] bytes = message.getBytes();
        out.write(bytes);
        System.out.println("Sent message to server: " + message);
        out.flush();

        // Send AUTH username - code from the week 4 workshop
        String username = System.getProperty("user.name");
        String authMessage = "AUTH " + username + "\n";
        byte[] authBytes = authMessage.getBytes();
        out.write(authBytes);
        System.out.println("Sent authentication message to server: " + authMessage);

        // Recieve OK
        String response = reader.readLine();
        System.out.println("Received message from server: " + response);
        out.flush();

        String r = "";
        // Send REDY
        message = "REDY\n";
        bytes = message.getBytes();
        out.write(bytes);
        System.out.println("Sent message to server: " + message);

        // Receive a message JOBN, JCPL and NONE
        response = reader.readLine();
        r = response;

        int jobID = 0;
        if (response.contains("JOBN")) {
            String[] jobCom = response.split(" ");
            jobID = Integer.parseInt(jobCom[2]);
        }

        System.out.println("Received message from server: " + response);
        out.flush();

        // Send a GETS message
        message = "GETS All \n";
        bytes = message.getBytes();
        out.write(bytes);
        System.out.println("Sent message to server: " + message);

        response = reader.readLine();
        System.out.println("Received message from server: " + response);
        out.flush();

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
            // Keep track of the number of records received
            i++;
        }

        message = "OK\n";
        bytes = message.getBytes();
        out.write(bytes);
        System.out.println("Sent message to server: " + message);

        String serverType = "";
        int serverID = 0;
        int serverCore = 0;

        // Find the server with the most cores
        for (int i = 0; i < nRecs; i++) {
            response = reader.readLine();
            String[] responseType = response.split(" ");
            if (serverCore < Integer.parseInt(responseType[4])) {
                serverType = responseType[0];
                serverCore = Integer.parseInt(responseType[4]);
                serverID = Integer.parseInt(responseType[1]);
            }
            if (serverType.equals(responseType[0])) {
                serverID = Integer.parseInt(responseType[1]);
            }
            System.out.println("Received message from server: " + response);
        }

        // Send OK
        message = "OK\n";
        bytes = message.getBytes();
        out.write(bytes);
        System.out.println("Sent message to server: " + message);

        // Recieve
        response = reader.readLine();
        System.out.println("Received message from server: " + response);
        out.flush();
        int ID = 0;

        while (!responseS.contains("NONE")) {
            // If the response is jobn n keep scheluling untill we reach NONE
            if (responseS.contains("JOBN")) {
                if (ID > serverID) {
                    ID = 0;
                }
                message = "SCHD " + jobID + " " + serverType + " " + ID + "\n";
                bytes = message.getBytes();
                out.write(bytes);
                System.out.println("Sent message to server: " + message);
                response = reader.readLine();
                System.out.println("Received message from server: " + response);
                out.flush();
                ID++;

            }
            // If the response is JCPL n keep scheluling until we reach NONE
            message = "REDY\n";
            bytes = message.getBytes();
            out.write(bytes);
            System.out.println("Sent message to server: " + message);

            response = reader.readLine();
            r = response;

            // Saving job ID
            if (r.contains("JOBN")) {
                String[] jobCom = r.split(" ");
                jobID = Integer.parseInt(jobCom[2]);
            }

            System.out.println("Received message from server: " + response);
            out.flush();
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
        out.close();
        reader.close();
        socket.close();
    }
}
