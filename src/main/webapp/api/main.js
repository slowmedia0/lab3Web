//Рисование
const canvas = document.getElementById("canvas");
const context = canvas.getContext("2d");

var xc = 200;
var yc = 200;
var R = 70;

context.beginPath();
//Рисуем линию x
context.moveTo(0, yc);
context.lineTo(2 * xc, yc);

//Рисуем линию y
context.moveTo(xc, 2 * yc);
context.lineTo(xc, 0);

context.closePath();
context.stroke();

//Рисуем четверть окружности
context.beginPath();
context.moveTo(xc, yc);
context.arc(xc, yc, R / 2, 0, Math.PI / 2, false);
context.closePath();
context.fillStyle = "blue";
context.globalAlpha = 0.45;
context.fill();
context.stroke();

//Рисуем прямоугольник
context.beginPath();
context.moveTo(xc, yc);
context.rect(xc, yc - R, R / 2, R);
context.closePath();
context.fillStyle = "blue";
context.globalAlpha = 0.45;
context.fill();
context.stroke();

//Рисуем треугольник
context.beginPath();
context.moveTo(xc, yc);
context.lineTo(xc - R, yc);
context.lineTo(xc, yc - R);
context.closePath();
context.fillStyle = "blue";
context.globalAlpha = 0.45;
context.fill();
context.stroke();

//Засечки на осях, стрелочки и подписи 

//Стрелочки
context.beginPath();
context.moveTo(2 * xc, yc);
context.lineTo(2 * xc - 10, yc - 5);
context.moveTo(2 * xc, yc);
context.lineTo(2 * xc - 10, yc + 5);

context.moveTo(xc, 0);
context.lineTo(xc - 5, 0 + 10); 
context.moveTo(xc, 0);
context.lineTo(xc + 5, 0 + 10); 
context.closePath();
context.stroke();

//Засечки и подписи
const crossLength = 5;
const gapX = 5;
const gapY = 5;
context.beginPath();
context.strokeStyle = "black";
context.moveTo(xc - crossLength / 2, yc - R / 2);
context.lineTo(xc + crossLength / 2, yc - R / 2);
context.fillText("R/2", xc + gapX, yc - R / 2 + gapY);
context.moveTo(xc - crossLength / 2, yc - R);
context.lineTo(xc + crossLength / 2, yc - R);
context.fillText("R", xc + gapX, yc - R + gapY);

context.moveTo(xc + R / 2, yc - crossLength / 2);
context.lineTo(xc + R / 2, yc + crossLength / 2);
context.fillText("R/2", xc + R / 2 - gapX, yc - gapY);
context.moveTo(xc + R, yc - crossLength / 2);
context.lineTo(xc + R, yc + crossLength / 2);
context.fillText("R", xc + R - gapX, yc - gapY);

context.moveTo(xc - crossLength / 2, yc + R / 2);
context.lineTo(xc + crossLength / 2, yc + R / 2);
context.fillText("-R/2", xc + gapX, yc + R / 2 + gapY);
context.moveTo(xc - crossLength / 2, yc + R);
context.lineTo(xc + crossLength / 2, yc + R);
context.fillText("-R", xc + gapX, yc + R + gapY);

context.moveTo(xc - R / 2, yc - crossLength / 2);
context.lineTo(xc - R / 2, yc + crossLength / 2);
context.fillText("-R/2", xc - R / 2 - gapX, yc - gapY);
context.moveTo(xc - R, yc - crossLength / 2);
context.lineTo(xc - R, yc + crossLength / 2);
context.fillText("-R", xc - R - gapX, yc - gapY);

context.textBaseline = "bottom";
context.fillText("x", 2 * xc - gapX, yc - gapY);
context.textBaseline = "hanging";
context.fillText("y", xc + 1.22 * gapX, 0);
context.closePath();
context.stroke();


//Подстановка точки и валидация
const form = document.forms["search"];
var tableRaws = [];
var x;
var y;
var r;

