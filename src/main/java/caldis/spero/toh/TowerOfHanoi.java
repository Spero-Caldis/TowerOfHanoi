package caldis.spero.toh;

import java.util.Stack;

public class TowerOfHanoi {

    private int numDisks;
    private Stack<Integer>[] towers;

    public TowerOfHanoi(int numDisks) {
        this.numDisks = numDisks;
        this.towers = new Stack[3];
        for (int i = 0; i < 3; i++) {
            towers[i] = new Stack<>();
        }
        for (int i = numDisks; i > 0; i--) {
            towers[0].push(i);
        }
    }

    public void moveDisk(int from, int to) {
        if (!towers[from].isEmpty() && (towers[to].isEmpty() || towers[from].peek() < towers[to].peek())) {
            towers[to].push(towers[from].pop());
        }
    }

    public void autoSolve() {
        solve(numDisks, 0, 2, 1);
    }

    private void solve(int n, int from, int to, int aux) {
        if (n == 1) {
            moveDisk(from, to);
            return;
        }
        solve(n - 1, from, aux, to);
        moveDisk(from, to);
        solve(n - 1, aux, to, from);
    }

    public Stack<Integer>[] getTowers() {
        return towers;
    }
}