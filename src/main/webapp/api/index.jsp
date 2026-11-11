<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Лабораторная работа №1</title>
    <style>
        body {
            font-family: sans-serif;
            color: #333;
        }

        .error-message {
    color: red;
    font-size: 16px;
    margin-top: 5px;
    display: none;
}


        .header {
            font-family: cursive;
            color: #1a5276;
            font-size: 20px;
            text-align: center;
            padding: 10px;
        }

        .r-btn.active {
    background-color: #2ecc71 !important;
    color: #ffffff !important;
    border: 2px solid #27ae60 !important;
    font-weight: bold;
}

        input[type="text"], select, button {
            padding: 6px 10px;
            margin: 4px 2px;
        }

        form button:hover {
            background-color: #d5f5e3;
            cursor: pointer;
        }

        input[type="text"]:focus {
            border: 2px solid #2980b9;
        }

        input[type="text"]::placeholder {
            color: #999;
            font-style: italic;
        }

        .layout-table {
            width: 100%;
            border-collapse: collapse;
        }

        .layout-table > tbody > tr > td {
            vertical-align: top;
            padding: 15px;
        }
    </style>
</head>
<body>

<table class="layout-table">
    <thead>
        <tr>
            <td colspan="2" class="header">
                ФИО: Кошелев Илья Олегович | Группа: P3210 | Вариант: 12660
            </td>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td style="width: 420px;">
                <canvas id="canvas" width="400" height="400"></canvas>
            </td>
            
            <td>
                <form id="search" name="search" onsubmit="return false;">
                    <div>
                        <label for="selectX">изменение X</label>
                        <div class="error-message" id="x-error">Выберите одно число для X!</div>
                        <select id="selectX" name="X">
                            <option value="" disabled selected>Выберите X</option>
                            <option value="-3">-3</option>
                            <option value="-2">-2</option>
                            <option value="-1">-1</option>
                            <option value="0">0</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                        </select>
                    </div>
                                        
                    <p>
                        <label for="Y">изменение Y</label>
                        <input type="text" required id="Y" name="Y" placeholder="От -5 до 5"/>
                           <div class="error-message" id="y-error">Введите число в диапазоне от -5 до 5</div>
                    </p>

                    <div>
                        <label>изменение R</label>
                        <div class="error-message" id="r-error">Выберите одно число для R!</div>
                        <button type="button" class="r-btn" value="1">1</button>
                        <button type="button" class="r-btn" value="2">2</button>
                        <button type="button" class="r-btn" value="3">3</button>
                        <button type="button" class="r-btn" value="4">4</button>
                        <button type="button" class="r-btn" value="5">5</button>
                    </div>

                    <button type="button" name="Проверить точку" onclick="checkPoint()">Проверить точку</button>                    
                    <button type="button" name="Очистка таблицы" onclick="clearHistory()">Очистка таблицы</button>                    
                    <button type="button" name="Сортировать время по возврастанию" onclick="sortTableByTimeAsc()">Сортировать время по возврастанию</button>
                    <button type="button" name="Сортировать время по убыванию" onclick="sortTableByTimeDesc()">Сортировать время по убыванию</button>

                    <div class="pagination">
                    <button type="button" id="prev">Назад</button>
                    <span id="page-info">Страница 1 из 1</span>
                    <input type="number" id="page-input" min="1" style="width: 50px; margin: 0 5px;">
                    <button type="button" id="go-to-page">Перейти</button>
                    <button type="button" id="next">Вперед</button>
                    
                    <div class="error-message" id="page-error" style="display: none;"></div>
                </div>

                    <table id="tableOfResults" border="1">
                        <thead>
                            <tr>
                                <th>X</th>
                                <th>Y</th>
                                <th>R</th>
                                <th>Результат</th>
                                <th>Время</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </form>
            </td>
        </tr>
    </tbody>
</table>

<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="main.js" defer></script>
</body>
</html>