function logFront(level, message) {
    const line = `[${new Date().toISOString()}] [Front] ${level}: ${message}`;
    if (level === "ERROR") console.error(line);
    else if (level === "WARN") console.warn(line);
    else console.log(line);
}

canvas.addEventListener("click", function(event) {
    if (!validateAndSetX()) {
            const xError = document.getElementById("x-error");
            xError.textContent = "Невозможно определить координаты: сначала выберите X на форме!";
            xError.style.display = "block";
            logFront("WARN", "Canvas click ignored: X is not selected");
            return;
    }


    if (!validateAndSetR()) {
        document.getElementById("r-error").textContent = "Невозможно определить координаты: сначала выберите R!";
        logFront("WARN", "Canvas click ignored: R is not selected");
        return;
    }

    const rect = canvas.getBoundingClientRect();
    const clickY = event.clientY - rect.top;

    const scale = R / r;
    y = parseFloat(((yc - clickY) / scale).toFixed(2));

    document.getElementById("Y").value = y;

    if (!validateAndSetY()) {
        logFront("WARN", "Canvas click rejected by Y validation");
        return;
    }

    logFront("INFO", `Sending point from canvas click: x=${x}, y=${y}, r=${r}`);
    playSound();

    $.ajax({
        url: 'controller-servlet',
        method: 'POST',
        dataType: 'json',
        data: {
            x: x,
            y: y,
            r: r
        },
        success: function(data) {
            if (!data.validatePoint) {
                logFront("WARN", "Server rejected canvas point as invalid");
                return;
            }
            logFront("INFO", `Server response: hit=${data.popadanie}, time=${data.currentTime}, exec=${data.executionTime}`);
            addRow(x, y, r, data.popadanie, data.currentTime, data.executionTime);
        },
        error: function(xhr) {
            logFront("ERROR", `Canvas point request failed: HTTP ${xhr.status}`);
        }
    });
});


function checkPoint() {
    if (!validateForm()) {
        logFront("WARN", "Client-side validation failed, request not sent");
        return; // Прерываем отправку, если форма заполнена с ошибками
    }

    logFront("INFO", `Sending point: x=${x}, y=${y}, r=${r}`);
    playSound();

    $.ajax({
        url: 'controller-servlet',
        method: 'POST',
        dataType: 'json',
        data: {
            x: x,
            y: y,
            r: r
        },
        success: function(data) {
            if (!data.validatePoint) {
                logFront("WARN", "Server rejected point as invalid");
                return;
            }
            logFront("INFO", `Server response: hit=${data.popadanie}, time=${data.currentTime}, exec=${data.executionTime}`);
            addRow(x, y, r, data.popadanie, data.currentTime, data.executionTime);
        },
        error: function(xhr) {
            logFront("ERROR", `Point request failed: HTTP ${xhr.status}`);
        }
    });
}

function validateForm() {
    return validateAndSetX() && validateAndSetY() && validateAndSetR();
}

function validateAndSetX() {
    const selectX = document.getElementById("selectX");
    let errorElement = document.getElementById("x-error");

    function showError(message) {
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = "block";
        }
    }

    function hideError() {
        if (errorElement) {
            errorElement.textContent = "";
            errorElement.style.display = "none";
        }
    }

    hideError();

    if (!selectX || selectX.value === "") {
        showError("Для X должно быть выбрано только одно значение!");
        return false;
    }
    const allowedX = [-3, -2, -1, 0, 1, 2, 3, 4, 5];
    let valX = parseInt(selectX.value, 10);
    if (isNaN(valX) || !allowedX.includes(valX)) {
        showError("Недопустимое значение X!");
        return false;
    }
    x = valX;
    return true;
}

