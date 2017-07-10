package com.miaogou.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author weicc
 *
 */
public class CheckLoginFilter implements Filter{
	
	private String excludedPages;       
	private String[] excludedPageArray; 

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		//获取排除的列表
		excludedPages = fConfig.getInitParameter("excludedPages");     
		if (StringUtils.isNotEmpty(excludedPages)) {     
		excludedPageArray = excludedPages.split(",");     
		} 
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		//获取完整url
		/*String url=((HttpServletRequest)request).getRequestURI();
		Boolean isExcludedPage=false;
		
		//一些特定的请求不过滤  例如登录 或者一些静态文件的访问
		if(excludedPageArray!=null&&excludedPageArray.length>0){
			for(String t:excludedPageArray){
				if(url.indexOf(t)>0){
					isExcludedPage=true;
				}
					
			}
		}
		Object user=((HttpServletRequest)request).getSession().getAttribute("loginInfo");
		if(!isExcludedPage&&user==null){
		
			response.setCharacterEncoding("UTF-8");  
		    response.setContentType("application/json; charset=utf-8"); 
		    
		    JSONObject obj=new JSONObject();
		    obj.put("errcode", "-1");
		    obj.put("errmsg", "请登陆");
			PrintWriter out=response.getWriter();
			out.append(obj.toString()); 
		}else{
			chain.doFilter(request, response);
		}*/
		
		chain.doFilter(request, response);
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
