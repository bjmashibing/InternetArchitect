package org.ormtest.step050;

import org.ormtest.step050.entity.AbstractEntityHelper;
import org.ormtest.step050.entity.EntityHelperFactory;
import org.ormtest.step050.entity.UserEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 主应用程序类
 */
public class App050 {
    /**
     * 应用程序主函数
     *
     * @param argvArray 参数数组
     * @throws Exception
     */
    static public void main(String[] argvArray) throws Exception {
        (new App050()).start();
    }

    /**
     * 测试开始
     */
    private void start() throws Exception {
        // 加载 Mysql 驱动
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        // 数据库连接地址
        String dbConnStr = "jdbc:mysql://localhost:3306/ormtest?user=root&password=root";
        // 创建数据库连接
        Connection conn = DriverManager.getConnection(dbConnStr);
        // 简历陈述对象
        Statement stmt = conn.createStatement();

        // 创建 SQL 查询
        String sql = "select * from t_user limit 200000";

        // 执行查询
        ResultSet rs = stmt.executeQuery(sql);

        // 创建助手类, 这里采用全新设计的工厂类!
        AbstractEntityHelper helper = EntityHelperFactory.getEntityHelper(UserEntity.class);
        // 读懂上面这一行,
        // 恭喜你已经迈入架构师行列...

        // 获取开始时间
        long t0 = System.currentTimeMillis();

        while (rs.next()) {
            // 创建新的实体对象
            UserEntity ue = (UserEntity) helper.create(rs);
        }

        // 获取结束时间
        long t1 = System.currentTimeMillis();

        // 关闭数据库连接
        stmt.close();
        conn.close();

        // 打印赋值时间消耗
        System.out.println("赋值花费时间 = " + (t1 - t0) + "ms");
    }
}
