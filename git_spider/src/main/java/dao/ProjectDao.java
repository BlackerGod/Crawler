package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 把Project对象进行数据操作
 */
public class ProjectDao {

    /**
     * 把一个Project保存到数据可
     * @param project
     */
    public void save(Project project){
        Connection connection = DBUtil.getConnection();
        PreparedStatement preparedStatement = null;
        String sql = "insert into project_table values(?,?,?,?,?,?,?);";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,project.getName());
            preparedStatement.setString(2,project.getUrl());
            preparedStatement.setString(3,project.getDescription());
            preparedStatement.setInt(4,project.getStarCount());
            preparedStatement.setInt(5,project.getForkCount());
            preparedStatement.setInt(6,project.getOpenIssueCount());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            preparedStatement.setString(7,simpleDateFormat.format(System.currentTimeMillis()));
            //new Date().getTime())
            int ret = preparedStatement.executeUpdate();
            if(ret != 1){
                System.out.println("当前数据库执行插入数据出错");
                return;
            }
            //System.out.println("数据插入成功");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,null);
        }
    }

    /**
     *
     * @param date 查找某天的项目数据
     * @return 返回项目列表
     */
    public List<Project> selectProjectByDate(String date){

        List<Project> projects = new ArrayList<>();
        Connection connection = DBUtil.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql = "select name,url,starCount,forkCount,openIssueCount " +
                       "from project_table where date = ? order by starCount desc;";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,date);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                Project project = new Project();
                project.setName(resultSet.getString("name"));
                project.setUrl(resultSet.getString("url"));
                project.setStarCount(resultSet.getInt("starCount"));
                project.setForkCount(resultSet.getInt("forkCount"));
                project.setOpenIssueCount(resultSet.getInt("openIssueCount"));
                projects.add(project);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.close(connection,preparedStatement,resultSet);
        }
        return projects;

    }

    public static void main(String[] args) {
        ProjectDao dao = new ProjectDao();
        System.out.println(dao.selectProjectByDate("20200321"));
    }
}
