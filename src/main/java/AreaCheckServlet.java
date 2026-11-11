import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.PointsDao;
import tools.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(GlobalDefs.pathService)
public class AreaCheckServlet extends HttpServlet {
    private PointsDao repository;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("clear")!=null){
            if (repository.clearPoints()){
                // Возвращаем ответ на фронтенд об успехе
                // sendJsonResponse("{\"cleared\": true}");
            }
            else {
                // Возвращаем ответ на фронтенд о неудаче
                //sendJsonResponse(500, "Internal Server Error", "{\"error\": \"JDBC-query is not correct!\"}");
            }
        }
        else if (request.getParameter("pageNumber")!=null && request.getParameter("pageSize")!=null){
            String pageNumberStr =  request.getParameter("pageNumber");
            String pageSizeStr = request.getParameter("pageSize");

            boolean isValid = Validator.validatePageDbSizeStr(pageNumberStr) &&
                    Validator.validatePageDbNumberStr(pageSizeStr);

            if (!isValid) {
                /* Возвращаем ответ фронту о ошибки валидации
                String jsonResponse = String.format(
                        "{\"validatePageParams\": false}"
                );
                sendJsonResponse(400, "Bad Request", jsonResponse);
                 */
            }

            int pageNumber, pageSize;
            pageNumber = Integer.parseInt(pageNumberStr.trim().replace(",", "."));
            pageSize = Integer.parseInt(pageSizeStr.trim().replace(",", "."));

            int totalPages = repository.getTotalPages(pageSize);
            String pointsJson = repository.getPointsPage(pageNumber, pageSize);

            String jsonResponse = String.format(
                    "{\"totalPages\":%d,\"points\":%s}",
                    totalPages, pointsJson
            );

            /* Возвращаем ответ на фронтенд об успехе
            sendJsonResponse(jsonResponse);
             */
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("x")!=null && request.getParameter("y")!=null && request.getParameter("r")!=null){
            long startTime = System.nanoTime();
            String xStr =  request.getParameter("x");
            String yStr = request.getParameter("y");
            String rStr = request.getParameter("r");

            boolean isValid = Validator.validateX(xStr)
                    && Validator.validateY(yStr)
                    && Validator.validateR(rStr);

            if (!isValid) {
                long executionTime = System.nanoTime() - startTime;
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                repository.insertFailedAttempt(xStr, yStr, rStr);
                /*
                String jsonResponse = String.format(
                        "{\"validatePoint\": false, \"popadanie\": false, \"currentTime\": \"%s\", \"executionTime\": \"%d ns\"}",
                        currentTime, executionTime
                );
                sendJsonResponse(400, "Bad Request", jsonResponse);
                 */
            }

            double y;
            int x, r;
            x = Integer.parseInt(rStr.trim().replace(",", "."));
            y = Double.parseDouble(yStr.trim().replace(",", "."));
            r = Integer.parseInt(rStr.trim().replace(",", "."));

            boolean isHit = checkArea(x, y, r);
            long executionTime = System.nanoTime() - startTime;

            String jsonResponse;
            if (repository.insertPoint(x, y, r, isHit, executionTime)){
                /*
                jsonResponse = String.format(
                        "{\"validatePoint\": %b, \"popadanie\": %b, \"currentTime\": \"%s\", \"executionTime\": \"%d ns\"}",
                        isValid, isHit, currentTime, executionTime
                );
                sendJsonResponse(jsonResponse);
                 */
            }
            else {
                /*
                sendJsonResponse(400, "Bad Request", "{\"error\": \"JDBC-query is not correct!\"}");
                 */
            }
        }
    }

    private static boolean checkArea(double x, double y, double r) {
        // Прямоугольник
        if (x >= 0 && x <= r / 2 && y >= 0 && y <= r) {
            return true;
        }
        // Четверть окружности
        if ()
        // Треугольник
        if (x >= -r && x <= 0 && y >= 0 && y<=r && y <= (x + r)) {
            return true;
        }
        return false;
    }
}
