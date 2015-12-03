/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sigma.codaclient.services.year;

import com.coda.efinance.schemas.common.GlobalKey;
import com.coda.efinance.schemas.company.Company;
import com.sigma.codaclient.codarouter.CodaRouter;
import com.coda.efinance.schemas.year.*;
import com.sigma.codaclient.codarouter.CodaLogicalServerType;
import com.sigma.codaclient.codarouter.CodaObjectStore;
import com.sigma.codaclient.db.CodaDbConnection;
import com.sigma.codaclient.services.common.CodaGenericService;
import com.sigma.codaclient.services.common.CodaGlobalKey;
import com.sigma.codaclient.services.common.CodaServiceDefinition;
import com.sigma.codaclient.services.company.CompanyPropertyProvider;
import com.sigma.codaclient.util.DateTools;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author clance
 */
public class YearService {
  private final CodaRouter router;
  private final CodaDbConnection codaDbConn;
  
  public class PeriodInterval {
    public static final int PERIOD_MONTHS = 179;
    public static final int PERIOD_MONTHEND = 180;
    public static final int PERIOD_DAYS = 181;
    public static final int PERIOD_454 = 182;
    public static final int PERIOD_445 = 183;
    public static final int PERIOD_544 = 184;
  }
  
  public YearService(CodaRouter router, CodaDbConnection dbConn) throws Exception {
    if (router == null)
      throw new Exception("Invalid value (null) provided for router parameter");
    this.router = router;
      
    if (dbConn == null)
      throw new Exception("Invalid value (null) provided for dbConn parameter");
    this.codaDbConn = dbConn;
  }
  
  private Year getYear(String companyCode, int yearNumber) throws Exception {
    
    Connection conn = codaDbConn.getConnection();
    String schema = codaDbConn.getUsername();
    Statement stmt = conn.createStatement();
    
    Year year = new Year();
    String cmpSql = String.format("SELECT CODE, YRBASE, YRMIN, YRMAX FROM %s.OAS_COMPANY WHERE CODE='%s' AND DELDATE IS NULL", schema, companyCode);
    ResultSet cmpRs = stmt.executeQuery(cmpSql);
    if (!cmpRs.next())
      throw new Exception("Failed to retrieve Company Master for " + companyCode);
    fillYearRange(cmpRs, year);
    String yearSql = String.format("SELECT CMPCODE, YR, TSTAMP, NAME, SNAME, NUMDAYS, PERINTERVAL FROM %s.OAS_YEAR WHERE CMPCODE='%s' and YR=%d AND DELDATE IS NULL", schema, companyCode, yearNumber);
    ResultSet yearRs = stmt.executeQuery(yearSql);
    if (!yearRs.next())
      throw new Exception("Failed to retrieve Year Master for year " + yearNumber);
    fillMaster(yearRs, year, yearNumber);
    String periodSql = String.format("SELECT CMPCODE, YR, PERIOD, SUMMARY, SEC1, SEC2, SEC3, ENDDATE FROM %s.OAS_PERLIST WHERE CMPCODE='%s' and YR=%d ORDER BY LSTSEQNO", schema, companyCode, yearNumber);
    ResultSet periodRs = stmt.executeQuery(periodSql);
    fillPeriods(periodRs, year); // Empty list is ok (method enumerates resultset - do NOT call next())

    return year;
  }
  
  public Object getYearDef(String companyCode, Integer yearNumber) throws Exception {
    return getYear(companyCode, yearNumber);
  }
  
