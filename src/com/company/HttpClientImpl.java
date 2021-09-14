package com.company;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClientImpl implements HttpClient {

    public static void main(String[] args) {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> params = new HashMap<>();

        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        params.put("userId", "1");
        params.put("description", "Hello");

        //System.out.println(new HttpClientImpl().get("https://postman-echo.com/get", headers, params));
        System.out.println(new HttpClientImpl().post("https://postman-echo.com/post", headers, params));

    }

    @Override
    public String get(String url, Map<String, String> headers, Map<String, String> params) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(methodForUrl(url, params)).openConnection();

            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            connection.setRequestMethod("GET");

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            System.out.println(connection.getResponseCode());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                    content.append("\n");
                }

                connection.disconnect();
                return content.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String methodForUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?");

        for (String key : params.keySet()) {
            urlBuilder.append(key).append("=").append(params.get(key)).append("&");
        }
        return urlBuilder.toString();
    }


    @Override
    public String post(String url, Map<String, String> headers, Map<String, String> params) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            connection.setDoOutput(true);


            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonMethod(url, params).getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }

            System.out.println(connection.getResponseCode());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input.trim());
                }
                System.out.println(content.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String jsonMethod(String url, Map<String, String> params) {
        StringBuilder jsonBuilder = new StringBuilder();

        jsonBuilder.append("{");

        for (String key : params.keySet()) {
            jsonBuilder.append(key).append(":").append(params.get(key)).append(", ");
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }
}