package lava.rt.test; 

import java.lang.String; 
import java.lang.Integer; 
import java.lang.Float; 
import java.lang.Double; 
import java.sql.Date; 
import java.sql.Timestamp; 
import java.math.BigDecimal; 
import java.lang.Byte; 
import java.lang.Boolean; 
import java.lang.Byte; 
import lava.rt.linq.*; 
import java.util.*; 
import java.sql.SQLException; 
import javax.sql.DataSource; 


import java.io.Serializable; 


/*
 *@Database JC2010_ENTERPRISE_DB
 *@SrcGener lava.rt.linq.src.MSSQLServerDataContextSrcGener
 *@CreateAt Sun Jun 16 14:54:09 CST 2019
*/ 
public class JC2010_ENTERPRISE_DB extends lava.rt.linq.DataSourceContext{ 

	private static final long serialVersionUID=1L;

	@Override
	protected Class thisClass() {return this.getClass(); }

	 //public JC2010_ENTERPRISE_DB(DataSource... dataSources)throws Exception{ super(dataSources);  } 

	 public final Table<Test_> TEST_=createTable(Test_.class,"TEST_","ID");
	 public final Table<CompanyRel> COMPANY_REL=createTable(CompanyRel.class,"COMPANY_REL","COP_GB_CODE");




	 public  class Test_ extends Entity implements Serializable {



private static final long serialVersionUID = JC2010_ENTERPRISE_DB.serialVersionUID;

		 private Integer ID ; 
 		 private Integer INT_ ; 
 		 private Double FLOAT_ ; 
 		 private String VARCHAR_ ; 
 		 private Timestamp DATETIME_ ; 
 		 private BigDecimal DECIMAL_ ; 
 
		 public Integer getId(){ return this.ID; } 
		 public void setId(Integer id ){  this.ID=id; } 
		 public Integer getInt_(){ return this.INT_; } 
		 public void setInt_(Integer int_ ){  this.INT_=int_; } 
		 public Double getFloat_(){ return this.FLOAT_; } 
		 public void setFloat_(Double float_ ){  this.FLOAT_=float_; } 
		 public String getVarchar_(){ return this.VARCHAR_; } 
		 public void setVarchar_(String varchar_ ){  this.VARCHAR_=varchar_; } 
		 public Timestamp getDatetime_(){ return this.DATETIME_; } 
		 public void setDatetime_(Timestamp datetime_ ){  this.DATETIME_=datetime_; } 
		 public BigDecimal getDecimal_(){ return this.DECIMAL_; } 
		 public void setDecimal_(BigDecimal decimal_ ){  this.DECIMAL_=decimal_; } 




		@Override
		public Class<? extends Entity> thisClass() {return this.getClass() ;}

	 } //end TEST_

	 public  class CompanyRel extends Entity implements Serializable {



private static final long serialVersionUID = JC2010_ENTERPRISE_DB.serialVersionUID;

