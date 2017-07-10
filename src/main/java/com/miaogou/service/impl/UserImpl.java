package com.miaogou.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.miaogou.dao.IUserDao;
import com.miaogou.service.IUserService;

/**
 * 
 * @author weicc
 *
 */

@Service("UserService")
public class UserImpl implements IUserService{

   
		@Resource
	    IUserDao userDao;
        
		
		
		
}
