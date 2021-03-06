/*
 * Copyright 2006-2014 innopost.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.popbill.api.fax;

/**
 * Class for Fax Sent Result.
 * 
 * @author KimSeongjun
 * @version 1.0.0
 */
public class FaxResult {
	private Integer sendState;
	private Integer convState;
	private String sendNum;
	private String senderName;
	private String receiveNum;
	private String receiveName;
	private Integer sendPageCnt;
	private Integer successPageCnt;
	private Integer failPageCnt;
	private Integer refundPageCnt;
	private Integer cancelPageCnt;
	private String reserveDT;
	private String sendDT;
	private String resultDT;
	private Integer sendResult;
	private String[] fileNames;
	private String receiptDT;

	/**
	 * 전송상태 확인 0 : 접수 1 : 업로드중 2 : 전송중 3 : 완료 4 : 실패 (DB항목 오류) 5 : 실패 (전송중 오류) 6
	 * : 과금실패 7 : 취소
	 * 
	 * @return 전송상태
	 */
	public Integer getSendState() {
		return sendState;
	}

	/**
	 * 변환상태 확인 0 : 대기중 1 : 업로드 2 : 완료 4 : DB 항목오류 5 : 업로드 후 오류 10 : 변환실패 12 :
	 * 파일저장 오류 15 : 회선통신 오류
	 * 
	 * @return 변환상태
	 */
	public Integer getConvState() {
		return convState;
	}

	/**
	 * 발신번호 확인
	 * 
	 * @return 발신번호
	 */
	public String getSendNum() {
		return sendNum;
	}

	/**
	 * 수신번호 확인
	 * 
	 * @return 수신번호
	 */
	public String getReceiveNum() {
		return receiveNum;
	}

	/**
	 * 수신자 명칭 확인
	 * 
	 * @return 수신자 명칭
	 */
	public String getReceiveName() {
		return receiveName;
	}

	/**
	 * 총전송 장수 확인
	 * 
	 * @return 총전송 장수
	 */
	public Integer getSendPageCnt() {
		return sendPageCnt;
	}

	/**
	 * 성공 장수 확인
	 * 
	 * @return 성공장수 확인
	 */
	public Integer getSuccessPageCnt() {
		return successPageCnt;
	}

	/**
	 * 실패 장수 확인
	 * 
	 * @return 실패장수 확인
	 */
	public Integer getFailPageCnt() {
		return failPageCnt;
	}

	/**
	 * 환불 장수 확인
	 * 
	 * @return 환불 장수
	 */
	public Integer getRefundPageCnt() {
		return refundPageCnt;
	}

	/**
	 * 취소 장수 확인
	 * 
	 * @return 취소 장수
	 */
	public Integer getCancelPageCnt() {
		return cancelPageCnt;
	}

	/**
	 * 예약일시 확인
	 * 
	 * @return 예약일시
	 */
	public String getReserveDT() {
		return reserveDT;
	}

	/**
	 * 전송일시 확인
	 * 
	 * @return 전송일시
	 */
	public String getSendDT() {
		return sendDT;
	}

	/**
	 * 결과 수신 일시 확인
	 * 
	 * @return 결과 수신 일시
	 */
	public String getResultDT() {
		return resultDT;
	}

	/**
	 * 전송결과 확인 메뉴얼 3) 통신사 처리결과 상태코드 테이블 참조
	 * 
	 * @return 전송결과
	 */
	public Integer getSendResult() {
		return sendResult;
	}

	/**
	 * 팩스전송 파일명 배열 확인 
	 * 
	 * @return 팩스전송 파일명 배열 
	 */
	public String[] getFileNames() {
		return fileNames;
	}

	/**
	 * 팩스전송 접수일시 확인 
	 * 
	 * @return 팩스전송 접수일시 
	 */
	public String getReceiptDT() {
		return receiptDT;
	}
	
	/**
	 * 팩스 발신자명 확인
	 * 
	 * @return 발신자명 
	 */
	public String getSenderName() {
		return senderName;
	}



}
