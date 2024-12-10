package com.yhzhang.mianshiya.model.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yhzhang.mianshiya.common.PageRequest;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 *
 * @author <a href="https://github.com/WeChat098">程序员yhzhang</a>

 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    /**
     * 微信开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;
    private String userProfile;

    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;



    private static final long serialVersionUID = 1L;
}