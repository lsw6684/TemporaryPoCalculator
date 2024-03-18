package orderCalculator.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;


public interface ExcelService {
    Workbook processExcelFile(InputStream excelFileInputStream) throws IOException;
}
