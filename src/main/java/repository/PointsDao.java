package repository;
import dto.FailledAttempt;
import dto.Point;
import jakarta.servlet.ServletContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PointsDao {
    private final ServletContext servletContext;

    public PointsDao(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public boolean insertPoint(double x, double y, int r, boolean hit, long exec) {
        Point point = new Point(x,y,r, hit, exec);
        List<Point> points = (List<Point>) servletContext.getAttribute("points");
        if (points == null){
            points = new ArrayList<>();
            servletContext.setAttribute("points",points);
            points.add(0,point);
        }
        else {
            boolean flag = points.stream().anyMatch(elem -> elem.id().equals(point.id()));
            if (!flag){
                points.add(0,point);
            }
            else {
                //Дублирование элементов
                return false;
            }
        }
        return true;
    }

    public boolean insertFailedAttempt(String xParam, String yParam, String rParam) {
        FailledAttempt failledAttempt = new FailledAttempt(xParam,yParam,rParam);
        List<FailledAttempt> failledAttempts = (List<FailledAttempt>) servletContext.getAttribute("failledAttempts");
        if (failledAttempts == null){
            failledAttempts = new ArrayList<>();
            servletContext.setAttribute("failledAttempts",failledAttempts);
            failledAttempts.add(0,failledAttempt);
        }
        else {
            boolean flag = failledAttempts.stream().anyMatch(elem -> elem.id().equals(failledAttempt.id()));
            if (!flag){
                failledAttempts.add(0,failledAttempt);
            }
            else {
                //Дублирование элементов
                return false;
            }
        }
        return true;
    }

    public boolean clearPoints() {
        servletContext.setAttribute("points",new ArrayList<>());
        return  true;
    }


    public String getPointsPage(int pageNumber, int pageSize) {
        List<Point> points = (List<Point>) servletContext.getAttribute("points");
        if (points == null || points.isEmpty()) {
            return "[]";
        }
        int offset = (pageNumber - 1) * pageSize;
        if (offset < 0 || offset >= points.size()) {
            return "[]";
        }
        List<Point> pageSlice = points.stream()
                .skip(offset)
                .limit(pageSize)
                .toList();
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Point p : pageSlice) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            String t = p.currentTime().format(formatter);
            sb.append(String.format(
                    java.util.Locale.US,
                    "{\"x\":%.2f,\"y\":%.2f,\"r\":%d,\"popadanie\":%b,\"currentTime\":\"%s\",\"executionTime\":%d}",
                    p.x(), p.y(), p.r(), p.hit(), t, p.executionTime()
            ));
        }
        sb.append("]");
        return sb.toString();
    }


    public int getTotalPages(int pageSize) {
        List<Point> points = (List<Point>) servletContext.getAttribute("points");
        int totalPages = 1;
        if (points==null){
            servletContext.setAttribute("points",new ArrayList<>());
        }
        else {
            int total = points.size();
            totalPages = Math.max(1, (total + pageSize - 1) / pageSize);
        }
        return totalPages;
    }
}