package com.explorer.gabom.domain.batch.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressCodeUtils {

	public static String sdCd(String code)  { return code.substring(0, 2); }
	public static String sggCd(String code) { return code.substring(0, 5); }

	/** “충청남도 천안시” -> “충청남도” */
	public static String sdNm(String emdCd) {
		int sp = emdCd.indexOf(' ');
		return sp > 0 ? emdCd.substring(0, sp) : emdCd;
	}

	/** 10자리 모두 숫자인지 */
	public static boolean isValidLawCode(String emdCd) {
		if (emdCd == null || emdCd.length() != 10) return false;
		for (int i=0; i<10; i++) if (!Character.isDigit(emdCd.charAt(i))) return false;
		return true;
	}
}
