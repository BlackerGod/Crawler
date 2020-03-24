package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.Project;
import dao.ProjectDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.List;

@WebServlet("/AllRankServlet")

//基于多态的语法来实现的，Servlet框架就能提供一些时机，让用户插入自己的罗技来完成一些工作
//对于用户来说，并不需要了解Servlet内部是怎么工作的，只需要知道针对一个GET/POTS请求处理即可
public class AllRankServlet extends HttpServlet {

    private Gson gson = new GsonBuilder().create();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        String date = req.getParameter("date");
        if(date == null || date.equals("")){
            resp.setStatus(404);
            resp.getWriter().write("date参数错误");
            return;
        }
        ProjectDao projectDao = new ProjectDao();
        List<Project> projects = projectDao.selectProjectByDate(date);
        String respString = gson.toJson(projects);
        resp.getWriter().write(respString);
        return;

    }
}
