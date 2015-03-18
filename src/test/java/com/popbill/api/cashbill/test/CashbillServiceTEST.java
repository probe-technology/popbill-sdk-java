package com.popbill.api.cashbill.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.popbill.api.CashbillService;
import com.popbill.api.PopbillException;
import com.popbill.api.Response;
import com.popbill.api.cashbill.Cashbill;
import com.popbill.api.cashbill.CashbillInfo;
import com.popbill.api.cashbill.CashbillLog;
import com.popbill.api.cashbill.CashbillServiceImp;

public class CashbillServiceTEST {
	private final String testLinkID = "TESTER";
	private final String testSecretKey = "cmiKpBBUtTmhdsBRlRpjTD/ADdFynTJtmna9Yn24phc=";
	
	private CashbillService cashbillService;
	
	public CashbillServiceTEST(){
		CashbillServiceImp service = new CashbillServiceImp();
		
		service.setLinkID(testLinkID);
		service.setSecretKey(testSecretKey);
		service.setTest(true);
		
		cashbillService = service;
	}
	
	@Test
	public void getUnitCost_TEST() throws PopbillException {
		float UnitCost = cashbillService.getUnitCost("1234567890");
		
		assertNotNull(UnitCost);
		System.out.println(UnitCost);
	}
	
	
	
	@Test
	public void getURL_TEST() throws PopbillException {
		String url = cashbillService.getURL("1234567890", "testkorea", "TBOX");
		assertNotNull(url);
		System.out.println(url);
	}

	@Test
	public void register_TEST() throws PopbillException {
		
		Cashbill cashbill = new Cashbill();
		
		cashbill.setMgtKey("20150318-02");
		cashbill.setTradeType("승인거래");
		cashbill.setFranchiseCorpNum("1234567890");
		cashbill.setFranchiseCorpName("발행자 상호");
		cashbill.setFranchiseCEOName("발행자 대표자");
		cashbill.setFranchiseAddr("발행자 주소");
		cashbill.setFranchiseTEL("07075103710");
		cashbill.setIdentityNum("01043245117");
		cashbill.setCustomerName("고개명");
		cashbill.setItemName("상품명");
		cashbill.setOrderNumber("주문번호");
		cashbill.setEmail("test@test.com");
		cashbill.setHp("01043245117");
		cashbill.setFax("07075103710");
		cashbill.setServiceFee("0");
		cashbill.setSupplyCost("10000");
		cashbill.setTax("1000");
		cashbill.setTotalAmount("11000");
		cashbill.setTradeUsage("소득공제용");
		cashbill.setTaxationType("과세");
		cashbill.setSmssendYN(false);
		
		Response response = cashbillService.register("1234567890", cashbill, "testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
	}
	
	@Test
	public void update_TEST() throws PopbillException {
		
		Cashbill cashbill = new Cashbill();
		
		cashbill.setMgtKey("20150317-01");
		cashbill.setTradeType("승인거래");
		cashbill.setFranchiseCorpNum("1234567890");
		cashbill.setFranchiseCorpName("발행자 상호_수정");
		cashbill.setFranchiseCEOName("발행자 대표자_수정");
		cashbill.setFranchiseAddr("발행자 주소");
		cashbill.setFranchiseTEL("07075103710");
		cashbill.setIdentityNum("01043245117");
		cashbill.setCustomerName("고개명");
		cashbill.setItemName("상품명");
		cashbill.setOrderNumber("주문번호");
		cashbill.setEmail("test@test.com");
		cashbill.setHp("01043245117");
		cashbill.setFax("07075103710");
		cashbill.setServiceFee("0");
		cashbill.setSupplyCost("10000");
		cashbill.setTax("1000");
		cashbill.setTotalAmount("11000");
		cashbill.setTradeUsage("소득공제용");
		cashbill.setTaxationType("과세");
		cashbill.setSmssendYN(false);
		
		Response response = cashbillService.update("1234567890", "20150318-02", cashbill, "testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
	}
	
	@Test
	public void issue_TEST() throws PopbillException {
		
		Response response = cashbillService.issue("1234567890", "20150318-02", "발행메모",	"testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
	}
	
	@Test
	public void checkMgtKeyInUse_TEST() throws PopbillException {
		boolean useYN = cashbillService.checkMgtKeyInUse("1234567890", "20150318-02");
		assertNotNull(useYN);
		System.out.println(useYN);
	}
	
	@Test
	public void cancelIssue_TEST() throws PopbillException {
		
		Response response = cashbillService.cancelIssue("1234567890", "20150318-02", "발행취소 메모", "testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
	}
	
	
	@Test
	public void sendEmail_TEST() throws PopbillException {
		
		Response response = cashbillService.sendEmail("1234567890", "20150317-01", "test@test.com", "testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
		
	}
	
	@Test
	public void sendSMS_TEST() throws PopbillException {
		
		Response response = cashbillService.sendSMS("1234567890", "20150317-01", "07075103710", "010111222", "현금영수증 문자전송 테스트", "testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
		
	}
	
	@Test
	public void sendFAX_TEST() throws PopbillException {
		
		Response response = cashbillService.sendFAX("1234567890", "20150317-01", "07075103710","010111222", "testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
	}
	
	@Test
	public void getDetailInfo_TEST() throws PopbillException {
		
		Cashbill cashbill = cashbillService.getDetailInfo("1234567890", "20150318-02");
		assertNotNull(cashbill);
		System.out.println("Detail Info : "+ cashbill.getMgtKey()+ " " );
		
	}
	
	@Test
	public void getInfo_TEST() throws PopbillException {
		CashbillInfo cashbillInfo = cashbillService.getInfo("1234567890", "20150318-02");
		assertNotNull(cashbillInfo);
		System.out.println("Get INfo : "+ cashbillInfo.getItemKey()+" "+cashbillInfo.getOrgConfirmNum());
		
	}
	
	@Test
	public void getInfos_TEST() throws PopbillException {
		String[] MgtKeyList = {"20150317-01","20150317-02", "20150317-03"};
		
		CashbillInfo[] infoList = cashbillService.getInfos("1234567890", MgtKeyList);
		
		assertNotNull(infoList);
		System.out.println(infoList.length);
	}
	
	@Test 
	public void getPrintURL_TEST() throws PopbillException {
		String url = cashbillService.getPrintURL("1234567890", "20150318-02", "testkorea");
		assertNotNull(url);
		System.out.println(url);
	}
	
	@Test
	public void getEPrintURL_TEST() throws PopbillException {
		String url = cashbillService.getEPrintURL("1234567890", "20150318-02", "testkorea");
		assertNotNull(url);
		System.out.println(url);
	
	}
	
	@Test
	public void getMassPrintURL_TEST() throws PopbillException {
		String[] MgtKeyList = {"20150317-01","20150317-02","20150317-03"};
		String url = cashbillService.getMassPrintURL("1234567890", MgtKeyList, "testkorea");
		
		assertNotNull(url);
		System.out.println(url);
	}
	
	@Test
	public void getLogs_TEST() throws PopbillException {
		CashbillLog[] logList = cashbillService.getLogs("1234567890", "20150318-02");
		for(int i=0; i<logList.length; i++){
			System.out.println(logList[i].getProcMemo());
		}
	}
	
	@Test
	public void delete_TEST() throws PopbillException {
		Response response = cashbillService.delete("1234567890", "20150318-02", "testkorea");
		assertNotNull(response);
		System.out.println(response.getMessage());
	}
}
























