package yifan;

import java.util.List;
import java.util.Map;

import yifan.util.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = "ռ �� �� ��";
		String[] ss = s.split(" ");
		for(String t:ss) IOUtils.console(t.length());
	}

}
