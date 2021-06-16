package de.wwu.muli.tcg.concrete_programs;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Copyright (c) 2011, Regents of the University of California All rights reserved.
 *
 * <p>Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * <p>1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * <p>2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * <p>3. Neither the name of the University of California, Berkeley nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class TspSolver {

    private int N;
    private int D[][];
    private boolean visited[];
    private int best;

    public int nCalls;

    public TspSolver() {}

    public int solve() {
        best = 2000000; // Adapted since otherwise an integer overflow occurs.

        for (int i = 0; i < N; i++) visited[i] = false;

        visited[0] = true;
        search(0, 0, N - 1);

        return best;
    }

    private int bound(int src, int length, int nLeft) {
        return length;
    }

    private void search(int src, int length, int nLeft) {
        nCalls++;

        if (nLeft == 0) {
            if (length + D[src][0] < best) best = length + D[src][0];
            return;
        }

        if (bound(src, length, nLeft) >= best) return;

        for (int i = 0; i < N; i++) {
            if (visited[i]) continue;

            visited[i] = true;
            search(i, length + D[src][i], nLeft - 1);
            visited[i] = false;
        }
    }

    public void setN(int n) {
        this.N = n;
    }

    public void setD(int[][] d) {
        this.D = d;
    }

    public void setVisited(boolean[] visited) {
        this.visited = visited;
    }

    public void setBest(Integer best) {
        this.best = best;
    }

    public void setNCalls(int nCalls) {
        this.nCalls = nCalls;
    }

    @Test
    public void test_solve_0() {
        TspSolver tspSolver0 = new TspSolver();
        Integer integer0 = 4;
        tspSolver0.setN(integer0);
        int[][] intAr0 = new int[4][4];
        int[] intAr1 = new int[4];
        Integer integer1 = 1;
        intAr1[0] = integer1;
        intAr1[1] = integer1;
        intAr1[2] = integer1;
        intAr1[3] = integer1;
        intAr0[0] = intAr1;
        int[] intAr2 = new int[4];
        intAr2[0] = integer1;
        intAr2[1] = integer1;
        intAr2[2] = integer1;
        intAr2[3] = integer1;
        intAr0[1] = intAr2;
        int[] intAr3 = new int[4];
        Integer integer2 = 2;
        intAr3[0] = integer2;
        intAr3[1] = integer1;
        intAr3[2] = integer1;
        intAr3[3] = integer0;
        intAr0[2] = intAr3;
        int[] intAr4 = new int[4];
        Integer integer3 = 1999994;
        intAr4[0] = integer3;
        intAr4[1] = integer0;
        intAr4[2] = integer1;
        intAr4[3] = integer1;
        intAr0[3] = intAr4;
        tspSolver0.setD(intAr0);
        boolean[] booleanAr0 = new boolean[4];
        Boolean boolean0 = false;
        booleanAr0[0] = boolean0;
        booleanAr0[1] = boolean0;
        booleanAr0[2] = boolean0;
        booleanAr0[3] = boolean0;
        tspSolver0.setVisited(booleanAr0);
        Integer integer4 = 0;
        tspSolver0.setBest(integer4);
        tspSolver0.setNCalls(integer4);
        assertEquals((int)integer0, tspSolver0.solve());
    }

    @Test
    public void test_solve_1() {
        TspSolver tspSolver0 = new TspSolver();
        Integer integer0 = 4;
        tspSolver0.setN(integer0);
        int[][] intAr0 = new int[4][4];
        int[] intAr1 = new int[4];
        Integer integer1 = 1;
        intAr1[0] = integer1;
        Integer integer2 = 3;
        intAr1[1] = integer2;
        Integer integer3 = 2;
        intAr1[2] = integer3;
        intAr1[3] = integer1;
        intAr0[0] = intAr1;
        int[] intAr2 = new int[4];
        intAr2[0] = integer1;
        intAr2[1] = integer1;
        intAr2[2] = integer1;
        intAr2[3] = integer1;
        intAr0[1] = intAr2;
        int[] intAr3 = new int[4];
        intAr3[0] = integer1;
        intAr3[1] = integer1;
        intAr3[2] = integer1;
        intAr3[3] = integer1;
        intAr0[2] = intAr3;
        int[] intAr4 = new int[4];
        intAr4[0] = integer1;
        intAr4[1] = integer0;
        intAr4[2] = integer1;
        intAr4[3] = integer1;
        intAr0[3] = intAr4;
        tspSolver0.setD(intAr0);
        boolean[] booleanAr0 = new boolean[4];
        Boolean boolean0 = false;
        booleanAr0[0] = boolean0;
        booleanAr0[1] = boolean0;
        booleanAr0[2] = boolean0;
        booleanAr0[3] = boolean0;
        tspSolver0.setVisited(booleanAr0);
        Integer integer4 = 0;
        tspSolver0.setBest(integer4);
        tspSolver0.setNCalls(integer4);
        assertEquals((int)integer0, tspSolver0.solve());
    }
}
