package com.fily.activiti.api.util;

import java.lang.reflect.Field;
import java.util.List;

import org.activiti.engine.history.HistoricVariableInstance;

import com.fily.activiti.api.entity.ProcessStatus;


/**
 * activiti中使用得到的工具方法
 * @author Created by yawn on 2018-01-10 16:32
 */
public class ActivitiUtil {

    /**
     * 将历史参数列表设置到实体中去
     * @param entity 实体
     * @param varInstanceList 历史参数列表
     */
    public static <T> void setVars(T entity, List<HistoricVariableInstance> varInstanceList) {
        Class<?> tClass = entity.getClass();
        try {
            for (HistoricVariableInstance varInstance : varInstanceList) {
                Field field = tClass.getDeclaredField(varInstance.getVariableName());
                if (field == null) {
                    continue;
                }
                field.setAccessible(true);
                field.set(entity, varInstance.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void main(String[] args) {
//		System.out.println(ProcessStatus.FINISHED.getName());
//	}
}
