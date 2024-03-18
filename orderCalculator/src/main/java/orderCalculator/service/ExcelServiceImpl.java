package orderCalculator.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);
    private final ExcelExportService excelExportService = new ExcelExportService(); // ExcelExportService 인스턴스 생성


    @Override
    public Workbook processExcelFile(InputStream excelFileInputStream) throws IOException {
        logger.info("processExcelFile 시작");
        Workbook workbook;

        try {
            workbook = new XSSFWorkbook(excelFileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            logger.info("엑셀 업로드 성공");

            // G열에서 실제 데이터가 있는 마지막 행 번호 찾기
            int lastRowNum = sheet.getLastRowNum();
            int lastDataRowNum = 0; // 데이터가 있는 마지막 행 번호 초기화
            for (int rowIndex = 0; rowIndex <= lastRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell cell = row.getCell(6); // G열 확인
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        lastDataRowNum = rowIndex; // 비어 있지 않은 마지막 셀 업데이트
                    }
                }
            }

            int[][] data = new int[8][5]; // A열의 1행부터 E열의 8행까지 정수 배열
            int[] preOrder = new int[lastDataRowNum + 1]; // G열의 1행부터 마지막 행까지 정수 배열
            int[] total = new int[5]; // 각 열의 합계를 저장할 배열

            // A열의 1행부터 E열의 8행까지 데이터 읽기
            for (int rowIndex = 0; rowIndex < 8; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                for (int colIndex = 0; colIndex < 5; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    data[rowIndex][colIndex] = cellToInt(cell);
                }
            }

            // G열의 1행부터 데이터 읽기
            for (int rowIndex = 0; rowIndex <= lastDataRowNum; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                Cell cell = row.getCell(6); // G열은 6번째 인덱스
                preOrder[rowIndex] = cellToInt(cell);
            }

            // 이하 logic은 현업에서 실제로 실행하는 프로세스를 코드화한 것
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

            // 엑셀 파일로 내보냄
            String exportFilePath = "./Result.xlsx"; // 내보낼 파일의 경로와 이름 지정
            excelExportService.exportResultsToExcel(results, exportFilePath);
            logger.info("엑셀 파일 내보내기 성공: " + exportFilePath);

        } catch (IOException e) {
            logger.error("엑셀 파일 처리 중 오류 발생", e);
            throw e;
        }

        return workbook; // 처리 후 Workbook 반환
    }

    // 셀에서 정수 데이터를 추출하는 메서드
    private int cellToInt(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int)cell.getNumericCellValue();
        }
        // 숫자 이외의 타입은 0으로 처리 or 예외 처리
        return 0;
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
                if(j != results[i].length -1)
                    System.out.println();

            }
            System.out.println();
            System.out.println(resultTotal);
        }
    }
}
