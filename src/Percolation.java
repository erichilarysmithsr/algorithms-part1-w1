/******************************************************************************
 *  We model a percolation system using an n-by-n grid of sites.
 *  Each site is either open or blocked. A full site is an open site that
 *  can be connected to an open site in the top row via a chain of neighboring
 *  (left, right, up, down) open sites. We say the system percolates if
 *  there is a full site in the bottom row. In other words,
 *  a system percolates if we fill all open sites connected to the top row
 *  and that process fills some open site on the bottom row.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[] grid;
    private WeightedQuickUnionUF fullyConnectedUf, topOnlyConnectedUf;
    private int cols, virtualTopIndex, virtualBottomIndex, openSites;

    public Percolation(int n) {
        // create n-by-n grid, with all sites blocked
        if (n <= 0) throw new IllegalArgumentException("n needs to be > 0");

        this.cols = n;
        this.grid = new boolean[n * n];
        this.fullyConnectedUf = new WeightedQuickUnionUF(n * n + 2);
        this.topOnlyConnectedUf = new WeightedQuickUnionUF(n * n + 1);
        this.virtualTopIndex = n * n;
        this.virtualBottomIndex = n * n + 1;
        this.openSites = 0;
    }

    private void connectToVirtualEdges(int point) {
        if (point >= 0 && point < this.cols) {
            // Connect virtual top to first row
            this.fullyConnectedUf.union(this.virtualTopIndex, point);
            this.topOnlyConnectedUf.union(this.virtualTopIndex, point);
        }

        if (point >= this.cols * this.cols - this.cols && point < this.cols * this.cols) {
            // Connect virtual bottom to last row
            this.fullyConnectedUf.union(this.virtualBottomIndex, point);
        }
    }

    private boolean areIndexesInRange(int row, int col) {
        return row > 0 && row <= this.cols && col > 0 && col <= this.cols;
    }

    private int xyTo1D(int row, int col) {
        return (this.cols * (row - 1) + col) - 1;
    }

    private int[][] getAdjacentSites(int row, int col) {
        return new int[][]{ {row - 1, col}, {row + 1, col}, {row, col - 1}, {row, col + 1} };
    }

    public void open(int row, int col) {
        if (!this.areIndexesInRange(row, col)) throw new java.lang.IndexOutOfBoundsException();
        if (this.isOpen(row, col)) return;

        int pointToBeOpened = this.xyTo1D(row, col);

        // open site (row, col)
        this.grid[pointToBeOpened] = true;
        this.openSites++;
        // Connect it to all open adjacent sites:
        int[][] adjacents = this.getAdjacentSites(row, col);

        for (int[] adjacent : adjacents) {
            if (this.areIndexesInRange(adjacent[0], adjacent[1]) &&
                this.isOpen(adjacent[0], adjacent[1])) {
                int adjacentOpenSite = this.xyTo1D(adjacent[0], adjacent[1]);

                this.fullyConnectedUf.union(pointToBeOpened, adjacentOpenSite);
                this.topOnlyConnectedUf.union(pointToBeOpened, adjacentOpenSite);
            }
        }

        this.connectToVirtualEdges(pointToBeOpened);
    }

    private boolean isOpen(int n) {
        return this.grid[n];
    }

    public boolean isOpen(int row, int col) {
        if (!this.areIndexesInRange(row, col)) throw new java.lang.IndexOutOfBoundsException();

        return this.isOpen(this.xyTo1D(row, col));
    }

    public boolean isFull(int row, int col) {
        if (!this.areIndexesInRange(row, col)) throw new java.lang.IndexOutOfBoundsException();

        return this.isOpen(row, col) && this.topOnlyConnectedUf.connected(this.xyTo1D(row, col), this.virtualTopIndex);
    }

    public int numberOfOpenSites() {
        return this.openSites;
    }

    public boolean percolates() {
        return this.fullyConnectedUf.connected(this.virtualTopIndex, this.virtualBottomIndex);
    }

    public static void main(String[] args) {
        System.out.println("Percolation");
    }
}
