package com.stone.mall.member.dao;

import com.stone.mall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author stone
 * @email 330079598@qq.com
 * @date 2021-12-31 21:57:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
