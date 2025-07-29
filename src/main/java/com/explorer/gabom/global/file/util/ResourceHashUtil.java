package com.explorer.gabom.global.file.util;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 파일의 내용을 기반으로 SHA-256 해시를 생성하고,
 * 해당 해시 값을 디렉토리 경로와 파일명으로 분리하여 저장 경로로 활용할 수 있도록 도와주는 유틸리티 클래스입니다.
 *
 * <p><b>주요 목적</b>:
 * <ul>
 *   <li>파일 중복 업로드 방지</li>
 *   <li>고유한 파일 경로 생성</li>
 *   <li>디렉토리 구조 분할을 통한 저장소 최적화</li>
 * </ul>
 *
 * <p><b>제공 기능</b>:
 * <ul>
 *   <li>{@link #generateHash(InputStream)}:
 *       파일 내용을 읽어 SHA-256 해시를 생성하고 Base64 URL-safe 형식으로 반환합니다.</li>
 *   <li>{@link #getDirPath(String)}:
 *       해시 값의 앞 부분을 디렉토리 이름으로 사용합니다.</li>
 *   <li>{@link #getFilePath(String)}:
 *       해시 값의 나머지를 파일 이름으로 사용합니다.</li>
 * </ul>
 *
 * <p><b>사용 예</b>:
 * <pre>{@code
 * String hash = ResourceHashUtil.generateHash(inputStream); // 예: "abCDe123456789..."
 * String dir = ResourceHashUtil.getDirPath(hash);           // "ab"
 * String file = ResourceHashUtil.getFilePath(hash);         // "CDe123456789..."
 * String fullPath = dir + "/" + file;                       // "ab/CDe123456789..."
 * }</pre>
 *
 * <p><b>참고</b>:
 * <ul>
 *   <li>해시 계산 시 InputStream을 버퍼 단위(8192바이트)로 읽어 메모리 효율을 높입니다.</li>
 *   <li>Base64 인코딩은 URL-safe 방식이며, 패딩(`=`) 없이 반환됩니다.</li>
 * </ul>
 */
public class ResourceHashUtil {

	private static final int BUFFER_SIZE = 8192;
	private static final int DIR_LENGTH = 2;	// 디렉토리 구분용

	// 인스턴스화 방지를 위한 private 생성자
	private ResourceHashUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * InputStream을 기반으로 SHA-256 해시 값을 생성
	 * 해시는 URL-safe Base64로 인코딩되어 반환됨
	 *
	 * @param inputStream 파일 InputStream
	 * @return 해시 문자열 (Base64 URL-safe, padding 제거됨)
	 * @throws IOException 입력 스트림 처리 중 오류
	 */
	public static String generateHash(InputStream inputStream) throws IOException {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				digest.update(buffer, 0, length);
			}
			byte[] hash = digest.digest();
			return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not find the algorithm", e);
		}
	}

	/**
	 * 해시 문자열에서 앞 N글자를 디렉토리 이름으로 추출
	 * (ex. 해시가 abcdef이면 "ab" 반환)
	 *
	 * @param hash 해시 문자열
	 * @return 디렉토리 이름
	 */
	public static String getDirPath(String hash) {
		if (!StringUtils.hasText(hash)) {
			throw new RuntimeException("Hash is EMPTY");
		}

		return hash.substring(0, DIR_LENGTH);
	}

	/**
	 * 해시 문자열에서 앞 N글자를 제외한 나머지를 파일 이름으로 추출
	 * (ex. 해시가 abcdef이면 "cdef" 반환)
	 *
	 * @param hash 해시 문자열
	 * @return 파일 이름 부분
	 */
	public static String getFilePath(String hash) {
		if (!StringUtils.hasText(hash)) {
			throw new RuntimeException("Hash is EMPTY");
		}

		return hash.substring(DIR_LENGTH);
	}
}
