package test;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileUtils {

    private File homedirectory;

    private int someRandomValue;


    public static File createFile(String fileName) {
        return new File(fileName);
    }

    public static void writeToFile(File file, String message) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(message);
            writer.flush();
            writer.close();
        } catch(IOException ex)
        {
            //
        }
    }

    public File getHomedirectory() {
        return this.homedirectory;
    }

    public void useSomeMath() {
        double sin = Math.sin(90);
        double sqrt = Math.sqrt(144);
    }

    public void conditionPrint(String message, boolean doPrint) {
        if (doPrint) {
            Console c =  System.console();
            c.printf(message);
        }
    }

    public String getInputFromCommandLine() {
        Scanner scanner = new Scanner(System.in);
        return scanner.next();
    }

    public int dependOnParameterAndMember(int first, int second, int notUsedForReturn, int unusedParameter) {
        int unused = notUsedForReturn * 2;
        return first * second + this.someRandomValue;
    }

    public int paramToInnerClass(int test) {
        InnerClass obj = new InnerClass(test, "fu");
        return obj.id;
    }

    private class InnerClass {
        private int id;
        private String name;

        public InnerClass(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int doesItExpose() {
            int a = this.id;
            return 5;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InnerClass that = (InnerClass) o;

            if (id != that.id) return false;
            return name != null ? name.equals(that.name) : that.name == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
