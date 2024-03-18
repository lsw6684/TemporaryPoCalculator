package orderCalculator.controller;

import orderCalculator.service.ExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final ExcelService excelService; // ExcelService 주입

    // 생성자를 통한 ExcelService 주입
    @Autowired
    public FileUploadController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            logger.error("업로드된 파일이 비어있습니다.");
            return new ResponseEntity<>("파일이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        logger.info("업로드된 파일 이름: {}", file.getOriginalFilename());
        logger.info("업로드된 파일 크기: {}", file.getSize());

        try {
            // 서비스를 통해 엑셀 파일 처리
            var inputStream = file.getInputStream();
            var processedFile = excelService.processExcelFile(inputStream);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"processed.xlsx\"")
                    .body("파일이 처리되었습니다. (실제로 처리된 파일을 반환하거나 파일 처리 결과에 대한 정보를 반환해야 함)");
        } catch (IOException e) {
            logger.error("파일 처리 중 오류가 발생했습니다.", e);
            return new ResponseEntity<>("파일 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
