package com.windaka.suizhi.manageport.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
* @Description: 车辆出入场记录
* @Author: Ms.wcl
* @Date: 2019/8/22 0022
*/
@Mapper
public interface CarAccessRecordDao {

	/**
	 * 新增车辆出入场记录
	 * @param xqCode
	 * @param carAccessRecord
	 * @return
	 */
	int saveCarAccessRecord(@Param("xqCode") String xqCode, @Param("carAccessRecord") Map<String,Object> carAccessRecord);
	/**
	 * 功能描述: 获取最大ID值
	 * @auther: lixianhua
	 * @date: 2019/12/8 8:30
	 * @param:
	 * @return:
	 */
	int getMaxRecordId();
}