		 private String ADMIN_DIVISION_CODE ; 
 		 private String ECONOMIC_AREA_CODE ; 
 		 private String ECONOMIC_CATEGORY_CODE ; 
 		 private String BROKER_TYPE ; 
 		 private String LATEST_TRADE_CO ; 
 		 private String FULL_NAME ; 
 		 private String CUSTOMS_CODE ; 
 		 private String CO_CLASS ; 
 		 private String ORG_CLASSIFICATION ; 
 		 private String SIGNOUT_FLAG ; 
 		 private Timestamp IC_REG_DATE ; 
 		 private Timestamp VALID_DATE ; 
 		 private String ACCO_BANK ; 
 		 private String ACCO_NO ; 
 		 private String MAIL_CO ; 
 		 private Timestamp RG_DATE ; 
 		 private Timestamp FIRST_RG_DATE ; 
 		 private String LICENSE_ID ; 
 		 private String EN_FULL_CO ; 
 		 private String EN_ADDR_CO ; 
 		 private String ADDR_CO ; 
 		 private String SECOND_ADDR_CO ; 
 		 private String BUSI_TYPE ; 
 		 private String CONTAC_CO ; 
 		 private String TEL_CO ; 
 		 private String CONTAC_MOBILE ; 
 		 private String CONTAC_EMAIL ; 
 		 private String CONTAC_FAX ; 
 		 private String HOME_PAGE ; 
 		 private String APPR_DEP ; 
 		 private String APPR_ID ; 
 		 private String LAW_MAN ; 
 		 private String LAW_MAN_TEL ; 
 		 private String LAW_MAN_MOBILE ; 
 		 private String LAW_MAN_EMAIL ; 
 		 private BigDecimal INV_FUND_T ; 
 		 private String INV_FUND_T_CURR ; 
 		 private BigDecimal INV_T_CURR_RATE_US ; 
 		 private String LAW_MAN_CREDENTIAL ; 
 		 private String ID_NUMBER ; 
 		 private BigDecimal RG_FUND ; 
 		 private String CURR_CODE ; 
 		 private BigDecimal CURR_RATE_RMB ; 
 		 private String TAXY_RG_NO ; 
 		 private BigDecimal ACT_FUND ; 
 		 private String COP_NOTE ; 
 		 private String PRE_TRADE_CO ; 
 		 private String COP_GB_CODE ; 
 		 private String COP_IO_CODE ; 
 		 private String COP_RANGE ; 
 		 private String SUPERIOR_COP_GB_CODE ; 
 		 private String SUPERIOR_FULL_NAME ; 
 		 private String SUPERIOR_RELATION ; 
 		 private String BUSINESS_PLACE_TYPE ; 
 		 private BigDecimal OVERALL_FLOORAGE ; 
 		 private String ACCOUNT_TYPE ; 
 		 private String ACCOUNT_AGENT_NAME ; 
 		 private String ACCOUNT_AGENT_COP_GB_CODE ; 
 		 private String ACCOUNT_AGENT_ADDR ; 
 		 private String ACCOUNT_AGENT_CONTAC ; 
 		 private String ACCOUNT_AGENT_TEL ; 
 		 private String COP_EMAIL ; 
 		 private String COP_FAX ; 
 		 private String IS_PUBLIC_COP ; 
 		 private String STAFF_QUANTITY ; 
 		 private String IS_ACCOUNT_COMPUTE ; 
 		 private String FINANCE_SOFTWARE ; 
 		 private String APPROVAL_STD ; 
 		 private String CORP_TYPE ; 
 		 private Integer CORP_DEVELOP_INDEX ; 
 		 private String TERRITORY_BUSI_MARK ; 
 		 private String GLOBAL_CREDIT_MARK ; 
 		 private String NATIONAL_CREDIT_MARK ; 
 		 private String CO_CLASS_PRO ; 
 		 private String SPECIAL_TRADE_ZONE ; 
 		 private String SOCIAL_CREDIT_CODE ; 
 		 private String SUPERIOR_SOCIAL_CREDIT_CODE ; 
 		 private BigDecimal INV_FUND_US ; 
 		 private BigDecimal RG_FUND_RMB ; 
 		 private String ENT_TYPE ; 
 		 private String REG_CODE ; 
 		 private String IS_EXPRESS ; 
 		 private String AGENT_TYPE ; 
 		 private String ENT_TYPE_CODE ; 
 		 private String LOCAL_ORG ; 
 		 private String EXPRESS_CERTIFICATE_CODE ; 
 		 private String DIRECT_FLAG ; 
 		 private String AUTO_PASS_FLAG ; 
 		 private String ACCOUNT_SOCIAL_CREDIT_CODE ; 
 		 private Timestamp CO_CLASS_PRO_LAST_MODIFY_TIME ; 
 		 private Timestamp LAST_CERTIFY_TIME ; 
 
