package com.akbank.wm.middleware.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.akbank.wm.middleware.core.entity.JobDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

@Repository
public interface JobDefinitionRepository extends JpaRepository<JobDefinition, UUID> {
      @Query(value = "SELECT *, 'null' as Status " +
                  "FROM (SELECT * FROM core.JOB_DEFINITIONS jd WHERE ((jd.WorkInHoliday = 'Y' and ?5='Y'  ) OR (jd.WorkInWeekend = 'Y' and ?4='Y') OR ?6 = 'Y') AND jd.RecordStatus = 'A') j "
                  +
                  "WHERE (j.JobType = 'Periodic'  AND ((?1-SUBSTRING(j.StartHour, 1, 2)*60-SUBSTRING(j.StartHour, 3, 2)) % j.PeriodicIntervalMinutes) = 0 AND j.StartHour < CAST(?2 AS VARCHAR) AND j.EndHour > CAST(?2 AS VARCHAR)) OR "
                  +
                  "((j.Times LIKE '%' + CAST(?2 AS VARCHAR) + '%' or j.JobID in (Select lg.JobID from core.JOB_EXECUTION_LOGS lg "
                  +
                  "WHERE lg.JobID = j.JobID " +
                  "AND lg.ReferenceDate = ?3 " +
                  "AND lg.Status = 'Waiting' And lg.RecordStatus = 'A') ) AND " +

                  "((j.JobType = 'Monthly' AND ((j.FirstWorkingDayOfMonth = 'Y' and ?7='Y'  ) OR (j.LastWorkingDayOfMonth = 'Y' and ?8='Y'  ) OR (j.DaysOfMonth LIKE '%' + CAST(?11 AS VARCHAR) + '%' AND j.MonthList LIKE '%' + CAST(?12 AS VARCHAR) + '%'))) OR "
                  +
                  "(j.JobType = 'Weekly' AND ((j.FirstWorkingDayOfWeek = 'Y' and ?9='Y'  ) OR (j.LastWorkingDayOfWeek = 'Y' and ?10='Y'  ) OR (j.DaysOfWeek LIKE '%' + CAST(?13 AS VARCHAR) + '%'))) "
                  +
                  "OR j.JobType = 'Daily') " +
                  "AND NOT EXISTS (" +
                  "SELECT 1 " +
                  "FROM core.JOB_EXECUTION_LOGS l " +
                  "WHERE l.JobID = j.JobID And " +
                  "FORMAT( cast(StartTime as datetime), 'HHmm')>=(SELECT top(1) value FROM STRING_SPLIT((select Times FROM [core].[JOB_DEFINITIONS] where JobID=l.JobID), ',') order by value desc)"
                  +
                  "AND l.ReferenceDate = ?3 " +
                  "AND l.Status = 'Completed' And l.RecordStatus = 'A')) " +
                  "ORDER BY j.Sequence", nativeQuery = true)
      List<JobDefinition> findActiveJobDefinitions(int nowInMinutes,
                  String hour,
                  LocalDate today,
                  String isWeekEnd,
                  String isHoliday,
                  String isWorkingDay,
                  String firstDayOfMonth,
                  String lastDayOfMonth,
                  String firstDayOfWeek,
                  String lastDayOfWeek,
                  int date,
                  int month,
                  String dayOfWeek);

      @Query(value = "SELECT j.*, (select Top(1) jel.Status FROM [core].[JOB_EXECUTION_LOGS] jel " +
                  "WHERE jel.ReferenceDate=?11 and jel.JobID=j.JobID order by jel.RecordDatetime desc) as Status " +
                  "FROM (SELECT * FROM core.JOB_DEFINITIONS jd WHERE ((jd.WorkInHoliday = 'Y' and ?2='Y'  ) OR (jd.WorkInWeekend = 'Y' and ?1='Y') OR ?3 = 'Y') AND jd.RecordStatus = 'A') j "
                  +
                  "WHERE " +
                  "(j.JobType = 'Periodic' Or (j.JobType = 'Monthly' AND ((j.FirstWorkingDayOfMonth = 'Y' and ?4='Y'  ) OR (j.LastWorkingDayOfMonth = 'Y' and ?5='Y'  ) OR (j.DaysOfMonth LIKE '%' + CAST(?8 AS VARCHAR) + '%' AND j.MonthList LIKE '%' + CAST(?9 AS VARCHAR) + '%'))) OR "
                  +
                  "(j.JobType = 'Weekly' AND ((j.FirstWorkingDayOfWeek = 'Y' and ?6='Y'  ) OR (j.LastWorkingDayOfWeek = 'Y' and ?7='Y'  ) OR (j.DaysOfWeek LIKE '%' + CAST(?10 AS VARCHAR) + '%'))) "
                  +
                  "OR j.JobType = 'Daily')  ORDER BY j.Sequence", nativeQuery = true)
      List<JobDefinition> findAllActiveJobsWithLastStatus(String isWeekEnd,
                  String isHoliday,
                  String isWorkingDay,
                  String firstDayOfMonth,
                  String lastDayOfMonth,
                  String firstDayOfWeek,
                  String lastDayOfWeek,
                  int date,
                  int month,
                  String dayOfWeek,
                  LocalDate today);

      @Query(value = "SELECT *, 'null' as Status FROM core.JOB_DEFINITIONS where JobID=?1", nativeQuery = true)
      Optional<JobDefinition> findByJobID(Integer jobId);

}
