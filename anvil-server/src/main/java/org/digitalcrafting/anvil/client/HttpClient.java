package org.digitalcrafting.anvil.client;

import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HttpClient {
    public static void main(String[] args) {
        System.out.println("Welcome to HTTP Client");
        try (Socket client = new Socket("127.0.0.1", 8080);
             OutputStream out = client.getOutputStream();
             BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))
        ) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String input = "";

            System.out.println("Web client started");
            System.out.println("Connected to server at 127.0.0.1:8080");
            System.out.println("What message do you want to send?");

            while (true) {
                System.out.print("> ");
                input = bufferedReader.readLine();
                HttpRequest request = new HttpRequest("POST", "/api/", out);
                request.body = input;

                request.send("close".equals(input));

                HttpResponse response = new HttpResponse(in);

                if (!response.parse()) {
                    System.out.println("Try again");
                    return;
                }

                System.out.println("Response from server: " + response.getBodyAsString());

                if ("close".equalsIgnoreCase(response.headers.get("Connection"))) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Something went wrong " + e);
        } finally {
            System.out.println("Goodbye!");
        }
    }
}
