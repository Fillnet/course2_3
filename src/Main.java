import Notebook.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Scanner;

public class Main {
    private static final Schedule SCHEDULE = new Schedule();
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            label:
            while (true) {
                printMenu();
                System.out.print("Выберите пункт меню: ");
                if (scanner.hasNextInt()) {
                    int menu = scanner.nextInt();
                    switch (menu) {
                        case 1:
                            addTask(scanner);
                            break;
                        case 2:
                            removeTask(scanner);
                            break;
                        case 3:
                            printTaskForDay(scanner);
                            break;
                        case 0:
                            break label;
                    }
                } else {
                    scanner.next();
                    System.out.println("Выберите пункт меню из списка!");
                }
            }
        }
    }
    private static void addTask(Scanner scanner) {
        String title = readString("Введите название задачи: ", scanner);
        String description = readString("Введите описание задачи: ", scanner);
        LocalDateTime taskDate = readDateTime(scanner);
        TaskType taskType = readType(scanner);
        Repeatability repeatability = readRepeatability(scanner);
        Task task = switch (repeatability) {
            case SINGLE -> new SingleTask(title, description, taskDate, taskType);
            case DAILY -> new DailyTask(title, description, taskDate, taskType);
            case WEEKLY -> new WeeklyTask(title, description, taskDate, taskType);
            case MONTHLY -> new MonthlyTask(title, description, taskDate, taskType);
            case YEARLY -> new YearlyTask(title, description, taskDate, taskType);
        };
        SCHEDULE.addTask(task);
    }

    private static Repeatability readRepeatability(Scanner scanner) {
        while (true) {
            try {

                System.out.print("Выберите повторямость задачи: \n");
                for (Repeatability repeatability : Repeatability.values()) {
                    System.out.println(repeatability.ordinal() + ". " + localizeRepeatability(repeatability));
                }
                System.out.println("Введите тип задачи: ");
                String ordinalLine = scanner.nextLine();
                int ordinal = Integer.parseInt(ordinalLine);
                return Repeatability.values()[ordinal];
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный тип задачи ");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Тип задачи не найден ");
            }
        }
    }

    private static TaskType readType(Scanner scanner) {
        while (true) {
            try {

                System.out.print("Выберите тип задачи: \n");
                for (TaskType taskType : TaskType.values()) {
                    System.out.println(taskType.ordinal() + ". " + localizeType(taskType));
                }
                System.out.println("Введите тип задачи: ");
                String ordinalLine = scanner.nextLine();
                int ordinal = Integer.parseInt(ordinalLine);
                return TaskType.values()[ordinal];
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный тип задачи ");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Тип задачи не найден ");
            }
        }
    }

    private static LocalDateTime readDateTime(Scanner scanner) {
        LocalDate localDate = readDate(scanner);
        LocalTime localTime = readTime(scanner);
        return localDate.atTime(localTime);
    }

    private static String readString(String message, Scanner scanner) {
        while (true) {
            System.out.print(message);
            String readString = scanner.nextLine();
            if (readString == null || readString.isBlank()) {
                System.out.println("Не задано название задачи ");
            } else {
                return readString;
            }
        }
    }

    private static void removeTask(Scanner scanner) {
        System.out.println("Все задачи: ");
        for (Task task : SCHEDULE.getAllTask()) {
            System.out.printf("%d. %s [%s](%s)%n",
                    task.getId(),
                    task.getTitle(),
                    localizeType(task.getTaskType()),
                    localizeRepeatability(task.getRepeatabilityType()));
        }
        while (true) {
            try {

                System.out.print("Выберите задачу для удаления: ");
                String idLine = scanner.nextLine();
                int id = Integer.parseInt(idLine);
                SCHEDULE.removeTask(id);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный ид задачи ");
            } catch (TaskNotFoundException e) {
                System.out.println("Задача не найдена ");
            }
        }

    }


    private static void printTaskForDay(Scanner scanner) {
        LocalDate localDate = readDate(scanner);
        Collection<Task> tasksForDate = SCHEDULE.getTaskForDate(localDate);
        System.out.println("Задачи на " + localDate.format(dateFormat));
        for (Task task : tasksForDate) {
            System.out.printf("%s.[%s] Название задачи: %s.Описание задачи: %s. Время выполнения задачи: %s. Повторяемость задачи: %s.%n",
                    task.getId(),
                    localizeType(task.getTaskType()),
                    task.getTitle(),
                    task.getDescription(),
                    task.getTaskDateTime().format(timeFormat),
                    localizeRepeatability(task.getRepeatabilityType()));
        }
    }

    private static LocalDate readDate(Scanner scanner) {
        while (true) {
            try {

                System.out.print("Введите дату в формате dd.MM.yyyy: ");
                String dateLine = scanner.nextLine();
                return LocalDate.parse(dateLine, dateFormat);
            } catch (DateTimeParseException e) {
                System.out.println("Дата введена в не верном формате ");
            }
        }
    }

    private static LocalTime readTime(Scanner scanner) {
        while (true) {
            try {

                System.out.print("Введите время в формате HH:mm: ");
                String dateLine = scanner.nextLine();
                return LocalTime.parse(dateLine, timeFormat);
            } catch (DateTimeParseException e) {
                System.out.println("Время введено в не верном формате ");
            }
        }
    }

    private static String localizeType(TaskType taskType) {
        return switch (taskType) {
            case WORK -> "Рабочая задача";
            case PERSONAL -> "Личная задача";
        };

    }

    private static String localizeRepeatability(Repeatability repeatability) {
        return switch (repeatability) {
            case SINGLE -> "Разовая ";
            case DAILY -> "Ежедневная ";
            case WEEKLY -> "Еженедельная ";
            case MONTHLY -> "Ежемесячная ";
            case YEARLY -> "Ежегодная ";
        };
    }

    private static void printMenu() {
        System.out.println(
                "1. Добавить задачу " +
                        "2. Удалить задачу " +
                        "3. Получить задачу на указанный день " +
                        "0. Выход "
        );
    }
}