  // TODO: Create multiple years at once
  public void addYear(String companyCode, Integer yearNumber, String name, String shortName, int periodInterval, int periodDays) throws Exception {
    
    Connection conn = codaDbConn.getConnection();
    String schema = codaDbConn.getUsername();
    Statement stmt = conn.createStatement();

    // Validate Company Code (exists)
    String cmpSql = String.format("SELECT CODE, YRBASE, YRMIN, YRMAX, PERNO FROM %s.OAS_COMPANY WHERE CODE='%s' AND DELDATE IS NULL", schema, companyCode);
    ResultSet rs = stmt.executeQuery(cmpSql);
    if (!rs.next())
      throw new Exception("Company " + companyCode + " does not exist");
    
    int baseYear = rs.getInt("YRBASE");
    int maxYear = rs.getInt("YRMAX");
    int periodCount = rs.getInt("PERNO");
    
    // Validate Year (does not exist)
    String yearSql = String.format("SELECT 1 FROM %s.OAS_YEAR WHERE CMPCODE='%s' and YR=%d", schema, companyCode, yearNumber);
    rs = stmt.executeQuery(yearSql);
    if (rs.next())
      throw new Exception("Year " + yearNumber + " already exists in company " + companyCode);

    // Begin transaction
    conn.setAutoCommit(false);

    // Calculate periods
    List<Period> periods = calulatePeriods(yearNumber, periodInterval, periodCount);
    
    // Add year to OAS_YEAR
    String insertYearSqlTempl = "INSERT INTO " + schema + ".OAS_YEAR"
        + "(CMPCODE, YR, TSTAMP, NAME, SNAME, ADDDATE, USRNAME, NUMDAYS, PERINTERVAL) VALUES"
        + "('%s',%d,0,'%s','%s',SYSDATE,'%s',%d,%d)";
    String insertYearSql = String.format(insertYearSqlTempl, 
            companyCode,
            yearNumber,
            name,
            shortName,
            router.getUsername(),
            periodDays,
            periodInterval);
    stmt.executeQuery(insertYearSql);
    stmt.close();
    
    // Add periods to OAS_PERLIST
    String insertPeriodSql = "INSERT INTO " + schema + ".OAS_PERLIST"
        + "(CMPCODE, YR, LSTSEQNO, PERIOD, SUMMARY, SEC1, SEC2, SEC3, ENDDATE) VALUES"
        + "(?,?,?,?,?,?,?,?,?)";
    PreparedStatement periodStatement = conn.prepareStatement(insertPeriodSql);
    
    int seqNo = 1;
    for (Period p : periods) {
      periodStatement.setString(1, companyCode);
      periodStatement.setInt(2, yearNumber);
      periodStatement.setInt(3, seqNo);
      periodStatement.setInt(4, p.getPeriodNum());
      periodStatement.setInt(5, p.getSummary());
      periodStatement.setInt(6, p.getSec1());
      periodStatement.setInt(7, p.getSec2());
      periodStatement.setInt(8, p.getSec3());
      XMLGregorianCalendar xmlDate = p.getEndDate();
      java.sql.Date endDate = DateTools.getSqlDate(xmlDate);
      periodStatement.setDate(9, endDate, xmlDate.toGregorianCalendar()); // MUST specify Calendar to ensure time zome is not used to offset date
      periodStatement.executeUpdate();
      seqNo++;
    }
    periodStatement.close();

    // End transaction
    conn.commit();
    conn.setAutoCommit(true);
    
    // Update Company Master
    CodaServiceDefinition<Company> serviceDef = new CodaServiceDefinition("Company", "11.3", Company.class, "Company", CodaGlobalKey.class, new String[] {"code"}, GlobalKey.class, new String[] {"code"}, CodaLogicalServerType.FINANCIALS, CompanyPropertyProvider.class);
    CodaObjectStore<Company,CodaGlobalKey> companyService = new CodaGenericService<>(router, serviceDef);

    try {
      CodaGlobalKey key = new CodaGlobalKey(companyCode);
      if (yearNumber > maxYear)
        companyService.updateObject(key, "yearMax", yearNumber.toString());
      if (yearNumber < baseYear || baseYear == 0)
        companyService.updateObject(key, "baseYear", yearNumber.toString());
      if (baseYear == 0)
        companyService.updateObject(key, "yearMin", yearNumber.toString());
    } catch(Exception ex) { // Delete year upon failure
      Statement deleteStatement = conn.createStatement();
      String deletePeriodSql = String.format("DELETE FROM " + schema + ".OAS_PERLIST WHERE CMPCODE='%s' AND YR=%d", companyCode, yearNumber);
      deleteStatement.execute(deletePeriodSql);
      String deleteYearSql = String.format("DELETE FROM " + schema + ".OAS_YEAR WHERE CMPCODE='%s' AND YR=%d", companyCode, yearNumber);
      deleteStatement.execute(deleteYearSql);
      throw ex;
    }
  }
  
  public void setCurrentPeriod(String companyCode, int securityGroup, int currentYear, int currentPeriod) throws Exception {
    
    if (securityGroup < 1 || securityGroup > 3)
      throw new Exception("Invalid security group: " + securityGroup);
    
    Connection conn = codaDbConn.getConnection();
    String schema = codaDbConn.getUsername();
    Statement stmt = conn.createStatement();

    String setPeriodSql = String.format("UPDATE " + schema + ".OAS_COMPANY SET YEARCURT%d=%d, PERCURT%d=%d WHERE CODE='%s'", securityGroup, currentYear, securityGroup, currentPeriod, companyCode);
    stmt.execute(setPeriodSql);
  }
  
  private void fillYearRange(ResultSet rsCmp, Year year) throws Exception {
    // Company Master Properties
    YearRange yr = new YearRange();
    yr.setCode(rsCmp.getString("CODE"));
    yr.setBaseYear(rsCmp.getString("YRBASE"));
    yr.setYearMin(rsCmp.getString("YRMIN"));
    yr.setYearMax(rsCmp.getString("YRMAX"));
    year.setCmp(yr);
  }
  
