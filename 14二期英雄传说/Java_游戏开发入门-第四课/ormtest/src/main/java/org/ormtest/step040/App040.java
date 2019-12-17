package org.ormtest.step040;

import org.ormtest.step040.entity.AbstractEntityHelper;
import org.ormtest.step040.entity.EntityHelperFactory;
import org.ormtest.step040.entity.UserEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 主应用程序类
 */
public class App040 {
    /**
     * 应用程序主函数
     *
     * @param argvArray 参数数组
     * @throws Exception
     */
    static public void main(String[] argvArray) throws Exception {
        (new App040()).start();
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

        // 创建助手类, 这里采用全新设计的工厂类
        AbstractEntityHelper helper = EntityHelperFactory.getEntityHelper(UserEntity.class);

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

        // 打印实例化花费时间
        System.out.println("实例化花费时间 = " + (t1 - t0) + "ms");
        // 注意: 到这里运行时间翻倍了!
        // 利用反射确实可以获得良好的通用性,
        // 但是却损失了性能...
    }
}
