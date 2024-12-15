import java.util.Arrays;

public class Main {
    // Константы и переменные
    private static final int SIZE = 41; // Размер массива
    private static int threadCount = 3; // Количество потоков для многопоточного метода

    public static void main(String[] args) {
        // Объявление переменных для хранения результатов
        float[] arr1, arr2, arrs;
        float[][] arrParts;

        // Переменные для измерения времени выполнения
        long startTime, endTime;

        // Измерение времени выполнения однопоточного метода
        startTime = System.currentTimeMillis(); // Начало измерения времени
        arr1 = methodThread1(); // Вызов метода с однопоточными вычислениями
        endTime = System.currentTimeMillis(); // Конец измерения времени
        long time1 = endTime - startTime; // Разница времени выполнения

        // Измерение времени выполнения двухпоточного метода
        startTime = System.currentTimeMillis();
        arr2 = methodThread2(); // Вызов метода с двухпоточными вычислениями
        endTime = System.currentTimeMillis();
        long time2 = endTime - startTime;

        // Измерение времени выполнения метода с настраиваемым количеством потоков
        startTime = System.currentTimeMillis();
        arrParts = methodThread3();  // Вызов многопоточного метода
        arrs = combineParts(arrParts); // Сборка финального массива из частей
        endTime = System.currentTimeMillis();
        long time3 = endTime - startTime;

        // Вывод времени выполнения каждого метода
        System.out.printf("Время выполнения:\n1 методом: %d мс\n2 методом: %d мс\n3 методом: %d мс\n\n", time1, time2, time3);

        // Расчёт и вывод длины подмассивов для многопоточного метода
        int partSize = SIZE / threadCount;
        System.out.printf("Длина подмассивов для 3 метода:\n");
        for (int i = 1; i <= threadCount; i++) {
            System.out.printf("Поток %d: %d элементов\n", i, i == threadCount ? partSize + (SIZE % threadCount) : partSize);
        }

        // Вывод результатов вычислений: первый и последний элементы массивов
        System.out.printf("\nРезультаты вычислений:\nПервый элемент:\n1 метод: %.8f\n2 метод: %.8f\n3 метод: %.8f\n\n",
                arr1[0], arr2[0], arrs[0]);

        System.out.printf("Последний элемент:\n1 метод: %.8f\n2 метод: %.8f\n3 метод: %.8f\n",
                arr1[SIZE - 1], arr2[SIZE - 1], arrs[SIZE - 1]);

        // Вывод элементов последнего потока для многопоточного метода
        String lastThreadElements = getLastThreadElements(arrParts);
        System.out.printf("Элементы последнего потока: %s\n", lastThreadElements);
    }

    // Метод с настройкой количества потоков
    private static float[][] methodThread3() {
        // Создаём массив исходных данных
        float[] array = new float[SIZE];
        Arrays.fill(array, 1); // Заполняем массив значением 1

        // Определяем размер частей массива
        int partSize = SIZE / threadCount; // Размер первых частей массива
        int remainder = SIZE % threadCount; // Оставшиеся элементы для последнего части массива

        // Создаём массив потоков и массив для частей результата
        Thread[] threads = new Thread[threadCount];
        float[][] parts = new float[threadCount][];
        int sourcePosition = 0; // Начальная позиция для копирования данных

        // Создаём и запускаем потоки
        for (int i = 0; i < threadCount; i++) {
            float[] arrayPart = new float[partSize + (i == threadCount - 1 ? remainder : 0)];
            System.arraycopy(array, sourcePosition, arrayPart, 0, arrayPart.length); // Копируем данные в часть

            int offset = sourcePosition; // Смещение в массиве
            int finalI = i; // Индекс потока
            sourcePosition += arrayPart.length; // Сдвигаем позицию для следующей части

            // Инициализация потока
            threads[i] = new Thread(() -> {
                // Вычисления в части массива
                for (int j = 0; j < arrayPart.length; j++) {
                    arrayPart[j] = calculateValue(j + offset);
                }
                parts[finalI] = arrayPart; // Сохраняем результаты в массив частей
            });

            threads[i].start(); // Запуск потока
        }

        // Ожидание завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join(); // Ожидание завершения текущего потока
            } catch (InterruptedException e) {
                e.printStackTrace(); // Обработка ошибки прерывания
            }
        }

        return parts; // Возвращаем массив частей результата
    }

    // Метод объединения частей в один массив
    private static float[] combineParts(float[][] parts) {
        float[] array = new float[SIZE]; // Итоговый массив
        int position = 0; // Позиция для копирования данных

        // Копируем данные из каждой части в итоговый массив
        for (float[] part : parts) {
            System.arraycopy(part, 0, array, position, part.length);
            position += part.length;
        }

        return array; // Возвращаем итоговый массив
    }

    // Метод с двухпоточными вычислениями
    private static float[] methodThread2() {
        float[] array = new float[SIZE];
        Arrays.fill(array, 1); // Заполняем массив значением 1

        int half = SIZE / 2; // Делим массив на две равные части
        float[] firstHalf = new float[half];
        float[] secondHalf = new float[SIZE - half];

        // Копируем данные в две части
        System.arraycopy(array, 0, firstHalf, 0, half);
        System.arraycopy(array, half, secondHalf, 0, SIZE - half);

        // Создаём потоки для вычислений в каждой части
        Thread firstHalfCalculation = new Thread(() -> {
            for (int i = 0; i < half; i++) {
                firstHalf[i] = calculateValue(i);
            }
        });

        Thread secondHalfCalculation = new Thread(() -> {
            for (int i = 0; i < secondHalf.length; i++) {
                secondHalf[i] = calculateValue(i + half);
            }
        });

        firstHalfCalculation.start(); // Запуск первого потока
        secondHalfCalculation.start(); // Запуск второго потока

        // Ожидание завершения потоков
        try {
            firstHalfCalculation.join();
            secondHalfCalculation.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Объединяем результаты обратно в массив
        System.arraycopy(firstHalf, 0, array, 0, half);
        System.arraycopy(secondHalf, 0, array, half, SIZE - half);

        return array; // Возвращаем итоговый массив
    }

    // Однопоточный метод
    private static float[] methodThread1() {
        float[] array = new float[SIZE];
        Arrays.fill(array, 1); // Заполняем массив значением 1

        // Вычисляем значения для каждого элемента
        for (int i = 0; i < SIZE; i++) {
            array[i] = calculateValue(i);
        }

        return array; // Возвращаем результат
    }

    // Функция для вычисления значения элемента
    private static float calculateValue(int index) {
        // Формула для расчёта
        return (float) (Math.sin(0.2f + index / 5) * Math.cos(0.2f + index / 5) * Math.cos(0.4f + index / 2));
    }

    // Метод для получения элементов последнего потока
    private static String getLastThreadElements(float[][] parts) {
        if (parts == null || parts.length == 0) {
            return "Нет данных о последнем потоке";
        }
        float[] lastThread = parts[parts.length - 1];
        return Arrays.toString(lastThread); // Преобразуем массив в строку
    }
}
