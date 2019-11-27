package com.mashibing.arica.service;

import com.jfinal.template.Env;
import com.jfinal.template.Template;
import com.jfinal.template.io.FastStringWriter;
import com.jfinal.template.stat.ast.Stat;

public class MyTemplate extends Template{

	public MyTemplate(Env env, Stat ast) {
		super(env, ast);
	}
	@Override
	public String toString() {
		FastStringWriter fw = new FastStringWriter();

		StringBuilder stringBuilder = fw.toStringBuilder();
		return "1:" + stringBuilder.toString();
	}

}
