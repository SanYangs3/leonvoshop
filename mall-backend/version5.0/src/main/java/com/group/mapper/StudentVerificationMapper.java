package com.group.mapper;

import com.group.entity.StudentVerification;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StudentVerificationMapper {

    // 插入申请记录
    @Insert("INSERT INTO student_verification (uid, school, student_id, card_image, status, create_time, update_time) " +
            "VALUES (#{uid}, #{school}, #{studentId}, #{cardImage}, #{status}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(StudentVerification verification);

    // 获取用户的最新申请状态
    @Select("SELECT * FROM student_verification WHERE uid = #{uid} ORDER BY create_time DESC LIMIT 1")
    StudentVerification getByUid(Integer uid);

    // 根据状态获取列表（待审核/全部）
    @Select("SELECT * FROM student_verification WHERE status = #{status} ORDER BY create_time DESC")
    List<StudentVerification> getByStatus(Integer status);

    // 获取所有记录
    @Select("SELECT * FROM student_verification ORDER BY create_time DESC")
    List<StudentVerification> getAll();

    // 更新审核状态
    @Update("UPDATE student_verification SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    void updateStatus(Integer id, Integer status, LocalDateTime updateTime);

    // 审核通过后，更新用户身份表
    @Update("UPDATE identity SET student = 1 WHERE uid = #{uid}")
    void updateIdentityStudentStatus(Integer uid);

    // 统计待审核数量
    @Select("SELECT COUNT(*) FROM student_verification WHERE status = 0")
    Integer countPending();
}