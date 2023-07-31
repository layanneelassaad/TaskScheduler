import java.util.*;

public class TaskScheduler {

    public static Map<Character, List<Character>> taskGraph = new HashMap<>();
    public static Map<Character, Integer> taskStartTime = new HashMap<>();

    public static void addTask(char task, List<Character> dependencies) {
        taskGraph.put(task, dependencies);
    }

    public static boolean isCyclic(char task, Set<Character> visited, Set<Character> recursionStack) {
        if (recursionStack.contains(task)) {
            return true; // Cycle detected
        }

        if (visited.contains(task)) {
            return false; // Already visited, no cycle
        }

        visited.add(task);
        recursionStack.add(task);

        List<Character> dependencies = taskGraph.get(task);
        if (dependencies != null) {
            for (char dep : dependencies) {
                if (isCyclic(dep, visited, recursionStack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(task);
        return false;
    }

    public static boolean hasCycles() {
        Set<Character> visited = new HashSet<>();
        Set<Character> recursionStack = new HashSet<>();

        for (char task : taskGraph.keySet()) {
            if (!visited.contains(task)) {
                if (isCyclic(task, visited, recursionStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void scheduleTasks() {
        Set<Character> visited = new HashSet<>();
        Stack<Character> stack = new Stack<>();

        for (char task : taskGraph.keySet()) {
            if (!visited.contains(task)) {
                topologicalSort(task, visited, stack);
            }
        }

        // Initialize taskStartTime with default values
        for (char task : taskGraph.keySet()) {
            taskStartTime.put(task, 0);
        }

        while (!stack.isEmpty()) {
            char task = stack.pop();
            int startTime = 1;

            List<Character> dependencies = taskGraph.get(task);
            if (dependencies != null) {
                for (char dep : dependencies) {
                    startTime = Math.max(startTime, taskStartTime.get(dep) + 1);
                }
            }

            taskStartTime.put(task, startTime);
        }
    }

    public static void topologicalSort(char task, Set<Character> visited, Stack<Character> stack) {
        visited.add(task);
        List<Character> dependencies = taskGraph.get(task);

        if (dependencies != null) {
            for (char dep : dependencies) {
                if (!visited.contains(dep)) {
                    topologicalSort(dep, visited, stack);
                }
            }
        }

        stack.push(task);
    }

    public static void main(String[] args) {
        addTask('A', new ArrayList<>());
        addTask('B', Arrays.asList('A'));
        addTask('C', Arrays.asList('A'));
        addTask('D', Arrays.asList('B', 'C'));
        addTask('E', Arrays.asList('D'));

        if (hasCycles()) {
            System.out.println("Tasks have cyclic dependencies. Cannot schedule.");
            return;
        }

        scheduleTasks();

        System.out.println("Task Schedule:");
        for (char task : taskStartTime.keySet()) {
            System.out.println(task + " - Start Time: " + taskStartTime.get(task));
        }
    }
}