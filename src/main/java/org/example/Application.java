package org.example;

import org.example.enums.TaskStatus;
import org.example.model.Task;
import org.example.repository.JsonFileTaskRepository;
import org.example.repository.TaskRepository;
import org.example.service.TaskService;

import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Application {
    private static final String PATH    = "task-tracker-data.json";
    private static final String VERSION = "1.0.0";

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    .withZone(ZoneId.systemDefault()); // Lấy múi giờ của máy tính

    public static void main(String[] args) {
        File dataFile = new File(PATH);
        TaskRepository repository = new JsonFileTaskRepository(dataFile);
        TaskService service = new TaskService(repository);

        if (args.length > 0) {
            executeCommand(service, args);
            return;
        }

        System.out.println("=== Task Tracker v" + VERSION + " ===");
        System.out.println("Gõ 'help' để xem hướng dẫn, 'exit' để thoát.\n");

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("task-tracker> ");
            String input = sc.nextLine().trim();

            if (input.isEmpty()) continue;
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Tạm biệt!");
                break;
            }

            String[] commandArgs = parseInput(input);

            executeCommand(service, commandArgs);
            System.out.println();
        }
        sc.close();
    }

    private static String[] parseInput(String input) {
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("\"([^\"]*)\"|(\\S+)");
        Matcher m = regex.matcher(input);
        while (m.find()) {
            if (m.group(1) != null) {
                matchList.add(m.group(1));
            } else {
                matchList.add(m.group(2));
            }
        }
        return matchList.toArray(new String[0]);
    }

    private static void executeCommand(TaskService service, String[] args) {
        try {
            if (args.length == 0) {
                printHelp();
                return;
            }

            switch (args[0]) {
                case "-h", "--help"    -> printHelp();
                case "-v", "--version" -> System.out.println("Task Tracker - Version " + VERSION);

                case "add" -> {
                    if (args.length < 2) { error("Cú pháp: add \"Mo ta\""); return; }
                    service.add(new Task(args[1]));
                    System.out.println("Đã thêm nhiệm vụ.");
                }

                case "update" -> {
                    if (args.length < 3) { error("Cú pháp: update <id> \"Mo ta\""); return; }
                    Integer id = parseId(args[1]);
                    if (id == null) return;
                    service.updateDescription(id, args[2]);
                    System.out.println("Đã cập nhật nhiệm vụ #" + id);
                }

                case "delete" -> {
                    if (args.length < 2) { error("Cú pháp: delete <id>"); return; }
                    Integer id = parseId(args[1]);
                    if (id == null) return;
                    service.delete(id);
                    System.out.println("Đã xóa nhiệm vụ #" + id);
                }

                case "mark-in-progress" -> {
                    if (args.length < 2) { error("Cú pháp: mark-in-progress <id>"); return; }
                    Integer id = parseId(args[1]);
                    if (id == null) return;
                    service.updateStatus(id, TaskStatus.IN_PROGRESS);
                    System.out.println("Nhiệm vụ #" + id + " đang làm.");
                }

                case "mark-done" -> {
                    if (args.length < 2) { error("Cú pháp: mark-done <id>"); return; }
                    Integer id = parseId(args[1]);
                    if (id == null) return;
                    service.updateStatus(id, TaskStatus.DONE);
                    System.out.println("Nhiệm vụ #" + id + " đã hoàn thành.");
                }

                case "list" -> {
                    if (args.length < 2) {
                        printList(service.findAll());
                        return;
                    }
                    switch (args[1]) {
                        case "done"        -> printList(service.findByStatus(TaskStatus.DONE));
                        case "in-progress" -> printList(service.findByStatus(TaskStatus.IN_PROGRESS));
                        case "todo"        -> printList(service.findByStatus(TaskStatus.TODO));
                        default -> {
                            error("Trạng thái không hợp lệ: " + args[1]);
                            System.out.println("Dùng: list [done | in-progress | todo]");
                        }
                    }
                }

                default -> {
                    error("Lệnh không hợp lệ: " + args[0]);
                    printHelp();
                }
            }
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private static void printList(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("(Không có nhiệm vụ nào)");
            return;
        }
        printHeader();
        for (Task task : tasks) {
            printRow(task);
        }
    }

    private static void printHeader() {
        System.out.printf("%-4s %-34s %-13s %s%n",
                "ID", "Description", "Status", "Last Update");
        System.out.println("─".repeat(78));
    }

    private static void printRow(Task task) {
        String updated = (task.getUpdatedAt() == null)
                ? "-"
                : DATE_FMT.format(task.getUpdatedAt());

        System.out.printf("%-4d %-34s %-13s %s%n",
                task.getId(),
                truncate(task.getDescription(), 34),
                task.getStatus(),
                updated);
    }

    private static String truncate(String s, int max) {
        if (s == null) return "-";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private static Integer parseId(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            error("ID không hợp lệ: " + raw);
            return null;
        }
    }

    private static void error(String msg) {
        System.out.println("Lỗi: " + msg);
    }

    private static void printHelp() {
        System.out.print("""
                 Bạn nên sử dụng theo form sau:
                 task-tracker <lenh>

                 Lệnh:
                    add "Mo ta"                 : Thêm nhiệm vụ.
                    update <id> "Mo ta"         : Cập nhật nhiệm vụ.
                    delete <id>                 : Xóa nhiệm vụ.
                    mark-in-progress <id>       : Đánh dấu là đang làm.
                    mark-done <id>              : Đánh dấu là đã hoàn thành.
                    list                        : Liệt kê.
                    list done                   : Liệt kê công việc đã hoàn thành.
                    list todo                   : Liệt kê công việc chưa làm.
                    list in-progress            : Liệt kê công việc đang làm.
                """);
    }
}