  private void fillMaster(ResultSet rsYear, Year year, int yearNumber) throws Exception {
    // Year Master Properties
    Master ym = new Master();
    ym.setCmpCode(rsYear.getString("CMPCODE"));
    ym.setFirstYearDate(DateTools.getXMLDate(yearNumber, 1, 1));
    int perInterval = rsYear.getInt("PERINTERVAL");
    switch (perInterval) {
      case PeriodInterval.PERIOD_MONTHS:
        ym.setInterval(TypeCtYearPeriodEndInterval.PERIOD_MONTHS);
        break;
      case PeriodInterval.PERIOD_MONTHEND:
        ym.setInterval(TypeCtYearPeriodEndInterval.PERIOD_MONTHEND);
        break;
      case PeriodInterval.PERIOD_DAYS:
        ym.setInterval(TypeCtYearPeriodEndInterval.PERIOD_DAYS);
        break;
      case PeriodInterval.PERIOD_454:
        ym.setInterval(TypeCtYearPeriodEndInterval.PERIOD_454);
        break;
      case PeriodInterval.PERIOD_445:
        ym.setInterval(TypeCtYearPeriodEndInterval.PERIOD_445);
        break;
      case PeriodInterval.PERIOD_544:
        ym.setInterval(TypeCtYearPeriodEndInterval.PERIOD_544);
        break;
      default:
        throw new Exception("Unknown period interval " + perInterval);
    }
    ym.setName(rsYear.getString("NAME"));
    ym.setNumDays(new Integer(rsYear.getInt("NUMDAYS")).shortValue());
    ym.setShortName(rsYear.getString("SNAME"));
    ym.setTimeStamp(new Integer(rsYear.getInt("TSTAMP")).shortValue());
    ym.setYear(String.valueOf(rsYear.getInt("YR")));
    year.setYea(ym);    
  }
  
  private void fillPeriods(ResultSet rsPeriods, Year year) throws Exception {
    // Period Properties
    Periods periods = new Periods();
    while (rsPeriods.next()) {
      Period p = new Period();
      java.util.Date endDate = rsPeriods.getTimestamp("ENDDATE");
      p.setEndDate(DateTools.getXMLDate(endDate));
      p.setPeriodNum(new Integer(rsPeriods.getInt("PERIOD")).shortValue());
      p.setSec1(new Integer(rsPeriods.getInt("SEC1")).byteValue());
      p.setSec2(new Integer(rsPeriods.getInt("SEC2")).byteValue());
      p.setSec3(new Integer(rsPeriods.getInt("SEC3")).byteValue());
      p.setSummary(new Integer(rsPeriods.getInt("SUMMARY")).byteValue());
      periods.getPeriod().add(p);
    }
    year.setPeriods(periods);
  }
  
  private List<Period> calulatePeriods(int yearNumber, int periodInterval, int periodCount) throws Exception {
    List<Period> periods = new ArrayList<>();
    List<Integer> periodNumbers = new ArrayList<>();
    for (int perNo = 0; perNo <= periodCount; perNo++)
      periodNumbers.add(perNo);
    periodNumbers.add(9998);
    periodNumbers.add(9999);

    Calendar cal = Calendar.getInstance();
    for (Integer periodNumber :  periodNumbers) {
      Period p = new Period();
      p.setPeriodNum(periodNumber.shortValue());
      Integer sec = (periodNumber > 0 && periodNumber <= periodCount) ? 1 : 7;
      p.setSec1(sec.byteValue());
      p.setSec2(sec.byteValue());
      p.setSec3(sec.byteValue());
      
      switch (periodInterval) {
        case PeriodInterval.PERIOD_MONTHS:
          throw new Exception("Period interval " + periodInterval + " not implemented");
        case PeriodInterval.PERIOD_MONTHEND:
          // Set special periods to mirror the correct real periods (0 == first period, >12 == 12)
          if (periodNumber == 0)
            periodNumber = 1;
          if (periodNumber > 12)
            periodNumber = 12;
          p.setSummary(new Integer(((periodNumber - 1)/ 3) + 1).byteValue());
          // Set calendar to first day of month
          cal.set(yearNumber, periodNumber - 1, 1);
          // Set the end date to the last day of the month
          p.setEndDate(DateTools.getXMLDate(yearNumber, periodNumber, cal.getActualMaximum(Calendar.DAY_OF_MONTH)));
          break;
        case PeriodInterval.PERIOD_DAYS:
          throw new Exception("Period interval " + periodInterval + " not implemented");
        case PeriodInterval.PERIOD_454:
          throw new Exception("Period interval " + periodInterval + " not implemented");
        case PeriodInterval.PERIOD_445:
          throw new Exception("Period interval " + periodInterval + " not implemented");
        case PeriodInterval.PERIOD_544:
          throw new Exception("Period interval " + periodInterval + " not implemented");
        default:
          throw new Exception("Unknown period interval " + periodInterval);
      }
      periods.add(p);
    }
    return periods;
  }
}
