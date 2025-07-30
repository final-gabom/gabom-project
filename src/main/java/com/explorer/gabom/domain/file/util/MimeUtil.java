package com.explorer.gabom.domain.file.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class MimeUtil {
	public static final String DEFAULT_MIME_TYPE = "application/octet-stream";
	/**
	 * 기본 mime type.
	 */
	private static final Logger log = LoggerFactory.getLogger(MimeUtil.class);
	private static final Map<String, String> MIME_MAPPINGS = new HashMap<>(256);
	private static final String[] MIME_MAPPINGS_ARRAY = {
		"7z", "application/x-7z-compressed",
		"avi", "video/x-msvideo",
		"bmp", "image/bmp",
		"css", "text/css",
		"csv", "text/comma-separated-values",
		"doc", "application/msword",
		"docx", "application/msword",
		"gif", "image/gif",
		"gz", "application/x-gzip",
		"htc", "text/x-component",
		"htm", "text/html",
		"html", "text/html",
		"jpg", "image/jpeg",
		"jpe", "image/jpeg",
		"jpeg", "image/jpeg",
		"js", "application/x-javascript",
		"json", "application/json",
		"mid", "audio/mid",
		"mp3", "audio/mpeg",
		"mov", "video/quicktime",
		"mpg", "video/mpeg",
		"mpeg", "video/mpeg",
		"pdf", "application/pdf",
		"png", "image/png",
		"ppt", "application/vnd.ms-powerpoint",
		"pptx", "application/vnd.ms-powerpoint",
		"ra", "audio/x-pn-realaudio",
		"ram", "audio/x-pn-realaudio",
		"svg", "image/svg+xml",
		"swf", "application/x-shockwave-flash",
		"tar", "application/x-tar",
		"tif", "image/tiff",
		"tiff", "image/tiff",
		"txt", "text/plain",
		"wav", "audio/x-wav",
		"xls", "application/vnd.ms-excel",
		"xlsx", "application/vnd.ms-excel",
		"xml", "text/xml",
		"zip", "application/zip"
	};

	static {
		try {
			for (int i = 0; i < MIME_MAPPINGS_ARRAY.length; i += 2) {
				MIME_MAPPINGS.put(MIME_MAPPINGS_ARRAY[i], MIME_MAPPINGS_ARRAY[i + 1]);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private MimeUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 파일 이름을 기반으로 해당 파일의 MIME 타입을 반환합니다.
	 * <p>
	 * 이 메서드는 전달받은 파일 이름에서 확장자를 추출하고,
	 * 미리 정의된 MIME 매핑 목록에서 대응되는 MIME 타입을 조회하여 반환합니다.
	 * 확장자가 존재하지 않거나, 매핑 정보에 등록되지 않은 확장자인 경우 기본값인
	 * {@value #DEFAULT_MIME_TYPE}을 반환합니다.
	 * </p>
	 *
	 * <p><strong>주요 동작 방식:</strong></p>
	 * <ol>
	 *   <li>입력받은 파일 이름의 유효성 검사 (빈 문자열이 아닌지 확인)</li>
	 *   <li>파일 이름의 마지막 점('.') 위치를 찾습니다.</li>
	 *   <li>마지막 점('.') 이후 문자열을 파일 확장자로 인식합니다.</li>
	 *   <li>확장자를 소문자로 변환한 후, {@code MIME_MAPPINGS}에서 해당 MIME 타입을 조회합니다.</li>
	 *   <li>조회한 MIME 타입이 없으면 기본값을 반환합니다.</li>
	 * </ol>
	 *
	 * <p><strong>사용 예시:</strong></p>
	 * <pre>{@code
	 * String mimeType1 = MimeUtil.getMimeType("image.jpg"); // "image/jpeg"
	 * String mimeType2 = MimeUtil.getMimeType("archive.zip"); // "application/zip"
	 * String mimeType3 = MimeUtil.getMimeType("file.unknown"); // "application/octet-stream"
	 * }</pre>
	 *
	 * @param fileName MIME 타입을 확인할 파일의 이름 (확장자 포함)
	 * @return 파일의 MIME 타입. 확장자가 없거나 알 수 없는 경우 {@value #DEFAULT_MIME_TYPE}
	 */
	public static String getMimeType(String fileName) {
		String result = DEFAULT_MIME_TYPE; // 기본값 octet-stream
		if (StringUtils.hasText(fileName)) { // 파일 이름이 비어있지 않은지 검사
			int idx = fileName.lastIndexOf('.'); // 마지막 점(.)의 위치 찾기
			if (idx != -1) { // 점(.)이 존재하면 확장자 추출
				// 확장자를 소문자로 변환하여 MIME_MAPPINGS에서 찾기
				result = MIME_MAPPINGS.get(fileName.substring(idx + 1).toLowerCase());
			}
		}
		// 찾은 값이 없으면 기본값 반환
		return result == null ? DEFAULT_MIME_TYPE : result;
	}
}
