package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Processor of HTTP request.
 */
public class Processor {
    private final Socket socket;
    private final HttpRequest request;

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    static int fib(int n)
    {
        if (n <= 1)
            return n;
        return fib(n - 1) + fib(n - 2);
    }

    public void process() throws IOException {
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();

        PrintWriter output = new PrintWriter(socket.getOutputStream());

        // We are returning a simple web page now.
        output.println("HTTP/1.1 200 OK");
        output.println("Content-Type: text/html; charset=utf-8");
        output.println();
        output.println("<html>");
//        output.println("<head><title>Hello</title></head>");
//        output.println("<body><p>Hello, world!</p></body>");
        output.println("</html>");
        output.flush();
        functions(output);
        socket.close();
    }

    public void functions(PrintWriter output) throws IOException {
        StringBuilder stringBuilder = new StringBuilder(request.getRequestLine());
        stringBuilder.delete(0,4);
        stringBuilder.delete(stringBuilder.length() - 9, stringBuilder.length() + 1);
        System.out.println();
        System.out.println(stringBuilder);
        System.out.println();

        if(stringBuilder.toString().contains("/create/")) {
            stringBuilder.delete(0,8);
            File file = new File(stringBuilder.toString());
            try {
                boolean value = file.createNewFile();
                if (value) {
                    System.out.println("New Java File is created.");
                    output.println("<html>");
                    output.println("<body><p>" + stringBuilder + " is created.</p></body>");
                }
                else {
                    System.out.println("The file already exists.");
                    output.println("<html>");
                    output.println("<body><p>" + stringBuilder + " already exists.</p></body>");
                }
                output.println("</html>");
                output.flush();
            }
            catch(Exception e) {
                e.getStackTrace();
            }
        } else if (stringBuilder.toString().contains("/delete/")) {
            stringBuilder.delete(0,8);
            File file = new File(stringBuilder.toString());
            try {
                boolean value = file.delete();
                if (value) {
                    System.out.println("Java File is deleted.");
                    output.println("<html>");
                    output.println("<body><p>" + stringBuilder + " is deleted.</p></body>");
                }
                else {
                    System.out.println("The file doesn't exists.");
                    output.println("<html>");
                    output.println("<body><p>" + stringBuilder + " doesn't exists.</p></body>");
                }
                output.println("</html>");
                output.flush();
            }
            catch(Exception e) {
                e.getStackTrace();
            }
        } else if (stringBuilder.toString().contains("/write")) {
            stringBuilder.delete(0,7);
            String filename = stringBuilder.substring(0, stringBuilder.lastIndexOf(".txt/") + 4);
            System.out.println(stringBuilder.substring(0, stringBuilder.lastIndexOf(".txt/") + 4));
            try {
                FileWriter fWriter = new FileWriter(filename);
                stringBuilder.delete(0, stringBuilder.lastIndexOf(".txt/") + 5);
                fWriter.write(stringBuilder.toString());
                fWriter.close();
                System.out.println("You successfully wrote inside the java file.");
                output.println("<html>");
                output.println("<body><p> You successfully wrote inside " + filename + " -----> " + stringBuilder + ".</p></body>");
                output.println("</html>");
                output.flush();
            }
            catch (IOException e) {
                // Print the exception
                System.out.print(e.getMessage());
            }
        } else if (stringBuilder.toString().contains("/exec/")) {
            stringBuilder.delete(0,6);
            int n = Integer.parseInt(stringBuilder.toString());
            System.out.println("Calculating Fibonacci sequence.");
            output.println("<html>");
            output.println("<body><p> Given a number " + stringBuilder + ", the n-th Fibonacci Number is  " + fib(n) + ".</p></body>");
            output.println("</html>");
            output.flush();
        }
    }
}
