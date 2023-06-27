package org.digitalcrafting.testing.client;

import org.digitalcrafting.anvil.client.BasicHttpClient;
import org.digitalcrafting.anvil.common.HttpRequest;
import org.digitalcrafting.anvil.common.HttpResponse;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestingClientApplication {
    public static void main(String[] args) {
        System.out.println("Welcome to HTTP Client");

        try (
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        ) {
            BasicHttpClient client = new BasicHttpClient("127.0.0.1", 8080);
            String input = "";

            System.out.println("Web client started");
            System.out.println("Connected to server at 127.0.0.1:8080");
            System.out.println("What message do you want to send?");

            while (true) {
                System.out.print("> ");
                input = bufferedReader.readLine();
                HttpRequest request = new HttpRequest("POST", "/api/");
                request.body = input;

                HttpResponse response = client.exchange(request);

                System.out.println("Response from server: " + response.body);

                if ("close".equalsIgnoreCase(input)) {
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Buffered reader failed " + e);
        } finally {
            System.out.println("Goodbye!");
        }
    }
}
