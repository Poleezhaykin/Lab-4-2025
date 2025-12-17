import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        try {

            System.out.println("=== Часть 1: Создание объектов Sin и Cos ===");
            Sin sin = new Sin();
            Cos cos = new Cos();

            System.out.println("Значения Sin и Cos на отрезке [0, π] с шагом 0.1:");
            System.out.println("  x\t\tSin(x)\t\tCos(x)");
            System.out.println("------------------------------------------------");
            for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
                System.out.printf("%6.2f\t%10.6f\t%10.6f%n",
                        x, sin.getFunctionValue(x), cos.getFunctionValue(x));
            }

            System.out.println("\n\n=== Часть 2: Табулирование Sin и Cos (10 точек) ===");
            TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
            TabulatedFunction tabCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);

            System.out.println("Сравнение аналитических и табулированных функций:");
            System.out.println("  x\t\tSin(аналит)\tSin(табул)\tРазница\t\tCos(аналит)\tCos(табул)\tРазница");
            System.out.println("--------------------------------------------------------------------------------------------------------");
            for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
                double sinAnalytic = sin.getFunctionValue(x);
                double sinTab = tabSin.getFunctionValue(x);
                double cosAnalytic = cos.getFunctionValue(x);
                double cosTab = tabCos.getFunctionValue(x);
                System.out.printf("%6.2f\t%10.6f\t%10.6f\t%10.6f\t%10.6f\t%10.6f\t%10.6f%n",
                        x, sinAnalytic, sinTab, sinAnalytic - sinTab,
                        cosAnalytic, cosTab, cosAnalytic - cosTab);
            }

            System.out.println("\n\n=== Часть 3: Сумма квадратов sin² + cos² ===");

            for (int points : new int[]{10, 20, 50, 100}) {
                System.out.println("\n--- Количество точек табуляции: " + points + " ---");
                TabulatedFunction tabSinN = TabulatedFunctions.tabulate(sin, 0, Math.PI, points);
                TabulatedFunction tabCosN = TabulatedFunctions.tabulate(cos, 0, Math.PI, points);

                Function sinSquared = Functions.power(tabSinN, 2);
                Function cosSquared = Functions.power(tabCosN, 2);
                Function sumSquares = Functions.sum(sinSquared, cosSquared);

                System.out.println("  x\t\tsin²+cos²\tОтклонение от 1");
                System.out.println("--------------------------------------------");
                double maxError = 0;
                for (double x = 0; x <= Math.PI + 1e-10; x += 0.1) {
                    double value = sumSquares.getFunctionValue(x);
                    double error = Math.abs(value - 1.0);
                    if (error > maxError) maxError = error;
                    System.out.printf("%6.2f\t%12.8f\t%12.8f%n", x, value, error);
                }
                System.out.printf("Максимальная погрешность: %.8f%n", maxError);
            }
            System.out.println("\n\n=== Часть 4: Текстовый формат (экспонента) ===");
            Exp exp = new Exp();
            TabulatedFunction tabExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);

            System.out.println("Запись экспоненты в файл exp.txt...");
            try (FileWriter writer = new FileWriter("exp.txt")) {
                TabulatedFunctions.writeTabulatedFunction(tabExp, writer);
            }

            System.out.println("Чтение экспоненты из файла exp.txt...");
            TabulatedFunction readExp;
            try (FileReader reader = new FileReader("exp.txt")) {
                readExp = TabulatedFunctions.readTabulatedFunction(reader);
            }

            System.out.println("\nСравнение исходной и считанной экспоненты:");
            System.out.println("  x\t\tОригинал\tПрочитано\tРазница");
            System.out.println("--------------------------------------------------------");
            for (int i = 0; i <= 10; i++) {
                double x = i;
                double orig = tabExp.getFunctionValue(x);
                double read = readExp.getFunctionValue(x);
                System.out.printf("%6.1f\t%12.6f\t%12.6f\t%12.6f%n", x, orig, read, orig - read);
            }

            System.out.println("\nСодержимое файла exp.txt:");
            try (BufferedReader br = new BufferedReader(new FileReader("exp.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }

            System.out.println("\n\n=== Часть 5: Бинарный формат (логарифм) ===");
            Log ln = new Log(Math.E);
            TabulatedFunction tabLn = TabulatedFunctions.tabulate(ln, 1, 10, 10);

            System.out.println("Запись логарифма в файл ln.bin...");
            try (FileOutputStream fos = new FileOutputStream("ln.bin")) {
                TabulatedFunctions.outputTabulatedFunction(tabLn, fos);
            }

            System.out.println("Чтение логарифма из файла ln.bin...");
            TabulatedFunction readLn;
            try (FileInputStream fis = new FileInputStream("ln.bin")) {
                readLn = TabulatedFunctions.inputTabulatedFunction(fis);
            }

            System.out.println("\nСравнение исходного и считанного логарифма:");
            System.out.println("  x\t\tОригинал\tПрочитано\tРазница");
            System.out.println("--------------------------------------------------------");
            for (int i = 1; i <= 10; i++) {
                double x = i;
                double orig = tabLn.getFunctionValue(x);
                double read = readLn.getFunctionValue(x);
                System.out.printf("%6.1f\t%12.6f\t%12.6f\t%12.6f%n", x, orig, read, orig - read);
            }

            System.out.println("\n\n=== Часть 6: Сериализация (Задание 9) ===");
            Log lnForComp = new Log(Math.E);
            Exp expForComp = new Exp();
            Composition lnExp = new Composition(expForComp, lnForComp); //исправлено 

            System.out.println("Табулирование функции ln(exp(x)) = x на [0, 10] с 11 точками...");
            TabulatedFunction tabLnExp = TabulatedFunctions.tabulate(lnExp, 0, 10, 11);

            System.out.println("\n1. Сериализация через Serializable");
            File serFile1 = new File("serializable_func.ser");

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(serFile1))) {
                oos.writeObject(tabLnExp);
                System.out.println("Создан файл: " + serFile1.getName());
                System.out.println("Размер файла: " + serFile1.length() + " байт");
            }

            TabulatedFunction deserialized1;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(serFile1))) {
                deserialized1 = (TabulatedFunction) ois.readObject();
            }

            System.out.println("\nПроверка корректности десериализации:");
            System.out.println("  x\t\tОригинал\tДесериал.\tСовпадает?");
            System.out.println("--------------------------------------------------------");
            boolean allMatch1 = true;
            for (int i = 0; i <= 10; i++) {
                double x = i;
                double orig = tabLnExp.getFunctionValue(x);
                double deser = deserialized1.getFunctionValue(x);
                boolean match = Math.abs(orig - deser) < 1e-10;
                if (!match) allMatch1 = false;
                System.out.printf("%6.1f\t%12.6f\t%12.6f\t%10s%n",
                        x, orig, deser, match ? "✓" : "✗");
            }
            System.out.println("Все значения совпадают: " + (allMatch1 ? "ДА" : "НЕТ"));

            System.out.println("\nАнализ содержимого Serializable файла:");
            System.out.println("Первые 100 байт файла (hex):");
            try (FileInputStream fis = new FileInputStream(serFile1)) {
                byte[] buffer = new byte[100];
                int bytesRead = fis.read(buffer);
                for (int i = 0; i < bytesRead; i++) {
                    if (i % 16 == 0) System.out.printf("%n%04X: ", i);
                    System.out.printf("%02X ", buffer[i] & 0xFF);
                }
                System.out.println();
            }

            System.out.println("\n2. Сериализация через Externalizable (LinkedListTabulatedFunction)");
            File serFile2 = new File("externalizable_linkedlist.ser");

            FunctionPoint[] pointsArray = new FunctionPoint[11];
            for (int i = 0; i <= 10; i++) {
                double x = i;
                double y = x; // ln(exp(x)) = x
                pointsArray[i] = new FunctionPoint(x, y);
            }

            LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(pointsArray);

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(serFile2))) {
                oos.writeObject(linkedListFunc);
                System.out.println("Создан файл: " + serFile2.getName());
                System.out.println("Размер файла: " + serFile2.length() + " байт");
            }

            LinkedListTabulatedFunction deserializedLinkedList;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(serFile2))) {
                deserializedLinkedList = (LinkedListTabulatedFunction) ois.readObject();
            }

            System.out.println("\nПроверка LinkedListTabulatedFunction (Externalizable):");
            System.out.println("  x\t\tОригинал\tДесериал.\tСовпадает?");
            System.out.println("--------------------------------------------------------");
            boolean allMatchLinkedList = true;
            for (int i = 0; i <= 10; i++) {
                double x = i;
                double orig = linkedListFunc.getFunctionValue(x);
                double deser = deserializedLinkedList.getFunctionValue(x);
                boolean match = Math.abs(orig - deser) < 1e-10;
                if (!match) allMatchLinkedList = false;
                System.out.printf("%6.1f\t%12.6f\t%12.6f\t%10s%n",
                        x, orig, deser, match ? "✓" : "✗");
            }
            System.out.println("Все значения совпадают: " + (allMatchLinkedList ? "ДА" : "НЕТ"));

            System.out.println("\nАнализ содержимого Externalizable файла:");
            System.out.println("Первые 100 байт файла (hex):");
            try (FileInputStream fis = new FileInputStream(serFile2)) {
                byte[] buffer = new byte[100];
                int bytesRead = fis.read(buffer);
                for (int i = 0; i < bytesRead; i++) {
                    if (i % 16 == 0) System.out.printf("%n%04X: ", i);
                    System.out.printf("%02X ", buffer[i] & 0xFF);
                }
                System.out.println();
            }

            System.out.println("\n=== Сравнение Serializable и Externalizable ===");
            System.out.println("\nРазмеры файлов:");
            System.out.println("  Serializable:  " + serFile1.length() + " байт");
            System.out.println("  Externalizable: " + serFile2.length() + " байт");
            System.out.println("  Разница: " + (serFile1.length() - serFile2.length()) + " байт");

        } catch (Exception e) {
            System.err.println("Ошибка во время выполнения теста:");
            e.printStackTrace();
        }
    }
}