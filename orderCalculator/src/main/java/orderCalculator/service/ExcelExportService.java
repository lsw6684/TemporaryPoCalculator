package orderCalculator.service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelExportService {

    public void exportResultsToExcel(int[][][] results, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook(); // 새 Workbook 생성
        Sheet sheet = workbook.createSheet("Results"); // 시트 생성

        int rowOffset = 0; // 현재 행의 위치를 추적

        for (int resultIndex = 0; resultIndex < results.length; resultIndex++) {
            // 각 결과 세트의 제목을 추가
            Row titleRow = sheet.createRow(rowOffset++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Result " + (resultIndex + 1));

            // 결과 데이터 기록
            for (int rowIndex = 0; rowIndex < results[resultIndex].length; rowIndex++) {
                Row row = sheet.createRow(rowOffset++); // 새 행 생성
                for (int colIndex = 0; colIndex < results[resultIndex][rowIndex].length; colIndex++) {
                    Cell cell = row.createCell(colIndex); // 새 셀 생성
                    cell.setCellValue(results[resultIndex][rowIndex][colIndex]); // 셀 값 설정
                }
            }

            rowOffset++; // 각 결과 세트 사이에 공백 행 추가
        }

        // 파일로 저장
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        // Workbook 자원 해제
        workbook.close();
    }
}