function validateAndSetY() {
    let valY = form.elements["Y"].value;
    let trimmed = valY.trim().replace(',', '.');
    let errorElement = document.getElementById("y-error");

    function showError(message) {
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = "block";
        }
    }

    function hideError() {
        if (errorElement) {
            errorElement.textContent = "";
            errorElement.style.display = "none";
        }
    }

    hideError();

    if (trimmed === "") {
        showError("Поле Y не должно быть пустым!");
        return false;
    }

    if (trimmed.length > 15) {
        showError("Длина ввода не должна превышать 15 символов!");
        return false;
    }

    if (!/^-?\d+(\.\d+)?$/.test(trimmed)) {
        showError("Поле Y должно быть числом!");
        return false;
    }

    y = parseFloat(trimmed);

    if (y <= -5 || y >= 5) {
        showError("Поле Y должно быть в диапазоне: (-5; 5)!");
        return false;
    }

    return true;
}

function validateAndSetR() {
    const activeBtn = document.querySelector('.r-btn.active');
    let errorElement = document.getElementById("r-error");

    function showError(message) {
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = "block";
        }
    }

    function hideError() {
        if (errorElement) {
            errorElement.textContent = "";
            errorElement.style.display = "none";
        }
    }

    hideError();

    if (!activeBtn) {
        showError("Для R должно быть выбрано только одно значение!");
        return false;
    }
    const allowedR = [1, 2, 3, 4, 5];
    let valR = parseInt(activeBtn.value, 10);
    if (isNaN(valR) || !allowedR.includes(valR)) {
        showError("Недопустимое значение R!");
        return false;
    }
    r = valR;
    return true;
}

function addRow(valX, valY, valR, isHit, serverTime, executionTime) {
    drawPoint(valX, valY, valR, isHit);

    loadPage(1);
}

function drawPoint(x, y, r, isHit) {
    const scale = R / r; 
    const pixelX = xc + x * scale;
    const pixelY = yc - y * scale;

    context.beginPath();
    context.arc(pixelX, pixelY, 4, 0, 2 * Math.PI);
    context.fillStyle = isHit ? "green" : "red";
    context.globalAlpha = 1.0; 
    context.fill();
    context.closePath();
}

function formatDateTime(isoString) {
    const date = new Date(isoString);
    
    return new Intl.DateTimeFormat('ru-RU', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    }).format(date);
}

function clearHistory() {
    logFront("INFO", "Clearing history in DB");
    fetch('controller-servlet?clear=1')
        .then(resp => {
            if (!resp.ok) logFront("ERROR", `Clear failed: HTTP ${resp.status}`);
            location.reload();
        })
        .catch(e => logFront("ERROR", `Clear request failed: ${e}`));
}

function sortTableByTimeAsc() {
    tableRaws.sort((a, b) => new Date(a.timeStamp) - new Date(b.timeStamp));
    updateUI();
}

function sortTableByTimeDesc() {
    tableRaws.sort((a, b) => new Date(b.timeStamp) - new Date(a.timeStamp));
    updateUI();
}

function playSound() {
    const audio = new Audio('applepay.mp3');
    audio.play();
}

const rowsPerPage = 8;
let currentPage = 1;
let totalPages = 1;

let prevButton, nextButton, pageInfo, pageInput, goToPageBtn;

document.addEventListener("DOMContentLoaded", () => {
    prevButton = document.getElementById('prev');
   nextButton = document.getElementById('next');
    pageInfo = document.getElementById('page-info');
    pageInput = document.getElementById('page-input');
    goToPageBtn = document.getElementById('go-to-page');

    document.querySelectorAll('.r-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.r-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const rError = document.getElementById("r-error");
            if (rError) rError.style.display = "none";
        });
    });

    if (prevButton) {
        prevButton.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentPage > 1) {
                loadPage(currentPage - 1);
            }
        });
    }

    if (nextButton) {
        nextButton.addEventListener('click', (e) => {
            e.preventDefault();
            if (currentPage < totalPages) {
                loadPage(currentPage + 1);
            }
        });
    }

    if (goToPageBtn) {
        goToPageBtn.addEventListener('click', (e) => {
            e.preventDefault();
            goToInputPage();
        });
    }

    if (pageInput) {
        pageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                goToInputPage();
            }
        });
    }

    initializeTable();
});

