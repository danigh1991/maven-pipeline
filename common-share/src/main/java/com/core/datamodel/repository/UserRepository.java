package com.core.datamodel.repository;


import com.core.datamodel.model.dbmodel.User;
import com.core.model.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface UserRepository extends BaseRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByEmail(String email);

    @Query("select u from User u where u.id=?1")
    User findByEntityId(Long id);


    @Query("select CASE WHEN count(u)>0 THEN true ELSE false END from User u where u.username=:username")
    Boolean existsByUsername(@Param("username") String username);

    @Query("select count(u) from User u ")
    Integer countAll();

    @Query("select u from User u order by u.id desc")
    List<User> findAll();

    @Query(value = "SELECT * FROM sc_user u \n" +
                   "INNER JOIN sc_user_roles ur ON (u.usr_id=ur.url_usr_id)\n" +
                   "INNER JOIN sc_roles r ON(ur.url_rol_id=r.rol_id)\n" +
                   "WHERE r.rol_name=:role ",nativeQuery = true)
    List<User> getUsersByRole(@Param("role") String role);

    @Query(value = "SELECT * FROM sc_user u \n" +
            "INNER JOIN sc_user_roles ur ON (u.usr_id=ur.url_usr_id)\n" +
            "INNER JOIN sc_roles r ON(ur.url_rol_id=r.rol_id)\n" +
            "WHERE r.rol_name=:role AND u.usr_id=:userId",nativeQuery = true)
    User getUserByRole(@Param("role") String role,@Param("userId") Long userId);


    @Query("select u from User u where u.affiliateCode=:affiliateCode")
    User findByAffiliateCode(@Param("affiliateCode") String affiliateCode);


    @Query(value = "SELECT Distinct uaf.* FROM sc_store  s \n" +
            "INNER JOIN sc_user u ON (s.str_usr_id=u.usr_id)\n" +
            "INNER JOIN sc_user uaf ON (u.usr_affiliate_reagent=uaf.usr_affiliate_code) \n" +
            "INNER JOIN sc_user muaf ON (uaf.usr_affiliate_reagent=muaf.usr_affiliate_code)\n" +
            "WHERE muaf.usr_id=:headUserId",nativeQuery = true)
    List<User> findAffiliateUserByHeaderId(@Param("headUserId") Long headUserId);

    @Query(value = "SELECT Distinct uaf.* FROM sc_store  s \n" +
            "INNER JOIN sc_user u ON (s.str_usr_id=u.usr_id)\n" +
            "INNER JOIN sc_user uaf ON (u.usr_affiliate_reagent=uaf.usr_affiliate_code) " , nativeQuery = true)
    List<User> findAllAffiliateUser();


    @Modifying
    @Query(value = "update User set lastLogin=:loginDate where id=:userId")
    Integer modifyLastLoginDate(@Param("userId") Long userId, @Param("loginDate") Date loginDate);


}
