package dao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 获取数据库的连接
 * */
public class DBUtil {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/gitproject?characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static volatile DataSource dataSource = null;

    private static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DBUtil.class) {
                dataSource = new MysqlDataSource();
                MysqlDataSource mysqlDataSource = (MysqlDataSource) dataSource;
                mysqlDataSource.setURL(URL);
                mysqlDataSource.setUser(USER);
                mysqlDataSource.setPassword(PASSWORD);
            }
        }
        return dataSource;
    }

    public static Connection getConnection(){
        try{
        return getDataSource().getConnection();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection connection , PreparedStatement preparedStatement,ResultSet resultSet){
        try {
            if(resultSet != null) {
                resultSet.close();
            }
            if(preparedStatement != null) {
                preparedStatement.close();
            }
            if(connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