function showPageError(message) {
    const errorElement = document.getElementById("page-error");
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = "block";
    }
}

function hidePageError() {
    const errorElement = document.getElementById("page-error");
    if (errorElement) {
        errorElement.textContent = "";
        errorElement.style.display = "none";
    }
}

function goToInputPage() {
    hidePageError();
    if (!validatePageNumber()) {
        return;
    }
    const rawVal = pageInput ? pageInput.value.trim() : "";
    const pageNum = parseInt(rawVal, 10);
    loadPage(pageNum);
}

function validatePageNumber() {
    const rawVal = pageInput ? pageInput.value.trim() : "";

    if (rawVal === "") {
        showPageError("Номер страницы не должен быть пустым!");
        return false;
    }

    if (rawVal.length > 15) {
        showPageError("Длина ввода не должна превышать 15 символов!");
        return false;
    }

    if (!/^\d+$/.test(rawVal)) {
        showPageError("Номер страницы должен быть целым положительным числом!");
        return false;
    }

    const pageNum = parseInt(rawVal, 10);

    if (isNaN(pageNum) || pageNum < 1 || pageNum > totalPages) {
        showPageError(`Номер страницы должен быть в диапазоне от 1 до ${totalPages}!`);
        return false;
    }

    return true;
}

function updateUI() {
    hidePageError();

    const tbody = document.getElementById("tableOfResults").querySelector("tbody");
    tbody.innerHTML = "";

    const totalRecords = tableRaws.length;

    for (let raw of tableRaws) {
        const newRow = tbody.insertRow();
        newRow.insertCell(0).textContent = raw.x;
        newRow.insertCell(1).textContent = raw.y;
        newRow.insertCell(2).textContent = raw.r;
        newRow.insertCell(3).textContent = raw.popadanie;
        newRow.insertCell(4).textContent = formatDateTime(raw.timeStamp);
    }

    if (pageInfo) {
        pageInfo.textContent = `Страница ${currentPage} из ${totalPages}`;
    }
    if (pageInput) {
        pageInput.max = totalPages;
        pageInput.value = currentPage;
    }

    if (prevButton) prevButton.disabled = (currentPage === 1);
    if (nextButton) nextButton.disabled = (currentPage === totalPages || totalRecords === 0);
}

function loadPage(page) {
    logFront("INFO", `Loading table page ${page}`);
    return fetch(`controller-servlet?pageNumber=${page}&pageSize=${rowsPerPage}`)
        .then(resp => resp.json())
        .then(data => {
            if (!data.points) {
                logFront("WARN", `Server did not return points for page ${page}`);
                return;
            }
            logFront("INFO", `Loaded page ${page}/${data.totalPages}, rows: ${data.points.length}`);
            currentPage = page;
            totalPages = data.totalPages;
            tableRaws = data.points.map(p => ({
                x: p.x,
                y: p.y,
                r: p.r,
                popadanie: p.popadanie ? "Попадание" : "Промах",
                timeStamp: p.currentTime.replace(" ", "T"),
                executionTime: p.executionTime
            }));
            updateUI();
        })
        .catch(e => {
            logFront("ERROR", `Failed to load page ${page}: ${e}`);
            updateUI();
        });
}

function drawSavedPoints(page) {
    fetch(`controller-servlet?pageNumber=${page}&pageSize=100`)
        .then(resp => resp.json())
        .then(data => {
            if (!data.points) return;
            logFront("INFO", `Drawing ${data.points.length} saved points (chunk ${page}/${data.totalPages})`);
            for (let p of data.points) {
                drawPoint(p.x, p.y, p.r, p.popadanie);
            }
            if (page < data.totalPages) {
                drawSavedPoints(page + 1);
            }
        })
        .catch(e => logFront("ERROR", `Failed to load saved points: ${e}`));
}

function initializeTable() {
    drawSavedPoints(1);
    loadPage(1);
}