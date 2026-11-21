package com.hackaton.rh.department.repository;

import com.hackaton.rh.department.entity.Department;
import com.hackaton.rh.summary.dto.DepartmentHeadcount;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("select new com.hackaton.rh.summary.dto.DepartmentHeadcount(d.id, d.name, count(e)) "
            + "from Department d left join Employee e on e.departmentId = d.id "
            + "group by d.id, d.name order by d.name")
    List<DepartmentHeadcount> headcountByDepartment();
}
