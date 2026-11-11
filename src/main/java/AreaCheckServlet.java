import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import repository.PointsDao;
import tools.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@WebServlet(GlobalDefs.pathService)
public class AreaCheckServlet extends HttpServlet {
    private PointsDao repository;
    private Validator validator;

    @Override
    public void init() throws ServletException {
        repository = new PointsDao(getServletContext());
        validator = new Validator();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("clear")!=null){
            if (repository.clearPoints()){
                sendJsonResponse(response, 200, "{\"cleared\": true}");
            }
            else {
                sendJsonResponse(response, 500, "{\"error\": \"Storage error!\"}");
            }
        }
        else if (request.getParameter("pageNumber")!=null && request.getParameter("pageSize")!=null){
            String pageNumberStr =  request.getParameter("pageNumber");
            String pageSizeStr = request.getParameter("pageSize");

            boolean isValid = validator.validatePageDbNumberStr(pageNumberStr) &&
                    validator.validatePageDbSizeStr(pageSizeStr);

            if (!isValid) {
                sendJsonResponse(response, 400, "{\"validatePageParams\": false}");
                return;
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

            sendJsonResponse(response, 200, jsonResponse);
        }
        else {
            sendJsonResponse(response, 400, "{\"error\": \"Unsupported parameters\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("x")!=null && request.getParameter("y")!=null && request.getParameter("r")!=null){
            long startTime = System.nanoTime();
            String xStr =  request.getParameter("x");
            String yStr = request.getParameter("y");
            String rStr = request.getParameter("r");

            boolean isValid = validator.validateX(xStr)
                    && validator.validateY(yStr)
                    && validator.validateR(rStr);

            String currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();

            if (!isValid) {
                long executionTime = System.nanoTime() - startTime;

                repository.insertFailedAttempt(xStr, yStr, rStr);

                String jsonResponse = String.format(
                        "{\"validatePoint\": false, \"popadanie\": false, \"currentTime\": \"%s\", \"executionTime\": \"%d ns\"}",
                        currentTime, executionTime
                );
                sendJsonResponse(response, 400, jsonResponse);
                return;
            }

            double x, y;
            int r;
            x = Double.parseDouble(xStr.trim().replace(",", "."));
            y = Double.parseDouble(yStr.trim().replace(",", "."));
            r = Integer.parseInt(rStr.trim());

            boolean isHit = checkArea(x, y, r);
            long executionTime = System.nanoTime() - startTime;

            if (repository.insertPoint(x, y, r, isHit, executionTime)){
                String jsonResponse = String.format(
                        "{\"validatePoint\": %b, \"popadanie\": %b, \"currentTime\": \"%s\", \"executionTime\": \"%d ns\"}",
                        isValid, isHit, currentTime, executionTime
                );
                sendJsonResponse(response, 200, jsonResponse);
            }
            else {
                sendJsonResponse(response, 500, "{\"error\": \"Storage error!\"}");
            }
        }
        else {
            sendJsonResponse(response, 400, "{\"error\": \"x, y and r are required\"}");
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int status, String json) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }


    private static boolean checkArea(double x, double y, double r) {
        // Прямоугольник
        if (x >= 0 && x <= r / 2 && y >= 0 && y <= r) {
            return true;
        }
        // Четверть окружности
        if (x >= 0 && y <= 0 && (Math.pow(x,2)+Math.pow(y,2)<=Math.pow(r / 2,2))){
            return true;
        }
        // Треугольник
        if (x >= -r && x <= 0 && y >= 0 && y<=r && y <= (x + r)) {
            return true;
        }
        return false;
    }
}