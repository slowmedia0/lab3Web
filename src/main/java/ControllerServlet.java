import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;


@WebServlet(GlobalDefs.pathController)
public class ControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher;
        if (request.getParameter("clear")!=null){
            //Перенаправляем запрос на сервис
            requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathService);
            requestDispatcher.forward(request,response);
        }
        else if (request.getParameter("pageNumber")==null || request.getParameter("pageSize")==null){
            //Возвращаем ответ на фронтенд, что метод GET не хватает параметров
            requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathView);
            requestDispatcher.forward(request,response);
        }
        else if (request.getParameter("pageNumber")!=null && request.getParameter("pageSize")!=null){
            //Soft валидация параметров
            String pageNumberStr =  request.getParameter("pageNumber");
            String pageSizeStr = request.getParameter("pageSize");
            if (!pageNumberStr.isBlank() && !pageSizeStr.isBlank()) {
                //Перенаправляем запрос на сервис
                requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathService);
                requestDispatcher.forward(request,response);
            } else {
                //Возвращаем ответ на фронтенд, что метод GET имеет хотя бы один blank параметр
                requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathView);
                requestDispatcher.forward(request, response);
            }
        }
        else {
            //Возвращаем ответ на фронтенд, что метод GET с другими параметрами не поддерживается
            requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathView);
            requestDispatcher.forward(request,response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher requestDispatcher;
        if (request.getParameter("x")==null || request.getParameter("y")==null || request.getParameter("r")==null){
            //Возвращаем ответ на фронтенд, что метод POST не хватает параметров
            requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathView);
            requestDispatcher.forward(request,response);
        } else if (request.getParameter("x")!=null && request.getParameter("y")!=null && request.getParameter("r")!=null){
            //Soft валидация параметров
            String xStr =  request.getParameter("x");
            String yStr = request.getParameter("y");
            String rStr = request.getParameter("r");
            if (!xStr.isBlank() && !yStr.isBlank() && !rStr.isBlank()) {
                //Перенаправляем запрос на сервис
                requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathService);
                requestDispatcher.forward(request,response);
            } else {
                //Возвращаем ответ на фронтенд, что метод POST имеет хотя бы один blank параметр
                requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathView);
                requestDispatcher.forward(request, response);
            }
        }
        else {
            //Возвращаем ответ на фронтенд, что метод POST с другими параметрами не поддерживается
            requestDispatcher = request.getRequestDispatcher(GlobalDefs.pathView);
            requestDispatcher.forward(request,response);
        }
    }
}