		 public String getAdminDivisionCode(){ return this.ADMIN_DIVISION_CODE; } 
		 public void setAdminDivisionCode(String adminDivisionCode ){  this.ADMIN_DIVISION_CODE=adminDivisionCode; } 
		 public String getEconomicAreaCode(){ return this.ECONOMIC_AREA_CODE; } 
		 public void setEconomicAreaCode(String economicAreaCode ){  this.ECONOMIC_AREA_CODE=economicAreaCode; } 
		 public String getEconomicCategoryCode(){ return this.ECONOMIC_CATEGORY_CODE; } 
		 public void setEconomicCategoryCode(String economicCategoryCode ){  this.ECONOMIC_CATEGORY_CODE=economicCategoryCode; } 
		 public String getBrokerType(){ return this.BROKER_TYPE; } 
		 public void setBrokerType(String brokerType ){  this.BROKER_TYPE=brokerType; } 
		 public String getLatestTradeCo(){ return this.LATEST_TRADE_CO; } 
		 public void setLatestTradeCo(String latestTradeCo ){  this.LATEST_TRADE_CO=latestTradeCo; } 
		 public String getFullName(){ return this.FULL_NAME; } 
		 public void setFullName(String fullName ){  this.FULL_NAME=fullName; } 
		 public String getCustomsCode(){ return this.CUSTOMS_CODE; } 
		 public void setCustomsCode(String customsCode ){  this.CUSTOMS_CODE=customsCode; } 
		 public String getCoClass(){ return this.CO_CLASS; } 
		 public void setCoClass(String coClass ){  this.CO_CLASS=coClass; } 
		 public String getOrgClassification(){ return this.ORG_CLASSIFICATION; } 
		 public void setOrgClassification(String orgClassification ){  this.ORG_CLASSIFICATION=orgClassification; } 
		 public String getSignoutFlag(){ return this.SIGNOUT_FLAG; } 
		 public void setSignoutFlag(String signoutFlag ){  this.SIGNOUT_FLAG=signoutFlag; } 
		 public Timestamp getIcRegDate(){ return this.IC_REG_DATE; } 
		 public void setIcRegDate(Timestamp icRegDate ){  this.IC_REG_DATE=icRegDate; } 
		 public Timestamp getValidDate(){ return this.VALID_DATE; } 
		 public void setValidDate(Timestamp validDate ){  this.VALID_DATE=validDate; } 
		 public String getAccoBank(){ return this.ACCO_BANK; } 
		 public void setAccoBank(String accoBank ){  this.ACCO_BANK=accoBank; } 
		 public String getAccoNo(){ return this.ACCO_NO; } 
		 public void setAccoNo(String accoNo ){  this.ACCO_NO=accoNo; } 
		 public String getMailCo(){ return this.MAIL_CO; } 
		 public void setMailCo(String mailCo ){  this.MAIL_CO=mailCo; } 
		 public Timestamp getRgDate(){ return this.RG_DATE; } 
		 public void setRgDate(Timestamp rgDate ){  this.RG_DATE=rgDate; } 
		 public Timestamp getFirstRgDate(){ return this.FIRST_RG_DATE; } 
		 public void setFirstRgDate(Timestamp firstRgDate ){  this.FIRST_RG_DATE=firstRgDate; } 
		 public String getLicenseId(){ return this.LICENSE_ID; } 
		 public void setLicenseId(String licenseId ){  this.LICENSE_ID=licenseId; } 
		 public String getEnFullCo(){ return this.EN_FULL_CO; } 
		 public void setEnFullCo(String enFullCo ){  this.EN_FULL_CO=enFullCo; } 
		 public String getEnAddrCo(){ return this.EN_ADDR_CO; } 
		 public void setEnAddrCo(String enAddrCo ){  this.EN_ADDR_CO=enAddrCo; } 
		 public String getAddrCo(){ return this.ADDR_CO; } 
		 public void setAddrCo(String addrCo ){  this.ADDR_CO=addrCo; } 
		 public String getSecondAddrCo(){ return this.SECOND_ADDR_CO; } 
		 public void setSecondAddrCo(String secondAddrCo ){  this.SECOND_ADDR_CO=secondAddrCo; } 
		 public String getBusiType(){ return this.BUSI_TYPE; } 
		 public void setBusiType(String busiType ){  this.BUSI_TYPE=busiType; } 
		 public String getContacCo(){ return this.CONTAC_CO; } 
		 public void setContacCo(String contacCo ){  this.CONTAC_CO=contacCo; } 
		 public String getTelCo(){ return this.TEL_CO; } 
		 public void setTelCo(String telCo ){  this.TEL_CO=telCo; } 
		 public String getContacMobile(){ return this.CONTAC_MOBILE; } 
		 public void setContacMobile(String contacMobile ){  this.CONTAC_MOBILE=contacMobile; } 
		 public String getContacEmail(){ return this.CONTAC_EMAIL; } 
		 public void setContacEmail(String contacEmail ){  this.CONTAC_EMAIL=contacEmail; } 
		 public String getContacFax(){ return this.CONTAC_FAX; } 
		 public void setContacFax(String contacFax ){  this.CONTAC_FAX=contacFax; } 
		 public String getHomePage(){ return this.HOME_PAGE; } 
		 public void setHomePage(String homePage ){  this.HOME_PAGE=homePage; } 
		 public String getApprDep(){ return this.APPR_DEP; } 
		 public void setApprDep(String apprDep ){  this.APPR_DEP=apprDep; } 
		 public String getApprId(){ return this.APPR_ID; } 
		 public void setApprId(String apprId ){  this.APPR_ID=apprId; } 
		 public String getLawMan(){ return this.LAW_MAN; } 
		 public void setLawMan(String lawMan ){  this.LAW_MAN=lawMan; } 
		 public String getLawManTel(){ return this.LAW_MAN_TEL; } 
		 public void setLawManTel(String lawManTel ){  this.LAW_MAN_TEL=lawManTel; } 
		 public String getLawManMobile(){ return this.LAW_MAN_MOBILE; } 
		 public void setLawManMobile(String lawManMobile ){  this.LAW_MAN_MOBILE=lawManMobile; } 
		 public String getLawManEmail(){ return this.LAW_MAN_EMAIL; } 
		 public void setLawManEmail(String lawManEmail ){  this.LAW_MAN_EMAIL=lawManEmail; } 
		 public BigDecimal getInvFundT(){ return this.INV_FUND_T; } 
		 public void setInvFundT(BigDecimal invFundT ){  this.INV_FUND_T=invFundT; } 
		 public String getInvFundTCurr(){ return this.INV_FUND_T_CURR; } 
		 public void setInvFundTCurr(String invFundTCurr ){  this.INV_FUND_T_CURR=invFundTCurr; } 
		 public BigDecimal getInvTCurrRateUs(){ return this.INV_T_CURR_RATE_US; } 
		 public void setInvTCurrRateUs(BigDecimal invTCurrRateUs ){  this.INV_T_CURR_RATE_US=invTCurrRateUs; } 
		 public String getLawManCredential(){ return this.LAW_MAN_CREDENTIAL; } 
		 public void setLawManCredential(String lawManCredential ){  this.LAW_MAN_CREDENTIAL=lawManCredential; } 
		 public String getIdNumber(){ return this.ID_NUMBER; } 
		 public void setIdNumber(String idNumber ){  this.ID_NUMBER=idNumber; } 
		 public BigDecimal getRgFund(){ return this.RG_FUND; } 
		 public void setRgFund(BigDecimal rgFund ){  this.RG_FUND=rgFund; } 
		 public String getCurrCode(){ return this.CURR_CODE; } 
		 public void setCurrCode(String currCode ){  this.CURR_CODE=currCode; } 
		 public BigDecimal getCurrRateRmb(){ return this.CURR_RATE_RMB; } 
		 public void setCurrRateRmb(BigDecimal currRateRmb ){  this.CURR_RATE_RMB=currRateRmb; } 
		 public String getTaxyRgNo(){ return this.TAXY_RG_NO; } 
		 public void setTaxyRgNo(String taxyRgNo ){  this.TAXY_RG_NO=taxyRgNo; } 
		 public BigDecimal getActFund(){ return this.ACT_FUND; } 
		 public void setActFund(BigDecimal actFund ){  this.ACT_FUND=actFund; } 
		 public String getCopNote(){ return this.COP_NOTE; } 
		 public void setCopNote(String copNote ){  this.COP_NOTE=copNote; } 
		 public String getPreTradeCo(){ return this.PRE_TRADE_CO; } 
		 public void setPreTradeCo(String preTradeCo ){  this.PRE_TRADE_CO=preTradeCo; } 
		 public String getCopGbCode(){ return this.COP_GB_CODE; } 
		 public void setCopGbCode(String copGbCode ){  this.COP_GB_CODE=copGbCode; } 
		 public String getCopIoCode(){ return this.COP_IO_CODE; } 
		 public void setCopIoCode(String copIoCode ){  this.COP_IO_CODE=copIoCode; } 
		 public String getCopRange(){ return this.COP_RANGE; } 
		 public void setCopRange(String copRange ){  this.COP_RANGE=copRange; } 
		 public String getSuperiorCopGbCode(){ return this.SUPERIOR_COP_GB_CODE; } 
		 public void setSuperiorCopGbCode(String superiorCopGbCode ){  this.SUPERIOR_COP_GB_CODE=superiorCopGbCode; } 
		 public String getSuperiorFullName(){ return this.SUPERIOR_FULL_NAME; } 
		 public void setSuperiorFullName(String superiorFullName ){  this.SUPERIOR_FULL_NAME=superiorFullName; } 
		 public String getSuperiorRelation(){ return this.SUPERIOR_RELATION; } 
		 public void setSuperiorRelation(String superiorRelation ){  this.SUPERIOR_RELATION=superiorRelation; } 
		 public String getBusinessPlaceType(){ return this.BUSINESS_PLACE_TYPE; } 
		 public void setBusinessPlaceType(String businessPlaceType ){  this.BUSINESS_PLACE_TYPE=businessPlaceType; } 
		 public BigDecimal getOverallFloorage(){ return this.OVERALL_FLOORAGE; } 
		 public void setOverallFloorage(BigDecimal overallFloorage ){  this.OVERALL_FLOORAGE=overallFloorage; } 
		 public String getAccountType(){ return this.ACCOUNT_TYPE; } 
		 public void setAccountType(String accountType ){  this.ACCOUNT_TYPE=accountType; } 
		 public String getAccountAgentName(){ return this.ACCOUNT_AGENT_NAME; } 
		 public void setAccountAgentName(String accountAgentName ){  this.ACCOUNT_AGENT_NAME=accountAgentName; } 
		 public String getAccountAgentCopGbCode(){ return this.ACCOUNT_AGENT_COP_GB_CODE; } 
		 public void setAccountAgentCopGbCode(String accountAgentCopGbCode ){  this.ACCOUNT_AGENT_COP_GB_CODE=accountAgentCopGbCode; } 
		 public String getAccountAgentAddr(){ return this.ACCOUNT_AGENT_ADDR; } 
		 public void setAccountAgentAddr(String accountAgentAddr ){  this.ACCOUNT_AGENT_ADDR=accountAgentAddr; } 
		 public String getAccountAgentContac(){ return this.ACCOUNT_AGENT_CONTAC; } 
		 public void setAccountAgentContac(String accountAgentContac ){  this.ACCOUNT_AGENT_CONTAC=accountAgentContac; } 
		 public String getAccountAgentTel(){ return this.ACCOUNT_AGENT_TEL; } 
		 public void setAccountAgentTel(String accountAgentTel ){  this.ACCOUNT_AGENT_TEL=accountAgentTel; } 
		 public String getCopEmail(){ return this.COP_EMAIL; } 
		 public void setCopEmail(String copEmail ){  this.COP_EMAIL=copEmail; } 
		 public String getCopFax(){ return this.COP_FAX; } 
		 public void setCopFax(String copFax ){  this.COP_FAX=copFax; } 
		 public String getIsPublicCop(){ return this.IS_PUBLIC_COP; } 
		 public void setIsPublicCop(String isPublicCop ){  this.IS_PUBLIC_COP=isPublicCop; } 
		 public String getStaffQuantity(){ return this.STAFF_QUANTITY; } 
		 public void setStaffQuantity(String staffQuantity ){  this.STAFF_QUANTITY=staffQuantity; } 
		 public String getIsAccountCompute(){ return this.IS_ACCOUNT_COMPUTE; } 
		 public void setIsAccountCompute(String isAccountCompute ){  this.IS_ACCOUNT_COMPUTE=isAccountCompute; } 
		 public String getFinanceSoftware(){ return this.FINANCE_SOFTWARE; } 
		 public void setFinanceSoftware(String financeSoftware ){  this.FINANCE_SOFTWARE=financeSoftware; } 
		 public String getApprovalStd(){ return this.APPROVAL_STD; } 
		 public void setApprovalStd(String approvalStd ){  this.APPROVAL_STD=approvalStd; } 
		 public String getCorpType(){ return this.CORP_TYPE; } 
		 public void setCorpType(String corpType ){  this.CORP_TYPE=corpType; } 
		 public Integer getCorpDevelopIndex(){ return this.CORP_DEVELOP_INDEX; } 
		 public void setCorpDevelopIndex(Integer corpDevelopIndex ){  this.CORP_DEVELOP_INDEX=corpDevelopIndex; } 
		 public String getTerritoryBusiMark(){ return this.TERRITORY_BUSI_MARK; } 
		 public void setTerritoryBusiMark(String territoryBusiMark ){  this.TERRITORY_BUSI_MARK=territoryBusiMark; } 
		 public String getGlobalCreditMark(){ return this.GLOBAL_CREDIT_MARK; } 
		 public void setGlobalCreditMark(String globalCreditMark ){  this.GLOBAL_CREDIT_MARK=globalCreditMark; } 
		 public String getNationalCreditMark(){ return this.NATIONAL_CREDIT_MARK; } 
		 public void setNationalCreditMark(String nationalCreditMark ){  this.NATIONAL_CREDIT_MARK=nationalCreditMark; } 
		 public String getCoClassPro(){ return this.CO_CLASS_PRO; } 
		 public void setCoClassPro(String coClassPro ){  this.CO_CLASS_PRO=coClassPro; } 
		 public String getSpecialTradeZone(){ return this.SPECIAL_TRADE_ZONE; } 
		 public void setSpecialTradeZone(String specialTradeZone ){  this.SPECIAL_TRADE_ZONE=specialTradeZone; } 
		 public String getSocialCreditCode(){ return this.SOCIAL_CREDIT_CODE; } 
		 public void setSocialCreditCode(String socialCreditCode ){  this.SOCIAL_CREDIT_CODE=socialCreditCode; } 
		 public String getSuperiorSocialCreditCode(){ return this.SUPERIOR_SOCIAL_CREDIT_CODE; } 
		 public void setSuperiorSocialCreditCode(String superiorSocialCreditCode ){  this.SUPERIOR_SOCIAL_CREDIT_CODE=superiorSocialCreditCode; } 
		 public BigDecimal getInvFundUs(){ return this.INV_FUND_US; } 
		 public void setInvFundUs(BigDecimal invFundUs ){  this.INV_FUND_US=invFundUs; } 
		 public BigDecimal getRgFundRmb(){ return this.RG_FUND_RMB; } 
		 public void setRgFundRmb(BigDecimal rgFundRmb ){  this.RG_FUND_RMB=rgFundRmb; } 
		 public String getEntType(){ return this.ENT_TYPE; } 
		 public void setEntType(String entType ){  this.ENT_TYPE=entType; } 
		 public String getRegCode(){ return this.REG_CODE; } 
		 public void setRegCode(String regCode ){  this.REG_CODE=regCode; } 
		 public String getIsExpress(){ return this.IS_EXPRESS; } 
		 public void setIsExpress(String isExpress ){  this.IS_EXPRESS=isExpress; } 
		 public String getAgentType(){ return this.AGENT_TYPE; } 
		 public void setAgentType(String agentType ){  this.AGENT_TYPE=agentType; } 
		 public String getEntTypeCode(){ return this.ENT_TYPE_CODE; } 
		 public void setEntTypeCode(String entTypeCode ){  this.ENT_TYPE_CODE=entTypeCode; } 
		 public String getLocalOrg(){ return this.LOCAL_ORG; } 
		 public void setLocalOrg(String localOrg ){  this.LOCAL_ORG=localOrg; } 
		 public String getExpressCertificateCode(){ return this.EXPRESS_CERTIFICATE_CODE; } 
		 public void setExpressCertificateCode(String expressCertificateCode ){  this.EXPRESS_CERTIFICATE_CODE=expressCertificateCode; } 
		 public String getDirectFlag(){ return this.DIRECT_FLAG; } 
		 public void setDirectFlag(String directFlag ){  this.DIRECT_FLAG=directFlag; } 
		 public String getAutoPassFlag(){ return this.AUTO_PASS_FLAG; } 
		 public void setAutoPassFlag(String autoPassFlag ){  this.AUTO_PASS_FLAG=autoPassFlag; } 
		 public String getAccountSocialCreditCode(){ return this.ACCOUNT_SOCIAL_CREDIT_CODE; } 
		 public void setAccountSocialCreditCode(String accountSocialCreditCode ){  this.ACCOUNT_SOCIAL_CREDIT_CODE=accountSocialCreditCode; } 
		 public Timestamp getCoClassProLastModifyTime(){ return this.CO_CLASS_PRO_LAST_MODIFY_TIME; } 
		 public void setCoClassProLastModifyTime(Timestamp coClassProLastModifyTime ){  this.CO_CLASS_PRO_LAST_MODIFY_TIME=coClassProLastModifyTime; } 
		 public Timestamp getLastCertifyTime(){ return this.LAST_CERTIFY_TIME; } 
		 public void setLastCertifyTime(Timestamp lastCertifyTime ){  this.LAST_CERTIFY_TIME=lastCertifyTime; } 




