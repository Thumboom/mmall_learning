package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {


    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if( resultCount == 0 ) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user  = userMapper.selectLogin(username,md5Password);

        if( user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        //重置密码为空，防止泄露
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功", user);
    }


    public ServerResponse<String> register(User user){
        ServerResponse<String> validResponse = checkValid(user.getUsername(), Const.USERNAME);
        if( !validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = checkValid(user.getEmail(), Const.EMAIL);
        if( !validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        // md5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if( resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");

    }



    public ServerResponse<String>  checkValid(String str, String type){
        if( StringUtils.isNotBlank(type)){

            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if( resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }

            if( Const.EMAIL.equals(type)){

                int resultCount = userMapper.checkEmail(str);
                if( resultCount > 0 ){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("检验成功");
    }


    public ServerResponse<String> selectQuestion(String username){

        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if( validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
       String question = userMapper.selectQuestionByUsername(username);

        if( StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("该用户的问题为空");
    }


    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);

        if( resultCount == 0) {
            return ServerResponse.createByErrorMessage("回答错误");
        }
        //使用token，标记该客户为已正确回答相应问题
        String forgetToken = UUID.randomUUID().toString();
        RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX  + username, forgetToken, 60 * 60 * 12);
        return ServerResponse.createBySuccess(forgetToken);

    }
    //未登录状态下的更改密码，需要传入token来检验用户是否已经正确回答相应问题
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误， token不能为空");
        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if( validResponse.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = RedisShardedPoolUtil.get(Const.TOKEN_PREFIX + username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if( StringUtils.equals(forgetToken, token)){
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if( resultCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorMessage("token错误，请重新获取token");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    //登录状态下更改密码
    public ServerResponse<String> resetPassword(String passwordNew, String passwordOld, User user){
        //校验用户名和密码，防止横向越权
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if( resultCount == 0 ) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if( updateCount > 0){
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");

    }

    public ServerResponse<User> updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if( resultCount > 0){
            return ServerResponse.createByErrorMessage("当前邮箱已存在");
        }

        User updateUser = new User();

        updateUser.setId(user.getId());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if( updateCount > 0){
            return ServerResponse.createBySuccess("更新信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");

    }

    public ServerResponse<User> getInformation(Integer id){
        User user = userMapper.selectByPrimaryKey(id);
        if( user == null){
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        return ServerResponse.createBySuccess(user);
    }

    //backend

    /**
     * 校验是否是管理员
     * @param user
     * @return
     */
    public ServerResponse checkAdminRole(User user){
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


}
