package com.mmall.controller.backend;


import com.mmall.common.ServerResponse;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value="parentId",defaultValue = "0") Integer parentId){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if( StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if( user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");
//        }
//        if( iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.addCategory(categoryName, parentId);
//        } else {
//            return ServerResponse.createByErrorMessage("不是管理员，没有权限操作");
//        }
        //交给springmvc 拦截器
        return iCategoryService.addCategory(categoryName, parentId);
    }
    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest request, Integer categoryId, String categoryName){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if( StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if( user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");
//        }
//        if( iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.updateCategoryName(categoryId, categoryName);
//        } else {
//            return ServerResponse.createByErrorMessage("不是管理员，没有权限操作");
//        }

        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpServletRequest request,
                                                      @RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if( StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if( user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");
//        }
//        if( iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.getChildrenParallelCategory(categoryId);
//        } else {
//            return ServerResponse.createByErrorMessage("不是管理员，没有权限操作");
//        }
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
//        String loginToken = CookieUtil.readLoginToken(request);
//        if( StringUtils.isEmpty(loginToken)){
//            return ServerResponse.createByErrorMessage("用户未登录");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = JsonUtil.string2Obj(userJsonStr, User.class);
//        if( user == null){
//            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");
//        }
//        if( iUserService.checkAdminRole(user).isSuccess()){
//            return iCategoryService.selectCategoryAndChildrenById(categoryId);
//        } else {
//            return ServerResponse.createByErrorMessage("不是管理员，没有权限操作");
//        }
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }



}
