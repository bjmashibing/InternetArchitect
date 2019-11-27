package com.mashibing.arica.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.mashibing.arica.entity.Item;
import com.mashibing.arica.entity.ItemExample;
import com.mashibing.arica.mapper.ItemDAO;

@Service
public class ItemService  {

	@Autowired
	ItemDAO itemDao;
	@Value(value = "${nginx.html.root}")
	String htmlRoot;
	
	public Item insert(Item item) {

		itemDao.insert(item);
		return item;
	}

	public Item findById(int id) {
		return itemDao.selectByPrimaryKey(id);		
	}

	public List<Item> findAll() {
		ItemExample example = new ItemExample();
		return itemDao.selectByExample(example );
	}

	public void generateHtml(int id) {

		// 初始化模板引擎
		Engine engine = Engine.use();
		engine.setDevMode(true);
		engine.setToClassPathSourceFactory();
		
		// 从数据源，获取数据
		Item item = itemDao.selectByPrimaryKey(id);
		//c:/dev/uploads/
		
		// 前端模板用的键值对
		Kv kv = Kv.by("item", item);
		
		// 文件写入路径
		String fileName = "item"+id+".html";
		String filePath = htmlRoot;
		// 路径 直接能被用户访问
		File file = new File(filePath+fileName);
		
		// 开始渲染 输出文件
		Template template = engine.getTemplate("templates/item.html");
		template.render(kv, file);
	}

	public String getFileTemplateString() throws Exception{
		// TODO Auto-generated method stub
		
		// tomcat 下部署项目，可以这么取
		// 获取 静态模板文件的输入流
		String file = ClassUtils.getDefaultClassLoader().getResource("templates/item.html").getFile();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		
		// 读缓冲区
		StringBuffer sb = new StringBuffer();
		
		String lineStr = reader.readLine();
		while (lineStr!=null) {
			sb.append(lineStr).append("\r\n");
			lineStr = reader.readLine();
		}
		reader.close();

		return sb.toString();
	}

	public void saveFileTemplateString(String content)throws Exception {
		
		String file = ClassUtils.getDefaultClassLoader().getResource("templates/item.html").getFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(content);
		writer.flush();
		writer.close();
	}

}