		@Override
		public Class<? extends Entity> thisClass() {return this.getClass() ;}

	 } //end COMPANY_REL



		public Object[][] getColumns(Integer id,OutputParam<Float> name,OutputParam<String> age) throws SQLException {


			 return callProcedure("getColumns",id,name,age);
		} 







	public final static Criteria CRITERIA=new Criteria();

	public  static class Criteria{ 

		private Criteria() {} 

		public static final Column 
		CopFax = new Column("COP_FAX"),
		AccoNo = new Column("ACCO_NO"),
		EnFullCo = new Column("EN_FULL_CO"),
		AccountAgentTel = new Column("ACCOUNT_AGENT_TEL"),
		BrokerType = new Column("BROKER_TYPE"),
		Decimal_ = new Column("DECIMAL_"),
		TaxyRgNo = new Column("TAXY_RG_NO"),
		AddrCo = new Column("ADDR_CO"),
		Id = new Column("ID"),
		FirstRgDate = new Column("FIRST_RG_DATE"),
		BusinessPlaceType = new Column("BUSINESS_PLACE_TYPE"),
		Datetime_ = new Column("DATETIME_"),
		ContacCo = new Column("CONTAC_CO"),
		SuperiorFullName = new Column("SUPERIOR_FULL_NAME"),
		AccountAgentContac = new Column("ACCOUNT_AGENT_CONTAC"),
		GlobalCreditMark = new Column("GLOBAL_CREDIT_MARK"),
		CoClass = new Column("CO_CLASS"),
		ApprId = new Column("APPR_ID"),
		ContacEmail = new Column("CONTAC_EMAIL"),
		SocialCreditCode = new Column("SOCIAL_CREDIT_CODE"),
		SignoutFlag = new Column("SIGNOUT_FLAG"),
		CopNote = new Column("COP_NOTE"),
		StaffQuantity = new Column("STAFF_QUANTITY"),
		RgDate = new Column("RG_DATE"),
		SuperiorSocialCreditCode = new Column("SUPERIOR_SOCIAL_CREDIT_CODE"),
		SuperiorCopGbCode = new Column("SUPERIOR_COP_GB_CODE"),
		AccountAgentAddr = new Column("ACCOUNT_AGENT_ADDR"),
		InvFundTCurr = new Column("INV_FUND_T_CURR"),
		IdNumber = new Column("ID_NUMBER"),
		DirectFlag = new Column("DIRECT_FLAG"),
		LawManTel = new Column("LAW_MAN_TEL"),
		IsExpress = new Column("IS_EXPRESS"),
		FinanceSoftware = new Column("FINANCE_SOFTWARE"),
		LawManCredential = new Column("LAW_MAN_CREDENTIAL"),
		Int_ = new Column("INT_"),
		ActFund = new Column("ACT_FUND"),
		LawManEmail = new Column("LAW_MAN_EMAIL"),
		ContacFax = new Column("CONTAC_FAX"),
		SuperiorRelation = new Column("SUPERIOR_RELATION"),
		AccountAgentName = new Column("ACCOUNT_AGENT_NAME"),
		AccountSocialCreditCode = new Column("ACCOUNT_SOCIAL_CREDIT_CODE"),
		CustomsCode = new Column("CUSTOMS_CODE"),
		Float_ = new Column("FLOAT_"),
		SecondAddrCo = new Column("SECOND_ADDR_CO"),
		AgentType = new Column("AGENT_TYPE"),
		RegCode = new Column("REG_CODE"),
		CurrCode = new Column("CURR_CODE"),
		AutoPassFlag = new Column("AUTO_PASS_FLAG"),
		ExpressCertificateCode = new Column("EXPRESS_CERTIFICATE_CODE"),
		ApprDep = new Column("APPR_DEP"),
		AccoBank = new Column("ACCO_BANK"),
		TelCo = new Column("TEL_CO"),
		AccountAgentCopGbCode = new Column("ACCOUNT_AGENT_COP_GB_CODE"),
		InvFundUs = new Column("INV_FUND_US"),
		EnAddrCo = new Column("EN_ADDR_CO"),
		EconomicAreaCode = new Column("ECONOMIC_AREA_CODE"),
		CopEmail = new Column("COP_EMAIL"),
		LocalOrg = new Column("LOCAL_ORG"),
		ApprovalStd = new Column("APPROVAL_STD"),
		InvTCurrRateUs = new Column("INV_T_CURR_RATE_US"),
		TerritoryBusiMark = new Column("TERRITORY_BUSI_MARK"),
		SpecialTradeZone = new Column("SPECIAL_TRADE_ZONE"),
		CopRange = new Column("COP_RANGE"),
		IsPublicCop = new Column("IS_PUBLIC_COP"),
		IcRegDate = new Column("IC_REG_DATE"),
		LatestTradeCo = new Column("LATEST_TRADE_CO"),
		LawManMobile = new Column("LAW_MAN_MOBILE"),
		OverallFloorage = new Column("OVERALL_FLOORAGE"),
		IsAccountCompute = new Column("IS_ACCOUNT_COMPUTE"),
		CorpType = new Column("CORP_TYPE"),
		NationalCreditMark = new Column("NATIONAL_CREDIT_MARK"),
		MailCo = new Column("MAIL_CO"),
		InvFundT = new Column("INV_FUND_T"),
		CurrRateRmb = new Column("CURR_RATE_RMB"),
		RgFundRmb = new Column("RG_FUND_RMB"),
		CopGbCode = new Column("COP_GB_CODE"),
		EntType = new Column("ENT_TYPE"),
		ValidDate = new Column("VALID_DATE"),
		AccountType = new Column("ACCOUNT_TYPE"),
		LawMan = new Column("LAW_MAN"),
		CopIoCode = new Column("COP_IO_CODE"),
		RgFund = new Column("RG_FUND"),
		EconomicCategoryCode = new Column("ECONOMIC_CATEGORY_CODE"),
		BusiType = new Column("BUSI_TYPE"),
		CoClassProLastModifyTime = new Column("CO_CLASS_PRO_LAST_MODIFY_TIME"),
		AdminDivisionCode = new Column("ADMIN_DIVISION_CODE"),
		FullName = new Column("FULL_NAME"),
		EntTypeCode = new Column("ENT_TYPE_CODE"),
		OrgClassification = new Column("ORG_CLASSIFICATION"),
		CorpDevelopIndex = new Column("CORP_DEVELOP_INDEX"),
		PreTradeCo = new Column("PRE_TRADE_CO"),
		LicenseId = new Column("LICENSE_ID"),
		ContacMobile = new Column("CONTAC_MOBILE"),
		HomePage = new Column("HOME_PAGE"),
		LastCertifyTime = new Column("LAST_CERTIFY_TIME"),
		CoClassPro = new Column("CO_CLASS_PRO"),
		Varchar_ = new Column("VARCHAR_")
		;

	}



	@Override
	protected DataSource[] getDataSources() {
		// TODO Auto-generated method stub
		return null;
	} 




} //end