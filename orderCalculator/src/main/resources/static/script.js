async function calculateFile() {
    const fileInput = document.getElementById('fileUpload');
    const file = fileInput.files[0];

    console.log('★uploadFile');

    const formData = new FormData();
    formData.append('file', file);

    console.log("★click");


    // 업로드 파일 없으면, alert 있으면 없앰
    if(fileInput.files.length === 0) {
        showAlert('alertBasic', "먼저 엑셀 파일을 업로드 해주세요.", 3);
        return;
    }

    try {

        const response = await fetch('http://localhost:8083/upload', {      // 백엔드 엔드포인트 - 일단 로컬
            method: 'POST',
            body: formData,
        });
        if (response.ok) {

            const downloadUrl = await response.text(); // 가정: 응답으로 다운로드 URL을 받음
            const downloadBtn = document.getElementById('downloadBtn');
            downloadBtn.style.display = 'inline';
            downloadBtn.onclick = () => window.location.href = downloadUrl;
            showAlert('alertBasic', "PO 계산이 성공적으로 완료되었습니다.", 2);

            console.log("★send: ");

        }
        else {
            console.error('서버 에러: ', response.statusText);
        }
    } catch (error) {
        console.error('Upload failed:', error);
    }
}

// 업로드 하자마자 파일이름 출력
document.addEventListener('DOMContentLoaded', function() {
    console.log('★★TEST');
    const fileInput = document.getElementById('fileUpload');
    const fileNameDisplay = document.getElementById('uploadFileName'); // 수정된 요소 ID
    fileInput.addEventListener('change', function() {
        if (fileInput.files.length > 0) {

            const file = fileInput.files[0];

            fileNameDisplay.textContent = `업로드된 파일: ${file.name}`; // 선택된 파일의 이름을 보여줍니다.
            fileNameDisplay.style.display = 'block'; // 파일 이름을 보여줄 div를 화면에 표시합니다.
            showAlert('alertBasic', "파일이 성공적으로 업로드 되었습니다.", 1);

            const btnCalculate = document.getElementById('btnCalculate');
            btnCalculate.style.display='block';
        }
    });
});


function showAlert(showValue, msg, color) {
    console.log("★showAlert");
    let showTarget = document.getElementById(showValue);
    console.log(msg);

    let preTag = `<div class="alert alert-`;

    if(color == 1)      // 하늘색
        preTag += "primary ";
    else if(color == 2) // 초록색
        preTag += "success ";
    else if(color == 3) // 빨강
        preTag += "danger ";
    else                // 기본 노랑
        preTag += "warning ";

    preTag += `alert-dismissible fade show" role="alert">${msg}</div>`;
    showTarget.innerHTML = preTag;

    showTarget.style.display = 'block';
    console.log(preTag);

}

function hideAlert(hideValue) {
    const hideTarget =  document.getElementById(hideValue);
    hideTarget.style.display = 'none';
}
