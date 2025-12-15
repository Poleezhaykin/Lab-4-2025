package functions;

import java.io.*;
import java.util.StringTokenizer;

public final class TabulatedFunctions {
    private TabulatedFunctions() {
    }
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Points count must be at least 2");
        }
        if (leftX < function.getLeftDomainBorder() - 1e-10 ||
                rightX > function.getRightDomainBorder() + 1e-10) {
            throw new IllegalArgumentException("Tabulation interval is outside function domain");
        }
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            points[i] = new FunctionPoint(x, y);
        }
        return new ArrayTabulatedFunction(points);
    }
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeInt(function.getPointsCount());
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                dos.writeDouble(point.getX());
                dos.writeDouble(point.getY());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing tabulated function to stream", e);
        }
    }
    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointsCount = dis.readInt();
            if (pointsCount < 2) {
                throw new IllegalArgumentException("Invalid data: points count must be at least 2");
            }
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                double x = dis.readDouble();
                double y = dis.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            return new ArrayTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Error reading tabulated function from stream", e);
        }
    }
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        try (PrintWriter writer = new PrintWriter(out)) {
            writer.print(function.getPointsCount());
            for (int i = 0; i < function.getPointsCount(); i++) {
                FunctionPoint point = function.getPoint(i);
                writer.print(" " + point.getX() + " " + point.getY());
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Error writing tabulated function to writer", e);
        }
    }
    public static TabulatedFunction readTabulatedFunction(Reader in) {
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(in);
            tokenizer.parseNumbers();
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new IllegalArgumentException("Expected number of points");
            }
            int pointsCount = (int) tokenizer.nval;
            if (pointsCount < 2) {
                throw new IllegalArgumentException("Points count must be at least 2");
            }
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IllegalArgumentException("Expected X coordinate");
                }
                double x = tokenizer.nval;
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new IllegalArgumentException("Expected Y coordinate");
                }
                double y = tokenizer.nval;
                points[i] = new FunctionPoint(x, y);
            }
            return new ArrayTabulatedFunction(points);
        } catch (IOException e) {
            throw new RuntimeException("Error reading tabulated function from reader", e);
        }
    }
}