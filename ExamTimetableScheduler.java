import java.util.*;

public class ExamTimetableScheduler {

    private static final String[] ROOMS = {
        "Room 101",
        "Room 102",
        "Room 103",
        "Room 104"
    };

    private static final List<Exam> exams = createExams();

    static class Exam {
        int id;
        String subject;
        Set<String> studentGroups;

        Exam(int id, String subject, String... groups) {
            this.id = id;
            this.subject = subject;
            this.studentGroups = new LinkedHashSet<>(Arrays.asList(groups));
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n==========================================");
            System.out.println("       EXAM TIMETABLE SCHEDULER");
            System.out.println("       DAA - GREEDY GRAPH COLORING");
            System.out.println("==========================================");
            System.out.println("1. View Examination Details");
            System.out.println("2. View Conflict Graph");
            System.out.println("3. Generate Examination Timetable");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {
                case 1:
                    viewExams();
                    break;

                case 2:
                    displayConflictGraph();
                    break;

                case 3:
                    generateTimetable();
                    break;

                case 4:
                    System.out.println("Thank you for using the Exam Timetable Scheduler!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static List<Exam> createExams() {

        List<Exam> list = new ArrayList<>();

        list.add(new Exam(1, "DAA", "CSE-A"));
        list.add(new Exam(2, "Computer Networks", "CSE-A"));
        list.add(new Exam(3, "DBMS", "CSE-B"));
        list.add(new Exam(4, "Java Programming", "CSE-B"));
        list.add(new Exam(5, "Mathematics", "CSE-A", "CSE-B"));
        list.add(new Exam(6, "Operating Systems", "CSE-A"));
        list.add(new Exam(7, "Artificial Intelligence", "CSE-C"));
        list.add(new Exam(8, "Software Engineering", "CSE-C"));
        list.add(new Exam(9, "Cyber Security", "CSE-B", "CSE-C"));
        list.add(new Exam(10, "Data Science", "CSE-D"));

        return list;
    }

    private static void viewExams() {

        System.out.println("\n========== EXAMINATION DETAILS ==========");

        System.out.printf("%-5s %-25s %-20s%n",
                "ID", "Subject", "Student Groups");

        System.out.println("------------------------------------------------------");

        for (Exam exam : exams) {
            System.out.printf("%-5d %-25s %-20s%n",
                    exam.id,
                    exam.subject,
                    String.join(", ", exam.studentGroups));
        }
    }

    private static boolean hasConflict(Exam e1, Exam e2) {

        for (String group : e1.studentGroups) {
            if (e2.studentGroups.contains(group)) {
                return true;
            }
        }

        return false;
    }

    private static void displayConflictGraph() {

        System.out.println("\n========== CONFLICT GRAPH ==========");

        System.out.println("Each examination is a vertex.");
        System.out.println("An edge indicates a shared student group.");
        System.out.println();

        boolean found = false;

        for (int i = 0; i < exams.size(); i++) {
            for (int j = i + 1; j < exams.size(); j++) {

                if (hasConflict(exams.get(i), exams.get(j))) {

                    System.out.println(
                            exams.get(i).subject
                            + " <----> "
                            + exams.get(j).subject
                    );

                    found = true;
                }
            }
        }

        if (!found) {
            System.out.println("No conflicts found.");
        }
    }

    private static int[] greedyColoring() {

        int n = exams.size();

        int[] colors = new int[n];
        Arrays.fill(colors, -1);

        List<Integer> slotCounts = new ArrayList<>();

        for (int i = 0; i < n; i++) {

            int slot = 0;

            while (true) {

                boolean conflict = false;

                for (int j = 0; j < i; j++) {

                    if (colors[j] == slot
                            && hasConflict(exams.get(i), exams.get(j))) {

                        conflict = true;
                        break;
                    }
                }

                boolean roomAvailable =
                        slot >= slotCounts.size()
                        || slotCounts.get(slot) < ROOMS.length;

                if (!conflict && roomAvailable) {
                    break;
                }

                slot++;
            }

            colors[i] = slot;

            while (slotCounts.size() <= slot) {
                slotCounts.add(0);
            }

            slotCounts.set(slot, slotCounts.get(slot) + 1);
        }

        return colors;
    }

    private static void generateTimetable() {

        int[] slots = greedyColoring();

        Map<Integer, Integer> roomCounts = new HashMap<>();

        int maxSlot = 0;

        System.out.println("\n========== GENERATED EXAM TIMETABLE ==========");

        System.out.printf(
                "%-25s %-10s %-25s %-12s%n",
                "SUBJECT", "DAY", "TIME", "ROOM"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (int i = 0; i < exams.size(); i++) {

            int slot = slots[i];

            int roomIndex = roomCounts.getOrDefault(slot, 0);

            roomCounts.put(slot, roomIndex + 1);

            int day = (slot / 2) + 1;

            String time;

            if (slot % 2 == 0) {
                time = "9:00 AM - 12:00 PM";
            } else {
                time = "1:00 PM - 4:00 PM";
            }

            System.out.printf(
                    "%-25s Day %-6d %-25s %-12s%n",
                    exams.get(i).subject,
                    day,
                    time,
                    ROOMS[roomIndex]
            );

            if (slot > maxSlot) {
                maxSlot = slot;
            }
        }

        System.out.println(
                "\nTotal examinations: " + exams.size()
        );

        System.out.println(
                "Total time slots used: " + (maxSlot + 1)
        );

        System.out.println(
                "Total examination days: " + ((maxSlot / 2) + 1)
        );

        System.out.println(
                "\nTimetable generated successfully!"
        );
    }
}
