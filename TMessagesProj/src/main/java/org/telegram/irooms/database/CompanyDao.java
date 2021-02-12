package org.telegram.irooms.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface CompanyDao {

    @Query("select * from tbl_company")
    List<Company> getCompanyList();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void createCompany(Company company);

    @Query("delete from tbl_company")
    void deleteAll();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCompany(Company company);

    @Query("update tbl_company set members=:members where id=:company_id")
    void updateCompanyMembers(int company_id, String members);

    @Query("select * from tbl_company where id=:companyId limit 1")
    Company getCompany(int companyId);

    @Query("delete from tbl_company")
    void deleteCompanies();

}
