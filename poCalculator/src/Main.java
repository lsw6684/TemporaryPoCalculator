import java.util.Arrays;

public class Main {
    private static int[][] data = {
            {6374, 12470, 15514, 10614, 5970},
            {6366, 12455, 15496, 10601, 5963},
            {4157, 8133, 10119, 6923, 3894},
            {4921, 9628, 11979, 8195, 4609},
            {4157, 8133, 10119, 6923, 3894},
            {4157, 8133, 10119, 6923, 3894},
            {4831, 9451, 11759, 8044, 4524},
            {6190, 12111, 15068, 10308, 5798}
    };
    private static int[] total = new int[5];
    private static int[] preOrder = {
            34500, 20945, 58779, 25360, 39303, 22881, 16848, 24934, 24934, 27112, 29140, 4181
    };

    public static void main(String[] args) {

        // 각 세로 값 계산.
        for(int i = 0; i < data.length; i++) {
            for(int j = 0; j < data[i].length; j++) {
                total[j] += data[i][j];
            }
        }


        int preOrderTotal = Arrays.stream(preOrder).sum(); // 전년도 오더 전체 합



        double[] preOrderPercentage = new double[preOrder.length];
        for (int i = 0; i < preOrder.length; i++) {
            preOrderPercentage[i] = (double) preOrder[i] / preOrderTotal * 100;
        }

        int[][][] results = new int[preOrderPercentage.length][data.length][data[0].length];

        for (int i = 0; i < preOrderPercentage.length; i++) {
            for (int j = 0; j < data.length; j++) {
                for (int k = 0; k < data[j].length; k++) {
                    results[i][j][k] = (int) Math.round(data[j][k] * preOrderPercentage[i] / 100);
                }
            }
        }

        adjustResultsToPreOrder(results, preOrder);
        adjustColumnSumsToTotal(results, total);
        printAndVerifyResults(results, preOrder, total);
    }

    private static void adjustResultsToPreOrder(int[][][] results, int[] preOrder) {
        for (int i = 0; i < results.length; i++) {
            int resultTotal = Arrays.stream(results[i]).flatMapToInt(Arrays::stream).sum();
            int discrepancy = preOrder[i] - resultTotal;

            if (discrepancy != 0) {
                for (int j = 0; j < results[i].length && discrepancy != 0; j++) {
                    for (int k = 0; k < results[i][j].length && discrepancy != 0; k++) {
                        if (discrepancy > 0) {
                            results[i][j][k] += 1;
                            discrepancy -= 1;
                        } else {
                            results[i][j][k] -= 1;
                            discrepancy += 1;
                        }
                    }
                }
            }
        }
    }

    private static void adjustColumnSumsToTotal(int[][][] results, int[] total) {
        for (int k = 0; k < total.length; k++) {
            int columnSum = 0;
            for (int[][] result : results) {
                for (int[] datum : result) {
                    columnSum += datum[k];
                }
            }
            int discrepancy = total[k] - columnSum;

            for (int i = 0; i < results.length && discrepancy != 0; i++) {
                for (int j = 0; j < results[i].length && discrepancy != 0; j++) {
                    if (discrepancy > 0) {
                        results[i][j][k] += 1;
                        discrepancy -= 1;
                    } else {
                        results[i][j][k] -= 1;
                        discrepancy += 1;
                    }
                }
            }
        }
    }

    private static void printAndVerifyResults(int[][][] results, int[] preOrder, int[] total) {
        int[] columnSums = new int[total.length];
        Arrays.fill(columnSums, 0);
        int test = 0;
        for (int i = 0; i < results.length; i++) {
            int resultTotal = 0;
            System.out.println("Result " + (i + 1));
            for (int j = 0; j < results[i].length; j++) {
                test += results[i][j][2];
                for (int k = 0; k < results[i][j].length; k++) {
                    System.out.print(results[i][j][k] + " ");
                    resultTotal += results[i][j][k];
                    columnSums[k] += results[i][j][k];
                }
                System.out.println();
            }
            System.out.println("Expected Total: " + preOrder[i] + ", Actual Total: " + resultTotal);
            System.out.println();
        }

        System.out.println("Column Totals Verification:");
        for (int i = 0; i < columnSums.length; i++) {
            System.out.println("Column " + (i + 1) + " Expected: " + total[i] + ", Actual: " + columnSums[i]);
        }

    }
}