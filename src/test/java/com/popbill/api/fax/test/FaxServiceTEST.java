package com.popbill.api.fax.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.popbill.api.ChargeInfo;
import com.popbill.api.FaxService;
import com.popbill.api.PopbillException;
import com.popbill.api.Response;
import com.popbill.api.fax.FAXSearchResult;
import com.popbill.api.fax.FaxResult;
import com.popbill.api.fax.FaxServiceImp;

public class FaxServiceTEST {

	private final String testLinkID = "TESTER";
	private final String testSecretKey = "SwWxqU+0TErBXy/9TVjIPEnI0VTUMMSQZtJf3Ed8q3I=";

	private FaxService faxService;
	
	public FaxServiceTEST() {
		FaxServiceImp service = new FaxServiceImp();

		service.setLinkID(testLinkID);
		service.setSecretKey(testSecretKey);
		service.setTest(true);
		
		faxService = service;
	}
	
	@Test
	public void getChargeInfo_TEST() throws PopbillException {
		
		ChargeInfo chrgInfo = faxService.getChargeInfo("1234567890");

		System.out.println(chrgInfo.getChargeMethod());
		System.out.println(chrgInfo.getRateSystem());
		System.out.println(chrgInfo.getUnitCost());
	}
	
	@Test
	public void getUnitCost_TEST() throws PopbillException {
		
		float UnitCost = faxService.getUnitCost("1234567890");

		System.out.println(UnitCost);
	}
	
	@Test
	public void getURL_TEST() throws PopbillException {
		
		String url = faxService.getURL("1234567890", "userid", "BOX");

		assertNotNull(url);
		System.out.println(url);
	}
	
	@Test
	public void sendFAX_Single_TEST() throws PopbillException {
		
		File file = new File("/Users/John/Desktop/test.jpg");
		
		String receiptNum = faxService.sendFAX("1234567890", "02-6442-9700", "111-2222-3333","수신자명칭",file, null,null);
		
		assertNotNull(receiptNum);
		
		System.out.println(receiptNum);
		
		FaxResult[] results = faxService.getFaxResult("1234567890", receiptNum);
		
		assertNotNull(results);
		
		System.out.println(results[0].getFileNames()[0] + " "+results[0].getSenderName());

	}
	
	@Test
	public void sendFAX_MultiFile_TEST() throws PopbillException {
		
		File file1 = new File("/Users/John/Desktop/test.jpg");
		
		String receiptNum = faxService.sendFAX("1234567890", "02-6442-9700","111-2222-3333","수신자명칭",new File[]{file1}, null,null);
		
		assertNotNull(receiptNum);
		
		
		FaxResult[] results = faxService.getFaxResult("1234567890", receiptNum);
		
		assertNotNull(results);
		
		System.out.println(results[0].getFileNames()[0] + " "+results[0].getSenderName());
	}
	
	@Test
	public void getFaxResult_TEST() throws PopbillException {
		
		String receiptNum = "015122413083700001";
		
		FaxResult[] results = faxService.getFaxResult("1234567890", receiptNum);
		
		assertNotNull(results);
		
		System.out.println(results[0].getFileNames()[1] + " "+results[0].getReceiptDT());
		
	}
	
	@Test
	public void cancelReserve_TEST() throws PopbillException {
		
		String receiptNum = "014101010184200001";
		
		Response response = faxService.cancelReserve("1234567890", receiptNum,"userid");
		
		assertNotNull(response);
		
		System.out.println(response.getMessage());
		
	}
	
	@Test
	public void search_TEST() throws PopbillException{
		String SDate = "20160801";
		String EDate = "20160831";
		String[] State = {"1","2","3","4"};
		Boolean ReserveYN = false;
		Boolean SenderOnlyYN = false;
		int Page = 1;
		int PerPage = 10;
		String Order = "D";
		FAXSearchResult response = faxService.search("1234567890", SDate, EDate, State, ReserveYN, SenderOnlyYN, Page, PerPage, Order);
		
		assertNotNull(response);
		System.out.println(response.getTotal()+" "+response.getList().get(0).getReceiptDT()+" "+response.getList().get(0).getSenderName());
	}
	
	public static Date addMinutes(Date date, int minutes)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes); //minus number would decrement the days
        return cal.getTime();
    }
}
